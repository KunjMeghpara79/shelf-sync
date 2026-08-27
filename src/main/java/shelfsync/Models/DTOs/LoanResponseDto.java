package shelfsync.Models.DTOs;

import shelfsync.Enums.LoanStatus;

import java.time.LocalDateTime;

public record LoanResponseDto(LocalDateTime issueDate, LocalDateTime dueDate, String bookName, LoanStatus loanStatus,LocalDateTime returnDate) {
    public LoanResponseDto withBookName(String newBookName) {
        return new LoanResponseDto(
                this.issueDate,
                this.dueDate,
                newBookName,
                this.loanStatus,
                this.returnDate

        );
    }
}
