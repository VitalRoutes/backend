package swyg.vitalroutes.participation.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import swyg.vitalroutes.comments.domain.Comment;
import swyg.vitalroutes.member.domain.Member;
import swyg.vitalroutes.post.entity.BoardEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "party")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Participation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long participationId;
    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime localDateTime;

    // 단방향, 연관관계 설정 X
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // 양방향( Board 에서 Participation 조회 필요 ), 연관관계 설정 O
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private BoardEntity board;

    /**
     * @Element 컬렉션 사용으로 @ManyToOne 사용 X
     * 내장 타입( ParticipationImage )으로 인해 연관관계 세팅 필요 없음
     * Repository 에서 @EntityGraph 로 함께 조회 가능
     */
    @ElementCollection
    @OrderBy("sequence asc")    // 사진 순서에 따른 오름차순 정렬
    private List<ParticipationImage> participationImages = new ArrayList<>();

    /**
     * Participation 과 Comment 의 연관관계는 Comment 를 생성할 때 설정
     */
    @OneToMany(mappedBy = "participation", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();


    public void setParticipationInBoard(BoardEntity board) {
        board.getParticipationList().add(this);
        this.board = board;
    }

    public static Participation createParticipation(String content, Member member, BoardEntity board, List<ParticipationImage> participationImages) {
        Participation participation = new Participation();
        participation.setContent(content);
        participation.setLocalDateTime(LocalDateTime.now());
        // 연관관계 세팅
        participation.setMember(member);
        participation.setParticipationInBoard(board);
        for (ParticipationImage participationImage : participationImages) {
            participation.getParticipationImages().add(participationImage);
        }
        return participation;
    }
}
