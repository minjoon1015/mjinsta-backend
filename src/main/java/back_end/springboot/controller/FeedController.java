package back_end.springboot.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import back_end.springboot.service.FeedService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/feed")
@RequiredArgsConstructor
public class FeedController {
    private final FeedService feedService;
    
    @GetMapping
    public void getFeed(@AuthenticationPrincipal String id, 
        @RequestParam(value = "pages", required = false) Integer pages, @RequestParam(value = "postId", required = false) Integer postId, @RequestParam(value = "favoriteCount", required = false) Integer favoriteCount) {
        feedService.getFeed(id, pages, postId, favoriteCount);
    }
}
