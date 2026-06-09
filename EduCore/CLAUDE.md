# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**EduCore** is a Spring Boot-based library management system REST API. The application provides CRUD operations for members and books, with loan tracking and reservation support planned.

- **Framework:** Spring Boot 4.0.6
- **Language:** Java 17
- **Build Tool:** Maven
- **Database:** PostgreSQL
- **ORM:** Spring Data JPA / Hibernate
- **Documentation:** Swagger/OpenAPI (springdoc-openapi v3.0.3)

## Project Structure

```
src/
├── main/
│   ├── java/com/hem/EduCore/
│   │   ├── config/           # EnvConfig — loads .env before Spring starts
│   │   ├── controller/       # REST controllers (MemberController, BookController)
│   │   ├── service/          # Business logic interfaces
│   │   ├── service/impl/     # Service implementations
│   │   ├── entity/           # JPA entities (Member, Book, Author, Loan, Category)
│   │   ├── dto/
│   │   │   ├── Request/      # CreateMemberDto, CreateBookDto, UpdateBookDto
│   │   │   └── Reponse/      # MemberResponseDto, BookResponseDto
│   │   ├── mapper/           # MemberMapper, BookMapper (manual field mapping)
│   │   ├── repository/       # MemberRepository, BookRepository, LoanRepository,
│   │   │                     # CategoryRepository
│   │   ├── exception/        # GlobalExceptionHandler, DuplicateResourceException,
│   │   │                     # ActiveLoanConflictException
│   │   └── EduCoreApplication.java
│   └── resources/
│       └── application.yaml  # Spring + DB configuration
└── test/
    └── java/com/hem/EduCore/
        ├── service/           # BookServiceTest, MemberServiceTest (Mockito unit tests)
        └── controller/        # BookControllerTest, MemberControllerTest (MockMvc)
```

## Implemented Features

### TICKET-001 — Member Registration (complete)
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/members` | Create member |
| GET | `/api/members/{id}` | Get member by ID |
| GET | `/api/members` | List members (paginated) |

### TICKET-002 — Book Management (complete)
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/books` | Create book |
| GET | `/api/books/{id}` | Get book by ID (includes category + authors) |
| GET | `/api/books` | List books (paginated, sortable, filterable by `categoryId`) |
| PUT | `/api/books/{id}` | Update book |
| DELETE | `/api/books/{id}` | Delete book (see three-way logic below) |

## Layered Architecture

The project enforces strict layering:

1. **Controller** — validates input with `@Valid`, returns DTOs, never entities, documents with Swagger `@Operation`/`@ApiResponses`
2. **Service interface** — defines the contract; `@Transactional` lives on the `impl`
3. **ServiceImpl** — business logic, exception throwing, calls mapper and repositories
4. **Repository** — extends `JpaRepository`; custom queries via derived method names or `@Query` JPQL
5. **Entity** — `@Data` + `@Entity` + Lombok; all extend `BaseEntity` (`createdAt`, `updatedAt`)
6. **Mapper** — plain `@Component` classes; map manually field by field (no MapStruct)

## Key Architectural Decisions

### DTOs Over Entities
API endpoints always return DTOs, never entities. This prevents JSON recursion (`Member → Loan → Member`), avoids lazy-load issues, and decouples the API contract from the schema.

### @Transactional on ServiceImpl
`BookServiceImpl` is annotated `@Transactional` at class level so that lazy-loaded collections (`book.getCategories()`, `book.getAuthors()`) are safely accessed inside the mapper without `LazyInitializationException`.

### Three-Way Book Delete Logic
`DELETE /api/books/{id}` applies different behavior depending on loan history:

| Condition | Action | HTTP |
|---|---|---|
| Active loans (`returnDate IS NULL`) | Reject | 409 Conflict |
| Historical loans only (`returnDate IS NOT NULL`) | Soft delete (`isDeleted = true`) | 204 |
| No loans at all | Hard delete | 204 |

### Exception Handling
| Exception | HTTP |
|---|---|
| `DuplicateResourceException` | 400 |
| `ActiveLoanConflictException` | 409 |
| `RuntimeException` with "Not found" in message | 404 |
| `MethodArgumentNotValidException` | 400 with field-level errors |

All handled centrally in `GlobalExceptionHandler`.

### ISBN Validation
`CreateBookDto` and `UpdateBookDto` validate ISBN format with:
```
^\\d{9}[\\dXx]$|^97[89]\\d{10}$
```
ISBN-10 (9 digits + check digit 0–9 or X) or ISBN-13 (13 digits starting with 978/979).

## Common Commands

```bash
# Build
mvn clean install

# Run
mvn spring-boot:run
# → http://localhost:8080
# → http://localhost:8080/swagger-ui.html

# All tests
mvn test

# Specific test class
mvn test -Dtest=BookServiceTest
mvn test -Dtest=BookControllerTest

# Specific test method
mvn test -Dtest=BookServiceTest#deleteBook_activeLoans_throwsConflictException
```

## Database Configuration

```yaml
datasource:
  url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:educore_db}
  username: ${DB_USERNAME:postgres}
  password: ${DB_PASSWORD:postgres}
```

Environment variables are loaded from a `.env` file via `dotenv-java`. `EnvConfig` initialises this in a static block in `EduCoreApplication` before Spring starts.

```bash
# Local setup
createdb educore_db
# .env file:
DB_HOST=localhost
DB_PORT=5432
DB_NAME=educore_db
DB_USERNAME=postgres
DB_PASSWORD=postgres
```

## Testing Strategy

| Layer | Tool | Pattern |
|---|---|---|
| Service unit tests | Mockito + JUnit 5 | `@ExtendWith(MockitoExtension.class)`, mock ALL constructor-injected deps including mappers |
| Controller integration tests | MockMvc + `@SpringBootTest` | `@MockBean` the service; test HTTP status codes and JSON response fields |

Key rules:
- **Mock every dependency** in service unit tests, including mappers. Unmocked deps will be `null` (constructor injection via `@RequiredArgsConstructor`) and cause NPE.
- Set up test data in `@BeforeEach`.
- Use `@DisplayName` on every test with a descriptive sentence.
- Assert both the happy path and every error branch documented in the ticket.
- Use `verify(repo, never()).save(any())` to assert side effects didn't happen.

## Key Files

| File | Purpose |
|---|---|
| `BookServiceImpl.java` | Full CRUD + three-way delete logic |
| `BookMapper.java` | Maps entity → DTO including category and author names |
| `BookRepository.java` | `existsByIsbn`, `existsByIsbnAndNotId`, `findByCategoryId` |
| `LoanRepository.java` | `existsByBookAndReturnDateIsNull`, `existsByBookAndReturnDateIsNotNull` |
| `GlobalExceptionHandler.java` | Centralised error mapping (400 / 404 / 409 / 500) |
| `ActiveLoanConflictException.java` | Thrown when deleting a book with active loans |
| `BookServiceTest.java` | 14 unit tests covering all service branches |
| `BookControllerTest.java` | 14 integration tests covering all endpoints |
| `docs/TICKET-002-GAPS-ANALYSIS.md` | Lessons learned from Book ticket; pre-implementation checklist |
| `docs/common_errors.md` | JPA ManyToMany `mappedBy` pitfalls |

## Git Workflow

- **Main branch:** `main`
- **Current branch:** `feature/book-management-and-refactoring`
- **User:** `hem`
- Commit convention: `feat(scope):`, `fix(scope):`, `test(scope):`, `docs:`

## Next Steps

| Ticket | Feature | Status |
|---|---|---|
| TICKET-001 | Member Registration | Done |
| TICKET-002 | Book Management | Done |
| TICKET-003 | Author Management | Pending |
| TICKET-004 | Category Management | Pending |
| TICKET-005 | Loan Management | Pending |
