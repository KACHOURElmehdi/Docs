# DocClassifier Backend Documentation

## 📋 Table of Contents

1. [Overview](#overview)
2. [Architecture](#architecture)
3. [Technology Stack](#technology-stack)
4. [Project Structure](#project-structure)
5. [Database Schema](#database-schema)
6. [API Reference](#api-reference)
7. [Authentication & Security](#authentication--security)
8. [Document Processing Pipeline](#document-processing-pipeline)
9. [Real-time Updates (SSE)](#real-time-updates-sse)
10. [Configuration](#configuration)
11. [Design Patterns](#design-patterns)
12. [Deployment](#deployment)

---

## Overview

**DocClassifier** is an intelligent document classification platform that automatically categorizes documents using OCR and AI-powered classification. The backend is built with **Spring Boot 3** and provides a RESTful API for document management, user authentication, and real-time processing updates.

### Key Features
- JWT-based authentication
- Asynchronous document processing
- OCR text extraction
- AI-powered auto-classification
- Real-time status updates via SSE
- Full-text search
- Audit logging
- Role-based access control (USER/ADMIN)

---

## Architecture

### Layered Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                      PRESENTATION LAYER                      │
│                        (Controllers)                         │
│  AuthenticationController, DocumentController, etc.          │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                       SERVICE LAYER                          │
│                    (Business Logic)                          │
│  DocumentService, AuthenticationService, PipelineService     │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      REPOSITORY LAYER                        │
│                      (Data Access)                           │
│  DocumentRepository, UserRepository, CategoryRepository      │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                       ENTITY LAYER                           │
│                    (Domain Models)                           │
│  Document, User, Category, Tag, AuditLog                     │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                        DATABASE                              │
│                         (MySQL)                              │
└─────────────────────────────────────────────────────────────┘
```

### Request Flow

```
HTTP Request
     │
     ▼
┌─────────────────┐
│ Security Filter │ ──► JWT Validation
└─────────────────┘
     │
     ▼
┌─────────────────┐
│   Controller    │ ──► HTTP handling, validation
└─────────────────┘
     │
     ▼
┌─────────────────┐
│    Service      │ ──► Business logic, orchestration
└─────────────────┘
     │
     ▼
┌─────────────────┐
│   Repository    │ ──► Data access
└─────────────────┘
     │
     ▼
┌─────────────────┐
│    Database     │
└─────────────────┘
```

---

## Technology Stack

| Component | Technology | Version |
|-----------|------------|---------|
| **Language** | Java | 17+ |
| **Framework** | Spring Boot | 3.x |
| **Security** | Spring Security + JWT | - |
| **ORM** | Spring Data JPA / Hibernate | - |
| **Database** | MySQL | 8.0+ |
| **Build Tool** | Maven | - |
| **Containerization** | Docker | - |
| **Code Generation** | Lombok | - |

### Dependencies
- `spring-boot-starter-web` - REST API
- `spring-boot-starter-security` - Security
- `spring-boot-starter-data-jpa` - Database access
- `jjwt` - JWT handling
- `mysql-connector-java` - MySQL driver
- `lombok` - Code generation

---

## Project Structure

```
backend/
├── src/main/java/com/mk/docclassifier/
│   ├── DocClassifierApplication.java     # Main entry point
│   ├── config/
│   │   ├── ApplicationConfig.java        # Bean configurations
│   │   ├── AsyncConfig.java              # Async processing config
│   │   ├── SecurityConfig.java           # Security & CORS config
│   │   └── WebConfig.java                # MVC configuration
│   ├── controller/
│   │   ├── AuthenticationController.java # Login/Register
│   │   ├── DocumentController.java       # Document CRUD
│   │   ├── CategoryController.java       # Category management
│   │   ├── TagController.java            # Tag management
│   │   ├── StatsController.java          # Dashboard statistics
│   │   ├── SseController.java            # Real-time events
│   │   └── AdminController.java          # Admin operations
│   ├── domain/entity/
│   │   ├── User.java                     # User entity
│   │   ├── Document.java                 # Document entity
│   │   ├── Category.java                 # Category entity
│   │   ├── Tag.java                      # Tag entity
│   │   ├── AuditLog.java                 # Audit logging
│   │   ├── DocumentStatus.java           # Status enum
│   │   └── Role.java                     # Role enum
│   ├── dto/
│   │   ├── AuthenticationRequest.java    # Login request
│   │   ├── AuthenticationResponse.java   # Login response (JWT)
│   │   ├── RegisterRequest.java          # Registration request
│   │   ├── CategoryRequest.java          # Category DTO
│   │   └── TagRequest.java               # Tag DTO
│   ├── repository/
│   │   ├── UserRepository.java
│   │   ├── DocumentRepository.java
│   │   ├── CategoryRepository.java
│   │   ├── TagRepository.java
│   │   └── AuditLogRepository.java
│   ├── security/
│   │   ├── JwtAuthenticationFilter.java  # JWT filter
│   │   └── JwtService.java               # JWT generation/validation
│   ├── service/
│   │   ├── AuthenticationService.java
│   │   ├── DocumentService.java
│   │   ├── PipelineService.java          # Document processing
│   │   ├── OcrService.java               # Text extraction
│   │   ├── ClassificationService.java    # AI classification
│   │   ├── StorageService.java           # File storage
│   │   ├── SseService.java               # Real-time events
│   │   └── impl/                         # Implementations
│   └── exception/
│       └── GlobalExceptionHandler.java   # Error handling
└── src/main/resources/
    ├── application.properties            # Configuration
    └── data.sql                          # Seed data
```

---

## Database Schema

### Entity Relationship Diagram

```
┌──────────────┐       ┌──────────────┐       ┌──────────────┐
│    users     │       │  documents   │       │  categories  │
├──────────────┤       ├──────────────┤       ├──────────────┤
│ id (PK)      │──┐    │ id (PK)      │    ┌──│ id (PK)      │
│ full_name    │  │    │ filename     │    │  │ name         │
│ email        │  │    │ original_    │    │  │ description  │
│ password     │  │    │   filename   │    │  │ color        │
│ role         │  └───►│ user_id (FK) │    │  │ created_by   │
└──────────────┘       │ category_id  │◄───┘  │ created_at   │
                       │   (FK)       │       │ updated_at   │
                       │ status       │       └──────────────┘
                       │ ocr_text     │
                       │ confidence   │       ┌──────────────┐
                       │ uploaded_at  │       │    tags      │
                       │ processed_at │       ├──────────────┤
                       └──────────────┘       │ id (PK)      │
                              │               │ name         │
                              │               │ color        │
                       ┌──────┴──────┐        │ created_by   │
                       │             │        └──────────────┘
                       ▼             │               ▲
                ┌──────────────┐     │               │
                │document_tags │     │               │
                ├──────────────┤     │               │
                │ document_id  │◄────┘               │
                │ tag_id       │─────────────────────┘
                └──────────────┘

┌──────────────┐
│  audit_logs  │
├──────────────┤
│ id (PK)      │
│ document_id  │
│ action       │
│ details      │
│ username     │
│ timestamp    │
└──────────────┘
```

### Entities

#### User
| Field | Type | Description |
|-------|------|-------------|
| id | Long | Primary key |
| fullName | String | User's full name |
| email | String | Unique email (username) |
| password | String | BCrypt hashed password |
| role | Enum | USER or ADMIN |

#### Document
| Field | Type | Description |
|-------|------|-------------|
| id | Long | Primary key |
| filename | String | Stored filename (UUID) |
| originalFilename | String | Original upload name |
| storagePath | String | File storage path |
| contentType | String | MIME type |
| size | Long | File size in bytes |
| status | Enum | UPLOADED, PROCESSING, PROCESSED, ERROR |
| category | Category | Classified category |
| confidence | Double | Classification confidence (0-1) |
| ocrText | Text | Extracted text content |
| errorMessage | String | Error details if failed |
| tags | Set<Tag> | Associated tags |
| user | User | Owner of document |
| uploadedAt | DateTime | Upload timestamp |
| processedAt | DateTime | Processing timestamp |

#### DocumentStatus (Enum)
```java
UPLOADED    // Just uploaded, awaiting processing
PROCESSING  // Currently being processed (OCR + Classification)
PROCESSED   // Successfully processed
ERROR       // Processing failed
```

#### Category
| Field | Type | Description |
|-------|------|-------------|
| id | Long | Primary key |
| name | String | Category name (unique) |
| description | String | Category description |
| color | String | UI color (hex) |
| createdBy | User | Creator |
| createdAt | DateTime | Creation timestamp |

#### Tag
| Field | Type | Description |
|-------|------|-------------|
| id | Long | Primary key |
| name | String | Tag name (unique) |
| color | String | UI color (hex) |
| createdBy | User | Creator |

#### AuditLog
| Field | Type | Description |
|-------|------|-------------|
| id | Long | Primary key |
| documentId | Long | Related document |
| action | String | Action type (PROCESS_START, OCR_DONE, etc.) |
| details | String | Action details |
| username | String | User who performed action |
| timestamp | DateTime | When action occurred |

---

## API Reference

### Base URL
```
http://localhost:8080/api
```

### Authentication Endpoints

#### Register
```http
POST /api/auth/register
Content-Type: application/json

{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "user": {
    "id": 1,
    "fullName": "John Doe",
    "email": "john@example.com",
    "role": "USER"
  }
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "password123"
}
```

**Response:** Same as register

---

### Document Endpoints

> **Note:** All document endpoints require authentication.
> Include header: `Authorization: Bearer <token>`

#### Upload Document
```http
POST /api/documents/upload
Content-Type: multipart/form-data
Authorization: Bearer <token>

file: <binary>
```

**Response:**
```json
{
  "id": 1,
  "filename": "uuid-123.pdf",
  "originalFilename": "invoice.pdf",
  "contentType": "application/pdf",
  "size": 102400,
  "status": "UPLOADED",
  "uploadedAt": "2024-01-15T10:30:00"
}
```

#### Get All Documents
```http
GET /api/documents
Authorization: Bearer <token>
```

#### Get Single Document
```http
GET /api/documents/{id}
Authorization: Bearer <token>
```

#### Download Document File
```http
GET /api/documents/{id}/file
Authorization: Bearer <token>
```

#### Search Documents
```http
GET /api/documents/search?q=invoice&category=INVOICE&status=PROCESSED&page=0&size=10
Authorization: Bearer <token>
```

| Parameter | Type | Description |
|-----------|------|-------------|
| q | String | Search query (filename, OCR text) |
| category | String | Filter by category name |
| status | String | Filter by status |
| page | Integer | Page number (0-based) |
| size | Integer | Page size |

#### Reclassify Document
```http
POST /api/documents/{id}/reclassify
Authorization: Bearer <token>
Content-Type: application/json

{
  "categoryId": 2
}
```

#### Delete Document
```http
DELETE /api/documents/{id}
Authorization: Bearer <token>
```

#### Add Tag to Document
```http
POST /api/documents/{id}/tags/{tagId}
Authorization: Bearer <token>
```

#### Remove Tag from Document
```http
DELETE /api/documents/{id}/tags/{tagId}
Authorization: Bearer <token>
```

---

### Category Endpoints

#### Get All Categories
```http
GET /api/categories
Authorization: Bearer <token>
```

#### Create Category
```http
POST /api/categories
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "INVOICE",
  "description": "Invoice documents",
  "color": "#4CAF50"
}
```

#### Update Category
```http
PUT /api/categories/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "INVOICES",
  "description": "Updated description",
  "color": "#2196F3"
}
```

#### Delete Category
```http
DELETE /api/categories/{id}
Authorization: Bearer <token>
```

---

### Tag Endpoints

#### Get All Tags
```http
GET /api/tags
Authorization: Bearer <token>
```

#### Create Tag
```http
POST /api/tags
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Important",
  "color": "#FF5722"
}
```

#### Update Tag
```http
PUT /api/tags/{id}
Authorization: Bearer <token>
```

#### Delete Tag
```http
DELETE /api/tags/{id}
Authorization: Bearer <token>
```

---

### Statistics Endpoints

#### Get Overview Stats
```http
GET /api/stats/overview
Authorization: Bearer <token>
```

**Response:**
```json
{
  "totalDocuments": 150,
  "processedDocuments": 145,
  "errorDocuments": 5,
  "totalCategories": 8,
  "totalTags": 12,
  "averageConfidence": 0.87
}
```

#### Get Category Stats
```http
GET /api/stats/categories
Authorization: Bearer <token>
```

---

### Real-time Events (SSE)

#### Subscribe to Document Events
```http
GET /api/sse/{documentId}
Authorization: Bearer <token>
Accept: text/event-stream
```

**Events:**
```
event: PROCESSING_STARTED
data: Processing started

event: OCR_DONE
data: OCR completed

event: CLASSIFIED
data: Classified as INVOICE

event: COMPLETED
data: Processing completed

event: ERROR
data: Error: <message>
```

---

## Authentication & Security

### JWT Authentication Flow

```
┌──────────┐                              ┌──────────┐
│  Client  │                              │  Server  │
└────┬─────┘                              └────┬─────┘
     │                                         │
     │  POST /api/auth/login                   │
     │  {email, password}                      │
     │────────────────────────────────────────►│
     │                                         │
     │                                         │ Validate credentials
     │                                         │ Generate JWT token
     │                                         │
     │  {token: "eyJhbG...", user: {...}}     │
     │◄────────────────────────────────────────│
     │                                         │
     │  GET /api/documents                     │
     │  Authorization: Bearer eyJhbG...        │
     │────────────────────────────────────────►│
     │                                         │
     │                                         │ Validate JWT
     │                                         │ Extract user
     │                                         │ Process request
     │                                         │
     │  [documents...]                         │
     │◄────────────────────────────────────────│
     │                                         │
```

### JWT Token Structure

```
Header.Payload.Signature

Header:
{
  "alg": "HS256"
}

Payload:
{
  "sub": "user@email.com",    // Subject (email)
  "iat": 1702384000,          // Issued at
  "exp": 1702470400           // Expiration (24h)
}

Signature:
HMACSHA256(base64UrlEncode(header) + "." + base64UrlEncode(payload), secret)
```

### Security Configuration

```java
// Public endpoints (no auth required)
/api/auth/**        // Login, Register
/uploads/**         // Static file access

// Protected endpoints (JWT required)
Everything else
```

### CORS Configuration

```java
Allowed Origins: * (all)
Allowed Methods: GET, POST, PUT, DELETE, OPTIONS, PATCH
Allowed Headers: *
Credentials: true
```

---

## Document Processing Pipeline

### Pipeline Flow

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         DOCUMENT PROCESSING PIPELINE                         │
│                            (PipelineServiceImpl)                             │
└─────────────────────────────────────────────────────────────────────────────┘

  Upload Request
       │
       ▼
┌──────────────────┐
│ DocumentService  │
│  uploadDocument  │
├──────────────────┤
│ 1. Save file     │
│ 2. Create entity │
│ 3. Status=UPLOAD │
│ 4. Trigger async │────────────────────────────┐
└──────────────────┘                            │
       │                                        │
       ▼                                        │
  Return Response                               │  @Async
  (Immediate)                                   │  (Background Thread)
                                                │
       ┌────────────────────────────────────────┘
       │
       ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                          processDocument(id)                              │
├──────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  ┌─────────────────────────────────────────────────────────────────┐     │
│  │ STEP 1: Status → PROCESSING                                     │     │
│  │         AuditLog: "PROCESS_START"                               │     │
│  │         SSE Event: "PROCESSING_STARTED"                         │     │
│  └─────────────────────────────────────────────────────────────────┘     │
│                              │                                           │
│                              ▼                                           │
│  ┌─────────────────────────────────────────────────────────────────┐     │
│  │ STEP 2: OCR EXTRACTION                                          │     │
│  │         OcrService.extractText(file)                            │     │
│  │         document.setOcrText(extractedText)                      │     │
│  │         AuditLog: "OCR_DONE"                                    │     │
│  │         SSE Event: "OCR_DONE"                                   │     │
│  └─────────────────────────────────────────────────────────────────┘     │
│                              │                                           │
│                              ▼                                           │
│  ┌─────────────────────────────────────────────────────────────────┐     │
│  │ STEP 3: AI CLASSIFICATION                                       │     │
│  │         ClassificationService.classify(document)                │     │
│  │         - Analyze OCR text                                      │     │
│  │         - Determine category                                    │     │
│  │         - Set confidence score                                  │     │
│  │         AuditLog: "CLASSIFIED"                                  │     │
│  │         SSE Event: "CLASSIFIED"                                 │     │
│  └─────────────────────────────────────────────────────────────────┘     │
│                              │                                           │
│                              ▼                                           │
│  ┌─────────────────────────────────────────────────────────────────┐     │
│  │ STEP 4: Status → PROCESSED                                      │     │
│  │         AuditLog: "PROCESS_COMPLETE"                            │     │
│  │         SSE Event: "COMPLETED"                                  │     │
│  └─────────────────────────────────────────────────────────────────┘     │
│                                                                          │
│  ┌─────────────────────────────────────────────────────────────────┐     │
│  │ ERROR HANDLING (catch block)                                    │     │
│  │         document.setErrorMessage(e.getMessage())                │     │
│  │         Status → ERROR                                          │     │
│  │         AuditLog: "PROCESS_ERROR"                               │     │
│  │         SSE Event: "ERROR"                                      │     │
│  └─────────────────────────────────────────────────────────────────┘     │
│                                                                          │
└──────────────────────────────────────────────────────────────────────────┘
```

### Status Transitions

```
     ┌──────────┐
     │ UPLOADED │  Initial state after upload
     └────┬─────┘
          │ processDocument() called
          ▼
    ┌───────────┐
    │PROCESSING │  OCR & Classification running
    └─────┬─────┘
          │
    ┌─────┴─────┐
    ▼           ▼
┌─────────┐ ┌───────┐
│PROCESSED│ │ ERROR │
└─────────┘ └───────┘
  Success    Failure
```

---

## Real-time Updates (SSE)

### Server-Sent Events Architecture

```
┌─────────────────┐                    ┌─────────────────┐
│     Client      │                    │     Server      │
│   (Frontend)    │                    │   (SseService)  │
└────────┬────────┘                    └────────┬────────┘
         │                                      │
         │  GET /api/sse/123                    │
         │  Accept: text/event-stream           │
         │─────────────────────────────────────►│
         │                                      │
         │                                      │ subscribe(123)
         │                                      │ Create SseEmitter
         │                                      │ Store in Map<Long, SseEmitter>
         │                                      │
         │◄─────── Connection kept open ────────│
         │                                      │
         │                                      │ (Pipeline processes document)
         │                                      │
         │  event: PROCESSING_STARTED           │
         │  data: Processing started            │
         │◄─────────────────────────────────────│ sendEvent(123, "PROCESSING_STARTED", ...)
         │                                      │
         │  event: OCR_DONE                     │
         │  data: OCR completed                 │
         │◄─────────────────────────────────────│ sendEvent(123, "OCR_DONE", ...)
         │                                      │
         │  event: CLASSIFIED                   │
         │  data: Classified as INVOICE         │
         │◄─────────────────────────────────────│ sendEvent(123, "CLASSIFIED", ...)
         │                                      │
         │  event: COMPLETED                    │
         │  data: Processing completed          │
         │◄─────────────────────────────────────│ sendEvent(123, "COMPLETED", ...)
         │                                      │
         │  Connection closed                   │ onCompletion() → remove emitter
         │─────────────────────────────────────►│
         │                                      │
```

### SseService Implementation

```java
@Service
public class SseService {
    // Thread-safe map of document ID → emitter
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    // Subscribe to document events
    public SseEmitter subscribe(Long documentId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.put(documentId, emitter);
        
        // Cleanup callbacks
        emitter.onCompletion(() -> emitters.remove(documentId));
        emitter.onTimeout(() -> emitters.remove(documentId));
        emitter.onError((e) -> emitters.remove(documentId));
        
        return emitter;
    }

    // Send event to subscribed client
    public void sendEvent(Long documentId, String eventName, Object data) {
        SseEmitter emitter = emitters.get(documentId);
        if (emitter != null) {
            emitter.send(SseEmitter.event().name(eventName).data(data));
        }
    }
}
```

---

## Configuration

### application.properties

```properties
# Server
server.port=${PORT:8080}

# Database
spring.datasource.url=${DATABASE_URL:jdbc:mysql://localhost:3306/docdb}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:}
spring.jpa.hibernate.ddl-auto=${DDL_AUTO:update}

# File Upload
app.upload.dir=${LOCAL_STORAGE_PATH:./storage}
spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=50MB

# JWT
app.jwt.secret=${JWT_SECRET:your-secret-key}
app.jwt.expiration-ms=${JWT_EXPIRATION:86400000}  # 24 hours

# Health Checks
management.endpoints.web.exposure.include=health,info
```

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `PORT` | Server port | 8080 |
| `DATABASE_URL` | MySQL connection URL | localhost:3306/docdb |
| `DB_USERNAME` | Database username | root |
| `DB_PASSWORD` | Database password | (empty) |
| `JWT_SECRET` | JWT signing secret | (hardcoded) |
| `JWT_EXPIRATION` | Token expiration (ms) | 86400000 (24h) |
| `LOCAL_STORAGE_PATH` | File storage directory | ./storage |

---

## Design Patterns

| Pattern | Implementation | Purpose |
|---------|----------------|---------|
| **Layered Architecture** | Controller → Service → Repository | Separation of concerns |
| **Repository Pattern** | Spring Data JPA interfaces | Data access abstraction |
| **Service Layer Pattern** | *Service interfaces + *ServiceImpl | Business logic isolation |
| **Filter Chain** | JwtAuthenticationFilter | Request processing chain |
| **Builder Pattern** | Lombok @Builder on entities | Object construction |
| **Strategy Pattern** | ClassificationService interface | Swappable algorithms |
| **Observer Pattern** | SseService | Real-time notifications |
| **Singleton Pattern** | Spring beans (@Service, @Repository) | Single instances |
| **Dependency Injection** | @RequiredArgsConstructor | Loose coupling |
| **Template Method** | PipelineServiceImpl.processDocument() | Fixed algorithm skeleton |

---

## Deployment

### Docker

```dockerfile
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Build & Run

```bash
# Build
./mvnw clean package -DskipTests

# Run locally
./mvnw spring-boot:run

# Docker build
docker build -t docclassifier-backend .

# Docker run
docker run -p 8080:8080 \
  -e DATABASE_URL=jdbc:mysql://host:3306/docdb \
  -e DB_USERNAME=user \
  -e DB_PASSWORD=pass \
  docclassifier-backend
```

### Health Check

```bash
curl http://localhost:8080/actuator/health
```

---

## Error Handling

### Global Exception Handler

All exceptions are handled centrally in `GlobalExceptionHandler`:

| Exception | HTTP Status | Response |
|-----------|-------------|----------|
| AccessDeniedException | 403 Forbidden | Access denied message |
| RuntimeException | 404/500 | Error message |
| ValidationException | 400 Bad Request | Validation errors |
| AuthenticationException | 401 Unauthorized | Auth error message |

### Error Response Format

```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Document not found",
  "path": "/api/documents/999"
}
```

---

## Audit Logging

All document processing actions are logged:

| Action | Description |
|--------|-------------|
| PROCESS_START | Document processing started |
| OCR_DONE | OCR text extraction completed |
| CLASSIFIED | Document classified |
| PROCESS_COMPLETE | Processing completed successfully |
| PROCESS_ERROR | Processing failed |

Example audit log entry:
```json
{
  "id": 1,
  "documentId": 123,
  "action": "CLASSIFIED",
  "details": "Classified as INVOICE",
  "username": "user@example.com",
  "timestamp": "2024-01-15T10:30:05"
}
```

---

## Author

**DocClassifier** - Intelligent Document Classification Platform

Built with ❤️ using Spring Boot
