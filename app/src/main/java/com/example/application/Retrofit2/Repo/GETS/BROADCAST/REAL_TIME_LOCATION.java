package com.example.application.Retrofit2.Repo.GETS.BROADCAST;

public class REAL_TIME_LOCATION {
    String lat;
    String lon;
    String user_id;
    String broad_cast_time;
    String route_stream;


    public String getRoute_stream() {
        return route_stream;
    }

    public void setRoute_stream(String route_stream) {
        this.route_stream = route_stream;
    }


    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getBroad_cast_time() {
        return broad_cast_time;
    }

    public void setBroad_cast_time(String broad_cast_time) {
        this.broad_cast_time = broad_cast_time;
    }

    @Override
    public String toString() {
        return "REAL_TIME_LOCATION{" +
                "lat='" + lat + '\'' +
                ", lon='" + lon + '\'' +
                ", user_id='" + user_id + '\'' +
                ", broad_cast_time='" + broad_cast_time + '\'' +
                ", route_stream='" + route_stream + '\'' +
                '}';
    }
}
