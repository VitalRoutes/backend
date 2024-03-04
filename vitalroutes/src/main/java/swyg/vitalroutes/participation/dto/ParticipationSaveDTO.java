package swyg.vitalroutes.participation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ParticipationSaveDTO {

    private Long boardId;
    private Long memberId;

    @NotBlank(message = "내용은 1000자 이내로 작성해주세요")
    @Size(min = 1, max = 1000, message = "내용은 1000자 이내로 작성해주세요")
    private String content;

    @Size(min = 2, max = 5, message = "사진은 최소 2장에서최대 5장까지 등록할 수 있습니다")
    private List<MultipartFile> files;
}
