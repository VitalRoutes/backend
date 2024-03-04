package swyg.vitalroutes.comments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentModifyDTO {
    @NotBlank(message = "내용은 1000자 이내로 작성해주세요")
    @Size(min = 1, max = 1000, message = "내용은 1000자 이내로 작성해주세요")
    private String content;
}
