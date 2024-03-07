package swyg.vitalroutes.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import swyg.vitalroutes.post.entity.BoardEntity;

import java.time.LocalDateTime;

// DTO ( Data Transfer Object), VL, Bean, ... 비슷한 용도
// Entity는 살짝 결이 다름 ( 여러 파라미터를 한 객체에 담아서 보내는 용도 )
@Getter
@Setter
@ToString
@NoArgsConstructor // 기본 생성자
@AllArgsConstructor // 모든 필드를 매개변수로 하는 생성자
@Schema(description = "Challenge 생성, 조회, 수정, 삭제 시 사용")
public class BoardDTO {
    private Long id;

    @Schema(description = "Challenge 작성자명을 담는 변수")
    private String boardWriter; // 작성자
    @Schema(description = "Challenge 제목을 담는 변수")
    private String boardTitle; // 제목
    @Schema(description = "Challenge 본문 내용을 담는 변수")
    private String boardContents; // 본문
    @Schema(description = "Challenge 등록시 사용한 이동 수단을 담는 변수. ex. 걷기(0), 자전거(1) ")
    private String boardTransportation; // 이동수단
    //@Schema(description = "Challenge 조회수를 담는 변수")
    private int boardHits; // 조회수
    //@Schema(description = "Challenge 생성 시간을 담는 변수")
    private LocalDateTime boardCreatedTime;
    //@Schema(description = "Challenge 수정 시간을 담는 변수")
    private LocalDateTime boardUpdatedTime;

    @Schema(description = "Challenge 대표사진 파일을 담는 변수")
    // DTO에서 받는 부분은 MultipartFile만 동작
    private MultipartFile titleImage; // 실제 파일을 담아줄 수 있는 역할 (대표사진 저장)
    // save.html -> Controller로 파일 담는용도

    //@Schema(description = "Challenge 대표사진 이름을 담는 변수")
    // Service 클래스에서 사용할 것
    private String originalTitleImageName; // 원본 파일 이름
    //@Schema(description = "Challenge 저장된 대표사진 이름을 담는 변수")
    private String storedTitleImageName; // 서버 저장용 파일 이름
    //@Schema(description = "Challenge 대표사진있으면 1, 없으면 0. 이번 프로젝트에선 항상 있을(1) 예정")
    private int fileAttached; // 파일 첨부 여부(첨부 1, 미첨부 0)

    @Schema(description = "Challenge 이동 경로 사진 중 출발지 사진 파일을 담는 변수")
    private MultipartFile startingPositionImage; // 출발지 이미지 파일 담는용도
    //@Schema(description = "Challenge 출발지 사진 파일명을 담는 변수")
    private String originalStartingPositionImageName; // 출발지 이미지 원본 파일 이름
    //@Schema(description = "Challenge 저장할 출발지 사진 파일명을 담는 변수")
    private String storedStartingPositionImageName; // 출발지 이미지 서버저장용 파일 이름
    //@Schema(description = "Challenge 출발 사진 존재 여부 확인. 있으면(1) 없으면(0). 항상(1)임")
    private int startingPositionImageAttached; // 파일 첨부 여부(첨부 1, 미첨부 0)

    @Schema(description = "Challenge 이동 경로 사진 중 도착지 사진 파일을 담는 변수")
    private MultipartFile destinationImage; // 도착지 이미지 파일 담는용도
    //@Schema(description = "Challenge 도착지 사진 파일명을 담는 변수")
    private String originalDestinationImageName; // 도착지 이미지 원본 파일 이름
    //@Schema(description = "Challenge 저장할 도착지 사진 파일명을 담는 변수")
    private String storedDestinationImageName; // 도착지 이미지 서버저장용 파일 이름
    //@Schema(description = "Challenge 도착 사진 존재 여부 확인. 있으면(1) 없으면(0). 항상(1)임")
    private int destinationImageAttached; // 파일 첨부 여부(첨부 1, 미첨부 0)

    @Schema(description = "Challenge 이동 경로 사진 중 경유지1 사진 파일을 담는 변수")
    private MultipartFile stopOverImage1; // 경유지1 이미지 파일 담는용도
    //@Schema(description = "Challenge 경유지1 사진 파일명을 담는 변수")
    private String originalStopOverImage1Name; // 경유지1 이미지 원본 파일 이름
    //@Schema(description = "Challenge 저장할 경유지1 사진 파일명을 담는 변수")
    private String storedStopOverImage1Name; // 경유지1 이미지 서버저장용 파일 이름
    //@Schema(description = "Challenge 경유지1 사진 존재 여부 확인. 있으면(1) 없으면(0). 항상(1)임")
    private int stopOverImage1Attached; // 파일 첨부 여부(첨부 1, 미첨부 0)

    @Schema(description = "Challenge 이동 경로 사진 중 경유지2 사진 파일을 담는 변수")
    private MultipartFile stopOverImage2; // 경유지2 이미지 파일 담는용도
    //@Schema(description = "Challenge 경유지2 사진 파일명을 담는 변수")
    private String originalStopOverImage2Name; // 경유지2 이미지 원본 파일 이름
    //@Schema(description = "Challenge 저장할 경유지2 사진 파일명을 담는 변수")
    private String storedStopOverImage2Name; // 경유지2 이미지 서버저장용 파일 이름
    //@Schema(description = "Challenge 경유지2 사진 존재 여부 확인. 있으면(1) 없으면(0). 항상(1)임")
    private int stopOverImage2Attached; // 파일 첨부 여부(첨부 1, 미첨부 0)

    @Schema(description = "Challenge 이동 경로 사진 중 경유지3 사진 파일을 담는 변수")
    private MultipartFile stopOverImage3; // 경유지3 이미지 파일 담는용도
    //@Schema(description = "Challenge 경유지3 사진 파일명을 담는 변수")
    private String originalStopOverImage3Name; // 경유지3 이미지 원본 파일 이름
    //@Schema(description = "Challenge 저장할 경유지3 사진 파일명을 담는 변수")
    private String storedStopOverImage3Name; // 경유지3 이미지 서버저장용 파일 이름
    //@Schema(description = "Challenge 경유지3 사진 존재 여부 확인. 있으면(1) 없으면(0). 항상(1)임")
    private int stopOverImage3Attached; // 파일 첨부 여부(첨부 1, 미첨부 0)

    // 생성자 생성
    public BoardDTO(Long id,
                    String boardWriter,
                    String boardTitle,
                    int boardHits,
                    LocalDateTime boardCreatedTime) { // 페이징시 보여줄 게시글 목록에 대한 생성자 ( alt + insert )
        this.id = id;
        this.boardWriter = boardWriter;
        this.boardTitle = boardTitle;
        this.boardHits = boardHits;
        this.boardCreatedTime = boardCreatedTime;
    }
    
    public static BoardDTO toBoardDTO(BoardEntity boardEntity){
        // entity -> DTO
        BoardDTO boardDTO = new BoardDTO();

        boardDTO.setId(boardEntity.getId());
        boardDTO.setBoardWriter(boardEntity.getBoardWriter());
        boardDTO.setBoardTitle(boardEntity.getBoardTitle());
        boardDTO.setBoardContents(boardEntity.getBoardContents());
        boardDTO.setBoardTransportation(String.valueOf(boardEntity.getBoardTransportation()));
        boardDTO.setBoardHits(boardEntity.getBoardHits());
        boardDTO.setBoardCreatedTime(boardEntity.getCreatedTime());
        boardDTO.setBoardUpdatedTime(boardEntity.getUpdatedTime());

        //boardDTO.setFileAttached(boardEntity.getFileAttached());
        if(boardEntity.getFileAttached() == 0){ // 파일 없다면
            System.out.println("DTO 파일없음");
            boardDTO.setFileAttached(boardEntity.getFileAttached()); // 0
        } else { // 파일 있다면
            boardDTO.setFileAttached(boardEntity.getFileAttached()); // 1
            // 파일 이름을 가져가야 함.
            // originalFileName, storedFileName : board_file_table에 있다.(BoardFileEntity)
            // (BoardFileEntity)을 찾기 위해 join 문법 사용
            // select * from board_table b, board_file_table bf where b.id=bf.board_id
            // and where b.id=?
            System.out.println("DTO탐색 ====> " + boardEntity.getBoardFileEntity());
            // 대표사진
            boardDTO.setOriginalTitleImageName(boardEntity.getBoardFileEntity().getOriginalFileName());
            boardDTO.setStoredTitleImageName(boardEntity.getBoardFileEntity().getStoredFileName());


            // 출발지 사진
            boardDTO.setOriginalStartingPositionImageName(boardEntity.getBoardFileEntity()
                    .getBoardPathImageEntityList().get(0).getOriginalFileName());
            boardDTO.setStoredStartingPositionImageName(boardEntity.getBoardFileEntity()
                    .getBoardPathImageEntityList().get(0).getStoredFileName());

            // 도착지 사진
            boardDTO.setOriginalDestinationImageName(boardEntity.getBoardFileEntity()
                    .getBoardPathImageEntityList().get(0).getOriginalFileName());
            boardDTO.setStoredDestinationImageName(boardEntity.getBoardFileEntity()
                    .getBoardPathImageEntityList().get(0).getStoredFileName());

            int mode = boardEntity.getBoardFileEntity().getFileAttached();
            if((mode & 0B01000) == 0B01000){ // 경유지 1
                boardDTO.setOriginalStopOverImage1Name(boardEntity.getBoardFileEntity()
                        .getBoardPathImageEntityList().get(0).getStoredFileName());
                boardDTO.setStoredStopOverImage1Name(boardEntity.getBoardFileEntity()
                        .getBoardPathImageEntityList().get(0).getStoredFileName());
            }
            if((mode & 0B00100) == 0B00100){  // 경유지 2
                boardDTO.setOriginalStopOverImage2Name(boardEntity.getBoardFileEntity()
                        .getBoardPathImageEntityList().get(0).getStoredFileName());
                boardDTO.setStoredStopOverImage2Name(boardEntity.getBoardFileEntity()
                        .getBoardPathImageEntityList().get(0).getStoredFileName());
            }
            if((mode & 0B00010) == 0B00010){  // 경유지 3
                boardDTO.setOriginalStopOverImage3Name(boardEntity.getBoardFileEntity()
                        .getBoardPathImageEntityList().get(0).getStoredFileName());
                boardDTO.setStoredStopOverImage3Name(boardEntity.getBoardFileEntity()
                        .getBoardPathImageEntityList().get(0).getStoredFileName());
            }
        }
        return boardDTO;
    }
}
