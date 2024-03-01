package swyg.vitalroutes.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swyg.vitalroutes.member.domain.Member;
import swyg.vitalroutes.member.domain.MemberSaveDTO;
import swyg.vitalroutes.member.domain.SocialType;
import swyg.vitalroutes.member.repository.MemberRepository;
import swyg.vitalroutes.security.domain.SocialMemberDTO;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 닉네임이 중복이면 true
    public boolean duplicateNicknameCheck(String nickname) {
        Optional<Member> byNickname = memberRepository.findByNickname(nickname);
        return byNickname.isPresent();
    }

    public Member saveMember(MemberSaveDTO memberDTO) {
        Member member = Member.builder()
                .name(memberDTO.getName())
                .nickname(memberDTO.getNickname())
                .email(memberDTO.getEmail())
                .password(passwordEncoder.encode(memberDTO.getPassword()))
                .build();
        return memberRepository.save(member);
    }

    public Member saveSocialMember(SocialMemberDTO memberDTO) {
        Member member = Member.builder()
                .name(memberDTO.getName())
                .nickname(memberDTO.getNickname())
                .socialId(memberDTO.getSocialId())
                .socialType(SocialType.valueOf(memberDTO.getSocialType()))
                .build();
        return memberRepository.save(member);
    }

}
