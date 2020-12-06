package uz.programmer.rahmat.model;

import com.google.gson.annotations.SerializedName;

public class Post {

    private User user;

    private int id;
    private int user_id;
    private int posted_to_telegram;

    private String content;
    private String photo;

    @SerializedName("published_time")
    private String created_at;

    private int is_liked;

    public int getIs_liked() {
        return is_liked;
    }

    public String getCreated_at() {
        return created_at;
    }

    public User getUser() {
        return user;
    }

    public int getId() {
        return id;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getPosted_to_telegram() {
        return posted_to_telegram;
    }

    public String getContent() {
        return content;
    }

    public String getPhoto() {
        return photo;
    }
}
