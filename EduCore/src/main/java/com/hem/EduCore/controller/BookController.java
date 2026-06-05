package com.hem.EduCore.controller;


import com.hem.EduCore.dto.Reponse.BookResponseDto;
import com.hem.EduCore.dto.Request.CreateBookDto;
import com.hem.EduCore.dto.Request.CreateMemberDto;
import com.hem.EduCore.service.BookService;
import com.hem.EduCore.service.impl.MemberServiceImpl;
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
@RequestMapping("api/books")
@RequiredArgsConstructor
@Tag(name ="Books",description="API endpoints for managing library books")
public class BookController {


    private  final BookService bookService;

    @PostMapping
    @Operation(summary = "Create a new book ",
    description = "Register a new book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Book created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BookResponseDto.class)
                    )
            ),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input or duplicate book "),
            @ApiResponse(responseCode = "500",
                    description = "Internal server error ")
    })

    public ResponseEntity<BookResponseDto> createBook(
            @Valid @RequestBody CreateBookDto createBookDto
            ){
        return  ResponseEntity.status(HttpStatus.CREATED).body(bookService.createBook(createBookDto)
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get book by id ",
    description = "Retreive book by id"
    )
    @ApiResponses(value={
            @ApiResponse(responseCode = "200",
            description = "Book is found",
                    content = @Content (mediaType = "application/json",
                    schema = @Schema(implementation = BookResponseDto.class)
                    )
            ),
            @ApiResponse(responseCode = "404",description = "book not found"),
            @ApiResponse(responseCode = "500",description = "Internel server error")
    })

    public  ResponseEntity<BookResponseDto> getBookById(
            @PathVariable Long id
    ){
        return  ResponseEntity.ok(bookService.getBookById(id));
    }



    @GetMapping
    @Operation(summary = "Get All books",
    description = "Retrieve all books"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Books retrieved successfully"
                    ),
                    @ApiResponse(responseCode = "500",
                    description = "Internal server error"
                    ),
                    @ApiResponse()

            }
    )
public  ResponseEntity<Page<BookResponseDto>> getAllBooks(
        Pageable pageable
){
        return  ResponseEntity.ok(bookService.getAllBooks(pageable));
}

//
//@DeleteMapping
//            public  ResponseEntity<>    softDeleteBook(
//                    @PathVariable Long id
//            ){
//        return  ResponseEntity.ok(bookService.softDeleteBook(id));
//            }

}
