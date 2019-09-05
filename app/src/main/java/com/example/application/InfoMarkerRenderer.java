package com.example.application;

import android.content.Context;

import com.example.application.CLASS.GoogleMarkerItem;
import com.google.android.gms.maps.GoogleMap;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

public class InfoMarkerRenderer extends DefaultClusterRenderer<GoogleMarkerItem> {


    public InfoMarkerRenderer(Context context, GoogleMap map, ClusterManager<GoogleMarkerItem> clusterManager) {
        super(context, map, clusterManager);
    }


}
