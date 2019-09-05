package com.example.application.Broadcast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.example.application.IPclass;
import com.example.application.Logg;
import com.example.application.R;
import com.example.application.Retrofit2.Repo.GETS.BROADCAST.REAL_TIME_LOCATION;
import com.example.application.Retrofit2.RequestApi;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.android.ui.IconGenerator;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class ViewerLiveStreamerMapLocationActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "ViewerLiveStreamerMapLo";
    private GoogleMap mMap;
    RequestApi requestApi;
    Intent intent;
    public static String route_stream;
    public static boolean flag = true;

    //최신 스트리머의 현재 위치
    LatLng latLng;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_viewer_live_streamer_map_location);

        flag = true;


        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        // 레트로핏 객체 생성
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + IPclass.IP_ADDRESS + "/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        requestApi = retrofit.create(RequestApi.class);


        intent = getIntent();
        route_stream = intent.getStringExtra("route_stream");

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);





        Logg.i("====================================test=========================");
        GET_REALTIME get_realtime = new GET_REALTIME();
        Logg.i("====================================test=========================");
        get_realtime.start();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }



    // ============================================== 네트워크 통신 ===========================================

    //서버로 부터 스트리머의 현재 가장 최신의 위치 하나 만 가져옴
    public void GET_REALTIME_BROADCAST(String endpoint){


        Call<REAL_TIME_LOCATION> real_time_locationCall = requestApi.GET_REAL_LOCATION_RESULT_CALL(endpoint, route_stream);
        real_time_locationCall.enqueue(new Callback<REAL_TIME_LOCATION>() {
            @Override
            public void onResponse(Call<REAL_TIME_LOCATION> call, Response<REAL_TIME_LOCATION> response) {
                if(!response.isSuccessful()){
                    Log.d(TAG, "onResponse: " + response.message());
                    Logg.i("====================================test=========================");
                }

                REAL_TIME_LOCATION real_time_location = response.body();
//                Log.d(TAG, "onResponse: " + real_time_location.toString());




                Log.d(TAG, "onResponse: " + real_time_location.getLat());
                Log.d(TAG, "onResponse: " + real_time_location.getLon());
                Log.d(TAG, "onResponse: " + real_time_location.getBroad_cast_time());
                Log.d(TAG, "onResponse: " + real_time_location.getRoute_stream());
                Log.d(TAG, "onResponse: " + real_time_location.getUser_id());

                current_streamer_location(Double.parseDouble(real_time_location.getLat()), Double.parseDouble(real_time_location.getLon()));

            }

            @Override
            public void onFailure(Call<REAL_TIME_LOCATION> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }


        });
    }





    public void current_streamer_location(Double latitude, Double longitude) {

        mMap.clear();

        LatLng latLng = new LatLng(latitude, longitude);

        IconGenerator iconGenerator = new IconGenerator(getApplicationContext());
        Bitmap bitmap = iconGenerator.makeIcon("스트리머의 현재 위치");
        mMap.addMarker(new MarkerOptions().position(latLng).title("위도 " + latitude + " , 경도 " + longitude).icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));

    }




    class GET_REALTIME extends Thread{

        @Override
        public void run() {

            while(flag){

                try {
                    Logg.i("====================================test=========================");
                    GET_REALTIME_BROADCAST("realtime_location.php");
                    Logg.i("====================================test=========================");
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }


    // ===================================================================== 생명주기 ==============================================================


    @Override
    protected void onDestroy() {
        super.onDestroy();
        flag = false;
    }
}
