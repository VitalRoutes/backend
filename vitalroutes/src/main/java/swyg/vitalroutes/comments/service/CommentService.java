package swyg.vitalroutes.comments.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swyg.vitalroutes.comments.domain.Comment;
import swyg.vitalroutes.comments.dto.CommentSaveDTO;
import swyg.vitalroutes.comments.repository.CommentRepository;
import swyg.vitalroutes.common.exception.CommentException;
import swyg.vitalroutes.common.exception.ParticipationException;
import swyg.vitalroutes.member.domain.Member;
import swyg.vitalroutes.member.repository.MemberRepository;
import swyg.vitalroutes.participation.domain.Participation;
import swyg.vitalroutes.participation.repository.ParticipationRepository;
import swyg.vitalroutes.post.entity.BoardEntity;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static swyg.vitalroutes.common.response.ResponseType.FAIL;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final ParticipationRepository participationRepository;
    private final MemberRepository memberRepository;

    public void saveComment(CommentSaveDTO saveDTO) {
        Member member = memberRepository.findById(saveDTO.getMemberId())
                .orElseThrow(() -> new CommentException(BAD_REQUEST, FAIL, "사용자 ID 가 잘못되었습니다"));
        Participation participation = participationRepository.findById(saveDTO.getParticipationId())
                .orElseThrow(() -> new CommentException(BAD_REQUEST, FAIL, "챌린지 참여가 존재하지 않습니다"));

        Comment comment = Comment.createComment(saveDTO.getContent(), member, participation);
        commentRepository.save(comment);
    }
}
