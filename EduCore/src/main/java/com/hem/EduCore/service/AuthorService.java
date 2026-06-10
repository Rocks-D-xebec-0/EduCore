package com.hem.EduCore.service;

import com.hem.EduCore.dto.Response.AuthorResponseDto;
import com.hem.EduCore.dto.Request.CreateAuthorDto;
import com.hem.EduCore.dto.Request.UpdateAuthorDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuthorService {



    AuthorResponseDto createAuthor(CreateAuthorDto request) ;
    AuthorResponseDto getAuthorById(Long id);
    void deleteAuthor(Long id );
    AuthorResponseDto fullUpdateAuthor(Long id , UpdateAuthorDto request);
    Page<AuthorResponseDto> getAllAuthors(Pageable pageable);
    AuthorResponseDto partialUpdate(Long id , UpdateAuthorDto request);
}
