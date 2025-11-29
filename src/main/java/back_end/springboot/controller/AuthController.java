package back_end.springboot.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import back_end.springboot.dto.request.auth.DuplicateCheckIdRequestDto;
import back_end.springboot.dto.request.auth.OauthSignUpRequestDto;
import back_end.springboot.dto.request.auth.SignInRequestDto;
import back_end.springboot.dto.request.auth.SignUpRequestDto;
import back_end.springboot.dto.response.auth.DuplicateCheckIdResponseDto;
import back_end.springboot.dto.response.auth.OauthSignUpResponseDto;
import back_end.springboot.dto.response.auth.SignInResponseDto;
import back_end.springboot.dto.response.auth.SignUpResponseDto;
import back_end.springboot.service.AuthService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService userService;

    @Value("${google-oauth-client-id}")
    private String clientId;

    @Value("${google-oauth-client-secret}")
    private String clientSecret;

    @Value("${cors.front-end.url}")
    private String frontEndUrl;
    private final RestTemplate resetTemplate = new RestTemplate();

    // oauth
    @GetMapping("/google/login-url")
    public ResponseEntity<String> getGoogleLoginUrl() {
        String redirectUrl = frontEndUrl+"/oauth/google";
        String loginUrl = UriComponentsBuilder.fromHttpUrl("https://accounts.google.com/o/oauth2/v2/auth")
                            .queryParam("client_id", clientId)
                            .queryParam("redirect_uri", redirectUrl)
                            .queryParam("response_type", "code")
                            .queryParam("scope", "openid email profile")
                            .queryParam("access_type", "offline")
                            .queryParam("prompt", "consent")
                            .build().toUriString();

        return ResponseEntity.status(HttpStatus.OK).body(loginUrl);
    }

    @GetMapping("/google/callback")
    public ResponseEntity<? super SignInResponseDto> googleCallback(@RequestParam("code") String code) {
        String redirectUrl = frontEndUrl+"/oauth/google";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> tokenRequest = new LinkedMultiValueMap<>();
        tokenRequest.add("code", code);
        tokenRequest.add("client_id", clientId);
        tokenRequest.add("client_secret", clientSecret);
        tokenRequest.add("redirect_uri", redirectUrl);
        tokenRequest.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> tokenRequestEntity = new HttpEntity<>(tokenRequest, headers);
        ResponseEntity<Map> tokenResponse = resetTemplate.exchange( "https://oauth2.googleapis.com/token", HttpMethod.POST, tokenRequestEntity, Map.class);

        String accessToken = (String) tokenResponse.getBody().get("access_token");

        HttpHeaders userInfoHeaders = new HttpHeaders();
        userInfoHeaders.setBearerAuth(accessToken);
        HttpEntity<Void> userInfoEntity = new HttpEntity<>(userInfoHeaders);

        ResponseEntity<Map> userInfoResponse = resetTemplate.exchange( "https://www.googleapis.com/oauth2/v3/userinfo",
        HttpMethod.GET,
        userInfoEntity,
        Map.class);

        Map<String, Object> userInfo = userInfoResponse.getBody();
        return userService.oauth(userInfo);
    }

    // auth
    @PostMapping("/signUp")
    public ResponseEntity<? super SignUpResponseDto> singUp(@RequestBody SignUpRequestDto requestDto) {
        return userService.signUp(requestDto);
    }
    
    @PostMapping("/signIn")
    public ResponseEntity<? super SignInResponseDto> signIn(@RequestBody SignInRequestDto requestDto) {
        return userService.signIn(requestDto);
    }

    @PostMapping("/checkId")
    public ResponseEntity<? super DuplicateCheckIdResponseDto> checkId(@RequestBody DuplicateCheckIdRequestDto requestDto) {
        return userService.duplicateCheckId(requestDto);
    }

    @GetMapping("/send/email")
    public void sendEmail(@RequestParam("receive_email") String email) {
        userService.sendMail(email);
    }

    @PostMapping("/oauth-signUp")
    public ResponseEntity<? super OauthSignUpResponseDto> oauthSignUn(@RequestBody OauthSignUpRequestDto requestDto) {
        return userService.oauthSignUp(requestDto);
    }
}
