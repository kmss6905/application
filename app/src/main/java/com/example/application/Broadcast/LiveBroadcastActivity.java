package com.example.application.Broadcast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.application.CLASS.GoogleMarkerItem;
import com.example.application.DIALOG.PostTripDialogActivity;
import com.example.application.IPclass;
import com.example.application.ItemData.ItemLiveData;
import com.example.application.Logg;
import com.example.application.MainActivity;
import com.example.application.R;
import com.example.application.Retrofit2.Repo.AddLiveStream;
import com.example.application.Retrofit2.Repo.LivestreamInfo;
import com.example.application.Retrofit2.Repo.PostResult;
import com.example.application.Retrofit2.RequestApi;
import com.example.application.UuidTest;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.maps.android.clustering.ClusterManager;
import com.wowza.gocoder.sdk.api.WowzaGoCoder;
import com.wowza.gocoder.sdk.api.android.opengl.WOWZGLES;
import com.wowza.gocoder.sdk.api.broadcast.WOWZBroadcast;
import com.wowza.gocoder.sdk.api.broadcast.WOWZBroadcastConfig;
import com.wowza.gocoder.sdk.api.configuration.WOWZMediaConfig;
import com.wowza.gocoder.sdk.api.devices.WOWZAudioDevice;
import com.wowza.gocoder.sdk.api.devices.WOWZCamera;
import com.wowza.gocoder.sdk.api.devices.WOWZCameraView;
import com.wowza.gocoder.sdk.api.errors.WOWZError;
import com.wowza.gocoder.sdk.api.errors.WOWZStreamingError;
import com.wowza.gocoder.sdk.api.geometry.WOWZSize;
import com.wowza.gocoder.sdk.api.logging.WOWZLog;
import com.wowza.gocoder.sdk.api.render.WOWZRenderAPI;
import com.wowza.gocoder.sdk.api.status.WOWZState;
import com.wowza.gocoder.sdk.api.status.WOWZStatus;
import com.wowza.gocoder.sdk.api.status.WOWZStatusCallback;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.security.auth.login.LoginException;

import gun0912.tedbottompicker.TedBottomPicker;
import gun0912.tedbottompicker.TedBottomSheetDialogFragment;
import gun0912.tedbottompicker.TedRxBottomPicker;
import noman.googleplaces.NRPlaces;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesListener;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/** 구글 api **/
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import noman.googleplaces.PlacesException;
import retrofit2.http.Url;


import static com.example.application.R.id.login_title_text;
import static com.example.application.R.id.mark;
import static com.example.application.R.id.post_map_trip_info;

public class LiveBroadcastActivity extends AppCompatActivity implements
        WOWZStatusCallback, PlacesListener {



    public static boolean flag = true;
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 99;
    private static final String TAG = "LiveBroadcastActivity";

    //SharedPerferences 에서 ID 를 가져옵니다.
    SharedPreferences sharedPreferences;

    // 닉네임 가져옴
    Intent intent;


    // routeStream 타이틀 만드는 클래스
    UuidTest uuidTest = new UuidTest();


    /**
     *  방송 타이틀, 테그, id값,
     */
    String title;
    String tag;
    String id;
    String routeStream;


    //retrofit 통신
    RequestApi requestApi;


    // ================================================================ GoCoder 위한 변수들=======================================================================

    private WowzaGoCoder goCoder; // 상위 레벨의 GoCoder API 인터페이스
    private WOWZCameraView goCoderCameraView; // GoCoder SDK camera view


    private WOWZAudioDevice goCoderAudioDevice; // The GoCoder SDK audio device
    private WOWZBroadcast goCoderBroadcaster; // GoCoder sdk broadcast
    private WOWZBroadcastConfig goCoderBroadcastConfig; // boradcast config setting
    private WOWZCamera wowzCamera;

    private static final int PERMISSIONS_REQUEST_CODE = 0x1; // 안드로이드 권한 다루기 , 카메라, 보이스
    private boolean mPermissionsGranted = true;
    private String[] mRequiredPermissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    // wowza 스크린샷 스레드로 돌릴 떄 한 번만 작동하기 위한 플래그!
    private AtomicBoolean mGrabFrame = new AtomicBoolean(false);
    private AtomicBoolean mSavingFrame = new AtomicBoolean(false);


    public static String toastStr = "";
    public static File jpegFile;


    // 시간 측정용
    private Chronometer Chronometer_stream_time;
    private boolean running;
    private long pauseOffset;

    public static int elepseMillis;


    // 버튼 참조
    ImageButton btn_back;// 뒤로가기 버튼
    TextView text_broadcast_title; // 방송 타이틀
    ImageButton btn_edit_broadcast_title; //방송 타이틀 수정 버튼
    ImageButton btn_flash_on; // 플래시 온 버튼
    ImageButton btn_flash_off; // 플래시 오프 버튼
    ImageButton btn_mic_on; // 마이크 온 버튼
    ImageButton btn_mic_off; // 마이크 오프 버튼
    ImageButton btn_camera_reverse; // 카메라 앞뒤 바꾸기 버튼
    ImageButton btn_add_trip_info; // 여행정보 등록하기 버튼
    ImageButton btn_add_tilter; // 영상 필터 버튼
    ImageButton btn_add_hashtag;
    ImageButton btn_start_broadcast; // 스트리밍 시작 버튼
    ImageButton btn_stop_broadcast; // 스트리밍 중지 버튼
    ImageButton btn_show_chat; // 채팅 보여주기 버튼
    ImageButton btn_hide_chatlist; // 채팅 내용만 숨기기 버튼
    ImageButton btn_show_chatlist; // 채팅 내용만 보이기 버튼
    RecyclerView recyclerView_chat_list; // 채팅 리스트 리사이클러뷰
    TextView space_when_click_chat_btn; // 최초의 채팅 버튼 눌렀을 때 모든 것 사리게 하는 버튼
    TextView space_chatlist; // 채팅 리스트 숨기기 버튼
    LinearLayout layout_chatting; // 채팅 레이아웃 ( btn_show_chat 누를 경우 채팅레이아웃 gone , 채팅버튼 공간 visibility)


    //==================================================방송 정보/ 버튼 참조 ========================================

    LinearLayout layout_broadcast_status_when_up; // 방송정보
    TextView textView_like_num; // 좋아요 수
    TextView textView_viewers_num; // 시청자 수
    TextView textView_donation_amount; // 도네 액수
    TextView textView_subscribe_viewer_num; // 구독시청자 수


    //================================================구글 맵 관련 AP==================================================

    private GoogleMap mMap; //구글 맵 객체를 전역으로 선언하여 다른곳에서도 사용할 수 있도록 하자.
    private GoogleApiClient googleApiClient = null; // google place 사용 위한 클라이언트 인스턴스
    private Marker currentMarker = null;

    private LatLng searchLocation; // 검색 결과의 위도 경도
    private MarkerOptions searchMarkerOption; // 검색 결과의 마커 옵션

    public static LatLng currentLocation;
    public static MarkerOptions currentMarkerOption;

    public static LatLng realTimeLocation; // 실시간 위치를 받은 위치 변수


    // 새롭게 실시간
    private FusedLocationProviderClient fusedLocationClient;


    List<Marker> previous_marker = null; // google place api



    // locationManager 변수
    private LocationManager mLocationManager;
    private LocationListener locationListener;

    // 위성 개수



////    private static final String TAG = "googlemap_example";
//    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
//    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
//    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
//    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초
//
//    private AppCompatActivity mActivity;
//    boolean askPermissionOnceAgain = false;
//    boolean mRequestingLocationUpdates = false;
//    Location mCurrentLocatiion;
//    boolean mMoveMapByUser = true;
//    boolean mMoveMapByAPI = true;
//    LatLng currentPosition;
//
//    LocationRequest locationRequest = new LocationRequest()
//            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//            .setInterval(UPDATE_INTERVAL_MS)
//            .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);
//
//


    PlacesClient placesClient;

    // ================================================================================ 실시간 현재위치 변수 =========================================================================



    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초

    Location mCurrentLocatiion;
    LatLng currentPosition;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;






    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_broadcast);
        sharedPreferences = getSharedPreferences("file", MODE_PRIVATE);

        //=============================================== LocationManager 사용 =============================================
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE); // 로케이션 매니져 생성




        // ============================================= 실시간 위치 관련 변수 및 객체 생성===================================================


        locationRequest = new LocationRequest() // 위치 요청 객체 생성
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);

        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();

        builder.addLocationRequest(locationRequest);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);





        //================================================ 구글 Place API 사용하기 ===============================================


        Places.initialize(getApplicationContext(), "AIzaSyAABuNkYxPajgwBw8hF4_VfH1l9uoK2ols"); // SDK 초기화
        placesClient = Places.createClient(this); // PLACE 인스턴스 만드릭






        intent = getIntent();

        // 방송 타이틀, 태그, 제목, id
        title = intent.getStringExtra("nickname") + " 님이 현재 방송 중입니다"; // 타이틀
        tag = "#여행 #먹방 #신입"; // 태그
        routeStream = uuidTest.getUnicVodString(); // 루트 스트림
        id = sharedPreferences.getString("id", ""); // pri ID


        // 레드로핏 객체 생성
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + IPclass.IP_ADDRESS + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        requestApi = retrofit.create(RequestApi.class);




        // 버튼 참조
        btn_back = findViewById(R.id.btn_back); // 뒤로가기 버튼
        text_broadcast_title = findViewById(R.id.text_broadcast_title); // 방송 타이틀
        btn_edit_broadcast_title = findViewById(R.id.btn_edit_broadcast_title); //방송 타이틀 수정 버튼
        btn_flash_on = findViewById(R.id.btn_flash_on); // 플래시 온 버튼
        btn_flash_off = findViewById(R.id.btn_flash_off); // 플래시 오프 버튼
        btn_mic_on = findViewById(R.id.btn_mic_on); // 마이크 온 버튼
        btn_mic_off = findViewById(R.id.btn_mic_off); // 마이크 오프 버튼
        btn_camera_reverse = findViewById(R.id.btn_camera_reverse); // 카메라 앞뒤 바꾸기 버튼
        btn_add_trip_info = findViewById(R.id.btn_add_trip_info); // 여행정보 등록하기 버튼
        btn_add_tilter = findViewById(R.id.btn_add_tilter); // 영상 필터 버튼
        btn_add_hashtag = findViewById(R.id.btn_add_hashtag); // 해쉬태그 추가 버튼
        btn_start_broadcast = findViewById(R.id.btn_start_broadcast); // 스트리밍 시작 버튼
        btn_stop_broadcast = findViewById(R.id.btn_stop_broadcast); // 스트리밍 중지 버튼
        btn_show_chat = findViewById(R.id.btn_show_chat); // 채팅 보여주기 버튼
        btn_hide_chatlist = findViewById(R.id.btn_hide_chatlist); // 채팅 내용만 숨기기 버튼
        btn_show_chatlist = findViewById(R.id.btn_show_chatlist); // 채팅 내용만 보이기 버튼
        recyclerView_chat_list = findViewById(R.id.recyclerView_chat_list); // 채팅 리스트 리사이클러뷰
        space_when_click_chat_btn = findViewById(R.id.space_when_click_chat_btn); // 최초의 채팅 버튼 눌렀을 때 모든 것 사리게 하는 버튼
        space_chatlist = findViewById(R.id.space_chatlist); // 채팅 리스트 숨기기 버튼
        layout_chatting = findViewById(R.id.layout_chatting); // 채팅 레이아웃 ( btn_show_chat 누를 경우 채팅레이아웃 gone , 채팅버튼 공간 visibility)
        Chronometer_stream_time = findViewById(R.id.Chronometer_stream_time); // 시간 측정
        Chronometer_stream_time.setFormat("방송시간 : %s");






        text_broadcast_title.setText(title); //set title

        //==================================================방송 정보 버튼 참조 ========================================



        layout_broadcast_status_when_up = findViewById(R.id.layout_broadcast_status_when_up);
                textView_like_num = findViewById(R.id.textView_like_num);
        textView_viewers_num = findViewById(R.id.textView_viewers_num);
                textView_donation_amount = findViewById(R.id.textView_donation_amount);
        textView_subscribe_viewer_num = findViewById(R.id.textView_subscribe_viewer_num);














        //=================================================WowzaGoCoder 방송 설정 및 방송 하기================================================


        // 1. wowza goCoder sdk 등록 및 초기화

        goCoder = WowzaGoCoder.init(getApplicationContext(), "GOSK-BD46-010C-E552-981F-FF61");




        if (goCoder == null) {
            // 만약에 객체가 형성되지 않았다? 즉, 제대로 등록 밑 초기화 되지 않았기 때문에 에러를 표시한다
            WOWZError wowzError = WowzaGoCoder.getLastError();
            Toast.makeText(this, "goCoder 초기화에서 문제 에러내용 입니다 : " + wowzError.getErrorDescription(), Toast.LENGTH_SHORT).show();
            return;
        }




        // 2. 카메라 프리뷰 시작
        // 우선 영상이 보이게 될 wowzaCameraView(in XML)를 참조합니다.
        // 해당 카메라 view에 영상을 띄웁니다.(당연히 권한이 승인 되었을 경우에 시작합니다.)
        goCoderCameraView = findViewById(R.id.camera_preview);


        // ================================================ 와우자 비디오 프레임 리스너( for 스크린샷 , 프리뷰에 달거임, 여기서 스크린 샷 기능 ================================================

        WOWZRenderAPI.VideoFrameListener videoFrameListener = new WOWZRenderAPI.VideoFrameListener() {

            @Override
            // onWZVideoFrameListenerFrameAvailable() will only be called nce isWZVideoFrameListenerActive() returns true
            public boolean isWZVideoFrameListenerActive() {
                // 계속 실행되고 있음

                // 프레임 수신기가 활성화중??
                Logg.i("=======================test======================");
                // Only indicate the frame listener once the screenshot button has been pressed
                // and when we're not in the process of saving a previous screenshot
                Logg.i("=======================test======================"
                        + " mGrabFrame.get() : " + mGrabFrame.get() + " // "
                        + "mSavingFrame.get() : " + mSavingFrame.get());


                return (mGrabFrame.get() && !mSavingFrame.get());



            }

            @Override
            public void onWZVideoFrameListenerInit(WOWZGLES.EglEnv eglEnv) {
                // nothing needed for this example
                Logg.i("=======================test======================");
            }


            @Override
            // onWZVideoFrameListenerFrameAvailable() is called when isWZVideoFrameListenerActive() = true
            // and a new frame has been rendered on the camera preview display surface
            public void onWZVideoFrameListenerFrameAvailable(WOWZGLES.EglEnv eglEnv, WOWZSize frameSize, int frameRotation, long timecodeNanos) {


                Logg.i("=======================test======================");
                // set these flags so that this doesn't get called numerous times in parallel
                mSavingFrame.set(true);
                Logg.i("=======================test======================");
                mGrabFrame.set(false);
                Logg.i("=======================test======================");

                // 어디서 해당 비트맵을 얻어 오는 지 정확히 모른다 ㅠㅠ 그것이 아는 것이 중요하다.
                // create a pixel buffer and read the pixels from the camera preview display surface using GLES
                final WOWZSize bitmapSize = new WOWZSize(frameSize);
                Logg.i("=======================test======================");
                final ByteBuffer pixelBuffer = ByteBuffer.allocateDirect(bitmapSize.getWidth() * bitmapSize.getHeight() * 4);

                Logg.i("=======================test======================");
                pixelBuffer.order(ByteOrder.LITTLE_ENDIAN);
                Logg.i("=======================test======================");
                GLES20.glReadPixels(0, 0, bitmapSize.getWidth(),  bitmapSize.getHeight(),
                        GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, pixelBuffer);

                final int eglError = WOWZGLES.checkForEglError(TAG + "(glReadPixels)");
                Logg.i("=======================test======================");





                if (eglError != EGL14.EGL_SUCCESS) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Logg.i("=======================test======================");
                            btn_add_trip_info.setEnabled(true);
                            Logg.i("=======================test======================");
                            btn_add_trip_info.setClickable(true);
                            Logg.i("=======================test======================");
                            Toast.makeText(getApplicationContext(), WOWZGLES.getEglErrorString(eglError), Toast.LENGTH_LONG).show();
                            Logg.i("=======================test======================");
                        }
                    });
                    Logg.i("=======================test======================");



                    mSavingFrame.set(false);


                    Logg.i("=======================test======================");
                    return;
                }
                Logg.i("=======================test======================");
                pixelBuffer.rewind();
                Logg.i("=======================test======================");




                // now that we have the pixels, create a new thread for transforming and saving the bitmap
                // so that we don't block the camera preview display renderer

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Logg.i("=======================test======================");
                        BufferedOutputStream bitmapStream = null;
                        Logg.i("=======================test======================");
                        StringBuffer statusMessage = new StringBuffer();
                        Logg.i("=======================test======================");

                        try {
                            Logg.i("=======================test======================");
                            jpegFile = getOutputJpegFile();


                            Logg.i("=======================test======================");

                            if (jpegFile != null) { // 파일객체가 만들어 진다면? 해당 경로로 파일이 만들어 진 객체가 반환된다면
                                Logg.i("=======================test======================");
                                bitmapStream = new BufferedOutputStream(new FileOutputStream(jpegFile));
                                Logg.i("=======================test======================");
                                Bitmap bmp = Bitmap.createBitmap(bitmapSize.getWidth(), bitmapSize.getHeight(), Bitmap.Config.ARGB_8888);
                                Logg.i("=======================test======================");
                                bmp.copyPixelsFromBuffer(pixelBuffer);
                                Logg.i("=======================test======================");
                                Matrix xformMatrix = new Matrix();
                                Logg.i("=======================test======================");
                                xformMatrix.setScale(-1, 1);  // flip horiz
                                Logg.i("=======================test======================");
                                xformMatrix.preRotate(180);  // flip vert
                                Logg.i("=======================test======================");
                                bmp = Bitmap.createBitmap(bmp, 0, 0, bitmapSize.getWidth(), bitmapSize.getHeight(), xformMatrix, false);
                                Logg.i("=======================test======================");

                                bmp.compress(Bitmap.CompressFormat.JPEG, 90, bitmapStream);
                                Logg.i("=======================test======================");
                                bmp.recycle();
                                Logg.i("=======================test======================");

                                statusMessage.append("Screenshot saved to ").append(jpegFile.getAbsolutePath());
                                Logg.i("=======================test======================");
                            } else {
                                Logg.i("=======================test======================");
                                statusMessage.append("The directory for the screenshot could not be created");
                                Logg.i("=======================test======================");
                            }

                        } catch(Exception e) {
                            Logg.i("=======================test======================");
                            WOWZLog.error(TAG, "An exception occurred trying to create the screenshot", e);
                            Logg.i("=======================test======================");
                            statusMessage.append(e.getLocalizedMessage());
                            Logg.i("=======================test======================");

                        } finally {
                            Logg.i("=======================test======================");
                            if (bitmapStream != null) {
                                try {
                                    Logg.i("=======================test======================");
                                    bitmapStream.close();
                                    Logg.i("=======================test======================");
                                } catch (IOException closeException) {
                                    // ignore exception on close
                                    Logg.i("=======================test======================");
                                }
                            }
                            Logg.i("=======================test======================");

                            toastStr = statusMessage.toString();
                            Logg.i("=======================test======================");


                            // 저장 결과 메세지를 띄워 주는 부분(UI thread)
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Logg.i("=======================test======================");
                                    Toast.makeText(getApplicationContext(), toastStr, Toast.LENGTH_LONG).show();
                                    Logg.i("=======================test======================");
                                    btn_add_trip_info.setEnabled(true);
                                    Logg.i("=======================test======================");
                                    btn_add_trip_info.setClickable(true);
                                    Logg.i("=======================test======================");
                                }
                            });





                            Logg.i("=======================test======================");
                            mSavingFrame.set(false);
                            Logg.i("=======================test======================");
                        }
                    }
                }).start();



            }

            @Override
            public void onWZVideoFrameListenerRelease(WOWZGLES.EglEnv eglEnv) {
                // nothing needed for this example
                Logg.i("=======================test======================");
            }
        };


        goCoderCameraView.registerFrameListener(videoFrameListener);















        if (mPermissionsGranted && goCoderCameraView != null) { // 퍼미션에 대한 승인과 카메라뷰가 정상적으로 참조 되었을 경우
            if (goCoderCameraView.isPreviewPaused()) { // 프리뷰가 멈춘 상태라면
                goCoderCameraView.onResume(); // resume으로 돌리고
            } else { // 프리뷰가 pause 상태가 아니라면 프리뷰를 처음부터 시작합니다.
                goCoderCameraView.startPreview(new WOWZCameraView.PreviewStatusListener() {
                    @Override
                    public void onWZCameraPreviewStarted(WOWZCamera wowzCamera, WOWZSize wowzSize, int i) {

                    }

                    @Override
                    public void onWZCameraPreviewStopped(int i) {

                    }

                    @Override
                    public void onWZCameraPreviewError(WOWZCamera wowzCamera, WOWZError wowzError) {

                    }
                });


            }
        }


        // 방송을 추가하고 구성합니다
        // 오디오 디바이스 추가
        goCoderAudioDevice = new WOWZAudioDevice();

        // 방송을 위한 인스턴스 만듬
        goCoderBroadcaster = new WOWZBroadcast(); // 이곳에



        // 방송에 추가되는 설정 인스턴스
        goCoderBroadcastConfig = new WOWZBroadcastConfig(WOWZMediaConfig.FRAME_SIZE_1280x720);

        // 방송하기 위한 설정
        // Set the connection properties for the target Wowza Streaming Engine server or Wowza Streaming Cloud live stream
        goCoderBroadcastConfig.setHostAddress("13.209.208.103");
        goCoderBroadcastConfig.setPortNumber(1935);
        goCoderBroadcastConfig.setApplicationName("live");
        goCoderBroadcastConfig.setStreamName(routeStream); // 사용자가 수정한 타이틀을 받아옵니다. 문제가 있다. 한글로 타이틀을 정할 경우 나중에 동영상 녹화시에 해당 타이틀.mp4 에서 한글의 경우 깨진다. 따라서 vor가 저장된 경로를 찾을 수 없게 된다

        goCoderBroadcastConfig.setAudioEnabled(true);
        goCoderBroadcastConfig.setVideoEnabled(true);
        goCoderBroadcastConfig.setUsername("wowza");
        goCoderBroadcastConfig.setPassword("i-0dfdb5881429cfe3b");
        goCoderBroadcastConfig.setVideoBroadcaster(goCoderCameraView); // 방송을 하게 될 원본 view를 지정합니다
        goCoderBroadcastConfig.setAudioBroadcaster(goCoderAudioDevice); // 방송을 할 떄 원본 받아올 audio 를 지정합니다.



        //==========================================================방송시작하기=======================================================


        // 방송시작하기 버튼
        btn_start_broadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mPermissionsGranted) return;

                // Ensure the minimum set of configuration settings have been specified necessary to
                // initiate a broadcast streaming session
                WOWZStreamingError configValidationError = goCoderBroadcastConfig.validateForBroadcast();
                if (configValidationError != null) {
                    Toast.makeText(getApplicationContext(), configValidationError.getErrorDescription(), Toast.LENGTH_LONG).show();
                } else if (goCoderBroadcaster.getStatus().isRunning()) {
                    // Stop the broadcast that is currently running
                    goCoderBroadcaster.endBroadcast(goCoderBroadcaster.getStatusCallback());


                } else {

                    // 아까 만든 goCoderBroeadcaster에 startBroadcast 메소드를 호출하고 파라미터로 아까 만든 goCoderBroadConfig 객체와 , 콜백메소드를 전달한다.
                    goCoderBroadcaster.startBroadcast(goCoderBroadcastConfig, goCoderBroadcaster.getStatusCallback());



                    Toast.makeText(getApplicationContext(), "방송을 시작합니다", Toast.LENGTH_SHORT).show();
                    // 방송을 시작하면 버튼을 바꾼다. 녹화 버튼 이미지는 숨기고
                    btn_start_broadcast.setVisibility(View.GONE);
                    // 방송 멈추기 버튼을 보이게 한다.
                    btn_stop_broadcast.setVisibility(View.VISIBLE);
                    Chronometer_stream_time.setVisibility(View.VISIBLE);


                    startChronometer(); // 방송 시간을 측정한다.

                    /**
                     *  방송이 시작되었을 경우 데이터 베이스에 방송제목, 방송 타입, 스트리머 닉네임num, 방송 태그, 방송 Url로 방송정보가 저장된다.
                     *
                     */


                    // POST ADD
                    POST_LIVE_STREAM("add.php");


                    // POST_REAL_LOCATION


                    RealTimeLocation realTimeLocation = new RealTimeLocation();
                    realTimeLocation.start();
                    /**
                     * 테스트
                     */
                    startLocationUpdates(); // 3. 위치 업데이트 시작

                }
            }
        });


        // 방송 종료 버튼
        btn_stop_broadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(goCoderBroadcaster.getStatus().isRunning()){ //현재 여전히 방송중이라면
                    goCoderBroadcaster.endBroadcast(goCoderBroadcaster.getStatusCallback()); //끈다
                    goCoderCameraView.clearView();

                    flag = false;
                    pauseChronometer(); // 시간초 멈춤
                    resetChronometer(); // 시간초 다시
                    btn_stop_broadcast.setVisibility(View.GONE); // 정지 버튼 안보이게 한후
                    btn_start_broadcast.setVisibility(View.VISIBLE); // 방송 시작버튼 보이게함
                    Chronometer_stream_time.setVisibility(View.GONE); // 방송시간 버튼 없앰
                    Toast.makeText(getApplicationContext(), "방송을 종료합니다", Toast.LENGTH_SHORT).show();

                    POST_LIVE_STREAM("delete.php");



                    // 방송이 종료되면 방송 시작시간과 방송종료 시간을 알 수 있으며 vod로 들어가게될 방송 제목 수정, 카테고리 수정, 후원받은 총 금액
                    // 최대 시청자 수 를 알 수 있다.
                }
            }
        });


        // 채팅 창 나오게 하는 버튼
        btn_show_chat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(layout_chatting.getVisibility() == View.GONE){ // 채팅창이 사라진 상태이면
                    layout_chatting.setVisibility(View.VISIBLE); // 클릭시 다시 보이게 하고
                    space_when_click_chat_btn.setVisibility(View.GONE); // 채웠던 공간을 다시 내준다
                }else {
                    layout_chatting.setVisibility(View.GONE); // 채팅 레이아웃을 없애고
                    space_when_click_chat_btn.setVisibility(View.VISIBLE); // 그 공간을 채운다.
                }

            }
        });


        // 채팅 리스트 숨기기 버튼
        btn_hide_chatlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btn_hide_chatlist.getVisibility() == View.VISIBLE) { // 만약에 내려가기 버튼이 보이는 상태라면
                    btn_hide_chatlist.setVisibility(View.GONE); // 내려가기 버튼은 사라지고
                    recyclerView_chat_list.setVisibility(View.GONE); // 채팅 리스트도 사라지고
                    btn_show_chatlist.setVisibility(View.VISIBLE); // 올라가기 버튼은 나타난다.
                    space_chatlist.setVisibility(View.VISIBLE); // 해당공간을 채운다.
                }
            }
        });

        // 채팅 리스트 보이기 버튼
        btn_show_chatlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btn_hide_chatlist.getVisibility() == View.GONE && btn_show_chatlist.getVisibility() == View.VISIBLE){ //리스트 감추기 버튼이 사라진 상태라면
                    btn_hide_chatlist.setVisibility(View.VISIBLE); // 내려가기 버튼은 다시 보이고
                    recyclerView_chat_list.setVisibility(View.VISIBLE); // 채팅리스트도 다시 보이고
                    btn_show_chatlist.setVisibility(View.GONE); //채팅 리스트 보이기 버튼은 이제 보이지 않게 하고
                    space_chatlist.setVisibility(View.GONE); // 채웠던 공간을 다시 없앤다.
                }
            }
        });


        // 해쉬태그 추가 버튼
        btn_add_hashtag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editHashTagDialog();
            }
        });



                    // 타이틀 수정 버튼 + 테그등을 수정합니다.
                    btn_edit_broadcast_title.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            show(); // 방송제목을 수정할 수 있는 다이얼로그를 띄움
                        }
                    });




                    wowzCamera = this.goCoderCameraView.getCamera(); // WowzaCamera 객체를 만든다.

                    // 플래시 켜기
                    btn_flash_on.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Enable the flash if the current camera has a flashlight (torch)
                            if(!(wowzCamera.isTorchOn())){
                                wowzCamera.setTorchOn(true);
                                btn_flash_on.setVisibility(View.GONE);
                                btn_flash_off.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                    // 플래시 끄기
                    btn_flash_off.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(wowzCamera.isTorchOn()){
                    wowzCamera.setTorchOn(false);
                    btn_flash_on.setVisibility(View.VISIBLE);
                    btn_flash_off.setVisibility(View.GONE);
                }
            }
        });




        // 음소거 버튼
        btn_mic_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(goCoderAudioDevice.isAudioEnabled()){ // 음소거가 아닐 경우에는
//                     goCoderAudioDevice.setMuted(true); // 음소거로 만든다.
                    goCoderAudioDevice.setAudioEnabled(false);
                    System.out.println("음소거 실행?");
                    System.out.println("goCoderAudioDevice.getStatus() : " + goCoderAudioDevice.getStatus());

                    btn_mic_on.setVisibility(View.GONE);
                    btn_mic_off.setVisibility(View.VISIBLE); // 음소거 상태를 보이게
                }
            }
        });

        // 음소거 해제 버튼
        btn_mic_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(goCoderAudioDevice.isAudioEnabled())){ // 음소거가 아닌 상태일 경우에는
                    goCoderAudioDevice.setAudioEnabled(true);
                    System.out.println("음소거 해제??");
                    System.out.println("goCoderAudioDevice.getStatus() : " + goCoderAudioDevice.getStatus());
                    btn_mic_on.setVisibility(View.VISIBLE); // 음소거가 아닌 상태가 보이도록
                    btn_mic_off.setVisibility(View.GONE);
                }
            }
        });


        //카메라 전환 버튼
        btn_camera_reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Set the active camera to the front camera if it's not active
                if (goCoderCameraView.getCamera().isFront()) {
                    goCoderCameraView.switchCamera();
                }else if(goCoderCameraView.getCamera().isBack()){ // 뒤 화면일 경우
                    goCoderCameraView.switchCamera(); // 앞을 보여준다
                }
            }
        });



        //여행 정보 등록 버튼
        btn_add_trip_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTakeScreenshot();
                postTripInfo();
//                startActivity(new Intent(getApplicationContext(), PostTripDialogActivity.class));
////                LatLng latLng = setCurrentLocation_foucusCamera();
//                LatLng latLng = new LatLng(setCurrentLocation_foucusCamera().latitude, setCurrentLocation_foucusCamera().longitude);
//                System.out.println("가져옴 위도 : " + latLng.latitude+ " // 경도 "+ latLng.longitude);
            }
        });


        //카메라 필터 버튼
        //뒤로가기 버튼
        /**
         *  뒤로가기 버튼을 누른다.
         *  방송중이 아닐 경우에는 홈엑티비티로 이동한다.
         *  방송 중일 경우에는 현재 방송중 입니다. 현재 방송을 종료 하시겠습니까? 라는 다이얼로그를 띄운다.
         *  확인 버튼을 누르면 현재 진행 중인 방송을 종료하고 다시보기로 저장 될 영상에 대한 세부적인 정보를 수정할 수 있는 다이얼로그가 나온다.
         *  수정을 한 후 확인버튼을 누르면 성공적으로 저장했다는 토스트 메세지가 뜨고 홈 액티비티로 돌아간다.
         *
         */
        // 뒤로가기 버튼을 누른다
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 방송 중이 아닐경우
                if(!(goCoderBroadcaster.getStatus().isRunning())){
                     onBackPressed(); //바로 홈 액티비티로 이동한다.

                // 방송 중일 경우
                }else if(goCoderBroadcaster.getStatus().isRunning()){
                    alert_broadcast_end(); // 경고 알림창을 띄운다.
                }
            }
        });





    }


    // 풀 스크린 모드 함수
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        if (rootView != null)
            rootView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }


    //==========================================================================================================================

    //
// Callback invoked in response to a call to ActivityCompat.requestPermissions() to interpret
// the results of the permissions request
//
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        mPermissionsGranted = true;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                // Check the result of each permission granted
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        mPermissionsGranted = false;
                    }
                }
            }
        }
    }

    //
// Utility method to check the status of a permissions request for an array of permission identifiers
//
    private static boolean hasPermissions(Context context, String[] permissions) {
        for (String permission : permissions)
            if (context.checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                return false;

        return true;


    }


    // 방송의 상태를 알려주는 콜백함수들
    @Override
    public void onWZStatus(WOWZStatus goCoderStatus) {
        // A successful status transition has been reported by the GoCoder SDK
        final StringBuffer statusMessage = new StringBuffer("Broadcast status: ");

        switch (goCoderStatus.getState()) {
            case WOWZState.STARTING:
                statusMessage.append("Broadcast initialization");
                System.out.println("방송 상태 : 초기화 합니다.");
                break;

            case WOWZState.READY:
                statusMessage.append("방송을 할 준비가 되었습니다.");
                System.out.println("방송 상태 : 방송 준비합니다.");
                break;

            case WOWZState.RUNNING:
                statusMessage.append("방송을 시작합니다");
                System.out.println("방송 상태 : 방송을 시작합니다.");
                break;

            case WOWZState.STOPPING:
                statusMessage.append("방송을 중지합니다");
                System.out.println("방송 상태 : 방송을 중지합니다.");
                break;

            case WOWZState.IDLE:
                statusMessage.append("방송을 종료합니다");
                System.out.println("방송 상태 : 방송을 종료합니다.");

                break;

            default:
                return;
        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LiveBroadcastActivity.this, statusMessage, Toast.LENGTH_LONG).show();
            }
        });

    }


    // 에러가 발생했을 때
    @Override
    public void onWZError(final WOWZStatus goCoderStatus) {
        // If an error is reported by the GoCoder SDK, display a message
        // containing the error details using the U/I thread
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LiveBroadcastActivity.this,
                        "Streaming error: " + goCoderStatus.getLastError().getErrorDescription(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }


    // ==================================================================== 방송 시간 측정 =======================================================

    public void startChronometer(){
        if(!running){
            Chronometer_stream_time.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            Chronometer_stream_time.start();
//            running = true;
        }
    }

    public void pauseChronometer(){
        if(running){
            Chronometer_stream_time.stop();
//            pauseOffset = SystemClock.elapsedRealtime() - Chronometer_stream_time.getBase();
            running = false;
        }
    }

    public void resetChronometer(){
        Chronometer_stream_time.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;
    }


    //====================================================================다이얼로그============================================================

    // 스트리밍 방송 제목 변경 다이얼로그 띄우기 함수
    // 방송 전이면 그냥 전역에 저장

    void show() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_broadcast_setting, null);
        builder.setView(view);



        final Button btn_change_title_cancel = (Button) view.findViewById(R.id.btn_change_title_cancel);
        final Button btn_change_title_ok = (Button) view.findViewById(R.id.btn_change_title_ok);
        final EditText editText_change_title = view.findViewById(R.id.editText_nick);


        editText_change_title.setText(text_broadcast_title.getText().toString()); //수정이기 때문에 우선 타이틀을 가져온다.
        final AlertDialog dialog = builder.create();





        editText_change_title.setSelection(editText_change_title.length()); // 방송 전체 선택
        editText_change_title.setSelectAllOnFocus(true); // 방송 제목 포커스
        editText_change_title.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                editText_change_title.post(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager inputMethodManager= (InputMethodManager) LiveBroadcastActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(editText_change_title, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
            }
        });
        editText_change_title.requestFocus();



        // (다이얼로그) 타이틀 수정 확인 버튼
        btn_change_title_ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                text_broadcast_title.setText(editText_change_title.getText().toString()); // 타이틀 제목을 수정한 타이틀로 바꾼다.
                title = editText_change_title.getText().toString(); // 제목 저장
                Toast.makeText(getApplicationContext(), "방송제목이 수정되었습니다.", Toast.LENGTH_SHORT).show();
                goCoderBroadcastConfig.setStreamName(editText_change_title.getText().toString()); // 사용자가 수정한 타이틀을 넣습니다.

                if(goCoderBroadcaster.getStatus().isRunning()){ //방송 중인 경우
                    POST_LIVE_STREAM("update.php");
                    dialog.dismiss();
                    return;
                }

                dialog.dismiss();
            }
        });


        // (다이얼로그) 타이틀 수정 취소버튼
        btn_change_title_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    //스트리밍 방송 태그 수정 다이얼로그
    void editHashTagDialog(){


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_tag, null);
        builder.setView(view);
        final Button btn_change_tag_cancel = (Button) view.findViewById(R.id.btn_change_tag_cancel);
        final Button btn_change_tag_ok = (Button) view.findViewById(R.id.btn_change_tag_ok);
        final EditText editText_tag = view.findViewById(R.id.editText_tag);

        editText_tag.setSelection(editText_tag.length());
        editText_tag.setText(tag);
        editText_tag.setSelectAllOnFocus(true); //태그 전체 선택

        editText_tag.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                editText_tag.post(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager inputMethodManager= (InputMethodManager) LiveBroadcastActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(editText_tag, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
            }
        });
        editText_tag.requestFocus();







        final AlertDialog dialog = builder.create();


        // (다이얼로그) 태그 수정 확인 버튼
        btn_change_tag_ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tag= editText_tag.getText().toString(); // 전역에 입력하고

                if(goCoderBroadcaster.getStatus().isRunning()){
                    POST_LIVE_STREAM("update.php");
                    Logg.i("===========================================test=====================");
                    dialog.dismiss();
                }

//                Toast.makeText(getApplicationContext(), "여행 태그가 추가되었습니다.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });


        // (다이얼로그) 타이틀 수정 취소버튼
        btn_change_tag_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    // 방송 종료 경고 알림창
    void alert_broadcast_end(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_broadcast_stop, null);
        builder.setView(view);


        final Button btn_change_title_cancel = (Button) view.findViewById(R.id.btn_dialog_cancel); //취소 버튼
        final Button btn_change_title_ok = (Button) view.findViewById(R.id.btn_dialog_ok); // 확인 버튼
        final AlertDialog dialog = builder.create();


        // (다이얼로그) 방송 경고 알림창 종료 버튼
        btn_change_title_ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //방송을 종료하고 다시보기 설정을 할 수 있는 알림창을 띄운다.
                btn_stop_broadcast.performClick(); // 방송을 종료한다.
                dialog.dismiss(); // 창 없앰
                onBackPressed(); // 뒤로가기
            }
        });

        // (다이얼로그) 방송 경고 알림창 취소 버튼
        btn_change_title_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                dialog.dismiss();
                startActivity(new Intent(getApplicationContext(), PostTripDialogActivity.class));

            }
        });

        dialog.show();


    }



    // (다이얼로그) 여행 정보 등록하기
    public void postTripInfo(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_post_trip_info, null, false);
        builder.setView(view);



        // 위젯 참조

        final TextView post_title_trip_info = view.findViewById(R.id.post_title_trip_info);
        final TextView post_tag_trip_info = view.findViewById(R.id.post_tag_trip_info);
        final ImageView post_captureimg_trip_info = view.findViewById(R.id.post_captureimg_trip_info);
        final Button post_cancle_tripinfo = view.findViewById(R.id.post_cancle_tripinfo); // 취소 버튼
        final Button post_ok_tripinfo = view.findViewById(R.id.post_ok_tripinfo); // 확인 버튼
        final ImageButton imgbtn_my_location = view.findViewById(R.id.imgbtn_my_location); // 현재 위치 가져오기
        final EditText post_address_trip_info = view.findViewById(R.id.post_address_trip_info); // 주소 가져오기
        final TextView post_broadcast_time_trip_info = view.findViewById(R.id.post_broadcast_time_trip_info); // 방송 경과 시간 가져오기
        final ImageButton renew_broadcast_time = view.findViewById(R.id.renew_broadcast_time); // 방송 시간 갱신하기
        final ImageButton food = view.findViewById(R.id.food);

        previous_marker = new ArrayList<Marker>();




        // 만약 방송 중 이라면 시간초 기록
        if(goCoderBroadcaster.getStatus().isRunning()){
            //방송 시간 갱신하기 버튼 -> 보이게끔
            renew_broadcast_time.setVisibility(View.VISIBLE);

            elepseMillis = (int)(SystemClock.elapsedRealtime() - Chronometer_stream_time.getBase()); // 방송 경과 시간을 가져옴

            int seconds = (int) (elepseMillis / 1000) % 60 ;
            int minutes = (int) ((elepseMillis / (1000*60)) % 60);
            int hours   = (int) ((elepseMillis / (1000*60*60)) % 24);



            if(hours == 0 && minutes == 0 && seconds != 0){
                post_broadcast_time_trip_info.setText(String.valueOf(seconds) + " 초");
            }else if(hours == 0 && minutes != 0){
                post_broadcast_time_trip_info.setText(String.valueOf(minutes) + " 분 " + String.valueOf(seconds) + " 초");
            }else{
                post_broadcast_time_trip_info.setText( String.valueOf(hours) +  " 시간 " + String.valueOf(minutes) + " 분 " + String.valueOf(seconds) + " 초");
            }

            // 방송 중이 아니라면
        }else{
            post_broadcast_time_trip_info.setText("방송을 시작하기 않아 측정된 시간이 없거나 \n 측정하기에 너무 짦은 시간입니다.");
        }




        // 방송 시간 새로고침 버튼 클릭
        renew_broadcast_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                elepseMillis = (int)(SystemClock.elapsedRealtime() - Chronometer_stream_time.getBase()); // 방송 경과 시간을 가져옴

                System.out.println("실행 방송경과 시간 밀리세컨드 : elepseMillis : " + elepseMillis);

                int seconds = (int) (elepseMillis / 1000) % 60 ;
                int minutes = (int) ((elepseMillis / (1000*60)) % 60);
                int hours   = (int) ((elepseMillis / (1000*60*60)) % 24);


                //택스트에 뿌려줌
                if(hours == 0 && minutes == 0 && seconds != 0){
                    post_broadcast_time_trip_info.setText(String.valueOf(seconds) + " 초");
                }else if(hours == 0 && minutes != 0){
                    post_broadcast_time_trip_info.setText(String.valueOf(minutes) + " 분 " + String.valueOf(seconds) + " 초");
                }else{
                    post_broadcast_time_trip_info.setText( String.valueOf(hours) +  " 시간 " + String.valueOf(minutes) + " 분 " + String.valueOf(seconds) + " 초");
                }



            }
        });




        //-----------------------------------------------------------------------------------------------------------------------
        //                                             <1>  맵 프래그먼트
        //-----------------------------------------------------------------------------------------------------------------------
        FragmentManager fragmentManager = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment)fragmentManager.findFragmentById(post_map_trip_info);




        //-----------------------------------------------------------------------------------------------------------------------
        //                                       <2> 자동완성(AutoComplete) 프래그먼트
        //-----------------------------------------------------------------------------------------------------------------------
        AutocompleteSupportFragment autocompleteSupportFragment = (AutocompleteSupportFragment)fragmentManager.findFragmentById(R.id.autocomplete_fragment);
        // Specify the types of place data to return.
        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.PHOTO_METADATAS, Place.Field.WEBSITE_URI, Place.Field.ADDRESS, Place.Field.LAT_LNG));




        final AlertDialog dialog = builder.create();


        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                LiveBroadcastActivity.this.finish();
            }
        });
//        setCurrentLocation_foucusCamera();

        //---------------------------------------------------------------------------------------------------------------------------------------
        //                                          Google Map api Marker 리스너
        //---------------------------------------------------------------------------------------------------------------------------------------


        /**
         * 구글 맵
         */
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                mMap = googleMap; // 맵 객체

                // 마커클릭시 이벤트
                // 텍스트 뿌려줌
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        autocompleteSupportFragment.setText(marker.getTitle());
                        post_title_trip_info.setText(marker.getTitle());
                        Logg.i("=================================================================test===================================================");
                        post_address_trip_info.setText(marker.getSnippet());
                        Logg.i("=================================================================test===================================================");
//                            post_captureimg_trip_info.

                        return false;
                    }
                });


                //이때 field를 사용해서 반환할 장소의 데이타 타입을 정의한다.
                List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.PHOTO_METADATAS, Place.Field.ADDRESS, Place.Field.ID);
                System.out.println("실행 onMapReady " + "1");

                FindCurrentPlaceRequest request = FindCurrentPlaceRequest.builder(placeFields).build();
                System.out.println("실행 onMapReady " + "2");


                if(ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    System.out.println("실행 onMapReady " + "3");

                    Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
                    System.out.println("실행 onMapReady " + "4");

                    // 여기서 부터는 조금 시간이 걸린다!
                    placeResponse.addOnCompleteListener(task -> {


                        if (task.isSuccessful()) { //요청이 성공했을 경우

                            FindCurrentPlaceResponse response = task.getResult(); // task에서 결과를 respone에 넣는다. 4단계

//                    현재 위치마커 찍음.(가능성이 가장 높은 녀석을 뽑아 그녀석의 이름!)


                            System.out.println("실행 onMapReady " + "5");
                            // Add a marker in 현재위치 and move the camera.

                            currentLocation = new LatLng(response.getPlaceLikelihoods().get(0).getPlace().getLatLng().latitude, response.getPlaceLikelihoods().get(0).getPlace().getLatLng().longitude); //위도와 경도 좌표 쌍을 나타내는 클래스. 위도와 경도의 좌표를 만들어낼때는 LatLng 라는 클래스를 이용해야 힌다.
                            System.out.println("실행 onMapReady " + "6" + "위도 :" + currentLocation.longitude + " 경도 : " + currentLocation.latitude);




                    currentMarkerOption = new MarkerOptions().position(currentLocation).title(response.getPlaceLikelihoods().get(0).getPlace().getName()).snippet(response.getPlaceLikelihoods().get(0).getPlace().getAddress());
//                    currentMarkerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));


//                   mMap.addMarker(currentMarkerOption);`
                            System.out.println("실행 onMapReady " + "7");
                            System.out.println("실행 onMapReady " + "8");

                            for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                                Log.i(TAG, String.format("Place '%s', LatLng '%s', Photo_MetaData '%s'  has likelihood: %f",
                                        placeLikelihood.getPlace().getName(),//높은 퍼센트로 위치하는 장소가 맨 처음으로 온다.
                                        placeLikelihood.getPlace().getLatLng(),
                                        placeLikelihood.getPlace().getPhotoMetadatas(),
                                        placeLikelihood.getLikelihood()));
                            }


                            /** 현재위치 위도와 경도를 받아옴 **/
                            Logg.i("============================================================TEST=============================================");

//                            MarkerOptions markerOptions = new MarkerOptions();
                            currentMarkerOption.position(currentLocation);
                            mMap.addMarker(currentMarkerOption); // 맵에 마커를 추가합니다.
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation)); // 현재위치가 있는 곳으로 카메라가 이동합니다.
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(17));


                            response.getPlaceLikelihoods().get(0).getPlace().getPhotoMetadatas();





                            //========================================================================== 현재 위치에 대한 정보를 텍스트에 뿌려준다 =================================

//                            post_title_trip_info.setText(response.getPlaceLikelihoods().get(0).getPlace().getName());
                            Logg.i("=================================================================test===================================================");
                            post_address_trip_info.setText(response.getPlaceLikelihoods().get(0).getPlace().getAddress());
                            Logg.i("=================================================================test===================================================");



//                            post_captureimg_trip_info.

                            if(jpegFile != null){ // 파일 객체가 만들어졌다면?
                                String path = jpegFile.getAbsolutePath(); //해당 파일의 절대경로는 얻어와
                                System.out.println("이미지 경로 확인 path : " + path);
                                post_captureimg_trip_info.setImageURI(Uri.parse(path)); // 이미지뷰에 넣는다.
                                return;
                            }else{
                                System.out.println("이미지 경로 확인 path : " + jpegFile.getAbsolutePath());
                            }


                            //========================================================================== 현재 위치에 대한 이미지 정보를 뿌려준다 =================================

                            if(response.getPlaceLikelihoods().get(0).getPlace().getPhotoMetadatas() == null){
                                return;
                            }


                            Logg.i("=================================================================test===================================================");

                            FetchPlaceRequest placeRequest = FetchPlaceRequest.newInstance(response.getPlaceLikelihoods().get(0).getPlace().getId() ,placeFields);
                            Logg.i("=================================================================test===================================================");

                            placesClient.fetchPlace(placeRequest).addOnSuccessListener((response1) -> {
                                Logg.i("=================================================================test===================================================");


                                Place place1 = response1.getPlace();
                                Logg.i("=================================================================test===================================================");

                                // Get the photo metadata.
                                PhotoMetadata photoMetadata = place1.getPhotoMetadatas().get(0);

                                Logg.i("=================================================================test===================================================");

                                // Get the attribution text.
                                String attributions = photoMetadata.getAttributions();
                                Logg.i("=================================================================test===================================================");

                                // Create a FetchPhotoRequest.
                                FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                                        .setMaxWidth(500) // Optional.
                                        .setMaxHeight(300) // Optional.
                                        .build();

                                Logg.i("=================================================================test===================================================");


                                placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                                    Bitmap bitmap = fetchPhotoResponse.getBitmap();
                                    post_captureimg_trip_info.setImageBitmap(bitmap);
                                }).addOnFailureListener((exception) -> {
                                    if (exception instanceof ApiException) {
                                        ApiException apiException = (ApiException) exception;
                                        int statusCode = apiException.getStatusCode();
                                        // Handle error with given status code.
                                        Log.e(TAG, "Place not found: " + exception.getMessage());
                                    }
                                });



                            });


                        } else {

                            Exception exception = task.getException();
                            if (exception instanceof ApiException) {

                                ApiException apiException = (ApiException) exception;
                                Log.e(TAG, "Place not found: " + apiException.getStatusCode());

                            }
                        }
                    });

                }else{
                    System.out.println("이게 실행되면 안되는데???");
                    REQUEST_PERMISSTION_ON_LOCATION();
                }

            }
        });



        /**
         * 자동완성 검색
         */
        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() { // 선택했을 경우
            @Override
            public void onPlaceSelected(@NonNull Place place) {




                // TODO: Get info about the selected place.
//                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                System.out.println("Place: " + place.getName() + ", " + place.getId() + ", " + place.getWebsiteUri() + " , " + place.getUserRatingsTotal() + " , " + place.getPhotoMetadatas() +" , " + place.getAddress() +" , " + place.getAddressComponents());
                Logg.i("=================================================================test===================================================");
                String placeId = place.getId();
                Logg.i("=================================================================test===================================================");

                // Specify fields. Requests for photos must always have the PHOTO_METADATAS field.
                List<Place.Field> fields = Arrays.asList(Place.Field.PHOTO_METADATAS, Place.Field.ADDRESS);
                Logg.i("=================================================================test===================================================");

                // Get a Place object (this example uses fetchPlace(), but you can also use findCurrentPlace())
                FetchPlaceRequest placeRequest1 = FetchPlaceRequest.newInstance(placeId, fields);
                Logg.i("=================================================================test===================================================");




                // 검색한 장소로 마커를 찍고 해당 장소로 카메라를 이동합니다.
                searchLocation = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                searchMarkerOption = new MarkerOptions();

                searchMarkerOption.position(searchLocation); //위치 추가
                searchMarkerOption.title(place.getName()); // 장소 이름
                searchMarkerOption.snippet(place.getAddress()); // 장소 주소


                BitmapDrawable bitmapDrawable = (BitmapDrawable)getResources().getDrawable(R.drawable.placeholder);
                Bitmap bitmap = bitmapDrawable.getBitmap();
                Bitmap searchIconBitmap = Bitmap.createScaledBitmap(bitmap, 150, 150 , false);
                searchMarkerOption.icon(BitmapDescriptorFactory.fromBitmap(searchIconBitmap)); // 서치 아이콘 마커




                mMap.addMarker(searchMarkerOption); // 맵에 마커를 추가합니다.
                mMap.moveCamera(CameraUpdateFactory.newLatLng(searchLocation)); // 현재위치가 있는 곳으로 카메라가 이동합니다.
                mMap.animateCamera(CameraUpdateFactory.zoomTo(17));






                //================================================================= 검색 결과 정보 텍스트에 뿌림 ======================================================

                post_title_trip_info.setText(place.getName());
                post_address_trip_info.setText(place.getAddress());


                Logg.i("=================================================================test===================================================");




                // 사진데이터가 없는 경우
                if(place.getPhotoMetadatas() == null){
                    Glide.with(getApplicationContext()).load(R.drawable.noimg).into(post_captureimg_trip_info);
                    //바로 종료 시킨다.
                    return;
                }



                placesClient.fetchPlace(placeRequest1).addOnSuccessListener((response) -> {
                    Logg.i("=================================================================test===================================================");

                    Place place1 = response.getPlace();
                    Logg.i("=================================================================test===================================================");


                    // Get the photo metadata.
                    PhotoMetadata photoMetadata = place1.getPhotoMetadatas().get(0);

                    Logg.i("=================================================================test===================================================");

                    // Get the attribution text.
                    String attributions = photoMetadata.getAttributions();
                    Logg.i("=================================================================test===================================================");

                    // Create a FetchPhotoRequest.
                    FetchPhotoRequest photoRequest1 = FetchPhotoRequest.builder(photoMetadata)
                            .setMaxWidth(500) // Optional.
                            .setMaxHeight(300) // Optional.
                            .build();

                    Logg.i("=================================================================test===================================================");


                    placesClient.fetchPhoto(photoRequest1).addOnSuccessListener((fetchPhotoResponse) -> {
                        Bitmap bitmap1 = fetchPhotoResponse.getBitmap();
                        post_captureimg_trip_info.setImageBitmap(bitmap1);
                    }).addOnFailureListener((exception) -> {
                        if (exception instanceof ApiException) {
                            ApiException apiException = (ApiException) exception;
                            int statusCode = apiException.getStatusCode();
                            // Handle error with given status code.
                            Log.e(TAG, "Place not found: " + exception.getMessage());
                        }
                    });



                });


            }

            @Override
            public void onError(@NonNull Status status) {

                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });








        /**
         * 현재 위치 다시 찍기
         */
        imgbtn_my_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setCurrentLocation_foucusCamera();

            }
        });

        /**
         * 확인 버튼
         *
         */
        post_ok_tripinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("실행 String.valueOf(elepseMillis) : " + elepseMillis);


                POST_TRIP_INFO("add.php", post_title_trip_info.getText().toString(), post_tag_trip_info.getText().toString(), post_address_trip_info.getText().toString(), elepseMillis);
            }
        });

        /**
         * 취소버튼
         */
        post_cancle_tripinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fragmentManager.beginTransaction().remove(mapFragment).remove(autocompleteSupportFragment).commit();
                dialog.dismiss();
            }
        });



        // 음식 버튼
        food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPlaceInformation_foodstore();
            }
        });






        dialog.show();
    }


    //================================================================================생명주기============================================================

    @Override
    protected void onStop() {
        super.onStop();

        System.out.println("실행 onStop goCoderBroadcaster.getStatusCallback() : " + goCoderBroadcaster.getStatusCallback());
        System.out.println("실행 onStop wowzCamera.isAvailable() : " + wowzCamera.isAvailable());
        System.out.println("실행 onStop goCoderCameraView.isVideoPaused() : " + goCoderCameraView.isVideoPaused());


        if (mFusedLocationClient != null) {

            Log.d(TAG, "onStop : call stopLocationUpdates");
            Logg.i("=====================================test===================================================");
            mFusedLocationClient.removeLocationUpdates(locationCallback);
            Logg.i("=====================================test===================================================");
        }


    }

    @Override
    protected void onStart() {
        super.onStart();

        System.out.println("실행 onStart goCoderBroadcaster.getStatusCallback() : A" + goCoderBroadcaster.getStatusCallback());
        System.out.println("실행 onStart wowzCamera.isAvailable() : " + wowzCamera.isAvailable());
        System.out.println("실행 onStart goCoderCameraView.isVideoPaused() : " + goCoderCameraView.isVideoPaused());
    }

    @Override
    protected void onRestart() {
        super.onRestart();



        System.out.println("실행 restart goCoderBroadcaster.getStatusCallback() : " + goCoderBroadcaster.getStatusCallback());
        System.out.println("실행 restart wowzCamera.isAvailable() : " + wowzCamera.isAvailable());
        System.out.println("실행 restart goCoderCameraView.isVideoPaused() : " + goCoderCameraView.isVideoPaused());
        goCoderBroadcaster.endBroadcast();
        goCoderCameraView.clearView();
        goCoderBroadcaster.startBroadcast(goCoderBroadcastConfig, goCoderBroadcaster.getStatusCallback());
        goCoderCameraView.getCamera().continuePreview();



        // 문제의 이유는 이미 goCoderBroadcaster 가 실행중인데 왜 또 실행하고 있니? 같은 에러
    }

    /**
     * onResume() 모든 화면이 전면에 등장하였을 때 앱의 권한을 체크합니다
     *
     */
    @Override
    protected void onResume() {
        super.onResume();

        // If running on Android 6 (Marshmallow) and later, check to see if the necessary permissions
        // have been granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mPermissionsGranted = hasPermissions(this, mRequiredPermissions);
            if (!mPermissionsGranted)
                ActivityCompat.requestPermissions(this, mRequiredPermissions, PERMISSIONS_REQUEST_CODE);
        } else
            mPermissionsGranted = true;




        if (goCoderCameraView != null) {
            goCoderCameraView.onResume();

            if(mPermissionsGranted && !goCoderCameraView.isPreviewing()) {
                Logg.i("=======================test======================");

                // start the camera preview display and enable the screenshot button when it is ready
                final Handler handler = new Handler();
                Logg.i("=======================test======================");
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        // start the camera preview display and enable the screenshot button when it is ready
                        goCoderCameraView.startPreview(new WOWZCameraView.PreviewStatusListener() {
                            @Override
                            public void onWZCameraPreviewStarted(WOWZCamera camera, WOWZSize frameSize, int frameRate) {
                                Logg.i("=======================test======================");
                                btn_add_trip_info.setEnabled(true);
                                Logg.i("=======================test======================");
                                btn_add_trip_info.setClickable(true);
                                Logg.i("=======================test======================");
                            }

                            @Override
                            public void onWZCameraPreviewStopped(int cameraId) {
                                btn_add_trip_info.setEnabled(false);
                                Logg.i("=======================test======================");
                                btn_add_trip_info.setClickable(false);
                                Logg.i("=======================test======================");
                            }

                            @Override
                            public void onWZCameraPreviewError(WOWZCamera camera, WOWZError error) {
                                btn_add_trip_info.setEnabled(false);
                                Logg.i("=======================test======================");
                                btn_add_trip_info.setClickable(false);
                                Logg.i("=======================test======================");
                            }
                        });
                    }
                }, 300);
            }
        }
    }

    @Override
    protected void onPause() {
        if (goCoderCameraView != null)
            Logg.i("=======================test======================");
        goCoderCameraView.onPause();
        Logg.i("=======================test======================");

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        POST_LIVE_STREAM("delete.php");
        goCoderBroadcaster.endBroadcast();
        goCoderCameraView.clearView();
        btn_stop_broadcast.performClick();
        flag = false;
    }



    //============================================================================방송 중 뒤로가기===========================================================

    @Override
    public void onBackPressed() {
        // 방송 중이 아닐경우
        if(!(goCoderBroadcaster.getStatus().isRunning())){
//            onBackPressed(); //바로 홈 액티비티로 이동한다.
            finish();

            // 방송 중일 경우
        }else if(goCoderBroadcaster.getStatus().isRunning()){
            alert_broadcast_end(); // 경고 알림창을 띄운다.
        }
    }

    //======================================================================================================================================================================
    //                                                                                구글 맵 커스텀 함수
    //======================================================================================================================================================================

    /**
     *  현재 위치의 주소, 위도, 경도를 반환하는 메소드
     */
    public void setCurrentLocation_foucusCamera(){

        //이때 field를 사용해서 반환할 장소의 데이타 타입을 정의한다.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.PHOTO_METADATAS, Place.Field.ADDRESS);
        System.out.println("실행 onMapReady " + "1");

        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.builder(placeFields).build();
        System.out.println("실행 onMapReady " + "2");


         if(ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            System.out.println("실행 onMapReady " + "3");

            Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
            System.out.println("실행 onMapReady " + "4");


            // 여기서 부터는 조금 시간이 걸린다!
            placeResponse.addOnCompleteListener(task -> {


                if (task.isSuccessful()) { //요청이 성공했을 경우

                    FindCurrentPlaceResponse response = task.getResult(); // task에서 결과를 respone에 넣는다. 4단계

//                    현재 위치마커 찍음.(가능성이 가장 높은 녀석을 뽑아 그녀석의 이름!)


                    System.out.println("실행 onMapReady " + "5");
                    // Add a marker in 현재위치 and move the camera.

                    currentLocation = new LatLng(response.getPlaceLikelihoods().get(0).getPlace().getLatLng().latitude, response.getPlaceLikelihoods().get(0).getPlace().getLatLng().longitude); //위도와 경도 좌표 쌍을 나타내는 클래스. 위도와 경도의 좌표를 만들어낼때는 LatLng 라는 클래스를 이용해야 힌다.
                    System.out.println("실행 onMapReady " + "6" + "위도 :" + currentLocation.longitude + " 경도 : " + currentLocation.latitude);




//                    currentMarkerOption = new MarkerOptions().position(currentLocation).title(response.getPlaceLikelihoods().get(0).getPlace().getName()).snippet(response.getPlaceLikelihoods().get(0).getPlace().getAddress());
//                    currentMarkerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));


//                   mMap.addMarker(currentMarkerOption);`
                    System.out.println("실행 onMapReady " + "7");
                    System.out.println("실행 onMapReady " + "8");

                    for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                        Log.i(TAG, String.format("Place '%s', LatLng '%s', Photo_MetaData '%s'  has likelihood: %f",
                                placeLikelihood.getPlace().getName(),//높은 퍼센트로 위치하는 장소가 맨 처음으로 온다.
                                placeLikelihood.getPlace().getLatLng(),
                                placeLikelihood.getPlace().getPhotoMetadatas(),
                                placeLikelihood.getLikelihood()));
                    }

                    currentMarkerOption.position(currentLocation);

                    mMap.clear();
                    mMap.addMarker(currentMarkerOption);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(17));




                } else {
                    Exception exception = task.getException();
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                    }
                }
            });


        }else{
            System.out.println("이게 실행되면 안되는데???");
            REQUEST_PERMISSTION_ON_LOCATION();

        }

    }

    public void showPlaceInformation_foodstore(){

        mMap.clear();

        // 현재 위치는 찍어주자
        setCurrentLocation_onlyMarker();

        new NRPlaces.Builder()
                .listener(LiveBroadcastActivity.this)
                .key("AIzaSyAABuNkYxPajgwBw8hF4_VfH1l9uoK2ols")
                .latlng(mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude)
                .radius(500)
                .type(PlaceType.RESTAURANT)
                .build()
                .execute();
    }


    public void getCurrentLocation_android(){


        if(mLocationManager != null){

            Boolean isGPSEnable = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            Boolean isNetworkEnable = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            Log.e(TAG, "isGPSEnable : "  + isGPSEnable);
            Log.e(TAG, "isNetworkEnable : " + isNetworkEnable);



            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    double lat = location.getLatitude();
                    double lng = location.getLongitude();

                    realTimeLocation = new LatLng(lat, lng);
//                    Toast.makeText(getApplicationContext(), "위도 : " + lat + " 경도 : " + lng, Toast.LENGTH_SHORT).show();

                    return;
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    Log.e(TAG, "위치 onStatusChanged : " + status);

                }

                @Override
                public void onProviderEnabled(String provider) {
                    Log.e(TAG, "위치 onProviderEnabled : " + provider);
                }

                @Override
                public void onProviderDisabled(String provider) {
                    Log.e(TAG, "위치 onProviderDisabled : ");
                }
            };



            //아래 코드를 실행시키기 위해서 임의적으로 한번 더 권한 체크를 하여야함. 그렇지 않으면 error
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }

            if(isGPSEnable && isNetworkEnable){
//                //시스템 위치마저 허용되어있을경우
                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

                }else{

            }
        }
    }




    public LatLng getCurrentLocation(){ //? 별로 정확하지 않음

        //이때 field를 사용해서 반환할 장소의 데이타 타입을 정의한다.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG);
        System.out.println("실행 onMapReady " + "1");
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.builder(placeFields).build();
        System.out.println("실행 onMapReady " + "2");


        if(ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            System.out.println("실행 onMapReady " + "3");

            Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
            System.out.println("실행 onMapReady " + "4");


            // 여기서 부터는 조금 시간이 걸린다!
            placeResponse.addOnCompleteListener(task -> {

                if (task.isSuccessful()) { //요청이 성공했을 경우
                    FindCurrentPlaceResponse response = task.getResult(); // task에서 결과를 respone에 넣는다. 4단계
                    System.out.println("실행 onMapReady " + "5");
                    realTimeLocation = new LatLng(response.getPlaceLikelihoods().get(0).getPlace().getLatLng().latitude, response.getPlaceLikelihoods().get(0).getPlace().getLatLng().longitude); //위도와 경도 좌표 쌍을 나타내는 클래스. 위도와 경도의 좌표를 만들어낼때는 LatLng 라는 클래스를 이용해야 힌다.
                    System.out.println("실행 onMapReady " + "6" + "위도 :" + realTimeLocation.longitude + " 경도 : " + realTimeLocation.latitude);
                    System.out.println("실행 onMapReady " + "7");
                    System.out.println("실행 onMapReady " + "8");
                    return;


                } else {
                    Exception exception = task.getException();
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                    }
                }

            });

        }else{
            System.out.println("이게 실행되면 안되는데???");
            REQUEST_PERMISSTION_ON_LOCATION();
        }

        return realTimeLocation; // 실시간 위치를 반환
    }


    // ================================================================================= 권한 요청 =================================================================================
    // 1.( 위치 권한 요청 )
    public void REQUEST_PERMISSTION_ON_LOCATION(){
        if (ContextCompat.checkSelfPermission(LiveBroadcastActivity.this,
                ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(LiveBroadcastActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {

                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(LiveBroadcastActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
    }

    // 2. (위치 권한 요청 응답 ) => 해결 못함 .. overRide에 PermissionResult 메소드가 나오지 않음 ...



    //---------------------------------------------------------------------------------------------------------------------------------------
    //                                                                        네트워크 통신
    //---------------------------------------------------------------------------------------------------------------------------------------

    // POST_LIVE_STREAM
    /**
     * @param endpoint  => add.php / delete.php/ update.php
     */
    public void POST_LIVE_STREAM(String endpoint){

        Map<String,String> addLiveStreamParameters = new HashMap<>();
        addLiveStreamParameters.put("id", id);
        addLiveStreamParameters.put("title", title);
        addLiveStreamParameters.put("tag", tag);
        addLiveStreamParameters.put("route_stream", routeStream);

        Log.i(TAG, "POST_LIVE_STREAM / id : " + id);
        Log.i(TAG, "POST_LIVE_STREAM / title : " + title);
        Log.i(TAG, "POST_LIVE_STREAM / tag : " + tag);
        Log.i(TAG, "POST_LIVE_STREAM / route_stream : " + routeStream);





        Call<PostResult> postResultCall = requestApi.POST_LIVE_STREAM_CALL(addLiveStreamParameters, endpoint);

        postResultCall.enqueue(new Callback<PostResult>() {
            @Override
            public void onResponse(Call<PostResult> call, Response<PostResult> response) {

                if(!response.isSuccessful()){
                    Log.i(TAG, "onResponse " + response.message());
                    return;
                }

                PostResult postResult = response.body();
                if(postResult.getResult().equals("success")){ //성공적으로 방송 POST_ADD 성공
//                    Toast.makeText(getApplicationContext(), "방송정보저장", Toast.LENGTH_SHORT).show();
                }else{ // 방송 POST_ADD 실패
//                    Toast.makeText(getApplicationContext(), "방송정보저장실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PostResult> call, Throwable t) {

            }
        });
    }

    //POST_VOD_ADD ( 방송이 종료될 떄 바로 해당 영상을 VOD로 올릴 수 있도록 한다)
    public void POST_VOD_STREAM(String endpoint){

    }

    // 스트리머의 위치 서버로 전송
    /**
     * @param endpoint  -> add_real_time_location.php
     */
    public void POST_REALTIME_LOCATION(String endpoint){

        if(currentPosition == null){
            System.out.println("실행 realTimeLocation : ");
            return;
        }




        HashMap<String, String> realTimeStringHashMap = new HashMap<>();
        realTimeStringHashMap.put("id", id);
        realTimeStringHashMap.put("lat", String.valueOf(currentPosition.latitude));
        realTimeStringHashMap.put("lon", String.valueOf(currentPosition.longitude));
        realTimeStringHashMap.put("route_stream", routeStream);
        realTimeStringHashMap.put("time", String.valueOf((int)(SystemClock.elapsedRealtime() - Chronometer_stream_time.getBase())));


        Call<PostResult> postResultCall = requestApi.POST_REAL_LOCATION_RESULT_CALL(endpoint, realTimeStringHashMap);
        postResultCall.enqueue(new Callback<PostResult>() {
            @Override
            public void onResponse(Call<PostResult> call, Response<PostResult> response) {
                if(!response.isSuccessful()){
                    Log.d(TAG, "onResponse: " + response.message());
                    return;
                }


                PostResult postResult = response.body();
                if(postResult.getResult().equals("success")){
                        Toast.makeText(getApplicationContext(), "위치 정보 저장",  Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "위치 정보 저장 실패",  Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PostResult> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });


    }


    // 여행 정보 POST
    public void POST_TRIP_INFO(String endpoint, String trip_title, String trip_tag, String trip_address, int trip_time){
        String post_trip_info = "post_trip_info";

        HashMap<String, RequestBody> requestBodyHashMap = new HashMap<>(); // 서버로 보낼 파라미터들
        Log.d(post_trip_info, "POST_TRIP_INFO: " + " 실행 1");
        requestBodyHashMap.put("id",RequestBody.create(MediaType.parse("text/plain"), id));
        Log.d(post_trip_info, "POST_TRIP_INFO: " + " 실행 2");
        requestBodyHashMap.put("lat",RequestBody.create(MediaType.parse("text/plain"), String.valueOf(currentLocation.latitude)));
        Log.d(post_trip_info, "POST_TRIP_INFO: " + " 실행 3");
        requestBodyHashMap.put("lon",RequestBody.create(MediaType.parse("text/plain"), String.valueOf(currentLocation.longitude)));
        Log.d(post_trip_info, "POST_TRIP_INFO: " + " 실행 4");
        requestBodyHashMap.put("tag",RequestBody.create(MediaType.parse("text/plain"), trip_tag));
        Log.d(post_trip_info, "POST_TRIP_INFO: " + " 실행 5");
        requestBodyHashMap.put("routestream",RequestBody.create(MediaType.parse("text/plain"), routeStream));
        Log.d(post_trip_info, "POST_TRIP_INFO: " + " 실행 6");
        requestBodyHashMap.put("title",RequestBody.create(MediaType.parse("text/plain"), trip_title));
        Log.d(post_trip_info, "POST_TRIP_INFO: " + " 실행 7");
        requestBodyHashMap.put("address",RequestBody.create(MediaType.parse("text/plain"), trip_address));
        Log.d(post_trip_info, "POST_TRIP_INFO: " + " 실행 8");
        requestBodyHashMap.put("address",RequestBody.create(MediaType.parse("text/plain"), trip_address));
        Log.d(post_trip_info, "POST_TRIP_INFO: " + " 실행 9");
        requestBodyHashMap.put("time",RequestBody.create(MediaType.parse("text/plain"), String.valueOf(trip_time)));




        RequestBody fileRequestBody = RequestBody.create(MediaType.parse("image/*"),jpegFile); // 서버로 보낼 이미지
        MultipartBody.Part filepart = MultipartBody.Part.createFormData("uploaded_file", jpegFile.getName(), fileRequestBody);


        Call<PostResult> postResultCall = requestApi.POST_TRIPINFO_RESULT_CALL(endpoint, filepart, requestBodyHashMap);

        postResultCall.enqueue(new Callback<PostResult>() {
            @Override
            public void onResponse(Call<PostResult> call, Response<PostResult> response) {
                if(!response.isSuccessful()){
                    Log.d("post_trip_info", "onResponse: message (is not successful)-> " + response.message());
                    return;
                }

                Log.d("post_trip_info", "onResponse: code -> " + response.code());
                PostResult postResult = response.body();
                if(postResult.getResult().equals("success")){
                    Toast.makeText(getApplicationContext(),"여행 정보가 등록되었습니다", Toast.LENGTH_SHORT).show();
                    Log.d("post_trip_info", "onResponse: message 성공-> " + response.message());
                }else{
                    Toast.makeText(getApplicationContext(),"여행 정보 등록 실패",Toast.LENGTH_SHORT).show();
                    Log.d("post_trip_info", "onResponse: message 실패 -> " + response.message());
                }
            }

            @Override
            public void onFailure(Call<PostResult> call, Throwable t) {
                Log.d("post_trip_info", "onFailure: " + t.getMessage() + " " + t.getCause());
            }
        });




    }



    //---------------------------------------------------------------------------------------------------------------------------------------
    //                                          Google place api 오버라이드
    //---------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void onPlacesFailure(PlacesException e) {
        System.out.println("실행 : " + e.getMessage());
    }

    @Override
    public void onPlacesStart() {

    }
    int i = 1;

    @Override
    public void onPlacesSuccess(final List<noman.googleplaces.Place> places) {


        runOnUiThread(new Runnable() {


            @Override

            public void run() {
//                // Position the map.
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude), 17));
//
//                // Initialize the manager with the context and the map.
//                // (Activity extends context, so we can pass 'this' in the constructor.)
//                mClusterManager = new ClusterManager<GoogleMarkerItem>(getApplicationContext(), mMap);
//
//                // Point the map's listeners at the listeners implemented by the cluster
//                // manager.
//                mMap.setOnCameraIdleListener(mClusterManager);
//                mMap.setOnMarkerClickListener(mClusterManager);

                // Add cluster items (markers) to the cluster manager.



                for (noman.googleplaces.Place place : places) {


                    LatLng latLng //검색해서 들어온 place 의 위도 경도

                            = new LatLng(place.getLatitude()

                            , place.getLongitude());

                    Logg.i("===========================TEST===================" + i + "번 째 " +  " latLng : " + latLng.latitude + " / " + latLng.longitude);


                    String markerSnippet = getCurrentAddress(latLng);  // getCurrentAddress 을 이용해 위도 경도 -> 주소를 얻어낸다.

                    Logg.i("===========================TEST===================" + i + "번 쨰 markerSnippet" + markerSnippet);

                    MarkerOptions markerOptions = new MarkerOptions(); // 새롭게 만들 마커 옵션

                    markerOptions.position(latLng); // 마커 옵션에 : 포지션 세팅

                    markerOptions.title(place.getName()); // 마크옵션 : 타이틀

                    markerOptions.snippet(markerSnippet); // 마크옵션 : 주소정보


                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.outline_place_black_36)); // 마크옵션 : 아이콘


                    mMap.addMarker(markerOptions);

                    // Set some lat/lng coordinates to start with.

                    // Add ten cluster items in close proximity, for purposes of this example.

//                        GoogleMarkerItem offsetItem = new GoogleMarkerItem(place.getLatitude(), place.getLongitude(), place.getName(), markerSnippet);
//                        mClusterManager.addItem(offsetItem);

                    i++;


                }


//
//                //중복 마커 제거
//
//                HashSet<Marker> hashSet = new HashSet<Marker>();
//
//                hashSet.addAll(previous_marker);
//
//                previous_marker.clear();
//
//                previous_marker.addAll(hashSet);


            }

        });
    }

    @Override
    public void onPlacesFinished() {

    }

    //----------------------------------------------------------------------------------------------------------------------------------------
    //                                                       클러스터 메소드
    //----------------------------------------------------------------------------------------------------------------------------------------
    // Declare a variable for the cluster manager.
    private ClusterManager<GoogleMarkerItem> mClusterManager;

    private void setUpClusterer() {


        // Position the map.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude), 10));

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<GoogleMarkerItem>(this, mMap);

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        // Add cluster items (markers) to the cluster manager.
        addItems();
    }

    private void addItems() {


        /**
         * 이곳으로 위도와 경도를 주면됨
         */

        // Set some lat/lng coordinates to start with.
        double lat = mMap.getCameraPosition().target.latitude;
        double lng = mMap.getCameraPosition().target.longitude;

        // Add ten cluster items in close proximity, for purposes of this example.
        for (int i = 0; i < 10; i++) {
            double offset = i / 60d;
            lat = lat + offset;
            lng = lng + offset;
            GoogleMarkerItem offsetItem = new GoogleMarkerItem(lat, lng);
            mClusterManager.addItem(offsetItem);
        }





    }






    //---------------------------------------------------------------------------------------------------------------------------------------
    //                                              현재 위치로 가기(그냥 마커만)
    //---------------------------------------------------------------------------------------------------------------------------------------

    public void setCurrentLocation_onlyMarker() {
        //이때 field를 사용해서 반환할 장소의 데이타 타입을 정의한다.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.PHOTO_METADATAS, Place.Field.ADDRESS);

        //2. FindCurrentPlaceRequest를 만든다. 이것은 Place.Fields들의 리스트를 넘겨서, 그 앱이 요청하는 장소의 데이타 타입을 구체화 한다.
        //요청받을 떄 그냥 해? 아니지 이 경우네는 field 형태로 한다~ 구글이 그렇게 만듬.
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.builder(placeFields).build();

        // Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {


            Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);//요청을 만들고 요청하는 장소의 데이타타입을 구체화 해서 만들 것을 findCurrentPlace의 매게변수로 넣음


            placeResponse.addOnCompleteListener(task -> {
                if (task.isSuccessful()) { //요청이 성공했을 경우
                    FindCurrentPlaceResponse response = task.getResult(); // task에서 결과를 respone에 넣는다. 4단계


                    //현재 위치마커 찍음.(가능성이 가장 높은 녀석을 뽑아 그녀석의 이름!)

                    System.out.println("onMapReady " + "1");
                    // Add a marker in 현재위치 and move the camera.
                    currentLocation = new LatLng(response.getPlaceLikelihoods().get(0).getPlace().getLatLng().latitude, response.getPlaceLikelihoods().get(0).getPlace().getLatLng().longitude); //위도와 경도 좌표 쌍을 나타내는 클래스. 위도와 경도의 좌표를 만들어낼때는 LatLng 라는 클래스를 이용해야 힌다.
                    System.out.println("onMapReady " + "2");
                    currentMarkerOption = new MarkerOptions().position(currentLocation).title(response.getPlaceLikelihoods().get(0).getPlace().getName()).snippet(response.getPlaceLikelihoods().get(0).getPlace().getAddress());

                    mMap.addMarker(currentMarkerOption);
                    System.out.println("onMapReady " + "3");
                    System.out.println("onMapReady " + "4");


                    for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                        Log.i(TAG, String.format("Place '%s', LatLng '%s', Photo_MetaData '%s'  has likelihood: %f",
                                placeLikelihood.getPlace().getName(),//높은 퍼센트로 위치하는 장소가 맨 처음으로 온다.
                                placeLikelihood.getPlace().getLatLng(),
                                placeLikelihood.getPlace().getPhotoMetadatas(),
                                placeLikelihood.getLikelihood()));
                    }


                } else {
                    Exception exception = task.getException();
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                    }
                }
            });
        } else {
            // A local method to request required permissions;
            // See https://developer.android.com/training/permissions/requesting

        }
    }



    //---------------------------------------------------------------------------------------------------------------------------------------
    //                                          현재 위치의 주소 알아내는 메소드
    //---------------------------------------------------------------------------------------------------------------------------------------
    public String getCurrentAddress(LatLng latlng) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);


        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }




    //---------------------------------------------------------------------------------------------------------------------------------------
    //                                          GoCoder 에서 찍은 스크린샷 사진을 담을 파일객체 얻기 메소드
    //---------------------------------------------------------------------------------------------------------------------------------------

    /**
     * Returns a File object to use for saving an image
     */
    private File getOutputJpegFile() {

        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        //

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "GoCoderSDK Screenshots");

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                WOWZLog.error(TAG, "Failed to create the directory in which to store the screenshot");
                Logg.i("=======================test======================");
                return null;
            }
        }

        // Create a media file name
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HHmmss").format(new Date());
        Logg.i("=======================test======================");

        // 새로운 파일 객체를 반환 . 경로이름을 정함!!
        return new File(mediaStorageDir.getPath() + File.separator + timeStamp + ".jpg");
    }

    //---------------------------------------------------------------------------------------------------------------------------------------
    //                                          GoCoder 스크린 샷 찍기 !
    //---------------------------------------------------------------------------------------------------------------------------------------

    public void onTakeScreenshot() {


        // Setting mGrabFrame to true will trigger the video frame listener to become active
        if (!mGrabFrame.get() && !mSavingFrame.get()) {
            Logg.i("=======================test======================");
            btn_add_trip_info.setEnabled(false);
            Logg.i("=======================test======================");
            btn_add_trip_info.setClickable(false);
            Logg.i("=======================test======================");
            mGrabFrame.set(true);
            Logg.i("=======================test======================");
        }


    }


    //---------------------------------------------------------------------------------------------------------------------------------------
    //                                                          uri 로 부터 경로 얻어내는 메소드
    //---------------------------------------------------------------------------------------------------------------------------------------

    public String getPathFromUri(Uri uri){

        Cursor cursor = getContentResolver().query(uri, null, null, null, null );

        System.out.println("실행1 to Filepath : " + cursor);
        System.out.println("실행2 to Filepath : " + cursor.toString());
        cursor.moveToNext(); //??  커서의 위치를 가리키는데 사용> cursor 를 다음 행으로 이동 시킨다. ??
        String path = cursor.getString( cursor.getColumnIndex( "_data" ) );
        System.out.println("실행3 to Filepath : " + path);
        cursor.close();
        System.out.println("실행4 close : ");

        return path;

    }


    //---------------------------------------------------------------------------------------------------------------------------------------
    //                                                (스레드)스트리머의 위치를 받아 계속해서 서버로 전송
    //---------------------------------------------------------------------------------------------------------------------------------------


    class RealTimeLocation extends Thread{


        @Override
        public void run() {

            while(flag){
                try {
                    POST_REALTIME_LOCATION("add_real_time_location.php");
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }


    //---------------------------------------------------------------------------------------------------------------------------------------
    //                                                           현재 위치를 계속  ?
    //--------------------------------------------------------------------------------------------------------------------------------------

    LocationCallback locationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            Logg.i("=====================================test===================================================");
            List<Location> locationList = locationResult.getLocations();
            Logg.i("=====================================test===================================================");
            if (locationList.size() > 0) {
                Logg.i("=====================================test===================================================");
                location = locationList.get(locationList.size() - 1);
                //location = locationList.get(0);
                Logg.i("=====================================test===================================================");

                currentPosition = new LatLng(location.getLatitude(), location.getLongitude());

                Logg.i("=====================================test===================================================");
                String markerTitle = getCurrentAddress(currentPosition);
                Logg.i("=====================================test===================================================");
                String markerSnippet = "위도:" + String.valueOf(location.getLatitude())
                        + " 경도:" + String.valueOf(location.getLongitude());
                Logg.i("=====================================test===================================================");

                Log.d(TAG, "onLocationResult : " + markerSnippet);
                Logg.i("=====================================test===================================================");


//                //현재 위치에 마커 생성하고 이동
//                setCurrentLocation(location, markerTitle, markerSnippet);
                Logg.i("=====================================test===================================================");

                mCurrentLocatiion = location;
                Logg.i("=====================================test===================================================");
            }

        }

    };

    private void startLocationUpdates() {

        if (!checkLocationServicesStatus()) {

            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            Logg.i("=====================================test===================================================");
            showDialogForLocationServiceSetting();
            Logg.i("=====================================test===================================================");
        }else {

            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            Logg.i("=====================================test===================================================");
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);
            Logg.i("=====================================test===================================================");



            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                    hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED   ) {

                Log.d(TAG, "startLocationUpdates : 퍼미션 안가지고 있음");
                Logg.i("=====================================test===================================================");
                return;
            }


            Log.d(TAG, "startLocationUpdates : call mFusedLocationClient.requestLocationUpdates");
            Logg.i("=====================================test===================================================");

            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            Logg.i("=====================================test===================================================");
        }

    }


    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    private boolean checkPermission() {

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);



        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {
            return true;
        }

        return false;

    }

    public boolean checkLocationServicesStatus() {
        Logg.i("=====================================test===================================================");
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Logg.i("=====================================test===================================================");

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {
        Logg.i("=====================================test===================================================");

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(LiveBroadcastActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }



}
