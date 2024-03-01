package swyg.vitalroutes.security.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "최초 소셜 로그인 시 사용")
@Data
public class SocialMemberDTO {
    
    @NotBlank(message = "이름은 필수입력값 입니다")
    private String name;

    @NotBlank(message = "닉네임은 필수입력값 입니다")
    @Size(min = 3, max = 16, message = "닉네임은 3자에서 16자까지 입력 가능합니다")
    @Pattern(regexp = "[0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힣|\\-|_|]*", message = "특수문자 없이 최소 3자에서 최대 16자까지 입력 가능합니다")
    @Schema(description = "3자에서 16자 사이로 입력 가능")
    private String nickname;

    @NotBlank(message = "소셜 ID 전달되지 않음")
    @Schema(description = "사용자가 보거나 수정하면 안됨")
    private String socialId;

    @NotBlank(message = "소셜 Type 전달되지 않음")
    @Schema(description = "사용자가 보거나 수정하면 안됨")
    private String socialType;

    @Schema(description = "닉네임 중복 확인 후, 해당 필드를 true 로 보내주어야 함")
    private Boolean isChecked = false;
}
