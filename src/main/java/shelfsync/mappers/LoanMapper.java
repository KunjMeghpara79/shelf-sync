package shelfsync.mappers;

import org.mapstruct.Mapper;
import shelfsync.models.dto.LoanResponseDto;
import shelfsync.models.entities.Loan;

@Mapper(componentModel = "spring")
public interface LoanMapper {
    public LoanResponseDto loanToLoanResponseDto(Loan loan);
}
