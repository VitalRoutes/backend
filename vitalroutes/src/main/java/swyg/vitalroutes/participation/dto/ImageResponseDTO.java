package swyg.vitalroutes.participation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import swyg.vitalroutes.participation.domain.ParticipationImage;

@Data
@AllArgsConstructor
public class ImageResponseDTO {
    private int sequence;
    private String fileName;

    // 데이터 조회 시 사용
    public ImageResponseDTO(ParticipationImage participationImage) {
        sequence = participationImage.getSequence();
        fileName = participationImage.getFileName();
    }
}
