package swyg.vitalroutes.post.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import swyg.vitalroutes.member.domain.Member;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@Setter
@Entity
@Table(name = "post_like")
public class BoardLikeMemberMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_tag_map_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private BoardEntity boardEntity;

    // 단방향, 연관관계 설정 X
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public static BoardLikeMemberMapping savedBoardLikeMemberMapping(
            BoardEntity boardEntity,
            Member member) {
        BoardLikeMemberMapping boardLikeMemberMapping = new BoardLikeMemberMapping();
        //BoardLikeMemberMapping.setBoardEntity(boardEntity);
        //BoardLikeMemberMapping.setMember(member);

        return boardLikeMemberMapping;
    }
}
