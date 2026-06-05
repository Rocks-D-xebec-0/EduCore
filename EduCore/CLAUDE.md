# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**EduCore** is a Spring Boot-based library management system REST API. The application is currently in development with the focus on member registration and management features. The codebase is well-structured with proper separation of concerns using a layered architecture pattern.

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
│   │   ├── config/          # Configuration classes (EnvConfig for environment variables)
│   │   ├── controller/      # REST API endpoints
│   │   ├── service/         # Business logic interfaces
│   │   ├── service/impl/    # Service implementations
│   │   ├── entity/          # JPA entities (Member, Book, Author, Loan, Category)
│   │   ├── dto/             # Data Transfer Objects for API requests/responses
│   │   ├── mapper/          # Entity-to-DTO mapping
│   │   ├── repository/      # Spring Data JPA repositories
│   │   ├── exception/       # Custom exceptions and GlobalExceptionHandler
│   │   └── EduCoreApplication.java
│   └── resources/
│       └── application.yaml # Spring configuration
└── test/
    └── java/com/hem/EduCore/
        ├── service/        # Unit tests using Mockito + JUnit 5
        └── controller/     # Integration/controller tests
```

## Layered Architecture

The project follows a clean, layered architecture pattern:

1. **Controller Layer** (`MemberController`, etc.)
   - REST endpoints with OpenAPI annotations
   - Request validation via `@Valid`
   - Returns DTOs, never entities directly

2. **Service Layer** (`MemberService` interface + `MemberServiceImpl`)
   - Business logic implementation
   - Data validation
   - DTO-to-Entity mapping and vice versa
   - Exception handling

3. **Repository Layer** (extends `JpaRepository`)
   - Database access via Spring Data JPA
   - Query methods automatically generated or custom SQL

4. **Entity Layer**
   - JPA-annotated POJOs with Lombok `@Data`, `@Entity`
   - `BaseEntity` provides `createdAt` and `updatedAt` fields with auto-lifecycle management via `@PrePersist` and `@PreUpdate`
   - Relationships: Member ↔ Loan (OneToMany), Book ↔ Author, Book ↔ Category, etc.

## Common Commands

### Build and Clean
```bash
mvn clean
mvn compile
mvn clean install
```

### Run the Application
```bash
mvn spring-boot:run
```

The application starts on `http://localhost:8080` by default. API documentation is available at `http://localhost:8080/swagger-ui.html`.

### Run All Tests
```bash
mvn test
```

### Run Specific Test Class
```bash
mvn test -Dtest=MemberServiceTest
```

### Run Specific Test Method
```bash
mvn test -Dtest=MemberServiceTest#testCreateMemberSuccess
```

### Run Tests with Coverage Report
```bash
mvn clean test
```

## Database Configuration

The application uses **PostgreSQL** and connects via JDBC. Configuration is managed in `application.yaml` and supports environment variables for flexible deployment:

```yaml
datasource:
  url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:educore_db}
  username: ${DB_USERNAME:postgres}
  password: ${DB_PASSWORD:postgres}
```

**Environment variables** are loaded from a `.env` file via `dotenv-java` library. The `EnvConfig` class is initialized in a static block in `EduCoreApplication` before Spring starts.

### Setting Up Local Database
```bash
# Create PostgreSQL database
createdb educore_db

# Set environment variables (or create .env file):
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=educore_db
export DB_USERNAME=postgres
export DB_PASSWORD=postgres
```

## Key Architectural Decisions

### DTOs Over Entities
- API endpoints **always** return DTOs, never entities
- **Why:** Prevents infinite JSON recursion (Member → Loan → Member → ...), avoids lazy-loading issues, and keeps API contract independent of database schema
- **Pattern:** Each entity has a corresponding DTO (e.g., `Member` → `MemberResponseDto`, input → `CreateMemberDto`)
- **Mapping:** Use `MemberMapper` or similar mappers in service implementations

### Exception Handling
- Custom exceptions (e.g., `DuplicateResourceException`) thrown in service layer
- `GlobalExceptionHandler` catches and translates to appropriate HTTP responses
- Validation errors from `@Valid` are caught and formatted as error maps

### Testing Strategy
- **Unit Tests:** Use Mockito to mock repository dependencies; test service logic in isolation
- **Test Patterns:** `@ExtendWith(MockitoExtension.class)`, `@Mock`, `@InjectMocks`
- **Assertions:** JUnit 5 `assertEquals()`, `assertThrows()`, `assertNotNull()`, etc.
- **Verification:** Mockito's `verify()` for method call assertions

## Active Development Branch

The repository is currently on the **`feat/member-registration`** branch. This branch implements the first user story: Member Registration & Management with CRUD operations and comprehensive tests.

Recent commits include:
- Integration tests for `MemberController`
- Unit tests for `MemberService`
- Exception handling layer
- `MemberController` with REST endpoints
- `MemberService` CRUD implementation

## Key Files to Know

- **Application Entry:** `EduCoreApplication.java`
- **Configuration:** `application.yaml`, `EnvConfig.java`
- **Example Entities:** `Member.java`, `Book.java`, `Author.java`
- **Example Service:** `MemberService.java`, `MemberServiceImpl.java`
- **Example Controller:** `MemberController.java` (with Swagger/OpenAPI documentation)
- **Exception Handling:** `GlobalExceptionHandler.java`, `DuplicateResourceException.java`
- **Test Examples:** `MemberServiceTest.java` (comprehensive unit test suite)
- **Documentation:** `docs/STORIES.md` (user stories), `docs/TESTING.md` (testing guide)

## Dependencies Summary

- **Spring Boot Starters:** web-mvc, data-jpa, validation
- **Database:** postgresql driver
- **Tooling:** lombok (reduces boilerplate)
- **Testing:** spring-boot-starter-data-jpa-test, spring-boot-starter-webmvc-test
- **API Docs:** springdoc-openapi-starter-webmvc-ui (Swagger/OpenAPI integration)
- **Environment:** dotenv-java (load environment variables from `.env`)

## Testing Notes

The project includes comprehensive test coverage for the Member service. Tests follow the Arrange-Act-Assert pattern and use Mockito for isolating dependencies. Key testing best practices observed:

- Unit tests use mocked repositories
- Test data is set up in `@BeforeEach` methods
- Descriptive test names with `@DisplayName` annotation
- Validation of both happy paths and error scenarios
- Verification of repository calls using `verify()` and `never()`

For more details, see `docs/TESTING.md`.

## Git Workflow

- **Main branch:** `main` — production-ready code
- **Feature branch:** `feat/member-registration` — current development
- **User:** `hem`

Use standard Git flow: create feature branches from `main`, develop and test, then create a pull request for review before merging back to `main`.

## Next Steps for Development

Refer to `docs/STORIES.md` for upcoming features and acceptance criteria. The first story (Member Registration) is currently in progress. Future stories will likely include:
- Book management (create, list, update, delete)
- Author management
- Category management
- Loan management and tracking
