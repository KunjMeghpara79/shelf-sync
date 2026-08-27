package shelfsync.exceptions;

public class LoanNotFoundException extends RuntimeException{
    private String message;

    public LoanNotFoundException(String message){this.message = message;}

    @Override
    public String getMessage() {
        return message;
    }
}
