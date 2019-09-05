package com.example.application.Broadcast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.se.omapi.Session;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.application.IPclass;
import com.example.application.Logg;
import com.example.application.R;
import com.example.application.Retrofit2.Repo.GETS.BROADCAST.LIVEINFO;
import com.example.application.Retrofit2.Repo.GETS.SUBSCRIBE.GET_REPO_CHECK;
import com.example.application.Retrofit2.Repo.GETS.USERS.USERINFO;
import com.example.application.Retrofit2.Repo.PostResult;
import com.example.application.Retrofit2.RequestApi;
import com.google.android.gms.common.api.internal.StatusCallback;
import com.wowza.gocoder.sdk.api.WowzaGoCoder;
import com.wowza.gocoder.sdk.api.player.WOWZPlayerConfig;
import com.wowza.gocoder.sdk.api.player.WOWZPlayerView;
import com.wowza.gocoder.sdk.api.status.WOWZStatus;
import com.wowza.gocoder.sdk.api.status.WOWZStatusCallback;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ViewerLiveBroadcastActivity extends AppCompatActivity {
    private static final String TAG = "ViewerLiveBroadcastActi";
    SharedPreferences sharedPreferences;
    ProgressDialog LodingDialog; // 방송 화면이 나오기 전까지 나오는 로딩창


    Boolean islock = false;


    Boolean isRunning = false;



    Intent intent; //primary key를 받는 녀석 그리고 routestream 을 받아와야 한다.
    String USERS_PRIMARY_ID;
    String STRAMER_PRIMARY_ID;
    String ROUTE_STREAM;



    RequestApi requestApi;





    WOWZPlayerView wowzPlayerView; // 와우자 플레이뷰


    //==========================================스트림 정보 레이아웃==========================
    LinearLayout layout_livestream_info;
    ImageButton btn_subscribe;
    TextView textView_broadcast_title;
    TextView textView_bj_nick;
    TextView textView_viewers_num;
    TextView textView_streamers_tag;
    ImageView imageView_profile;
    ImageButton btn_unsubscribe;

    //==========================================세팅 레이아웃=================================
    ImageButton btn_viewers_menu_miniwindow;
    ImageButton btn_viewers_menu_map;
    ImageButton btn_viewers_menu_setting;
    ImageButton btn_fullscreen;
    LinearLayout layout_hide_setting;



    //========================================채팅 레이아웃===================================
    RecyclerView recyclerView_chat_list;
    EditText editText_chat;
    ImageButton btn_gift;
    ImageButton btn_good_check;
    TextView alert_info;
    LinearLayout layout_chatting;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer_live_broadcast);


        // ===================================== 시청자의 PRIKEY , 스트리머의 PRIKEY =================================================================================
        sharedPreferences = getSharedPreferences("file", MODE_PRIVATE);
        USERS_PRIMARY_ID = sharedPreferences.getString("id","");


        intent = getIntent();
        STRAMER_PRIMARY_ID = intent.getStringExtra("STRAMER_PRIMARY_ID"); // 스트리머 아이디
        ROUTE_STREAM = intent.getStringExtra("ROUTE_STREAM"); // 루트 스트림(제목)


        //==========================================레트로핏 구현====================================================================================================
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + IPclass.IP_ADDRESS + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        requestApi = retrofit.create(RequestApi.class);


        //====================================================레이아웃 VIEW 참조==============================================\

        layout_livestream_info = findViewById(R.id.layout_livestream_info);
                btn_subscribe = findViewById(R.id.btn_subscribe);
        textView_broadcast_title =findViewById(R.id.textView_broadcast_title);
                textView_bj_nick =findViewById(R.id.textView_bj_nick);
        textView_viewers_num=findViewById(R.id.textView_viewers_num);
                textView_streamers_tag=findViewById(R.id.textView_streamers_tag);
        imageView_profile=findViewById(R.id.imageView_profile);
                btn_viewers_menu_miniwindow=findViewById(R.id.btn_viewers_menu_miniwindow);
        btn_viewers_menu_map=findViewById(R.id.btn_viewers_menu_map);
                btn_viewers_menu_setting=findViewById(R.id.btn_viewers_menu_setting);
        btn_fullscreen=findViewById(R.id.btn_fullscreen);
                recyclerView_chat_list=findViewById(R.id.recyclerView_chat_list);
        editText_chat=findViewById(R.id.editText_chat);
                btn_gift=findViewById(R.id.btn_gift);
        btn_good_check=findViewById(R.id.btn_good_check);
        alert_info= findViewById(R.id.alert_info);
        layout_hide_setting = findViewById(R.id.layout_hide_setting);
        wowzPlayerView = findViewById(R.id.wowza_player_view); // wowza playerView
        layout_chatting = findViewById(R.id.layout_chatting); // chat 레이아웃
        btn_unsubscribe = findViewById(R.id.btn_unsubscribe);


        // 최초에 시작하면
        GET_LIVEINFO_AND_GET_SUBSCRIBE(); // 서버로부터 해당 방송의 정보와 구독정보를 가져옵니다.
        GET_SUBSCRIBE_CHECK(); // 해당 방송의 스트리머를 구독중인지 체크합니다.
        POST_BROADCAST_VIEWERS_NUM("addnum.php");

        //==========================================스트림 정보 레이아웃 기능===========================================
        // 1. 구독 버튼 누르면 구독 or 구독 취소
        btn_subscribe.setOnClickListener(new View.OnClickListener() { // 구독해제 버튼
            @Override
            public void onClick(View view) {

                POST_SUBSCRIBE("delete.php");

                btn_subscribe.setVisibility(View.GONE);
                btn_unsubscribe.setVisibility(View.VISIBLE);

                Toast.makeText(getApplicationContext(), "구독을 취소하였습니다.", Toast.LENGTH_SHORT).show();

            }
        });

        btn_unsubscribe.setOnClickListener(new View.OnClickListener() { // 구독하기 버튼
            @Override
            public void onClick(View view) {

                POST_SUBSCRIBE("update.php");

                btn_subscribe.setVisibility(View.VISIBLE);
                btn_unsubscribe.setVisibility(View.GONE);

                Toast.makeText(getApplicationContext(), "구독 목록에 추가하였습니다", Toast.LENGTH_SHORT).show();
            }
        });




        // 스트리머 지도 보기 버튼
        btn_viewers_menu_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ViewerLiveStreamerMapLocationActivity.class);
                intent.putExtra("route_stream", ROUTE_STREAM);
                startActivity(intent);
            }
        });






        // 화면 클릭시 스트림정보 레이아웃과 뷰세팅 레이아웃을 gone 시킨다.

        layout_hide_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout_hide_setting.setVisibility(View.GONE);
                layout_livestream_info.setVisibility(View.GONE);

                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        6.0f
                );
                layout_chatting.setLayoutParams(param);
            }
        });

        // 다시 화면을 클릭시
        wowzPlayerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout_hide_setting.setVisibility(View.VISIBLE);
                layout_livestream_info.setVisibility(View.VISIBLE);

                GET_LIVEINFO_AND_GET_SUBSCRIBE(); // 서버로부터 해당 방송의 정보와 구독정보를 가져옵니다.
                GET_SUBSCRIBE_CHECK();

                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        4.0f
                );
                layout_chatting.setLayoutParams(param);
            }
        });

        //화면 꾹 눌렀을 경우 먼저 고정되어 있는 지 확인
        wowzPlayerView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE && !(islock)) {
                    // 가로 .. 가로 고정
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
                    Toast.makeText(getApplicationContext(), "가로화면으로 고정되었습니다 \n해제하기 위해선 화면을 2~3초간 터치해주세요", Toast.LENGTH_SHORT).show();
                    islock = true;

                } else if(orientation == Configuration.ORIENTATION_PORTRAIT && !(islock)){
                    // 가로 .. 세로 고정
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
                    Toast.makeText(getApplicationContext(), "세로화면으로 고정되었습니다 \n해제하기 위해선 화면을 2~3초간 터치해주세요", Toast.LENGTH_SHORT).show();
                    islock = true;

                } else if(islock){
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
                    Toast.makeText(getApplicationContext(), "잠금모드가 해제되었습니다", Toast.LENGTH_SHORT).show();
                    islock = false;
                }

                return false;
            }
        });


        //화면 꾹 눌렀을 경우 먼저 고정되어 있는 지 확인
        layout_hide_setting.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE && !(islock)) {
                    // 가로 .. 가로 고정
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
                    Toast.makeText(getApplicationContext(), "가로화면으로 고정되었습니다 \n해제하기 위해선 화면을 2~3초간 터치해주세요", Toast.LENGTH_SHORT).show();
                    islock = true;

                } else if(orientation == Configuration.ORIENTATION_PORTRAIT && !(islock)){
                    // 가로 .. 세로 고정
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
                    Toast.makeText(getApplicationContext(), "세로화면으로 고정되었습니다 \n해제하기 위해선 화면을 2~3초간 터치해주세요", Toast.LENGTH_SHORT).show();
                    islock = true;

                } else if(islock){
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
                    Toast.makeText(getApplicationContext(), "잠금모드가 해제되었습니다", Toast.LENGTH_SHORT).show();
                    islock = false;
                }

                return false;
            }
        });
























        //=========================================플레이뷰 설정 및 재생================================================================
        WowzaGoCoder.init(getApplicationContext(), "GOSK-BD46-010C-E552-981F-FF61");
        WOWZPlayerConfig wowzPlayerConfig;
        wowzPlayerConfig = new WOWZPlayerConfig();
        wowzPlayerConfig.setIsPlayback(true);
        wowzPlayerConfig.setHostAddress("13.209.208.103");
        wowzPlayerConfig.setApplicationName("live");
        wowzPlayerConfig.setStreamName(ROUTE_STREAM);
        wowzPlayerConfig.setPortNumber(1935);
        wowzPlayerConfig.setAudioEnabled(true);
        wowzPlayerConfig.setVideoEnabled(true);

        WOWZStatusCallback statusCallback2 = new StatusCallback();
        wowzPlayerView.play(wowzPlayerConfig, statusCallback2);
        Logg.i("============================================test=============================wowzPlayerView.getCurrentState()1 : " + wowzPlayerView.getCurrentState());
        Logg.i("============================================test=============================wowzPlayerView.getCurrentState()2 : " + wowzPlayerView.getCurrentState());
        Logg.i("============================================test=============================wowzPlayerView.getCurrentState() 3: " + wowzPlayerView.getCurrentState());


//        if(wowzPlayerView.isPlaying()){ // 플레이어가 재생 중이라면
//            Logg.i("========================================================test=================================");
//             // 시청자수를 1 늘린다.
//            Logg.i("========================================================test=================================");
//        }




    }


    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("콜백 ?1 resume ? : isRunning" + isRunning);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        wowzPlayerView.stop();
        wowzPlayerView.clear();
        POST_BROADCAST_VIEWERS_NUM("deletenum.php"); // 시청자수 1 감소
    }















    //========================================================wowza play view 상태 콜백 클래스=======================================

    class StatusCallback implements WOWZStatusCallback {
        private static final String TAG = "StatusCallback";

        @Override
        public void onWZStatus(WOWZStatus wzStatus) {
            Log.e(TAG, "onWZStatus: " + wzStatus.getState() );
            System.out.println("콜백실행1");
            if(wzStatus.isRunning()){
                System.out.println("콜백실행2");
                System.out.println("콜백실행2 : wzStatus.isRunning() : " + wzStatus.isRunning());
                isRunning = true;
                System.out.println("콜백실행2 : isRunning : " + isRunning);
            }
        }



        @Override
        public void onWZError(WOWZStatus wzStatus) {
            Log.e(TAG, "onWZStatus: " + wzStatus.getState() );
            Log.e(TAG, "onWZError: " +  wzStatus.getLastError());

        }

    }
















    //=================================================================서버 통신===========================================================================




    public void GET_LIVEINFO_AND_GET_SUBSCRIBE(){
        Call<USERINFO> userinfoCall = requestApi.GET_USER_INFO(STRAMER_PRIMARY_ID);
        userinfoCall.enqueue(new Callback<USERINFO>() {
            @Override
            public void onResponse(Call<USERINFO> call, Response<USERINFO> response) {
                if(!response.isSuccessful()){
                    Log.i(TAG, "onResponse: " + response.message());
                    return;
                }

                USERINFO userinfo = response.body();
                userinfo.getNick_name();


                textView_bj_nick.setText(userinfo.getNick_name()); // 닉네임
                System.out.println("통신 가져와 ? userinfo.getNick_name() : " + userinfo.getNick_name());
                Glide.with(getApplicationContext())
                        .load(userinfo.getProfile_img())
                        .apply(new RequestOptions()
                                .circleCrop())
                        .into(imageView_profile); // 이미지 넣음



                System.out.println("통신 가져와 ? userinfo.getProfile_img(); : " + userinfo.getProfile_img());





                Call<LIVEINFO> liveinfoCall = requestApi.GET_LIVE_STREAM_INFO(STRAMER_PRIMARY_ID);

                liveinfoCall.enqueue(new Callback<LIVEINFO>() {
                    @Override
                    public void onResponse(Call<LIVEINFO> call, Response<LIVEINFO> response) {
                        if(!response.isSuccessful()){
                            Log.i(TAG, "onResponse: " + response.message());
                            System.out.println("통신 가져와 ? liveinfo?? // repo.message" + response.message());
                            System.out.println("통신 가져와 ? liveinfo?? // repo.code" + response.code());
                            return;
                        }

                        LIVEINFO liveinfo = response.body();
                        liveinfo.getLive_stream_like();

                        textView_viewers_num.setText(liveinfo.getLive_stream_viewers());
                        System.out.println("통신 가져와 ? liveinfo??" + liveinfo.getLive_stream_viewers());
                        textView_broadcast_title.setText(liveinfo.getLive_stream_title());
                        System.out.println("통신 가져와 ? liveinfo??" + liveinfo.getLive_stream_title());
                        textView_streamers_tag.setText(liveinfo.getLive_stream_tag());
                        System.out.println("통신 가져와 ? liveinfo??" + liveinfo.getLive_stream_tag());
                        liveinfo.getLive_stream_route_stream();
                    }

                    @Override
                    public void onFailure(Call<LIVEINFO> call, Throwable t) {
                        System.out.println("통신 가져와 ? liveinfo?? FAIL??" + t.getMessage());
                    }
                });


            }

            @Override
            public void onFailure(Call<USERINFO> call, Throwable t) {

            }
        });
    }

    public void GET_USERINFO(){

    }





    public void GET_SUBSCRIBE_CHECK(){
        Log.i(TAG, "GET_SUBSCRIBE_CHECK: " + new Exception().getStackTrace()[0].getMethodName());

        Call<GET_REPO_CHECK> repo_checkCall = requestApi.GET_SUBSCRIBE_CHECK_INFO(USERS_PRIMARY_ID, STRAMER_PRIMARY_ID);

        repo_checkCall.enqueue(new Callback<GET_REPO_CHECK>() {

            @Override
            public void onResponse(Call<GET_REPO_CHECK> call, Response<GET_REPO_CHECK> response) {
                if(!response.isSuccessful()){
                    Log.i(TAG, "GET_SUBSCRIBE_CHECK: " + new Exception().getStackTrace()[0].getMethodName() + response.message());
                    return;
                }

                GET_REPO_CHECK get_repo_check = response.body();

                if(get_repo_check.getResult().equals("success")){ //구독자일경우
                    //  구독 체크 별표표시
                    btn_subscribe.setVisibility(View.VISIBLE);
                    btn_unsubscribe.setVisibility(View.GONE);
                }else if(get_repo_check.getResult().equals("fail")){
                    //  구독 체크 별표표시 하지 않음
                    btn_subscribe.setVisibility(View.GONE);
                    btn_unsubscribe.setVisibility(View.VISIBLE);
                }
            }



            @Override
            public void onFailure(Call<GET_REPO_CHECK> call, Throwable t) {
                Log.i(TAG, "onFailure: " + new Exception().getStackTrace()[0].getMethodName() + " // " + t.getMessage());
            }
        });
    }



    public void POST_LIVECOMMENT(){

    }

    public void POST_BROADCAST_LIKE(){

    }


    // 시청자 입장시 시청자 수 증가 시키기

    /**
     *
     * @param endpoint => addnum.php / deletenum.php
     *
     */
    public void POST_BROADCAST_VIEWERS_NUM(String endpoint){
        Map<String, String> POST_BROADCAST_BY_VIEWERS = new HashMap<>();
        POST_BROADCAST_BY_VIEWERS.put("streamerid" , STRAMER_PRIMARY_ID);

        Call<PostResult> isAddViewersNumCall = requestApi.POST_BROADCAST_BY_VIEWERS(POST_BROADCAST_BY_VIEWERS, endpoint);
        isAddViewersNumCall.enqueue(new Callback<PostResult>() {
            @Override
            public void onResponse(Call<PostResult> call, Response<PostResult> response) {
                if(!response.isSuccessful()){
                    Logg.i("========================================================test=================================");
                    Log.i(TAG, "onResponse " + response.message());
                    Logg.i("========================================================test================================= response.code() : " + response.code());
                    Logg.i("========================================================test================================= response.message() : " + response.message());
                    Logg.i("========================================================test=================================");
                    return;
                }

                PostResult postResult = response.body();
                if(postResult.getResult().equals("success")){
//                    Toast.makeText(getApplicationContext(), "시청자수 반영", Toast.LENGTH_SHORT).show();
                    Logg.i("========================================================test=================================");
                }else{
//                    Toast.makeText(getApplicationContext(), "시청자수 반영실패", Toast.LENGTH_SHORT).show();
                    Logg.i("========================================================test=================================");
                }
            }

            @Override
            public void onFailure(Call<PostResult> call, Throwable t) {
                Log.i(TAG, "onFailure " + t.getMessage());
                Logg.i("========================================================test=================================");
            }
        });
    }


    /**
     *
     * @param endpoint  => update.php, delete.php
     */
    public void POST_SUBSCRIBE(String endpoint){
        Map<String, String> parameters = new HashMap<>();
        parameters.put("id", USERS_PRIMARY_ID);
        parameters.put("streamerid", STRAMER_PRIMARY_ID);

        Call<PostResult> postResultCall = requestApi.POST_RESULT_CALL(endpoint ,parameters);

        postResultCall.enqueue(new Callback<PostResult>() {
            @Override
            public void onResponse(Call<PostResult> call, Response<PostResult> response) {
                if(!response.isSuccessful()){
                    Log.i(TAG, "onResponse: " + response.message());
                    return;
                }

                PostResult postResult = response.body();
                if(postResult.getResult().equals("success")){ //성공시 쿼리 성공
                     System.out.println("통신 가져와 ? POST_SUBSCRIBE result : " + response.body());
                }else{

                }
            }

            @Override
            public void onFailure(Call<PostResult> call, Throwable t) {
                Log.i(TAG, "onFailure: " + t.getMessage());
            }
        });
    }


}
