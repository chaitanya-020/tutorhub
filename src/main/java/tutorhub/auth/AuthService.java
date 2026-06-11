package tutorhub.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tutorhub.auth.dto.LoginRequest;
import tutorhub.auth.dto.RegisterRequest;
import tutorhub.security.JwtService;
import tutorhub.user.User;
import tutorhub.user.UserRepository;
import tutorhub.auth.dto.AuthResponse;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyUsedException(request.email());
        }
        User user = new User();
        user.setEmail(request.email());
        // NEVER store the raw password - BCrypt hashes it (with a built-in salt).
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setDisplayName(request.displayName());
        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail());
        return AuthResponse.bearer(token, user.getEmail(), user.getDisplayName());
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        // Throws BadCredentialsException (-> 401) if the email/password are wrong.
        // The same error is returned for "no such email" to avoid leaking which
        // emails exist.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
        String token = jwtService.generateToken(user.getEmail());
        return AuthResponse.bearer(token, user.getEmail(), user.getDisplayName());
    }
}