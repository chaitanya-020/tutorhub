package tutorhub.course;

import jakarta.validation.Valid;
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
import tutorhub.course.dto.CourseResponse;
import tutorhub.course.dto.CreateCourseRequest;
import tutorhub.course.dto.UpdateCourseRequest;

import java.net.URI;
import java.util.List;

/**
 * Courses in the ACTIVE academy (X-Academy-Id header). DIRECTOR/COORDINATOR/TUTOR
 * may manage; any member may read. The role checked here is the per-academy role
 * stamped on the request by TenantFilter.
 */
@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private static final String MANAGE = "hasAnyRole('DIRECTOR','COORDINATOR','TUTOR')";
    private static final String READ =
            "hasAnyRole('DIRECTOR','COORDINATOR','TUTOR','ASSISTANT','STUDENT')";

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    @PreAuthorize(MANAGE)
    public ResponseEntity<CourseResponse> create(@Valid @RequestBody CreateCourseRequest request) {
        CourseResponse created = courseService.create(request);
        return ResponseEntity.created(URI.create("/api/courses/" + created.id())).body(created);
    }

    @GetMapping("/{id}")
    @PreAuthorize(READ)
    public CourseResponse get(@PathVariable Long id) {
        return courseService.getById(id);
    }

    @GetMapping
    @PreAuthorize(READ)
    public List<CourseResponse> list() {
        return courseService.list();
    }

    @PutMapping("/{id}")
    @PreAuthorize(MANAGE)
    public CourseResponse update(@PathVariable Long id,
                                 @Valid @RequestBody UpdateCourseRequest request) {
        return courseService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(MANAGE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        courseService.delete(id);
    }
}