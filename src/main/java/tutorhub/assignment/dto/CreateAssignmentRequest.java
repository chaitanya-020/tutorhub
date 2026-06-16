package tutorhub.assignment.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record CreateAssignmentRequest(

        @NotBlank @Size(max = 255)
        String title,

        @Size(max = 10_000)
        String instructions,            // optional

        @NotNull @Future
        Instant dueDate,                // must be in the future

        @NotNull @Min(1)
        Integer maxScore
) {}