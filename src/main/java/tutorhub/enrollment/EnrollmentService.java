package tutorhub.enrollment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tutorhub.common.ForbiddenException;
import tutorhub.common.ResourceNotFoundException;
import tutorhub.course.Course;
import tutorhub.course.CourseRepository;
import tutorhub.enrollment.dto.EnrollRequest;
import tutorhub.enrollment.dto.EnrollmentResponse;
import tutorhub.membership.MembershipRepository;
import tutorhub.security.TenantContext;
import tutorhub.user.User;
import tutorhub.user.UserRepository;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             CourseRepository courseRepository,
                             UserRepository userRepository,
                             MembershipRepository membershipRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.membershipRepository = membershipRepository;
    }

    public EnrollmentResponse enroll(Long courseId, EnrollRequest request) {
        Course course = loadCourseInAcademy(courseId);

        User student = userRepository.findByEmail(request.studentEmail())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No registered user with email " + request.studentEmail()));

        // You can only enroll someone who already belongs to this academy.
        if (!membershipRepository.existsByUserIdAndAcademyId(student.getId(), TenantContext.academyId())) {
            throw new ForbiddenException("That user is not a member of this academy.");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setCourse(course);
        enrollment.setStudent(student);
        enrollment.setEnrolledAt(Instant.now());
        // Re-enrolling the same student is blocked by the unique (course, student)
        // constraint -> 409 via the global handler.
        return EnrollmentResponse.from(enrollmentRepository.save(enrollment));
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> listForCourse(Long courseId) {
        loadCourseInAcademy(courseId); // ensures the course is in the active academy
        return enrollmentRepository.findByCourseId(courseId).stream()
                .map(EnrollmentResponse::from).toList();
    }

    public void unenroll(Long courseId, Long enrollmentId) {
        loadCourseInAcademy(courseId);
        Enrollment enrollment = enrollmentRepository.findByIdAndCourseId(enrollmentId, courseId)
                .orElseThrow(() -> ResourceNotFoundException.of("Enrollment", enrollmentId));
        enrollmentRepository.delete(enrollment);
    }

    /** Cross-tenant guard: the course must belong to the active academy, else 404. */
    private Course loadCourseInAcademy(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> ResourceNotFoundException.of("Course", courseId));
        if (!course.getAcademy().getId().equals(TenantContext.academyId())) {
            throw ResourceNotFoundException.of("Course", courseId);
        }
        return course;
    }
}