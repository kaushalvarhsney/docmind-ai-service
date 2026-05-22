package com.docmind.service;

import com.docmind.dto.ChatResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatModel chatModel;
    private final VectorStore vectorStore;

    @CircuitBreaker(name = "azureOpenAI", fallbackMethod = "fallback")
    @Retry(name = "azureOpenAI")
    public ChatResponse ask(String question) {
        log.info("Searching PGVector for: {}", question);

        // Step 1 — Search PGVector for relevant chunks
        List<Document> relevantDocs = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(question)
                        .topK(3)
                        .similarityThreshold(0.5)
                        .build()
        );

        log.info("Found {} relevant chunks", relevantDocs.size());

        // Step 2 — Build context from chunks
        String context = relevantDocs.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));

        // Step 3 — Build prompt with context
        String systemPrompt;
        if (!relevantDocs.isEmpty()) {
            systemPrompt = """
                You are DocMind — an intelligent document assistant.
                Answer the user's question based ONLY on the 
                context provided below.
                If the answer is not in the context, say 
                "I could not find this information in the 
                uploaded documents."
                
                Context from uploaded documents:
                """ + context;
        } else {
            systemPrompt = """
                You are DocMind — an intelligent document assistant.
                No relevant documents were found for this question.
                Please inform the user to upload relevant documents first.
                """;
        }

        // Step 4 — Call GPT-4o with context
        Prompt prompt = new Prompt(List.of(
                new SystemMessage(systemPrompt),
                new UserMessage(question)
        ));

        String answer = chatModel.call(prompt)
                .getResult()
                .getOutput()
                .getText();

        log.info("RAG answer generated successfully!");
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