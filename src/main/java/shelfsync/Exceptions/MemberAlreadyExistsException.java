package shelfsync.Exceptions;

public class MemberAlreadyExistsException extends RuntimeException{
    private String message;

    public MemberAlreadyExistsException(String message){this.message = message;}

    @Override
    public String getMessage() {
        return message;
    }
}
