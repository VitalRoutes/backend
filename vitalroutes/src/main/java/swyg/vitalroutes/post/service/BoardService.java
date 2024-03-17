package swyg.vitalroutes.post.service;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.GpsDirectory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.multipart.MultipartFile;
import swyg.vitalroutes.member.domain.Member;
import swyg.vitalroutes.post.dto.BoardDTO;
import swyg.vitalroutes.post.dto.ChallengeCheckListDTO;
import swyg.vitalroutes.post.entity.*;
import swyg.vitalroutes.post.repository.*;
import swyg.vitalroutes.s3.S3UploadService;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// service에서 일어나는 작업
// Controller -> (DTO -> Entity) -> Repository (entity class에서 이루어질 작업)
// Repository -> (Entity -> DTO) -> Controller (DTO class에서 이루어질 작업)
// JPA 특성이 Entity 클래스에 담긴 값들이 DB값에 영향을 줄 수 있다.
    // 그렇기에 Entity 객체는 뷰단으로 많이 보이지 않게 service클래스까지만 사용하는것이 권고됨

@Service
@RequiredArgsConstructor // 생성자 주입방식으로 의존성 주입받음
public class BoardService {
    private final BoardRepository boardRepository; // 생성자 주입방식으로 의존성 주입받음
    private final BoardFileRepository boardFileRepository; // 생성자 주입방식으로 의존성 주입받음
    private final BoardPathImageRepository boardPathImageRepository; // 생성자 주입방식으로 의존성 주입받음
    private final TagRepository tagRepository;
    private final PostTagMappingRepository postTagMappingRepository;

    private final S3UploadService s3UploadService;

    @Transactional
    //public void save(BoardDTO boardDTO) throws IOException, ImageProcessingException {
    public void save(BoardDTO boardDTO) throws IOException, ImageProcessingException, URISyntaxException {
        // DTO -> Entity로 옮겨 담음
        System.out.println("Saving..... ------------------------------------------------>");
        Member member = (Member) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // 파일 첨부 여부에 따라 로직분리
        if(boardDTO.getTitleImage().isEmpty()){
            // 첨부 파일이 파일이 없음
            System.out.println("Title Image is null");
            BoardEntity boardEntity = BoardEntity.toSaveEntity(boardDTO, member);
            boardRepository.save(boardEntity); // DB에 insert, save()는 entity클래스를 입력받고 반환한다
        } else{
            System.out.println("saved Title Image");
            // 대표사진 파일 저장---------------------------
            MultipartFile titleImage = boardDTO.getTitleImage(); // 1. DTO에 담긴 파일 꺼냄
            String originalFilename = titleImage.getOriginalFilename(); // 2. 파일의 이름을 가져옴
            String savePath = s3UploadService.saveChallengeTitleImage(titleImage);
            System.out.println("save path : " + savePath);
            //titleImage.transferTo(new File(savePath)); // 5. 해당 경로에 파일 저장
            // DB 저장------------------------------
            BoardEntity boardEntity = BoardEntity.toSaveFileEntity(boardDTO, member);
            Long savedId = boardRepository.save(boardEntity).getId(); // 자식 테이블에서는 부모의 pk값이 필요하다
            BoardEntity board = boardRepository.findById(savedId).get(); // 부모 Entity를 DB에서 가져옴

            BoardFileEntity boardFileEntity = BoardFileEntity.toBoardFileEntity(board, originalFilename, savePath);
            boardFileRepository.save(boardFileEntity);

            // 태그 정보 저장
            List<String> tags = boardDTO.getTags();

            if(null != tags){
                for(String tag : tags){
                    TagEntity tagEntity = new TagEntity();

                    List<TagEntity> tagEntities = tagRepository.findAll();

                    boolean isTag = false; // 이미 있는 태그인지 확인
                    Long curTagId = 0L;
                    if(tagEntities.size() > 0){
                        for(TagEntity curTag : tagEntities){
                            //System.out.println("tag : " + tag);
                            //System.out.println("cur tag : " + curTag);
                            if (tag.equals(curTag.getName())){
                                System.out.println("true");
                                isTag = true;
                                curTagId = curTag.getId();
                                break;
                            }
                        }
                    }
                    if (isTag){
                        tagEntity.setId(curTagId);
                        tagEntity.setName(tag);
                    }
                    else {
                        tagEntity.setName(tag);
                        tagRepository.save(tagEntity);
                    }

                    BoardTagMapping boardTagMapping = new BoardTagMapping();
                    boardTagMapping = BoardTagMapping.savedBoardTagMap(boardEntity, tagEntity);
                    postTagMappingRepository.save(boardTagMapping);
                }
            }

            // 출발지 파일 저장
            System.out.println("saved starting position");
            MultipartFile startingPositionImage = boardDTO.getStartingPositionImage();
            //boardFileEntity.setExistingPathImage(boardFileEntity.getExistingPathImage() + 0B10000); // 출발지 존재 여부
            boardFileEntity.setExistingPathImage(setExistingPathImage(boardFileEntity, 1));
            saved_path_image(boardDTO, boardFileEntity, startingPositionImage, 1);

            // 도착지 파일 저장
            System.out.println("saved destination");
            MultipartFile destinationImage = boardDTO.getDestinationImage();
            //boardFileEntity.setExistingPathImage(boardFileEntity.getExistingPathImage() + 0B00001); // 도착지 존재 여부 체크
            boardFileEntity.setExistingPathImage(setExistingPathImage(boardFileEntity, 5));
            saved_path_image(boardDTO, boardFileEntity, destinationImage, 5);

            if(boardDTO.getStopOverImage1() != null) { // 경유지1이 있다면
                System.out.println("saved stopover 1");
                MultipartFile stopOverImage1 = boardDTO.getStopOverImage1();
                //boardFileEntity.setExistingPathImage(boardFileEntity.getExistingPathImage() + 0B01000); // 경유지1 존재여부
                boardFileEntity.setExistingPathImage(setExistingPathImage(boardFileEntity, 2));
                saved_path_image(boardDTO, boardFileEntity, stopOverImage1, 2);
            }
            if(boardDTO.getStopOverImage2() != null) { // 경유지2이 있다면
                System.out.println("saved stopover 2");
                MultipartFile stopOverImage2 = boardDTO.getStopOverImage2();
                //boardFileEntity.setExistingPathImage(boardFileEntity.getExistingPathImage() + 0B00100); // 경유지2 존재여부 체크
                boardFileEntity.setExistingPathImage(setExistingPathImage(boardFileEntity, 3));
                saved_path_image(boardDTO, boardFileEntity, stopOverImage2, 3);
            }
            if(boardDTO.getStopOverImage1() != null) { // 경유지1이 있다면
                System.out.println("saved stopover 3");
                MultipartFile stopOverImage3 = boardDTO.getStopOverImage3();
                //boardFileEntity.setExistingPathImage(boardFileEntity.getExistingPathImage() + 0B00010); // 경유지3 존재여부 체크
                boardFileEntity.setExistingPathImage(setExistingPathImage(boardFileEntity, 4));
                saved_path_image(boardDTO, boardFileEntity, stopOverImage3, 4);
            }
        }
    }

    @Transactional
    public List<BoardDTO> findAll() { // 게시글 목록 조회
        List<BoardEntity> boardEntityList = boardRepository.findAll(); // 리스트 형태의 entity가 넘어옴
        List<BoardDTO> boardDTOList = new ArrayList<>();

        // Entity로 넘어온 객체를 DTO객체로 옮겨담아서 controller로 리턴해야한다.
        for(BoardEntity boardEntity: boardEntityList){
            // Entity를 DTO에 하나씩 옮겨담음
            boardDTOList.add(BoardDTO.toBoardDTO(boardEntity));
        }
        return boardDTOList;
    }

    @Transactional // jpa가 제공하는 method가 아닌 별도 추가 method를 사용하는 경우
    // BoardRepository.interface에 상속받은 jpa외에 함수가 선언되어 있음
    public void updateHits(Long id) {
        boardRepository.updateHits(id);
    }

    @Transactional
    public BoardDTO findById(Long id) {
        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(id);
        if (optionalBoardEntity.isPresent()) { // .isPresent() 객체가 값을 가지고 있으면 True, 없으면 false
            BoardEntity boardEntity = optionalBoardEntity.get();
            BoardDTO boardDTO = BoardDTO.toBoardDTO(boardEntity);
            return boardDTO;
        } else { // 객체에 값이 없다면 null
            return null;
        }
    }

    @Transactional
    public int findExistingModeById(Long id) {
        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(id);
        if (optionalBoardEntity.isPresent()) { // .isPresent() 객체가 값을 가지고 있으면 True, 없으면 false
            BoardEntity boardEntity = optionalBoardEntity.get();
            int existingMode = boardEntity.getBoardFileEntity().getExistingPathImage();
            return existingMode;
        } else { // 객체에 값이 없다면 null
            return 0;
        }
    }

    @Transactional
    public List<String> findTagId(Long id) {
        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(id);
        if (optionalBoardEntity.isPresent()) { // .isPresent() 객체가 값을 가지고 있으면 True, 없으면 false
            List<String> tags = new ArrayList<>();

            BoardEntity boardEntity = optionalBoardEntity.get();
            for(BoardTagMapping boardTagMapping : boardEntity.getBoardTagMappingList()){
                tags.add(boardTagMapping.getTagEntity().getName());
            }

            return tags;
        } else { // 객체에 값이 없다면 null
            return null;
        }
    }

    public BoardDTO update(BoardDTO boardDTO) {
        /*
            jpa에서 update를 위한 method가 없다.
            save method를 통해 update와 insert작업을 수행한다.
            해당 작업이 update인지 insert인지를 확인하는 방법은 id의 존재 유무이다.
            id값이 있다면 => update
            id값이 없다면 => insert
         */
        Member member = (Member) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BoardEntity boardEntity = BoardEntity.toUpdateEntity(boardDTO, member);
        boardRepository.save(boardEntity);
        return findById(boardDTO.getId());
    }

    public void delete(Long id) {
        boardRepository.deleteById(id);
    }

    public Page<BoardDTO> paging(Pageable pageable) {
        int page = pageable.getPageNumber() - 1;
        int pageLimit = 12; // 한 페이지에 보여줄 글 갯수
        // 한 페이지에 3개씩 을을 보여주고 정렬 기준은 id 기준으로 내림차순 정렬
        // page 위치에 있는 값은 0부터 시작
        Page<BoardEntity> boardEntities =
                boardRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC,"id")));
        // PageRequest.of(보고 싶은 페이지, 한 페이지에 보여줄 글 갯수, 정렬 기준(내림차순, Entity에 작성한 id(DB컬럼이 아니다.)))

        Member member = (Member) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("boardEntities.getContent() = " + boardEntities.getContent()); // 요청 페이지에 해당하는 글
        System.out.println("boardEntities.getTotalElements() = " + boardEntities.getTotalElements()); // 전체 글갯수
        System.out.println("boardEntities.getNumber() = " + boardEntities.getNumber()); // DB로 요청한 페이지 번호
        System.out.println("boardEntities.getTotalPages() = " + boardEntities.getTotalPages()); // 전체 페이지 갯수
        System.out.println("boardEntities.getSize() = " + boardEntities.getSize()); // 한 페이지에 보여지는 글 갯수
        System.out.println("boardEntities.hasPrevious() = " + boardEntities.hasPrevious()); // 이전 페이지 존재 여부
        System.out.println("boardEntities.isFirst() = " + boardEntities.isFirst()); // 첫 페이지 여부
        System.out.println("boardEntities.isLast() = " + boardEntities.isLast()); // 마지막 페이지 여부

        // 목록 : id, writer, title, hit, createdTime => 관련 정보를 담을 BoardDTO객체를 생성해준다.
        Page<BoardDTO> boardDTOS = boardEntities.map(board -> new BoardDTO(board.getId(), member.getNickname(), board.getBoardTitle(), board.getBoardHits(), board.getCreatedTime())); // boardEntities객체에서 board 매개변수에 담아서 하나씩 꺼내 BoardDTO에 옮긴다.
        return boardDTOS;
    }

    @Transactional
    public int setExistingPathImage(BoardFileEntity boardFileEntity, int idx) {
        int mode = boardFileEntity.getExistingPathImage();
        if(idx == 1) {
            mode = mode + 0B10000;
            boardFileEntity.setExistingPathImage(mode);
        }
        else if(idx == 2) {
            mode = mode + 0B01000;
            boardFileEntity.setExistingPathImage(mode);
        }
        else if(idx == 3) {
            mode = mode + 0B00100;
            boardFileEntity.setExistingPathImage(mode);
        }
        else if(idx == 4) {
            mode = mode + 0B00010;
            boardFileEntity.setExistingPathImage(mode);
        }
        else if(idx == 5) {
            mode = mode + 0B00001;
            boardFileEntity.setExistingPathImage(mode);
        }

        return mode;
    }

    @Transactional
    public void saved_path_image(BoardDTO boardDTO, BoardFileEntity boardFileEntity,
                                 MultipartFile pathImageFile,
                                 int locationOnRoute) throws IOException, ImageProcessingException, URISyntaxException {
        String originalFilename = pathImageFile.getOriginalFilename(); // 2.
        String savePath = s3UploadService.saveChallengePathImage(pathImageFile);
        System.out.println("save path : " + savePath);
        String storedFileName = System.currentTimeMillis() + "_" + originalFilename; // 3. 시간을 밀리초로 바꾼 난수을 붙임
        //String saveServerPath = "C:/springboot_img/path/" + originalFilename; // 4.
        String saveServerPath = "/home/ubuntu/dev/image_resource_dummy/" + originalFilename; // 4.
        pathImageFile.transferTo(new File(saveServerPath)); // 5.

        File imageFile = new File(saveServerPath); // 위도 경도
        //File imageFile = new File(); // 위도 경도
        Metadata pathImageFileMetadata = ImageMetadataReader.readMetadata(imageFile);
        GpsDirectory pathImageFileGpsDirectory = pathImageFileMetadata.getFirstDirectoryOfType(GpsDirectory.class);
        if(pathImageFileGpsDirectory.containsTag(GpsDirectory.TAG_LATITUDE) &&
                pathImageFileGpsDirectory.containsTag(GpsDirectory.TAG_LONGITUDE)) {
            String pdsLat = String.valueOf(pathImageFileGpsDirectory.getGeoLocation().getLatitude());
            String pdsLon = String.valueOf(pathImageFileGpsDirectory.getGeoLocation().getLongitude());

            double lat = Double.parseDouble(pdsLat);    //위도
            double lon = Double.parseDouble(pdsLon);    //경도

            // DB 저장------------------------------
            BoardPathImageEntity boardPathImageEntity = BoardPathImageEntity.toBoardPathImageEntity(boardFileEntity,
                    originalFilename,
                    savePath,
                    lat, lon, locationOnRoute);
            boardPathImageRepository.save(boardPathImageEntity);
        }
    }

    public List<ChallengeCheckListDTO> fetchPostPagesBy(Long lastBoardId, int size){
        PageRequest pageRequest = PageRequest.of(0,size);

        if(lastBoardId == 0){
            //Long boardMaxId = boardRepository.getMaxId();
            Long boardMaxId = 2134567890L;
            System.out.println("max _ id : " + boardMaxId);
            Page<BoardEntity> boardEntityPage =
                    boardRepository.findByPostIdLessThanOrderByPostIdDesc(boardMaxId, pageRequest);
            List<BoardEntity> boardEntities = boardEntityPage.getContent();

            return boardEntities.stream().map(BoardDTO::transformChallengeCheckListDTO).collect(Collectors.toList());
        }
        else {
            Page<BoardEntity> boardEntityPage =
                    boardRepository.findByPostIdLessThanOrderByPostIdDesc(lastBoardId, pageRequest);
            List<BoardEntity> boardEntities = boardEntityPage.getContent();

            /*
            for(BoardEntity be : boardEntities){
                System.out.println("===========================");
                System.out.println("post id : " + be.getId());
                System.out.println("post title : " + be.getBoardTitle());
                System.out.println("post writer : " + be.getBoardWriter());
            }
            */
            return boardEntities.stream().map(BoardDTO::transformChallengeCheckListDTO).collect(Collectors.toList());
        }
    }

    public ChallengeCheckListDTO getChallengeCheckListDTO(BoardDTO boardDTO) {
        //챌린지 목록을 조회하기 위한 데이터 전송
        ChallengeCheckListDTO challengeCheckListDTO = new ChallengeCheckListDTO();
        challengeCheckListDTO.setBoardId(boardDTO.getId());
        challengeCheckListDTO.setChallengeTitle(boardDTO.getBoardTitle());
        challengeCheckListDTO.setStoredTitleImageName(boardDTO.getStoredTitleImageName());
        challengeCheckListDTO.setBoardParty(boardDTO.getTotalComments()); // 참여 인원은일단 0세팅

        return challengeCheckListDTO;
    }
}
