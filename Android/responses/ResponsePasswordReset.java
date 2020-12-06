package uz.programmer.rahmat.responses;

public class ResponsePasswordReset {
    private String message;
    private int error;
    private int success;

    private String new_password;

    public String getNew_password() {
        return new_password;
    }

    public String getMessage() {
        return message;
    }

    public int getError() {
        return error;
    }

    public int getSuccess() {
        return success;
    }
}
