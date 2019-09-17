package com.example.application.Retrofit2.Repo.GETS.SNS;

public class post {
    String id;
    String user_id;
    String photo_list;
    String lon;
    String lat;
    String tag;
    String date;
    String content;
    String like;
    String address;

    @Override
    public String toString() {
        return "post{" +
                "id='" + id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", photo_list='" + photo_list + '\'' +
                ", lon='" + lon + '\'' +
                ", lat='" + lat + '\'' +
                ", tag='" + tag + '\'' +
                ", date='" + date + '\'' +
                ", content='" + content + '\'' +
                ", like='" + like + '\'' +
                ", address='" + address + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPhoto_list() {
        return photo_list;
    }

    public void setPhoto_list(String photo_list) {
        this.photo_list = photo_list;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }



}
