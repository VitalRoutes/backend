package swyg.vitalroutes.comments.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import swyg.vitalroutes.comments.domain.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("select c from Comment c join fetch c.member where c.participation.participationId = :participationId and c.commentId not in :hideIds order by c.commentId desc")
    Page<Comment> findAllByParticipationId(@Param("participationId") Long participationId, @Param("hideIds") List<Long> hideIds, Pageable pageable);

    @Query("select count(c) from Comment c where c.participation.participationId = :participationId and c.commentId not in :hideIds")
    long countByParticipationId(@Param("participationId") Long participationId, @Param("hideIds") List<Long> hideIds);
}
