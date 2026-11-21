package com.scm.repositories;

import com.scm.entities.Feedback;
import com.scm.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepo extends JpaRepository<Feedback, String> {
    List<Feedback> findByUserOrderBySubmittedAtDesc(User user);
}
