package com.scm.services;

import com.scm.entities.Feedback;
import com.scm.entities.User;
import com.scm.forms.FeedbackForm;

import java.util.List;

public interface FeedbackService {

    Feedback saveFeedback(FeedbackForm feedbackForm, User user);

    List<Feedback> getUserFeedbacks(User user);

    List<Feedback> getAllFeedbacks();

    void deleteFeedback(String feedbackId, User user);
}