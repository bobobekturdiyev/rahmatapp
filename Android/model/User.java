package uz.programmer.rahmat.model;

import com.google.gson.annotations.SerializedName;

public class User {

    private int id;
    private String  username;

    private String bio;

    private String full_name;

    private String email;

    private String password;
    private String nickname;

    private String phone;
    private String photo;
    private String status;

    private int following_count;
    private int followers_count;
    private int likes_count;

    public int getFollowing_count() {
        return following_count;
    }

    public int getFollowers_count() {
        return followers_count;
    }

    public int getLikes_count() {
        return likes_count;
    }

    public String getStatus() {
        return status;
    }

    @SerializedName("is_activate")
    private int is_active;


    public int getIs_active() {
        return is_active;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getBio() {
        return bio;
    }

    public String getFull_name() {
        return full_name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPhone() {
        return phone;
    }

    public String getPhoto() {
        return photo;
    }
}
