package com.docmind.controller;

import com.docmind.dto.ChatRequest;
import com.docmind.dto.ChatResponse;
import com.docmind.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/ask")
    public ResponseEntity<ChatResponse> ask(
            @RequestBody ChatRequest request) {
        log.info("Received question: {}", request.getQuestion());
        ChatResponse response = chatService.ask(request.getQuestion());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("DocMind AI Service is running!");
    }
}