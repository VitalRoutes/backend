package swyg.vitalroutes.comments.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import swyg.vitalroutes.comments.domain.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("select c from Comment c join fetch c.member where c.participation.participationId = :participationId order by c.commentId asc")
    List<Comment> findAllByParticipationId(@Param("participationId") Long participationId);
}
