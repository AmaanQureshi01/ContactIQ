package com.scm.forms;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackForm {

    @NotBlank(message = "Please select a category")
    private String category;

    @NotBlank(message = "Subject is required")
    @Size(min = 3, max = 100, message = "Subject must be between 3 and 100 characters")
    private String subject;

    @Min(value = 1, message = "Please select a rating between 1 and 5")
    @Max(value = 5, message = "Rating cannot exceed 5")
    private int rating = 5;

    @NotBlank(message = "Feedback message cannot be empty")
    @Size(min = 10, max = 2000, message = "Please write at least 10 characters")
    private String message;
}