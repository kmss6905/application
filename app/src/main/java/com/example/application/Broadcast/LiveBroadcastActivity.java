package com.example.application.Broadcast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.application.IPclass;
import com.example.application.ItemData.ItemLiveData;
import com.example.application.Logg;
import com.example.application.R;
import com.example.application.Retrofit2.Repo.AddLiveStream;
import com.example.application.Retrofit2.Repo.LivestreamInfo;
import com.example.application.Retrofit2.Repo.PostResult;
import com.example.application.Retrofit2.RequestApi;
import com.example.application.UuidTest;
import com.wowza.gocoder.sdk.api.WowzaGoCoder;
import com.wowza.gocoder.sdk.api.broadcast.WOWZBroadcast;
import com.wowza.gocoder.sdk.api.broadcast.WOWZBroadcastConfig;
import com.wowza.gocoder.sdk.api.configuration.WOWZMediaConfig;
import com.wowza.gocoder.sdk.api.devices.WOWZAudioDevice;
import com.wowza.gocoder.sdk.api.devices.WOWZCamera;
import com.wowza.gocoder.sdk.api.devices.WOWZCameraView;
import com.wowza.gocoder.sdk.api.errors.WOWZError;
import com.wowza.gocoder.sdk.api.errors.WOWZStreamingError;
import com.wowza.gocoder.sdk.api.status.WOWZState;
import com.wowza.gocoder.sdk.api.status.WOWZStatus;
import com.wowza.gocoder.sdk.api.status.WOWZStatusCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.LoginException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LiveBroadcastActivity extends AppCompatActivity implements WOWZStatusCallback {
    private static final String TAG = "LiveBroadcastActivity";

    //SharedPerferences 에서 ID 를 가져옵니다.
    SharedPreferences sharedPreferences;

    // 닉네임 가져옴
    Intent intent = getIntent();


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
            Manifest.permission.RECORD_AUDIO
    };




    // 시간 측정용
    private Chronometer Chronometer_stream_time;
    private  boolean running;
    private long pauseOffset;


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









    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_broadcast);
        sharedPreferences = getSharedPreferences("file", MODE_PRIVATE);

        // 방송 타이틀, 태그, 제목, id
        title = intent.getStringExtra("nickname") + " 님이 현재 방송 중입니다"; // 타이틀
        tag = "현재 태그 없음"; // 태그
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



//        title = sharedPreferences.getString("id", "") + " 님의 여행방송입니다";








        /**
         *  wowza goCoder sdk를 등록하고 초기화 합니다.
         */

        goCoder = WowzaGoCoder.init(getApplicationContext(),"GOSK-B746-010C-E227-A534-0CE7");



        if (goCoder == null) {
            // 만약에 객체가 형성되지 않았다? 즉, 제대로 등록 밑 초기화 되지 않았기 때문에 에러를 표시한다
            WOWZError wowzError = WowzaGoCoder.getLastError();
            Toast.makeText(this, "goCoder 초기화에서 문제 에러내용 입니다 : " + wowzError.getErrorDescription(), Toast.LENGTH_SHORT).show();
            return;
        }

        /**
         *
         *  카메라 프리뷰를 시작합니다.,
         *
         */

        // 우선 영상이 보인 wowzaCameraView를 참조합니다.
        // 해당 카메라 view에 영상을 띄웁니다.(당연히 권한이 승인 되었을 경우에 시작합니다.)
        goCoderCameraView = findViewById(R.id.camera_preview);

        if (mPermissionsGranted && goCoderCameraView != null) { // 퍼미션에 대한 승인과 카메라뷰가 정상적으로 참조 되었을 경우
            if (goCoderCameraView.isPreviewPaused()) { // 프리뷰가 멈춘 상태라면
                goCoderCameraView.onResume(); // resume으로 돌리고
            } else { // 프리뷰가 pause 상태가 아니라면 프리뷰를 처음부터 시작합니다.
                goCoderCameraView.startPreview();
            }
        }


        // 방송을 추가하고 구성합니다
        // 오디오 디바이스 추가
        goCoderAudioDevice = new WOWZAudioDevice();


        // 방송을 위한 인스턴스 만듬
        goCoderBroadcaster = new WOWZBroadcast();

        // 방송에 추가되는 설정 인스턴스
        goCoderBroadcastConfig = new WOWZBroadcastConfig(WOWZMediaConfig.FRAME_SIZE_1280x720);

        // 방송하기 위한 설정
        // Set the connection properties for the target Wowza Streaming Engine server or Wowza Streaming Cloud live stream
        goCoderBroadcastConfig.setHostAddress("13.209.208.103");
        goCoderBroadcastConfig.setPortNumber(1935);
        goCoderBroadcastConfig.setApplicationName("live");
        goCoderBroadcastConfig.setStreamName(new UuidTest().getUnicVodString()); // 사용자가 수정한 타이틀을 받아옵니다. 문제가 있다. 한글로 타이틀을 정할 경우 나중에 동영상 녹화시에 해당 타이틀.mp4 에서 한글의 경우 깨진다. 따라서 vor가 저장된 경로를 찾을 수 없게 된다

        goCoderBroadcastConfig.setAudioEnabled(true);
        goCoderBroadcastConfig.setVideoEnabled(true);
        goCoderBroadcastConfig.setUsername("wowza");
        goCoderBroadcastConfig.setPassword("i-0dfdb5881429cfe3b");
        goCoderBroadcastConfig.setVideoBroadcaster(goCoderCameraView); // 방송을 하게 될 원본 view를 지정합니다
        goCoderBroadcastConfig.setAudioBroadcaster(goCoderAudioDevice); // 방송을 할 떄 원본 받아올 audio 를 지정합니다.




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
                    // Start streaming
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

                }
            }
        });


        // 방송 종료 버튼
        btn_stop_broadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(goCoderBroadcaster.getStatus().isRunning()){ //현재 여전히 방송중이라면
                    goCoderBroadcaster.endBroadcast(goCoderBroadcaster.getStatusCallback()); //끈다


                    POST_LIVE_STREAM("delete.php");
                    pauseChronometer(); // 시간초 멈춤
                    resetChronometer(); // 시간초 다시
                    btn_stop_broadcast.setVisibility(View.GONE); // 정지 버튼 안보이게 한후
                    btn_start_broadcast.setVisibility(View.VISIBLE); // 방송 시작버튼 보이게함
                    Chronometer_stream_time.setVisibility(View.GONE); // 방송시간 버튼 없앰
                    Toast.makeText(getApplicationContext(), "방송을 종료합니다", Toast.LENGTH_SHORT).show();

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




                    wowzCamera = this.goCoderCameraView.getCamera(); // WowzaCanera 객체를 만든다.

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


    /**
     * onResume() 모든 화면이 전면에 등장하였을 때 앱의 권한을 체크합니다
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

    }

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
                break;

            case WOWZState.READY:
                statusMessage.append("방송을 할 준비가 되었습니다.");
                break;

            case WOWZState.RUNNING:
                statusMessage.append("방송을 시작합니다");
                break;

            case WOWZState.STOPPING:
                statusMessage.append("방송을 중지합니다");
                break;

            case WOWZState.IDLE:
                statusMessage.append("방송을 종료합니다");
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


    // 시간 측정 함수
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

        editText_tag.setText(tag);




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

                Toast.makeText(getApplicationContext(), "여행 태그가 추가되었습니다.", Toast.LENGTH_SHORT).show();
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


        // (다이얼로그) 타이틀 수정 확인 버튼
        btn_change_title_ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //방송을 종료하고 다시보기 설정을 할 수 있는 알림창을 띄운다.
                btn_stop_broadcast.performClick(); // 방송을 종료한다.
                dialog.dismiss(); // 창 없앰
                onBackPressed(); // 뒤로가기
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




    //===================================================================================네트워크 통신===================================================================================
    // POST_LIVE_STREAM
    /**
     *
     * @param endpoint  => add.php / delete.php/ update.php
     */
    public void POST_LIVE_STREAM(String endpoint){
        Map<String,String> addLiveStreamParameters = new HashMap<>();
        addLiveStreamParameters.put("id", id);
        addLiveStreamParameters.put("title", title);
        addLiveStreamParameters.put("tag", tag);
        addLiveStreamParameters.put("route_stream", routeStream);

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
                    Toast.makeText(getApplicationContext(), "방송정보저장", Toast.LENGTH_SHORT).show();
                }else{ // 방송 POST_ADD 실패
                    Toast.makeText(getApplicationContext(), "방송정보저장실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PostResult> call, Throwable t) {

            }
        });
    }

    //POST_VOD_ADD
    public void POST_VOD_STREAM(String endpoint){

    }
}
