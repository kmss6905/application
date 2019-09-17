package com.example.application.Retrofit2.Repo.GETS.SUBSCRIBE;

public class Following {
    String id;
    String subscribe;
    String nick_name;
    String profile_img;
    String user_id;
    String following;

    @Override
    public String toString() {
        return "Following{" +
                "id='" + id + '\'' +
                ", subscribe='" + subscribe + '\'' +
                ", nick_name='" + nick_name + '\'' +
                ", profile_img='" + profile_img + '\'' +
                ", user_id='" + user_id + '\'' +
                ", following='" + following + '\'' +
                '}';
    }

    public String getFollowing() {
        return following;
    }

    public void setFollowing(String following) {
        this.following = following;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(String subscribe) {
        this.subscribe = subscribe;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getProfile_img() {
        return profile_img;
    }

    public void setProfile_img(String profile_img) {
        this.profile_img = profile_img;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
