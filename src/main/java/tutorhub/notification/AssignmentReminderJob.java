package tutorhub.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tutorhub.assignment.Assignment;
import tutorhub.assignment.AssignmentRepository;
import tutorhub.enrollment.Enrollment;
import tutorhub.enrollment.EnrollmentRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Runs on a cron schedule. Finds assignments due within the next 24 hours that
 * haven't been reminded yet, emails every enrolled student, and stamps
 * reminderSentAt so the next run won't email them again.
 *
 * @Transactional keeps the session open so the LAZY course/student associations
 * can be read while building each email.
 */
@Component
public class AssignmentReminderJob {

    private static final Logger log = LoggerFactory.getLogger(AssignmentReminderJob.class);

    private final AssignmentRepository assignmentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final EmailService emailService;

    public AssignmentReminderJob(AssignmentRepository assignmentRepository,
                                 EnrollmentRepository enrollmentRepository,
                                 EmailService emailService) {
        this.assignmentRepository = assignmentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.emailService = emailService;
    }

    // Default: top of every hour. Override tutorhub.reminders.cron for testing.
    @Scheduled(cron = "${tutorhub.reminders.cron:0 0 * * * *}")
    @Transactional
    public void sendDueSoonReminders() {
        Instant now = Instant.now();
        Instant cutoff = now.plus(24, ChronoUnit.HOURS);

        List<Assignment> dueSoon =
                assignmentRepository.findByReminderSentAtIsNullAndDueDateBetween(now, cutoff);
        if (dueSoon.isEmpty()) {
            return;
        }

        for (Assignment assignment : dueSoon) {
            List<Enrollment> enrollments =
                    enrollmentRepository.findByCourseId(assignment.getCourse().getId());
            for (Enrollment enrollment : enrollments) {
                emailService.sendAssignmentReminder(
                        enrollment.getStudent().getEmail(),
                        enrollment.getStudent().getDisplayName(),
                        assignment.getTitle(),
                        assignment.getDueDate());
            }
            assignment.setReminderSentAt(now);
        }

        assignmentRepository.saveAll(dueSoon);
        log.info("Sent reminders for {} assignment(s) due within 24h", dueSoon.size());
    }

}