package shelfsync.exceptions;

public class FinePayException extends RuntimeException{
    private final String message;

    public FinePayException(String message){this.message = message;}

    @Override
    public String getMessage() {
        return message;
    }
}
