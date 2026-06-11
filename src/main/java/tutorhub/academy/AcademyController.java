package tutorhub.academy;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tutorhub.academy.dto.AcademyResponse;
import tutorhub.academy.dto.CreateAcademyRequest;
import tutorhub.academy.dto.UpdateAcademyRequest;
import tutorhub.security.AppUserPrincipal;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/academies")
public class AcademyController {

    private final AcademyService academyService;

    public AcademyController(AcademyService academyService) {
        this.academyService = academyService;
    }

    @PostMapping
    public ResponseEntity<AcademyResponse> create(@Valid @RequestBody CreateAcademyRequest request,
                                                  @AuthenticationPrincipal AppUserPrincipal principal) {
        AcademyResponse created = academyService.create(request, principal.getId());
        return ResponseEntity.created(URI.create("/api/academies/" + created.id())).body(created);
    }

    @GetMapping
    public List<AcademyResponse> list(@AuthenticationPrincipal AppUserPrincipal principal) {
        return academyService.listForUser(principal.getId());
    }

    @GetMapping("/{id}")
    public AcademyResponse get(@PathVariable Long id,
                               @AuthenticationPrincipal AppUserPrincipal principal) {
        return academyService.getById(id, principal.getId());
    }

    @PutMapping("/{id}")
    public AcademyResponse update(@PathVariable Long id,
                                  @Valid @RequestBody UpdateAcademyRequest request,
                                  @AuthenticationPrincipal AppUserPrincipal principal) {
        return academyService.update(id, request, principal.getId());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id,
                       @AuthenticationPrincipal AppUserPrincipal principal) {
        academyService.delete(id, principal.getId());
    }
}