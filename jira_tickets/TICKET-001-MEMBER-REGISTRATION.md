# TICKET-001: Member Registration & Management

**Status:** ✅ COMPLETED

**Story Type:** Feature

**Epic:** Library Core System

---

## Title
As a librarian, I want to register new members in the system so that I can track who uses the library.

## Description
The library management system must provide functionality for library staff to create, view, and manage member records. Members are a core entity within the system and are required before loan management can be performed.

The solution should expose REST API endpoints that allow staff to create new members, retrieve member information, and browse existing members through a paginated list.

## Business Value
Maintaining accurate member records enables the library to:
- Track library users
- Associate loans with registered members
- Support future borrowing and reservation features
- Maintain member contact information

## Acceptance Criteria

### AC1: Create Member
- **Given** a librarian wants to register a new member
- **When** a valid request is submitted to `POST /api/members`
- **Then** the system creates the member record and returns the created member including a unique ID

**Required Fields:**
- `name` (String, required)
- `email` (String, required, unique)
- `phone` (String, optional)
- `address` (String, required)

**Response:**
- HTTP 201 Created
- Member object with generated ID, createdAt, updatedAt timestamps

### AC2: Retrieve Member by ID
- **Given** a librarian wants to view a specific member's details
- **When** a request is submitted to `GET /api/members/{id}`
- **Then** the system returns the member details
- **And** returns HTTP 404 if member does not exist

### AC3: List All Members (Paginated)
- **Given** a librarian wants to view all members
- **When** a request is submitted to `GET /api/members?page=0&size=10`
- **Then** the system returns a paginated list of members
- **And** supports pagination parameters (page, size)

### AC4: Validation Rules
- Email must be valid format and unique in the system
- Name must not be empty
- Phone format must be valid if provided
- Address must not be empty
- Return HTTP 400 with field-level error messages for invalid input

### AC5: Error Handling
- Duplicate email returns HTTP 400 with DuplicateResourceException
- Invalid member ID returns HTTP 404 with "Not found" message
- Validation errors return HTTP 400 with field error details

## Technical Details

### API Endpoints
```
POST   /api/members              → Create new member
GET    /api/members/{id}         → Get member by ID
GET    /api/members              → List all members (paginated)
```

### Entity Model
```java
Member {
  member_id: Long (PK, auto-generated)
  name: String (required)
  email: String (required, unique)
  phone: String (optional)
  address: String (required)
  membershipDate: LocalDate
  createdAt: LocalDateTime (auto)
  updatedAt: LocalDateTime (auto)
  loans: List<Loan> (one-to-many)
}
```

### DTO Models
```java
CreateMemberDto {
  name: String
  email: String
  phone: String
  address: String
}

MemberResponseDto {
  memberId: Long
  name: String
  email: String
  phone: String
  address: String
  membershipDate: LocalDate
  createdAt: LocalDateTime
  updatedAt: LocalDateTime
}
```

## Implementation Checklist
- [x] Create `Member` entity with BaseEntity inheritance
- [x] Create `MemberRepository` (JPA interface)
- [x] Create `MemberService` interface
- [x] Create `MemberServiceImpl` with CRUD logic
- [x] Create DTOs (`CreateMemberDto`, `MemberResponseDto`)
- [x] Create `MemberMapper` for entity-to-DTO conversion
- [x] Create `MemberController` with REST endpoints
- [x] Add OpenAPI/Swagger annotations
- [x] Create `DuplicateResourceException` custom exception
- [x] Create `GlobalExceptionHandler` for exception handling
- [x] Implement validation (email uniqueness, field validation)
- [x] Create unit tests for `MemberService` (MemberServiceTest)
- [x] Create integration tests for `MemberController` (MemberControllerTest)
- [x] Update API documentation

## Definition of Done
- [x] All AC1-AC5 acceptance criteria met
- [x] API endpoints tested via Postman/curl
- [x] Data persists to PostgreSQL correctly
- [x] Timestamps (createdAt/updatedAt) auto-populate correctly
- [x] All unit tests pass
- [x] All integration tests pass
- [x] Exception handling returns proper HTTP status codes
- [x] OpenAPI/Swagger documentation is accurate
- [x] Code reviewed and merged to main branch

## Notes
- Email validation enforces uniqueness at database constraint level
- Pagination uses Spring Data's `Pageable` interface
- DTOs prevent infinite JSON serialization loops when returning member objects
- `BaseEntity` handles createdAt/updatedAt lifecycle automatically via JPA callbacks

---

**Completed Date:** 2026-06-04
**Completed By:** hem
**Branch:** feat/member-registration
