package swyg.vitalroutes.post.controller;

import com.drew.imaging.ImageProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import swyg.vitalroutes.common.response.ApiResponseDTO;
import swyg.vitalroutes.member.dto.MemberNicknameDTO;
import swyg.vitalroutes.post.dto.BoardDTO;
import swyg.vitalroutes.post.dto.ChallengeCheckDTO;
import swyg.vitalroutes.post.dto.ChallengeCheckListDTO;
import swyg.vitalroutes.post.dto.ChallengeSaveFormDTO;
import swyg.vitalroutes.post.entity.TagEntity;
import swyg.vitalroutes.post.service.BoardService;
import swyg.vitalroutes.s3.S3UploadService;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static swyg.vitalroutes.common.response.ResponseType.SUCCESS;

@Tag(name = "Challenge API Controller", description = "Challenge 생성, 조회, 수정, 삭제 기능 제공")
//@Controller
@RestController
@RequiredArgsConstructor // 생성자 주입방식으로 의존성 주입받음 => service클래스 호출
@RequestMapping("/board") // 부모 주소 자동 입력
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class BoardController {
    private final BoardService boardService; // 생성자 주입방식으로 의존성 주입받음

    @GetMapping("/save") // 자식 주소 매핑
    public String saveForm() {
        System.out.println("\n============\nsave.html로 이동\n============\n");
        return "save"; // save.html 반환 (게시글 저장 페이지)
    }

    @Operation(summary = "새로운 챌린지 생성", description = "새로운 챌린지를 생성(등록) 할 수 있다.")
    @ApiResponse(responseCode = "200", description = "챌린지 생성(등록) 완료")
    @PostMapping("/save") // 클라이언트로부터 post로 /save주소로 요청을 받음
    public ApiResponseDTO<?> save(ChallengeSaveFormDTO challengeSaveFormDTO) throws ImageProcessingException, IOException, URISyntaxException {
    //public String save(@ModelAttribute BoardDTO boardDTO) throws IOException, ImageProcessingException { // html에서 Controller로 전달해줄 때, 가장 간단한 방법은 @RequestParam() 방법이 있다. 여기서는 대신 @ModelAttribute 사용
        BoardDTO boardDTO = toTransformBoardDTO(challengeSaveFormDTO); // challengeSaveForm -> boardDTO
        System.out.println("\n============\nindex.html로 이동\n============\n");
        System.out.println("boardDTO = " + boardDTO); // 들어온 값 확인
        boardService.save(boardDTO);
        challengeSaveFormDTO = toTransformChallengeSaveFormDTO(boardDTO);
        return new ApiResponseDTO<>(OK, SUCCESS, "Challenge가 생성되었습니다.", null);
        //return "index";
    }

    private ChallengeSaveFormDTO toTransformChallengeSaveFormDTO(BoardDTO boardDTO) {
        ChallengeSaveFormDTO challengeSaveFormDTO = new ChallengeSaveFormDTO();
        challengeSaveFormDTO.setId(boardDTO.getId());
        challengeSaveFormDTO.setChallengeWriter(boardDTO.getBoardWriter());
        challengeSaveFormDTO.setChallengeTitle(boardDTO.getBoardTitle());
        challengeSaveFormDTO.setChallengeContents(boardDTO.getBoardContents());
        challengeSaveFormDTO.setChallengeTransportation(boardDTO.getBoardTransportation());
        challengeSaveFormDTO.setTags(boardDTO.getTags());
        challengeSaveFormDTO.setTitleImage(boardDTO.getTitleImage());

        challengeSaveFormDTO.setStartingPositionImage(boardDTO.getStartingPositionImage());
        challengeSaveFormDTO.setStartingPosLat(boardDTO.getStartingPosLat());
        challengeSaveFormDTO.setStartingPosLon(boardDTO.getStartingPosLon());

        challengeSaveFormDTO.setDestinationImage(boardDTO.getDestinationImage());
        challengeSaveFormDTO.setDestinationLat(boardDTO.getDestinationLat());
        challengeSaveFormDTO.setDestinationLon(boardDTO.getDestinationLon());

        if(boardDTO.getStopOverImage1() != null) {
            challengeSaveFormDTO.setStopOverImage1(boardDTO.getStopOverImage1());
            challengeSaveFormDTO.setStopOver1Lat(boardDTO.getStopOver1Lat());
            challengeSaveFormDTO.setStopOver1Lon(boardDTO.getStopOver1Lon());
        }
        if(boardDTO.getStopOverImage2() != null) {
            challengeSaveFormDTO.setStopOverImage2(boardDTO.getStopOverImage2());
            challengeSaveFormDTO.setStopOver2Lat(boardDTO.getStopOver2Lat());
            challengeSaveFormDTO.setStopOver2Lon(boardDTO.getStopOver2Lon());
        }
        if(boardDTO.getStopOverImage3() != null) {
            challengeSaveFormDTO.setStopOverImage3(boardDTO.getStopOverImage3());
            challengeSaveFormDTO.setStopOver3Lat(boardDTO.getStopOver3Lat());
            challengeSaveFormDTO.setStopOver3Lon(boardDTO.getStopOver3Lon());
        }
        return challengeSaveFormDTO;
    }

    private BoardDTO toTransformBoardDTO(ChallengeSaveFormDTO challengeSaveFormDTO) {
        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setBoardWriter(challengeSaveFormDTO.getChallengeWriter());
        boardDTO.setBoardTitle(challengeSaveFormDTO.getChallengeTitle());
        boardDTO.setBoardContents(challengeSaveFormDTO.getChallengeContents());
        boardDTO.setBoardTransportation(challengeSaveFormDTO.getChallengeTransportation());
        boardDTO.setTags(challengeSaveFormDTO.getTags());
        boardDTO.setTitleImage(challengeSaveFormDTO.getTitleImage());
        boardDTO.setTags(challengeSaveFormDTO.getTags());

        boardDTO.setStartingPositionImage(challengeSaveFormDTO.getStartingPositionImage());
        boardDTO.setStartingPosLat(challengeSaveFormDTO.getStartingPosLat());
        boardDTO.setStartingPosLon(challengeSaveFormDTO.getStartingPosLon());

        boardDTO.setDestinationImage(challengeSaveFormDTO.getDestinationImage());
        boardDTO.setDestinationLat(challengeSaveFormDTO.getDestinationLat());
        boardDTO.setDestinationLon(challengeSaveFormDTO.getDestinationLon());

        if(challengeSaveFormDTO.getStopOverImage1() != null) {
            boardDTO.setStopOverImage1(challengeSaveFormDTO.getStopOverImage1());
            boardDTO.setStopOver1Lat(challengeSaveFormDTO.getStopOver1Lat());
            boardDTO.setStopOver1Lon(challengeSaveFormDTO.getStopOver1Lon());
        }
        if(challengeSaveFormDTO.getStopOverImage2() != null) {
            boardDTO.setStopOverImage2(challengeSaveFormDTO.getStopOverImage2());
            boardDTO.setStopOver2Lat(challengeSaveFormDTO.getStopOver2Lat());
            boardDTO.setStopOver2Lon(challengeSaveFormDTO.getStopOver2Lon());
        }
        if(challengeSaveFormDTO.getStopOverImage3() != null) {
            boardDTO.setStopOverImage3(challengeSaveFormDTO.getStopOverImage3());
            boardDTO.setStopOver3Lat(challengeSaveFormDTO.getStopOver3Lat());
            boardDTO.setStopOver3Lon(challengeSaveFormDTO.getStopOver3Lon());
        }
        return boardDTO;
    }

    @Operation(summary = "챌린지 목록 조회", description = "지금까지 등록된 챌린지 목록입니다.")
    @ApiResponse(responseCode = "200", description = "챌린지 조회 완료")
    @GetMapping("/list")
    public ApiResponseDTO<?> findAll(Model model) {
        List<BoardDTO> boardDTOList = boardService.findAll(); // 게시글 "목록"을 가져온다.
        List<ChallengeCheckListDTO> challengeCheckListDTOList = new ArrayList<ChallengeCheckListDTO>();

        for(BoardDTO boardDTO : boardDTOList){
            ChallengeCheckListDTO challengeCheckListDTO = new ChallengeCheckListDTO();
            challengeCheckListDTO = boardService.getChallengeCheckListDTO(boardDTO);
            challengeCheckListDTOList.add(challengeCheckListDTO);
        }

        return new ApiResponseDTO<>(OK, SUCCESS, "챌린지 목록이 조회되었습니다.", challengeCheckListDTOList);
    }

    /*
    private ChallengeCheckListDTO getChallengeCheckListDTO(BoardDTO boardDTO) {
        //챌린지 목록을 조회하기 위한 데이터 전송
        ChallengeCheckListDTO challengeCheckListDTO = new ChallengeCheckListDTO();
        challengeCheckListDTO.setBoardId(boardDTO.getId());
        challengeCheckListDTO.setChallengeTitle(boardDTO.getBoardTitle());
        challengeCheckListDTO.setStoredTitleImageName(boardDTO.getStoredTitleImageName());
        challengeCheckListDTO.setBoardParty(boardDTO.getTotalComments()); // 참여 인원은일단 0세팅

        return challengeCheckListDTO;
    }
    */

    @Operation(summary = "{id}에 해당하는 챌린지 상세 조회", description = "{id}에 해당하는 챌린지입니다.")
    @ApiResponse(responseCode = "200", description = "{id}에 해당하는 챌린지 상세 조회 완료")
    @GetMapping("/detail/{id}")
    /*
    public BoardDTO findById(@PathVariable("id") Long id, Model model, // 경로상의 값을 가져올때는 @PathVariable를 사용
                            @PageableDefault(page=1) Pageable pageable) {
     */
    public ApiResponseDTO<?> findById(@PathVariable("id") Long id, Model model, // 경로상의 값을 가져올때는 @PathVariable를 사용
                             @PageableDefault(page=1) Pageable pageable) {
        /*
            해당 게시글의 조회수를 하나 올리고
            게시글 데이터를 가져와서 detail.html에 출력
         */
        //System.out.println("\n============\ndetail.html로 이동\n============\n");
        boardService.updateHits(id);
        BoardDTO boardDTO = boardService.findById(id);

        ChallengeCheckDTO challengeCheckDTO = getChallengeCheckDTO(boardDTO);
        //model.addAttribute("board", boardDTO);
        //model.addAttribute("page", pageable.getPageNumber());
        //return "detail";
        //return boardDTO;
        return new ApiResponseDTO<>(OK, SUCCESS, "챌린지가 조회되었습니다.", challengeCheckDTO);
    }

    @Operation(summary = "{id}에 해당하는 챌린지 좋아요 on", description = "{id}인 챌린지에 좋아요 눌렀습니다.")
    @ApiResponse(responseCode = "200", description = "{id}에 해당하는 챌린지 좋아요 on")
    @GetMapping("/likeOn/{id}")
    public ApiResponseDTO<?> turnOnLikes(@PathVariable("id") Long id, Model model, // 경로상의 값을 가져올때는 @PathVariable를 사용
                                      @PageableDefault(page=1) Pageable pageable) {
        /*
            해당 게시글의 좋아요를 하나 올림
         */
        boardService.increaseLikes(id);
        return new ApiResponseDTO<>(OK, SUCCESS, "챌린지에 좋아요 눌렀습니다.", null);
    }

    @Operation(summary = "{id}에 해당하는 챌린지 좋아요 off", description = "{id}인 챌린지에 좋아요 껐습니다.")
    @ApiResponse(responseCode = "200", description = "{id}에 해당하는 챌린지 좋아요 off")
    @GetMapping("/likeOff/{id}")
    public ApiResponseDTO<?> turnOffLikes(@PathVariable("id") Long id, Model model, // 경로상의 값을 가져올때는 @PathVariable를 사용
                                         @PageableDefault(page=1) Pageable pageable) {
        /*
            해당 게시글의 좋아요를 하나 내림
         */
        boardService.decreaseLikes(id);
        return new ApiResponseDTO<>(OK, SUCCESS, "챌린지에 좋아요 내렀습니다.", null);
    }

    private ChallengeCheckDTO getChallengeCheckDTO(BoardDTO boardDTO) {
        ChallengeCheckDTO challengeCheckDTO = new ChallengeCheckDTO();
        challengeCheckDTO.setId(boardDTO.getId());
        challengeCheckDTO.setChallengeWriter(boardDTO.getBoardWriter());
        challengeCheckDTO.setChallengeTitle(boardDTO.getBoardTitle());
        challengeCheckDTO.setChallengeContents(boardDTO.getBoardContents());
        challengeCheckDTO.setChallengeTransportation(boardDTO.getBoardTransportation());

        List<String> tags = boardService.findTagId(boardDTO.getId());
        challengeCheckDTO.setTags(tags);

        challengeCheckDTO.setStoredTitleImageName(boardDTO.getStoredTitleImageName());
        challengeCheckDTO.setStoredStartingPositionImageName(boardDTO.getStoredStartingPositionImageName());
        challengeCheckDTO.setStartingPosLat(boardDTO.getStartingPosLat());
        challengeCheckDTO.setStartingPosLon(boardDTO.getStartingPosLon());

        challengeCheckDTO.setStoredDestinationImageName(boardDTO.getStoredDestinationImageName());
        challengeCheckDTO.setDestinationLat(boardDTO.getDestinationLat());
        challengeCheckDTO.setDestinationLon(boardDTO.getDestinationLon());

        challengeCheckDTO.setBoardHits(boardDTO.getBoardHits());    // 조회 수
        challengeCheckDTO.setTotalComments(boardDTO.getTotalComments());    // 참가자 수

        int existingMode = boardService.findExistingModeById(boardDTO.getId());
        challengeCheckDTO.setExistingMode(existingMode);

        if((existingMode & 0B01000) == 0B01000){
            challengeCheckDTO.setStoredStopOverImage1Name(boardDTO.getStoredStopOverImage1Name());
            challengeCheckDTO.setStopOver1Lat(boardDTO.getStopOver1Lat());
            challengeCheckDTO.setStopOver1Lon(boardDTO.getStopOver1Lon());
        }
        else {
            challengeCheckDTO.setStoredStopOverImage1Name(null);
            challengeCheckDTO.setStopOver1Lat(-1);
            challengeCheckDTO.setStopOver1Lon(-1);
        }

        if((existingMode & 0B00100) == 0B00100){
            challengeCheckDTO.setStoredStopOverImage2Name(boardDTO.getStoredStopOverImage2Name());
            challengeCheckDTO.setStopOver2Lat(boardDTO.getStopOver2Lat());
            challengeCheckDTO.setStopOver2Lon(boardDTO.getStopOver2Lon());
        }
        else {
            challengeCheckDTO.setStoredStopOverImage2Name(null);
            challengeCheckDTO.setStopOver1Lat(-1);
            challengeCheckDTO.setStopOver1Lon(-1);
        }

        if((existingMode & 0B00010) == 0B00010){
            challengeCheckDTO.setStoredStopOverImage3Name(boardDTO.getStoredStopOverImage3Name());
            challengeCheckDTO.setStopOver3Lat(boardDTO.getStopOver3Lat());
            challengeCheckDTO.setStopOver3Lon(boardDTO.getStopOver3Lon());
        }
        else {
            challengeCheckDTO.setStoredStopOverImage3Name(null);
            challengeCheckDTO.setStopOver1Lat(-1);
            challengeCheckDTO.setStopOver1Lon(-1);
        }

        return challengeCheckDTO;
    }

    @Operation(summary = "수정할 {id}에 해당하는 챌린지 불러오기", description = "수정할 {id}에 해당하는 챌린지입니다.")
    @ApiResponse(responseCode = "200", description = "{id}에 해당하는 챌린지 불러오기 완료")
    @GetMapping("/update/{id}")
    public ApiResponseDTO<?> updateForm(@PathVariable("id") Long id, Model model) {
        /*
            게시글의 정보를 Update.html에 보여줄 목적
         */
        BoardDTO boardDTO = boardService.findById(id);
        ChallengeCheckDTO challengeCheckDTO = getChallengeCheckDTO(boardDTO);

        //model.addAttribute("boardUpdate", boardDTO);
        return new ApiResponseDTO<>(OK, SUCCESS, "챌린지를 불러왔습니다.", challengeCheckDTO);
    }

    @Operation(summary = "챌린지 수정후 수정된 챌린지 확인", description = "수정된 챌린지입니다.")
    @ApiResponse(responseCode = "200", description = "수정 완료")
    @PostMapping("/update")
    public ApiResponseDTO<?> update(@ModelAttribute BoardDTO boardDTO, Model model) {
        /*
            수정 후 수정이 반영된 상세페이지를 보여줌
            (목록을 보여주어도 됨)
         */
        BoardDTO board = boardService.update(boardDTO);
        ChallengeSaveFormDTO challengeSaveFormDTO = toTransformChallengeSaveFormDTO(boardDTO);
        //model.addAttribute("board", board);
        //return "detail";
        //return "redirect:/board/" + boardDTO.getId(); // 수정 후 조회수 업데이트에 영향을 받을 수 있다.
        return new ApiResponseDTO<>(OK, SUCCESS, "챌린지 수정 완료", challengeSaveFormDTO);
    }

    @Operation(summary = "챌린지 삭제하기", description = "삭제할 챌린지입니다.")
    @ApiResponse(responseCode = "200", description = "삭제 완료")
    @DeleteMapping("/delete/{id}")
    public ApiResponseDTO<?> delete(@PathVariable("id") Long id) {
        /*
            삭제 후 게시글 목록이 나타남
         */
        boardService.delete(id);
        //return "redirect:/board/";
        return new ApiResponseDTO<>(OK, SUCCESS, "챌린지 삭제 완료", "삭제 완료");
    }

    // /board/paging?page=1
    @GetMapping("/paging")
    public String paging(
            @PageableDefault(page = 1)Pageable pageable,
            Model model) {// Pageable 라이브러리 사용시 스프링에 내장된 라이브러리 사용할것
        pageable.getPageNumber();
        Page<BoardDTO> boardList = boardService.paging(pageable);

        // page 갯수가 20개
        // 현재 사용자가 3페이지를 보고 있다면
        // 시스템 별로 다르겠지만
        // 1 2 3 4 5 페이지 중 3페이지에 대한 css가 다르게 보인다.
        // 지금 프로젝트에서는 보여지는 페이지 갯수를 3개만 보여줄 예정이다.
        // 총 페이지 갯수가 8 개라면
        // 1 2 3 || 7 8 까지 보여주면 된다. 9페이지는 보여줄 필요없다.
        int blockLimit = 3; // 보여지는 페이지 갯수
        int startPage = (((int)(Math.ceil((double)pageable.getPageNumber() / blockLimit))) - 1) * blockLimit + 1; // 1 4 7 10 ~~
        // 현재 사용자가 1 2 3페이지 중에 있다면 starting page는 1 값부터 준다.
        // (소숫점 올림(현재 사용자가 요청한 페이지 / 보여지는 페이지 갯수) - 1) * 보여지는 페이지 갯수 + 1;
        //int endPage = ((startPage + blockLimit - 1) < boardList.getTotalPages()) ? startPage + blockLimit - 1 : boardList.getTotalPages(); // 3 6 9 12 ~~
        int endPage = Math.min((startPage + blockLimit - 1), boardList.getTotalPages()); // 3 6 9 12 ~~
        // 실제 페이지 갯수가 endPage갯수보다 작은값을 가지고 있다면 endPage값이 아닌 실제 페이지 갯수를 저장

        model.addAttribute("boardList", boardList);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "paging";
    }

    @Operation(summary = "챌린지 목록 불러오기", description = "챌린지 목록 불러오기")
    @ApiResponse(responseCode = "200", description = "불러오기 완료")
    @GetMapping("/scroll")
    public List<ChallengeCheckListDTO> getBoardList(@RequestParam Long lastBoardId) {
                                                    //@RequestParam int size){
        int size = 12; // 12개씩 불러옴
        return boardService.fetchPostPagesBy(lastBoardId, size);
    }
}
