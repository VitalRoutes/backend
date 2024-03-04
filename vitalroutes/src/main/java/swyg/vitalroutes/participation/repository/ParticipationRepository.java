package swyg.vitalroutes.participation.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import swyg.vitalroutes.participation.domain.Participation;

import java.util.List;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    @EntityGraph(attributePaths = "locations")
    @Query("select p from Participation p join fetch p.member where p.board.id = :boardId")
    List<Participation> findAllByBoardId(@Param("boardId") Long boardId, Pageable pageable);

    @Query("select count(p) from Participation p where p.board.id = :boardId")
    long countAllByBoardId(@Param("boardId") Long boardId);
}
