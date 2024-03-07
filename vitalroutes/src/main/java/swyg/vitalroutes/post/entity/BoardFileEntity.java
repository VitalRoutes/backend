package swyg.vitalroutes.post.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import swyg.vitalroutes.post.dto.BoardDTO;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "image")
public class BoardFileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String originalFileName; // 대표사진 이름

    @Column
    private String storedFileName; // 저장된 대표사진 이름

    @Column
    private int fileAttached;
    // 000000
    // 사작 위치 : 10000, 경유지1 : 01000, 경유지2 : 00100, 경유지3 : 00010, 도착위치 : 00001
    /*
    @Column
    private int boardStartingPositionFileAttached; // 파일 있으면 1, 없으면 0

    @Column
    private int boardFileDestinationFileAttached; // 파일 있으면 1, 없으면 0

    @Column
    private int boardFileStopOver1FileAttached; // 파일 있으면 1, 없으면 0

    @Column
    private int boardFileStopOver2FileAttached; // 파일 있으면 1, 없으면 0

    @Column
    private int boardFileStopOver3FileAttached; // 파일 있으면 1, 없으면 0
     */

    @OneToOne(fetch = FetchType.LAZY) // N:1 관계, 부모가 호출되더라도 자식을 쓰고자 한다면 값을 불러오고, 아니라면 불러오지 않음
    //@ManyToOne(fetch = FetchType.EAGER) // N:1 관계, 요청을 하든 안하든 부모가 호출되면 자식도 호출되어 모든 값을 불러옴
    @JoinColumn(name = "post_id") // table에 만들어지는 column이름을 정한다.
    private BoardEntity boardEntity; // DB에 BIGINT타입이지만 Long이 아닌 부모entity타입으로 입력해야한다. 실제 DB에는 그냥 ID값만 들어가게된다.

    @OneToMany(mappedBy = "boardFileEntity", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    // mappedBy : 어떤 것과 매칭을 시킬지 -> BoardFileEntity에 외래키인 boardEntity과 맞춰준다
    // boardFileEntity파일에서 매핑할 변수이름과 동일하게 작성
    private List<BoardPathImageEntity> boardPathImageEntityList = new ArrayList<>();

    public static BoardFileEntity toBoardFileEntity(BoardEntity boardEntity, String originalFileName, String storedFileName) {
        BoardFileEntity boardFileEntity = new BoardFileEntity();
        boardFileEntity.setOriginalFileName(originalFileName);
        boardFileEntity.setStoredFileName(storedFileName);
        boardFileEntity.setBoardEntity(boardEntity);
        //boardFileEntity.setFileAttached(boardFileEntity, 1); // 출발지만 파일 있을때
        //boardFileEntity.setFileAttached(boardFileEntity, 5); // 도착지만 파일 있을때
        return boardFileEntity;
    }

    public static BoardFileEntity toUpdateEntityFile(BoardDTO boardDTO) {
        BoardFileEntity boardFileEntity = new BoardFileEntity();
        boardFileEntity.setId(boardDTO.getId()); // jpa에서 update작업임을 나타내는 id값
        boardFileEntity.setOriginalFileName(boardDTO.getOriginalTitleImageName());
        boardFileEntity.setStoredFileName(boardDTO.getStoredTitleImageName());
        boardFileEntity.setFileAttached(boardFileEntity.setFFileAttached(boardFileEntity, 1));
        return boardFileEntity;
    }

    public int setFFileAttached(BoardFileEntity boardFileEntity, int idx) {
        int mode = boardFileEntity.getFileAttached();
        if(idx == 1) {
            mode = mode + 0B10000;
            boardFileEntity.setFileAttached(mode);
        }
        else if(idx == 2) {
            mode = mode + 0B01000;
            boardFileEntity.setFileAttached(mode);
        }
        else if(idx == 3) {
            mode = mode + 0B00100;
            boardFileEntity.setFileAttached(mode);
        }
        else if(idx == 4) {
            mode = mode + 0B00010;
            boardFileEntity.setFileAttached(mode);
        }
        else if(idx == 5) {
            mode = mode + 0B00001;
            boardFileEntity.setFileAttached(mode);
        }

        return mode;
    }
}
