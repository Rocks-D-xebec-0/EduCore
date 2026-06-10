package com.hem.EduCore.repository;

import com.hem.EduCore.entity.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {

    boolean existsByEmail(String email);
    boolean existsByEmailAndAuthorIdNot(String email, Long authorId);
    Page<Author> findAllByIsDeletedFalse(Pageable pageable);
}
