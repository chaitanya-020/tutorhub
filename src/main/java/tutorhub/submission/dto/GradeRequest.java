package tutorhub.submission.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * version is the value the grader last saw (from the submission they fetched).
 * If it no longer matches the stored version, someone else graded in the
 * meantime and we reject with 409 instead of overwriting their work.
 */
public record GradeRequest(

        @NotNull @Min(0)
        Integer score,

        @NotNull
        Long version
) {}