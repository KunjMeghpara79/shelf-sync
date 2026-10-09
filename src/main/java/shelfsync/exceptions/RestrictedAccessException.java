package shelfsync.exceptions;

public class RestrictedAccessException extends RuntimeException{
    private final String message;

    public RestrictedAccessException(String message){this.message = message;}

    @Override
    public String getMessage() {
        return message;
    }
}
