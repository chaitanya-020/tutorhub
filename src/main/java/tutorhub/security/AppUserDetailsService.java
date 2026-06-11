package tutorhub.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tutorhub.user.User;
import tutorhub.user.UserRepository;

/**
 * Spring Security calls this to look up a user during login and when validating
 * a token. Our "username" is the email. Returns an AppUserPrincipal.
 */
@Service
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public AppUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No user with email " + email));
        return new AppUserPrincipal(user.getId(), user.getEmail(), user.getPasswordHash());
    }
}