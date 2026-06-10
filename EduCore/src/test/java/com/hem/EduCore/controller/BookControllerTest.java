package com.hem.EduCore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hem.EduCore.dto.Response.BookResponseDto;
import com.hem.EduCore.exception.ResourceNotFoundException;
import com.hem.EduCore.dto.Request.CreateBookDto;
import com.hem.EduCore.dto.Request.UpdateBookDto;
import com.hem.EduCore.exception.ActiveLoanConflictException;
import com.hem.EduCore.exception.DuplicateResourceException;
import com.hem.EduCore.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("BookController Integration Tests")
class BookControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean  private BookService bookService;

    private BookResponseDto bookResponseDto;
    private CreateBookDto createBookDto;
    private UpdateBookDto updateBookDto;

    @BeforeEach
    void setUp() {
        bookResponseDto = new BookResponseDto();
        bookResponseDto.setId(1L);
        bookResponseDto.setTitle("Clean Code");
        bookResponseDto.setIsbn("9780132350884");
        bookResponseDto.setCategoryId(1);
        bookResponseDto.setCategoryName("Fiction");
        bookResponseDto.setAvailableCopies(3);

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

    // ==================== POST /api/books ====================

    @Test
    @DisplayName("POST /api/books should return 201 on success")
    void createBook_returns201() throws Exception {
        when(bookService.createBook(any(CreateBookDto.class))).thenReturn(bookResponseDto);

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createBookDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.isbn").value("9780132350884"));
    }

    @Test
    @DisplayName("POST /api/books should return 400 when title is missing")
    void createBook_missingTitle_returns400() throws Exception {
        createBookDto.setTitle("");

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createBookDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/books should return 400 when ISBN format is invalid")
    void createBook_invalidIsbn_returns400() throws Exception {
        createBookDto.setIsbn("INVALID");

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createBookDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/books should return 400 on duplicate ISBN")
    void createBook_duplicateIsbn_returns400() throws Exception {
        when(bookService.createBook(any(CreateBookDto.class)))
                .thenThrow(new DuplicateResourceException("Book with ISBN already exists"));

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createBookDto)))
                .andExpect(status().isBadRequest());
    }

    // ==================== GET /api/books/{id} ====================

    @Test
    @DisplayName("GET /api/books/{id} should return 200 with book details")
    void getBookById_returns200() throws Exception {
        when(bookService.getBookById(1L)).thenReturn(bookResponseDto);

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Clean Code"))
                .andExpect(jsonPath("$.categoryName").value("Fiction"));
    }

    @Test
    @DisplayName("GET /api/books/{id} should return 404 when book not found")
    void getBookById_notFound_returns404() throws Exception {
        when(bookService.getBookById(99L))
                .thenThrow(new ResourceNotFoundException("Book not found"));

        mockMvc.perform(get("/api/books/99"))
                .andExpect(status().isNotFound());
    }

    // ==================== GET /api/books ====================

    @Test
    @DisplayName("GET /api/books should return 200 with paginated list")
    void getAllBooks_returns200() throws Exception {
        when(bookService.getAllBooks(eq(null), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(bookResponseDto)));

        mockMvc.perform(get("/api/books?page=0&size=10"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/books?categoryId=1 should return filtered results")
    void getAllBooks_withCategoryFilter_returns200() throws Exception {
        when(bookService.getAllBooks(eq(1), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(bookResponseDto)));

        mockMvc.perform(get("/api/books?categoryId=1&page=0&size=10"))
                .andExpect(status().isOk());
    }

    // ==================== PUT /api/books/{id} ====================

    @Test
    @DisplayName("PUT /api/books/{id} should return 200 on success")
    void updateBook_returns200() throws Exception {
        when(bookService.updateBook(eq(1L), any(UpdateBookDto.class))).thenReturn(bookResponseDto);

        mockMvc.perform(put("/api/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateBookDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("PUT /api/books/{id} should return 404 when book not found")
    void updateBook_notFound_returns404() throws Exception {
        when(bookService.updateBook(eq(99L), any(UpdateBookDto.class)))
                .thenThrow(new ResourceNotFoundException("Book not found"));

        mockMvc.perform(put("/api/books/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateBookDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/books/{id} should return 400 on duplicate ISBN")
    void updateBook_duplicateIsbn_returns400() throws Exception {
        when(bookService.updateBook(eq(1L), any(UpdateBookDto.class)))
                .thenThrow(new DuplicateResourceException("Book with ISBN already exists"));

        mockMvc.perform(put("/api/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateBookDto)))
                .andExpect(status().isBadRequest());
    }

    // ==================== DELETE /api/books/{id} ====================

    @Test
    @DisplayName("DELETE /api/books/{id} should return 204 on successful delete")
    void deleteBook_returns204() throws Exception {
        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/books/{id} should return 404 when book not found")
    void deleteBook_notFound_returns404() throws Exception {
        doThrow(new ResourceNotFoundException("Book not found")).when(bookService).deleteBook(99L);

        mockMvc.perform(delete("/api/books/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/books/{id} should return 409 when active loans exist")
    void deleteBook_activeLoans_returns409() throws Exception {
        doThrow(new ActiveLoanConflictException("Cannot delete book with active loans"))
                .when(bookService).deleteBook(1L);

        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Cannot delete book with active loans"));
    }
}
