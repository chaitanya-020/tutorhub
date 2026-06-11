package tutorhub.enrollment;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tutorhub.enrollment.dto.EnrollRequest;
import tutorhub.enrollment.dto.EnrollmentResponse;

import java.net.URI;
import java.util.List;

/**
 * Enrollments are a sub-resource of a course: /api/courses/{courseId}/enrollments.
 * The academy still comes from the X-Academy-Id header; the service verifies the
 * course belongs to it. Managing enrollments is staff-only.
 */
@RestController
@RequestMapping("/api/courses/{courseId}/enrollments")
public class EnrollmentController {

    private static final String MANAGE = "hasAnyRole('DIRECTOR','COORDINATOR','TUTOR')";
    private static final String VIEW = "hasAnyRole('DIRECTOR','COORDINATOR','TUTOR','ASSISTANT')";

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    @PreAuthorize(MANAGE)
    public ResponseEntity<EnrollmentResponse> enroll(@PathVariable Long courseId,
                                                     @Valid @RequestBody EnrollRequest request) {
        EnrollmentResponse created = enrollmentService.enroll(courseId, request);
        return ResponseEntity
                .created(URI.create("/api/courses/" + courseId + "/enrollments/" + created.enrollmentId()))
                .body(created);
    }

    @GetMapping
    @PreAuthorize(VIEW)
    public List<EnrollmentResponse> list(@PathVariable Long courseId) {
        return enrollmentService.listForCourse(courseId);
    }

    @DeleteMapping("/{enrollmentId}")
    @PreAuthorize(MANAGE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unenroll(@PathVariable Long courseId, @PathVariable Long enrollmentId) {
        enrollmentService.unenroll(courseId, enrollmentId);
    }
}