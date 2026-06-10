package tutorhub.academy;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import java.net.URI;
import java.util.List;

/**
 * HTTP entry points for academies. The controller stays thin: validate input,
 * delegate to the service, return the right status code.
 */
@RestController
@RequestMapping("/api/academies")
public class AcademyController {

    private final AcademyService academyService;

    public AcademyController(AcademyService academyService) {
        this.academyService = academyService;
    }

    @PostMapping
    public ResponseEntity<AcademyResponse> create(@Valid @RequestBody CreateAcademyRequest request) {
        AcademyResponse created = academyService.create(request);
        return ResponseEntity
                .created(URI.create("/api/academies/" + created.id()))
                .body(created);
    }

    @GetMapping("/{id}")
    public AcademyResponse get(@PathVariable Long id) {
        return academyService.getById(id);
    }

    @GetMapping
    public List<AcademyResponse> list() {
        return academyService.list();
    }

    @PutMapping("/{id}")
    public AcademyResponse update(@PathVariable Long id,
                                  @Valid @RequestBody UpdateAcademyRequest request) {
        return academyService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        academyService.delete(id);
    }
}