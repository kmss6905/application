package com.example.application.Broadcast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.application.R;
import com.google.android.gms.maps.GoogleMap;

public class ShowTripInfoListActivity extends AppCompatActivity {

    GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_trip_info_list);

    }
}
