package swyg.vitalroutes.post.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import swyg.vitalroutes.common.dto.ApiResponseDto;
import swyg.vitalroutes.common.exception.FileNotEnoughException;
import swyg.vitalroutes.common.exception.InputViolateException;
import swyg.vitalroutes.post.domain.Post;
import swyg.vitalroutes.post.dto.PostRequestDto;
import swyg.vitalroutes.post.dto.PostResponseDto;
import swyg.vitalroutes.post.service.PostService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping("/post/{postId}")
    public ApiResponseDto<?> findPost(@PathVariable Long postId) {
        Post post = postService.findPost(postId);
        PostResponseDto postResponseDto = new PostResponseDto(post);
        return new ApiResponseDto<>(postResponseDto);
    }

    @GetMapping("/post")
    public ApiResponseDto<?> findAllPost() {
        List<PostResponseDto> list = postService.findAllPost().stream().map(PostResponseDto::new).toList();
        return new ApiResponseDto<>(list);
    }

    @PostMapping("/post/add")
    public ApiResponseDto<?> registerPost(@Valid PostRequestDto postRequestDto, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new InputViolateException(bindingResult.getFieldError().getDefaultMessage());
        }

        log.info("postRequestDto = {}", postRequestDto);

        MultipartFile titleImg = postRequestDto.getTitleImg();
        log.info("titleImg = {}", titleImg);

        List<MultipartFile> files = postRequestDto.getFiles();
        if (files.size() < 2) {
            throw new FileNotEnoughException("파일은 두 개 이상 등록해야 합니다");
        }
        log.info("files = {}", files);

        Long registeredId = postService.registerPost(postRequestDto);

        return new ApiResponseDto<>(registeredId);
    }
}
