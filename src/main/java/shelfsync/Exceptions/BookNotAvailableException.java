package shelfsync.Exceptions;

public class BookNotAvailableException extends  RuntimeException {
    private String message;

    public BookNotAvailableException(String message){this.message = message;}

    @Override
    public String getMessage() {
        return message;
    }
}
