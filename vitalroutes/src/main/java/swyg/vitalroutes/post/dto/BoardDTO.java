package swyg.vitalroutes.post.dto;

import lombok.*;
import org.springframework.core.SpringVersion;
import org.springframework.web.multipart.MultipartFile;
import swyg.vitalroutes.post.entity.BoardEntity;
import swyg.vitalroutes.post.entity.BoardFileEntity;

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

    private MultipartFile boardFile; // 실제 파일을 담아줄 수 있는 역할
    // save.html -> Controller로 파일 담는용도
    private String originalFileName; // 원본 파일 이름
    private String storedFileName; // 서버 저장용 파일 이름
    private int fileAttached; // 파일 첨부 여부(첨부 1, 미첨부 0)

    // 생성자 생성
    public BoardDTO(Long id, String boardWriter, String boardTitle, int boardHits, LocalDateTime boardCreatedTime) {
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
        boardDTO.setBoardHits(boardEntity.getBoardHits());
        boardDTO.setBoardCreatedTime(boardEntity.getCreatedTime());
        boardDTO.setBoardUpdatedTime(boardEntity.getUpdatedTime());

        //boardDTO.setFileAttached(boardEntity.getFileAttached());
        if(boardEntity.getFileAttached() == 0){ // 파일 없다면
            boardDTO.setFileAttached(boardEntity.getFileAttached()); // 0
        } else { // 파일 있다면
            boardDTO.setFileAttached(boardEntity.getFileAttached()); // 1
            // 파일 이름을 가져가야 함.
            // originalFileName, storedFileName : board_file_table에 있다.(BoardFileEntity)
            // (BoardFileEntity)을 찾기 위해 join 문법 사용
            // select * from board_table b, board_file_table bf where b.id=bf.board_id
            // and where b.id=?
            boardDTO.setOriginalFileName(boardEntity.getBoardFileEntityList().get(0).getOriginalFileName());
            boardDTO.setStoredFileName(boardEntity.getBoardFileEntityList().get(0).getStoredFileName());
        }
        return boardDTO;
    }
}
