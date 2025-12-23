package back_end.springboot.dto.response.feed;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import back_end.springboot.common.ResponseCode;
import back_end.springboot.dto.object.post.PostDto;
import back_end.springboot.dto.response.ResponseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetFeedResponseDto extends ResponseDto {
    private List<PostDto> feed;
    private Integer pointer;
    private Integer lastPostId;
    private Integer lastFavoriteCount;

    public GetFeedResponseDto(ResponseCode code, List<PostDto> feed, Integer pointer) {
        super(code);
        this.feed = feed;
        this.pointer = pointer;
    }

    public GetFeedResponseDto(ResponseCode code, List<PostDto> feed, Integer lastPostId, Integer lastFavoriteCount) {
        super(code);
        this.feed = feed;
        this.lastPostId = lastPostId;
        this.lastFavoriteCount = lastFavoriteCount;
    }
    
    public static ResponseEntity<GetFeedResponseDto> successForCachingFeed(List<PostDto> feed, Integer pointer) {
        return ResponseEntity.status(HttpStatus.OK).body(new GetFeedResponseDto(ResponseCode.SC, feed, pointer));
    }

    public static ResponseEntity<GetFeedResponseDto> successForSearchFeed(List<PostDto> feed) {
        Integer lastPostId = null;
        Integer lastFavoriteCount = null;
        if (feed != null && !feed.isEmpty()) {
            PostDto lastPost = feed.get(feed.size()-1);
            lastPostId = lastPost.getPostId();
            lastFavoriteCount = lastPost.getFavoriteCount();
        }
        return ResponseEntity.status(HttpStatus.OK).body(new GetFeedResponseDto(ResponseCode.SC, feed, lastPostId, lastFavoriteCount));
    }
}
