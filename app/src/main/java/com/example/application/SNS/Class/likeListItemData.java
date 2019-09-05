package com.example.application.SNS.Class;

public class likeListItemData {
    String profile;
    String nickname;
    String id;
    String isFollowing;
    String unique_id;

    public String getUnique_id() {
        return unique_id;
    }

    public void setUnique_id(String unique_id) {
        this.unique_id = unique_id;
    }

    @Override
    public String toString() {
        return "likeListItemData{" +
                "profile='" + profile + '\'' +
                ", nickname='" + nickname + '\'' +
                ", id='" + id + '\'' +
                ", isFollowing='" + isFollowing + '\'' +
                '}';
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsFollowing() {
        return isFollowing;
    }

    public void setIsFollowing(String isFollowing) {
        this.isFollowing = isFollowing;
    }
}
