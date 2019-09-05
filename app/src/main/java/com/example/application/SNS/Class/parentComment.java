package com.example.application.SNS.Class;

import java.util.ArrayList;

public class parentComment {
    String comment_id;
    String user_id;
    String content;
    String date;
    String profile;
    String parent_id;

    String nickname;
    ArrayList<comment> childComments;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }


    @Override
    public String toString() {
        return "parentComment{" +
                "comment_id='" + comment_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", content='" + content + '\'' +
                ", date='" + date + '\'' +
                ", profile='" + profile + '\'' +
                ", parent_id='" + parent_id + '\'' +
                ", childComments=" + childComments +
                '}';
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public ArrayList<comment> getChildComments() {
        return childComments;
    }

    public void setChildComments(ArrayList<comment> childComments) {
        this.childComments = childComments;
    }
}
