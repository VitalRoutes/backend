package swyg.vitalroutes.comments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import swyg.vitalroutes.member.domain.Member;
import swyg.vitalroutes.member.repository.MemberRepository;
import swyg.vitalroutes.participation.domain.Participation;
import swyg.vitalroutes.participation.repository.ParticipationRepository;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static swyg.vitalroutes.common.response.ResponseType.FAIL;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final ParticipationRepository participationRepository;
    private final MemberRepository memberRepository;

    public DataWithCount<?> viewComments(Long participationId, Pageable pageable) {
        log.info("page = {}", pageable.getPageNumber());
        log.info("size = {}", pageable.getOffset());
        List<Comment> entityList = commentRepository.findAllByParticipationId(participationId, pageable);
        List<CommentResponseDTO> dtoList = entityList.stream().map(CommentResponseDTO::new).toList();
        long count = commentRepository.countByParticipationId(participationId);

        boolean remainFlag = (pageable.getOffset() * (pageable.getPageNumber() + 1)) < count;  // 현재까지 보여지고 있는 데이터 외에 남은 데이터가 있는지

        return new DataWithCount<>(count, remainFlag, dtoList);
    }


    public void saveComment(CommentSaveDTO saveDTO) {
        Member member = memberRepository.findById(saveDTO.getMemberId())
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
