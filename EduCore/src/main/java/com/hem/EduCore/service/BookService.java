package com.hem.EduCore.service;

import com.hem.EduCore.dto.Reponse.BookResponseDto;
import com.hem.EduCore.dto.Request.CreateBookDto;
import com.hem.EduCore.dto.Request.UpdateBookDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {


    BookResponseDto createBook(CreateBookDto request);
    BookResponseDto getBookById(Long id );
    Page<BookResponseDto> getAllBooks(Pageable pageable);
    void deleteBook(Long id);
    BookResponseDto updateBook(Long id, UpdateBookDto request );
}
