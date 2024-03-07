package swyg.vitalroutes.post.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import swyg.vitalroutes.post.entity.BoardEntity;
import swyg.vitalroutes.post.entity.BoardFileEntity;
import swyg.vitalroutes.post.entity.BoardPathImageEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// DTO ( Data Transfer Object), VL, Bean, ... 비슷한 용도
// Entity는 살짝 결이 다름 ( 여러 파라미터를 한 객체에 담아서 보내는 용도 )
@Getter
@Setter
@ToString
@NoArgsConstructor // 기본 생성자
@AllArgsConstructor // 모든 필드를 매개변수로 하는 생성자
public class BoardDTO {
    private Long id;
    private String boardWriter; // 작성자
    private String boardTitle; // 제목
    private String boardContents; // 본문
    private String boardTransportation; // 이동수단
    private int boardHits; // 조회수
    private LocalDateTime boardCreatedTime;
    private LocalDateTime boardUpdatedTime;

    // DTO에서 받는 부분은 MultipartFile만 동작
    private MultipartFile titleImage; // 실제 파일을 담아줄 수 있는 역할 (대표사진 저장)
    // save.html -> Controller로 파일 담는용도
    
    // Service 클래스에서 사용할 것
    private String originalTitleImageName; // 원본 파일 이름
    private String storedTitleImageName; // 서버 저장용 파일 이름
    private int fileAttached; // 파일 첨부 여부(첨부 1, 미첨부 0)

    private MultipartFile startingPositionImage; // 출발지 이미지 파일 담는용도
    private String originalStartingPositionImageName; // 출발지 이미지 원본 파일 이름
    private String storedStartingPositionImageName; // 출발지 이미지 서버저장용 파일 이름
    private int startingPositionImageAttached; // 파일 첨부 여부(첨부 1, 미첨부 0)

    private MultipartFile destinationImage; // 도착지 이미지 파일 담는용도
    private String originalDestinationImageName; // 도착지 이미지 원본 파일 이름
    private String storedDestinationImageName; // 도착지 이미지 서버저장용 파일 이름
    private int destinationImageAttached; // 파일 첨부 여부(첨부 1, 미첨부 0)

    private MultipartFile stopOverImage1; // 경유지1 이미지 파일 담는용도
    private String originalStopOverImage1Name; // 경유지1 이미지 원본 파일 이름
    private String storedStopOverImage1Name; // 경유지1 이미지 서버저장용 파일 이름
    private int stopOverImage1Attached; // 파일 첨부 여부(첨부 1, 미첨부 0)

    private MultipartFile stopOverImage2; // 경유지2 이미지 파일 담는용도
    private String originalStopOverImage2Name; // 경유지2 이미지 원본 파일 이름
    private String storedStopOverImage2Name; // 경유지2 이미지 서버저장용 파일 이름
    private int stopOverImage2Attached; // 파일 첨부 여부(첨부 1, 미첨부 0)

    private MultipartFile stopOverImage3; // 경유지3 이미지 파일 담는용도
    private String originalStopOverImage3Name; // 경유지3 이미지 원본 파일 이름
    private String storedStopOverImage3Name; // 경유지3 이미지 서버저장용 파일 이름
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
            System.out.println("DTO탐색 ====> " + boardEntity.getBoardFileEntityList().get(0));
            // 대표사진
            boardDTO.setOriginalTitleImageName(boardEntity.getBoardFileEntityList().get(0).getOriginalFileName());
            boardDTO.setStoredTitleImageName(boardEntity.getBoardFileEntityList().get(0).getStoredFileName());

            // 출발지 사진
            boardDTO.setOriginalStartingPositionImageName(boardEntity.getBoardFileEntityList().get(0)
                    .getBoardPathImageEntityList().get(0).getOriginalFileName());
            boardDTO.setStoredStartingPositionImageName(boardEntity.getBoardFileEntityList().get(0)
                    .getBoardPathImageEntityList().get(0).getStoredFileName());

            // 도착지 사진
            boardDTO.setOriginalDestinationImageName(boardEntity.getBoardFileEntityList().get(0)
                    .getBoardPathImageEntityList().get(0).getOriginalFileName());
            boardDTO.setStoredDestinationImageName(boardEntity.getBoardFileEntityList().get(0)
                    .getBoardPathImageEntityList().get(0).getStoredFileName());

            int mode = boardEntity.getBoardFileEntityList().get(0).getFileAttached();
            if((mode & 0B01000) == 0B01000){ // 경유지 1
                boardDTO.setOriginalStopOverImage1Name(boardEntity.getBoardFileEntityList().get(0)
                        .getBoardPathImageEntityList().get(0).getStoredFileName());
                boardDTO.setStoredStopOverImage1Name(boardEntity.getBoardFileEntityList().get(0)
                        .getBoardPathImageEntityList().get(0).getStoredFileName());
            }
            if((mode & 0B00100) == 0B00100){  // 경유지 2
                boardDTO.setOriginalStopOverImage2Name(boardEntity.getBoardFileEntityList().get(0)
                        .getBoardPathImageEntityList().get(0).getStoredFileName());
                boardDTO.setStoredStopOverImage2Name(boardEntity.getBoardFileEntityList().get(0)
                        .getBoardPathImageEntityList().get(0).getStoredFileName());
            }
            if((mode & 0B00010) == 0B00010){  // 경유지 3
                boardDTO.setOriginalStopOverImage3Name(boardEntity.getBoardFileEntityList().get(0)
                        .getBoardPathImageEntityList().get(0).getStoredFileName());
                boardDTO.setStoredStopOverImage3Name(boardEntity.getBoardFileEntityList().get(0)
                        .getBoardPathImageEntityList().get(0).getStoredFileName());
            }
        }
        return boardDTO;
    }
}
