package back_end.springboot.service;

import org.springframework.http.ResponseEntity;

import back_end.springboot.dto.response.feed.GetFeedResponseDto;

public interface FeedService {
    ResponseEntity<? super GetFeedResponseDto> getFeed(String userId, Integer pages, Integer postId, Integer favoriteCount);
}
