package com.example.application.Retrofit2.Repo.GETS.SNS.COMMENT;

import java.util.ArrayList;

public class comment_list {
    String date;
    String content;
    String sns_post_id;
    String parent_comment_id;
    String id;
    String profile_img;
    String nick_name;
    String user_id;
    ArrayList<comment_list> comment_lists;



    public ArrayList<comment_list> getComment_lists() {
        return comment_lists;
    }

    public void setComment_lists(ArrayList<comment_list> comment_lists) {
        this.comment_lists = comment_lists;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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


//
//    public ArrayList<comment_list> getComment_lists() {
//        return comment_lists;
//    }
//
//    public void setComment_lists(ArrayList<comment_list> comment_lists) {
//        this.comment_lists = comment_lists;
//    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSns_post_id() {
        return sns_post_id;
    }

    public void setSns_post_id(String sns_post_id) {
        this.sns_post_id = sns_post_id;
    }

    public String getParent_comment_id() {
        return parent_comment_id;
    }

    public void setParent_comment_id(String parent_comment_id) {
        this.parent_comment_id = parent_comment_id;
    }


}
