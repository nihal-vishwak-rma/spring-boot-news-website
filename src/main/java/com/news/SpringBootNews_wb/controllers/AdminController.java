package com.news.SpringBootNews_wb.controllers;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.news.SpringBootNews_wb.model.User;
import com.news.SpringBootNews_wb.model.UserFeedback;
import com.news.SpringBootNews_wb.repository.FeedbackRepository;
import com.news.SpringBootNews_wb.repository.UserRepository;
import com.news.SpringBootNews_wb.services.UserDetailServiceImpl;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

  @Autowired
  private final UserRepository userRepository;

  @Autowired
  private final UserDetailServiceImpl userDetailServiceImpl;

  @Autowired
  private final FeedbackRepository feedbackRepository;

  @GetMapping("/viewAllUsers")
  public String getAllUser(Model model , @RequestParam(defaultValue = "0") int page ) {

     int size = 10;

     Page<User> userPage = userRepository.findAll(PageRequest.of(page, size));

    model.addAttribute("userList", userPage.getContent());
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", userPage.getTotalPages());

   

    return "adminInterface/viewuser";
  }

  @GetMapping("/deleteUser/{id}")
  public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {

    boolean status = userDetailServiceImpl.deleteUser(id);

    if (status) {
      redirectAttributes.addFlashAttribute("sucessMsg", "User Delete Successfully.");
    } else {
      redirectAttributes.addFlashAttribute("errorMsg", "User Not Delete Due to Some errors.");
    }

    return "redirect:/admin/viewAllUsers";
  }

  @GetMapping("/admindashboard")
  public String adminDashboard() {

    return "adminInterface/admindashboard";
  }

  @GetMapping("/switchRole/{id}")
  public String switchRole(@PathVariable Long id, RedirectAttributes redirectAttributes) {

    boolean status = userDetailServiceImpl.switchRole(id);

    if (status) {

      redirectAttributes.addFlashAttribute("successMsg", "User Role Switch Successfully.");

    } else {

      redirectAttributes.addFlashAttribute("errorMsg", "User Role Not Switch Due To Some Errors.");
    }

    return "redirect:/admin/viewAllUsers";
  }

  @GetMapping("/feedback")
  public String feedback(Model model) {

    List<UserFeedback> feedbackList = feedbackRepository.findAll();

    model.addAttribute("feedbacks", feedbackList);

    return "adminInterface/adminfeedback";
  }

  @GetMapping("/feedback/delete/{id}")
  public String deleteFeedback(@PathVariable Long id, RedirectAttributes redirectAttributes) {

    UserFeedback user = feedbackRepository.findById(id)
        .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

    if (user != null) {

      feedbackRepository.delete(user);

      redirectAttributes.addFlashAttribute("successMsg" , "User FeedBack Delete Successfully.");

      return "redirect:/admin/feedback";
    }

     redirectAttributes.addFlashAttribute("errorMsg" , "User Not Found And Something Error.");

    return "redirect:/admin/feedback";
  }

}
