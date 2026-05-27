package com.docmind.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentListResponse {
    private String id;
    private String fileName;
    private int chunksCount;
    private LocalDateTime uploadedAt;
    private String status;
}