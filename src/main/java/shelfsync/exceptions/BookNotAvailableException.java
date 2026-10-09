package shelfsync.exceptions;

public class BookNotAvailableException extends  RuntimeException {
    private final String message;

    public BookNotAvailableException(String message){this.message = message;}

    @Override
    public String getMessage() {
        return message;
    }
}
