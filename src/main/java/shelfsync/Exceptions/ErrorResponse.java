package shelfsync.Exceptions;

public class ErrorResponse {
    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    private int statusCode;
    private String message;
    public ErrorResponse() {
    }
    public ErrorResponse(int statusCode,String message){
        this.statusCode = statusCode;
        this.message = message;
    }
}
