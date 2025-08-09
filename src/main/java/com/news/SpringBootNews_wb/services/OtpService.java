package com.news.SpringBootNews_wb.services;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.news.SpringBootNews_wb.model.Otp;
import com.news.SpringBootNews_wb.repository.OtpRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OtpService {

    @Autowired
    private final OtpRepository otpRepository;

    @Autowired
    private final EmailService emailService;

    public String generateOtp() {

        SecureRandom random = new SecureRandom();

        int otp = random.nextInt(900000) + 100000;

        return String.valueOf(otp);
    }

    public boolean verifyOtp(String email, String userotp) {

        Optional<Otp> otp = otpRepository.findByEmail(email);

        if (otp.isPresent() && otp.get().getOtp().equals(userotp)) {

            otp.get().setVerified(true);
            otpRepository.save(otp.get());

            return true;
        } else {
            return false;
        }

    }

    public boolean sendOpt(String email) {

        try {

            String otpGen = generateOtp();

            emailService.sendOtpEmail(email, "Your Otp is : ", otpGen);

            Otp otp;

            Optional<Otp> existingotp = otpRepository.findByEmail(email);

            if (existingotp.isPresent()) {
                otp = existingotp.get();

            } else {

                otp = new Otp();
                otp.setEmail(email);
            }

            otp.setOtp(otpGen);
            otp.setCreatedAt(LocalDateTime.now());
            otp.setVerified(false);

            otpRepository.save(otp);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

}
