package back_end.springboot.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import back_end.springboot.dto.request.post.PostCommentRequestDto;
import back_end.springboot.dto.response.post.PostCommentResponseDto;
import back_end.springboot.dto.response.post.PostCreateResponseDto;
import back_end.springboot.dto.response.post.PostGetDetailsInfoResponseDto;
import back_end.springboot.dto.response.post.PostGetMeResponseDto;
import back_end.springboot.dto.response.post.PostLikeResponseDto;

public interface PostService {
    ResponseEntity<? super PostCreateResponseDto> createPost(String userId, List<MultipartFile> images, String data);
    ResponseEntity<? super PostGetMeResponseDto> getList(String userId, Integer postId);
    ResponseEntity<? super PostGetDetailsInfoResponseDto> getDetailsInfo(Integer postId, String userId);
    ResponseEntity<? super PostLikeResponseDto> like(String userId, Integer postId);
    ResponseEntity<? super PostCommentResponseDto> comment(PostCommentRequestDto requestDto);
}
