package com.scm.services.impl;

import com.scm.entities.Feedback;
import com.scm.entities.User;
import com.scm.forms.FeedbackForm;
import com.scm.repositories.FeedbackRepo;
import com.scm.services.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackRepo feedbackRepo;

    @Override
    public Feedback saveFeedback(FeedbackForm feedbackForm, User user) {
        Feedback feedback = Feedback.builder()
                .id(UUID.randomUUID().toString())
                .category(feedbackForm.getCategory())
                .subject(feedbackForm.getSubject())
                .rating(feedbackForm.getRating())
                .message(feedbackForm.getMessage())
                .submittedAt(LocalDateTime.now())
                .user(user)
                .build();

        return feedbackRepo.save(feedback);
    }

    @Override
    public List<Feedback> getUserFeedbacks(User user) {
        return feedbackRepo.findByUserOrderBySubmittedAtDesc(user);
    }

    @Override
    public List<Feedback> getAllFeedbacks() {
        return feedbackRepo.findAll();
    }

    @Override
    public void deleteFeedback(String feedbackId, User user) {
        Feedback feedback = feedbackRepo.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));

        // Security check: ensure the feedback belongs to the logged-in user
        if (feedback.getUser() != null && feedback.getUser().getUserId().equals(user.getUserId())) {
            feedbackRepo.delete(feedback);
        }
    }
}