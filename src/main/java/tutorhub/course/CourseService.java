package tutorhub.course;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tutorhub.academy.Academy;
import tutorhub.academy.AcademyRepository;
import tutorhub.common.ResourceNotFoundException;
import tutorhub.course.dto.CourseResponse;
import tutorhub.course.dto.CreateCourseRequest;
import tutorhub.course.dto.UpdateCourseRequest;
import tutorhub.enrollment.EnrollmentRepository;
import tutorhub.membership.Role;
import tutorhub.security.SecurityUtils;
import tutorhub.security.TenantContext;

import java.util.List;

/**
 * Every operation is scoped to the active academy (cross-tenant isolation).
 * On top of that, STUDENTS are scoped to their OWN enrollments (intra-tenant
 * per-student scoping): a student sees only the courses they're enrolled in,
 * while staff see all courses in the academy.
 */
@Service
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final AcademyRepository academyRepository;
    private final EnrollmentRepository enrollmentRepository;

    public CourseService(CourseRepository courseRepository,
                         AcademyRepository academyRepository,
                         EnrollmentRepository enrollmentRepository) {
        this.courseRepository = courseRepository;
        this.academyRepository = academyRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public CourseResponse create(CreateCourseRequest request) {
        Academy academy = academyRepository.getReferenceById(TenantContext.academyId());
        Course course = new Course();
        course.setAcademy(academy);
        course.setTitle(request.title());
        course.setSubject(request.subject());
        course.setTerm(request.term());
        return CourseResponse.from(courseRepository.save(course));
    }

    @Transactional(readOnly = true)
    public CourseResponse getById(Long id) {
        Course course = loadInAcademy(id);
        // A student may only see a course they're enrolled in; otherwise 404.
        if (TenantContext.role() == Role.STUDENT
                && !enrollmentRepository.existsByCourseIdAndStudentId(id, SecurityUtils.currentUserId())) {
            throw ResourceNotFoundException.of("Course", id);
        }
        return CourseResponse.from(course);
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> list() {
        Long academyId = TenantContext.academyId();

        if (TenantContext.role() == Role.STUDENT) {
            Long studentId = SecurityUtils.currentUserId();
            return enrollmentRepository
                    .findByStudent_IdAndCourse_Academy_Id(studentId, academyId)
                    .stream()
                    .map(e -> CourseResponse.from(e.getCourse()))
                    .toList();
        }

        return courseRepository.findByAcademyId(academyId).stream()
                .map(CourseResponse::from).toList();
    }

    public CourseResponse update(Long id, UpdateCourseRequest request) {
        Course course = loadInAcademy(id);
        course.setTitle(request.title());
        course.setSubject(request.subject());
        course.setTerm(request.term());
        return CourseResponse.from(courseRepository.save(course));
    }

    public void delete(Long id) {
        courseRepository.delete(loadInAcademy(id));
    }

    /**
     * Loads a course ONLY if it belongs to the active academy; a course from a
     * different academy is reported as 404. This is the cross-tenant guard.
     */
    private Course loadInAcademy(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Course", id));
        if (!course.getAcademy().getId().equals(TenantContext.academyId())) {
            throw ResourceNotFoundException.of("Course", id);
        }
        return course;
    }
}