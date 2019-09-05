package com.example.application.Retrofit2.Repo.GETS.SNS;

public class Like {

    String id; // sns_like_list 의 post_num 임 필요할까? 혹시모르니까
    String profile_img;
    String nick_name;
    String user_id; // 왜냐하면 나중에 선택할 떄 이녀석은 가지고 있어야 무엇이든지 할 수 있기 떄문! 핵심이 되는 값임
    String isFollowing;
    String regi;

    @Override
    public String toString() {
        return "Like{" +
                "id='" + id + '\'' +
                ", profile_img='" + profile_img + '\'' +
                ", nick_name='" + nick_name + '\'' +
                ", user_id='" + user_id + '\'' +
                ", isFollowing='" + isFollowing + '\'' +
                ", regi='" + regi + '\'' +
                '}';
    }

    public String getRegi() {
        return regi;
    }

    public void setRegi(String regi) {
        this.regi = regi;
    }

    public String getIsFollowing() {
        return isFollowing;
    }

    public void setIsFollowing(String isFollowing) {
        this.isFollowing = isFollowing;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfile_img() {
        return profile_img;
    }

    public void setProfile_img(String profile_img) {
        this.profile_img = profile_img;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
