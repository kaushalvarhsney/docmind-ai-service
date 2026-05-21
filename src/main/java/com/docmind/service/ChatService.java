package com.docmind.service;

import com.docmind.dto.ChatResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatModel chatModel;

    @CircuitBreaker(name = "azureOpenAI", fallbackMethod = "fallback")
    @Retry(name = "azureOpenAI")
    public ChatResponse ask(String question) {
        log.info("Calling Azure OpenAI with question: {}", question);

        Prompt prompt = new Prompt(new UserMessage(question));
        String answer = chatModel.call(prompt)
                .getResult()
                .getOutput()
                .getText();

        log.info("Received answer from Azure OpenAI");
        return new ChatResponse(answer, "SUCCESS");
    }

    public ChatResponse fallback(String question, Exception ex) {
        log.error("Circuit breaker triggered: {}", ex.getMessage());
        return new ChatResponse(
                "Service temporarily unavailable. Please try again.",
                "FALLBACK"
        );
    }
}