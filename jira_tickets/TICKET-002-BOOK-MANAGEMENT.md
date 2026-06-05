# TICKET-002: Book Management & Catalog

**Status:** 📋 TODO (Ready for Development)

**Story Type:** Feature

**Epic:** Library Core System

**Depends On:** TICKET-001 (Member Registration)

---

## Title
As a librarian, I want to manage books in the library system so that I can maintain an accurate catalog and track available resources.

## Description
The library management system must provide functionality for library staff to create, view, update, and delete book records. Books are a core resource entity and are essential for managing loans and reservations.

The solution should expose REST API endpoints that allow staff to add new books, view book details, search/browse the catalog with pagination, and update or remove book information from the system.

## Business Value
Maintaining an accurate book catalog enables the library to:
- Track available books and their locations
- Associate books with loans and reservations
- Support future search and discovery features
- Manage inventory and book metadata
- Generate reports on library resources

## Acceptance Criteria

### AC1: Create Book
- **Given** a librarian wants to add a new book to the catalog
- **When** a valid request is submitted to `POST /api/books`
- **Then** the system creates the book record and returns the created book including a unique ID

**Required Fields:**
- `title` (String, required)
- `isbn` (String, required, unique)
- `categoryId` (Long, required - foreign key to Category)
- `publishedYear` (Integer, optional)
- `availableCopies` (Integer, required, default: 1)

**Optional Fields:**
- `description` (String)
- `publisher` (String)

**Response:**
- HTTP 201 Created
- Book object with generated ID, createdAt, updatedAt timestamps

### AC2: Get Book by ID
- **Given** a librarian or system needs book details
- **When** a request is submitted to `GET /api/books/{id}`
- **Then** the system returns the complete book details including category and authors
- **And** returns HTTP 404 if book does not exist

### AC3: List All Books (Paginated)
- **Given** a librarian wants to browse the book catalog
- **When** a request is submitted to `GET /api/books?page=0&size=10`
- **Then** the system returns a paginated list of books
- **And** supports sorting by title, publishedYear, or availableCopies
- **And** supports filtering by categoryId

### AC4: Update Book
- **Given** a librarian needs to update book information
- **When** a valid request is submitted to `PUT /api/books/{id}`
- **Then** the system updates the book record
- **And** returns the updated book details
- **And** returns HTTP 404 if book does not exist
- **And** returns HTTP 400 if attempting to change ISBN to an existing ISBN

### AC5: Delete Book
- **Given** a librarian needs to remove a book from the catalog
- **When** a DELETE request is submitted to `/api/books/{id}`
- **Then** the system soft-deletes the book (or hard-deletes if no loans exist)
- **And** returns HTTP 204 No Content on success
- **And** returns HTTP 404 if book does not exist
- **And** returns HTTP 409 if book has active loans

### AC6: Validation Rules
- ISBN must be valid format and unique in the system
- Title must not be empty
- availableCopies must be >= 0
- publishedYear must be a valid year (if provided)
- categoryId must reference an existing Category
- Return HTTP 400 with field-level error messages for invalid input

### AC7: Error Handling
- Duplicate ISBN returns HTTP 400 with DuplicateResourceException
- Invalid book ID returns HTTP 404 with "Book not found" message
- Validation errors return HTTP 400 with field error details
- Book with active loans returns HTTP 409 on deletion attempt

## Technical Details

### API Endpoints
```
POST   /api/books                    → Create new book
GET    /api/books/{id}               → Get book by ID
GET    /api/books                    → List all books (paginated, sortable, filterable)
PUT    /api/books/{id}               → Update book
DELETE /api/books/{id}               → Delete book
```

### Entity Model
```java
Book {
  book_id: Long (PK, auto-generated)
  title: String (required)
  isbn: String (required, unique)
  description: String (optional)
  publisher: String (optional)
  publishedYear: Integer (optional)
  availableCopies: Integer (required, default: 1)
  categoryId: Long (FK to Category, required)
  createdAt: LocalDateTime (auto)
  updatedAt: LocalDateTime (auto)
  authors: List<Author> (many-to-many)
  loans: List<Loan> (one-to-many)
  category: Category (many-to-one)
}
```

### DTO Models
```java
CreateBookDto {
  title: String
  isbn: String
  description: String
  publisher: String
  publishedYear: Integer
  availableCopies: Integer
  categoryId: Long
}

UpdateBookDto {
  title: String
  isbn: String
  description: String
  publisher: String
  publishedYear: Integer
  availableCopies: Integer
  categoryId: Long
}

BookResponseDto {
  bookId: Long
  title: String
  isbn: String
  description: String
  publisher: String
  publishedYear: Integer
  availableCopies: Integer
  categoryId: Long
  categoryName: String
  authors: List<AuthorDto>
  createdAt: LocalDateTime
  updatedAt: LocalDateTime
}
```

## Implementation Checklist
- [ ] Create/update `Book` entity with relationships
- [ ] Create `BookRepository` (JPA interface with custom queries)
- [ ] Create `BookService` interface
- [ ] Create `BookServiceImpl` with full CRUD logic
- [ ] Create DTOs (`CreateBookDto`, `UpdateBookDto`, `BookResponseDto`)
- [ ] Create `BookMapper` for entity-to-DTO conversion
- [ ] Create `BookController` with REST endpoints
- [ ] Add OpenAPI/Swagger annotations
- [ ] Implement validation (ISBN uniqueness, field validation)
- [ ] Create custom repository queries for filtering/sorting
- [ ] Create unit tests for `BookService`
- [ ] Create integration tests for `BookController`
- [ ] Update API documentation

## Definition of Done
- [ ] All AC1-AC7 acceptance criteria met
- [ ] API endpoints tested via Postman/curl
- [ ] Data persists to PostgreSQL correctly
- [ ] Pagination, sorting, and filtering work correctly
- [ ] Timestamps (createdAt/updatedAt) auto-populate correctly
- [ ] All unit tests pass (>80% code coverage)
- [ ] All integration tests pass
- [ ] Exception handling returns proper HTTP status codes
- [ ] OpenAPI/Swagger documentation is accurate
- [ ] Code reviewed and merged to develop/main branch
- [ ] Verified no breaking changes to existing Member endpoints

## Notes
- Book-Author relationship should be many-to-many (a book can have multiple authors, an author can write multiple books)
- Consider implementing soft-delete if books with historical loan data need to be preserved
- availableCopies should decrease when loans are created (implementation in loan service)
- ISBN validation should use industry-standard format (ISBN-10 or ISBN-13)
- Consider adding full-text search on book catalog in future iterations

## Related Tickets
- TICKET-001: Member Registration (prerequisite)
- TICKET-003: Author Management (upcoming)
- TICKET-004: Category Management (upcoming)
- TICKET-005: Loan Management (depends on books + members)

---

**Created Date:** 2026-06-04
**Created By:** hem
**Priority:** High
**Estimated Story Points:** 8
