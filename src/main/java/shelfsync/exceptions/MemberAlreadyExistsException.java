package shelfsync.exceptions;

public class MemberAlreadyExistsException extends RuntimeException{
    private final String message;

    public MemberAlreadyExistsException(String message){this.message = message;}

    @Override
    public String getMessage() {
        return message;
    }
}
