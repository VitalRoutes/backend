package swyg.vitalroutes.participation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import swyg.vitalroutes.comments.repository.CommentRepository;
import swyg.vitalroutes.common.exception.ParticipationException;
import swyg.vitalroutes.common.response.DataWithCount;
import swyg.vitalroutes.common.response.MyChallengeDTO;
import swyg.vitalroutes.common.utils.FileUtils;
import swyg.vitalroutes.member.domain.Member;
import swyg.vitalroutes.member.repository.MemberRepository;
import swyg.vitalroutes.participation.domain.ParticipationImage;
import swyg.vitalroutes.participation.domain.Participation;
import swyg.vitalroutes.participation.dto.*;
import swyg.vitalroutes.participation.repository.ParticipationRepository;
import swyg.vitalroutes.post.entity.BoardEntity;
import swyg.vitalroutes.post.entity.BoardPathImageEntity;
import swyg.vitalroutes.post.repository.BoardRepository;
import swyg.vitalroutes.s3.S3UploadService;

import java.io.IOException;
import java.util.*;

import static org.springframework.http.HttpStatus.*;
import static swyg.vitalroutes.common.response.ResponseType.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ParticipationService {

    private final ParticipationRepository participationRepository;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final S3UploadService s3UploadService;


    public DataWithCount<?> findParticipation(Long boardId, Pageable pageable) {
        // 참여 게시글 조회
        Page<Participation> pagingData = participationRepository.findAllByBoardId(boardId, pageable);
        List<ParticipationResponseDTO> dtoList = pagingData.map(ParticipationResponseDTO::new).toList();

        for (ParticipationResponseDTO dto : dtoList) {
            long size = commentRepository.countByParticipationId(dto.getParticipationId());
            dto.setTotalComments(size);
        }

        long count = pagingData.getTotalElements(); // 총 데이터의 수
        boolean remainFlag = pagingData.hasNext();  // 현재까지 보여지고 있는 데이터 외에 남은 데이터가 있는지

        return new DataWithCount<>(count, remainFlag, dtoList);
    }

    public DataWithCount<?> findMyParticipation(Long memberId, Pageable pageable) {
        Page<MyChallengeDTO> myParticipation = participationRepository.findMyParticipation(memberId, pageable);
        long count = myParticipation.getTotalElements();
        boolean remainFlag = myParticipation.hasNext();
        return new DataWithCount<>(count, remainFlag, myParticipation.getContent());
    }


    public void saveParticipation(Long memberId, ParticipationSaveDTO saveDTO) {
        // 거치지 않아도 되지만 DB 에서 조회하는 걸로 남김
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ParticipationException(NOT_FOUND, FAIL, "사용자가 존재하지 않습니다"));
        BoardEntity board = boardRepository.findById(saveDTO.getBoardId())
                .orElseThrow(() -> new ParticipationException(NOT_FOUND, FAIL, "게시글이 존재하지 않습니다"));

        List<BoardPathImageEntity> boardPathImageEntityList = board.getBoardFileEntity().getBoardPathImageEntityList();
        // 도착지를 먼저 저장하시니...내가 정렬을 해야지...;;
        boardPathImageEntityList.sort(Comparator.comparing(BoardPathImageEntity::getLocationOnRoute));
        log.info("boardPathImageEntityList size = {}", boardPathImageEntityList.size());
        for (BoardPathImageEntity boardPathImageEntity : boardPathImageEntityList) {
            System.out.println("boardPathImageEntity.getLocationOnRoute() = " + boardPathImageEntity.getLocationOnRoute());
        }
        
        List<MultipartFile> files = saveDTO.getFiles(); // 전달 받은 이미지 파일

        if (boardPathImageEntityList.size() != files.size()) {
            throw new ParticipationException(BAD_REQUEST, FAIL, "게시글과 동일한 개수의 이미지를 업로드해주세요");
        }

        List<ParticipationImage> participationImages = new ArrayList<>();
        int seq = 0;
        
        
        for (MultipartFile file : files) {
            double[] locationInfo = FileUtils.getLocationInfo(file);

            // 거리 비교
            double latitude = boardPathImageEntityList.get(seq).getLatitude();
            double longitude = boardPathImageEntityList.get(seq).getLongitude();
            double distance = FileUtils.calDistance(locationInfo[0], locationInfo[1], latitude, longitude);
            log.info(seq + "번째 지점 distance = {}", distance);
            if (distance > 5) {
                throw new ParticipationException(BAD_REQUEST, FAIL, (seq + 1) + "번째 지점의 거리가 멀리 떨어져있습니다. 챌린지 지점과의 거리는 5m 이하여야 합니다. 현재거리 = " + (int) distance + "m");
            }
            // 이상 없으면 업로드 진행
            String fileName = "";
            try {
                fileName = s3UploadService.saveFile(file);
            } catch (IOException e) {
                throw new ParticipationException(INTERNAL_SERVER_ERROR, FAIL, "파일 업로드 중 에러가 발생하였습니다");
            }
            participationImages.add(ParticipationImage.createParticipationImage(++seq, fileName));
        }
        
        Participation participation = Participation.createParticipation(saveDTO.getContent(), member, board, participationImages);
        participationRepository.save(participation);
    }

    public void deleteParticipation(Long participationId) {
        Participation participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new ParticipationException(NOT_FOUND, FAIL, "참여 게시글이 존재하지 않습니다"));
        participationRepository.deleteById(participationId);
    }

    public ParticipationResponseDTO findById(Long participationId) {
        Participation participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new ParticipationException(NOT_FOUND, FAIL, "참여 게시글이 존재하지 않습니다"));
        return new ParticipationResponseDTO(participation);
    }

    /**
     * 이미지 변경 2가지 선택지
     * 1. 변경할 이미지를 업로드한다
     * 2. 변경할 이미지 없이 내용만 변경된다면 내용을 변경하는 API 를 호출한다
     */
    public ImageResponseDTO uploadImage(ImageSaveDTO imageDTO) {
        Long boardId = imageDTO.getBoardId();
        int sequence = imageDTO.getSequence();

        BoardEntity board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ParticipationException(NOT_FOUND, FAIL, "게시글이 존재하지 않습니다"));
        List<BoardPathImageEntity> boardPathImageEntityList = board.getBoardFileEntity().getBoardPathImageEntityList();
        // 도착지를 먼저 저장하시니...내가 정렬을 해야지...;;
        boardPathImageEntityList.sort(Comparator.comparing(BoardPathImageEntity::getLocationOnRoute));
        double latitude = boardPathImageEntityList.get(sequence - 1).getLatitude();
        double longitude = boardPathImageEntityList.get(sequence - 1).getLongitude();

        String url = "modifyURL";
        MultipartFile file = imageDTO.getFile();
        double[] locationInfo = FileUtils.getLocationInfo(file);

        double distance = FileUtils.calDistance(locationInfo[0], locationInfo[1], latitude, longitude);
        log.info(sequence + "번째 지점 distance = {}", distance);
        if (distance > 5) {
            throw new ParticipationException(BAD_REQUEST, FAIL, sequence + "번째 지점의 거리가 멀리 떨어져있습니다. 챌린지 지점과의 거리는 5m 이하여야 합니다. 현재거리 = " + (int) distance + "m");
        }
        
        try {
            url = s3UploadService.saveFile(file);
        } catch (IOException exception) {
            throw new ParticipationException(INTERNAL_SERVER_ERROR, FAIL, "파일 업로드 중 에러가 발생하였습니다");
        }
        
        return new ImageResponseDTO(sequence, url);
    }

    public void modifyParticipation(Long participationId, ParticipationModifyDTO modifyDTO) {
        Participation participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new ParticipationException(NOT_FOUND, FAIL, "참여 게시글이 존재하지 않습니다"));
        participation.setContent(modifyDTO.getContent());
        // 파일 변경

        List<ParticipationImage> newImages = modifyDTO.getUploadedFiles().stream()
                .map(imageResponseDTO -> ParticipationImage
                        .createParticipationImage(imageResponseDTO.getSequence(), imageResponseDTO.getFileName()))
                .toList();
        participation.getParticipationImages().clear();
        for (ParticipationImage newImage : newImages) {
            participation.getParticipationImages().add(newImage);
        }
    }
}
