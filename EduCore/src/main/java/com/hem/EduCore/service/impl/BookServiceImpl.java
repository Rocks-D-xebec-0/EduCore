package com.hem.EduCore.service.impl;


import com.hem.EduCore.dto.Reponse.BookResponseDto;
import com.hem.EduCore.dto.Request.CreateBookDto;
import com.hem.EduCore.dto.Request.UpdateBookDto;
import com.hem.EduCore.entity.Book;
import com.hem.EduCore.mapper.BookMapper;
import com.hem.EduCore.repository.BookRepository;
import com.hem.EduCore.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private  final BookMapper bookMapper;
    private  final BookRepository bookRepository;


    @Override
    public BookResponseDto createBook(CreateBookDto request) {


        Book book = bookMapper.toEntity(request);

        bookRepository.save(book);



        return  bookMapper.toResponseDto(book);
    }

    @Override
    public BookResponseDto getBookById(Long id) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found "));



        return  bookMapper.toResponseDto(book)  ;
    }

    @Override
    public Page<BookResponseDto> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(bookMapper::toResponseDto);

    }

    @Override
    public void softDeleteBook(Long id) {

        Book book
=        bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));


book.setDeleted(true);

bookRepository.save(book);


    }

    @Override
    public BookResponseDto updateBook(Long id, UpdateBookDto request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));


        book.setTitle(request.getTitle());
        book.setDescription(request.getDescription());
        book.setPages(request.getPages());

        bookRepository.save(book);

        return  bookMapper.toResponseDto(book );
    }
}
