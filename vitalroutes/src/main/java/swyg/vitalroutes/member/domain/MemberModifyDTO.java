package swyg.vitalroutes.member.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "회원 정보 수정 시 사용, 수정하는 정보만 담아서 전달해야함")
@Data
public class MemberModifyDTO {
    private Long memberId;
    private String profile;
    private String name;
    private String nickname;
    private String email;

    @Schema(description = "이전 비밀번호, 비밀번호 변경 시, 신규 비밀번호와 함께 보내주어야함")
    private String prePassword;
    @Schema(description = "신규 비밀번호, 규칙 준수 여부 판단 로직도 작성되어 있음")
    private String newPassword;

    public static MemberModifyDTO entityToDto(Member member) {
        MemberModifyDTO dto = new MemberModifyDTO();
        dto.setMemberId(member.getMemberId());
        dto.setProfile(member.getProfile());
        dto.setName(member.getName());
        dto.setNickname(member.getNickname());
        dto.setEmail(member.getEmail());
        return dto;
    }
}
