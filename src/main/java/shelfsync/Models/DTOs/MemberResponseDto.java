package shelfsync.Models.DTOs;
public record MemberResponseDto(
        int memberId,
        String memberName,
        String memberEmail,
        int fine
) {
}
