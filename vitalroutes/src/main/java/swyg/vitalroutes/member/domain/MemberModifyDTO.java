package swyg.vitalroutes.member.domain;

import lombok.Data;

@Data
public class MemberModifyDTO {
    private Long memberId;
    private String profile;
    private String name;
    private String nickname;
    private String email;

    private String prePassword;
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
