package com.example.application.SNS.Class;

public class ac_photo_item {
    String post_id;
    String is_photos;
    String photo_url;

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getIs_photos() {
        return is_photos;
    }

    public void setIs_photos(String is_photos) {
        this.is_photos = is_photos;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    @Override
    public String toString() {
        return "ac_photo_item{" +
                "post_id='" + post_id + '\'' +
                ", is_photos='" + is_photos + '\'' +
                ", photo_url='" + photo_url + '\'' +
                '}';
    }
}
