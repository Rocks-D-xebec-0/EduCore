package com.hem.EduCore.service;

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
import com.hem.EduCore.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookService Tests")
class BookServiceTest {

    @Mock private BookMapper bookMapper;
    @Mock private BookRepository bookRepository;
    @Mock private LoanRepository loanRepository;
    @Mock private CategoryRepository categoryRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book testBook;
    private Category testCategory;
    private BookResponseDto testBookResponseDto;
    private CreateBookDto createBookDto;
    private UpdateBookDto updateBookDto;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setCategory_id(1);
        testCategory.setName("Fiction");

        testBook = new Book();
        testBook.setBook_id(1L);
        testBook.setTitle("Clean Code");
        testBook.setIsbn("9780132350884");
        testBook.setAvailableCopies(3);

        testBookResponseDto = new BookResponseDto();
        testBookResponseDto.setId(1L);
        testBookResponseDto.setTitle("Clean Code");
        testBookResponseDto.setIsbn("9780132350884");
        testBookResponseDto.setCategoryId(1);
        testBookResponseDto.setCategoryName("Fiction");

        createBookDto = new CreateBookDto();
        createBookDto.setTitle("Clean Code");
        createBookDto.setIsbn("9780132350884");
        createBookDto.setCategoryId(1);
        createBookDto.setAvailableCopies(3);

        updateBookDto = new UpdateBookDto();
        updateBookDto.setTitle("Clean Code Updated");
        updateBookDto.setIsbn("9780132350884");
        updateBookDto.setCategoryId(1);
        updateBookDto.setAvailableCopies(5);
    }

    // ==================== CREATE BOOK ====================

    @Test
    @DisplayName("Should create book successfully")
    void createBook_success() {
        when(bookRepository.existsByIsbn(createBookDto.getIsbn())).thenReturn(false);
        when(categoryRepository.findById(createBookDto.getCategoryId())).thenReturn(Optional.of(testCategory));
        when(bookMapper.toEntity(createBookDto)).thenReturn(testBook);
        when(bookRepository.save(testBook)).thenReturn(testBook);
        when(bookMapper.toResponseDto(testBook)).thenReturn(testBookResponseDto);

        BookResponseDto result = bookService.createBook(createBookDto);

        assertNotNull(result);
        assertEquals("Clean Code", result.getTitle());
        assertEquals("9780132350884", result.getIsbn());
        verify(bookRepository).save(testBook);
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when ISBN already exists")
    void createBook_duplicateIsbn_throwsException() {
        when(bookRepository.existsByIsbn(createBookDto.getIsbn())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> bookService.createBook(createBookDto));
        verify(bookRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw RuntimeException when category not found")
    void createBook_categoryNotFound_throwsException() {
        when(bookRepository.existsByIsbn(createBookDto.getIsbn())).thenReturn(false);
        when(categoryRepository.findById(createBookDto.getCategoryId())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> bookService.createBook(createBookDto));
        verify(bookRepository, never()).save(any());
    }

    // ==================== GET BOOK BY ID ====================

    @Test
    @DisplayName("Should return book when found by ID")
    void getBookById_found() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookMapper.toResponseDto(testBook)).thenReturn(testBookResponseDto);

        BookResponseDto result = bookService.getBookById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(bookRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw RuntimeException when book not found by ID")
    void getBookById_notFound_throwsException() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> bookService.getBookById(99L));
    }

    // ==================== GET ALL BOOKS ====================

    @Test
    @DisplayName("Should return all books without category filter")
    void getAllBooks_withoutFilter_returnsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(List.of(testBook), pageable, 1);

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toResponseDto(testBook)).thenReturn(testBookResponseDto);

        Page<BookResponseDto> result = bookService.getAllBooks(null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(bookRepository).findAll(pageable);
        verify(bookRepository, never()).findByCategoryId(any(), any());
    }

    @Test
    @DisplayName("Should return filtered books when categoryId provided")
    void getAllBooks_withCategoryFilter_returnsFilteredPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(List.of(testBook), pageable, 1);

        when(bookRepository.findByCategoryId(1, pageable)).thenReturn(bookPage);
        when(bookMapper.toResponseDto(testBook)).thenReturn(testBookResponseDto);

        Page<BookResponseDto> result = bookService.getAllBooks(1, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(bookRepository).findByCategoryId(1, pageable);
        verify(bookRepository, never()).findAll(pageable);
    }

    // ==================== DELETE BOOK ====================

    @Test
    @DisplayName("Should throw RuntimeException when deleting a book that does not exist")
    void deleteBook_notFound_throwsException() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> bookService.deleteBook(99L));
    }

    @Test
    @DisplayName("Should throw ActiveLoanConflictException when active loans exist")
    void deleteBook_activeLoans_throwsConflictException() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(loanRepository.existsByBookAndReturnDateIsNull(testBook)).thenReturn(true);

        assertThrows(ActiveLoanConflictException.class, () -> bookService.deleteBook(1L));
        verify(bookRepository, never()).delete(any());
        verify(bookRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should soft-delete book when only historical loans exist")
    void deleteBook_historicalLoansOnly_softDeletes() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(loanRepository.existsByBookAndReturnDateIsNull(testBook)).thenReturn(false);
        when(loanRepository.existsByBookAndReturnDateIsNotNull(testBook)).thenReturn(true);

        bookService.deleteBook(1L);

        assertTrue(testBook.isDeleted());
        verify(bookRepository).save(testBook);
        verify(bookRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should hard-delete book when no loans exist")
    void deleteBook_noLoans_hardDeletes() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(loanRepository.existsByBookAndReturnDateIsNull(testBook)).thenReturn(false);
        when(loanRepository.existsByBookAndReturnDateIsNotNull(testBook)).thenReturn(false);

        bookService.deleteBook(1L);

        verify(bookRepository).delete(testBook);
        verify(bookRepository, never()).save(any());
    }

    // ==================== UPDATE BOOK ====================

    @Test
    @DisplayName("Should update book successfully")
    void updateBook_success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookRepository.existsByIsbnAndNotId(updateBookDto.getIsbn(), 1L)).thenReturn(false);
        when(categoryRepository.findById(updateBookDto.getCategoryId())).thenReturn(Optional.of(testCategory));
        when(bookRepository.save(testBook)).thenReturn(testBook);
        when(bookMapper.toResponseDto(testBook)).thenReturn(testBookResponseDto);

        BookResponseDto result = bookService.updateBook(1L, updateBookDto);

        assertNotNull(result);
        assertEquals("Clean Code Updated", testBook.getTitle());
        verify(bookRepository).save(testBook);
    }

    @Test
    @DisplayName("Should throw RuntimeException when updating a book that does not exist")
    void updateBook_notFound_throwsException() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> bookService.updateBook(99L, updateBookDto));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when ISBN belongs to another book")
    void updateBook_duplicateIsbn_throwsException() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookRepository.existsByIsbnAndNotId(updateBookDto.getIsbn(), 1L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> bookService.updateBook(1L, updateBookDto));
        verify(bookRepository, never()).save(any());
    }
}
