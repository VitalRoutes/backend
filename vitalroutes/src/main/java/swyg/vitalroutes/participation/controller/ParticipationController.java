package swyg.vitalroutes.participation.controller;

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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import swyg.vitalroutes.common.exception.FileProcessException;
import swyg.vitalroutes.common.exception.ParticipationException;
import swyg.vitalroutes.common.response.ApiResponseDTO;
import swyg.vitalroutes.common.response.DataWithCount;
import swyg.vitalroutes.member.domain.Member;
import swyg.vitalroutes.participation.dto.*;
import swyg.vitalroutes.participation.service.ParticipationService;

import static org.springframework.http.HttpStatus.*;
import static swyg.vitalroutes.common.response.ResponseType.*;

@Tag(name = "챌린지 참여 API Controller", description = "챌린지 참여( 댓글 )와 관련된 API 를 제공")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/participation")
public class ParticipationController {

    private final ParticipationService participationService;

    /**
     * 챌린지( 게시글 )에 대한 챌린지 참여( 참여 게시글? 댓글? ) 불러오기
     */
    @Operation(description = "게시글 ID 를 통해 챌린지 참여 게시글을 조회", summary = "챌린지 참여 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "OK SUCCESS",
                    description = "조회 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "Postman 에서 확인한 응답 예시", value = "{\n" +
                                    "    \"status\": \"OK\",\n" +
                                    "    \"type\": \"SUCCESS\",\n" +
                                    "    \"message\": null,\n" +
                                    "    \"data\": {\n" +
                                    "        \"totalCount\": 15,\n" +
                                    "        \"remainFlag\": true,\n" +
                                    "        \"data\": [\n" +
                                    "            {\n" +
                                    "                \"participationId\": 5,\n" +
                                    "                \"memberProfile\": \"testProfileImg\",\n" +
                                    "                \"nickname\": \"테스트유저1\",\n" +
                                    "                \"content\": \"챌린지 내용이다\",\n" +
                                    "                \"timeString\": \"14시간 전\",\n" +
                                    "                \"totalImages\": 0,\n" +
                                    "                \"participationImages\": [],\n" +
                                    "                \"totalComments\": 0\n" +
                                    "            }\n" +
                                    "        ]\n" +
                                    "    }\n" +
                                    "}")))
    })
    @Parameters(value = {
            @Parameter(name = "boardId", description = "챌린지(게시글) ID"),
            @Parameter(name = "pageable", description = "size 는 디폴트로 5를 지정해놓았기 때문에 page 만 전달해주면 됨")
    })
    @GetMapping("/view/{boardId}")
    public ApiResponseDTO<?> viewAllParticipation(@PathVariable Long boardId, @PageableDefault(size = 5) Pageable pageable) {
        // 숨김 처리한 참여 게시글 조회를 위해 로그인한 사용자의 ID 를 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = (Member) authentication.getPrincipal();

        DataWithCount<?> dataWithCount = participationService.findParticipation(member.getMemberId(), boardId, pageable);

        return new ApiResponseDTO<>(OK, SUCCESS, null, dataWithCount);
    }

    
    @Operation(description = "챌린지 참여 등록하기, form-data 형식으로 전달 필요, files 는 string 이 아닌 파일 형태, ParticipationSaveDTO 참고하면 좋음", summary = "챌린지 참여 게시글 등록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "OK SUCCESS",
                    description = "챌린지 참여 완료",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "BAD REQUEST",
                    description = "데이터 validation 오류 or 이미지에 위치정보 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "NOT FOUND",
                    description = "사용자 혹은 챌린지가 존재하지 않는다",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "INTERNAL_SERVER_ERROR",
                    description = "이미지 처리 중 오류가 발생",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class)))
    })
    @PostMapping("/save")
    public ApiResponseDTO<?> saveParticipation(@Valid ParticipationSaveDTO saveDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ApiResponseDTO<>(BAD_REQUEST, FAIL, bindingResult.getFieldError().getDefaultMessage(), null);
        }

        try {
            participationService.saveParticipation(saveDTO);
        } catch (FileProcessException exception) {
            return new ApiResponseDTO<>(exception.getStatus(), exception.getType(), exception.getMessage(), null);
        } catch (ParticipationException exception) {
            return new ApiResponseDTO<>(exception.getStatus(), exception.getType(), exception.getMessage(), null);
        }
        return new ApiResponseDTO<>(OK, SUCCESS, "챌린지 참여가 완료되었습니다", null);
    }


    @Operation(description = "참여 ID 를 통해 삭제", summary = "챌린지 참여를 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "OK SUCCESS",
                    description = "참여 게시글 삭제 완료",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "NOT FOUND",
                    description = "참여 게시글이 존재하지 않는다",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class)))
    })
    @DeleteMapping("/{participationId}")
    public ApiResponseDTO<?> deleteParticipation(@PathVariable Long participationId) {
        try {
            participationService.deleteParticipation(participationId);
        } catch (ParticipationException exception) {
            return new ApiResponseDTO<>(exception.getStatus(), exception.getType(), exception.getMessage(), null);
        }
        return new ApiResponseDTO<>(OK, SUCCESS, "챌린지 참여 게시글이 삭제되었습니다", null);
    }


    @Operation(description = "수정 전, 참여 ID 를 통해 참여 게시글의 내용을 불러온다", summary = "챌린지 게시글 단건 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "OK SUCCESS",
                    description = "조회 성공",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Postman 에서 확인한 응답 예시", value = "{\n" +
                            "    \"status\": \"OK\",\n" +
                            "    \"type\": \"SUCCESS\",\n" +
                            "    \"message\": null,\n" +
                            "    \"data\": {\n" +
                            "        \"participationId\": 18,\n" +
                            "        \"memberProfile\": \"testProfileImg\",\n" +
                            "        \"nickname\": \"테스트유저1\",\n" +
                            "        \"content\": \"챌린지 내용이다\",\n" +
                            "        \"timeString\": \"방금 전\",\n" +
                            "        \"totalImages\": 2,\n" +
                            "        \"participationImages\": [\n" +
                            "            {\n" +
                            "                \"sequence\": 1,\n" +
                            "                \"fileName\": \"file image url\"\n" +
                            "            },\n" +
                            "            {\n" +
                            "                \"sequence\": 2,\n" +
                            "                \"fileName\": \"file image url\"\n" +
                            "            }\n" +
                            "        ],\n" +
                            "        \"totalComments\": 0\n" +
                            "    }\n" +
                            "}"))),
            @ApiResponse(responseCode = "NOT FOUND",
                    description = "참여 게시글이 존재하지 않는다",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class)))
    })
    @GetMapping("/{participationId}")
    public ApiResponseDTO<?> viewParticipationById(@PathVariable Long participationId) {
        ParticipationResponseDTO participationResponseDTO = null;
        try {
            participationResponseDTO = participationService.findById(participationId);
        } catch (ParticipationException exception) {
            return new ApiResponseDTO<>(exception.getStatus(), exception.getType(), exception.getMessage(), null);
        }
        return new ApiResponseDTO<>(OK, SUCCESS, null, participationResponseDTO);
    }

    /**
     * 참여 이미지를 변경하는 경우 호출되는 API
     * sequence 와 fileURL 이 반환
     */
    @Operation(description = "챌린지 참여의 이미지 수정, 챌린지 수정할 때 이미지를 수정하는 경우 해당 API 를 호출해서 파일을 S3 에 업로드해야함, ImageSaveDTO 참고하면 좋음",
            summary = "챌린지 참여의 이미지 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "OK SUCCESS",
                    description = "이미지 업로드 완료",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Postman 에서 확인한 응답 예시", value = "{\n" +
                            "    \"status\": \"OK\",\n" +
                            "    \"type\": \"SUCCESS\",\n" +
                            "    \"message\": \"이미지가 업로드 되었습니다\",\n" +
                            "    \"data\": {\n" +
                            "        \"sequence\": 3,\n" +
                            "        \"fileName\": \"modifyURL\"\n" +
                            "    }\n" +
                            "}"))),
            @ApiResponse(responseCode = "BAD REQUEST",
                    description = "이미지에 위치정보가 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "INTERNAL_SERVER_ERROR",
                    description = "이미지 처리 중 오류가 발생",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class)))
    })
    @Parameters(value = {
            @Parameter(name = "imageDTO", description = "file 은 string 이 아닌 파일 자체, form-data 로 전달 필요")
    })
    @PostMapping("/image")
    public ApiResponseDTO<?> uploadImage(ImageSaveDTO imageDTO) {
        ImageResponseDTO imageResponseDTO = null;
        try {
            imageResponseDTO = participationService.uploadImage(imageDTO);
        } catch (ParticipationException exception) {
            return new ApiResponseDTO<>(exception.getStatus(), exception.getType(), exception.getMessage(), null);
        }
        return new ApiResponseDTO<>(OK, SUCCESS, "이미지가 업로드 되었습니다", imageResponseDTO);
    }


    @Operation(description = "챌린지 참여의 수정, 수정 내용을 반영할 때 사용, fileName 은 URL 을 의미", summary = "챌린지 참여 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "OK SUCCESS",
                    description = "이미지 업로드 완료",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "BAD REQUEST",
                    description = "데이터 validation 오류",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "NOT_FOUND",
                    description = "챌린지 참여 게시글이 존재하지 않는다",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class)))
    })
    @PatchMapping("/{participationId}")
    public ApiResponseDTO<?> modifyParticipation(@PathVariable Long participationId,
                                                 @Valid @RequestBody ParticipationModifyDTO modifyDTO,
                                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ApiResponseDTO<>(BAD_REQUEST, FAIL, bindingResult.getFieldError().getDefaultMessage(), null);
        }
        
        try {
            participationService.modifyParticipation(participationId, modifyDTO);
        } catch (ParticipationException exception) {
            return new ApiResponseDTO<>(exception.getStatus(), exception.getType(), exception.getMessage(), null);
        }
        
        return new ApiResponseDTO<>(OK, SUCCESS, "수정되었습니다", null);
    }

}
