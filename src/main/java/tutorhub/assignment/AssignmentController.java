package tutorhub.assignment;

import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tutorhub.assignment.dto.AssignmentResponse;
import tutorhub.assignment.dto.CreateAssignmentRequest;
import tutorhub.assignment.dto.UpdateAssignmentRequest;
import tutorhub.common.PageResponse;

import java.net.URI;

/**
 * Assignments of a course: /api/courses/{courseId}/assignments.
 * The list endpoint is paged: ?page=0&size=20&sort=dueDate,desc
 */
@RestController
@RequestMapping("/api/courses/{courseId}/assignments")
public class AssignmentController {

    private static final String MANAGE = "hasAnyRole('DIRECTOR','COORDINATOR','TUTOR')";
    private static final String READ =
            "hasAnyRole('DIRECTOR','COORDINATOR','TUTOR','ASSISTANT','STUDENT')";

    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PostMapping
    @PreAuthorize(MANAGE)
    public ResponseEntity<AssignmentResponse> create(@PathVariable Long courseId,
                                                     @Valid @RequestBody CreateAssignmentRequest request) {
        AssignmentResponse created = assignmentService.create(courseId, request);
        return ResponseEntity
                .created(URI.create("/api/courses/" + courseId + "/assignments/" + created.id()))
                .body(created);
    }

    @GetMapping
    @PreAuthorize(READ)
    public PageResponse<AssignmentResponse> list(
            @PathVariable Long courseId,
            @PageableDefault(size = 20, sort = "dueDate") Pageable pageable) {
        return PageResponse.from(assignmentService.listForCourse(courseId, pageable));
    }

    @GetMapping("/{assignmentId}")
    @PreAuthorize(READ)
    public AssignmentResponse get(@PathVariable Long courseId, @PathVariable Long assignmentId) {
        return assignmentService.getInCourse(courseId, assignmentId);
    }

    @PutMapping("/{assignmentId}")
    @PreAuthorize(MANAGE)
    public AssignmentResponse update(@PathVariable Long courseId,
                                     @PathVariable Long assignmentId,
                                     @Valid @RequestBody UpdateAssignmentRequest request) {
        return assignmentService.update(courseId, assignmentId, request);
    }

    @DeleteMapping("/{assignmentId}")
    @PreAuthorize(MANAGE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long courseId, @PathVariable Long assignmentId) {
        assignmentService.delete(courseId, assignmentId);
    }
}