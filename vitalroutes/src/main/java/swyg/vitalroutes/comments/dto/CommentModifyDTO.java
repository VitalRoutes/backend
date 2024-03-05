package swyg.vitalroutes.comments.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "챌린지 참여의 댓글을 수정할 때 사용")
@Data
public class CommentModifyDTO {
    @NotBlank(message = "내용은 1000자 이내로 작성해주세요")
    @Size(min = 1, max = 1000, message = "내용은 1000자 이내로 작성해주세요")
    private String content;
}
