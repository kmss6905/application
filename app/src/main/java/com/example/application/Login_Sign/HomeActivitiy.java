package com.example.application.Login_Sign;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.application.Account.ManageAccountActivity;
import com.example.application.Broadcast.LiveBroadcastActivity;
import com.example.application.Broadcast.ManageMyBroadcastActivity;
import com.example.application.Fragment.fragment_home;
import com.example.application.Fragment.fragment_map;
import com.example.application.Fragment.fragment_travel_info;
import com.example.application.Fragment.fragment_account;
import com.example.application.IPclass;
import com.example.application.Logg;
import com.example.application.R;
import com.example.application.Retrofit2.Repo.GETS.USERS.USERINFO;
import com.example.application.Retrofit2.RequestApi;
import com.example.application.SNS.SNSPostActicity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class HomeActivitiy extends AppCompatActivity {
    private static final String TAG = "HomeActivitiy";

    SharedPreferences sharedPreferences;


    // 레트로핏 인터페이스
    RequestApi requestApi;


    Toolbar toolbar;
    static String nickname;


    // DrawerLayout 에 있는 헤더 참조
    // 프로필이미지, 닉네임, 이메일, 개인 정보 상세보기 버튼
    ImageView nav_header_imageview;
    TextView nav_header_email;
    TextView nav_header_nick_name;
    ImageButton nav_header_info_account;





    // onBackPress 버튼 시간체크
    long first_time;
    long second_time;



    //fragment 사용위한 frament manager 만들기
    //framlayout 에 각각의 프레그먼트를 넣는다.
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private fragment_home  fragment_home = new fragment_home();
    private fragment_map fragment_sns = new fragment_map();
    private fragment_account fragment_account = new fragment_account();
    private fragment_travel_info fragment_travel_info = new fragment_travel_info();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sharedPreferences = getSharedPreferences("file", MODE_PRIVATE);


        // 레트로핏
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + IPclass.IP_ADDRESS + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        requestApi = retrofit.create(RequestApi.class);







        // 툴바 만들기
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //툴바 타이틀 안보여줌


        // Drawerlayout        과 그 안에 있는 NavigationView 참조
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);






        // Drawerlayout 을 안에 있는 child를 꺼냅니다!
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();




        // NavigationView의 nav_header_view 의 값을 바꾸기 위해서는 getHeaderView 함수를 이용한다.
        View nav_header_view = navigationView.getHeaderView(0);
        nav_header_email = (TextView)nav_header_view.findViewById(R.id.textView_email);
        nav_header_nick_name = (TextView)nav_header_view.findViewById(R.id.textView_nickname);
        nav_header_info_account = (ImageButton)nav_header_view.findViewById(R.id.btn_info_account);
        nav_header_imageview = nav_header_view.findViewById(R.id.imageView_profile);

        GET_USER_INFO();

        // 프로필 화면으로 이동
        nav_header_info_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(), "정보더보기 클릭", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), ManageAccountActivity.class);
                intent.putExtra("nickname", nickname);
                startActivity(intent);
            }
        });


        //drawlayer 네비게이션 아이템 선택 리스너
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.post: //게시글 추가 이동
                    startActivity(new Intent(getApplicationContext(), SNSPostActicity.class));
                    break;

                    case R.id.nav_live:
                        Log.i(TAG, "onNavigationItemSelected : nav_live " + nickname);
//                        Toast.makeText(getApplicationContext(), "라이브 방송 클릭", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), LiveBroadcastActivity.class);
                        intent.putExtra("nickname", nickname);
                        startActivity(intent);
                        break;

                    case R.id.nav_my_broadcast_room:
                        Toast.makeText(getApplicationContext(), "내 방송국 가기", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.nav_my_coin_status:
                        Toast.makeText(getApplicationContext(), "코인현황", Toast.LENGTH_SHORT).show();
                        break;
                }

                return true;
            }
        });



        /**
         *
         * 이제 밑 부분인 bottonNavication 부분이다.
         */
        // bottomNavigationView 에 대한 참조를 한다
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);


        // fragment manager를 활용해 어디에 fragment를 나둘지 정하고.
        // fragmentReansaction 을 활용해 어디에 프레그먼트를 두고, 처음에 오게되는 프레그먼트를 정한다.
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment_home).commitAllowingStateLoss();


        //bottomNavigation의 아이템선택 리스너를 만든다. 선택시에는 fragmentTransaction 을 이용해 해당 fragment로 교체한다.

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                switch (menuItem.getItemId()){
                    case R.id.navigation_home:
                        transaction.replace(R.id.frame_layout, fragment_home).commitAllowingStateLoss();
                        break;
                    case R.id.navigation_sns:
                        transaction.replace(R.id.frame_layout, fragment_sns).commitAllowingStateLoss();
                        break;
                    case R.id.navigation_trip:
                        transaction.replace(R.id.frame_layout, fragment_travel_info).commitAllowingStateLoss();
                        break;
                    case R.id.navigation_account:
                        transaction.replace(R.id.frame_layout, fragment_account).commitAllowingStateLoss();
                }
                return true;
            }
        });






    }


    @Override
    protected void onRestart() {
        super.onRestart();
        GET_USER_INFO();
    }

    // ===========================================================툴바 메뉴==============================================================
    // Toolbar에 menu_home_toolbar.xml 을 인플레이트 함
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_home_toolbar, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_notice: // 알림 버튼
                break;


            case R.id.menu_item_search: // 검색 버튼
                break;


            case R.id.menu_item_subscribe: // 구독한 비디오 보기 내역 버튼
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    //===============================================================================서버 통신=============================================================================

    public void GET_USER_INFO(){
        Log.i(TAG, "GET_USER_INFO ");


        Call<USERINFO> GET_USER_INFO_CALL = requestApi.GET_USER_INFO(sharedPreferences.getString("id", ""));

        GET_USER_INFO_CALL.enqueue(new Callback<USERINFO>() {
            @Override
            public void onResponse(Call<USERINFO> call, Response<USERINFO> response) {
                if(!response.isSuccessful()){
                    Log.i(TAG, "onResponse " + response.message());
                    Log.i(TAG, "onResponse / code ; " + response.code());
                    return;
                }


                USERINFO userinfo = response.body();
                nickname = userinfo.getNick_name();


                Log.i(TAG, "userinfo.getNick_name() : " + nickname);
                nav_header_nick_name.setText(nickname);
                nav_header_email.setText(nickname);
                Glide.with(getApplicationContext()).load(userinfo.getProfile_img()).apply(new RequestOptions().circleCrop()).into(nav_header_imageview);

            }

            @Override
            public void onFailure(Call<USERINFO> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }






}
