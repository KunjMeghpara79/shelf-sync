package shelfsync.exceptions;

public class BookAlreadyBorrowedException extends RuntimeException{
    private final String message;

    public BookAlreadyBorrowedException(String message){this.message = message;}

    @Override
    public String getMessage() {
        return message;
    }
}
