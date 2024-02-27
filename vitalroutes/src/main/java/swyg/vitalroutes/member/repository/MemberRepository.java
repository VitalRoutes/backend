package swyg.vitalroutes.member.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import swyg.vitalroutes.member.domain.Member;

@Repository
@RequiredArgsConstructor
public class MemberRepository {
    private final EntityManager em;

    // Optional 처리 필요
    public Member findById(Long memberId) {
        return em.find(Member.class, memberId);
    }
}
