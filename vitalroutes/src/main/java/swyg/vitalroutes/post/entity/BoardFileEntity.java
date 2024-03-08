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
    private int ExistingPathImage; // 경로이지미 존재 여부

    @OneToOne(fetch = FetchType.LAZY) // 1:1 관계, 부모가 호출되더라도 자식을 쓰고자 한다면 값을 불러오고, 아니라면 불러오지 않음
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

    public static BoardFileEntity toUpdateEntityFile(BoardDTO boardDTO) {
        BoardFileEntity boardFileEntity = new BoardFileEntity();
        boardFileEntity.setId(boardDTO.getId()); // jpa에서 update작업임을 나타내는 id값
        boardFileEntity.setOriginalFileName(boardDTO.getOriginalTitleImageName());
        boardFileEntity.setStoredFileName(boardDTO.getStoredTitleImageName());
        return boardFileEntity;
    }
}
