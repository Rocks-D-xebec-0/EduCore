package com.hem.EduCore.service.impl;

import com.hem.EduCore.dto.Response.AuthorResponseDto;
import com.hem.EduCore.dto.Request.CreateAuthorDto;
import com.hem.EduCore.dto.Request.UpdateAuthorDto;
import com.hem.EduCore.entity.Author;
import com.hem.EduCore.exception.DuplicateResourceException;
import com.hem.EduCore.exception.ResourceNotFoundException;
import com.hem.EduCore.mapper.AuthorMapper;
import com.hem.EduCore.repository.AuthorRepository;
import com.hem.EduCore.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class AuthorServiceImpl implements AuthorService {

    private final AuthorMapper authorMapper;
    private final AuthorRepository authorRepository;

    @Override
    public AuthorResponseDto createAuthor(CreateAuthorDto request) {
        if (authorRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Author with email " + request.getEmail() + " already exists");
        }
        return authorMapper.toResponseDto(authorRepository.save(authorMapper.toEntity(request)));
    }

    @Override
    @Transactional(readOnly = true)
    public AuthorResponseDto getAuthorById(Long id) {
        Author author = authorRepository.findById(id)
                .filter(a -> !a.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Author with id " + id + " not found"));
        return authorMapper.toResponseDto(author);
    }

    @Override
    public void deleteAuthor(Long id) {
        Author author = authorRepository.findById(id)
                .filter(a -> !a.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Author with id " + id + " not found"));
        author.setDeleted(true);
        authorRepository.save(author);
    }

    @Override
    public AuthorResponseDto fullUpdateAuthor(Long id, UpdateAuthorDto request) {
        if (request.getName() == null || request.getEmail() == null) {
            throw new IllegalArgumentException("Name and email are required for a full update");
        }
        Author author = authorRepository.findById(id)
                .filter(a -> !a.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Author with id " + id + " not found"));
        if (authorRepository.existsByEmailAndAuthorIdNot(request.getEmail(), id)) {
            throw new DuplicateResourceException("Author with email " + request.getEmail() + " already exists");
        }
        author.setName(request.getName());
        author.setEmail(request.getEmail());
        author.setBio(request.getBio());
        return authorMapper.toResponseDto(authorRepository.save(author));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuthorResponseDto> getAllAuthors(Pageable pageable) {
        return authorRepository.findAllByIsDeletedFalse(pageable)
                .map(authorMapper::toResponseDto);
    }

    @Override
    public AuthorResponseDto partialUpdate(Long id, UpdateAuthorDto request) {
        Author author = authorRepository.findById(id)
                .filter(a -> !a.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Author with id " + id + " not found"));
        if (request.getName() != null) {
            author.setName(request.getName());
        }
        if (request.getEmail() != null) {
            if (authorRepository.existsByEmailAndAuthorIdNot(request.getEmail(), id)) {
                throw new DuplicateResourceException("Author with email " + request.getEmail() + " already exists");
            }
            author.setEmail(request.getEmail());
        }
        if (request.getBio() != null) {
            author.setBio(request.getBio());
        }
        return authorMapper.toResponseDto(authorRepository.save(author));
    }
}
