package swyg.vitalroutes.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swyg.vitalroutes.common.exception.MemberSignUpException;
import swyg.vitalroutes.member.domain.MemberSaveDTO;
import swyg.vitalroutes.member.service.MemberService;

import java.util.Map;

@Tag(name = "Member API Controller", description = "Member 회원가입, 로그인을 기능을 제공하는 Controller")
@Slf4j
@RequestMapping("/member")
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(description = "{'nickname' : 'userNickName'} 형태로 전달필요", summary = "닉네임 중복 확인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "닉네임 인증 완료",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400",
                    description = "닉네임 전달 오류 or 이미 존재하는 닉네임 ( 예외 메세지에 표시됨 )",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/duplicateCheck")
    public ResponseEntity<?> duplicateCheck(@RequestBody Map<String, String> map) {
        String nickname = map.get("nickname");
        if (nickname == null) {
            throw new MemberSignUpException("닉네임이 전달되지 않았습니다.");
        }
        log.info("nickname = {}", nickname);
        if (memberService.duplicateNicknameCheck(map.get("nickname"))) {
            throw new MemberSignUpException("이미 존재하는 닉네임입니다.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("success", "닉네임 인증이 완료되었습니다"));
    }

    @Operation(summary = "회원가입")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "회원가입 성공",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400",
                    description = "닉네임 중복 확인 필요",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500",
                    description = "회원가입 중 서버 내부 오류 발생",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@Valid @RequestBody MemberSaveDTO memberDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new MemberSignUpException(bindingResult.getFieldError().getDefaultMessage());
        }

        if (!memberDTO.getIsChecked()) {
            throw new MemberSignUpException("닉네임 중복 확인이 필요합니다");
        }

        try {
            memberService.saveMember(memberDTO);
        } catch (DataIntegrityViolationException e) {
            throw new MemberSignUpException("중복되는 닉네임 혹은 이메일이 존재합니다");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원가입 중 오류가 발생하였습니다");
        }
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("success", "회원가입이 완료되었습니다"));
    }
}
