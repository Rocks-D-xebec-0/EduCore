package com.hem.EduCore.mapper;

import com.hem.EduCore.dto.Reponse.BookResponseDto;
import com.hem.EduCore.dto.Request.CreateBookDto;
import com.hem.EduCore.entity.Book;
import com.hem.EduCore.entity.Member;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    public Book toEntity(CreateBookDto request){

        Book book = new Book();

        book.setTitle(request.getTitle());
        book.setDescription(request.getDescription());
        book.setPages(request.getPages());


        return  book ;



    }



     public BookResponseDto toResponseDto(Book book ){

        BookResponseDto bookResponseDto=new BookResponseDto();

        bookResponseDto.setId(book.getBook_id());
        bookResponseDto.setTitle(book.getTitle());
        bookResponseDto.setDescription(bookResponseDto.getDescription());
         bookResponseDto.setPublicationYear(book.getPublicationYear());
         bookResponseDto.setPages(book.getPages());

         return  bookResponseDto;


     }
}
