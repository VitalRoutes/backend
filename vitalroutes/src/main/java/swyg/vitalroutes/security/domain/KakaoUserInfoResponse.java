package swyg.vitalroutes.security.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KakaoUserInfoResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("properties")
    private KakaoProperties properties;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class KakaoProperties {
        private String nickname;
    }
}
