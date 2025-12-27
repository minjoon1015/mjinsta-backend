package back_end.springboot.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import back_end.springboot.provider.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        try {
            String token = parseJwt(request);
            
            // 1단계: 헤더에서 토큰 추출 확인
            if (token != null) {
                System.out.println("[JWT Filter] 추출된 토큰 발견: " + token.substring(0, Math.min(token.length(), 15)) + "...");
                
                // 2단계: 토큰 유효성 검증 확인
                if (jwtProvider.validateToken(token)) {
                    String userId = jwtProvider.getUserIdFromToken(token);
                    String role = jwtProvider.getRoleFromToken(token);
                    
                    System.out.println("[JWT Filter] 검증 성공! UserId: " + userId + ", Role: " + role);

                    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    authorities.add(new SimpleGrantedAuthority(role));

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userId,
                        null,  
                        authorities 
                    );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // 3단계: SecurityContext에 등록 확인
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("[JWT Filter] SecurityContext에 인증 객체 등록 완료");
                } else {
                    System.out.println("[JWT Filter] 토큰 검증 실패 (validateToken == false)");
                }
            } else {
                // 토큰이 아예 없는 경우 (Authorization 헤더 문제)
                String rawHeader = request.getHeader("Authorization");
                System.out.println("[JWT Filter] 토큰이 없음. 원본 헤더: " + rawHeader);
            }
        } catch (Exception e) {
            System.err.println("[JWT Filter] 필터 처리 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        
        // 대소문자 문제 대응을 위해 로그 출력
        if (header == null) {
            header = request.getHeader("authorization");
        }

        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7).trim(); // 공백 제거 추가
        }
        return null;
    }
}