package swyg.vitalroutes.member.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member {
    /**
     * country 제거
     * nickname 추가
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;
    private String profile;
    private String name;
    @Column(unique = true)
    private String nickname;
    @Column(unique = true)
    private String email;
    private String password;
    private String socialId;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    // JWT 토큰에 들어갈 정보
    public Map<String, Object> getClaims() {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("memberId", memberId);
        dataMap.put("profile", profile);
        dataMap.put("name", name);
        dataMap.put("nickname", nickname);
        dataMap.put("email", email);
        dataMap.put("socialId", socialId);
        dataMap.put("socialType", socialType);
        return dataMap;
    }
}
