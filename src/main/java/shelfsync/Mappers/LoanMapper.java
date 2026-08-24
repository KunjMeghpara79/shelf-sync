package shelfsync.Mappers;

import org.mapstruct.Mapper;
import shelfsync.Models.DTOs.LoanResponseDto;
import shelfsync.Models.Entities.Loan;

@Mapper(componentModel = "spring")
public interface LoanMapper {
    public LoanResponseDto loanToLoanResponseDto(Loan loan);
}
