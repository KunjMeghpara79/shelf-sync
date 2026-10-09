package shelfsync.exceptions;

public class MemberNotFoundException extends RuntimeException{

    private final String message;

    public MemberNotFoundException(String message){this.message = message;}

    @Override
    public String getMessage() {
        return message;
    }
}
