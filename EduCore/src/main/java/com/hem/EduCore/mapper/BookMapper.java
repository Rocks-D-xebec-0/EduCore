package com.hem.EduCore.mapper;

import com.hem.EduCore.dto.Reponse.BookResponseDto;
import com.hem.EduCore.dto.Request.CreateBookDto;
import com.hem.EduCore.entity.Author;
import com.hem.EduCore.entity.Book;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class BookMapper {

    public Book toEntity(CreateBookDto request) {
        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setIsbn(request.getIsbn());
        book.setDescription(request.getDescription());
        book.setPublisher(request.getPublisher());
        book.setPublishedYear(request.getPublishedYear());
        book.setAvailableCopies(request.getAvailableCopies());
        return book;
    }

    public BookResponseDto toResponseDto(Book book) {
        BookResponseDto dto = new BookResponseDto();
        dto.setId(book.getBook_id());
        dto.setTitle(book.getTitle());
        dto.setIsbn(book.getIsbn());
        dto.setDescription(book.getDescription());
        dto.setPublisher(book.getPublisher());
        dto.setPublishedYear(book.getPublishedYear());
        dto.setAvailableCopies(book.getAvailableCopies());

        book.getCategories().stream().findFirst().ifPresent(c -> {
            dto.setCategoryId(c.getCategory_id());
            dto.setCategoryName(c.getName());
        });

        dto.setAuthorNames(
            book.getAuthors().stream()
                .map(Author::getName)
                .collect(Collectors.toList())
        );

        return dto;
    }
}
