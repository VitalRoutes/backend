package swyg.vitalroutes.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import swyg.vitalroutes.common.exception.MemberSignUpException;
import swyg.vitalroutes.common.response.ApiResponseDTO;
import swyg.vitalroutes.member.domain.MemberNicknameDTO;
import swyg.vitalroutes.member.domain.MemberSaveDTO;
import swyg.vitalroutes.member.service.MemberService;
import swyg.vitalroutes.security.domain.SocialMemberDTO;


import static org.springframework.http.HttpStatus.*;
import static swyg.vitalroutes.common.response.ResponseType.*;


@Tag(name = "Member API Controller", description = "Member 회원가입, 로그인을 기능을 제공하는 Controller")
@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(description = "회원가입 전, 닉네임 중복 확인 시 호출하는 API", summary = "닉네임 중복 확인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "OK",
                    description = "닉네임 인증 완료",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "BAD_REQUEST",
                    description = "닉네임 전달 오류 or 이미 존재하는 닉네임",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class)))
    })
    @PostMapping("/member/duplicateCheck")
    public ApiResponseDTO<?> duplicateCheck(@RequestBody MemberNicknameDTO dto) {
        String nickname = dto.getNickname();
        if (nickname == null) {
            return new ApiResponseDTO<>(BAD_REQUEST, FAIL, "닉네임이 전달되지 않았습니다", null);
        }
        log.info("nickname = {}", nickname);
        if (memberService.duplicateNicknameCheck(dto.getNickname())) {
            return new ApiResponseDTO<>(BAD_REQUEST, FAIL, "이미 존재하는 닉네임입니다", null);
        }
        return new ApiResponseDTO<>(OK, SUCCESS, "닉네임 인증이 완료되었습니다", null);
    }

    @Operation(description = "일반적으로 회원가입하는 사용자인 경우 호출하는 API", summary = "회원가입")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "CREATED",
                    description = "회원가입 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "BAD_REQUEST",
                    description = "닉네임 중복 확인 필요",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "CONFLICT",
                    description = "DB 에 중복된 닉네임 혹은 이메일이 존재",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "INTERNAL_SERVER_ERROR",
                    description = "회원가입 중 서버 내부 오류 발생",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class)))
    })
    @PostMapping("/member/signUp")
    public ApiResponseDTO<?> signUp(@Valid @RequestBody MemberSaveDTO memberDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new MemberSignUpException(BAD_REQUEST, FAIL, bindingResult.getFieldError().getDefaultMessage());
        }

        if (!memberDTO.getIsChecked()) {
            return new ApiResponseDTO<>(BAD_REQUEST, FAIL, "닉네임 중복 확인이 필요합니다", null);
        }

        try {
            memberService.saveMember(memberDTO);
        } catch (DataIntegrityViolationException e) {
            return new ApiResponseDTO<>(CONFLICT, FAIL, "중복되는 닉네임 혹은 이메일이 존재합니다", null);
        } catch (Exception e) {
            return new ApiResponseDTO<>(INTERNAL_SERVER_ERROR, ERROR, "회원가입 중 오류가 발생하였습니다", null);
        }
        return new ApiResponseDTO<>(CREATED, SUCCESS, "회원가입이 완료되었습니다", null);
    }

    @Operation(description = "최초로 소셜 로그인한 경우, 닉네임 입력 후에 호출해야 하는 API", summary = "소셜 사용자 회원 가입")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "CREATED",
                    description = "회원가입 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "BAD_REQUEST",
                    description = "닉네임 중복 확인 필요",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "CONFLICT",
                    description = "DB 에 중복된 닉네임 혹은 이메일이 존재",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "INTERNAL_SERVER_ERROR",
                    description = "회원가입 중 서버 내부 오류 발생",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class)))
    })
    @PostMapping("/oauth2/signUp")
    public ApiResponseDTO<?> socialSignUp(@Valid @RequestBody SocialMemberDTO memberDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new MemberSignUpException(BAD_REQUEST, FAIL, bindingResult.getFieldError().getDefaultMessage());
        }

        if (!memberDTO.getIsChecked()) {
            return new ApiResponseDTO<>(BAD_REQUEST, FAIL, "닉네임 중복 확인이 필요합니다", null);
        }

        try {
            memberService.saveSocialMember(memberDTO);
        } catch (DataIntegrityViolationException e) {
            return new ApiResponseDTO<>(CONFLICT, FAIL, "중복되는 닉네임이 존재합니다", null);
        } catch (Exception e) {
            return new ApiResponseDTO<>(INTERNAL_SERVER_ERROR, ERROR, "회원가입 중 오류가 발생하였습니다", null);
        }
        return new ApiResponseDTO<>(CREATED, SUCCESS, "회원가입이 완료되었습니다", null);
    }
}
