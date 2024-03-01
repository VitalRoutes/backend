package swyg.vitalroutes.security.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import swyg.vitalroutes.common.response.ApiResponseDTO;
import swyg.vitalroutes.member.domain.Member;
import swyg.vitalroutes.member.domain.SocialType;
import swyg.vitalroutes.security.domain.SocialMemberDTO;
import swyg.vitalroutes.security.service.KakaoLoginService;
import swyg.vitalroutes.security.utils.JwtConstants;
import swyg.vitalroutes.security.utils.JwtTokenProvider;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpStatus.OK;
import static swyg.vitalroutes.common.response.ResponseType.ONGOING;
import static swyg.vitalroutes.common.response.ResponseType.SUCCESS;

@Tag(name = "Kakao Login API Controller", description = "카카오 소셜 로그인을 위한 controller ")
@Slf4j
@RestController
@RequiredArgsConstructor
public class KakaoLoginController {
    @Value("${kakao.login.client-id}")
    private String restAPiKey;

    @Value("${kakao.login.redirect-uri}")
    private String redirectURI;

    private final KakaoLoginService kakaoLoginService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(description = "카카오 로그인 페이지로 이동하게 리다이렉트 시킨다", summary = "카카오 로그인 페이지 호출")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "301",
                    description = "카카오 로그인 화면으로 리다이렉트")}
    )
    @GetMapping("/oauth2/kakao")
    public void kakaoRedirect(HttpServletResponse response) throws IOException {
        String uriString = UriComponentsBuilder
                .fromUriString("https://kauth.kakao.com/oauth/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", restAPiKey)
                .queryParam("redirect_uri", redirectURI)
                .toUriString();
        response.sendRedirect(uriString);
    }

    @Operation(description = "카카오 로그인 페이지로 이동하게 리다이렉트 시킨다", summary = "카카오 로그인 페이지 호출")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "로그인 성공, 일반 로그인 성공과 동일한 응답",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "202",
                    description = "최초 로그인 시 회원가입을 위해 필요한 정보 전달, name, socialId, socialType 데이터만 담겨있음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SocialMemberDTO.class)))
    })
    @Parameters(value = {
            @Parameter(required = true, description = "카카오 로그인 후 받게 되는 인가코드를 전달해야함")
    })
    @GetMapping("/oauth2/kakao/login")
    public ApiResponseDTO<?> kakaoLogin(@RequestParam("code") String code) {
        String[] userInfo = kakaoLoginService.kakaoLogin(code);
        Optional<Member> optionalMember = kakaoLoginService.findMember(userInfo[0], SocialType.KAKAO);
        if (optionalMember.isPresent()) {
            log.info("----------------------- 존재하는 소셜 회원 -----------------------");
            // 사용자가 있는 경우, 일반 로그인 성공과 동일하게 처리( 토큰 발급 )
            Member member = optionalMember.get();
            Map<String, Object> claims = member.getClaims();
            claims.put("accessToken", jwtTokenProvider.generateToken(claims, JwtConstants.ACCESS_EXP_TIME));
            claims.put("refreshToken", jwtTokenProvider.generateToken(claims, JwtConstants.REFRESH_EXP_TIME));
            return new ApiResponseDTO<>(OK, SUCCESS, "로그인이 완료되었습니다", claims);
        }
        log.info("----------------------- 회원가입 필요 -----------------------");
        SocialMemberDTO socialMemberDTO = new SocialMemberDTO();
        socialMemberDTO.setName(userInfo[1]);
        socialMemberDTO.setSocialId(userInfo[0]);
        socialMemberDTO.setSocialType("KAKAO");
        return new ApiResponseDTO<>(OK, ONGOING, "회원가입 완료를 위해 닉네임을 설정해주세요", socialMemberDTO);
    }
}
