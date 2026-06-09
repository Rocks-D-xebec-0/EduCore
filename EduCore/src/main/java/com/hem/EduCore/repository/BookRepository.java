package com.hem.EduCore.repository;

import com.hem.EduCore.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByIsbn(String isbn);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN TRUE ELSE FALSE END FROM Book b WHERE b.isbn = :isbn AND b.book_id <> :bookId")
    boolean existsByIsbnAndNotId(@Param("isbn") String isbn, @Param("bookId") Long bookId);

    @Query("SELECT b FROM Book b JOIN b.categories c WHERE c.category_id = :categoryId")
    Page<Book> findByCategoryId(@Param("categoryId") Integer categoryId, Pageable pageable);
}
