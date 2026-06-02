# Common Errors in JPA/Hibernate Relationships - Learning Guide

## Error #1: Bidirectional Many-to-Many Without `mappedBy`

### ❌ **The Problem (Before)**

**File:** `Category.java`

```java
@Entity
public class Category extends BaseEntity{
    
    @ManyToMany  // ← WRONG: No @JoinTable and no mappedBy
    private Set<Book> books = new HashSet<>();
}
```

**File:** `Book.java`

```java
@Entity
public class Book extends BaseEntity{
    
    @ManyToMany
    @JoinTable(
        name = "book_categories",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();
}
```

---

### 🚨 **What It Can Cause**

1. **Duplicate Join Tables:**
   - Hibernate creates **TWO separate join tables** instead of one:
     - `book_categories` (defined in Book.java)
     - `category_book` (auto-generated for Category.java)
   - This leads to data inconsistency

2. **Data Integrity Issues:**
   - Inserting a category-book relationship in one table doesn't appear in the other
   - Queries return incomplete results
   - Data becomes out of sync

3. **Runtime Errors:**
   ```
   Foreign key constraint fails
   Data appears to exist but queries don't find it
   ```

4. **Query Problems:**
   ```java
   Category cat = categoryRepo.findById(1);
   cat.getBooks();  // Returns empty, even though books exist!
   ```

5. **Database Confusion:**
   - DBA sees duplicate tables: `book_categories` AND `category_book`
   - Both tables partially contain data
   - Complex to debug and maintain

---

### ✅ **Solution**

Use `mappedBy` on the **non-owning side** (the side that doesn't define `@JoinTable`).

**File:** `Category.java` - FIXED

```java
@Entity
@Table(name = "categories")
public class Category extends BaseEntity{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int category_id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String description;
    
    // ✅ CORRECT: Use mappedBy to reference the owning side
    @ManyToMany(mappedBy = "categories")
    private Set<Book> books = new HashSet<>();
}
```

**Key Points:**
- `mappedBy = "categories"` → points to the `categories` field in Book.java
- Category.java is now the **non-owning side**
- Book.java remains the **owning side** (defines the join table)
- Only ONE join table is created: `book_categories`

---

## Error #2: Same Issue with Author-Book Relationship

### ❌ **The Problem (Before)**

**File:** `Author.java`

```java
@Entity
public class Author extends BaseEntity{
    
    @ManyToMany  // ← WRONG: No @JoinTable and no mappedBy
    private Set<Book> books = new HashSet<>();
}
```

**File:** `Book.java`

```java
@Entity
public class Book extends BaseEntity{
    
    @ManyToMany
    @JoinTable(
        name = "book_authors",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Author> authors = new HashSet<>();
}
```

---

### 🚨 **What It Can Cause** (Same as Error #1)

1. **Duplicate Join Tables:** `book_authors` AND `author_book`
2. **Data inconsistency** when adding/removing relationships
3. **Broken queries** - data exists but can't be retrieved
4. **Database maintenance nightmare** with orphaned tables

---

### ✅ **Solution**

**File:** `Author.java` - FIXED

```java
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "authors")
public class Author extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int author_id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column
    private String bio;
    
    // ✅ CORRECT: Use mappedBy to reference the owning side
    @ManyToMany(mappedBy = "authors")
    private Set<Book> books = new HashSet<>();
}
```

---

## 📚 **Key Learning Points**

### **Bidirectional M:M Rule:**
- **Only ONE side** should define `@JoinTable` (the **owning side**)
- The **other side** must use `mappedBy = "fieldNameInOwningEntity"`

### **How to Choose the Owning Side:**
- Usually, the **most frequently queried** side owns the relationship
- In this case: Book owns both relationships
  - It's more common to query "Which categories does this book belong to?" 
  - Than "Which books are in this category?"

### **Example Comparison:**

```
❌ WRONG (2 join tables)
─────────────────────────
Book.java → defines @JoinTable "book_categories"
Category.java → defines implicit @ManyToMany (creates "category_book")
Result: 2 tables created → DATA INCONSISTENCY

✅ CORRECT (1 join table)
─────────────────────────
Book.java → defines @JoinTable "book_categories" (OWNER)
Category.java → uses @ManyToMany(mappedBy = "categories") (NON-OWNER)
Result: 1 table → SINGLE SOURCE OF TRUTH
```

---

## 🔍 **How to Debug This**

If you already have duplicate tables in your database:

```sql
-- Check what join tables exist
SHOW TABLES LIKE '%book%';
SHOW TABLES LIKE '%author%';

-- You'll see both: 
-- book_categories, category_book
-- book_authors, author_book
```

**Fix:** Drop the auto-generated tables and let Hibernate recreate them correctly:
```sql
DROP TABLE category_book;
DROP TABLE author_book;
```

---

## 🎯 **Summary**

| Aspect | ❌ Wrong | ✅ Correct |
|--------|---------|-----------|
| Join Tables Created | 2 (duplicate) | 1 (single source of truth) |
| Data Consistency | Broken | Guaranteed |
| Query Results | Incomplete/Inconsistent | Accurate |
| Maintenance | Complex | Simple |
| Annotation Used | `@ManyToMany` only | `@JoinTable` on one side, `mappedBy` on other |

