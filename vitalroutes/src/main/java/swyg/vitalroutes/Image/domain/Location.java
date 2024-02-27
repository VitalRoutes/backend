package swyg.vitalroutes.Image.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long locationId;
    private int sequence;
    private String fileName;
    private String latitude;
    private String longitude;

    @ManyToOne
    @JoinColumn(name = "image_id")
    private Image image;

    public static Location createLocation(int seq, String fileName, String[] info) {
        Location location = new Location();
        location.setSequence(seq);
        location.setFileName(fileName);
        location.setLatitude(info[0]);
        location.setLongitude(info[1]);
        return location;
    }
}
