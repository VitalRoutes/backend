package swyg.vitalroutes.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import swyg.vitalroutes.common.exception.KakaoLoginException;
import swyg.vitalroutes.member.domain.Member;
import swyg.vitalroutes.member.domain.SocialType;
import swyg.vitalroutes.member.repository.MemberRepository;
import swyg.vitalroutes.security.domain.KakaoTokenResponse;
import swyg.vitalroutes.security.domain.KakaoUserInfoResponse;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoLoginService {

    @Value("${kakao.login.client-id}")
    private String restAPiKey;

    @Value("${kakao.login.redirect-uri}")
    private String redirectURI;

    private final MemberRepository memberRepository;

    public Optional<Member> findMember(String socialId, SocialType socialType) {
        return memberRepository.findBySocialIdAndSocialType(socialId, socialType);
    }

    public String[] kakaoLogin(String code) {
        String accessToken = getAccessToken(code);
        return getUserInfo(accessToken);
    }

    public String getAccessToken(String code) {

        RestTemplate restTemplate = new RestTemplate();

        // Header
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        // Body
        MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", restAPiKey);
        body.add("redirect_uri", redirectURI);
        body.add("code", code);

        // Header 와 Body 를 가진 Request 생성
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        String accessTokenUri = "https://kauth.kakao.com/oauth/token";
        UriComponents uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(accessTokenUri).build();

        // HTTP POST 요청
        ResponseEntity<KakaoTokenResponse> response = restTemplate.exchange(uriComponentsBuilder.toUri(), HttpMethod.POST, request, KakaoTokenResponse.class);

        if (response.getStatusCode().value() != 200) {
            throw new KakaoLoginException(response.getStatusCode().value(), "Access Token 을 받는 중 문제가 발생하였습니다");
        }
        
        KakaoTokenResponse responseBody = response.getBody();
        String accessToken = responseBody.getAccessToken();
        return accessToken;
    }

    public String[] getUserInfo(String accessToken) {

        RestTemplate restTemplate = new RestTemplate();

        // Header
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<String> request = new HttpEntity<>(headers);

        String userInfoUri = "https://kapi.kakao.com/v2/user/me";
        UriComponents uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(userInfoUri).build();

        ResponseEntity<KakaoUserInfoResponse> response = restTemplate.exchange(uriComponentsBuilder.toUri(), HttpMethod.GET, request, KakaoUserInfoResponse.class);

        if (response.getStatusCode().value() != 200) {
            throw new KakaoLoginException(response.getStatusCode().value(), "사용자 정보를 받는 중 문제가 발생하였습니다");
        }

        KakaoUserInfoResponse responseBody = response.getBody();
        String socialId = String.valueOf(responseBody.getId());
        String nickname = responseBody.getProperties().getNickname();

        log.info("socialId = {}", socialId);
        log.info("nickname = {}", nickname);
        return new String[]{socialId, nickname};
    }



}
