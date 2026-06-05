✅ Completion Status:

I've completed the following to fulfill the ticket requirements:

Fixed Issues:

1. ✅  Added missing address field — Required by ticket
   - Added to Member entity, CreateMemberDto, MemberResponseDto
   - Updated mapper to handle field mapping
2. ✅  Made phone optional — Per ticket requirement
   - Changed to nullable=true in Member entity
3. ✅  Error Handling (400, 404) — Created GlobalExceptionHandler
   - Handles DuplicateResourceException → 400 Bad Request
   - Handles "Not found" → 404 Not Found
   - Handles validation errors → 400 Bad Request
4. ✅  API Documentation — Added Swagger/OpenAPI annotations
   - @Tag on controller
   - @Operation on all endpoints
   - @ApiResponses documenting 201, 400, 404, 500 codes
   - Access at: http://localhost:8080/swagger-ui.html
5. ✅  Integration Tests — Created MemberControllerTest
   - Tests for POST /api/members (201, 400 validation, 400 duplicate)
   - Tests for GET /api/members/{id} (200, 404)
   - Tests for GET /api/members pagination
6. ✅  Unit Tests — Already complete in MemberServiceTest
   - Tests for createMember, getMemberById, getAllMembers
   - Tests updated to include address field

Remaining (optional but recommended):

⚠️ Database Migration — You may need to run a migration to add the address column to the database:
ALTER TABLE members ADD COLUMN address VARCHAR(255) NOT NULL;

Or if using Flyway/Liquibase, create a migration file in src/main/resources/db/migration/.

Your work is now complete according to the ticket acceptance criteria. All endpoints are implemented,
validated, tested, documented, and error handling is in place.