package com.hem.EduCore.service;

import com.hem.EduCore.dto.Response.AuthorResponseDto;
import com.hem.EduCore.dto.Request.CreateAuthorDto;
import com.hem.EduCore.dto.Request.UpdateAuthorDto;
import com.hem.EduCore.entity.Author;
import com.hem.EduCore.exception.DuplicateResourceException;
import com.hem.EduCore.exception.ResourceNotFoundException;
import com.hem.EduCore.mapper.AuthorMapper;
import com.hem.EduCore.repository.AuthorRepository;
import com.hem.EduCore.service.impl.AuthorServiceImpl;
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
@DisplayName("AuthorService Unit Tests")
class AuthorServiceTest {

    @Mock private AuthorMapper authorMapper;
    @Mock private AuthorRepository authorRepository;

    @InjectMocks
    private AuthorServiceImpl authorService;

    private Author activeAuthor;
    private Author deletedAuthor;
    private AuthorResponseDto responseDto;
    private CreateAuthorDto createAuthorDto;
    private UpdateAuthorDto updateAuthorDto;

    @BeforeEach
    void setUp() {
        activeAuthor = new Author();
        activeAuthor.setAuthorId(1L);
        activeAuthor.setName("George Orwell");
        activeAuthor.setEmail("orwell@example.com");
        activeAuthor.setBio("British novelist");
        activeAuthor.setDeleted(false);

        deletedAuthor = new Author();
        deletedAuthor.setAuthorId(2L);
        deletedAuthor.setEmail("deleted@example.com");
        deletedAuthor.setDeleted(true);

        responseDto = new AuthorResponseDto();
        responseDto.setAuthorId(1L);
        responseDto.setName("George Orwell");
        responseDto.setEmail("orwell@example.com");

        createAuthorDto = new CreateAuthorDto();
        createAuthorDto.setName("George Orwell");
        createAuthorDto.setEmail("orwell@example.com");
        createAuthorDto.setBio("British novelist");

        updateAuthorDto = new UpdateAuthorDto();
        updateAuthorDto.setName("Eric Blair");
        updateAuthorDto.setEmail("eric@example.com");
        updateAuthorDto.setBio("Updated bio");
    }

    // ==================== CREATE AUTHOR ====================

    @Test
    @DisplayName("Should create author successfully when email is unique")
    void createAuthor_success() {
        when(authorRepository.existsByEmail("orwell@example.com")).thenReturn(false);
        when(authorMapper.toEntity(createAuthorDto)).thenReturn(activeAuthor);
        when(authorRepository.save(activeAuthor)).thenReturn(activeAuthor);
        when(authorMapper.toResponseDto(activeAuthor)).thenReturn(responseDto);

        AuthorResponseDto result = authorService.createAuthor(createAuthorDto);

        assertNotNull(result);
        assertEquals("George Orwell", result.getName());
        assertEquals("orwell@example.com", result.getEmail());
        verify(authorRepository).save(activeAuthor);
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when email already exists")
    void createAuthor_duplicateEmail_throwsException() {
        when(authorRepository.existsByEmail("orwell@example.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authorService.createAuthor(createAuthorDto));
        verify(authorRepository, never()).save(any());
    }

    // ==================== GET AUTHOR BY ID ====================

    @Test
    @DisplayName("Should return author when found and not deleted")
    void getAuthorById_found() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(activeAuthor));
        when(authorMapper.toResponseDto(activeAuthor)).thenReturn(responseDto);

        AuthorResponseDto result = authorService.getAuthorById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getAuthorId());
        verify(authorRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when author ID does not exist")
    void getAuthorById_notFound_throwsException() {
        when(authorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authorService.getAuthorById(99L));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when author is soft-deleted")
    void getAuthorById_softDeleted_throwsException() {
        when(authorRepository.findById(2L)).thenReturn(Optional.of(deletedAuthor));

        assertThrows(ResourceNotFoundException.class, () -> authorService.getAuthorById(2L));
    }

    // ==================== DELETE AUTHOR ====================

    @Test
    @DisplayName("Should soft-delete author by setting isDeleted to true")
    void deleteAuthor_success() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(activeAuthor));

        authorService.deleteAuthor(1L);

        assertTrue(activeAuthor.isDeleted());
        verify(authorRepository).save(activeAuthor);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent author")
    void deleteAuthor_notFound_throwsException() {
        when(authorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authorService.deleteAuthor(99L));
        verify(authorRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting already soft-deleted author")
    void deleteAuthor_alreadyDeleted_throwsException() {
        when(authorRepository.findById(2L)).thenReturn(Optional.of(deletedAuthor));

        assertThrows(ResourceNotFoundException.class, () -> authorService.deleteAuthor(2L));
        verify(authorRepository, never()).save(any());
    }

    // ==================== FULL UPDATE ====================

    @Test
    @DisplayName("Should fully update all author fields including email")
    void fullUpdateAuthor_success() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(activeAuthor));
        when(authorRepository.existsByEmailAndAuthorIdNot("eric@example.com", 1L)).thenReturn(false);
        when(authorRepository.save(activeAuthor)).thenReturn(activeAuthor);
        when(authorMapper.toResponseDto(activeAuthor)).thenReturn(responseDto);

        authorService.fullUpdateAuthor(1L, updateAuthorDto);

        assertEquals("Eric Blair", activeAuthor.getName());
        assertEquals("eric@example.com", activeAuthor.getEmail());
        assertEquals("Updated bio", activeAuthor.getBio());
        verify(authorRepository).save(activeAuthor);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when fully updating non-existent author")
    void fullUpdateAuthor_notFound_throwsException() {
        when(authorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authorService.fullUpdateAuthor(99L, updateAuthorDto));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when new email belongs to another author")
    void fullUpdateAuthor_duplicateEmail_throwsException() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(activeAuthor));
        when(authorRepository.existsByEmailAndAuthorIdNot("eric@example.com", 1L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authorService.fullUpdateAuthor(1L, updateAuthorDto));
        verify(authorRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when name is null in full update")
    void fullUpdateAuthor_nullName_throwsException() {
        updateAuthorDto.setName(null);

        assertThrows(IllegalArgumentException.class, () -> authorService.fullUpdateAuthor(1L, updateAuthorDto));
        verify(authorRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when email is null in full update")
    void fullUpdateAuthor_nullEmail_throwsException() {
        updateAuthorDto.setEmail(null);

        assertThrows(IllegalArgumentException.class, () -> authorService.fullUpdateAuthor(1L, updateAuthorDto));
        verify(authorRepository, never()).findById(any());
    }

    // ==================== PARTIAL UPDATE ====================

    @Test
    @DisplayName("Should partially update only the name field when only name is provided")
    void partialUpdate_nameOnly() {
        UpdateAuthorDto dto = new UpdateAuthorDto();
        dto.setName("New Name");
        when(authorRepository.findById(1L)).thenReturn(Optional.of(activeAuthor));
        when(authorRepository.save(activeAuthor)).thenReturn(activeAuthor);
        when(authorMapper.toResponseDto(activeAuthor)).thenReturn(responseDto);

        authorService.partialUpdate(1L, dto);

        assertEquals("New Name", activeAuthor.getName());
        assertEquals("orwell@example.com", activeAuthor.getEmail());
    }

    @Test
    @DisplayName("Should partially update only the email field when email is unique")
    void partialUpdate_emailOnly_unique() {
        UpdateAuthorDto dto = new UpdateAuthorDto();
        dto.setEmail("new@example.com");
        when(authorRepository.findById(1L)).thenReturn(Optional.of(activeAuthor));
        when(authorRepository.existsByEmailAndAuthorIdNot("new@example.com", 1L)).thenReturn(false);
        when(authorRepository.save(activeAuthor)).thenReturn(activeAuthor);
        when(authorMapper.toResponseDto(activeAuthor)).thenReturn(responseDto);

        authorService.partialUpdate(1L, dto);

        assertEquals("new@example.com", activeAuthor.getEmail());
        assertEquals("George Orwell", activeAuthor.getName());
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when patched email belongs to another author")
    void partialUpdate_duplicateEmail_throwsException() {
        UpdateAuthorDto dto = new UpdateAuthorDto();
        dto.setEmail("taken@example.com");
        when(authorRepository.findById(1L)).thenReturn(Optional.of(activeAuthor));
        when(authorRepository.existsByEmailAndAuthorIdNot("taken@example.com", 1L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authorService.partialUpdate(1L, dto));
        verify(authorRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when partially updating non-existent author")
    void partialUpdate_notFound_throwsException() {
        when(authorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authorService.partialUpdate(99L, updateAuthorDto));
    }

    // ==================== GET ALL AUTHORS ====================

    @Test
    @DisplayName("Should return paginated page of non-deleted authors")
    void getAllAuthors_returnsActivePage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Author> page = new PageImpl<>(List.of(activeAuthor), pageable, 1);
        when(authorRepository.findAllByIsDeletedFalse(pageable)).thenReturn(page);
        when(authorMapper.toResponseDto(activeAuthor)).thenReturn(responseDto);

        Page<AuthorResponseDto> result = authorService.getAllAuthors(pageable);

        assertEquals(1, result.getTotalElements());
        verify(authorRepository).findAllByIsDeletedFalse(pageable);
        verify(authorRepository, never()).findAll(pageable);
    }

    @Test
    @DisplayName("Should return empty page when no active authors exist")
    void getAllAuthors_emptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        when(authorRepository.findAllByIsDeletedFalse(pageable))
                .thenReturn(new PageImpl<>(List.of(), pageable, 0));

        Page<AuthorResponseDto> result = authorService.getAllAuthors(pageable);

        assertEquals(0, result.getTotalElements());
    }
}
