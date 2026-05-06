package com.example.jobportal.controller;

import com.example.jobportal.model.Application;
import com.example.jobportal.model.Job;
import com.example.jobportal.model.User;
import com.example.jobportal.repository.ApplicationRepository;
import com.example.jobportal.repository.JobRepository;
import com.example.jobportal.repository.UserRepository;
import com.example.jobportal.service.EmailNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.UUID;

@Controller
@RequestMapping("/applications")
public class ApplicationController {

    private static final Logger log = LoggerFactory.getLogger(ApplicationController.class);

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final EmailNotificationService emailNotificationService;
    
    private final String UPLOAD_DIR = "uploads/";

    public ApplicationController(ApplicationRepository applicationRepository, JobRepository jobRepository,
                                 UserRepository userRepository, EmailNotificationService emailNotificationService) {
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.emailNotificationService = emailNotificationService;
    }

    @PostMapping("/apply/{jobId}")
    public String applyJob(@PathVariable Long jobId, @RequestParam("resume") MultipartFile resume, Authentication auth) {
        try {
            if (resume == null || resume.isEmpty()) {
                return "redirect:/jobs?error=Resume_Required";
            }

            User student = userRepository.findByUsername(auth.getName()).orElseThrow();
            Job job = jobRepository.findById(jobId).orElseThrow();

            Application application = new Application();
            application.setStudent(student);
            application.setJob(job);
            application.setStatus("APPLIED");

            if (!resume.isEmpty()) {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                String filename = UUID.randomUUID().toString() + "_" + resume.getOriginalFilename();
                byte[] bytes = resume.getBytes();
                Path path = Paths.get(UPLOAD_DIR + filename);
                Files.write(path, bytes);
                application.setResumePath(filename);
            }

            applicationRepository.save(application);
            return "redirect:/dashboard?applied_success";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/jobs?error=Upload_Failed";
        }
    }

    @PostMapping("/{appId}/status")
    public String updateStatus(@PathVariable Long appId, @RequestParam String status, Authentication auth) {
        try {
            Application application = applicationRepository.findById(appId).orElseThrow();
            User employer = userRepository.findByUsername(auth.getName()).orElseThrow();

            if (!application.getJob().getEmployer().getId().equals(employer.getId())) {
                return "redirect:/dashboard?error=Unauthorized";
            }

            application.setStatus(status);
            applicationRepository.save(application);

            try {
                emailNotificationService.sendStatusUpdateEmail(application, status);
                return "redirect:/dashboard?status_update_success";
            } catch (Exception mailEx) {
                log.error("Application status updated but email failed for appId={}", appId, mailEx);
                String mailError = classifyMailError(mailEx);
                return "redirect:/dashboard?status_updated_email_failed&mail_error=" + mailError;
            }
        } catch (Exception ex) {
            log.error("Failed to update application status and send notification for appId={}", appId, ex);
            return "redirect:/dashboard?error=Status_Update_Failed";
        }
    }

    @GetMapping("/resume/{appId}")
    @ResponseBody
    public ResponseEntity<Resource> downloadResume(@PathVariable Long appId, Authentication auth) {
        try {
            Application application = applicationRepository.findById(appId).orElseThrow();
            User currentUser = userRepository.findByUsername(auth.getName()).orElseThrow();

            boolean isEmployerOwner = application.getJob().getEmployer().getId().equals(currentUser.getId());
            boolean isOwnerStudent = application.getStudent().getId().equals(currentUser.getId());
            if (!isEmployerOwner && !isOwnerStudent) {
                return ResponseEntity.status(403).build();
            }
            if (application.getResumePath() == null || application.getResumePath().isBlank()) {
                return ResponseEntity.notFound().build();
            }

            Path filePath = Paths.get(UPLOAD_DIR).resolve(application.getResumePath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + application.getResumePath() + "\"")
                    .body(resource);
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private String classifyMailError(Exception mailEx) {
        String message = mailEx.getMessage();
        if (message == null) {
            message = "";
        }

        String normalized = message.toLowerCase(Locale.ROOT);
        if (normalized.contains("student email is required")) {
            return "STUDENT_EMAIL";
        }
        if (normalized.contains("smtp sender is not configured")) {
            return "CONFIG";
        }
        if (normalized.contains("authentication")
                || normalized.contains("535")
                || normalized.contains("534")
                || normalized.contains("username and password not accepted")
                || normalized.contains("failed to authenticate")
                || normalized.contains("not accepted due to")) {
            return "AUTH";
        }
        if (normalized.contains("timed out")
                || normalized.contains("timeout")
                || normalized.contains("could not connect")
                || normalized.contains("connection refused")
                || normalized.contains("unknown host")) {
            return "CONNECTION";
        }
        return "UNKNOWN";
    }
}
