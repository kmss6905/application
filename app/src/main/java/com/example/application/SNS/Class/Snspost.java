package com.example.application.SNS.Class;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;

public class Snspost implements Serializable {
    String user_id;
    String profileImgUrl;
    ArrayList<String> photoUriArrayList;

    Boolean islike;
    String id;
    String likenum;
    String commentNum;
    String date;
    String comment;
    String address;
    String lat;
    String lon;
    String tag;

    @Override
    public String toString() {
        return "Snspost{" +
                "user_id='" + user_id + '\'' +
                ", profileImgUrl='" + profileImgUrl + '\'' +
                ", photoUriArrayList=" + photoUriArrayList +
                ", islike=" + islike +
                ", id='" + id + '\'' +
                ", likenum='" + likenum + '\'' +
                ", commentNum='" + commentNum + '\'' +
                ", date='" + date + '\'' +
                ", comment='" + comment + '\'' +
                ", address='" + address + '\'' +
                ", lat='" + lat + '\'' +
                ", lon='" + lon + '\'' +
                ", tag='" + tag + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    public String getContent() {
        return content;
    }

    String content;

    public Boolean getIslike() {
        return islike;
    }

    public void setIslike(Boolean islike) {
        this.islike = islike;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getAddress() {
        return address;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    public String getTag() {
        return tag;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getProfileImgUrl() {
        return profileImgUrl;
    }

    public ArrayList<String> getPhotoUriArrayList() {
        return photoUriArrayList;
    }

    public String getLikenum() {
        return likenum;
    }

    public String getCommentNum() {
        return commentNum;
    }

    public String getDate() {
        return date;
    }

    public String getComment() {
        return comment;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setProfileImgUrl(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }

    public void setPhotoUriArrayList(ArrayList<String> photoUriArrayList) {
        this.photoUriArrayList = photoUriArrayList;
    }

    public void setLikenum(String likenum) {
        this.likenum = likenum;
    }

    public void setCommentNum(String commentNum) {
        this.commentNum = commentNum;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
