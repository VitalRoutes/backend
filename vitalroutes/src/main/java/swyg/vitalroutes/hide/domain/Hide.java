package swyg.vitalroutes.hide.domain;

import jakarta.persistence.*;
import lombok.*;
import swyg.vitalroutes.comments.domain.Comment;
import swyg.vitalroutes.member.domain.Member;
import swyg.vitalroutes.participation.domain.Participation;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Hide {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hideId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participation_id")
    private Participation participation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

}
