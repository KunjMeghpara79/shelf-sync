package shelfsync.models.dto;

import shelfsync.enums.LoanStatus;

import java.time.LocalDateTime;

public record LoanResponseDto(LocalDateTime issueDate, LocalDateTime dueDate,String memberName, String bookName, LoanStatus loanStatus,LocalDateTime returnDate,int fine) {
    public LoanResponseDto withBookName(String memberName,String newBookName) {
        return new LoanResponseDto(
                this.issueDate,
                this.dueDate,
                memberName,
                newBookName,
                this.loanStatus,
                this.returnDate,
                this.fine
        );
    }
}
