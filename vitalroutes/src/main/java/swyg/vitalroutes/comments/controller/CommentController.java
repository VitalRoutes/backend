package swyg.vitalroutes.comments.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import swyg.vitalroutes.comments.dto.CommentModifyDTO;
import swyg.vitalroutes.comments.dto.CommentSaveDTO;
import swyg.vitalroutes.comments.service.CommentService;
import swyg.vitalroutes.common.exception.CommentException;
import swyg.vitalroutes.common.response.ApiResponseDTO;
import swyg.vitalroutes.common.response.DataWithCount;
import swyg.vitalroutes.member.domain.Member;


import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static swyg.vitalroutes.common.response.ResponseType.FAIL;
import static swyg.vitalroutes.common.response.ResponseType.SUCCESS;

@Tag(name = "챌린지 참여의 댓글 API Controller", description = "챌린지 참여에 달린 댓글( 대댓글 ) 관련된 API 를 제공")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    /**
     * 챌린지 참여에 대한 댓글 불러오기
     */
    @Operation(description = "챌린지 참여 ID 를 통해 댓글을 조회", summary = "챌린지 참여의 댓글 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "OK SUCCESS",
                    description = "조회 성공",
                    content = @Content(mediaType = "application/json",
                            examples = {@ExampleObject(name = "Postman 에서 확인한 응답 예시", value = "{\n" +
                                    "    \"status\": \"OK\",\n" +
                                    "    \"type\": \"SUCCESS\",\n" +
                                    "    \"message\": null,\n" +
                                    "    \"data\": {\n" +
                                    "        \"totalCount\": 9,\n" +
                                    "        \"remainFlag\": true,\n" +
                                    "        \"data\": [\n" +
                                    "            {\n" +
                                    "                \"commentId\": 7,\n" +
                                    "                \"memberProfile\": \"testProfileImg\",\n" +
                                    "                \"nickname\": \"테스트유저1\",\n" +
                                    "                \"content\": \"챌린지 참여 댓글댓글댓글\",\n" +
                                    "                \"timeString\": \"16시간 전\"\n" +
                                    "            }\n" +
                                    "        ]\n" +
                                    "    }\n" +
                                    "}")}))
    })
    @Parameters(value = {
            @Parameter(name = "participationId", description = "참여 ID"),
            @Parameter(name = "pageable", description = "size 는 디폴트로 5를 지정해놓았기 때문에 page 만 전달해주면 됨")
    })
    @GetMapping("/view/{participationId}")
    public ApiResponseDTO<?> findAllComments(@PathVariable Long participationId, @PageableDefault(size = 5) Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = (Member) authentication.getPrincipal();

        DataWithCount<?> dataWithCount = commentService.viewComments(member.getMemberId(), participationId, pageable);
        return new ApiResponseDTO<>(OK, SUCCESS, null, dataWithCount);
    }

    @Operation(description = "챌린지 참여 ID 를 통해 댓글을 저장", summary = "챌린지 참여의 댓글 저장")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "OK SUCCESS",
                    description = "댓글 작성 완료",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "BAD REQUEST",
                    description = "데이터 validation 오류",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "NOT FOUND",
                    description = "사용자 혹은 챌린지 참여가 존재하지 않는다",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class)))
    })
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


    @Operation(description = "댓글 ID 를 통해 수정", summary = "챌린지 참여의 댓글 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "OK SUCCESS",
                    description = "댓글 수정 완료",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "NOT FOUND",
                    description = "댓글이 존재하지 않는다",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class)))
    })
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

    @Operation(description = "댓글 ID 를 통해 삭제", summary = "챌린지 참여의 댓글 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "OK SUCCESS",
                    description = "댓글 삭제 완료",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "NOT FOUND",
                    description = "댓글이 존재하지 않는다",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class)))
    })
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
