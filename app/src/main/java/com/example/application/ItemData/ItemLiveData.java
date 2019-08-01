package com.example.application.ItemData;

public class ItemLiveData {

    String live_stream_title;
    String live_stream_thumbnail;
    String live_stream_streamer_nick;
    String live_stream_viewer;
    String live_stream_tag;

    public String getLive_stream_route_stream() {
        return live_stream_route_stream;
    }

    public void setLive_stream_route_stream(String live_stream_route_stream) {
        this.live_stream_route_stream = live_stream_route_stream;
    }

    String live_stream_route_stream;

    public ItemLiveData(String live_stream_title, String live_stream_thumbnail, String live_stream_streamer_nick, String live_stream_tag) {
        this.live_stream_title = live_stream_title;
        this.live_stream_thumbnail = live_stream_thumbnail;
        this.live_stream_streamer_nick = live_stream_streamer_nick;
        this.live_stream_tag = live_stream_tag;
    }

    public ItemLiveData() {

    }


    public void setLive_stream_title(String live_stream_title) {
        this.live_stream_title = live_stream_title;
    }

    public void setLive_stream_thumbnail(String live_stream_thumbnail) {
        this.live_stream_thumbnail = live_stream_thumbnail;
    }

    public void setLive_stream_streamer_nick(String live_stream_streamer_nick) {
        this.live_stream_streamer_nick = live_stream_streamer_nick;
    }

    public void setLive_stream_viewer(String live_stream_viewer) {
        this.live_stream_viewer = live_stream_viewer;
    }

    public void setLive_stream_tag(String live_stream_tag) {
        this.live_stream_tag = live_stream_tag;
    }

    public String getLive_stream_title() {
        return live_stream_title;
    }

    public String getLive_stream_thumbnail() {
        return live_stream_thumbnail;
    }

    public String getLive_stream_streamer_nick() {
        return live_stream_streamer_nick;
    }

    public String getLive_stream_viewer() {
        return live_stream_viewer;
    }

    public String getLive_stream_tag() {
        return live_stream_tag;
    }
}
