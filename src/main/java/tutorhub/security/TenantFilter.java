package tutorhub.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tutorhub.membership.Membership;
import tutorhub.membership.MembershipRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads the X-Academy-Id header. If the authenticated user is a member of that
 * academy, it (1) puts (academyId, role) in TenantContext for query scoping and
 * (2) adds a ROLE_<role> authority so @PreAuthorize role checks work for THIS
 * request only. If they are NOT a member, nothing is added -> they have no
 * academy role -> protected endpoints reject them. Runs after the JWT filter.
 */
@Component
public class TenantFilter extends OncePerRequestFilter {

    public static final String ACADEMY_HEADER = "X-Academy-Id";

    private final MembershipRepository membershipRepository;

    public TenantFilter(MembershipRepository membershipRepository) {
        this.membershipRepository = membershipRepository;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain)
            throws ServletException, IOException {
        try {
            resolveTenant(request);
            chain.doFilter(request, response);
        } finally {
            TenantContext.clear(); // never leak context to the next request on this thread
        }
    }

    private void resolveTenant(HttpServletRequest request) {
        String header = request.getHeader(ACADEMY_HEADER);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (header == null || header.isBlank()
                || auth == null || !(auth.getPrincipal() instanceof AppUserPrincipal principal)) {
            return;
        }

        long academyId;
        try {
            academyId = Long.parseLong(header.trim());
        } catch (NumberFormatException e) {
            return; // malformed header -> no tenant context
        }

        membershipRepository.findByUserIdAndAcademyId(principal.getId(), academyId)
                .ifPresent(membership -> applyTenant(auth, principal, academyId, membership));
    }

    private void applyTenant(Authentication auth, AppUserPrincipal principal,
                             long academyId, Membership membership) {
        TenantContext.set(academyId, membership.getRole());

        List<GrantedAuthority> authorities = new ArrayList<>(auth.getAuthorities());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + membership.getRole().name()));

        UsernamePasswordAuthenticationToken newAuth =
                new UsernamePasswordAuthenticationToken(principal, null, authorities);
        newAuth.setDetails(auth.getDetails());
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }
}