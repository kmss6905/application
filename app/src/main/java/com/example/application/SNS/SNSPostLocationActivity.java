package com.example.application.SNS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.application.R;
import com.example.application.Retrofit2.Repo.AddLiveStream;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SNSPostLocationActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "SNSPostLocationActivity";
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;


    private GoogleMap mMap;
    private PlacesClient placesClient;
    private Marker marker;
    private MarkerOptions markerOptions;


    //부득이한 참조변수
    TextView addressText;


    // 전역 결과 값 담을 변수
    // 주소 , 위도경도 위치
    public static String Address;
    public static LatLng latLng;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                latLng = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                Address = place.getAddress();

                System.out.println("담겼나 확인하자 : latlng : " + latLng.latitude + " // lon : " + latLng.longitude);
                System.out.println("담겼나 확인하자 : Address : " + Address);

                mMap.clear(); // 맵에 있는 마커 지우고

                addressText.setText(Address); // 주소 텍스트 칸에 주소 넣어주고
                markerOptions = new MarkerOptions().title(place.getName()).position(latLng); // 마커를 만든 후
                mMap.addMarker(markerOptions); // 맵에 마커를 추가한다.
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng)); // 현재위치가 있는 곳으로 카메라가 이동합니다.
                mMap.animateCamera(CameraUpdateFactory.zoomTo(17));


            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snspost_location);


        //======================================================== 변수 참조 =============================================
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("위치 추가");

        Button findLocationBtn = findViewById(R.id.findLocationBtn); // 장소 검색 버튼
        addressText = findViewById(R.id.addressText); // 주소 텍스트
        Button okBtn = findViewById(R.id.okBtn); // 확인 버튼


        //======================================================== 구글 Place api 사용위한 key 등록 =============================================


        Places.initialize(getApplicationContext(), "AIzaSyAABuNkYxPajgwBw8hF4_VfH1l9uoK2ols"); // SDK 초기화
        placesClient = Places.createClient(this); // PLACE 인스턴스 만드릭


        // ======================================================== 구글 장소 검색 ===============================================
        //google map api 참조 변수
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // ======================================================== 구글 장소 검색 ===============================================


        // 버튼 클릭 메소드들
        // 1. 위치 추가 버튼
        findLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Set the fields to specify which types of place data to
                // return after the user has made a selection.
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);

                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(getApplicationContext());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });


        // 2. 확인 버튼
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("lat", latLng.latitude); // 위도
                resultIntent.putExtra("lon", latLng.longitude); // 경도
                resultIntent.putExtra("address", Address); // 주소
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }


    //====================================================== Google Map Api 오버라이드 메소드 =================================================


    // 1. 맵이 등장할 떄 가장 먼저 호출되는 메소드
    // 2. 구글맵을 인스턴스화 시킨다.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng seoul = new LatLng(37.484313, 126.973713);
        mMap.addMarker(new MarkerOptions().position(seoul).title("현재 위치"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(seoul));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
    }




    //======================================================== 메뉴 선택 ============================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_register, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);

    }

}
