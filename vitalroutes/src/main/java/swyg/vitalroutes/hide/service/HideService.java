package swyg.vitalroutes.hide.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swyg.vitalroutes.comments.domain.Comment;
import swyg.vitalroutes.comments.repository.CommentRepository;
import swyg.vitalroutes.hide.domain.Hide;
import swyg.vitalroutes.hide.repository.HideRepository;
import swyg.vitalroutes.member.domain.Member;
import swyg.vitalroutes.member.repository.MemberRepository;
import swyg.vitalroutes.participation.domain.Participation;
import swyg.vitalroutes.participation.repository.ParticipationRepository;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class HideService {

    private final HideRepository hideRepository;
    private final MemberRepository memberRepository;
    private final ParticipationRepository participationRepository;
    private final CommentRepository commentRepository;

    public void addHideParticipation(Long memberId, Long participationId) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        Participation participation = participationRepository.findById(participationId).orElseThrow();
        Hide hide = Hide.builder().member(member).participation(participation).build();
        hideRepository.save(hide);
    }

    public void addHideComment(Long memberId, Long commentId) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        Hide hide = Hide.builder().member(member).comment(comment).build();
        hideRepository.save(hide);
    }

    public List<Long> findHidedParticipations(Long memberId) {
        return hideRepository.findHidedParticipations(memberId);
    }

    public List<Long> findHidedComments(Long memberId) {
        return hideRepository.findHidedComments(memberId);
    }
}
