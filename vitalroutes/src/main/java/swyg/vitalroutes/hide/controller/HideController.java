package swyg.vitalroutes.hide.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swyg.vitalroutes.common.response.ApiResponseDTO;
import swyg.vitalroutes.common.response.ResponseType;
import swyg.vitalroutes.hide.service.HideService;
import swyg.vitalroutes.member.domain.Member;

@RestController
@RequestMapping("/hide")
@RequiredArgsConstructor
public class HideController {

    private final HideService hideService;

    @PostMapping("/participation/{participationId}")
    public ApiResponseDTO<?> hideParticipation(@PathVariable Long participationId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = (Member) authentication.getPrincipal();
        hideService.addHideParticipation(member.getMemberId(), participationId);
        return new ApiResponseDTO<>(HttpStatus.OK, ResponseType.SUCCESS, "참여 게시글이 숨김처리되었습니다", null);
    }

    @PostMapping("/comment/{commentId}")
    public ApiResponseDTO<?> hideComment(@PathVariable Long commentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = (Member) authentication.getPrincipal();
        hideService.addHideComment(member.getMemberId(), commentId);
        return new ApiResponseDTO<>(HttpStatus.OK, ResponseType.SUCCESS, "댓글이 숨김처리되었습니다", null);
    }
}
