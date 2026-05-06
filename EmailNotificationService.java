package com.example.jobportal.controller;

import com.example.jobportal.model.User;
import com.example.jobportal.model.Role;
import com.example.jobportal.repository.UserRepository;
import com.example.jobportal.repository.JobRepository;
import com.example.jobportal.repository.ApplicationRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class DashboardController {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;

    public DashboardController(UserRepository userRepository, JobRepository jobRepository, ApplicationRepository applicationRepository) {
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        if (authentication == null) return "redirect:/login";
        User user = userRepository.findByUsername(authentication.getName()).orElse(null);
        if (user == null) return "redirect:/login";
        
        model.addAttribute("user", user);
        
        if (user.getRole() == Role.EMPLOYER) {
            model.addAttribute("jobs", jobRepository.findByEmployer(user));
            model.addAttribute("applications", applicationRepository.findByJobEmployer(user));
            model.addAttribute("applicationCount", applicationRepository.countByJobEmployer(user));
            model.addAttribute("shortlistedCount", applicationRepository.countByJobEmployerAndStatus(user, "SHORTLISTED"));
            return "employer_dashboard";
        } else if (user.getRole() == Role.STUDENT) {
            model.addAttribute("myApplications", applicationRepository.findByStudent(user));
            return "student_dashboard";
        }
        
        return "index";
    }

    @GetMapping("/profile/edit")
    public String editProfile(Authentication authentication, Model model) {
        if (authentication == null) return "redirect:/login";
        User user = userRepository.findByUsername(authentication.getName()).orElse(null);
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        return "edit_profile";
    }

    @PostMapping("/profile/edit")
    public String saveProfile(Authentication authentication, 
                              @RequestParam(required = false) String companyDescription,
                              @RequestParam(required = false) String website,
                              @RequestParam(required = false) String bio,
                              @RequestParam(required = false) String skills) {
        if (authentication == null) return "redirect:/login";
        User user = userRepository.findByUsername(authentication.getName()).orElse(null);
        if (user == null) return "redirect:/login";
        
        if (user.getRole() == Role.EMPLOYER) {
            user.setCompanyDescription(companyDescription);
            user.setWebsite(website);
        } else if (user.getRole() == Role.STUDENT) {
            user.setBio(bio);
            user.setSkills(skills);
        }
        userRepository.save(user);
        
        return "redirect:/dashboard?profile_updated=true";
    }

    @GetMapping("/student/{id}")
    public String studentProfile(@PathVariable Long id, Model model) {
        User student = userRepository.findById(id).orElse(null);
        if (student == null || student.getRole() != Role.STUDENT) {
            return "redirect:/dashboard";
        }
        model.addAttribute("student", student);
        return "student_profile";
    }

    @GetMapping("/company/{id}")
    public String companyProfile(@PathVariable Long id, Model model) {
        User employer = userRepository.findById(id).orElse(null);
        if (employer == null || employer.getRole() != Role.EMPLOYER) {
            return "redirect:/jobs";
        }
        model.addAttribute("employer", employer);
        return "company_profile";
    }
}
