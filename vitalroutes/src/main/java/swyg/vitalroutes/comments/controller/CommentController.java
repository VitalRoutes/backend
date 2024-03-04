package swyg.vitalroutes.comments.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import swyg.vitalroutes.comments.dto.CommentSaveDTO;
import swyg.vitalroutes.comments.service.CommentService;
import swyg.vitalroutes.common.exception.CommentException;
import swyg.vitalroutes.common.response.ApiResponseDTO;

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
}
