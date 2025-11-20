package back_end.springboot.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import back_end.springboot.dto.request.post.PostCommentRequestDto;
import back_end.springboot.dto.response.post.PostCommentResponseDto;
import back_end.springboot.dto.response.post.PostCreateResponseDto;
import back_end.springboot.dto.response.post.PostGetDetailsInfoResponseDto;
import back_end.springboot.dto.response.post.PostGetMeResponseDto;
import back_end.springboot.dto.response.post.PostLikeResponseDto;
import back_end.springboot.service.PostService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping("/create")
    public ResponseEntity<? super PostCreateResponseDto> createPost(@RequestPart("images") List<MultipartFile> images, @RequestPart("data") String data, @AuthenticationPrincipal UserDetails userDetails) {        
        return postService.createPost(userDetails.getUsername(), images, data);
    }

    @GetMapping("/get/list")
    public ResponseEntity<? super PostGetMeResponseDto> getList(@AuthenticationPrincipal UserDetails userDetails, 
        @RequestParam(required = false, value = "postId") Integer postId, @RequestParam(required = false, value = "userId") String userId) {
        return postService.getList(userId == null || (userId.equals("")) ? userDetails.getUsername() : userId, postId);
    }

    @GetMapping("/get/details_info")
    public ResponseEntity<? super PostGetDetailsInfoResponseDto> getDetailsInfo(@RequestParam("postId") Integer postId, @AuthenticationPrincipal UserDetails userDetails) {
        return postService.getDetailsInfo(postId, userDetails.getUsername());
    }

    @PostMapping("/like")
    public ResponseEntity<? super PostLikeResponseDto> like(@AuthenticationPrincipal UserDetails userDetails, @RequestParam("postId") Integer postId) {
        return postService.like(userDetails.getUsername(), postId);
    }

    @PostMapping("/comment")
    public ResponseEntity<? super PostCommentResponseDto> comment(@AuthenticationPrincipal UserDetails userDetails, @RequestBody PostCommentRequestDto requestDto) {
        requestDto.setUserId(userDetails.getUsername());
        return postService.comment(requestDto);
    }
}
