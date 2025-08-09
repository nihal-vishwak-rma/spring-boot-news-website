package com.news.SpringBootNews_wb.controllers;

import com.news.SpringBootNews_wb.services.EmailService;
import com.news.SpringBootNews_wb.services.OtpService;
import com.news.SpringBootNews_wb.services.UserDetailServiceImpl;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.io.IOException;
import java.security.Principal;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.news.SpringBootNews_wb.model.Otp;
import com.news.SpringBootNews_wb.model.User;
import com.news.SpringBootNews_wb.model.UserFeedback;
import com.news.SpringBootNews_wb.repository.FeedbackRepository;
import com.news.SpringBootNews_wb.repository.OtpRepository;
import com.news.SpringBootNews_wb.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final UserDetailServiceImpl userDetailServiceImpl;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final OtpRepository otpRepository;

    @Autowired
    private final FeedbackRepository feedbackRepository;

    @Autowired
    private OtpService otpService;


    @GetMapping("/")
    public String home() {

        return "index";
    }

    @GetMapping("/login")
    public String openLoginPage() {

        return "login";
    }

    @GetMapping("/register")
    public String openRegisterPage(Model model) {

        model.addAttribute("user", new User());

        return "register";
    }

    @PostMapping("/otp")
    @ResponseBody
    @Transactional
    public String sendOtp(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {

        Optional<User> existinguser = userRepository.findByEmail(email);

        if (existinguser.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMsg", "User already exists.");
            return "Otp not sent because user already exists.";

        }

        if (!otpService.sendOpt(email)) {

            System.out.println("OTP is not sent to: " + email);

            return "OTP not sent due to some errors " + email;

        }

        System.out.println("OTP sent to: " + email);

        return "OTP sent successfully to " + email;
    }

    @PostMapping("/verify-otp")
    @ResponseBody
    public String verifyOtp(@RequestParam String email, @RequestParam String userotp) {

        if (otpService.verifyOtp(email, userotp)) {

            return "OTP Verified Successfully!";
        } else {
            return "Invalid OTP or Email.";
        }

    }

    @PostMapping("/regForm")
    public String submitRegister(@Valid @ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {

        Optional<Otp> otpcheck = otpRepository.findByEmail(user.getEmail());

        if (otpcheck.isEmpty() || !otpcheck.get().isVerified()) {
            redirectAttributes.addFlashAttribute("errorMsg", "Please verify OTP before registration.");
            return "redirect:/user/register";
        }

        if (!userDetailServiceImpl.registerUser(user)) {
            redirectAttributes.addFlashAttribute("errorMsg", "User already exists!");
            return "redirect:/user/register";
        }

        userDetailServiceImpl.registerUser(user);

        redirectAttributes.addFlashAttribute("successMsg", "Register Successfully. Please login.");

        return "redirect:/user/login";
    }

    @GetMapping("/profile")
    public String userProfile(Model model, Principal principal) {

        String email = principal.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        model.addAttribute("user", user);

        return "profile";
    }

    @PostMapping("/profile")
    public String userUpdateProfile(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes,
            Principal principal) {

        if (user != null) {

            String email = principal.getName();

            User updateuser = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

            updateuser.setEmail(user.getEmail());
            updateuser.setName(user.getName());

            userRepository.save(updateuser);

            redirectAttributes.addFlashAttribute("successMsg", "update successfully.");

            return "redirect:/user/profile";

        }

        redirectAttributes.addFlashAttribute("errorMsg", "Details Not Update Due To Some Errors.");

        return "redirect:/user/profile";
    }

    @GetMapping("/feedback")
    public String userFeedback(Model model, Principal principal) {

        String email = principal.getName();

        UserFeedback feedback = new UserFeedback();

        feedback.setEmail(email);

        model.addAttribute("feedback", feedback);

        return "feedback";
    }

    @PostMapping("/feedback")
    public String submitFeedback(@ModelAttribute("feedback") UserFeedback userFeedback,
            RedirectAttributes redirectAttributes) {

        feedbackRepository.save(userFeedback);

        redirectAttributes.addFlashAttribute("successMsg", "Feedback Submited Successfully.");

        return "redirect:/user/feedback";
    }

    @GetMapping("/forgotpassword")
    public String forgotPassword(Model model) {

        return "forgotpassword";
    }

    @PostMapping("/forgot-password")
    public String sumbitForgotPassword(@RequestParam("email") String email, Model model,
            RedirectAttributes redirectAttributes) throws IOException {

        Optional<User> user = userRepository.findByEmail(email);

        if (!user.isPresent()) {

            redirectAttributes.addFlashAttribute("errorMsg", "User not Found.");
            return "redirect:/user/forgotpassword";

        }

        if (otpService.sendOpt(email)) {

            System.out.println("OTP is not sent to: " + email);

            model.addAttribute("successMsg", "OTP sent to your email.");
            model.addAttribute("showOtpSection", true);
            model.addAttribute("email", email);

        }

        else {

            model.addAttribute("errorMsg", "OTP not sent due to some errors.");
            model.addAttribute("showOtpSection", false);
            model.addAttribute("email", email);
        }

        return "forgotpassword";
    }

    @PostMapping("/verify-otp-forgotpassword")
    public String verifyOtpForgotPassword(@RequestParam String email, @RequestParam String userotp,
            RedirectAttributes redirectAttributes, Model model) {

        if (otpService.verifyOtp(email, userotp)) {

            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/user/resetpassword";
        }

         redirectAttributes.addFlashAttribute("errorMsg", "Invalid OTP.");
         redirectAttributes.addFlashAttribute("email", email);
         redirectAttributes.addFlashAttribute("showOtpSection", true);
        return "redirect:/user/forgotpassword";

    }

    @GetMapping("/resetpassword")
    public String resetPassword(@ModelAttribute("email") String email, Model model) {

        model.addAttribute("email", email);
        return "resetpassword";
    }

    @PostMapping("/updatepassword")
    public String updatePassword(@RequestParam String email, @RequestParam String newPassword,
            @RequestParam String confirmPassword, Model model, RedirectAttributes redirectAttributes) {

        if (!newPassword.equals(confirmPassword)) {

            redirectAttributes.addFlashAttribute("errorMsg", "Password not matched");
            redirectAttributes.addFlashAttribute("email", email);

            return "redirect:/user/resetpassword";

        }

        Optional<User> user = userRepository.findByEmail(email);

        if (!user.isPresent()) {

            redirectAttributes.addFlashAttribute("errorMsg", "User not found.");
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/user/forgotpassword";

        }

        user.get().setPassword(passwordEncoder.encode(confirmPassword));
        userRepository.save(user.get());

        redirectAttributes.addFlashAttribute("successMsg", "Password reset successfully.");

        System.out.println("update ho gya");

        return "redirect:/user/login";

    }

    @PostMapping("/resendotp")
    public String resendOtp(@RequestParam String email, Model model, RedirectAttributes redirectAttributes) {

        if (!otpService.sendOpt(email)) {
            redirectAttributes.addFlashAttribute("errorMsg", "Otp not sent.");
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("showOtpSection", true);
            return "redirect:/user/forgotpassword";
        }

        redirectAttributes.addFlashAttribute("successMsg", "Otp sent to your " + email);
        redirectAttributes.addFlashAttribute("email", email);
        redirectAttributes.addFlashAttribute("showOtpSection", true);

        return "redirect:/user/forgotpassword";
    }

}
