package com.hem.EduCore.repository;

import com.hem.EduCore.entity.Book;
import com.hem.EduCore.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Integer> {
    boolean existsByBook(Book book);
}
