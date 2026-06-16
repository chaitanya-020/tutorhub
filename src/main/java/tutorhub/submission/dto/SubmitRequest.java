package tutorhub.submission.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SubmitRequest(

        @NotBlank @Size(max = 20_000)
        String content
) {}