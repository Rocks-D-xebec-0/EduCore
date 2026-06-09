package com.hem.EduCore.service;

import com.hem.EduCore.dto.Request.CreateMemberDto;
import com.hem.EduCore.dto.Reponse.MemberResponseDto;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;

public interface MemberService {


    MemberResponseDto createMember(CreateMemberDto request);
    MemberResponseDto getMemberById(Long id );
    Page<MemberResponseDto> getAllMembers(Pageable pageable);
}
