package com.example.application.SNS.Class;

public class comment {
    String comment_id;
    String user_id;
    String content;
    String date;
    String profile;
    String parent_id;
    String nickname;
    String postid;

    @Override
    public String toString() {
        return "comment{" +
                "comment_id='" + comment_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", content='" + content + '\'' +
                ", date='" + date + '\'' +
                ", profile='" + profile + '\'' +
                ", parent_id='" + parent_id + '\'' +
                ", nickname='" + nickname + '\'' +
                ", postid='" + postid + '\'' +
                '}';
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
}
