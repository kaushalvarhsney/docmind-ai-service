package com.docmind.service;

import com.docmind.dto.AgentResponse;
import io.github.resilience4j.circuitbreaker
        .annotation.CircuitBreaker;
import io.github.resilience4j.retry
        .annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentAgentService {

    private final ChatModel chatModel;

    @CircuitBreaker(name = "azureOpenAI",
            fallbackMethod = "fallback")
    @Retry(name = "azureOpenAI")
    public AgentResponse execute(String goal) {
        log.info("Agent executing goal: {}", goal);

        // Build ChatClient with tools registered
        // LLM decides which tools to call!
        String result = ChatClient
                .builder(chatModel)
                .build()
                .prompt()
                .system("""
                        You are DocMind — an intelligent
                        document analysis agent.
                        
                        You have access to tools to:
                        - Search documents for information
                        - Summarise documents
                        - List available documents
                        
                        Use these tools to achieve the
                        user's goal. Be thorough and 
                        precise in your analysis.
                        Always cite which document 
                        your information comes from.
                        """)
                .user(goal)
                .functions(
                        "searchDocumentTool",
                        "summariseDocumentTool",
                        "listDocumentsTool"
                )
                .call()
                .content();

        log.info("Agent completed goal successfully!");
        return new AgentResponse(result, "SUCCESS");
    }

    public AgentResponse fallback(
            String goal, Exception ex) {
        log.error("Agent circuit breaker: {}",
                ex.getMessage());
        return new AgentResponse(
                "Agent temporarily unavailable. " +
                        "Please try again.",
                "FALLBACK"
        );
    }
}