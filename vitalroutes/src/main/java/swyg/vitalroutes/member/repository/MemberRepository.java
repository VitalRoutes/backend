package swyg.vitalroutes.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swyg.vitalroutes.member.domain.Member;
import swyg.vitalroutes.member.domain.SocialType;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("select m from Member m where m.nickname = :nickname")
    Optional<Member> findByNickname(@Param("nickname") String nickname);

    @Query("select m from Member m where m.email = :email")
    Optional<Member> findByEmail(@Param("email") String email);

    @Query("select m from Member m where m.socialId = :socialId and m.socialType = :socialType")
    Optional<Member> findBySocialIdAndSocialType(@Param("socialId") String socialId,
                                                 @Param("socialType") SocialType socialType);

    
}
