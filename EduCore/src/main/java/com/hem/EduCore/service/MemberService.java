package com.hem.EduCore.service;

import com.hem.EduCore.dto.CreateMemberDto;
import com.hem.EduCore.dto.MemberResponseDto;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface MemberService {


    MemberResponseDto createMember(CreateMemberDto request);
    MemberResponseDto getMemberById(Long id );
    Page<MemberResponseDto> getAllMembers(Pageable pageable);
}
