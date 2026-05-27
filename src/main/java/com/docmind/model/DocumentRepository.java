package com.docmind.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DocumentRepository
        extends JpaRepository<DocumentEntity, String> {

    Optional<DocumentEntity> findByFileName(String fileName);
    boolean existsByFileName(String fileName);
}