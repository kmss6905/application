package com.example.application.Retrofit2.Repo.GETS.USERS;

public class USERINFO {
    String sns;
    String profile_img;
    String nick_name;
    String id;

    @Override
    public String toString() {
        return "USERINFO{" +
                "sns='" + sns + '\'' +
                ", profile_img='" + profile_img + '\'' +
                ", nick_name='" + nick_name + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

    public void setSns(String sns) {
        this.sns = sns;
    }

    public void setProfile_img(String profile_img) {
        this.profile_img = profile_img;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSns() {
        return sns;
    }

    public String getProfile_img() {
        return profile_img;
    }

    public String getNick_name() {
        return nick_name;
    }

}
