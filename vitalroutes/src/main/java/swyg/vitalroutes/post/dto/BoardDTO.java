package swyg.vitalroutes.post.dto;

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
public class BoardDTO {
    private Long id;
    private String boardWriter; // 작성자
    private String boardTitle; // 제목
    private String boardContents; // 본문
    private String boardTransportation; // 이동수단
    private int boardHits; // 조회수
    private LocalDateTime boardCreatedTime;
    private LocalDateTime boardUpdatedTime;

    private MultipartFile boardFile; // 실제 파일을 담아줄 수 있는 역할 (대표사진 저장)
    // save.html -> Controller로 파일 담는용도
    private String originalFileName; // 원본 파일 이름
    private String storedFileName; // 서버 저장용 파일 이름
    private int fileAttached; // 파일 첨부 여부(첨부 1, 미첨부 0)

    private MultipartFile boardStartingPosition; // 출발지 이미지 저장
    private String boardStartingPositionOriginalFileName; // 출발지 이미지 원본 파일 이름
    private String boardStartingPositionStoredFileName; // 출발지 이미지 서버저장용 파일 이름
    private int boardStartingPositionFileAttached; // 파일 첨부 여부(첨부 1, 미첨부 0)

    /*
    private MultipartFile boardFileDestination; // 도착지 이미지 저장
    private String boardFileDestinationOriginalFileName; // 도착지 이미지 원본 파일 이름
    private String boardFileDestinationStoredFileName; // 도착지 이미지 서버저장용 파일 이름
    private int boardFileDestinationFileAttached; // 파일 첨부 여부(첨부 1, 미첨부 0)

    private MultipartFile boardFileStopOver1; // 경유지1 이미지 저장
    private String boardFileStopOver1OriginalFileName; // 경유지1 이미지 원본 파일 이름
    private String boardFileStopOver1StoredFileName; // 경유지1 이미지 서버저장용 파일 이름
    private int boardFileStopOver1FileAttached; // 파일 첨부 여부(첨부 1, 미첨부 0)

    private MultipartFile boardFileStopOver2; // 경유지2 이미지 저장
    private String boardFileStopOver2OriginalFileName; // 경유지2 이미지 원본 파일 이름
    private String boardFileStopOver2StoredFileName; // 경유지2 이미지 서버저장용 파일 이름
    private int boardFileStopOver2FileAttached; // 파일 첨부 여부(첨부 1, 미첨부 0)

    private MultipartFile boardFileStopOver3; // 경유지3 이미지 저장
    private String boardFileStopOver3OriginalFileName; // 경유지3 이미지 원본 파일 이름
    private String boardFileStopOver3StoredFileName; // 경유지3 이미지 서버저장용 파일 이름
    private int boardFileStopOver3FileAttached; // 파일 첨부 여부(첨부 1, 미첨부 0)
     */

    // 생성자 생성
    public BoardDTO(Long id,
                    String boardWriter,
                    String boardTitle,
                    int boardHits,
                    LocalDateTime boardCreatedTime) {
        this.id = id;
        this.boardWriter = boardWriter;
        this.boardTitle = boardTitle;
        this.boardHits = boardHits;
        this.boardCreatedTime = boardCreatedTime;
    }

    public static BoardDTO toBoardDTO(BoardEntity boardEntity){
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
            boardDTO.setOriginalFileName(boardEntity.getBoardFileEntityList().get(0).getOriginalFileName());
            boardDTO.setStoredFileName(boardEntity.getBoardFileEntityList().get(0).getStoredFileName());

            /*
            boardDTO.setBoardStartingPositionFileAttached(1);
            boardDTO.setBoardStartingPositionOriginalFileName(boardEntity.getBoardFileEntityList().get(0).getOriginalFileName());
            boardDTO.setBoardStartingPositionStoredFileName(boardEntity.getBoardFileEntityList().get(0).getStoredFileName());

            boardDTO.setBoardFileDestinationOriginalFileName(boardEntity.getBoardFileEntityList().get(0).getOriginalFileName());
            boardDTO.setBoardFileDestinationStoredFileName(boardEntity.getBoardFileEntityList().get(0).getStoredFileName());

            boardDTO.setBoardFileStopOver1OriginalFileName(boardEntity.getBoardFileEntityList().get(0).getOriginalFileName());
            boardDTO.setBoardFileStopOver1StoredFileName(boardEntity.getBoardFileEntityList().get(0).getStoredFileName());

            boardDTO.setBoardFileStopOver2OriginalFileName(boardEntity.getBoardFileEntityList().get(0).getOriginalFileName());
            boardDTO.setBoardFileStopOver2StoredFileName(boardEntity.getBoardFileEntityList().get(0).getStoredFileName());

            boardDTO.setBoardFileStopOver3OriginalFileName(boardEntity.getBoardFileEntityList().get(0).getOriginalFileName());
            boardDTO.setBoardFileStopOver3StoredFileName(boardEntity.getBoardFileEntityList().get(0).getStoredFileName());

             */
        }
        return boardDTO;
    }
}
