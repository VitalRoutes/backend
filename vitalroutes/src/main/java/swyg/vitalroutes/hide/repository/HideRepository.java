package swyg.vitalroutes.hide.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import swyg.vitalroutes.hide.domain.Hide;

import java.util.List;

public interface HideRepository extends JpaRepository<Hide, Long> {

    @Query("select h.participation.participationId from Hide h where h.member.memberId = :memberId and h.participation.participationId is not null")
    List<Long> findHidedParticipations(@Param("memberId") Long memberId);

    @Query("select h.comment.commentId from Hide h where h.member.memberId = :memberId and h.comment.commentId is not null")
    List<Long> findHidedComments(@Param("memberId") Long memberId);
}
