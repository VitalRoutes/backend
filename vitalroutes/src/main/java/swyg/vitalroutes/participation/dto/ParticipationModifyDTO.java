package swyg.vitalroutes.participation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "챌린지 수정 시 사용하는 데이터")
@Data
public class ParticipationModifyDTO {
    @NotBlank(message = "내용은 1000자 이내로 작성해주세요")
    @Size(min = 1, max = 1000, message = "내용은 1000자 이내로 작성해주세요")
    String content;

    @Size(min = 2, max = 5, message = "사진은 최소 2장에서최대 5장까지 등록할 수 있습니다")
    @Schema(description = "사진을 전달하는 필드")
    List<ImageResponseDTO> uploadedFiles = new ArrayList<>();
}
