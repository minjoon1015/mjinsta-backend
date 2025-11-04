package back_end.springboot.userDetails;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import back_end.springboot.common.Role;
import back_end.springboot.entity.UserEntity;
import back_end.springboot.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(()-> new UsernameNotFoundException("User not found"));
        String role = userEntity.getRole() == Role.ADMIN ? "ROLE_ADMIN" : "ROLE_USER";
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(role);
        Collection<SimpleGrantedAuthority> collection = new ArrayList<>();
        collection.add(simpleGrantedAuthority);
        return new CustomUserDetails(
            userEntity.getId(),
            collection
        );
    }
    
}
