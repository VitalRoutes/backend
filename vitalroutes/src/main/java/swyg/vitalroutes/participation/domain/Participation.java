package swyg.vitalroutes.participation.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
     * 내장 타입( Location )으로 인해 연관관계 세팅 필요 없음
     * Repository 에서 @EntityGraph 로 함께 조회 가능
     */
    @ElementCollection
    private List<Location> locations = new ArrayList<>();

    /**
     * Comment 와 연관관계 설정 필요, Comment 는 엔티티이기 때문에 연관관계 세팅이 필요함
     */
    
    

    public void setParticipationInBoard(BoardEntity board) {
        // board.getParticipation().add(this);  // BoardEntity 에 List 구현 필요
        this.board = board;
    }

    public static Participation createParticipation(String content, Member member, BoardEntity board, List<Location> locations) {
        Participation participation = new Participation();
        participation.setContent(content);
        participation.setLocalDateTime(LocalDateTime.now());
        // 연관관계 세팅
        participation.setMember(member);
        participation.setParticipationInBoard(board);
        participation.setLocations(locations);
        return participation;
    }
}
