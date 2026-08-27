package shelfsync.mappers;
import shelfsync.models.dto.MemberRequestDto;
import shelfsync.models.dto.MemberResponseDto;
import shelfsync.models.entities.Member;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MemberMapper {
    public Member memberRequestDtoToMember(MemberRequestDto memberRequestDto);
    public MemberResponseDto memberToMemberResponseDto(Member member);
}
