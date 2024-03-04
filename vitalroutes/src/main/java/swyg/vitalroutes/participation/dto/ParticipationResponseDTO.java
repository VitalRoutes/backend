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
    private String nickname;
    private String content;
    private String timeString;
    
    // 등록된 이미지의 개수
    private int totalImage;
    private List<LocationResponseDTO> participationImages;
    
    // 댓글(comment) DTO 도 추가 필요 ( 참여에 대한 댓글 )

    public ParticipationResponseDTO(Participation participation) {
        participationId = participation.getParticipationId();
        nickname = participation.getMember().getNickname();
        content = participation.getContent();
        timeString = calTimeString(participation.getLocalDateTime());
        totalImage = participation.getLocations().size();
        participationImages = participation.getLocations().stream().map(location -> new LocationResponseDTO(location)).toList();
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
