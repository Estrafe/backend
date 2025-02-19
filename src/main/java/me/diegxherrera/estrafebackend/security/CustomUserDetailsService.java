package me.diegxherrera.estrafebackend.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    // Mock user list (replace with database retrieval logic)
    private final List<UserDetails> users = List.of(
            User.withUsername("admin")
                    .password("{noop}password") // {noop} means no password encoding
                    .roles("ADMIN")
                    .build(),
            User.withUsername("user")
                    .password("{noop}password")
                    .roles("USER")
                    .build()
    );

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return users.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}