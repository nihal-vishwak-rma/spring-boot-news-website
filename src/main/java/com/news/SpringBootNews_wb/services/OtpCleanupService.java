package com.news.SpringBootNews_wb.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.news.SpringBootNews_wb.repository.OtpRepository;

@Service
public class OtpCleanupService {


    @Autowired
    private OtpRepository otpRepository;

     @Scheduled(fixedRate = 60000) // Har 60 seconds me chalega
    public void deleteExpiredOtps() {
        LocalDateTime expiryTime = LocalDateTime.now().minusMinutes(10);
        otpRepository.deleteByCreatedAtBefore(expiryTime);
    }

}
