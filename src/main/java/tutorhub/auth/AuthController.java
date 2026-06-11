package tutorhub.auth;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tutorhub.auth.dto.CurrentUserResponse;
import tutorhub.auth.dto.LoginRequest;
import tutorhub.auth.dto.RegisterRequest;
import tutorhub.security.AppUserPrincipal;
import tutorhub.auth.dto.AuthResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    /**
     * Protected: only reachable with a valid token. @AuthenticationPrincipal
     * injects whoever the JWT filter authenticated for this request.
     */
    @GetMapping("/me")
    public CurrentUserResponse me(@AuthenticationPrincipal AppUserPrincipal principal) {
        return new CurrentUserResponse(principal.getId(), principal.getUsername());
    }
}