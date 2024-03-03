package swyg.vitalroutes.member.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "이메일로 비밀번호 재설정 링크를 받았을 때 비밀번호 변경을 위해 사용")
@Data
public class MemberPasswordDTO {
    private String password;
}
