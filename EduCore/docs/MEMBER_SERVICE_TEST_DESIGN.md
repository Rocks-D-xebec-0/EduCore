# MemberService Unit Test Design

## Overview
Comprehensive unit tests for `MemberService` using JUnit 5 and Mockito. The test suite covers:
- Create member functionality
- Retrieve member by ID
- Retrieve all members with pagination
- Error handling and edge cases

## Test Structure

### Setup
```java
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
    @Mock
    private MemberRepository memberRepository;
    
    @InjectMocks
    private MemberServiceImpl memberService;
}
```

- `@Mock`: Creates a fake MemberRepository
- `@InjectMocks`: Automatically injects the mock into MemberServiceImpl
- `@BeforeEach`: Initializes test data before each test

## Test Categories

### 1. Create Member Tests (5 tests)

| Test | Purpose | Expected |
|------|---------|----------|
| `testCreateMemberSuccess` | Happy path - member created successfully | Returns MemberResponseDto with correct data |
| `testCreateMemberSavesCorrectEmail` | Verifies email is saved correctly | Repository receives member with correct email |
| `testCreateMemberSetsMembershipDate` | Verifies membership date is set | Member has current date |
| `testCreateMemberWithNullInput` | Rejects null input | Throws IllegalArgumentException |
| `testCreateMemberWithNullEmail` | Rejects null email | Throws IllegalArgumentException |

### 2. Get Member by ID Tests (4 tests)

| Test | Purpose | Expected |
|------|---------|----------|
| `testGetMemberByIdSuccess` | Happy path - member found | Returns MemberResponseDto |
| `testGetMemberByIdNotFound` | Member doesn't exist | Throws RuntimeException |
| `testGetMemberByIdCallsRepositoryWithCorrectId` | Verifies correct ID passed | Repository called with exact ID |
| `testGetMemberByIdWithNegativeId` | Rejects invalid ID | Throws IllegalArgumentException |

### 3. Get All Members Tests (4 tests)

| Test | Purpose | Expected |
|------|---------|----------|
| `testGetAllMembersSuccess` | Happy path with pagination | Returns page with members |
| `testGetAllMembersEmptyPage` | No members exist | Returns empty page |
| `testGetAllMembersRespectsPagination` | Respects page parameters | Correct page size/number |
| `testGetAllMembersWithMultipleMembers` | Multiple members in page | All members returned |

### 4. Edge Cases & Integration Tests (2 tests)

| Test | Purpose | Expected |
|------|---------|----------|
| `testValidationFailureDoesNotCallRepository` | Invalid input = no DB call | Repository not called |
| `testVerifyNoUnexpectedCalls` | Only expected calls made | No extra interactions with mock |

## Key Testing Patterns

### 1. Mocking with When/Then
```java
when(memberRepository.findById(1L))
    .thenReturn(Optional.of(testMember));

MemberResponseDto result = memberService.getMemberById(1L);
```

### 2. Argument Matching
```java
verify(memberRepository).save(argThat(member ->
    member.getEmail().equals("john@example.com")
));
```

### 3. Verification
```java
verify(memberRepository, times(1)).save(any(Member.class));
verify(memberRepository, never()).save(any(Member.class));
verifyNoMoreInteractions(memberRepository);
```

### 4. Assertions
```java
assertNotNull(result);
assertEquals("John Doe", result.getName());
assertThrows(IllegalArgumentException.class, () -> {...});
```

## Running the Tests

```bash
# Run all tests
mvn test

# Run only MemberService tests
mvn test -Dtest=MemberServiceTest

# Run with detailed output
mvn test -e -X
```

## Test Coverage

- **Happy Path**: Create, retrieve, list operations ✓
- **Error Cases**: Not found, invalid input ✓
- **Validation**: Null checks, empty strings ✓
- **Pagination**: Page size, page number, empty results ✓
- **Verification**: Correct method calls, no extra calls ✓

## Dependencies Required

Add to `pom.xml`:

```xml
<!-- JUnit 5 -->
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.9.0</version>
    <scope>test</scope>
</dependency>

<!-- Mockito -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <version>5.3.0</version>
    <scope>test</scope>
</dependency>

<!-- Spring Test -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

## Notes

- Tests use `@DisplayName` for readable test names
- Each test follows AAA pattern: Arrange → Act → Assert
- Tests are independent and can run in any order
- `setUp()` provides consistent test data
- No database calls - repository is mocked
