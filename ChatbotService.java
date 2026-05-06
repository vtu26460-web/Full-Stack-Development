package com.example.jobportal.controller;

import com.example.jobportal.service.ChatbotService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatbotController {

    private final ChatbotService chatbotService;

    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping
    public ReplyBody chat(@RequestBody MessageBody body) {
        String text = body != null ? body.message() : "";
        return new ReplyBody(chatbotService.getReply(text));
    }

    public record MessageBody(String message) {
    }

    public record ReplyBody(String reply) {
    }
}
