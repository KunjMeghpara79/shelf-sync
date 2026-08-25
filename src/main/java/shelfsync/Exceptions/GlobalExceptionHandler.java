package shelfsync.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.awt.print.Book;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = MemberNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleMemberNotFoundException(MemberNotFoundException ex){
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }
    @ExceptionHandler(value = MemberAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleMemberAlreadyExistsException(MemberAlreadyExistsException ex){
        return new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage());
    }
    @ExceptionHandler(value = InvalidPasswordException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleInvalidPassword(InvalidPasswordException ex){
        return new ErrorResponse(HttpStatus.FORBIDDEN.value(), ex.getMessage());
    }

    @ExceptionHandler(value = BookNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleBookNotFoundException(BookNotFoundException ex){
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }
    @ExceptionHandler(value = BookNotAvailableException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleBookNotAvailableException(BookNotAvailableException ex){
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }
    @ExceptionHandler(value = RestrictedAccessException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleRestrictedAccessException(RestrictedAccessException ex){
        return new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
    }
    @ExceptionHandler(value = BookAlreadyBorrowedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleRestrictedAccessException(BookAlreadyBorrowedException ex){
        return new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage());
    }
}
