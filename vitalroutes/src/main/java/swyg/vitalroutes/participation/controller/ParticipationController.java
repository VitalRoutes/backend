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
import swyg.vitalroutes.participation.dto.ParticipationSaveDTO;
import swyg.vitalroutes.participation.service.ParticipationService;


import java.util.List;

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

}
