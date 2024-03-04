package swyg.vitalroutes.comments.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import swyg.vitalroutes.comments.dto.CommentModifyDTO;
import swyg.vitalroutes.comments.dto.CommentSaveDTO;
import swyg.vitalroutes.comments.service.CommentService;
import swyg.vitalroutes.common.exception.CommentException;
import swyg.vitalroutes.common.response.ApiResponseDTO;
import swyg.vitalroutes.common.response.DataWithCount;


import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static swyg.vitalroutes.common.response.ResponseType.FAIL;
import static swyg.vitalroutes.common.response.ResponseType.SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    /**
     * 챌린지 참여에 대한 댓글 불러오기
     */
    @GetMapping("/view/{participationId}")
    public ApiResponseDTO<?> findAllComments(@PathVariable Long participationId, @PageableDefault(size = 5) Pageable pageable) {
        DataWithCount<?> dataWithCount = commentService.viewComments(participationId, pageable);
        return new ApiResponseDTO<>(OK, SUCCESS, null, dataWithCount);
    }

    @PostMapping("/save")
    public ApiResponseDTO<?> saveComment(@Valid @RequestBody CommentSaveDTO saveDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ApiResponseDTO<>(BAD_REQUEST, FAIL, bindingResult.getFieldError().getDefaultMessage(), null);
        }

        try {
            commentService.saveComment(saveDTO);
        } catch (CommentException exception) {
            return new ApiResponseDTO<>(exception.getStatus(), exception.getType(), exception.getMessage(), null);
        }
        return new ApiResponseDTO<>(OK, SUCCESS, "댓글 작성이 완료되었습니다", null);
    }

    @PatchMapping("/{commentId}")
    public ApiResponseDTO<?> modifyComment(@PathVariable Long commentId, @Valid @RequestBody CommentModifyDTO modifyDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ApiResponseDTO<>(BAD_REQUEST, FAIL, bindingResult.getFieldError().getDefaultMessage(), null);
        }

        try {
            commentService.modifyComment(commentId, modifyDTO);
        } catch (CommentException exception) {
            return new ApiResponseDTO<>(exception.getStatus(), exception.getType(), exception.getMessage(), null);
        }
        return new ApiResponseDTO<>(OK, SUCCESS, "댓글 수정이 완료되었습니다", null);
    }

    @DeleteMapping("/{commentId}")
    public ApiResponseDTO<?> deleteComment(@PathVariable Long commentId) {
        try {
            commentService.deleteComment(commentId);
        } catch (CommentException exception) {
            return new ApiResponseDTO<>(exception.getStatus(), exception.getType(), exception.getMessage(), null);
        }
        return new ApiResponseDTO<>(OK, SUCCESS, "댓글이 삭제되었습니다", null);
    }
}
