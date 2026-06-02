# JPA @JoinTable Annotation Guide

## Overview
The `@JoinTable` annotation is used in many-to-many relationships to configure the join table that stores the relationship between two entities.

## Why Do We Need @JoinTable?

In a many-to-many relationship, JPA must create a third table to manage the relationship:

```
Book <----> Author

Becomes:

┌─────────┐         ┌─────────────┐         ┌────────┐
│  books  │         │ book_authors│         │ authors│
└─────────┘         └─────────────┘         └────────┘
```

### What @JoinTable Does
`@JoinTable` tells Hibernate/JPA: *"Use this table to store the relationship between Book and Author."*

## Owning Side vs. Inverse Side

One side must be the **owner** of the relationship (where you put @JoinTable):

```
Book (owner) -------- Author (inverse side)
```

### Can I Put @JoinTable on Author Instead?
**Yes.** You can place `@JoinTable` on either side of the relationship. The side where you put the annotation becomes the owning side.

## Common Mistakes

### ❌ Both Sides Have @JoinTable
Hibernate will see conflicting table definitions:

```
book_authors     (from Book)
author_books     (from Author)
```

This creates confusion and incorrect behavior.

### ✅ Best Practice
Place `@JoinTable` on only ONE side of the relationship.



-------------------------------
Note
In relational databases

A database doesn't talk about "owner" and "inverse" sides. Instead, it talks about:

Parent table (referenced table)
Child table (referencing table)
Primary key (PK)
Foreign key (FK)





Summary
Database Term	JPA/Hibernate Term
Parent table	Referenced entity
Child table	Referencing entity
Foreign key holder	Owning side
Other side of bidirectional relationship	Inverse side
PK/FK relationship	Association