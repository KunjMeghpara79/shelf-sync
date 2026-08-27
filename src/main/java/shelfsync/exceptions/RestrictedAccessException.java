package shelfsync.exceptions;

public class RestrictedAccessException extends RuntimeException{
    private String message;

    public RestrictedAccessException(String message){this.message = message;}

    @Override
    public String getMessage() {
        return message;
    }
}
