package swyg.vitalroutes.post.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@Setter
@Entity
@Table(name = "post_tag")
public class BoardTagMapping extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_tag_map_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private BoardEntity boardEntity;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "tag_id")
    private TagEntity tagEntity;

    public static BoardTagMapping savedBoardTagMap(BoardEntity boardEntity,
                                                   TagEntity tagEntity) {
        BoardTagMapping boardTagMapping = new BoardTagMapping();
        boardTagMapping.setBoardEntity(boardEntity);
        boardTagMapping.setTagEntity(tagEntity);

        return boardTagMapping;
    }
}
