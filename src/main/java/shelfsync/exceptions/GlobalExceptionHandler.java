package shelfsync.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler({
            MemberNotFoundException.class,
            BookNotFoundException.class,
            BookNotAvailableException.class,
            LoanNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundExceptions(Exception ex) {

        return switch (ex) {
            case MemberNotFoundException e ->
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());

            case BookNotFoundException e ->
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());

            case BookNotAvailableException e ->
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());

            case LoanNotFoundException e ->
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());

            default ->
                    new ErrorResponse(
                            HttpStatus.NOT_FOUND.value(),
                            ex.getMessage()
                    );
        };
    }



    @ExceptionHandler({
            MemberAlreadyExistsException.class,
            BookAlreadyBorrowedException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictExceptions(Exception ex) {

        return switch (ex) {
            case MemberAlreadyExistsException e ->
                    new ErrorResponse(HttpStatus.CONFLICT.value(), e.getMessage());

            case BookAlreadyBorrowedException e ->
                    new ErrorResponse(HttpStatus.CONFLICT.value(), e.getMessage());

            default ->
                    new ErrorResponse(
                            HttpStatus.CONFLICT.value(),
                            ex.getMessage()
                    );
        };
    }



    @ExceptionHandler({
            InvalidPasswordException.class,
            FinePayException.class
    })
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbiddenExceptions(Exception ex) {

        return switch (ex) {
            case InvalidPasswordException e ->
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), e.getMessage());

            case FinePayException e ->
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), e.getMessage());

            default ->
                    new ErrorResponse(
                            HttpStatus.FORBIDDEN.value(),
                            ex.getMessage()
                    );
        };
    }



    @ExceptionHandler(RestrictedAccessException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleUnauthorizedException(
            RestrictedAccessException ex) {

        return switch (ex) {
            case RestrictedAccessException e ->
                    new ErrorResponse(
                            HttpStatus.UNAUTHORIZED.value(),
                            e.getMessage()
                    );

        };
    }



    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadInput(Exception ex) {

        return switch (ex) {

            case MethodArgumentNotValidException e -> {

                String errorMessage = e.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .collect(Collectors.joining(", "));

                yield new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        "Validation Failed: " + errorMessage
                );
            }

            case HttpMessageNotReadableException e -> {

                yield new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        "Error: Invalid JSON structure or incorrect data types."
                );
            }

            case MethodArgumentTypeMismatchException e -> {

                String message = String.format(
                        "Parameter '%s' should be of type '%s'.",
                        e.getName(),
                        e.getRequiredType() != null
                                ? e.getRequiredType().getSimpleName()
                                : "unknown"
                );

                yield new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        "Error: " + message
                );
            }

            default ->
                    new ErrorResponse(
                            HttpStatus.BAD_REQUEST.value(),
                            "Invalid input provided"
                    );
        };
    }
}