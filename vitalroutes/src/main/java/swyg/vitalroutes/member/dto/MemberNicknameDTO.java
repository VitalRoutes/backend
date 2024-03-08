package swyg.vitalroutes.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Body 에 담긴 Nickname 을 담는 DTO")
@Data
public class MemberNicknameDTO {
    private String nickname;
}
