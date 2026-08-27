package shelfsync.exceptions;

public class FinePayException extends RuntimeException{
    private String message;

    public FinePayException(String message){this.message = message;}

    @Override
    public String getMessage() {
        return message;
    }
}
