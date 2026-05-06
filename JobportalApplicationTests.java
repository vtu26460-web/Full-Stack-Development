package com.example.jobportal.service;

import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class ChatbotService {

    /**
     * Simple, rule-based replies for Nexus Jobs—no external API keys required.
     */
    public String getReply(String raw) {
        if (raw == null || raw.isBlank()) {
            return "Pop in a question when you’re ready—I’m here to help.";
        }
        String message = raw.toLowerCase(Locale.ROOT).trim();

        if (anyWord(message, "hi", "hello", "hey", "hiya", "howdy")) {
            return "Hello! I’m your Nexus Jobs guide. Ask me about signing up, finding jobs, or posting roles.";
        }
        if (anyWord(message, "help")) {
            return "Try asking: how do I register, where are the jobs, how do I apply, or how do employers post a job.";
        }
        if (anyWord(message, "register", "sign up", "signup", "account", "join")) {
            return "Create an account from the Sign up page. Pick Student to apply to roles, or Employer to post openings.";
        }
        if (anyWord(message, "login", "log in", "sign in", "password")) {
            return "Head to Login with your username and password. After sign-in you’ll land on your dashboard.";
        }
        if (anyWord(message, "job", "jobs", "search", "browse", "find work", "vacancy")) {
            return "Open Job Board after you’re logged in to browse listings. Use the search filters to narrow things down.";
        }
        if (anyWord(message, "apply", "application", "submit")) {
            return "Students: pick a job on the board and apply from there. You can track things from your student dashboard.";
        }
        if (anyWord(message, "employer", "company", "hire", "recruit")) {
            return "Employer accounts can post roles and manage applicants from the employer dashboard.";
        }
        if (anyWord(message, "post job", "posting", "list job")) {
            return "From your employer dashboard, use the flow to publish a new job. Fill in role details so students know what you need.";
        }
        if (anyWord(message, "student", "profile", "resume", "cv")) {
            return "Students can open their profile from the dashboard to keep details tidy before applying.";
        }
        if (anyWord(message, "dashboard")) {
            return "Your dashboard is your home base after login—links depend on whether you’re a student or employer.";
        }
        if (anyWord(message, "logout", "sign out")) {
            return "Use the Logout control in the navbar when you’re done. You can always come back anytime.";
        }
        if (anyWord(message, "thank", "thanks", "ty")) {
            return "You’re very welcome—good luck with your search or hiring.";
        }

        return "I’m not sure about that one yet. Try asking how to browse jobs, apply, register, or post a role—or say “help” for ideas.";
    }

    private static boolean anyWord(String haystack, String... needles) {
        for (String n : needles) {
            if (haystack.contains(n)) {
                return true;
            }
        }
        return false;
    }
}
