package swyg.vitalroutes.participation.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class Location {
    private int sequence;
    private String fileName;
    private double latitude;
    private double longitude;

    public static Location createLocation(int seq, String fileName, double[] info) {
        Location location = new Location();
        location.setSequence(seq);
        location.setFileName(fileName);
        location.setLatitude(info[0]);
        location.setLongitude(info[1]);
        return location;
    }
}
