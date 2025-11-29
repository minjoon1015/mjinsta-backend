package back_end.springboot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import back_end.springboot.dto.object.user.EditUserDto;
import back_end.springboot.dto.request.user.EditPasswordRequestDto;
import back_end.springboot.dto.request.user.FollowRequestDto;
import back_end.springboot.dto.request.user.UnFollowRequestDto;
import back_end.springboot.dto.response.user.EditInfoResponseDto;
import back_end.springboot.dto.response.user.EditPasswordResponseDto;
import back_end.springboot.dto.response.user.FollowResponseDto;
import back_end.springboot.dto.response.user.GetEditInfoResponseDto;
import back_end.springboot.dto.response.user.GetRecommendListResponseDto;
import back_end.springboot.dto.response.user.GetUserDetailsInfoResponseDto;
import back_end.springboot.dto.response.user.GetUserListForKeywordResponseDto;
import back_end.springboot.dto.response.user.UnFollowResponseDto;
import back_end.springboot.dto.response.user.UserMeResponseDto;
import back_end.springboot.service.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<? super UserMeResponseDto> me(@AuthenticationPrincipal String id) {
        return userService.me(id);
    }

    @PostMapping("/follow")
    public ResponseEntity<? super FollowResponseDto> follow(@RequestBody FollowRequestDto requestDto, @AuthenticationPrincipal String id) {
        return userService.follow(requestDto, id);
    }

    @PostMapping("/un_follow")
    public ResponseEntity<? super UnFollowResponseDto> unFollow(@RequestBody UnFollowRequestDto requestDto, @AuthenticationPrincipal String id) {
        return userService.unFollow(requestDto, id);
    }

    @GetMapping("/search")
    public ResponseEntity<? super GetUserListForKeywordResponseDto> getUserList(@AuthenticationPrincipal String id, @RequestParam("keyword") String keyword) {
        return userService.getUserList(id, keyword);
    }

    @GetMapping("/details/info")
    public ResponseEntity<? super GetUserDetailsInfoResponseDto> getUserDetailsInfo(@AuthenticationPrincipal String id, @RequestParam("searchId") String searchId) {
        return userService.getUserDetailsInfo(searchId, id);
    }

    @GetMapping("/get/recommend/list")
    public ResponseEntity<? super GetRecommendListResponseDto> getRecommendList(@AuthenticationPrincipal String id, @RequestParam(value = "isAll", required = false) Boolean isAll) {
        return userService.getRecommendList(id, isAll);
    }

    @GetMapping("/get/edit/info")
    public ResponseEntity<? super GetEditInfoResponseDto> getEditInfo(@AuthenticationPrincipal String id) {
        return userService.getEditInfo(id);
    }

    @PostMapping("/edit/info")
    public ResponseEntity<? super EditInfoResponseDto> editInfo(@AuthenticationPrincipal String id, @RequestBody EditUserDto requestDto) {
        return userService.editInfo(id, requestDto);
    }

    @PostMapping("/edit/password")
    public ResponseEntity<? super EditPasswordResponseDto> editPassword(@AuthenticationPrincipal String id, @RequestBody EditPasswordRequestDto requestDto) {
        return userService.editPassword(id, requestDto);
    } 
}