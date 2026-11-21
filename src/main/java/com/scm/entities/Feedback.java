package com.scm.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "feedbacks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback {

    @Id
    private String id;

    private String subject;

    private String category; // e.g., "Feature Request", "Bug Report", "UI/UX", "General"

    private int rating; // 1 to 5 stars

    @Column(length = 2000, nullable = false)
    private String message;

    private LocalDateTime submittedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
