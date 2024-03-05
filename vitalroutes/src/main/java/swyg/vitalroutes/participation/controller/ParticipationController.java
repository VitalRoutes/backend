package swyg.vitalroutes.participation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import swyg.vitalroutes.common.exception.FileProcessException;
import swyg.vitalroutes.common.exception.ParticipationException;
import swyg.vitalroutes.common.response.ApiResponseDTO;
import swyg.vitalroutes.common.response.DataWithCount;
import swyg.vitalroutes.participation.dto.*;
import swyg.vitalroutes.participation.service.ParticipationService;

import static org.springframework.http.HttpStatus.*;
import static swyg.vitalroutes.common.response.ResponseType.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/participation")
public class ParticipationController {

    private final ParticipationService participationService;

    /**
     * 챌린지( 게시글 )에 대한 챌린지 참여( 참여 게시글 ) 불러오기
     */
    @GetMapping("/view/{boardId}")
    public ApiResponseDTO<?> viewAllParticipation(@PathVariable Long boardId, @PageableDefault(size = 5) Pageable pageable) {
        DataWithCount<?> dataWithCount = participationService.findParticipation(boardId, pageable);
        return new ApiResponseDTO<>(OK, SUCCESS, null, dataWithCount);
    }

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

    @DeleteMapping("/{participationId}")
    public ApiResponseDTO<?> deleteParticipation(@PathVariable Long participationId) {
        try {
            participationService.deleteParticipation(participationId);
        } catch (ParticipationException exception) {
            return new ApiResponseDTO<>(exception.getStatus(), exception.getType(), exception.getMessage(), null);
        }
        return new ApiResponseDTO<>(OK, SUCCESS, "챌린지 참여 게시글이 삭제되었습니다", null);
    }


    @GetMapping("/{participationId}")
    public ApiResponseDTO<?> viewParticipationById(@PathVariable Long participationId) {
        ParticipationResponseDTO participationResponseDTO = participationService.findById(participationId);
        return new ApiResponseDTO<>(OK, SUCCESS, null, participationResponseDTO);
    }

    /**
     * 참여 이미지를 변경하는 경우 호출되는 API
     * sequence 와 fileURL 이 반환
     */
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

    @PatchMapping("/{participationId}")
    public ApiResponseDTO<?> modifyParticipation(@PathVariable Long participationId,
                                                 @Valid @RequestBody ParticipationModifyDTO modifyDTO,
                                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ApiResponseDTO<>(BAD_REQUEST, FAIL, bindingResult.getFieldError().getDefaultMessage(), null);
        }
        participationService.modifyParticipation(participationId, modifyDTO);
        return new ApiResponseDTO<>(OK, SUCCESS, "수정되었습니다", null);
    }

}
