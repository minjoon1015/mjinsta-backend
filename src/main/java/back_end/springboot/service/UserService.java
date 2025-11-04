package back_end.springboot.service;

import org.springframework.http.ResponseEntity;

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
import back_end.springboot.dto.response.user.UpdateProfileUrlResponseDto;
import back_end.springboot.dto.response.user.UserMeResponseDto;

public interface UserService {
    public ResponseEntity<? super UserMeResponseDto> me(String id);
    public ResponseEntity<? super FollowResponseDto> follow(FollowRequestDto requestDto, String id);
    public ResponseEntity<? super UnFollowResponseDto> unFollow(UnFollowRequestDto requestDto, String id);
    public ResponseEntity<? super UpdateProfileUrlResponseDto> updateProfileImage(String id, String url);
    public String existProfileImage(String id);
    public ResponseEntity<? super GetUserListForKeywordResponseDto> getUserList(String id, String keyword);
    public ResponseEntity<? super GetUserDetailsInfoResponseDto> getUserDetailsInfo(String searchId, String userId);
    public ResponseEntity<? super GetRecommendListResponseDto> getRecommendList(String id, Boolean isAll);
    public ResponseEntity<? super GetEditInfoResponseDto> getEditInfo(String id);
    public ResponseEntity<? super EditInfoResponseDto> editInfo(String id, EditUserDto requestDto);
    public ResponseEntity<? super EditPasswordResponseDto> editPassword(String id, EditPasswordRequestDto requestDto);
}
