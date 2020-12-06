package uz.programmer.rahmat.responses;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import uz.programmer.rahmat.model.User;

public class ResponseUser {
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


    @SerializedName("users")
    private List<User> userList;

    public List<User> getUserList() {
        return userList;
    }
}
