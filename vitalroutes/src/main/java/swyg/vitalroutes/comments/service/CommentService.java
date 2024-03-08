package swyg.vitalroutes.comments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swyg.vitalroutes.comments.domain.Comment;
import swyg.vitalroutes.comments.dto.CommentModifyDTO;
import swyg.vitalroutes.comments.dto.CommentResponseDTO;
import swyg.vitalroutes.comments.dto.CommentSaveDTO;
import swyg.vitalroutes.comments.repository.CommentRepository;
import swyg.vitalroutes.common.exception.CommentException;
import swyg.vitalroutes.common.response.DataWithCount;
import swyg.vitalroutes.hide.repository.HideRepository;
import swyg.vitalroutes.member.domain.Member;
import swyg.vitalroutes.member.repository.MemberRepository;
import swyg.vitalroutes.participation.domain.Participation;
import swyg.vitalroutes.participation.repository.ParticipationRepository;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static swyg.vitalroutes.common.response.ResponseType.FAIL;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final HideRepository hideRepository;
    private final CommentRepository commentRepository;
    private final ParticipationRepository participationRepository;
    private final MemberRepository memberRepository;


    public DataWithCount<?> viewComments(Long memberId, Long participationId, Pageable pageable) {
        List<Long> hidedComments = hideRepository.findHidedComments(memberId);
        // 숨김처리한 게시글 혹은 댓글이 없으면 NULL 이 들어가서 아무것도 조회되지 않음
        if (hidedComments.size() == 0) {
            hidedComments.add(-1L);
        }
        Page<Comment> pagingData = commentRepository .findAllByParticipationId(participationId, hidedComments, pageable);
        Page<CommentResponseDTO> pagingDTO = pagingData.map(CommentResponseDTO::new);

        long count = pagingData.getTotalElements();
        boolean remainFlag = pagingData.hasNext();

        return new DataWithCount<>(count, remainFlag, pagingDTO.getContent());
    }


    public void saveComment(Long memberId, CommentSaveDTO saveDTO) {
        // 거치지 않아도 되지만 거치려고함
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CommentException(NOT_FOUND, FAIL, "사용자가 존재하지 않습니다"));
        Participation participation = participationRepository.findById(saveDTO.getParticipationId())
                .orElseThrow(() -> new CommentException(NOT_FOUND, FAIL, "챌린지 참여가 존재하지 않습니다"));

        Comment comment = Comment.createComment(saveDTO.getContent(), member, participation);
        commentRepository.save(comment);
    }

    public void modifyComment(Long commentId, CommentModifyDTO modifyDTO) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(NOT_FOUND, FAIL, "댓글이 존재하지 않습니다"));
        comment.setContent(modifyDTO.getContent());
    }

    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(NOT_FOUND, FAIL, "댓글이 존재하지 않습니다"));
        commentRepository.deleteById(commentId);
    }
}
