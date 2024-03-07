package swyg.vitalroutes.participation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Schema(description = "챌린지 참여 등록 시 사용되는 데이터, form-data 형식으로 넘겨주어야 함")
@Data
public class ParticipationSaveDTO {

    private Long boardId;

    @NotBlank(message = "내용은 1000자 이내로 작성해주세요")
    @Size(min = 1, max = 1000, message = "내용은 1000자 이내로 작성해주세요")
    private String content;

    @Size(min = 2, max = 5, message = "사진은 최소 2장에서최대 5장까지 등록할 수 있습니다")
    @Schema(description = "string 이 아닌 파일 자체를 넘겨주어야함")
    private List<MultipartFile> files;
}
