package back_end.springboot.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import back_end.springboot.dto.request.auth.DuplicateCheckIdRequestDto;
import back_end.springboot.dto.request.auth.SignInRequestDto;
import back_end.springboot.dto.request.auth.SignUpRequestDto;
import back_end.springboot.dto.response.auth.DuplicateCheckIdResponseDto;
import back_end.springboot.dto.response.auth.SignInResponseDto;
import back_end.springboot.dto.response.auth.SignUpResponseDto;

public interface AuthService {
    ResponseEntity<? super SignInResponseDto> signIn(SignInRequestDto requestDto);
    ResponseEntity<? super SignUpResponseDto> signUp(SignUpRequestDto requestDto);
    ResponseEntity<? super SignInResponseDto> oauth(Map<String, Object> userInfo);
    ResponseEntity<? super DuplicateCheckIdResponseDto> duplicateCheckId(DuplicateCheckIdRequestDto requestDto);
    void sendMail(String email);
}
