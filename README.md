# 🤖 DocMind AI Service

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen?style=for-the-badge&logo=springboot)
![Spring AI](https://img.shields.io/badge/Spring%20AI-1.0.0-green?style=for-the-badge&logo=spring)
![Azure OpenAI](https://img.shields.io/badge/Azure%20OpenAI-GPT--4o-blue?style=for-the-badge&logo=microsoftazure)
![Resilience4j](https://img.shields.io/badge/Resilience4j-2.1.0-yellow?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-purple?style=for-the-badge)

**An intelligent Document Q&A Microservice powered by Spring AI and Azure OpenAI GPT-4o**

*Ask questions in natural language — get AI-powered answers from your documents!*

[Features](#-features) • [Architecture](#-architecture) • [Tech Stack](#-tech-stack) • [API Documentation](#-api-documentation) • [Roadmap](#-roadmap)

</div>

---

## 🌟 Overview

**DocMind** is a production-grade intelligent microservice that combines the power of **Spring AI 1.0**, **Azure OpenAI GPT-4o**, and **RAG (Retrieval Augmented Generation)** pattern to enable natural language Q&A over your own documents.

Upload a PDF → Ask any question → Get accurate AI-powered answers from your document content!

> *"Built to demonstrate real-world AI integration in enterprise Java microservices — combining modern Spring AI with production-grade resilience patterns."*

---

## ✨ Features

- 🤖 **AI-Powered Q&A** — Natural language questions answered by Azure OpenAI GPT-4o
- 📄 **PDF Document Upload** — Upload and process PDF documents *(coming soon)*
- 🔍 **RAG Pattern** — Retrieval Augmented Generation for document-aware answers *(coming soon)*
- 🔌 **Circuit Breaker** — Resilience4j Circuit Breaker protecting AI API calls
- 🔄 **Auto Retry** — Exponential backoff retry on transient failures
- 📊 **Swagger UI** — Interactive API documentation
- 📈 **Actuator Metrics** — Health checks and metrics out of the box
- 🐳 **Docker Ready** — Containerised deployment *(coming soon)*
- ☁️ **Azure Deployment** — Cloud-native deployment *(coming soon)*

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────┐
│                    Client                           │
│              (Postman / Browser)                    │
└─────────────────────┬───────────────────────────────┘
                      │ HTTP Request
                      ▼
┌─────────────────────────────────────────────────────┐
│               ChatController                        │
│          POST /api/v1/chat/ask                      │
│          GET  /api/v1/chat/health                   │
└─────────────────────┬───────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────┐
│            Resilience4j Layer                       │
│   ┌─────────────────┐   ┌─────────────────────┐    │
│   │ Circuit Breaker │   │    Retry (3 attempts │    │
│   │ (50% threshold) │   │  exponential backoff)│    │
│   └─────────────────┘   └─────────────────────┘    │
└─────────────────────┬───────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────┐
│               ChatService                           │
│         Spring AI ChatModel                         │
└─────────────────────┬───────────────────────────────┘
                      │
          ┌───────────┼───────────┐
          ▼           ▼           ▼
   ┌────────────┐ ┌────────┐ ┌──────────┐
   │Azure OpenAI│ │PGVector│ │  PDF     │
   │  GPT-4o   │ │  Store │ │ Reader   │
   │ (Active)  │ │(Soon)  │ │ (Soon)   │
   └────────────┘ └────────┘ └──────────┘
```

---

## 🛠️ Tech Stack

| Layer | Technology | Version |
|---|---|---|
| **Language** | Java | 17 |
| **Framework** | Spring Boot | 3.3.5 |
| **AI Integration** | Spring AI | 1.0.0 |
| **LLM Provider** | Azure OpenAI GPT-4o | Latest |
| **Vector Store** | PGVector (PostgreSQL) | Coming Soon |
| **PDF Processing** | Spring AI PDF Reader | 1.0.0 |
| **Resilience** | Resilience4j | 2.1.0 |
| **API Docs** | SpringDoc OpenAPI | 2.3.0 |
| **Build Tool** | Maven | 3.9.x |
| **Containerisation** | Docker | Coming Soon |
| **Cloud** | Azure Container Apps | Coming Soon |

---

## 📚 API Documentation

### Interactive Swagger UI
```
http://localhost:8080/swagger-ui.html
```

### Endpoints

#### 🟢 Health Check
```http
GET /api/v1/chat/health
```

**Response:**
```
DocMind AI Service is running!
```

---

#### 🤖 Ask a Question
```http
POST /api/v1/chat/ask
Content-Type: application/json
```

**Request Body:**
```json
{
    "question": "What is Spring Boot and why is it popular?"
}
```

**Success Response:**
```json
{
    "answer": "Spring Boot is an open-source Java framework...",
    "status": "SUCCESS"
}
```

**Fallback Response (Circuit Open):**
```json
{
    "answer": "Service temporarily unavailable. Please try again.",
    "status": "FALLBACK"
}
```

---

#### 📊 Health & Metrics
```http
GET /actuator/health
GET /actuator/metrics
GET /actuator/circuitbreakers
```

---

## ⚡ Resilience Configuration

DocMind implements production-grade resilience using **Resilience4j**:

### Circuit Breaker
```yaml
resilience4j:
  circuitbreaker:
    instances:
      azureOpenAI:
        sliding-window-size: 10        # Monitor last 10 calls
        failure-rate-threshold: 50     # Open if 50% fail
        wait-duration-in-open-state: 30s
        permitted-calls-in-half-open-state: 3
        slow-call-rate-threshold: 80
        slow-call-duration-threshold: 5s
```

### Retry
```yaml
resilience4j:
  retry:
    instances:
      azureOpenAI:
        max-attempts: 3
        wait-duration: 1s
        enable-exponential-backoff: true
        exponential-backoff-multiplier: 2  # 1s → 2s → 4s
```

---

## 🗺️ Roadmap

### ✅ Phase 1 — Foundation (Completed!)
- [x] Spring Boot 3.3.5 setup
- [x] Spring AI 1.0.0 integration
- [x] Azure OpenAI GPT-4o connection
- [x] REST API with Chat endpoint
- [x] Resilience4j Circuit Breaker + Retry
- [x] Swagger UI documentation
- [x] Spring Actuator metrics
- [x] GitHub repository

### 🔲 Phase 2 — RAG Implementation (Coming Soon)
- [ ] PDF document upload API
- [ ] PostgreSQL + PGVector setup
- [ ] Document chunking and embedding
- [ ] Vector similarity search
- [ ] RAG-powered document Q&A

### 🔲 Phase 3 — Production Ready (Coming Soon)
- [ ] Docker containerisation
- [ ] Docker Compose with PostgreSQL
- [ ] Azure Container Apps deployment
- [ ] Rate Limiter implementation
- [ ] Bulkhead pattern
- [ ] Comprehensive test coverage

---

## 🔒 Security Notes

- API keys stored as **environment variables** — never hardcoded!
- `.gitignore` configured to exclude sensitive files
- Azure Key Vault integration planned for Phase 3

---

## 🤝 About the Author

**Kaushal Varshney**
*Technical Lead | Java Microservices Architect | 14+ Years Experience*

- 💼 [LinkedIn](https://www.linkedin.com/in/kaushalvarshney)
- 🐙 [GitHub](https://github.com/kaushalvarshney)

*Building this project as part of my AI integration upskilling journey — combining 14+ years of enterprise Java experience with modern AI capabilities.*

---

## 📄 License

This project is licensed under the MIT License.

---

<div align="center">

**⭐ Star this repo if you find it useful!**

*Built with ❤️ using Spring AI + Azure OpenAI*

</div>