package com.hem.EduCore.controller;

import com.hem.EduCore.dto.Response.AuthorResponseDto;
import com.hem.EduCore.dto.Request.CreateAuthorDto;
import com.hem.EduCore.dto.Request.UpdateAuthorDto;
import com.hem.EduCore.service.AuthorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
@Tag(name = "Authors", description = "API endpoints for managing library authors")
public class AuthorController {

    private final AuthorService authorService;

    @PostMapping
    @Operation(summary = "Create an author", description = "Register a new author in the library system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Author created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthorResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or duplicate email"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AuthorResponseDto> createAuthor(@Valid @RequestBody CreateAuthorDto createAuthorDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authorService.createAuthor(createAuthorDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get author by ID", description = "Retrieve an author's information by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Author found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Author not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AuthorResponseDto> getAuthorById(@PathVariable Long id) {
        return ResponseEntity.ok(authorService.getAuthorById(id));
    }

    @GetMapping
    @Operation(summary = "Get all authors", description = "Retrieve all active authors with pagination support")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authors retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<AuthorResponseDto>> getAllAuthors(Pageable pageable) {
        return ResponseEntity.ok(authorService.getAllAuthors(pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Full update an author", description = "Replace all author fields by ID (name and email required)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Author updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthorResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or duplicate email"),
            @ApiResponse(responseCode = "404", description = "Author not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AuthorResponseDto> fullUpdate(@PathVariable Long id, @Valid @RequestBody UpdateAuthorDto updateAuthorDto) {
        return ResponseEntity.ok(authorService.fullUpdateAuthor(id, updateAuthorDto));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partial update an author", description = "Update one or more author fields by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Author updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthorResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or duplicate email"),
            @ApiResponse(responseCode = "404", description = "Author not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AuthorResponseDto> partialUpdate(@PathVariable Long id, @Valid @RequestBody UpdateAuthorDto updateAuthorDto) {
        return ResponseEntity.ok(authorService.partialUpdate(id, updateAuthorDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an author", description = "Soft-deletes an author by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Author deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Author not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }
}
