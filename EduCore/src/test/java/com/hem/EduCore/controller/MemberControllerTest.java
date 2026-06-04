package com.hem.EduCore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hem.EduCore.dto.CreateMemberDto;
import com.hem.EduCore.dto.MemberResponseDto;
import com.hem.EduCore.exception.DuplicateResourceException;
import com.hem.EduCore.service.impl.MemberServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Member Controller Integration Tests")
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberServiceImpl memberService;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateMemberDto createMemberDto;
    private MemberResponseDto memberResponseDto;

    @BeforeEach
    void setUp() {
        createMemberDto = new CreateMemberDto();
        createMemberDto.setName("John Doe");
        createMemberDto.setEmail("john@example.com");
        createMemberDto.setPhone("555-1234");
        createMemberDto.setAddress("123 Main St");

        memberResponseDto = new MemberResponseDto();
        memberResponseDto.setMemberId(1L);
        memberResponseDto.setName("John Doe");
        memberResponseDto.setEmail("john@example.com");
        memberResponseDto.setPhone("555-1234");
        memberResponseDto.setAddress("123 Main St");
        memberResponseDto.setMemberShipDate(LocalDate.now());
    }

    @Test
    @DisplayName("POST /api/members should return 201 Created")
    void testCreateMemberReturns201() throws Exception {
        when(memberService.createMember(any(CreateMemberDto.class))).thenReturn(memberResponseDto);

        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createMemberDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.memberId", equalTo(1)))
            .andExpect(jsonPath("$.name", equalTo("John Doe")))
            .andExpect(jsonPath("$.email", equalTo("john@example.com")));
    }

    @Test
    @DisplayName("POST /api/members should return 400 for missing required fields")
    void testCreateMemberReturns400ForMissingFields() throws Exception {
        CreateMemberDto invalidDto = new CreateMemberDto();
        invalidDto.setEmail("test@example.com");

        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/members should return 400 for duplicate email")
    void testCreateMemberReturns400ForDuplicateEmail() throws Exception {
        when(memberService.createMember(any(CreateMemberDto.class)))
            .thenThrow(new DuplicateResourceException("Member with email already exist"));

        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createMemberDto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/members/{id} should return 200 with member details")
    void testGetMemberByIdReturns200() throws Exception {
        when(memberService.getMemberById(1L)).thenReturn(memberResponseDto);

        mockMvc.perform(get("/api/members/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.memberId", equalTo(1)))
            .andExpect(jsonPath("$.name", equalTo("John Doe")))
            .andExpect(jsonPath("$.address", equalTo("123 Main St")));
    }

    @Test
    @DisplayName("GET /api/members/{id} should return 404 when member not found")
    void testGetMemberByIdReturns404() throws Exception {
        when(memberService.getMemberById(999L))
            .thenThrow(new RuntimeException("Member Not found"));

        mockMvc.perform(get("/api/members/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/members should return paginated results with 200")
    void testGetAllMembersReturns200() throws Exception {
        mockMvc.perform(get("/api/members?page=0&size=10"))
            .andExpect(status().isOk());
    }
}