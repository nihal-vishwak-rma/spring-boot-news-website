package com.news.SpringBootNews_wb.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.news.SpringBootNews_wb.model.Otp;

@Repository
public interface OtpRepository extends JpaRepository<Otp,Long> {

    Optional<Otp> findByEmail(String email);

    void deleteByCreatedAtBefore(LocalDateTime expiryTime);


}
