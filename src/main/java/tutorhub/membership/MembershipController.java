package tutorhub.membership;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorhub.membership.dto.AddMemberRequest;
import tutorhub.membership.dto.MemberResponse;

import java.net.URI;
import java.util.List;

/**
 * Members of the ACTIVE academy (chosen by the X-Academy-Id header). Managing
 * members is limited to DIRECTOR and COORDINATOR. @PreAuthorize checks the
 * per-academy role that TenantFilter stamped onto this request.
 */
@RestController
@RequestMapping("/api/members")
public class MembershipController {

    private final MembershipService membershipService;

    public MembershipController(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('DIRECTOR','COORDINATOR')")
    public ResponseEntity<MemberResponse> addMember(@Valid @RequestBody AddMemberRequest request) {
        MemberResponse created = membershipService.addMember(request);
        return ResponseEntity
                .created(URI.create("/api/members/" + created.membershipId()))
                .body(created);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('DIRECTOR','COORDINATOR')")
    public List<MemberResponse> listMembers() {
        return membershipService.listMembers();
    }
}