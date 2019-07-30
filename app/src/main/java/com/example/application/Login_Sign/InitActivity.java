package com.example.application.Login_Sign;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.example.application.Logg;
import com.example.application.R;

/*

앱을 키면 기본 배경화면이 등장한다
이곳 배경화면은 4~5초동안 지속되다가 다음화면으로 이동되는 초기 화면이다.
이곳에서는 사용자가 이 앱에 로그인 이력이 있으면 로그인 화면으로 이동하지 않고 바로 메인화면으로 이동하게 한다.
로그인 이력이 없다면(예를들어 처음 사용자, 또는 사용자 로그인 후 강제로 로그아웃을 하여 앱을 종료 시킨 경우)


 */

public class InitActivity extends AppCompatActivity {
    private static String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        SharedPreferences sharedPreferences = getSharedPreferences("file", MODE_PRIVATE);
        // email 이라는 key에 저장된 값이 있는 지 확인. 아무값도 들어있지 않으면 ""반환
        id = sharedPreferences.getString("id","");


        startLoding();
    }


    public void chechkUser(){
        if(!(id.equals(""))){ // 만약 값이 있을 경우
            Intent intent = new Intent(InitActivity.this, HomeActivitiy.class);
            intent.putExtra("id", id);
            startActivity(intent);
            finish();

        }else{ // 만약 값이 없을 경우
            Intent intent = new Intent(InitActivity.this, IndexActivity.class);
            startActivity(intent);
            finish();
        }
    }


    private void startLoding(){
        Handler handler = new Handler();
        Logg.i("---------------------------test------------------------");

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Logg.i("---------------------------test------------------------");
                chechkUser();
            }
        }, 2000);
    }
}
