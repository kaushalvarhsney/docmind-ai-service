# 🤖 DocMind AI Service

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen?style=for-the-badge&logo=springboot)
![Spring AI](https://img.shields.io/badge/Spring%20AI-1.0.0-green?style=for-the-badge&logo=spring)
![Azure OpenAI](https://img.shields.io/badge/Azure%20OpenAI-GPT--4o-blue?style=for-the-badge&logo=microsoftazure)
![PGVector](https://img.shields.io/badge/PGVector-PostgreSQL-336791?style=for-the-badge&logo=postgresql)
![Resilience4j](https://img.shields.io/badge/Resilience4j-2.1.0-yellow?style=for-the-badge)
![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker)
![License](https://img.shields.io/badge/License-MIT-purple?style=for-the-badge)

**An Intelligent Document Q&A Microservice powered by Spring AI and Azure OpenAI GPT-4o**

*Upload your documents — Ask questions — Get AI-powered answers!*

[Features](#-features) • [Architecture](#-architecture) • [Tech Stack](#-tech-stack) • [API Documentation](#-api-documentation) • [Roadmap](#-roadmap) • [Author](#-about-the-author)

</div>

---

## 🌟 Overview

**DocMind** is a production-grade intelligent microservice that implements the **RAG (Retrieval Augmented Generation)** pattern using **Spring AI 1.0**, **Azure OpenAI GPT-4o**, and **PostgreSQL with PGVector**.

### The Problem it Solves:
> GPT-4o is powerful but doesn't know YOUR documents. DocMind bridges this gap — upload any PDF and ask questions in natural language to get accurate, document-aware AI answers!

### How it Works:
1. 📄 **Upload PDF** → Spring AI reads and chunks the document
2. 🔢 **Generate Embeddings** → Azure OpenAI Ada-002 converts chunks to 1536-dimension vectors
3. 🗄️ **Store in PGVector** → Vectors stored in PostgreSQL with HNSW index
4. ❓ **Ask Question** → Question converted to vector → Similarity search finds relevant chunks
5. 🤖 **Get Answer** → GPT-4o answers from YOUR document context!

---

## ✨ Features

- 🤖 **AI-Powered Q&A** — Natural language questions answered by Azure OpenAI GPT-4o
- 📄 **PDF Document Upload** — Upload and process PDF documents
- 🔍 **RAG Pattern** — Retrieval Augmented Generation for document-aware answers
- 🧮 **Vector Embeddings** — text-embedding-ada-002 for semantic understanding
- 🗄️ **PGVector Storage** — PostgreSQL vector store with HNSW indexing
- 🔌 **Circuit Breaker** — Resilience4j protecting AI API calls
- 🔄 **Auto Retry** — Exponential backoff retry on transient failures
- 📊 **Swagger UI** — Interactive API documentation
- 📈 **Actuator Metrics** — Health checks and metrics
- 🐳 **Docker Ready** — PostgreSQL + PGVector via Docker

---

## 🏗️ Architecture

```
┌──────────────────────────────────────────────────────────┐
│                     Client                               │
│              (Postman / Browser / App)                   │
└────────────────────────┬─────────────────────────────────┘
                         │
              ┌──────────┴──────────┐
              │                     │
              ▼                     ▼
┌─────────────────────┐ ┌─────────────────────────┐
│  DocumentController  │ │     ChatController       │
│  POST /documents     │ │  POST /chat/ask          │
│  /upload             │ │  GET  /chat/health       │
└──────────┬──────────┘ └────────────┬────────────┘
           │                         │
           ▼                         ▼
┌─────────────────────┐ ┌─────────────────────────┐
│   DocumentService   │ │      ChatService         │
│                     │ │                          │
│ 1. Read PDF         │ │ 1. Search PGVector       │
│ 2. Chunk text       │ │ 2. Build RAG prompt      │
│ 3. Generate vectors │ │ 3. Call GPT-4o           │
│ 4. Store PGVector   │ │ 4. Return answer         │
└──────────┬──────────┘ └────────────┬────────────┘
           │                         │
           │         ┌───────────────┘
           ▼         ▼
┌─────────────────────────────────────────────────┐
│              Resilience4j Layer                  │
│   Circuit Breaker (50% threshold, 30s wait)      │
│   Retry (3 attempts, exponential backoff)        │
└──────────────────────┬──────────────────────────┘
                       │
          ┌────────────┴────────────┐
          ▼                         ▼
┌──────────────────┐     ┌──────────────────────┐
│  Azure OpenAI    │     │   PostgreSQL          │
│                  │     │   + PGVector          │
│  GPT-4o          │     │                       │
│  (Chat)          │     │  HNSW Index           │
│                  │     │  Cosine Distance      │
│  Ada-002         │     │  1536 dimensions      │
│  (Embeddings)    │     │                       │
└──────────────────┘     └──────────────────────┘
```

---

## 🛠️ Tech Stack

| Layer | Technology | Version | Purpose |
|---|---|---|---|
| **Language** | Java | 17 | Core language |
| **Framework** | Spring Boot | 3.3.5 | Application framework |
| **AI Integration** | Spring AI | 1.0.0 | AI abstraction layer |
| **Chat Model** | Azure OpenAI GPT-4o | Latest | Answer generation |
| **Embedding Model** | text-embedding-ada-002 | Latest | Vector generation |
| **Vector Store** | PGVector (PostgreSQL) | Latest | Semantic search |
| **Index Algorithm** | HNSW | - | Fast similarity search |
| **Resilience** | Resilience4j | 2.1.0 | Circuit Breaker + Retry |
| **API Docs** | SpringDoc OpenAPI | 2.3.0 | Swagger UI |
| **Container** | Docker | Latest | PostgreSQL + PGVector |
| **Build Tool** | Maven | 3.9.x | Dependency management |
| **Cloud** | Azure | - | OpenAI hosting |

---

## 🤖 AI Models Used

### 1. GPT-4o (Chat Model)
```
Provider: Azure OpenAI
Purpose: Generate natural language answers
Input: Question + document context chunks
Output: Accurate, context-aware answers
Strength: Excellent reasoning and comprehension
```

### 2. text-embedding-ada-002 (Embedding Model)
```
Provider: Azure OpenAI
Purpose: Convert text to 1536-dimension vectors
Input: Text chunks from PDF / User questions
Output: Numerical vector representation
Strength: Captures semantic meaning of text
Dimensions: 1536
```

---

## 📚 API Documentation

### Interactive Swagger UI
```
http://localhost:8080/swagger-ui.html
```

### Endpoints

#### 🟢 Chat Health Check
```http
GET /api/v1/chat/health
Response: "DocMind AI Service is running!"
```

#### 🤖 RAG-Powered Q&A
```http
POST /api/v1/chat/ask
Content-Type: application/json

Request:
{
    "question": "What is this document about?"
}

Success Response:
{
    "answer": "Based on the uploaded document...",
    "status": "SUCCESS"
}

Fallback Response (Circuit Open):
{
    "answer": "Service temporarily unavailable.",
    "status": "FALLBACK"
}
```

#### 📄 Document Upload
```http
POST /api/v1/documents/upload
Content-Type: multipart/form-data

Request: file=<your-pdf-file>

Success Response:
{
    "fileName": "document.pdf",
    "chunksProcessed": 45,
    "status": "SUCCESS",
    "message": "Document processed and stored successfully!"
}
```

#### 🟢 Document Health Check
```http
GET /api/v1/documents/health
Response: "Document Service is running!"
```

#### 📊 Actuator Endpoints
```http
GET /actuator/health
GET /actuator/metrics
GET /actuator/circuitbreakers
```

## 🗺️ Roadmap

### ✅ Phase 1 — Foundation (Complete!)
- [x] Spring Boot 3.3.5 + Spring AI 1.0.0 setup
- [x] Azure OpenAI GPT-4o integration
- [x] text-embedding-ada-002 integration
- [x] PostgreSQL + PGVector via Docker
- [x] PDF upload and chunking (TokenTextSplitter)
- [x] Vector embedding and storage
- [x] RAG-powered Q&A API
- [x] Resilience4j Circuit Breaker + Retry
- [x] Swagger UI documentation
- [x] Spring Actuator metrics
- [x] GitHub repository + MIT License

### 🔲 Phase 2 — Enhanced Features (Coming Soon!)
- [ ] Multi-document support
- [ ] Document management (list, delete)
- [ ] Chat history and conversation memory
- [ ] Source citation in answers
- [ ] CQRS pattern for read/write separation

### 🔲 Phase 3 — Production Ready (Coming Soon!)
- [ ] Docker Compose with full stack
- [ ] Azure Container Apps deployment
- [ ] Rate Limiter implementation
- [ ] Bulkhead pattern
- [ ] Comprehensive test coverage
- [ ] CI/CD pipeline

---

## 🔒 Security

- API keys stored as **environment variables** — never hardcoded!
- `.gitignore` configured to exclude sensitive files
- MIT License — open source with attribution required

---

## 🤝 About the Author

**Kaushal Varshney**
*Technical Lead | Java Microservices Architect | 14+ Years Experience*

Passionate about building enterprise-grade Java applications and bridging the gap between traditional backend development and modern AI capabilities.

- 💼 [LinkedIn](https://www.linkedin.com/in/kaushalvarshney)
- 🐙 [GitHub](https://github.com/kaushalvarshney)

> *"Built this project to demonstrate that Java developers can leverage cutting-edge AI capabilities using familiar Spring ecosystem tools — without switching languages or frameworks!"*

---

## ⭐ Support

If you find DocMind useful:
- ⭐ **Star** this repository
- 🍴 **Fork** and contribute
- 📢 **Share** with fellow Java developers
- 💬 **Raise issues** for bugs or feature requests

---

## 📄 License

This project is licensed under the **MIT License** — see [LICENSE](LICENSE) for details.

Copyright (c) 2026 Kaushal Varshney

---

<div align="center">

**Built with ❤️ using Spring AI + Azure OpenAI**

*Bridging Enterprise Java with Modern AI* 🚀

</div>
