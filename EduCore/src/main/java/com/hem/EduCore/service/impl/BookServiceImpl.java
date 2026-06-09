package com.hem.EduCore.service.impl;

import com.hem.EduCore.dto.Reponse.BookResponseDto;
import com.hem.EduCore.dto.Request.CreateBookDto;
import com.hem.EduCore.dto.Request.UpdateBookDto;
import com.hem.EduCore.entity.Book;
import com.hem.EduCore.entity.Category;
import com.hem.EduCore.exception.ActiveLoanConflictException;
import com.hem.EduCore.exception.DuplicateResourceException;
import com.hem.EduCore.mapper.BookMapper;
import com.hem.EduCore.repository.BookRepository;
import com.hem.EduCore.repository.CategoryRepository;
import com.hem.EduCore.repository.LoanRepository;
import com.hem.EduCore.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BookServiceImpl implements BookService {

    private final BookMapper bookMapper;
    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public BookResponseDto createBook(CreateBookDto request) {
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new DuplicateResourceException("Book with ISBN " + request.getIsbn() + " already exists");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category Not found"));

        Book book = bookMapper.toEntity(request);
        book.getCategories().add(category);
        bookRepository.save(book);

        return bookMapper.toResponseDto(book);
    }

    @Override
    public BookResponseDto getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book Not found"));
        return bookMapper.toResponseDto(book);
    }

    @Override
    public Page<BookResponseDto> getAllBooks(Integer categoryId, Pageable pageable) {
        if (categoryId != null) {
            return bookRepository.findByCategoryId(categoryId, pageable)
                    .map(bookMapper::toResponseDto);
        }
        return bookRepository.findAll(pageable)
                .map(bookMapper::toResponseDto);
    }

    @Override
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book Not found"));

        if (loanRepository.existsByBookAndReturnDateIsNull(book)) {
            throw new ActiveLoanConflictException("Cannot delete book with active loans");
        }

        if (loanRepository.existsByBookAndReturnDateIsNotNull(book)) {
            book.setDeleted(true);
            bookRepository.save(book);
        } else {
            bookRepository.delete(book);
        }
    }

    @Override
    public BookResponseDto updateBook(Long id, UpdateBookDto request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book Not found"));

        if (bookRepository.existsByIsbnAndNotId(request.getIsbn(), id)) {
            throw new DuplicateResourceException("Book with ISBN " + request.getIsbn() + " already exists");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category Not found"));

        book.setTitle(request.getTitle());
        book.setIsbn(request.getIsbn());
        book.setDescription(request.getDescription());
        book.setPublisher(request.getPublisher());
        book.setPublishedYear(request.getPublishedYear());
        book.setAvailableCopies(request.getAvailableCopies());
        book.getCategories().clear();
        book.getCategories().add(category);
        bookRepository.save(book);

        return bookMapper.toResponseDto(book);
    }
}
