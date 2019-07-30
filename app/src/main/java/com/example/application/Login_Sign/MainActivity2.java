package com.example.application.Login_Sign;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.application.IPclass;
import com.example.application.Logg;
import com.example.application.R;
import com.facebook.login.LoginManager;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.helper.log.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 로그인 시 로그인한 사용자의 고유한 아이디 값을 메인페이지로 전달과 동시에 메인페이지로 이동
 * 아이디값을 전달받은 메인페이지는 서버에 아이디값에 맞는 유저 정보를 요청한다.
 * 서버는 해당 유저의 정보를 클라이언트에게 응답한다.
 * 응답받은 클라이언트는 해당 유저 정보를 메인페이지 사용자정보 보기칸에 보여준다.
 *
 */

public class MainActivity2 extends AppCompatActivity {

    Button kako_button_logout, btn_custom_facebook_logout, btn_email_logout;
    TextView user_nickname, user_sns, user_email;

    String id;
    Intent intent; //값을 받아오기 위한 intent


    Toolbar toolbar;


    // onBackPress 버튼 시간체크
    long first_time;
    long second_time;

    protected void redirectIndexActivity() {
        //저장되었던 데이터 삭제
        SharedPreferences sharedPreferences = getSharedPreferences("file", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("id");
        editor.commit();


        final Intent intent = new Intent(this, IndexActivity.class);
        startActivity(intent);
        finish();
    }




    /*
    카카오 로그아웃 함수
     */
    private void onClickLogout() {
        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                //그리고 초기화면으로 이동
                redirectIndexActivity();
            }
        });
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        kako_button_logout = findViewById(R.id.kako_button_logout); //카카오 로그아웃
        btn_custom_facebook_logout = findViewById(R.id.btn_custom_facebook_logout); //페이스북 로그아웃
        btn_email_logout = findViewById(R.id.btn_email_logout);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        /*
        이메일 계정 로그아웃
         */

        btn_email_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 저장되어 있던 id 값 삭제
                SharedPreferences sharedPreferences = getSharedPreferences("file", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("id");
                editor.commit();
                finish();

                redirectIndexActivity();
            }
        });


        Logg.i("-------------------------TEST--------------------");
        user_email = findViewById(R.id.user_email);
        Logg.i("-------------------------TEST--------------------");
        user_nickname = findViewById(R.id.user_nickname);
        Logg.i("-------------------------TEST--------------------");
        user_sns = findViewById(R.id.user_sns);
        Logg.i("-------------------------TEST--------------------");



        intent = getIntent();
        Logg.i("-------------------------TEST--------------------");
        id = intent.getStringExtra("id");
        Logg.i("------------------------TEST---------------------- id : " + id);

        String strUrl = "http://" + IPclass.IP_ADDRESS + "/main/main.php";

        RequsetAccount requestAccount = new RequsetAccount();
        Logg.i("-------------------------TEST----------------------");
        requestAccount.execute(strUrl, id); // 접속할 서버 와 아이디를 넘긴다.
        Logg.i("-------------------------TEST----------------------");




    //-----------------------------카카오 로그아웃 -----------------------
        kako_button_logout.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onClickLogout();
        }
    });


        btn_custom_facebook_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logOut();
                redirectIndexActivity();
            }
        });

    }



    //--------------------------------------------------서버 요청---------------------------------------------
    class RequsetAccount extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            String param_strUrl = params[0];
            String param_id = params[1];

            Logg.i("-------------------------TEST----------------------" + "id : " + param_id + "// strurl : " + param_strUrl);

            String postParameter = "id=" + param_id;

            Logg.i("--------------------------TEST-------------------" + "postParameter : " + postParameter);



            try{
                URL url = new URL(param_strUrl);
                 HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                Logg.i("--------------------TEST-----------------");

                 httpURLConnection.setRequestMethod("POST");
                Logg.i("--------------------TEST-----------------");
                 httpURLConnection.setConnectTimeout(5000);
                Logg.i("--------------------TEST-----------------");
                 httpURLConnection.setReadTimeout(5000);
                Logg.i("--------------------TEST-----------------");
                 httpURLConnection.setDoInput(true); //?
                Logg.i("--------------------TEST-----------------");
                 httpURLConnection.setDoOutput(true);
                Logg.i("--------------------TEST-----------------");
                 httpURLConnection.connect();
                Logg.i("--------------------TEST-----------------");


                OutputStream outputStream = httpURLConnection.getOutputStream();
                Logg.i("--------------------TEST-----------------");

                outputStream.write(postParameter.getBytes("UTF-8"));
                Logg.i("--------------------TEST-----------------");
                outputStream.flush();
                Logg.i("--------------------TEST-----------------");
                outputStream.close();
                Logg.i("--------------------TEST-----------------");


                Logg.i("--------------------TEST-----------------");
                 int requsetStatus = httpURLConnection.getResponseCode();
                Logg.i("--------------------TEST-----------------");

                InputStream inputStream;

                 if(requsetStatus == HttpURLConnection.HTTP_OK){
                     Logg.i("--------------------TEST-----------------");
                     inputStream = httpURLConnection.getInputStream();
                     Logg.i("--------------------TEST-----------------응답코드 : " + requsetStatus);
                 }else{
                     Logg.i("--------------------TEST-----------------");
                     inputStream = httpURLConnection.getErrorStream();
                     Logg.i("--------------------TEST-----------------");
                 }
                Logg.i("--------------------TEST-----------------");
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                Logg.i("--------------------TEST-----------------");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                Logg.i("--------------------TEST-----------------");

                StringBuffer stringBuffer = new StringBuffer();
                Logg.i("--------------------TEST-----------------");
                String line = null;
                Logg.i("--------------------TEST-----------------");

                Logg.i("--------------------TEST-----------------");

                while((line = bufferedReader.readLine()) != null){
                    Logg.i("--------------------TEST-----------------");
                    stringBuffer.append(line);
                    Logg.i("--------------------TEST-----------------");
                }

                Logg.i("--------------------TEST-----------------");
                bufferedReader.close();
                Logg.i("--------------------TEST-----------------");
                System.out.println("가져온 값 : " + stringBuffer.toString());
                Logg.i("--------------------TEST-----------------");

                return  stringBuffer.toString();


            }catch (Exception e){
                e.getMessage();
                Logg.i("---------------------TEST--------------------" + e.getMessage());
                return  e.getMessage();
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            Logg.i("--------------TEST---------------- response : " + response);

            try {
                JSONObject jsonObject =new JSONObject(response);
                System.out.println("jsonObject.getString(\"user_email\") : " +jsonObject.getString("user_email"));
                System.out.println("jsonObject.getString(\"nick_name\"); : "+ jsonObject.getString("nick_name"));

                /*
                전달 받은 것을 넣어주자
                 */


                /**
                 *
                 * 우선 발표
                 * 모든 버튼이 있으면 이상하니까
                 *
                 */
                Logg.i("--------------------무엇을 가져올까?--------------- jsonObject.getString(\"sns\").equals(\"kakao\") : " + jsonObject.getString("sns").equals("kakao"));

                if(jsonObject.getString("sns").equals("kakao")){ //카카오 버튼만 살림
                    btn_custom_facebook_logout.setVisibility(View.GONE); //페북
                    btn_email_logout.setVisibility(View.GONE); //이메일
                }else if(jsonObject.getString("sns").equals("facebook")){
                    btn_email_logout.setVisibility(View.GONE); //이메일
                    kako_button_logout.setVisibility(View.GONE); //카카오
                }else{
                    btn_custom_facebook_logout.setVisibility(View.GONE); // 페북
                    kako_button_logout.setVisibility(View.GONE); //카카오
                }

                if(!(jsonObject.getString("sns").equals(""))){
                    user_sns.setText(jsonObject.getString("sns"));
                    Logg.i("--------------TEST----------------- user_email" + jsonObject.getString("user_email"));
                }
                user_nickname.setText(jsonObject.getString("nick_name"));
                Logg.i("--------------TEST----------------- nick name : " + jsonObject.getString("nicK_name"));

                user_email.setText(jsonObject.getString("user_email"));
                Logg.i("--------------TEST----------------- sns : " + jsonObject.getString("sns"));


                // 저장한다.
                SharedPreferences sharedPreferences = getSharedPreferences("file", MODE_PRIVATE);
                Logg.i("--------------------test---------------------");
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Logg.i("--------------------test---------------------");
                Logg.i("--------------------test--------------------- id :" + id);
                editor.putString("id", id);
                Logg.i("--------------------test---------------------");
                editor.commit();
                Logg.i("--------------------test---------------------");



            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onBackPressed() {

        Log.d("과연?", "실행1?");
        second_time = System.currentTimeMillis();
        Log.d("과연?", "실행2?");
        Snackbar.make(getWindow().getDecorView().getRootView(), "한 번더 뒤로가기를 누르면 어플을 종료합니다.", Snackbar.LENGTH_SHORT).show();
        Log.d("과연?", "실행3?");
        if (second_time - first_time < 2000) { // 첫번째 누름과 두번 째 누름의 시간차가 2초 이내일 경우
            super.onBackPressed();
            finishAffinity();
            System.exit(0);
            finish(); // 현재 액티비티 제거.(즉 메인 로그인 홈 화면으로 이동)
        }

        first_time = System.currentTimeMillis();
    }


}
