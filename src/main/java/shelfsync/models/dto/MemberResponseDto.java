package shelfsync.models.dto;
public record MemberResponseDto(
        int memberId,
        String memberName,
        String memberEmail,
        int fine
) {
}
