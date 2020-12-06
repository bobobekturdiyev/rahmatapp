package uz.programmer.rahmat.responses;

public class DefaultResponse {
    private int error = 0;
    private int success = 0;
    private String message = "";

    public int getError() {
        return error;
    }

    public int getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
