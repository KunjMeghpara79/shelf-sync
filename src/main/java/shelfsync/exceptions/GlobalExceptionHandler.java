package shelfsync.exceptions;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundExceptions(NotFoundException ex){
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(),ex.getMessage());
    }

    @ExceptionHandler({ConflictException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictExceptions(ConflictException ex){
        return new ErrorResponse(HttpStatus.CONFLICT.value(),ex.getMessage());
    }

    @ExceptionHandler({ForbiddenException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbiddenExceptions(ForbiddenException ex){
        return new ErrorResponse(HttpStatus.FORBIDDEN.value(),ex.getMessage());
    }

    @ExceptionHandler({UnauthorizedException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleUnauthorizedExceptions(UnauthorizedException ex){
        return new ErrorResponse(HttpStatus.FORBIDDEN.value(),ex.getMessage());
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            IllegalArgumentException.class,
            InvalidFormatException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadInput(Exception ex) {

        return switch (ex) {

            case MethodArgumentNotValidException e -> {

                String errorMessage = e.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .collect(Collectors.joining(", "));

                yield new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Validation Failed: " + errorMessage);
            }

            case HttpMessageNotReadableException e -> {

                yield new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Error: Invalid JSON structure or incorrect data types.");
            }
            case IllegalArgumentException e -> {
                yield new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
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
            case InvalidFormatException e -> {
                yield new ErrorResponse(HttpStatus.BAD_REQUEST.value(),ex.getMessage());
            }
            default ->
                    new ErrorResponse(
                            HttpStatus.BAD_REQUEST.value(),
                            ex.getMessage()
                    );
        };
    }
}