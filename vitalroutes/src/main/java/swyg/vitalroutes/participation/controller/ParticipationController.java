package swyg.vitalroutes.participation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import swyg.vitalroutes.common.exception.FileProcessException;
import swyg.vitalroutes.common.exception.ParticipationException;
import swyg.vitalroutes.common.response.ApiResponseDTO;
import swyg.vitalroutes.participation.domain.Participation;
import swyg.vitalroutes.participation.dto.ParticipationResponseDTO;
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
     * 임시로 만들어놓은 controller ( 해당 API 를 직접 호출할 일이 없음 )
     * 쓰게 된다면 DTO 로 변환하는 로직을 서비스로 이동
     */
    @GetMapping("/view/{boardId}")
    public ApiResponseDTO<?> viewTest(@PathVariable Long boardId) {
        List<Participation> participations = participationService.findParticipation(boardId);
        List<ParticipationResponseDTO> responseDTO = participations.stream().map(ParticipationResponseDTO::new).toList();
        return new ApiResponseDTO<>(OK, SUCCESS, null, responseDTO);
    }

    @PostMapping("/save")
    public ApiResponseDTO<?> saveParticipation(@Valid ParticipationSaveDTO saveDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ApiResponseDTO<>(BAD_REQUEST, FAIL, bindingResult.getFieldError().getDefaultMessage(), null);
        }
        Participation participation = null;
        try {
            participation = participationService.saveParticipation(saveDTO);
        } catch (FileProcessException exception) {
            return new ApiResponseDTO<>(exception.getStatus(), exception.getType(), exception.getMessage(), null);
        } catch (ParticipationException exception) {
            return new ApiResponseDTO<>(exception.getStatus(), exception.getType(), exception.getMessage(), null);
        }
        return new ApiResponseDTO<>(OK, SUCCESS, "챌린지 참여가 완료되었습니다", null);
    }

}
