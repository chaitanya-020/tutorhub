package tutorhub.submission;

import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorhub.common.PageResponse;
import tutorhub.submission.dto.GradeRequest;
import tutorhub.submission.dto.SubmissionResponse;
import tutorhub.submission.dto.SubmitRequest;

import java.net.URI;

/**
 * Submissions for an assignment: /api/assignments/{assignmentId}/submissions.
 * Students submit (own work); staff grade. List is paged and role-scoped.
 */
@RestController
@RequestMapping("/api/assignments/{assignmentId}/submissions")
public class SubmissionController {

    private static final String READ =
            "hasAnyRole('DIRECTOR','COORDINATOR','TUTOR','ASSISTANT','STUDENT')";
    private static final String GRADE =
            "hasAnyRole('DIRECTOR','COORDINATOR','TUTOR','ASSISTANT')";

    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<SubmissionResponse> submit(@PathVariable Long assignmentId,
                                                     @Valid @RequestBody SubmitRequest request) {
        SubmissionResponse created = submissionService.submit(assignmentId, request);
        return ResponseEntity
                .created(URI.create("/api/assignments/" + assignmentId + "/submissions/" + created.id()))
                .body(created);
    }

    @GetMapping
    @PreAuthorize(READ)
    public PageResponse<SubmissionResponse> list(
            @PathVariable Long assignmentId,
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return PageResponse.from(submissionService.listForAssignment(assignmentId, pageable));
    }

    @GetMapping("/{submissionId}")
    @PreAuthorize(READ)
    public SubmissionResponse get(@PathVariable Long assignmentId, @PathVariable Long submissionId) {
        return submissionService.getById(assignmentId, submissionId);
    }

    @PostMapping("/{submissionId}/grade")
    @PreAuthorize(GRADE)
    public SubmissionResponse grade(@PathVariable Long assignmentId,
                                    @PathVariable Long submissionId,
                                    @Valid @RequestBody GradeRequest request) {
        return submissionService.grade(assignmentId, submissionId, request);
    }
}