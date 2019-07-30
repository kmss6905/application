package com.example.application.Retrofit2.Repo;

import com.google.gson.annotations.SerializedName;

public class CheckNickResult {
    String result;
    String nick;

    public String getNick() {
        return nick;
    }

    public String getResult() {
        return result;
    }

    @SerializedName("body")
    String text;

}
