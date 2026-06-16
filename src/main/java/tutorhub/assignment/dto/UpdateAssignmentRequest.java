package tutorhub.assignment.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

/**
 * No @Future on dueDate here — you may legitimately edit an assignment whose
 * due date has already passed.
 */
public record UpdateAssignmentRequest(

        @NotBlank @Size(max = 255)
        String title,

        @Size(max = 10_000)
        String instructions,

        @NotNull
        Instant dueDate,

        @NotNull @Min(1)
        Integer maxScore
) {}