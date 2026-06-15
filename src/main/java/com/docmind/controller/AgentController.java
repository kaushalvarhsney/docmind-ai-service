package com.docmind.controller;

import com.docmind.dto.AgentRequest;
import com.docmind.dto.AgentResponse;
import com.docmind.service.DocumentAgentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/agent")
@RequiredArgsConstructor
@Slf4j
public class AgentController {

    private final DocumentAgentService agentService;

    @PostMapping("/execute")
    public ResponseEntity<AgentResponse> execute(
            @RequestBody AgentRequest request) {
        log.info("Agent goal received: {}",
                request.getGoal());
        return ResponseEntity.ok(
                agentService.execute(request.getGoal()));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok(
                "DocMind Agent is ready!");
    }
}