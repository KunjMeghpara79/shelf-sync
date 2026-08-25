package shelfsync.Exceptions;

public class BookNotFoundException extends RuntimeException{
    private String message;

    public BookNotFoundException(String message){this.message = message;}

    @Override
    public String getMessage() {
        return message;
    }
}
