package com.hem.EduCore.mapper;


import com.hem.EduCore.dto.Request.CreateMemberDto;
import com.hem.EduCore.dto.Response.MemberResponseDto;
import com.hem.EduCore.entity.Member;
import org.springframework.stereotype.Component;

@Component
public class MemberMapper {


    public Member toEntity(CreateMemberDto dto){

        Member member=new Member();

        member.setName(dto.getName());
        member.setEmail(dto.getEmail());
        member.setPhone(dto.getPhone());
        member.setAddress(dto.getAddress());
        return member;
    }

    public MemberResponseDto toResponseDto(Member member){

        MemberResponseDto dto=new MemberResponseDto();

        dto.setMemberId(member.getMember_id());
        dto.setName(member.getName());
        dto.setEmail(member.getEmail());
        dto.setPhone(member.getPhone());
        dto.setAddress(member.getAddress());
        dto.setMemberShipDate(member.getMemberShipDate());
   return dto;
    }
}
