package tutorhub.submission;

/**
 * Lifecycle of a student's submission.
 */
public enum SubmissionStatus {
    DRAFT,      // started, not yet handed in
    SUBMITTED,  // handed in, awaiting grading
    GRADED      // graded by a tutor
}