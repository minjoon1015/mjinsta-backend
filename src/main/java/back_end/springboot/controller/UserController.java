package back_end.springboot.controller;

import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    public ResponseEntity<? super UserMeResponseDto> me(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.me(userDetails.getUsername());
    }

    @PostMapping("/follow")
    public ResponseEntity<? super FollowResponseDto> follow(@RequestBody FollowRequestDto requestDto, @AuthenticationPrincipal UserDetails userDetails) {
        return userService.follow(requestDto, userDetails.getUsername());
    }

    @PostMapping("/un_follow")
    public ResponseEntity<? super UnFollowResponseDto> unFollow(@RequestBody UnFollowRequestDto requestDto, @AuthenticationPrincipal UserDetails userDetails) {
        return userService.unFollow(requestDto, userDetails.getUsername());
    }

    @GetMapping("/search")
    public ResponseEntity<? super GetUserListForKeywordResponseDto> getUserList(@AuthenticationPrincipal UserDetails userDetails, @RequestParam("keyword") String keyword) {
        return userService.getUserList(userDetails.getUsername(), keyword);
    }

    @GetMapping("/details/info")
    public ResponseEntity<? super GetUserDetailsInfoResponseDto> getUserDetailsInfo(@AuthenticationPrincipal UserDetails userDetails, @RequestParam("searchId") String searchId) {
        return userService.getUserDetailsInfo(searchId, userDetails.getUsername());
    }

    @GetMapping("/get/recommend/list")
    public ResponseEntity<? super GetRecommendListResponseDto> getRecommendList(@AuthenticationPrincipal UserDetails userDetails, @RequestParam(value = "isAll", required = false) Boolean isAll) {
        return userService.getRecommendList(userDetails.getUsername(), isAll);
    }

    @GetMapping("/get/edit/info")
    public ResponseEntity<? super GetEditInfoResponseDto> getEditInfo(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.getEditInfo(userDetails.getUsername());
    }

    @PostMapping("/edit/info")
    public ResponseEntity<? super EditInfoResponseDto> editInfo(@AuthenticationPrincipal UserDetails userDetails, @RequestBody EditUserDto requestDto) {
        return userService.editInfo(userDetails.getUsername(), requestDto);
    }

    @PostMapping("/edit/password")
    public ResponseEntity<? super EditPasswordResponseDto> editPassword(@AuthenticationPrincipal UserDetails userDetails, @RequestBody EditPasswordRequestDto requestDto) {
        return userService.editPassword(userDetails.getUsername(), requestDto);
    }   
}
