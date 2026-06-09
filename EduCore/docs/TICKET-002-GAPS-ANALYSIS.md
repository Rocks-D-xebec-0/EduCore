# TICKET-002 Book Management — Gap Analysis & Lessons Learned

This document records every part of the ticket that was missed during implementation,
why it matters, and how to avoid repeating the same mistakes.

---

## 1. Entity Gaps — Fields Not Read from the Ticket

### What was implemented vs what was required

| Field | Ticket Status | Implemented | Problem |
|---|---|---|---|
| `isbn` | Required, unique | Missing | Core business field, uniqueness constraint never added |
| `availableCopies` | Required, default = 1 | Missing | Needed for loan/reservation logic |
| `publisher` | Optional | Missing | Silently dropped from scope |
| `publishedYear` | Optional | Mapped to `publicationYear` (nullable = false) | Optional field was made mandatory |
| `pages` | Not in ticket | Added | Invented a field that the ticket never asked for |
| `description` | Optional | Marked `nullable = false` | Optional field made mandatory in DB column |

### Lesson
**Read every field in the ticket before touching the entity.**
Mark required vs optional clearly. Never add fields the ticket doesn't ask for.
`nullable = false` on an optional field is a schema bug that breaks inserts silently.

---

## 2. DTO Gaps — DTOs Didn't Mirror the Entity

### `CreateBookDto` missing fields
- `isbn` (required + unique)
- `categoryId` (required, references Category entity)
- `availableCopies` (required, `@Min(0)`)
- `publisher` (optional)
- `publishedYear` (optional)

### `UpdateBookDto` missing fields
- `isbn`
- `availableCopies`
- `publisher`

### `BookResponseDto` missing fields
- `isbn`
- `availableCopies`
- `publisher`
- category details (name, id)
- authors list

### Lesson
**DTOs must be designed from the ticket's field list, not derived from a half-built entity.**
Check: does every required field appear in Create? Does every readable field appear in Response?
The Response DTO is the contract the client depends on — missing fields here break the front end silently.

---

## 3. Mapper Bug — Reading from Wrong Object

### The bug in `BookMapper.toResponseDto`

```java
// WRONG — reads description from the DTO, not from the entity
bookResponseDto.setDescription(bookResponseDto.getDescription());

// CORRECT
bookResponseDto.setDescription(book.getDescription());
```

### Why it happened
The mapper was written quickly, copying a line pattern without checking which object to read from.
The field silently mapped to `null` in every response — no compilation error, no test caught it.

### Lesson
**Every mapper line must read from the source object, not the destination.**
If there are no unit tests verifying field values in the response, this type of bug
will reach production. Always write at least one mapper test that asserts each individual field.

---

## 4. AC3 Filtering — Pagination Without Filtering

### What was missed
The ticket requires filtering by `categoryId` on `GET /api/books`.
The implementation only accepted `Pageable` — no filter parameter was added.

### What was needed

**Repository:**
```java
Page<Book> findByCategoriesId(Long categoryId, Pageable pageable);
```

**Service:**
```java
Page<BookResponseDto> getAllBooks(Long categoryId, Pageable pageable);
```

**Controller:**
```java
@GetMapping
public ResponseEntity<Page<BookResponseDto>> getAllBooks(
    @RequestParam(required = false) Long categoryId,
    Pageable pageable
) { ... }
```

### Lesson
**"Filtering by X" in the ticket always means a `@RequestParam` in the controller,
a conditional in the service, and a derived query in the repository.**
Pagination alone is not enough. Always check the "Supported Features" section of each AC.

---

## 5. AC5 Delete — Three Cases, Not Two

### The correct logic from the ticket

| Condition | Action | HTTP Response |
|---|---|---|
| Active loans exist (`returnDate IS NULL`) | Reject | 409 Conflict |
| Historical loans only (`returnDate IS NOT NULL`) | Soft delete | 204 No Content |
| No loans at all | Hard delete | 204 No Content |

### What was implemented
Only a two-way check using `existsByBook(book)` — no distinction between
active and historical loans. The 409 case was never handled.

### What was needed in `LoanRepository`
```java
boolean existsByBookAndReturnDateIsNull(Book book);       // active loans
boolean existsByBookAndReturnDateIsNotNull(Book book);    // historical loans
```

### What was needed in `BookServiceImpl`
```java
public void deleteBook(Long id) {
    Book book = bookRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Book Not found"));

    if (loanRepository.existsByBookAndReturnDateIsNull(book)) {
        throw new ActiveLoanConflictException("Cannot delete book with active loans");
    }

    if (loanRepository.existsByBookAndReturnDateIsNotNull(book)) {
        book.setDeleted(true);
        bookRepository.save(book);
    } else {
        bookRepository.delete(book);
    }
}
```

### Lesson
**"Rules" sections inside an AC are not optional notes — they are the business logic.**
Read them as `if/else` branches. Count the branches before writing any code.
If an AC lists an HTTP 409 response, there must be an exception class,
a service throw, and a handler in `GlobalExceptionHandler`.

---

## 6. AC6 Validation — Skipped Entirely

### What was required

| Rule | Implementation needed | Status |
|---|---|---|
| ISBN format (ISBN-10 or ISBN-13) | Custom validator or regex on DTO field | Not done |
| ISBN uniqueness | `existsByIsbn` in `BookRepository` + check in service | Not done |
| `availableCopies ≥ 0` | `@Min(0)` on DTO field | Not done (field missing) |
| `publishedYear` valid | `@Pattern` or `@Min`/`@Max` on DTO | Not done |
| `categoryId` must exist | Service call to `CategoryRepository` | Not done |

### What ISBN validation looks like
```java
// On CreateBookDto field
@NotBlank
@Pattern(
    regexp = "^(?:ISBN(?:-1[03])?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3})[- 0-9X]{13}$|97[89][0-9]{10}$|(?=(?:[0-9]+[- ]){4})[- 0-9]{17}$)(?:97[89][- ]?)?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$",
    message = "Invalid ISBN format"
)
private String isbn;
```

### Lesson
**AC6 (Validation) is always a full AC, not a checklist to skim.**
Every validation rule maps to an annotation on a DTO field or a manual check in the service.
If the validation requires DB access (uniqueness, foreign key existence),
it belongs in the service with a specific exception and HTTP 400 response.

---

## 7. AC7 Error Handling — HTTP 409 Not Wired Up

### What was missing
- No `ActiveLoanConflictException` class
- No `@ExceptionHandler` in `GlobalExceptionHandler` for HTTP 409
- Duplicate ISBN (HTTP 400) not handled

### What was needed

**New exception:**
```java
public class ActiveLoanConflictException extends RuntimeException {
    public ActiveLoanConflictException(String message) {
        super(message);
    }
}
```

**In `GlobalExceptionHandler`:**
```java
@ExceptionHandler(ActiveLoanConflictException.class)
public ResponseEntity<Map<String, String>> handleActiveLoansConflict(ActiveLoanConflictException ex) {
    Map<String, String> error = new HashMap<>();
    error.put("message", ex.getMessage());
    error.put("status", "409");
    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
}
```

### Lesson
**Every distinct HTTP response code in AC7 needs its own exception class and handler.**
Before starting implementation, list every HTTP code in the ticket and make sure
each one has: an exception, a throw site, and a handler.

---

## 8. Tests — Zero Coverage on Book Feature

### What was required by Definition of Done
- Unit tests for `BookService` with >80% coverage
- Integration tests for `BookController`

### What was implemented
Nothing. All CRUD methods in `BookService` and all endpoints in `BookController`
were shipped with no tests.

### Minimum test cases needed

**`BookServiceTest` (unit):**
- `createBook_success`
- `createBook_duplicateIsbn_throwsException`
- `getBookById_found`
- `getBookById_notFound_throwsException`
- `getAllBooks_withCategoryFilter`
- `deleteBook_activeLoans_throws409`
- `deleteBook_historicalLoans_softDeletes`
- `deleteBook_noLoans_hardDeletes`
- `updateBook_success`
- `updateBook_notFound_throwsException`
- `updateBook_duplicateIsbn_throwsException`

**`BookControllerTest` (integration):**
- `DELETE /api/books/{id}` → 204 when no loans
- `DELETE /api/books/{id}` → 204 soft delete when historical loans
- `DELETE /api/books/{id}` → 409 when active loans
- `DELETE /api/books/{id}` → 404 when book not found
- `POST /api/books` → 201 with valid body
- `POST /api/books` → 400 with invalid ISBN

### Lesson
**Tests are not optional. The Definition of Done lists them explicitly.**
Write the test list before implementation — it forces you to think through every edge case.
The delete logic above has 4 distinct test cases for one endpoint.
If you had written the tests first, you would have caught the missing 409 case immediately.

---

## 9. Summary — Checklist to Use on Every Future Ticket

Before writing any code, answer these questions from the ticket:

- [ ] Listed every entity field with its type, nullability, and default value?
- [ ] Distinguished required fields from optional fields?
- [ ] Created all three DTOs (Create, Update, Response) with correct fields?
- [ ] Every AC has a method signature planned in the service interface?
- [ ] Every "Rules" section translated into `if/else` branches with HTTP codes?
- [ ] Every HTTP error code in AC7 has an exception class and a handler?
- [ ] Every validation rule in AC6 mapped to a DTO annotation or service check?
- [ ] Filtering/sorting requirements in AC3 mapped to repository methods?
- [ ] Written the test case list before starting implementation?
- [ ] All Definition of Done items checked before opening the PR?
