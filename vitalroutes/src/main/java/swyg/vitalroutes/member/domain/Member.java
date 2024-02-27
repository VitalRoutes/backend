package swyg.vitalroutes.member.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;
    private String profile;
    private String name;
    private String email;
    private String password;
    private String country;
    private String socialId;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;
}
