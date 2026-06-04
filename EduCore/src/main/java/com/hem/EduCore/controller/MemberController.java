package com.hem.EduCore.controller;


import com.hem.EduCore.dto.CreateMemberDto;
import com.hem.EduCore.dto.MemberResponseDto;
import com.hem.EduCore.service.MemberService;
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
@RequestMapping("api/members")
@RequiredArgsConstructor
@Tag(name = "Members", description = "API endpoints for managing library members")
public class MemberController {

    private final MemberServiceImpl memberService;




    @PostMapping()
    @Operation(summary = "Create a new member", description = "Register a new member in the library system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Member created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MemberResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input or duplicate email"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<MemberResponseDto> createMember(
            @Valid @RequestBody CreateMemberDto request
            ){

     return ResponseEntity.status(HttpStatus.CREATED).body(
             memberService.createMember(request)
     )  ;

    }

    @GetMapping("/{id}")
    @Operation(summary = "Get member by ID", description = "Retrieve a member's information by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Member found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MemberResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Member not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public  ResponseEntity<MemberResponseDto> getMemberById(
           @PathVariable Long id){
        return  ResponseEntity.ok(memberService.getMemberById(id));

    }



    @GetMapping
    @Operation(summary = "Get all members", description = "Retrieve all members with pagination support")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Members retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public  ResponseEntity<Page<MemberResponseDto>> getAllMembers(
            Pageable pageable
    ){
        return  ResponseEntity.ok(memberService.getAllMembers(pageable));

    }









}
