package com.hem.EduCore.mapper;


import com.hem.EduCore.dto.Response.AuthorResponseDto;
import com.hem.EduCore.dto.Request.CreateAuthorDto;
import com.hem.EduCore.entity.Author;
import org.springframework.stereotype.Component;

@Component
public class AuthorMapper {

    public Author toEntity(CreateAuthorDto request){


        Author author = new Author();

        author.setName(request.getName());
        author.setEmail(request.getEmail());
        author.setBio(request.getBio());

        return  author;



    }


    public AuthorResponseDto toResponseDto(Author author){
        AuthorResponseDto authorResponseDto=new AuthorResponseDto();
        authorResponseDto.setAuthorId(author.getAuthorId());
        authorResponseDto.setName(author.getName());
        authorResponseDto.setEmail(author.getEmail());
        authorResponseDto.setBio(author.getBio());
        return  authorResponseDto;

    }
}
