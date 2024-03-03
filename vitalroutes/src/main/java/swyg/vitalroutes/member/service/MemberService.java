package swyg.vitalroutes.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import swyg.vitalroutes.common.exception.MemberModifyException;
import swyg.vitalroutes.member.domain.Member;
import swyg.vitalroutes.member.domain.MemberModifyDTO;
import swyg.vitalroutes.member.domain.MemberSaveDTO;
import swyg.vitalroutes.member.domain.SocialType;
import swyg.vitalroutes.member.repository.MemberRepository;
import swyg.vitalroutes.security.domain.SocialMemberDTO;

import java.util.Optional;

import static org.springframework.http.HttpStatus.*;
import static swyg.vitalroutes.common.response.ResponseType.*;

@Slf4j
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


    public Optional<Member> getMemberInfo(Long memberId) {
        return memberRepository.findById(memberId);
    }

    public void deleteMember(Long memberId) {
        memberRepository.deleteById(memberId);
    }


    public Member modifyMemberInfo(Long memberId, MemberModifyDTO memberModifyDTO) {
        Member member = memberRepository.findById(memberId).get();  // controller 에서 예외처리

        boolean socialFlag = false;
        if (member.getSocialId() != null) {
            socialFlag = true;
        }

        /**
         * 1. 변경할 데이터만 전달 받는다
         * 2. 닉네임이 전달되면 DB 중복 확인이 필요
         * 3. 이메일 변경된 경우 -> 소셜 회원이라면 ok, 일반 회원이라면 이메일은 삭제 불가
         * 4. 이전 비밀번호는 일치하는지 확인, 새 비밀번호는 규칙에 맞는지 확인
         */

        if (memberModifyDTO.getName() != null) {
            // 일반 JPA 와 동일하게 엔티티 변경하면 하면 커밋 시점에 자동으로 update
            member.setName(memberModifyDTO.getName());
        }

        if (memberModifyDTO.getNickname() != null) {
            // 닉네임 중복 확인을 하지 않았더라도 DB 에 저장될 때 유니크 제약조건에 의해 예외 발생 -> Controller 에서 처리
            member.setNickname(memberModifyDTO.getNickname());
        }

        if (memberModifyDTO.getEmail() != null) {
            // 이메일이 비어있는데 일반 회원인 경우
            if (!StringUtils.hasText(memberModifyDTO.getEmail()) && !socialFlag) {
                throw new MemberModifyException(BAD_REQUEST, FAIL, "일반회원은 이메일을 비워둘 수 없습니다");
            }
            member.setEmail(memberModifyDTO.getEmail());
        }

        if (memberModifyDTO.getNewPassword() != null && memberModifyDTO.getPrePassword() == null) {
            throw new MemberModifyException(BAD_REQUEST, FAIL, "비밀번호 변경 시에는 이전 비밀번호와 새 비밀번호를 입력해주세요");
        } else if (memberModifyDTO.getNewPassword() != null && memberModifyDTO.getPrePassword() != null) {
            // 1. 이전 비밀번호 일치여부 확인
            boolean matchePre = passwordEncoder.matches(memberModifyDTO.getPrePassword(), member.getPassword());
            if (!matchePre) { // 일치하지 않는 경우
                throw new MemberModifyException(BAD_REQUEST, FAIL, "이전 비밀번호가 일치하지 않습니다");
            }
            // 2. 새로운 비밀번호 규칙 준수 확인
            String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,20}$";
            boolean matchNew = memberModifyDTO.getNewPassword().matches(regex);
            if (!matchNew) {
                throw new MemberModifyException(BAD_REQUEST, FAIL, "비밀번호는 대문자, 소문자, 숫자를 포함한 최소 8자 이상, 20자 이하여야 합니다");
            }
            member.setPassword(passwordEncoder.encode(memberModifyDTO.getNewPassword()));
        }

        return member;
    }


    public Member modifyProfileImage(Long memberId, String imageURL) {
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        if (optionalMember.isEmpty()) {
            throw new MemberModifyException(BAD_REQUEST, FAIL, "사용자가 존재하지 않습니다");
        }
        Member member = optionalMember.get();
        member.setProfile(imageURL);
        return member;
    }

}
