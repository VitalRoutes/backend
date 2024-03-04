package swyg.vitalroutes.participation.dto;

import lombok.Data;
import swyg.vitalroutes.participation.domain.Location;

@Data
public class LocationResponseDTO {
    private int sequence;
    private String fileName;

    public LocationResponseDTO(Location location) {
        sequence = location.getSequence();
        fileName = location.getFileName();
    }
}
