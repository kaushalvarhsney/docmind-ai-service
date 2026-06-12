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

**An Intelligent Multi-Document Q&A and Agentic AI Microservice**
**powered by Spring AI 1.0 and Azure OpenAI GPT-4o**

*Upload documents · Ask questions · Let the Agent do the thinking!*

[Features](#-features) • [Architecture](#-architecture) • [Tech Stack](#-tech-stack) • [API Documentation](#-api-documentation) • [Roadmap](#-roadmap) • [Author](#-about-the-author)

</div>

---

## 🌟 Overview

**DocMind** is a production-grade intelligent microservice that evolves through two AI paradigms:

**RAG (Retrieval Augmented Generation)** — for direct, precise document Q&A.

**Agentic AI** — for autonomous, multi-step document analysis where GPT-4o decides which tools to invoke based on the user's goal — no hardcoded flow.

### The Problem it Solves:
> GPT-4o is powerful but doesn't know YOUR documents. DocMind bridges this gap — upload multiple PDFs and either ask precise questions via RAG or give the agent a complex goal and let it autonomously search, summarise, and analyse your documents.

---

## ✨ Features

### ✅ Phase 1 — Foundation
- 🤖 **AI-Powered Q&A** — Natural language questions answered by Azure OpenAI GPT-4o
- 📄 **PDF Document Upload** — Upload and process PDF documents
- 🔍 **RAG Pattern** — Retrieval Augmented Generation for document-aware answers
- 🧮 **Vector Embeddings** — text-embedding-ada-002 for semantic understanding
- 🗄️ **PGVector Storage** — PostgreSQL with HNSW indexing and cosine similarity
- 🔌 **Circuit Breaker** — Resilience4j protecting all Azure OpenAI calls
- 🔄 **Auto Retry** — Exponential backoff retry on transient failures
- 📊 **Swagger UI** — Interactive API documentation
- 📈 **Actuator Metrics** — Health checks and observability
- 🐳 **Docker** — PostgreSQL + PGVector via Docker

### ✅ Phase 2 — Multi-Document Support
- 📚 **Multi-Document Upload** — Upload and query across multiple PDFs simultaneously
- 🗂️ **Document Management** — List and delete uploaded documents
- 🏷️ **Document Metadata** — Each chunk tagged with source document name
- 🔍 **Cross-Document Search** — Answers drawn from all relevant documents
- ♻️ **Duplicate Detection** — Prevents uploading the same document twice
- 📝 **Dynamic System Prompt** — Context includes source document names

### ✅ Phase 3 — Agentic AI
- 🤖 **AI Agent** — GPT-4o autonomously decides which tools to invoke
- 🔧 **SearchDocumentTool** — Agent searches PGVector for relevant content
- 📋 **SummariseDocumentTool** — Agent summarises specific or all documents
- 📁 **ListDocumentsTool** — Agent lists all available documents
- 🧠 **Autonomous Reasoning** — ReAct loop: Reason → Act → Observe → Repeat
- 🎯 **Goal-Based Execution** — Give a goal, agent handles the rest
- 🔌 **Resilience4j on Agent** — Circuit Breaker protects all agent calls

### 🔲 Coming Soon — Phase 4
- 💬 **Chat History** — Multi-turn conversation memory
- 📌 **Source Citation** — Shows exactly which document answered
- 🔐 **Azure Key Vault** — Enterprise secret management
- 🚀 **Azure Container Apps** — Live cloud deployment
- 🧪 **Comprehensive Tests** — Unit and integration test coverage

---

## 🏗️ Architecture

```
┌────────────────────────────────────────────────────────────────┐
│                           Client                               │
│                  (Postman / Browser / App)                     │
└──────────────────────────┬─────────────────────────────────────┘
                           │
          ┌────────────────┼─────────────────┐
          ▼                ▼                 ▼
┌──────────────────┐ ┌──────────────┐ ┌──────────────────┐
│DocumentController│ │ChatController│ │ AgentController  │
│                  │ │              │ │                  │
│POST  /upload     │ │POST /ask     │ │POST /execute     │
│GET   /documents  │ │GET  /health  │ │GET  /health      │
│DELETE /documents │ │              │ │                  │
└────────┬─────────┘ └──────┬───────┘ └────────┬─────────┘
         │                  │                  │
         ▼                  ▼                  ▼
┌──────────────────┐ ┌──────────────┐ ┌──────────────────┐
│ DocumentService  │ │  ChatService │ │DocumentAgentSvc  │
│                  │ │              │ │                  │
│1. Duplicate check│ │1. PGVector   │ │ ChatClient with  │
│2. Read PDF       │ │   search     │ │ Tool Registry:   │
│3. Chunk text     │ │2. Build RAG  │ │                  │
│4. Tag metadata   │ │   context    │ │ SearchDocument   │
│5. Store PGVector │ │3. Call GPT4o │ │ SummariseDoc     │
│6. Save DB record │ │4. Return ans │ │ ListDocuments    │
└────────┬─────────┘ └──────┬───────┘ └────────┬─────────┘
         │                  │                  │
         └──────────────────┼──────────────────┘
                            │
┌───────────────────────────▼────────────────────────────────────┐
│                    Resilience4j Layer                           │
│      Circuit Breaker · Retry with Exponential Backoff          │
└───────────────────────────┬────────────────────────────────────┘
                            │
               ┌────────────┴────────────┐
               ▼                         ▼
┌──────────────────────┐     ┌───────────────────────────┐
│    Azure OpenAI      │     │   PostgreSQL + PGVector    │
│                      │     │                            │
│  GPT-4o              │     │  HNSW Index                │
│  Answer generation   │     │  Cosine Distance           │
│  Agent reasoning     │     │  1536 dimensions           │
│  Tool orchestration  │     │  Document metadata         │
│                      │     │  Document registry         │
│  Ada-002             │     │                            │
│  Vector embeddings   │     │                            │
└──────────────────────┘     └───────────────────────────┘
```

---

## 🤖 Agentic AI — How It Works

```
═══════════════════════════════════════════════
RAG Mode — Direct Q&A
═══════════════════════════════════════════════
User: "What are Kaushal's technical skills?"
         ↓
Hardcoded flow:
Search PGVector → Build context → GPT-4o
         ↓
Precise answer returned ✅


═══════════════════════════════════════════════
Agent Mode — Autonomous Goal Execution
═══════════════════════════════════════════════
User: "List all documents then summarise
       each one and highlight key topics"
         ↓
Agent REASONS:
"I need to list docs first,
 then summarise each one"
         ↓
Agent ACTS autonomously:
→ Calls ListDocumentsTool()
→ Calls SummariseDocumentTool("doc1.pdf")
→ Calls SummariseDocumentTool("doc2.pdf")
→ Synthesises comprehensive response
         ↓
Complete analysis delivered! ✅
No hardcoded steps — GPT-4o decided! 🤖
```

---

## 🛠️ Tech Stack

| Layer | Technology | Version | Purpose |
|---|---|---|---|
| **Language** | Java | 17 | Core language |
| **Framework** | Spring Boot | 3.3.5 | Application framework |
| **AI Integration** | Spring AI | 1.0.0 | AI abstraction layer |
| **Chat + Agent** | Azure OpenAI GPT-4o | Latest | Answer generation + Agent reasoning |
| **Embedding Model** | text-embedding-ada-002 | Latest | 1536-dim vector generation |
| **Vector Store** | PGVector (PostgreSQL) | Latest | Semantic similarity search |
| **Index Algorithm** | HNSW | — | Fast nearest-neighbour search |
| **Resilience** | Resilience4j | 2.1.0 | Circuit Breaker + Retry |
| **API Docs** | SpringDoc OpenAPI | 2.3.0 | Swagger UI |
| **Container** | Docker | Latest | PostgreSQL + PGVector |
| **Build Tool** | Maven | 3.9.x | Dependency management |
| **Cloud** | Azure | — | OpenAI services |

---

## 🤖 AI Models Used

### GPT-4o — Generative + Agent Model
```
Provider  : Azure OpenAI
Purpose   : RAG answer generation + Agentic tool orchestration
Strength  : Excellent reasoning, tool calling, document comprehension
Used for  : ChatService (RAG) + DocumentAgentService (Agent)
```

### text-embedding-ada-002 — Embedding Model
```
Provider   : Azure OpenAI
Purpose    : Convert text chunks and questions to semantic vectors
Dimensions : 1536
Used for   : PDF ingestion pipeline + similarity search queries
```

---

## 📚 API Documentation

### Interactive Swagger UI
```
http://localhost:8080/swagger-ui.html
```

---

### 📄 Document APIs

#### Upload Document
```http
POST /api/v1/documents/upload
Content-Type: multipart/form-data

Request: file = <your-pdf-file>

Responses:
SUCCESS  → { "chunksProcessed": 45, "status": "SUCCESS" }
DUPLICATE→ { "status": "DUPLICATE", "message": "Already uploaded!" }
FAILED   → { "status": "FAILED", "message": "Error: ..." }
```

#### List All Documents
```http
GET /api/v1/documents

Response:
[
  {
    "id": "uuid",
    "fileName": "document.pdf",
    "chunksCount": 45,
    "uploadedAt": "2026-05-27T10:30:00",
    "status": "PROCESSED"
  }
]
```

#### Delete Document
```http
DELETE /api/v1/documents/{id}

Response:
{ "fileName": "document.pdf", "status": "SUCCESS" }
```

---

### 🔍 RAG Chat APIs

#### Direct Q&A
```http
POST /api/v1/chat/ask
Content-Type: application/json

Request:  { "question": "What are the key skills?" }
Response: { "answer": "Based on documents...", "status": "SUCCESS" }
Fallback: { "answer": "Unavailable.", "status": "FALLBACK" }
```

---

### 🤖 Agent APIs

#### Execute Goal
```http
POST /api/v1/agent/execute
Content-Type: application/json

Request:
{
  "goal": "List all documents and summarise each one"
}

Response:
{
  "result": "I found 2 documents...[detailed analysis]",
  "status": "SUCCESS"
}
```

#### Example Goals:
```json
{ "goal": "List all uploaded documents" }
{ "goal": "Summarise all uploaded documents" }
{ "goal": "Search for Java experience and summarise findings" }
{ "goal": "List documents then search for technical skills and provide analysis" }
```

---

### 📊 Actuator APIs
```http
GET /actuator/health
GET /actuator/metrics
GET /actuator/circuitbreakers
```

---

## ⚡ Resilience Configuration

```yaml
resilience4j:
  circuitbreaker:
    instances:
      azureOpenAI:
        sliding-window-size: 10
        failure-rate-threshold: 50
        wait-duration-in-open-state: 30s
        permitted-calls-in-half-open-state: 3
        slow-call-rate-threshold: 80
        slow-call-duration-threshold: 5s
  retry:
    instances:
      azureOpenAI:
        max-attempts: 3
        wait-duration: 1s
        enable-exponential-backoff: true
        exponential-backoff-multiplier: 2
```

---

## 🗺️ Roadmap

### ✅ Phase 1 — Foundation (Complete!)
- [x] Spring Boot + Spring AI 1.0.0 setup
- [x] Azure OpenAI GPT-4o + Ada-002
- [x] PostgreSQL + PGVector via Docker
- [x] RAG-powered Q&A API
- [x] Resilience4j Circuit Breaker + Retry
- [x] Swagger UI + Actuator

### ✅ Phase 2 — Multi-Document (Complete!)
- [x] Multi-document upload and storage
- [x] Document metadata tagging
- [x] Cross-document similarity search
- [x] Document management API
- [x] Duplicate detection

### ✅ Phase 3 — Agentic AI (Complete!)
- [x] Spring AI Tool Calling integration
- [x] SearchDocumentTool
- [x] SummariseDocumentTool
- [x] ListDocumentsTool
- [x] Autonomous agent endpoint
- [x] Resilience4j on agent calls

### 🔲 Phase 4 — Production (Coming Soon!)
- [ ] Chat history and memory
- [ ] Source citation in answers
- [ ] Azure Key Vault
- [ ] Docker Compose full stack
- [ ] Azure Container Apps deployment
- [ ] Comprehensive test coverage

---

## 🔒 Security Notes

- API keys stored as **environment variables** — never hardcoded
- `.gitignore` excludes all sensitive configuration
- Azure Key Vault integration planned for Phase 4
- MIT License — open source with attribution

---

## 🤝 About the Author

**Kaushal Varshney**
*Technical Lead | Java Microservices Architect | 14+ Years Experience*

Passionate about bridging enterprise Java development with modern AI capabilities.

- 💼 [LinkedIn](https://www.linkedin.com/in/kaushal-varshney-5628b822)
- 🐙 [GitHub](https://github.com/kaushalvarshney)

> *"Started with RAG, evolved to Agentic AI — the same journey real enterprise AI products take."*

---

## ⭐ Support

- ⭐ **Star** this repository
- 🍴 **Fork** and build on it
- 💬 **Raise issues** for bugs or features
- 📢 **Share** with fellow Java developers

---

## 📄 License

MIT License — Copyright (c) 2026 Kaushal Varshney

---

<div align="center">

**Built with ❤️ using Spring AI + Azure OpenAI**

*From RAG to Agentic AI — Enterprise Java meets Modern AI* 🚀

</div>
