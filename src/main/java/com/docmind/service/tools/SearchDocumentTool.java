package com.docmind.service.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Description("Search uploaded documents for relevant information based on a query")
@Slf4j
public class SearchDocumentTool
        implements Function<SearchDocumentTool.Request,
        String> {

    private final VectorStore vectorStore;

    public SearchDocumentTool(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public record Request(String query) {}

    @Override
    public String apply(Request request) {
        log.info("Agent SearchDocumentTool: {}",
                request.query());

        List<Document> results =
                vectorStore.similaritySearch(
                        SearchRequest.builder()
                                .query(request.query())
                                .topK(5)
                                .similarityThreshold(0.3)
                                .build()
                );

        if (results.isEmpty()) {
            return "No relevant documents found for: "
                    + request.query();
        }

        return results.stream()
                .map(doc -> {
                    String fileName = (String) doc
                            .getMetadata()
                            .getOrDefault("fileName", "Unknown");
                    return "Source: [" + fileName + "]\n"
                            + doc.getText();
                })
                .collect(Collectors.joining("\n\n---\n\n"));
    }
}