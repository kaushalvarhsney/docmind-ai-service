package com.docmind.service.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Description("Summarise content of a specific document or all documents. Use document name or all.")
@Slf4j
public class SummariseDocumentTool
        implements Function<SummariseDocumentTool.Request,
        String> {

    private final VectorStore vectorStore;

    public SummariseDocumentTool(
            VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public record Request(String documentName) {}

    @Override
    public String apply(Request request) {
        log.info("Agent SummariseDocumentTool: {}",
                request.documentName());

        SearchRequest searchRequest;

        if ("all".equalsIgnoreCase(
                request.documentName())) {
            searchRequest = SearchRequest.builder()
                    .query("document summary overview")
                    .topK(10)
                    .similarityThreshold(0.0)
                    .build();
        } else {
            searchRequest = SearchRequest.builder()
                    .query("summary of "
                            + request.documentName())
                    .topK(8)
                    .similarityThreshold(0.1)
                    .filterExpression("fileName == '"
                            + request.documentName() + "'")
                    .build();
        }

        List<Document> results =
                vectorStore.similaritySearch(searchRequest);

        if (Objects.isNull(results) || results.isEmpty()) {
            return "No content found for: "
                    + request.documentName();
        }

        return "Content from ["
                + request.documentName() + "]:\n\n"
                + results.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));
    }
}