# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview


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
│   │   ├── service/          # Business logic interfaces
│   │   ├── service/impl/     # Service implementations
│   │   ├── entity/           # JPA entities (Member, Book, Author, Loan, Category)
│   │   └── EduCoreApplication.java
│   └── resources/
└── test/
    └── java/com/hem/EduCore/
```

## Layered Architecture






## Common Commands

```bash
mvn clean install

mvn spring-boot:run

mvn test


```

## Database Configuration

```yaml
datasource:
  url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:educore_db}
  username: ${DB_USERNAME:postgres}
  password: ${DB_PASSWORD:postgres}
```


```bash
createdb educore_db
```






## Git Workflow

- **User:** `hem`


