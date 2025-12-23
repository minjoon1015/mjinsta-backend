package back_end.springboot.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import back_end.springboot.dto.request.post.PostAddViewHistoryRequestDto;
import back_end.springboot.dto.request.post.PostCommentRequestDto;
import back_end.springboot.dto.response.post.CommentPaginationListResponseDto;
import back_end.springboot.dto.response.post.CommentTopListResponseDto;
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
    public ResponseEntity<? super PostCreateResponseDto> createPost(
            @RequestPart("images") List<MultipartFile> images,
            @RequestPart("data") String data,
            @AuthenticationPrincipal String id) {

        return postService.createPost(id, images, data);
    }

    @GetMapping("/get/list")
    public ResponseEntity<? super PostGetMeResponseDto> getList(
            @AuthenticationPrincipal String id,
            @RequestParam(required = false, value = "postId") Integer postId,
            @RequestParam(required = false, value = "userId") String userId) {

        return postService.getList(userId == null || (userId.equals("")) ? id : userId, postId);
    }

    @GetMapping("/get/details_info")
    public ResponseEntity<? super PostGetDetailsInfoResponseDto> getDetailsInfo(
            @RequestParam("postId") Integer postId,
            @AuthenticationPrincipal String id) {

        return postService.getDetailsInfo(postId, id);
    }

    @PostMapping("/like")
    public ResponseEntity<? super PostLikeResponseDto> like(
            @AuthenticationPrincipal String id,
            @RequestParam("postId") Integer postId) {

        return postService.like(id, postId);
    }

    @PostMapping("/comment")
    public ResponseEntity<? super PostCommentResponseDto> comment(
            @AuthenticationPrincipal String id,
            @RequestBody PostCommentRequestDto requestDto) {

        requestDto.setUserId(id);
        return postService.comment(requestDto);
    }

    @GetMapping("/comment/top-list")
    public ResponseEntity<? super CommentTopListResponseDto> getTopCommentList(@AuthenticationPrincipal String id, @RequestParam("postId") Integer postId) {
        return postService.getCommentTopList(id, postId);
    }

    @GetMapping("/comment/pagination-list")
    public ResponseEntity<? super CommentPaginationListResponseDto> getCommentList(@AuthenticationPrincipal String id, @RequestParam("postId") Integer postId,
            @RequestParam(value = "commentId", required = false) Integer commentId) {
        return postService.getCommentPaginationList(id, postId, commentId);
    }

    @PostMapping("/view_history")
    public void addViewHistory(
            @AuthenticationPrincipal String id, @RequestBody PostAddViewHistoryRequestDto requestDto) {
                postService.addViewHistory(id, requestDto);
    }
}