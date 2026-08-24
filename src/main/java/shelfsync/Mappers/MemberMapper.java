package shelfsync.Mappers;
import shelfsync.Models.DTOs.MemberRequestDto;
import shelfsync.Models.DTOs.MemberResponseDto;
import shelfsync.Models.Entities.Member;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MemberMapper {
    public Member memberRequestDtoToMember(MemberRequestDto memberRequestDto);
    public MemberResponseDto memberToMemberResponseDto(Member member);
}
