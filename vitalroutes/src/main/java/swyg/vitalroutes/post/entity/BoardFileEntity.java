package swyg.vitalroutes.post.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
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
    // 사작 위치 : 1, 경유지1 : 2, 경유지2 : 3, 경유지3 : 4, 도착위치 : 5
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

    @ManyToOne(fetch = FetchType.LAZY) // N:1 관계, 부모가 호출되더라도 자식을 쓰고자 한다면 값을 불러오고, 아니라면 불러오지 않음
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
        return boardFileEntity;
    }

    public static BoardFileEntity toSaveFileEntity(BoardDTO boardDTO, int num){
        // save.html에서 입력한 값 -> boardDTO에 담긴 작성자값 -> BoardEntity의 작성자값
        BoardFileEntity boardFileEntity = new BoardFileEntity();
        boardFileEntity.setFileAttached(num);
        return boardFileEntity;
    }
}
