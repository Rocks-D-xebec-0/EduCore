package com.hem.EduCore.service.impl;


import com.hem.EduCore.dto.CreateMemberDto;
import com.hem.EduCore.dto.MemberResponseDto;
import com.hem.EduCore.entity.Member;
import com.hem.EduCore.exception.DuplicateResourceException;
import com.hem.EduCore.mapper.MemberMapper;
import com.hem.EduCore.repository.MemberRepository;
import com.hem.EduCore.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
/*RequiredArgsConstructor is a Lombok annotation that generates a constructor with parameters
for fields
final
or annotated with @NonNull
 */
public class MemberServiceImpl  implements MemberService {



    private final MemberMapper memberMapper;
    private final MemberRepository memberRepository;

    @Override
    public MemberResponseDto createMember(CreateMemberDto request) {
        Member member=memberMapper.toEntity(request);


      if (      memberRepository.existsByEmail(member.getEmail())){
          throw new DuplicateResourceException("Member with email " +member.getEmail()+"already exist ");
      }
        member.setMemberShipDate(LocalDate.now());

        Member savedMember=memberRepository.save(member);
        return memberMapper.toResponseDto(savedMember);
    }

    @Override
    public MemberResponseDto getMemberById(Long id) {

        Member member=memberRepository.findById(id).orElseThrow(() -> new RuntimeException("Member Not found "));

        return  memberMapper.toResponseDto(member);

    }

    @Override
    public Page<MemberResponseDto> getAllMembers(Pageable pageable) {
        return memberRepository.findAll(pageable)
                .map(memberMapper::toResponseDto);
    }


}
