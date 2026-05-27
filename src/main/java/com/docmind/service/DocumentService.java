package com.docmind.service;

import com.docmind.dto.DocumentDeleteResponse;
import com.docmind.dto.DocumentListResponse;
import com.docmind.dto.DocumentResponse;
import com.docmind.model.DocumentEntity;
import com.docmind.model.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

    private final VectorStore vectorStore;
    private final DocumentRepository documentRepository;

    @Transactional
    public DocumentResponse processDocument(MultipartFile file) {
        try {
            log.info("Processing document: {}", file.getOriginalFilename());

            // Check if document already exists
            if (documentRepository.existsByFileName(file.getOriginalFilename())) {
                return new DocumentResponse(file.getOriginalFilename(), 0, "DUPLICATE", "Document already uploaded!");
            }

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

            // Step 3 — Add metadata to each chunk
            // This is the KEY for multi-document support!
            String fileName = file.getOriginalFilename();
            chunks.forEach(chunk -> {
                Map<String, Object> metadata = new HashMap<>(chunk.getMetadata());
                metadata.put("fileName", fileName);
                metadata.put("uploadedAt", LocalDateTime.now().toString());
                chunk.getMetadata().putAll(metadata);
            });

            // Step 4 — Store in PGVector with metadata
            vectorStore.add(chunks);
            log.info("Chunks stored in PGVector successfully!");

            // Step 5 — Save document record in DB
            DocumentEntity entity = DocumentEntity.builder().fileName(fileName).fileType("PDF").chunksCount(chunks.size()).uploadedAt(LocalDateTime.now()).status("PROCESSED").build();
            documentRepository.save(entity);

            return new DocumentResponse(fileName, chunks.size(), "SUCCESS", "Document processed successfully!");

        } catch (Exception e) {
            log.error("Error processing document: {}", e.getMessage());
            return new DocumentResponse(file.getOriginalFilename(), 0, "FAILED", "Error: " + e.getMessage());
        }
    }

    public List<DocumentListResponse> getAllDocuments() {
        return documentRepository.findAll().stream().map(doc -> new DocumentListResponse(doc.getId(), doc.getFileName(), doc.getChunksCount(), doc.getUploadedAt(), doc.getStatus())).collect(Collectors.toList());
    }

    @Transactional
    public DocumentDeleteResponse deleteDocument(String id) {
        try {
            DocumentEntity doc = documentRepository.findById(id).orElseThrow(() -> new RuntimeException("Document not found!"));

            // Delete from PGVector using metadata filter
            vectorStore.delete(
                    "fileName == '" + doc.getFileName() + "'"
            );

            // Delete from documents table
            documentRepository.deleteById(id);

            log.info("Document deleted: {}", doc.getFileName());

            return new DocumentDeleteResponse(doc.getFileName(), "SUCCESS", "Document deleted successfully!");

        } catch (Exception e) {
            log.error("Error deleting document: {}", e.getMessage());
            return new DocumentDeleteResponse(id, "FAILED", "Error: " + e.getMessage());
        }
    }
}