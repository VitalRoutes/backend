package swyg.vitalroutes.comments.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import swyg.vitalroutes.member.domain.Member;
import swyg.vitalroutes.participation.domain.Participation;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime localDateTime;

    // 참여의 외래키를 관리
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participation_id")
    private Participation participation;

    // 멤버와 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public void setCommentInParticipation(Participation participation) {
        this.participation = participation;
        participation.getComments().add(this);
    }

    public static Comment createComment(String content, Member member, Participation participation) {
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setLocalDateTime(LocalDateTime.now());
        comment.setMember(member);
        comment.setCommentInParticipation(participation);
        return comment;
    }
}
