package uz.programmer.rahmat.responses;


import com.google.gson.annotations.SerializedName;

import java.util.List;

import uz.programmer.rahmat.model.Post;
import uz.programmer.rahmat.model.User;

public class ResponseProfileData {

    private int success;
    private User user;
    @SerializedName("posts")
    private List<Post> postList;

    public int getSuccess() {
        return success;
    }

    public List<Post> getPostList() {
        return postList;
    }

    public User getUser() {
        return user;
    }
}
