package tutorhub.membership;

/**
 * A user's role WITHIN a specific academy (carried by Membership).
 * The same person can be a TUTOR in one academy and a STUDENT in another.
 */
public enum Role {
    DIRECTOR,     // founder/owner: full control of the academy
    COORDINATOR,  // manages tutors, courses, enrollments
    TUTOR,        // teaches courses, creates and grades assignments
    ASSISTANT,    // helps grade; cannot manage course setup or enrollment
    STUDENT       // sees only their own courses, assignments, and feedback
}