package swyg.vitalroutes.participation.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ImageSaveDTO {
    private Long boardId;
    private int sequence;
    private MultipartFile file;
}
