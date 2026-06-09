# Error Log & Learning

## 1. PostgreSQL Authentication Error (2026-06-03)

### Error Message
```
org.hibernate.exception.JDBCConnectionException: Unable to obtain isolated JDBC connection
[The server requested SCRAM-based authentication, but no password was provided.]

Caused by: org.postgresql.util.PSQLException: The server requested SCRAM-based 
authentication, but no password was provided.
```

### Root Cause
In `application.yaml` (line 7):
```yaml
password: ${DB_PASSWORD:}
```
The password environment variable had an **empty default value** (`:}` means default to empty string). PostgreSQL requires a password for SCRAM authentication but was receiving an empty string.

### Solution
Set a proper default password or make it required:
```yaml
# Option 1: With a default password (dev only)
password: ${DB_PASSWORD:postgres}

# Option 2: Require it (better for production)
# Omit the default and ensure DB_PASSWORD env var is always set
password: ${DB_PASSWORD}
```

### Prevention
- Always ensure database credentials have sensible defaults or are explicitly required
- Never leave password fields with empty defaults in production environments
- Use `.env` files for local development with actual credentials

---

## 2. Hibernate Dialect Error

### Error Message
```
Unable to resolve name [org.hibernate.dialect.PostgreSQL10Dialect] as strategy 
[org.hibernate.dialect.Dialect]
```

### Root Cause
In `application.yaml` (line 15):
```yaml
dialect: org.hibernate.dialect.PostgreSQL10Dialect
```
`PostgreSQL10Dialect` is **deprecated/removed** in Hibernate 7.2. This dialect class no longer exists.

### Solution
Update to a supported dialect:
```yaml
# For Hibernate 7.2.x
dialect: org.hibernate.dialect.PostgreSQLDialect
# or just use the auto-detection by removing the property entirely
```

### Prevention
- Check Hibernate documentation when upgrading versions
- Dialect names are frequently refactored; keep them generic when possible
- Test database connectivity after version upgrades
- Let Hibernate auto-detect dialects when possible (remove explicit dialect property)

---

## Summary: Configuration Fixes Needed
1. **application.yaml line 7**: Change `password: ${DB_PASSWORD:}` to `password: ${DB_PASSWORD:postgres}`
2. **application.yaml line 15**: Change `org.hibernate.dialect.PostgreSQL10Dialect` to `org.hibernate.dialect.PostgreSQLDialect`
