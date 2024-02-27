package swyg.vitalroutes.post.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import swyg.vitalroutes.Image.domain.Image;
import swyg.vitalroutes.hashtag.domain.PostMappingTag;
import swyg.vitalroutes.member.domain.Member;
import swyg.vitalroutes.post.dto.PostRequestDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    private String title;
    @Column(columnDefinition = "TEXT")
    private String content;
    @Enumerated(EnumType.STRING)
    private ChallengeType type;

    private LocalDateTime time;
    private long viewCnt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostMappingTag> tagList = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id")
    private Image image;

    // PostMappingTag 에 POST 참조를 넣어줌
    private void setPostInPostMappingTag(PostMappingTag postMappingTag) {
        postMappingTag.setPost(this);
    }

    private void setPostInImage(Image image) {
        image.setPost(this);
    }

    public static Post createPost(PostRequestDto postRequestDto, Member member, List<PostMappingTag> postMappingTags, Image image) {
        Post post = new Post();
        post.setTitle(postRequestDto.getTitle());
        post.setContent(postRequestDto.getContent());
        post.setType(ChallengeType.valueOf(postRequestDto.getType()));
        post.setTime(LocalDateTime.now());
        post.setViewCnt(0L);
        // 연관관계 세팅
        post.setMember(member);

        post.setImage(image);
        post.setPostInImage(image);

        for (PostMappingTag mappingTag : postMappingTags) {
            post.setPostInPostMappingTag(mappingTag);
            post.getTagList().add(mappingTag);
        }
        return post;
    }
}
