package tutorhub.assignment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tutorhub.assignment.dto.AssignmentResponse;
import tutorhub.assignment.dto.CreateAssignmentRequest;
import tutorhub.assignment.dto.UpdateAssignmentRequest;
import tutorhub.common.ResourceNotFoundException;
import tutorhub.course.Course;
import tutorhub.course.CourseRepository;
import tutorhub.enrollment.EnrollmentRepository;
import tutorhub.membership.Role;
import tutorhub.security.SecurityUtils;
import tutorhub.security.TenantContext;

/**
 * Assignments live inside a course. Cross-tenant: the course must be in the
 * active academy. Per-student: a STUDENT can only touch assignments of courses
 * they're enrolled in (otherwise the course "doesn't exist" -> 404).
 */
@Service
@Transactional
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    public AssignmentService(AssignmentRepository assignmentRepository,
                             CourseRepository courseRepository,
                             EnrollmentRepository enrollmentRepository) {
        this.assignmentRepository = assignmentRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public AssignmentResponse create(Long courseId, CreateAssignmentRequest request) {
        Course course = requireCourseInAcademy(courseId);
        Assignment assignment = new Assignment();
        assignment.setCourse(course);
        assignment.setTitle(request.title());
        assignment.setInstructions(request.instructions());
        assignment.setDueDate(request.dueDate());
        assignment.setMaxScore(request.maxScore());
        return AssignmentResponse.from(assignmentRepository.save(assignment));
    }

    @Transactional(readOnly = true)
    public Page<AssignmentResponse> listForCourse(Long courseId, Pageable pageable) {
        requireCourseInAcademy(courseId);
        requireStudentEnrolled(courseId);
        return assignmentRepository.findByCourseId(courseId, pageable).map(AssignmentResponse::from);
    }

    @Transactional(readOnly = true)
    public AssignmentResponse getInCourse(Long courseId, Long assignmentId) {
        return AssignmentResponse.from(loadAssignment(courseId, assignmentId));
    }

    public AssignmentResponse update(Long courseId, Long assignmentId, UpdateAssignmentRequest request) {
        Assignment assignment = loadAssignment(courseId, assignmentId);
        assignment.setTitle(request.title());
        assignment.setInstructions(request.instructions());
        assignment.setDueDate(request.dueDate());
        assignment.setMaxScore(request.maxScore());
        return AssignmentResponse.from(assignmentRepository.save(assignment));
    }

    public void delete(Long courseId, Long assignmentId) {
        assignmentRepository.delete(loadAssignment(courseId, assignmentId));
    }

    private Assignment loadAssignment(Long courseId, Long assignmentId) {
        requireCourseInAcademy(courseId);
        requireStudentEnrolled(courseId);
        return assignmentRepository.findByIdAndCourseId(assignmentId, courseId)
                .orElseThrow(() -> ResourceNotFoundException.of("Assignment", assignmentId));
    }

    private Course requireCourseInAcademy(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> ResourceNotFoundException.of("Course", courseId));
        if (!course.getAcademy().getId().equals(TenantContext.academyId())) {
            throw ResourceNotFoundException.of("Course", courseId);
        }
        return course;
    }

    private void requireStudentEnrolled(Long courseId) {
        if (TenantContext.role() == Role.STUDENT
                && !enrollmentRepository.existsByCourseIdAndStudentId(courseId, SecurityUtils.currentUserId())) {
            throw ResourceNotFoundException.of("Course", courseId);
        }
    }
}