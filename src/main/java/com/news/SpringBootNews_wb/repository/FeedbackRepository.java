package com.news.SpringBootNews_wb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.news.SpringBootNews_wb.model.UserFeedback;

public interface FeedbackRepository extends JpaRepository<UserFeedback , Long>{

}
