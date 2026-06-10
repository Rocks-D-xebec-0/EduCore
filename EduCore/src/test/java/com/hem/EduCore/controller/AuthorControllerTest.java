package com.hem.EduCore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hem.EduCore.dto.Response.AuthorResponseDto;
import com.hem.EduCore.dto.Request.CreateAuthorDto;
import com.hem.EduCore.dto.Request.UpdateAuthorDto;
import com.hem.EduCore.exception.DuplicateResourceException;
import com.hem.EduCore.exception.ResourceNotFoundException;
import com.hem.EduCore.service.AuthorService;
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
@DisplayName("AuthorController Integration Tests")
class AuthorControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean  private AuthorService authorService;

    private AuthorResponseDto authorResponseDto;
    private CreateAuthorDto createAuthorDto;
    private UpdateAuthorDto updateAuthorDto;

    @BeforeEach
    void setUp() {
        authorResponseDto = new AuthorResponseDto();
        authorResponseDto.setAuthorId(1L);
        authorResponseDto.setName("George Orwell");
        authorResponseDto.setEmail("orwell@example.com");
        authorResponseDto.setBio("British novelist");

        createAuthorDto = new CreateAuthorDto();
        createAuthorDto.setName("George Orwell");
        createAuthorDto.setEmail("orwell@example.com");
        createAuthorDto.setBio("British novelist");

        updateAuthorDto = new UpdateAuthorDto();
        updateAuthorDto.setName("Eric Blair");
        updateAuthorDto.setEmail("eric@example.com");
        updateAuthorDto.setBio("Updated bio");
    }

    // ==================== POST /api/authors ====================

    @Test
    @DisplayName("POST /api/authors should return 201 with author body on success")
    void createAuthor_returns201() throws Exception {
        when(authorService.createAuthor(any(CreateAuthorDto.class))).thenReturn(authorResponseDto);

        mockMvc.perform(post("/api/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createAuthorDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.authorId").value(1))
                .andExpect(jsonPath("$.name").value("George Orwell"))
                .andExpect(jsonPath("$.email").value("orwell@example.com"));
    }

    @Test
    @DisplayName("POST /api/authors should return 400 when name is blank")
    void createAuthor_blankName_returns400() throws Exception {
        createAuthorDto.setName("");

        mockMvc.perform(post("/api/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createAuthorDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/authors should return 400 when email is blank")
    void createAuthor_blankEmail_returns400() throws Exception {
        createAuthorDto.setEmail("");

        mockMvc.perform(post("/api/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createAuthorDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/authors should return 400 when email format is invalid")
    void createAuthor_invalidEmail_returns400() throws Exception {
        createAuthorDto.setEmail("not-an-email");

        mockMvc.perform(post("/api/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createAuthorDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/authors should return 400 with error message on duplicate email")
    void createAuthor_duplicateEmail_returns400() throws Exception {
        when(authorService.createAuthor(any(CreateAuthorDto.class)))
                .thenThrow(new DuplicateResourceException("Author with email orwell@example.com already exists"));

        mockMvc.perform(post("/api/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createAuthorDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Author with email orwell@example.com already exists"));
    }

    // ==================== GET /api/authors/{id} ====================

    @Test
    @DisplayName("GET /api/authors/{id} should return 200 with author details")
    void getAuthorById_returns200() throws Exception {
        when(authorService.getAuthorById(1L)).thenReturn(authorResponseDto);

        mockMvc.perform(get("/api/authors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorId").value(1))
                .andExpect(jsonPath("$.name").value("George Orwell"))
                .andExpect(jsonPath("$.email").value("orwell@example.com"));
    }

    @Test
    @DisplayName("GET /api/authors/{id} should return 404 with message when author not found")
    void getAuthorById_notFound_returns404() throws Exception {
        when(authorService.getAuthorById(99L))
                .thenThrow(new ResourceNotFoundException("Author with id 99 not found"));

        mockMvc.perform(get("/api/authors/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Author with id 99 not found"));
    }

    // ==================== GET /api/authors ====================

    @Test
    @DisplayName("GET /api/authors should return 200 with paginated results")
    void getAllAuthors_returns200() throws Exception {
        when(authorService.getAllAuthors(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(authorResponseDto)));

        mockMvc.perform(get("/api/authors?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("George Orwell"));
    }

    // ==================== PUT /api/authors/{id} ====================

    @Test
    @DisplayName("PUT /api/authors/{id} should return 200 on success")
    void fullUpdate_returns200() throws Exception {
        when(authorService.fullUpdateAuthor(eq(1L), any(UpdateAuthorDto.class))).thenReturn(authorResponseDto);

        mockMvc.perform(put("/api/authors/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateAuthorDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorId").value(1));
    }

    @Test
    @DisplayName("PUT /api/authors/{id} should return 404 when author not found")
    void fullUpdate_notFound_returns404() throws Exception {
        when(authorService.fullUpdateAuthor(eq(99L), any(UpdateAuthorDto.class)))
                .thenThrow(new ResourceNotFoundException("Author with id 99 not found"));

        mockMvc.perform(put("/api/authors/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateAuthorDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/authors/{id} should return 400 on duplicate email")
    void fullUpdate_duplicateEmail_returns400() throws Exception {
        when(authorService.fullUpdateAuthor(eq(1L), any(UpdateAuthorDto.class)))
                .thenThrow(new DuplicateResourceException("Author with email already exists"));

        mockMvc.perform(put("/api/authors/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateAuthorDto)))
                .andExpect(status().isBadRequest());
    }

    // ==================== PATCH /api/authors/{id} ====================

    @Test
    @DisplayName("PATCH /api/authors/{id} should return 200 on success")
    void partialUpdate_returns200() throws Exception {
        UpdateAuthorDto patchDto = new UpdateAuthorDto();
        patchDto.setName("New Name");
        when(authorService.partialUpdate(eq(1L), any(UpdateAuthorDto.class))).thenReturn(authorResponseDto);

        mockMvc.perform(patch("/api/authors/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patchDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /api/authors/{id} should return 400 when email format is invalid")
    void partialUpdate_invalidEmail_returns400() throws Exception {
        UpdateAuthorDto patchDto = new UpdateAuthorDto();
        patchDto.setEmail("not-an-email");

        mockMvc.perform(patch("/api/authors/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patchDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /api/authors/{id} should return 404 when author not found")
    void partialUpdate_notFound_returns404() throws Exception {
        when(authorService.partialUpdate(eq(99L), any(UpdateAuthorDto.class)))
                .thenThrow(new ResourceNotFoundException("Author with id 99 not found"));

        mockMvc.perform(patch("/api/authors/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateAuthorDto)))
                .andExpect(status().isNotFound());
    }

    // ==================== DELETE /api/authors/{id} ====================

    @Test
    @DisplayName("DELETE /api/authors/{id} should return 204 on success")
    void deleteAuthor_returns204() throws Exception {
        mockMvc.perform(delete("/api/authors/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/authors/{id} should return 404 with message when author not found")
    void deleteAuthor_notFound_returns404() throws Exception {
        doThrow(new ResourceNotFoundException("Author with id 99 not found"))
                .when(authorService).deleteAuthor(99L);

        mockMvc.perform(delete("/api/authors/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Author with id 99 not found"));
    }
}
