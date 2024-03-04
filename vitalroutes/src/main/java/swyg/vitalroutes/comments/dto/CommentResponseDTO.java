package swyg.vitalroutes.comments.dto;

import lombok.Data;
import swyg.vitalroutes.comments.domain.Comment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Data
public class CommentResponseDTO {
    private Long commentId;
    private String memberProfile;
    private String nickname;

    private String content;
    private String timeString;

    public CommentResponseDTO(Comment comment) {
        commentId = comment.getCommentId();
        memberProfile = comment.getMember().getProfile();
        nickname = comment.getMember().getNickname();
        content = comment.getContent();
        timeString = calTimeString(comment.getLocalDateTime());
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
