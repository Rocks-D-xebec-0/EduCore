# User Stories

## **Story #1: Member Registration & Management**

### **Title:** 
As a librarian, I want to register new members in the system so that I can track who uses the library

### **Description:**
The system should allow library staff to create, view, and list members. This is the foundation for the library system—without members, we can't manage loans.

### **Acceptance Criteria:**

1. **Create Member**
   - API endpoint: `POST /api/members`
   - Fields: name, email, phone, address
   - Returns created member with ID

2. **Get Member by ID**
   - API endpoint: `GET /api/members/{id}`
   - Returns member details or 404 if not found

3. **List All Members**
   - API endpoint: `GET /api/members`
   - Returns paginated list of members

4. **Data Validation**
   - Email must be valid format
   - Name is required
   - Phone is optional but valid if provided

5. **Timestamps**
   - createdAt and updatedAt auto-managed

### **What to Build:**

- [ ] `MemberRepository` (JPA interface)
- [ ] `MemberService` (business logic)
- [ ] `MemberController` (REST endpoints)
- [ ] Basic validation in Service layer
- [ ] Test the happy path (POST, GET, LIST)

### **Definition of Done:**

- All 3 endpoints work via Postman/curl
- Data persists to PostgreSQL
- createdAt/updatedAt populate correctly

---

## **Technical Note: Why Use DTOs Instead of Entities?**

### **The Problem with Returning Entities Directly:**

Your entity contains:
```java
private List<Loan> loans;
```

Returning the entity directly causes:
- **Infinite JSON recursion** — Member → Loan → Member → Loan → ... (circular dependency)
- **Exposing internal database structure** — Clients see all fields, including IDs they shouldn't access
- **Performance issues from lazy loading** — Jackson tries to serialize lazy-loaded collections, triggering database queries

### **The Solution: Data Transfer Objects (DTOs)**

Create separate DTO classes for API responses:
```java
public class MemberDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    // NO loans list
}
```

**Benefits:**
- ✅ Control what data leaves your API
- ✅ Prevent circular references
- ✅ Keep database schema private
- ✅ Faster serialization (no lazy loading)
- ✅ Version APIs independently from entities

**Build DTOs alongside repositories in Story #1.**
