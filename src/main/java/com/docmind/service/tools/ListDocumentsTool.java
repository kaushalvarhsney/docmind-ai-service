package com.docmind.service.tools;

import com.docmind.model.DocumentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Description("List all documents currently uploaded in the system with their names and chunk counts.")
@Slf4j
public class ListDocumentsTool
        implements Function<ListDocumentsTool.Request,
        String> {

    private final DocumentRepository documentRepository;

    public ListDocumentsTool(
            DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public record Request(String input) {}

    @Override
    public String apply(Request request) {
        log.info("Agent calling ListDocumentsTool");

        var documents = documentRepository.findAll();

        if (documents.isEmpty()) {
            return "No documents uploaded yet.";
        }

        return "Uploaded documents:\n"
                + documents.stream()
                .map(doc -> "- " + doc.getFileName()
                        + " (" + doc.getChunksCount()
                        + " chunks)")
                .collect(Collectors.joining("\n"));
    }
}