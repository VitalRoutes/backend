package swyg.vitalroutes.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "회원가입 시 사용")
@Data
public class MemberSaveDTO {
    /**
     * 닉네임 규칙 : 특수문자 없이 최소 3자에서 최대 16자 사이로 제한
     * 비밀번호 규칙 : 최소 8자 이상이며, 대문자, 소문자, 숫자 중 최소 2종류를 포함
     */

    @NotBlank(message = "이름은 필수입력값 입니다")
    private String name;

    @NotBlank(message = "닉네임은 필수입력값 입니다")
    @Size(min = 3, max = 16, message = "닉네임은 3자에서 16자까지 입력 가능합니다")
    @Pattern(regexp = "[0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힣|\\-|_|]*", message = "특수문자 없이 최소 3자에서 최대 16자까지 입력 가능합니다")
    @Schema(description = "3자에서 16자 사이로 입력 가능")
    private String nickname;

    @NotBlank(message = "이메일은 필수입력값 입니다")
    @Email(message = "올바르지 않은 이메일 형식입니다")
    private String email;

    @NotBlank(message = "비밀번호는 필수입력값 입니다")
    @Pattern(regexp = "^(?!((?:[A-Za-z]+)|(?:[~!@#$%^&*()_+=]+)|(?:[0-9]+))$)[A-Za-z\\d~!@#$%^&*()_+=]{8,16}$", message = "비밀번호는 대문자, 소문자, 숫자를 포함한 최소 8자 이상, 20자 이하여야 합니다")
    @Schema(description = "대문자, 소문자, 숫자를 포함한 최소 8자 이상, 20자 이하")
    private String password;

    @Schema(description = "닉네임 중복 확인 후, 해당 필드를 true 로 보내주어야 함")
    private Boolean isChecked = false;
}
