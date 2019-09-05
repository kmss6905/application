package com.example.application.SNS.Class;

import android.net.Uri;

import retrofit2.http.Url;

public class photo {
    Uri url;

    public Uri getUrl() {
        return url;
    }

    public void setUrl(Uri url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "photo{" +
                "url=" + url +
                '}';
    }
}
