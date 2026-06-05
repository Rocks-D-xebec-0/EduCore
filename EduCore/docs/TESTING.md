# Testing Guide

## Mockito

Mockito is a mocking framework. It lets you create fake objects (mocks) so you can test a class without using its real dependencies.

### Example Usage

```java
@Mock
private MemberRepository repository;
```

Instead of connecting to a real database, the mock can return predefined values:

```java
when(repository.findById(1L))
        .thenReturn(Optional.of(member));
```

## JUnit 5

JUnit 5 is a testing framework. It provides:

- The `@Test` annotation
- Assertions like `assertEquals()`, `assertTrue()`, etc.
- Test lifecycle methods (`@BeforeEach`, `@AfterEach`)
- Test execution and reporting

### Example Test

```java
@Test
void testSomething() {
    assertEquals(expected, actual);
}
```

## JUnit 5 and Mockito Together

JUnit 5 and Mockito are two different testing tools that are commonly used together in Java.

To use Mockito with JUnit 5, use the `@ExtendWith(MockitoExtension.class)` annotation:

```java
@ExtendWith(MockitoExtension.class)
class MyServiceTest {
    
    @Mock
    private MemberRepository repository;
    
    @InjectMocks
    private MemberService service;
    
    @Test
    void testFindMember() {
        when(repository.findById(1L))
                .thenReturn(Optional.of(new Member()));
        
        assertTrue(service.findMember(1L).isPresent());
    }
}
```
