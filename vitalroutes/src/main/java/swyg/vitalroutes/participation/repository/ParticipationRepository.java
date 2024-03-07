package swyg.vitalroutes.participation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import swyg.vitalroutes.participation.domain.Participation;

import java.util.List;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    @EntityGraph(attributePaths = "participationImages")
    @Query("select p from Participation p join fetch p.member where p.board.id = :boardId and p.participationId not in :hidedIds order by p.participationId desc")
    Page<Participation> findAllByBoardId(@Param("boardId") Long boardId, @Param("hidedIds") List<Long> hidedIds, Pageable pageable);
}
