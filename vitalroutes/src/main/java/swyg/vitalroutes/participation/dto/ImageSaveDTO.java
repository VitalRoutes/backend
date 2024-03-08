package swyg.vitalroutes.participation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Schema(description = "챌린지 참여 이미지 변경 시 사용되는 데이터, form-data 형식으로 넘겨주어야 함")
@Data
public class ImageSaveDTO {
    private Long boardId;
    private int sequence;
    @Schema(description = "파일 자체를 전달")
    private MultipartFile file;
}
