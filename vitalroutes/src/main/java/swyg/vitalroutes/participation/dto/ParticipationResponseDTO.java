package swyg.vitalroutes.participation.dto;

import lombok.Data;
import swyg.vitalroutes.participation.domain.Participation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Data
public class ParticipationResponseDTO {
    private Long participationId;
    private Long memberId;
    private String memberProfile;
    private String nickname;
    private String content;
    private String timeString;
    
    // 등록된 이미지의 개수
    private int totalImages;
    private List<ImageResponseDTO> participationImages;
    
    // 참여에 대한 댓글( comment ) 카운트
    private long totalComments;

    public ParticipationResponseDTO(Participation participation) {
        participationId = participation.getParticipationId();
        memberId = participation.getMember().getMemberId();
        memberProfile = participation.getMember().getProfile();
        nickname = participation.getMember().getNickname();
        content = participation.getContent();
        timeString = calTimeString(participation.getLocalDateTime());
        totalImages = participation.getParticipationImages().size();
        participationImages = participation.getParticipationImages().stream().map(ImageResponseDTO::new).toList();
    }

    public static String calTimeString(LocalDateTime localDateTime) {
        long between = ChronoUnit.MINUTES.between(localDateTime, LocalDateTime.now());
        String result = "";
        if (between < 1) {
            result = "방금 전";
        } else if (between < 60) {
            result = between + "분 전";
        } else if (between < 60 * 24) {
            result = (between/60) + "시간 전";
        } else if (between < 60 * 24 * 10) {
            result = (between/60/24) + "일 전";
        } else {
            result = localDateTime.format(DateTimeFormatter.ofPattern("MM월 dd일"));
        }
        return result;
    }
}
