package com.example.application.Retrofit2.Repo;

import com.google.gson.annotations.SerializedName;

public class Account {
    String user_email;
    String user_pwd;
    String user_hash;
    String active;
    String sns;
    String id;
    String nick_name;
    String profile_img;

    @SerializedName("body")
    String Text;



    public String getProfile_img() {
        return profile_img;
    }

    public String getText() {
        return Text;
    }



    public String getUser_email() {
        return user_email;
    }

    public String getUser_pwd() {
        return user_pwd;
    }

    public String getUser_hash() {
        return user_hash;
    }

    public String getActive() {
        return active;
    }

    public String getSns() {
        return sns;
    }

    public String getId() {
        return id;
    }

    public String getNick_name() {
        return nick_name;
    }

    @Override
    public String toString() {
        return "Account{" +
                "user_email='" + user_email + '\'' +
                ", user_pwd='" + user_pwd + '\'' +
                ", user_hash='" + user_hash + '\'' +
                ", active='" + active + '\'' +
                ", sns='" + sns + '\'' +
                ", id='" + id + '\'' +
                ", nick_name='" + nick_name + '\'' +
                '}';
    }


}
