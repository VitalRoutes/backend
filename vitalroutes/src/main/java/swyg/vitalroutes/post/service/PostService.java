package swyg.vitalroutes.post.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import swyg.vitalroutes.Image.domain.Image;
import swyg.vitalroutes.Image.domain.Location;
import swyg.vitalroutes.common.file.FileUtils;
import swyg.vitalroutes.hashtag.domain.PostMappingTag;
import swyg.vitalroutes.hashtag.repository.HashTagRepository;
import swyg.vitalroutes.member.domain.Member;
import swyg.vitalroutes.member.repository.MemberRepository;
import swyg.vitalroutes.post.domain.Post;
import swyg.vitalroutes.post.dto.PostRequestDto;
import swyg.vitalroutes.post.repository.PostRepository;
import swyg.vitalroutes.s3.S3UploadService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final HashTagRepository hashTagRepository;
    private final S3UploadService s3UploadService;

    public Long registerPost(PostRequestDto postRequestDto) {
        Member member = memberRepository.findById(postRequestDto.getMemberId());
        // PostMappingTag Entity 생성
        List<PostMappingTag> tagList = new ArrayList<>();
        for(String tag : postRequestDto.getTags()) {
            tagList.add(PostMappingTag.createPostMapTag(hashTagRepository.findTag(tag)));
        }
        /**
         * Image Entity 생성
         * 1. S3UploadService 를 호출해서 파일을 저장하고 URL 을 반환받는다
         * 2. FileUtils 호출해서 위도, 경도를 가진 String[] 을 반환받는다
         * 3. String[] 으로 Location 을 생성한다
         * 4. Image 를 생성해서 Location 을 저장한다
         */
        MultipartFile titleImg = postRequestDto.getTitleImg();
        String titleImgUrl = "title image url";
        //String titleImgUrl = s3UploadService.saveFile(titleImg);

        List<Location> locations = new ArrayList<>();
        List<MultipartFile> files = postRequestDto.getFiles();
        int seq = 0;
        for (MultipartFile file : files) {
            String fileName = "file image url";
            // String fileName = s3UploadService.saveFile(file);
            String[] locationInfo = FileUtils.getLocationInfo(file);
            locations.add(Location.createLocation(++seq, fileName, locationInfo));
        }
        Image image = Image.crateImage(titleImgUrl, locations);
        
        // POST Entity 생성
        Post post = Post.createPost(postRequestDto, member, tagList, image);
        return postRepository.registerPost(post);
    }

    @Transactional(readOnly = true)
    public Post findPost(Long postId) {
        return postRepository.findById(postId);
    }

    @Transactional(readOnly = true)
    public List<Post> findAllPost() {
        return postRepository.findAll();
    }
}
