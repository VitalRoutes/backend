package swyg.vitalroutes.Image.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import swyg.vitalroutes.post.domain.Post;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long imageId;
    String titleImg;

    @OneToMany(mappedBy = "image",cascade = CascadeType.ALL)
    private List<Location> locations = new ArrayList<>();

    @OneToOne(mappedBy = "image", fetch = FetchType.LAZY)
    private Post post;

    public void setImageInLocation(Location location) {
        location.setImage(this);
    }

    public static Image crateImage(String titleImg, List<Location> locations) {
        Image image = new Image();
        image.setTitleImg(titleImg);
        for (Location location : locations) {
            image.setImageInLocation(location);
        }
        image.setLocations(locations);
        return image;
    }
}
