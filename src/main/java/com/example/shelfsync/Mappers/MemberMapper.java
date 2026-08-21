package com.example.shelfsync.Mappers;
import com.example.shelfsync.Models.DTOs.MemberRequestDto;
import com.example.shelfsync.Models.DTOs.MemberResponseDto;
import com.example.shelfsync.Models.Entities.Member;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MemberMapper {
    public Member memberRequestDtoToMember(MemberRequestDto memberRequestDto);
    public MemberResponseDto memberToMemberResponseDto(Member member);
}
