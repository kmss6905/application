package com.example.application.SNS;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.application.IPclass;
import com.example.application.R;
import com.example.application.Retrofit2.RequestApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *
 *  이미지를 클릭했을 떄 들어오는 곳 입니다.
 *  이미지에 해당하는 게시물로 이동합니다.
 *  먼저 이미지로 클릭 시 해당 해당 이미지가 게시되어 있는 게시물 번호를 인텐트로 전달받는다.
 *  인텐트로 받은 게시물번호를 이용해 서버로 부터 해당 게시물에 대한 데이터를 가져온다.
 *
 */

public class SNSOnePostActivity extends AppCompatActivity {
    private static final String TAG = "SNSOnePostActivity";

    Intent intent;
    String post_id; // 게시물 번호(사진 이미지 클릭해서 들고온 게시물 번호)

    // 쉐어드에서 가져올 아이디
    String id;

    // 레트로핏
    RequestApi requestApi;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snsone_post);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 세팅
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 기본 툴바 타이틀 제목 제거


        // 레트로핏 통신 세팅
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + IPclass.IP_ADDRESS + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        requestApi = retrofit.create(RequestApi.class);


        intent = getIntent();
        post_id = intent.getStringExtra("post_id");





    }


    // 툴바 메뉴
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }




    // ------------------------------------------------------ 네트워크 통신 ------------------------------------------------------
    // 하나의 게시물에 대한 정보를 가져옵니다.
    public void GET_ONE_POST(){

    }



}
