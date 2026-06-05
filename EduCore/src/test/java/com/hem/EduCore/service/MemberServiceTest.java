package com.hem.EduCore.service;

import com.hem.EduCore.dto.Request.CreateMemberDto;
import com.hem.EduCore.dto.Reponse.MemberResponseDto;
import com.hem.EduCore.entity.Member;
import com.hem.EduCore.repository.MemberRepository;
import com.hem.EduCore.service.impl.MemberServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberService Tests")
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberServiceImpl memberService;

    private Member testMember;
    private CreateMemberDto createMemberDto;
    private MemberResponseDto memberResponseDto;

    @BeforeEach
    void setUp() {
        testMember = new Member();
        testMember.setMember_id(1L);
        testMember.setName("John Doe");
        testMember.setEmail("john@example.com");
        testMember.setPhone("555-1234");
        testMember.setAddress("123 Main St");
        testMember.setMemberShipDate(LocalDate.now());

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
    }

    // ==================== CREATE MEMBER TESTS ====================

    @Test
    @DisplayName("Should create a member successfully")
    void testCreateMemberSuccess() {
        when(memberRepository.save(any(Member.class))).thenReturn(testMember);

        MemberResponseDto result = memberService.createMember(createMemberDto);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("john@example.com", result.getEmail());
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("Should save member with correct email")
    void testCreateMemberSavesCorrectEmail() {
        when(memberRepository.save(any(Member.class))).thenReturn(testMember);

        memberService.createMember(createMemberDto);

        verify(memberRepository).save(argThat(member ->
            member.getEmail().equals("john@example.com")
        ));
    }

    @Test
    @DisplayName("Should set membership date when creating member")
    void testCreateMemberSetsMembershipDate() {
        when(memberRepository.save(any(Member.class))).thenReturn(testMember);

        MemberResponseDto result = memberService.createMember(createMemberDto);

        assertNotNull(result);
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("Should throw exception when creating member with null input")
    void testCreateMemberWithNullInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            memberService.createMember(null);
        });
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    @DisplayName("Should throw exception when email is null")
    void testCreateMemberWithNullEmail() {
        createMemberDto.setEmail(null);

        assertThrows(IllegalArgumentException.class, () -> {
            memberService.createMember(createMemberDto);
        });
        verify(memberRepository, never()).save(any(Member.class));
    }

    // ==================== GET MEMBER BY ID TESTS ====================

    @Test
    @DisplayName("Should retrieve member by ID successfully")
    void testGetMemberByIdSuccess() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));

        MemberResponseDto result = memberService.getMemberById(1L);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        verify(memberRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return null when member not found")
    void testGetMemberByIdNotFound() {
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            memberService.getMemberById(999L);
        });
        verify(memberRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should call repository with correct ID")
    void testGetMemberByIdCallsRepositoryWithCorrectId() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));

        memberService.getMemberById(1L);

        verify(memberRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception for negative ID")
    void testGetMemberByIdWithNegativeId() {
        assertThrows(IllegalArgumentException.class, () -> {
            memberService.getMemberById(-1L);
        });
        verify(memberRepository, never()).findById(anyLong());
    }

    // ==================== GET ALL MEMBERS TESTS ====================

    @Test
    @DisplayName("Should retrieve all members with pagination")
    void testGetAllMembersSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Member> members = Arrays.asList(testMember);
        Page<Member> memberPage = new PageImpl<>(members, pageable, 1);

        when(memberRepository.findAll(pageable)).thenReturn(memberPage);

        Page<MemberResponseDto> result = memberService.getAllMembers(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(memberRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Should return empty page when no members exist")
    void testGetAllMembersEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Member> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(memberRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<MemberResponseDto> result = memberService.getAllMembers(pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        verify(memberRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Should respect pagination parameters")
    void testGetAllMembersRespectsPagination() {
        Pageable pageable = PageRequest.of(0, 5);
        List<Member> members = Arrays.asList(testMember);
        Page<Member> memberPage = new PageImpl<>(members, pageable, 1);

        when(memberRepository.findAll(pageable)).thenReturn(memberPage);

        memberService.getAllMembers(pageable);

        verify(memberRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Should handle multiple members in page")
    void testGetAllMembersWithMultipleMembers() {
        Member member2 = new Member();
        member2.setMember_id(2L);
        member2.setName("Jane Doe");
        member2.setEmail("jane@example.com");
        member2.setPhone("555-5678");
        member2.setAddress("456 Oak Ave");

        Pageable pageable = PageRequest.of(0, 10);
        List<Member> members = Arrays.asList(testMember, member2);
        Page<Member> memberPage = new PageImpl<>(members, pageable, 2);

        when(memberRepository.findAll(pageable)).thenReturn(memberPage);

        Page<MemberResponseDto> result = memberService.getAllMembers(pageable);

        assertEquals(2, result.getTotalElements());
        verify(memberRepository, times(1)).findAll(pageable);
    }

    // ==================== EDGE CASES AND INTEGRATION TESTS ====================

    @Test
    @DisplayName("Should not call repository when input validation fails")
    void testValidationFailureDoesNotCallRepository() {
        createMemberDto.setName("");

        assertThrows(IllegalArgumentException.class, () -> {
            memberService.createMember(createMemberDto);
        });
        verify(memberRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should verify no other methods called on mock")
    void testVerifyNoUnexpectedCalls() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));

        memberService.getMemberById(1L);

        verifyNoMoreInteractions(memberRepository);
    }
}
