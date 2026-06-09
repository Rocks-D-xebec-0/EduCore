# TICKET-003: Author Management

**Status:** 📋 TODO (Ready for Development)

**Story Type:** Feature

**Epic:** Library Core System

**Depends On:** TICKET-002 (Book Management)

---

## Title
As a librarian, I want to manage authors in the library system so that I can associate books with their correct authors and maintain accurate catalog metadata.

## Description
The library management system must provide functionality for library staff to create, view, update, and delete author records. Authors are linked to books through a many-to-many relationship and are required for complete book catalog information.

The solution should expose REST API endpoints that allow staff to add new authors, view author details including their associated books, browse the author list with pagination, and update or remove author records.

## Business Value
- Maintain accurate authorship data across the book catalog
- Enable search and discovery by author
- Support future author-based filtering and reporting
- Ensure book metadata is complete and trustworthy

## Acceptance Criteria

### AC1 – Create Author
Endpoint: `POST /api/authors`

- **Given** a librarian wants to add a new author
- **When** a valid request is submitted
- **Then** the system creates the author and returns the created resource with a generated ID

**Required Fields:**
- `name` (String, required)
- `email` (String, required, unique)

**Optional Fields:**
- `bio` (String)

**Response:**
- HTTP 201 Created
- `AuthorResponseDto`

### AC2 – Get Author by ID
Endpoint: `GET /api/authors/{id}`

- **Given** a valid author ID
- **When** a request is submitted
- **Then** the system returns the author's details including their list of associated book titles

**Error Handling:**
- HTTP 404 if author does not exist

### AC3 – List Authors
Endpoint: `GET /api/authors`

- **Given** a librarian wants to browse authors
- **When** a request is submitted with pagination parameters
- **Then** the system returns a paginated list of authors

**Supported Features:**
- Pagination (`page`, `size`)
- Sorting by: `name`, `email`

### AC4 – Update Author
Endpoint: `PUT /api/authors/{id}`

- **Given** a librarian wants to update author information
- **When** a valid request is submitted
- **Then** the system updates the author and returns the updated details

**Errors:**
- HTTP 404 if author does not exist
- HTTP 400 if email already belongs to another author

### AC5 – Delete Author
Endpoint: `DELETE /api/authors/{id}`

- **Given** a librarian wants to remove an author
- **When** a DELETE request is submitted
- **Then** the system removes the author

**Rules:**
- Hard delete if the author has no associated books
- HTTP 409 if the author is linked to one or more books (must unlink books first)

**Responses:**
- HTTP 204 No Content
- HTTP 404 if author not found
- HTTP 409 if author has associated books

### AC6 – Validation
- `name` must not be empty
- `email` must be a valid email format
- `email` must be unique across all authors
- Validation failures return HTTP 400 with field-level errors

### AC7 – Error Handling
| Scenario | HTTP |
|---|---|
| Duplicate email | 400 |
| Invalid author ID | 404 |
| Validation error | 400 |
| Author linked to books | 409 |

## Technical Details

### API Endpoints
```
POST   /api/authors          → Create author
GET    /api/authors/{id}     → Get author by ID (includes book titles)
GET    /api/authors          → List authors (paginated, sortable)
PUT    /api/authors/{id}     → Update author
DELETE /api/authors/{id}     → Delete author
```

### Entity Model
The `Author` entity already exists. Verify it has the following before implementing:
```java
Author {
  author_id: int (PK, auto-generated)
  name: String (required)
  email: String (required, unique)
  bio: String (optional)
  books: Set<Book> (many-to-many, mappedBy = "authors")
  createdAt: LocalDateTime (auto via BaseEntity)
  updatedAt: LocalDateTime (auto via BaseEntity)
}
```

### DTO Models
```java
CreateAuthorDto {
  name: String          // @NotBlank
  email: String         // @NotBlank, @Email
  bio: String           // optional
}

UpdateAuthorDto {
  name: String          // @NotBlank
  email: String         // @NotBlank, @Email
  bio: String           // optional
}

AuthorResponseDto {
  authorId: Long
  name: String
  email: String
  bio: String
  bookTitles: List<String>   // titles of associated books
  createdAt: LocalDateTime
  updatedAt: LocalDateTime
}
```

### Repository
```java
AuthorRepository extends JpaRepository<Author, Integer> {
  boolean existsByEmail(String email);
  boolean existsByEmailAndAuthor_idNot(String email, int authorId); // for update check
}
```

### Delete Logic
```java
if (author.getBooks().isEmpty()) {
    authorRepository.delete(author);       // hard delete
} else {
    throw new AuthorHasBooksException(...); // 409 Conflict
}
```

## Implementation Checklist
- [ ] Verify `Author` entity fields and relationships match the model above
- [ ] Create `AuthorRepository` with `existsByEmail` and duplicate-on-update query
- [ ] Create `AuthorService` interface
- [ ] Create `AuthorServiceImpl` with full CRUD + delete logic
- [ ] Create DTOs (`CreateAuthorDto`, `UpdateAuthorDto`, `AuthorResponseDto`)
- [ ] Create `AuthorMapper` (include book titles in `toResponseDto`)
- [ ] Create `AuthorController` with REST endpoints and Swagger annotations
- [ ] Create `AuthorHasBooksException` and register handler in `GlobalExceptionHandler` (HTTP 409)
- [ ] Add `@Email` and `@NotBlank` validation to DTOs
- [ ] Add `@Transactional` to `AuthorServiceImpl` (needed to access lazy `books` collection)
- [ ] Create `AuthorServiceTest` (unit tests — mock all deps including mapper)
- [ ] Create `AuthorControllerTest` (integration tests — `@MockBean AuthorService`)

## Definition of Done
- [ ] All AC1–AC7 acceptance criteria met
- [ ] API endpoints verified via Swagger UI
- [ ] Data persists to PostgreSQL correctly
- [ ] Many-to-many relationship with Book works correctly (no duplicate join tables)
- [ ] Unit test coverage > 80%
- [ ] All integration tests pass
- [ ] Exception handling returns correct HTTP status codes
- [ ] No regression on `/api/books` or `/api/members` endpoints
- [ ] Code reviewed and merged

## Notes
- `Author` is the **non-owning** side of the `Book ↔ Author` ManyToMany relationship. `Book` owns the join table (`book_authors`). Do not add `@JoinTable` to `Author` — use `mappedBy = "authors"` only (see `docs/common_errors.md`).
- `AuthorMapper.toResponseDto` must access `author.getBooks()` (lazy-loaded). The service must be `@Transactional` or this will throw `LazyInitializationException`.
- To link an author to a book, the operation belongs in the Book service (owning side). Do not add book-linking logic here.
- `author_id` is `int` in the existing entity — `AuthorRepository` should extend `JpaRepository<Author, Integer>`.

## Related Tickets
- TICKET-001: Member Registration (complete)
- TICKET-002: Book Management (complete)
- TICKET-004: Category Management (upcoming)
- TICKET-005: Loan Management (depends on authors + books + members)

---

**Created Date:** 2026-06-09
**Created By:** hem
**Priority:** High
**Estimated Story Points:** 5
