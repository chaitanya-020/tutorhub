package tutorhub.submission;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tutorhub.assignment.Assignment;
import tutorhub.assignment.AssignmentRepository;
import tutorhub.common.BadRequestException;
import tutorhub.common.ForbiddenException;
import tutorhub.common.ResourceNotFoundException;
import tutorhub.enrollment.EnrollmentRepository;
import tutorhub.membership.Role;
import tutorhub.security.SecurityUtils;
import tutorhub.security.TenantContext;
import tutorhub.submission.dto.GradeRequest;
import tutorhub.submission.dto.SubmissionResponse;
import tutorhub.submission.dto.SubmitRequest;
import tutorhub.user.User;
import tutorhub.user.UserRepository;

/**
 * Submissions belong to an assignment. Students submit and see only their own;
 * staff see all and grade. Grading is protected by optimistic locking.
 */
@Service
@Transactional
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;

    public SubmissionService(SubmissionRepository submissionRepository,
                             AssignmentRepository assignmentRepository,
                             EnrollmentRepository enrollmentRepository,
                             UserRepository userRepository) {
        this.submissionRepository = submissionRepository;
        this.assignmentRepository = assignmentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
    }

    /** The current STUDENT submits their own work for an assignment. */
    public SubmissionResponse submit(Long assignmentId, SubmitRequest request) {
        Assignment assignment = loadAssignmentInAcademy(assignmentId);
        Long studentId = SecurityUtils.currentUserId();

        if (!enrollmentRepository.existsByCourseIdAndStudentId(
                assignment.getCourse().getId(), studentId)) {
            throw new ForbiddenException("You are not enrolled in this course.");
        }

        User student = userRepository.getReferenceById(studentId);
        Submission submission = new Submission();
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setContent(request.content());
        submission.setStatus(SubmissionStatus.SUBMITTED);
        return SubmissionResponse.from(submissionRepository.save(submission));
    }

    @Transactional(readOnly = true)
    public Page<SubmissionResponse> listForAssignment(Long assignmentId, Pageable pageable) {
        loadAssignmentInAcademy(assignmentId);
        Page<Submission> page = (TenantContext.role() == Role.STUDENT)
                ? submissionRepository.findByAssignmentIdAndStudentId(
                assignmentId, SecurityUtils.currentUserId(), pageable)
                : submissionRepository.findByAssignmentId(assignmentId, pageable);
        return page.map(SubmissionResponse::from);
    }

    @Transactional(readOnly = true)
    public SubmissionResponse getById(Long assignmentId, Long submissionId) {
        return SubmissionResponse.from(loadSubmission(assignmentId, submissionId));
    }

    /** Staff grade a submission, guarded by optimistic locking. */
    public SubmissionResponse grade(Long assignmentId, Long submissionId, GradeRequest request) {
        Submission submission = loadSubmission(assignmentId, submissionId);

        int maxScore = submission.getAssignment().getMaxScore();
        if (request.score() > maxScore) {
            throw new BadRequestException(
                    "Score " + request.score() + " exceeds the assignment max of " + maxScore + ".");
        }

        // Optimistic check: the grader must be acting on the version they last saw.
        // If it no longer matches, someone else graded in the meantime -> 409.
        if (!submission.getVersion().equals(request.version())) {
            throw new ObjectOptimisticLockingFailureException(Submission.class, submissionId);
        }

        submission.setScore(request.score());
        submission.setStatus(SubmissionStatus.GRADED);
        // On save/flush, Hibernate bumps @Version and would ALSO throw
        // ObjectOptimisticLockingFailureException if a true concurrent write slipped through.
        return SubmissionResponse.from(submissionRepository.save(submission));
    }

    private Submission loadSubmission(Long assignmentId, Long submissionId) {
        loadAssignmentInAcademy(assignmentId);
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> ResourceNotFoundException.of("Submission", submissionId));
        if (!submission.getAssignment().getId().equals(assignmentId)) {
            throw ResourceNotFoundException.of("Submission", submissionId);
        }
        // A student may only see their own submission.
        if (TenantContext.role() == Role.STUDENT
                && !submission.getStudent().getId().equals(SecurityUtils.currentUserId())) {
            throw ResourceNotFoundException.of("Submission", submissionId);
        }
        return submission;
    }

    private Assignment loadAssignmentInAcademy(Long assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> ResourceNotFoundException.of("Assignment", assignmentId));
        if (!assignment.getCourse().getAcademy().getId().equals(TenantContext.academyId())) {
            throw ResourceNotFoundException.of("Assignment", assignmentId);
        }
        return assignment;
    }
}