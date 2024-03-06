package swyg.vitalroutes.participation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import swyg.vitalroutes.participation.domain.Participation;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    @EntityGraph(attributePaths = "participationImages")
    @Query("select p from Participation p join fetch p.member where p.board.id = :boardId")
    Page<Participation> findAllByBoardId(@Param("boardId") Long boardId, Pageable pageable);
}
