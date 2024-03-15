package swyg.vitalroutes.participation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import swyg.vitalroutes.common.response.MyChallengeDTO;
import swyg.vitalroutes.participation.domain.Participation;

import java.util.Optional;


public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    @EntityGraph(attributePaths = "participationImages")
    @Query("select p from Participation p join fetch p.member where p.board.id = :boardId order by p.participationId desc")
    Page<Participation> findAllByBoardId(@Param("boardId") Long boardId, Pageable pageable);

    @Query("select new swyg.vitalroutes.common.response.MyChallengeDTO(p.board.id, p.board.boardTitle) from Participation p where p.member.memberId = :memberId order by p.localDateTime desc")
    Page<MyChallengeDTO> findMyParticipation(@Param("memberId") Long memberId, Pageable pageable);

    @Query("select p from Participation p where p.member.memberId = :memberId and p.board.id = :boardId")
    Optional<Participation> findByMemberIdAndBoardId(@Param("memberId") Long memberId, @Param("boardId") Long boardId);
}
