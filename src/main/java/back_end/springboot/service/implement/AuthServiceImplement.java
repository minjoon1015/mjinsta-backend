package back_end.springboot.service.implement;

import java.util.Map;
import java.util.Random;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import back_end.springboot.common.UserType;
import back_end.springboot.component.MailManager;
import back_end.springboot.dto.request.auth.DuplicateCheckIdRequestDto;
import back_end.springboot.dto.request.auth.OauthSignUpRequestDto;
import back_end.springboot.dto.request.auth.SignInRequestDto;
import back_end.springboot.dto.request.auth.SignUpRequestDto;
import back_end.springboot.dto.response.ResponseDto;
import back_end.springboot.dto.response.auth.DuplicateCheckIdResponseDto;
import back_end.springboot.dto.response.auth.OauthSignUpResponseDto;
import back_end.springboot.dto.response.auth.SignInResponseDto;
import back_end.springboot.dto.response.auth.SignUpResponseDto;
import back_end.springboot.entity.UserEntity;
import back_end.springboot.provider.JwtProvider;
import back_end.springboot.repository.UserRepository;
import back_end.springboot.service.AuthCodeService;
import back_end.springboot.service.AuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImplement implements AuthService {
    private final AuthCodeService authCodeService;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final MailManager mailManager;
    private final UserDetailsService userDetailsService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public ResponseEntity<? super SignInResponseDto> signIn(SignInRequestDto requestDto) {
        try {
            if (requestDto == null)
                return ResponseDto.badRequest();
            UserEntity userEntity = userRepository.findById(requestDto.getId()).orElse(null);
            if (userEntity == null)
                return SignInResponseDto.notExistedId();
            if (!passwordEncoder.matches(requestDto.getPassword(), userEntity.getPassword()))
                return SignInResponseDto.notExistedPassword();

            String token = jwtProvider.generateToken(userEntity.getId());
            return SignInResponseDto.success(token);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
    }

    @Override
    @Transactional
    public ResponseEntity<? super SignUpResponseDto> signUp(SignUpRequestDto requestDto) {
        try {
            if (requestDto == null)
                return ResponseDto.badRequest();
            if (!requestDto.isDuplicateCheckId())
                return SignUpResponseDto.duplicateIdNotCheck();

            boolean existedEmail = userRepository.existsByEmail(requestDto.getEmail());
            if (existedEmail)
                return SignUpResponseDto.duplicateEmail();

            String email = requestDto.getEmail();
            String email_code = requestDto.getEmail_code();
            boolean IsVerify = authCodeService.verifyAuthCode(email, email_code);
            if (!IsVerify)
                return SignUpResponseDto.notVerifyCode();

            UserEntity userEntity = new UserEntity(requestDto.getId(), passwordEncoder.encode(requestDto.getPassword()),
                    requestDto.getName(), requestDto.getSex(), "", requestDto.getEmail(), requestDto.getAddress(),
                    requestDto.getAddressDetail(), "", UserType.LOCAL, null);
            userRepository.save(userEntity);
            return SignUpResponseDto.success();
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseDto.badRequest();
        }
    }

    @Override
    @Transactional
    public ResponseEntity<? super SignInResponseDto> oauth(Map<String, Object> userInfo) {
        try {
            if (userInfo == null)
                SignInResponseDto.badRequest();
            String id = (String) userInfo.get("sub");
            String name = (String) userInfo.get("name");
            String profileImage = (String) userInfo.get("picture");
            String email = (String) userInfo.get("email");

            boolean existedEmail = userRepository.existsByEmail(email);
            if (existedEmail) {
                UserEntity userEntity = userRepository.findByEmail(email);
                String token = jwtProvider.generateToken(userEntity.getId());
                return SignInResponseDto.success(token);
            }

            UserEntity saved = userRepository.findById(id).orElse(null);    
            if (saved == null) {
                ValueOperations<String, Object> ops = redisTemplate.opsForValue();
                UserEntity userEntity = objectMapper.convertValue(ops.get(id), new TypeReference<UserEntity>() {
                });
                if (userEntity == null) {
                    userEntity = new UserEntity(null, null, name, null, null, email, null, null, null,
                        UserType.SOCIAL, id);
                    ops.set(id, userEntity);
                }
                return SignInResponseDto.newOauthSignIn(id);
            } else {
                UserDetails userDetails = userDetailsService.loadUserByUsername(saved.getId());
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);

                String token = jwtProvider.generateToken(id);
                return SignInResponseDto.success(token);
            }
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseDto.databaseError();
        }
    }

    @Override
    public ResponseEntity<? super DuplicateCheckIdResponseDto> duplicateCheckId(DuplicateCheckIdRequestDto requestDto) {
        try {
            if (requestDto == null)
                return ResponseDto.badRequest();
            boolean exist = userRepository.existsById(requestDto.getId());
            if (exist)
                return DuplicateCheckIdResponseDto.faild();
            return DuplicateCheckIdResponseDto.success();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
    }

    @Override
    public void sendMail(String email) {
        try {
            Random random = new Random();
            int number = 10000 + random.nextInt(90000);
            mailManager.send(email, "인증번호", String.valueOf(number));
            authCodeService.saveAuthCode(email, String.valueOf(number));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @Transactional
    public ResponseEntity<? super OauthSignUpResponseDto> oauthSignUp(OauthSignUpRequestDto requestDto) {
        try {
            ValueOperations<String, Object> ops = redisTemplate.opsForValue();
            Object ob = ops.get(requestDto.getSocialId());
            if (ob == null) return ResponseDto.badRequest();
            UserEntity userEntity = objectMapper.convertValue(ob, new TypeReference<UserEntity>() {});
            userEntity.setId(requestDto.getId());
            userEntity.setSex(requestDto.getSex());
            userEntity.setAddress(requestDto.getAddress());
            userEntity.setAddressDetail(requestDto.getAddress_detail());
            userRepository.save(userEntity);
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEntity.getId());
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            return OauthSignUpResponseDto.success(jwtProvider.generateToken(userEntity.getId()));
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseDto.databaseError();
        }
    }

}
