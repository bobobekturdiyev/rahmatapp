package uz.programmer.rahmat.responses;


import uz.programmer.rahmat.model.User;

public class ResponseLogin {

    private int error;
    private int success;
    private String message;
    private User user;

    public int getError() {
        return error;
    }

    public int getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }
}
