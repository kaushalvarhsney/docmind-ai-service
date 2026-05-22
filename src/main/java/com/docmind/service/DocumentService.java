package com.docmind.service;

import com.docmind.dto.DocumentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

    private final VectorStore vectorStore;

    public DocumentResponse processDocument(MultipartFile file) {
        try {
            log.info("Processing document: {}", file.getOriginalFilename());

            // Step 1 — Read PDF
            Resource resource = file.getResource();
            PagePdfDocumentReader pdfReader =
                    new PagePdfDocumentReader(resource);
            List<Document> documents = pdfReader.get();
            log.info("PDF pages read: {}", documents.size());

            // Step 2 — Split into chunks
            TokenTextSplitter splitter = new TokenTextSplitter();
            List<Document> chunks = splitter.apply(documents);
            log.info("Chunks created: {}", chunks.size());

            // Step 3 — Store in PGVector
            // (Embeddings generated automatically by Spring AI!)
            vectorStore.add(chunks);
            log.info("Chunks stored in PGVector successfully!");

            return new DocumentResponse(
                    file.getOriginalFilename(),
                    chunks.size(),
                    "SUCCESS",
                    "Document processed and stored successfully!"
            );

        } catch (Exception e) {
            log.error("Error processing document: {}", e.getMessage());
            return new DocumentResponse(
                    file.getOriginalFilename(),
                    0,
                    "FAILED",
                    "Error: " + e.getMessage()
            );
        }
    }
}