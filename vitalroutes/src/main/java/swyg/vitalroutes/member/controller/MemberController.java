package swyg.vitalroutes.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.parameters.P;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import swyg.vitalroutes.common.exception.JwtTokenException;
import swyg.vitalroutes.common.exception.MemberModifyException;
import swyg.vitalroutes.common.exception.MemberSignUpException;
import swyg.vitalroutes.common.response.ApiResponseDTO;
import swyg.vitalroutes.member.domain.*;
import swyg.vitalroutes.member.dto.*;
import swyg.vitalroutes.member.service.MailService;
import swyg.vitalroutes.member.service.MemberService;
import swyg.vitalroutes.s3.S3UploadService;
import swyg.vitalroutes.security.dto.SocialMemberDTO;
import swyg.vitalroutes.security.utils.JwtConstants;
import swyg.vitalroutes.security.utils.JwtTokenProvider;


import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.springframework.http.HttpStatus.*;
import static swyg.vitalroutes.common.response.ResponseType.*;


@Tag(name = "Member API Controller", description = "Member 회원가입, 로그인을 기능을 제공하는 Controller")
@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final S3UploadService s3UploadService;
    private final MailService mailService;

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
        try {
            memberService.duplicateNicknameCheck(dto);
        } catch (MemberSignUpException exception) {
            return new ApiResponseDTO<>(exception.getStatus(), exception.getType(), exception.getMessage(), null);
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
            return new ApiResponseDTO<>(BAD_REQUEST, FAIL, bindingResult.getFieldError().getDefaultMessage(), null);
        }

        try {
            memberService.saveMember(memberDTO);
        } catch (MemberSignUpException exception) {
            return new ApiResponseDTO<>(exception.getStatus(), exception.getType(), exception.getMessage(), null);
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

        try {
            memberService.saveSocialMember(memberDTO);
        } catch (MemberSignUpException exception) {
            return new ApiResponseDTO<>(exception.getStatus(), exception.getType(), exception.getMessage(), null);
        } catch (DataIntegrityViolationException e) {
            return new ApiResponseDTO<>(CONFLICT, FAIL, "중복되는 닉네임이 존재합니다", null);
        } catch (Exception e) {
            return new ApiResponseDTO<>(INTERNAL_SERVER_ERROR, ERROR, "회원가입 중 오류가 발생하였습니다", null);
        }
        return new ApiResponseDTO<>(CREATED, SUCCESS, "회원가입이 완료되었습니다", null);
    }

    @Operation(description = "회원의 프로필 정보를 가져오는 API", summary = "회원 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "OK SUCCESS",
                    description = "회원정보 정상 조회, API 응답 스펙의 data 에 해당 정보가 들어있음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MemberModifyDTO.class))),
            @ApiResponse(responseCode = "NOT_FOUND FAIL",
                    description = "존재하는 회원이 아님",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class)))
    })
    @Parameters(value = {
            @Parameter(name = "memberId", required = true, description = "로그인 후에 토큰과 함께 전달 받은 회원번호")
    })
    @GetMapping("/member/profile/{memberId}")
    public ApiResponseDTO<?> viewMemberInfo(@PathVariable Long memberId) {
        MemberModifyDTO memberModifyDTO = null;
        try {
            memberModifyDTO = memberService.getMemberInfo(memberId);
        } catch (NoSuchElementException exception) {
            return new ApiResponseDTO<>(NOT_FOUND, FAIL, exception.getMessage(), null);
        }
        return new ApiResponseDTO<>(OK, SUCCESS, null, memberModifyDTO);
    }


    @Operation(description = "회원 정보를 수정하는 API, 수정하는 정보만 전달, 수정되지 않은 데이터는 null", summary = "회원 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "OK SUCCESS",
                    description = "회원정보 수정 완료, data 에 memberId, name, profile, email, access token, refresh token 이 존재",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "NOT_FOUND FAIL",
                    description = "존재하는 회원이 아님",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "CONFLICT FAIL",
                    description = "DB 에 닉네임이나 이메일이 존재",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class)))
    })
    @PatchMapping("/member/profile/{memberId}")
    public ApiResponseDTO<?> modifyMemberInfo(@PathVariable Long memberId, @RequestBody MemberModifyDTO memberDto) {
        log.info("memberDto = {}", memberDto);
        Member member = null;
        try {
            member = memberService.modifyMemberInfo(memberId, memberDto);
        } catch (NoSuchElementException exception) {
            return new ApiResponseDTO<>(NOT_FOUND, FAIL, "존재하지 않는 회원입니다", null);
        } catch (DataIntegrityViolationException exception) {
            return new ApiResponseDTO<>(CONFLICT, FAIL, "중복되는 닉네임 혹은 이메일이 존재합니다", null);
        } catch (MemberModifyException exception) {
            return new ApiResponseDTO<>(exception.getStatus(), exception.getType(), exception.getMessage(), null);
        }
        // 수정 후 사용자 정보를 토큰과 함께 data 에 담아서 전달
        Map<String, Object> claims = member.getClaims();
        claims.put("accessToken", jwtTokenProvider.generateToken(claims, JwtConstants.ACCESS_EXP_TIME));
        claims.put("refreshToken", jwtTokenProvider.generateToken(claims, JwtConstants.REFRESH_EXP_TIME));

        return new ApiResponseDTO<>(OK, SUCCESS, "회원정보 수정이 완료되었습니다", claims);
    }


    @Operation(description = "회원탈퇴 API", summary = "회원 탈퇴")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "OK SUCCESS",
                    description = "회원탈퇴 완료",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "NOT_FOUND FAIL",
                    description = "존재하는 회원이 아님",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class)))
    })
    @DeleteMapping("/member/profile/{memberId}")
    public ApiResponseDTO<?> deleteMember(@PathVariable Long memberId) {
        try {
            memberService.deleteMember(memberId);
        } catch (NoSuchElementException exception) {
            return new ApiResponseDTO<>(NOT_FOUND, FAIL, exception.getMessage(), null);
        }
        return new ApiResponseDTO<>(OK, SUCCESS, "회원탈퇴가 완료되었습니다", null);
    }

    /**
     * 프로필 이미지 변경 프로세스
     * 1. S3 에 이미지를 업로드 하는 API 를 호출한다 -> formData
     * 2. 프로필 이미지를 수정하는 API 를 호출한다 -> formData
     */
    @Operation(description = "프로필 이미지를 S3 에 업로드하는 API, form-data 형식으로 profileImage 에 전달 필요", summary = "프로필 이미지 업로드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "OK SUCCESS",
                    description = "이미지 업로드 완료, data 에 imageURL 이라는 키값으로 업로드된 URL 을 반환",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "INTERNAL_SERVER_ERROR",
                    description = "이미지 업로드 중 에러 발생",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class)))
    })
    @PostMapping("/member/profile/image")
    public ApiResponseDTO<?> uploadProfileImage(MemberProfileImageDTO imageDTO) {
        log.info("imageDTO = {}", imageDTO);
        String imageURL = "";
        try {
            MultipartFile profileImage = imageDTO.getProfileImage();
            imageURL = s3UploadService.saveFile(profileImage);  // UUID + 원본파일명 형태로 저장됨
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponseDTO<>(INTERNAL_SERVER_ERROR, ERROR, "이미지 업로드 중 에러가 발생하였습니다", null);
        }
        return new ApiResponseDTO<>(OK, SUCCESS, "프로필 이미지 업로드가 완료되었습니다", Map.of("imageURL", imageURL));
    }

    @Operation(description = "프로필 이미지를 변경하는 API, form-data 형식으로 profileImageURL 전달 필요", summary = "프로필 이미지 변경")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "OK SUCCESS",
                    description = "프로필 이미지 수정 완료",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "BAD_REQUEST FAIL",
                    description = "프로필 이미지 URL 전달되지 않음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class)))
    })
    @PatchMapping("/member/profile/image/{memberId}")
    public ApiResponseDTO<?> modifyProfileImage(@PathVariable Long memberId, MemberProfileImageDTO imageDTO) {
        MemberModifyDTO memberModifyDTO = null;
        try {
            memberModifyDTO = memberService.modifyProfileImage(memberId, imageDTO);
        } catch (MemberModifyException exception) {
            return new ApiResponseDTO<>(exception.getStatus(), exception.getType(), exception.getMessage(), null);
        } catch (NoSuchElementException exception) {
            return new ApiResponseDTO<>(NOT_FOUND, FAIL, exception.getMessage(), null);
        }
        return new ApiResponseDTO<>(OK, SUCCESS, "프로필 이미지 수정이 완료되었습니다", memberModifyDTO);
    }


    /**
     * 비밀번호 재설정 메일 프로세스
     * 1. message body 에 email 을 전달 받는다
     * 2. email 이 유효하면 해당 주소로 재설정 이메일을 보낸다. 이때 URI 뒤쪽에 식별자 비슷하게 붙는게 하나 있음
     * 3. 재설정 이메일에 접속한 후에 새로운 비밀번호를 입력해서 전달한다. API 요청 시 뒤에 붙은 식별자도 함께 전달해주어야함
     */
    @Operation(description = "비밀번호 재설정 링크롤 보내는 API", summary = "비밀번호 재설정 이메일 전송")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "OK SUCCESS",
                    description = "비밀번호 재설정 링크가 전송 완료",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "NOT_FOUND FAIL",
                    description = "존재하지 않는 이메일",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "INTERNAL_SERVER_ERROR",
                    description = "이메일 발송 실패",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class)))
    })
    @PostMapping("/member/password")
    public ApiResponseDTO<?> sendPasswordEmail(@RequestBody MemberEmailDTO emailDTO) {
        try {
            Member member = memberService.findMemberByEmail(emailDTO);

            Map<String, Object> memberInfo = new HashMap<>();
            memberInfo.put("memberId", member.getMemberId());
            memberInfo.put("email", member.getEmail());
            String token = jwtTokenProvider.generateToken(memberInfo, 5);  // 재설정 링크는 5분 동안 유효

            StringBuffer sb = new StringBuffer();
            sb.append("https://vital-routes.vercel.app/member/password/");
            sb.append(token);
            String url = sb.toString();

            String email = emailDTO.getEmail();
            mailService.sendEmail(member.getName(), email, url);
        } catch (MemberModifyException exception) {
            return new ApiResponseDTO<>(exception.getStatus(), exception.getType(), exception.getMessage(), null);
        } catch (NoSuchElementException exception) {
            return new ApiResponseDTO<>(NOT_FOUND, FAIL, exception.getMessage(), null);
        } catch (MessagingException exception) {
            return new ApiResponseDTO<>(INTERNAL_SERVER_ERROR, ERROR, "이메일 전송에 실패하였습니다", null);
        }
        return new ApiResponseDTO<>(OK, SUCCESS, "비밀번호 재설정 링크가 전송되었습니다", null);
    }

    @Operation(description = "비밀번호 재설정 API", summary = "비밀번호 재설정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "OK SUCCESS",
                    description = "비밀번호 변경 완료",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "NOT_FOUND FAIL",
                    description = "비밀번호 재설정 링크의 유효시간이 지남( 5분 )",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class)))
    })
    @Parameters(value = {
            @Parameter(name = "token", required = true, description = "비밀번호 재설정 링크는 /member/password/xxxx 형태인데 뒤에 붙은 xxx 전달 필요")
    })
    @PatchMapping("/member/password/{token}")
    public ApiResponseDTO<?> modifyPassword(@PathVariable String token, @RequestBody MemberPasswordDTO passwordDTO) {
        try {
            Map<String, Object> tokenValue = jwtTokenProvider.validateToken(token);
            Long memberId = Long.valueOf(String.valueOf(tokenValue.get("memberId")));// 어떤 회원 비밀번호를 변경할지 알게 해주는 데이터
            memberService.modifyPassword(memberId, passwordDTO);
        } catch (JwtTokenException exception) {
            return new ApiResponseDTO<>(NOT_FOUND, FAIL, "올바르지 않은 비밀번호 설정 링크입니다", null);
        } catch (NoSuchElementException exception) {
            return new ApiResponseDTO<>(NOT_FOUND, FAIL, exception.getMessage(), null);
        }
        return new ApiResponseDTO<>(OK, SUCCESS, "비밀번호 변경이 완료되었습니다", null);
    }
}
