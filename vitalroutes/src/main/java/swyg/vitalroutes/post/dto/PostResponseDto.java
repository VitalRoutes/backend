package swyg.vitalroutes.post.dto;

import lombok.Data;
import swyg.vitalroutes.Image.domain.Location;
import swyg.vitalroutes.post.domain.ChallengeType;
import swyg.vitalroutes.post.domain.Post;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class PostResponseDto {
    private Long postId;
    private String memberName;
    private String title;
    private String content;
    private ChallengeType type;
    private long viewCnt;
    private List<Map<String, String>> tagList = new ArrayList<>();
    private String titleImg;
    private List<Map<String, String>> images = new ArrayList<>();

    public PostResponseDto(Post post) {
        postId = post.getPostId();
        title = post.getTitle();
        content = post.getContent();
        type = post.getType();
        viewCnt = post.getViewCnt();

        memberName = post.getMember().getName();
        post.getTagList().forEach(tag -> {
            this.tagList.add(Map.of(tag.getHashTag().getTagId(), tag.getHashTag().getTagName()));
        });

        titleImg = post.getImage().getTitleImg();
        for (Location location : post.getImage().getLocations()) {
            images.add(createLocationMap(location));
        }
    }

    public Map<String, String> createLocationMap(Location location) {
        Map<String, String> map = new HashMap<>();
        if (location != null) {
            map.put("sequence", String.valueOf(location.getSequence()));
            map.put("imageUrl", location.getFileName());
            map.put("latitude", location.getLatitude());
            map.put("longitude", location.getLongitude());
        }
        return map;
    }
}
