package com.docmind.controller;

import com.docmind.dto.DocumentDeleteResponse;
import com.docmind.dto.DocumentListResponse;
import com.docmind.dto.DocumentResponse;
import com.docmind.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
@Slf4j
public class DocumentController {

    private final DocumentService documentService;

    // Upload PDF
    @PostMapping("/upload")
    public ResponseEntity<DocumentResponse> upload(
            @RequestParam("file") MultipartFile file) {
        log.info("Uploading: {}", file.getOriginalFilename());
        return ResponseEntity.ok(
                documentService.processDocument(file));
    }

    // List all documents
    @GetMapping
    public ResponseEntity<List<DocumentListResponse>>
    getAllDocuments() {
        return ResponseEntity.ok(
                documentService.getAllDocuments());
    }

    // Delete document by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<DocumentDeleteResponse>
    deleteDocument(@PathVariable String id) {
        return ResponseEntity.ok(
                documentService.deleteDocument(id));
    }

    // Health check
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok(
                "Document Service is running!");
    }
}