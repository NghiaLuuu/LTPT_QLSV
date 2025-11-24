package iuh.fit.se.service.impl;

import iuh.fit.se.model.User;
import iuh.fit.se.repository.UserRepository;
import iuh.fit.se.util.LocalCacheClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocalCacheClient localCacheClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Use local cache to avoid DB hits
        User user = localCacheClient.getOrLoad("user:" + username, () ->
                userRepository.findByUsername(username).orElse(null)
        );

        if (user == null) {
            throw new UsernameNotFoundException("User không tồn tại: " + username);
        }

        Collection<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(!user.getActive())
                .credentialsExpired(false)
                .disabled(!user.getActive())
                .build();
    }
}
