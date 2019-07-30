package com.example.application.Login_Sign;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.application.IPclass;
import com.example.application.Logg;
import com.example.application.R;
import com.google.android.material.snackbar.Snackbar;
import com.kakao.usermgmt.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * 이메일 주소 입력 창과 비밀번호 입력창에 각각 이메일과 비밀번호를 입력한다.
 * 어느 하나라도 입력되어 있지 않으면 값을 입력하라는 경고 메세지를 띄운다.
 * 이메일 입력되어 있고 비밀번호가 입력되어 있지 않은 경우에는 "비밀번호를 입력하세요"라는 입력메세지를 띄운다.
 * 이메일이 입력되어 있지 않고 비밀번호가 입력되어있는 경우 "이메일을 입력하세요"라는 경고메세지를 띄운다.
 * 이메일과 비밀번호를 모두 입력하고 로그인 버튼을
 */
public class LoginActivity extends AppCompatActivity {



    EditText user_email_text, user_pwd_text;
    TextView forget_pwd_btn;
    Button Login_btn;
    Toolbar toolbar;


    //text
    String Json_reuslt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toolbar = findViewById(R.id.toolbar); // 툴바

        user_email_text = findViewById(R.id.user_email_text); // 이메일 입력
        user_pwd_text = findViewById(R.id.user_pwd_text); // 비밀번호 입력

        forget_pwd_btn = findViewById(R.id.forget_pwd_btn);  // 비밀번호 찾기
        Login_btn = findViewById(R.id.Login_btn); // 로그인 버튼

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        forget_pwd_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FindPwdActivity.class);
                startActivity(intent);
            }
        });



        //로그인 버튼 클릭시
        Login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //이메일이 입력되어 있지 않은 경우
                if(user_email_text.getText().toString().equals("")){
                    //이메일 주소를 입력하라는 메세지를 띄움
                    Snackbar.make(view, "이메일 주소를 입력하세요", Snackbar.LENGTH_SHORT).show();
                    return; //종료
                }

                //비밀번호가 입력되어 있지 않은 경우
                if(user_pwd_text.getText().toString().equals("")){
                    Snackbar.make(view, "비밀번호를 입력하세요", Snackbar.LENGTH_SHORT).show();
                    return; //종료
                }

                /**
                 *
                 * 모두 입력되어 있을 경우 해당 계정 존재하는 지 확인하기 위해 입력된 이메일 주소와 비밀번호를 서버로 전달함
                 *
                 *
                 */

                String user_email = user_email_text.getText().toString();
                String user_pwd = user_pwd_text.getText().toString();





                String uURL = "http://" + IPclass.IP_ADDRESS + "/SignUp_login/login.php";



                /*
                통신 스레드 작업
                 */
                CheckAccount checkAccount = new CheckAccount();
                checkAccount.execute(uURL, user_email,user_pwd);

//                InsertData task = new InsertData(); //스레드로 돌릴 네트워크 작업
//                task.execute("http://" + IP_ADDRESS + "/SignUp_login/vertify.php", id,pwd,pwd_confirm); //네트워크 작업 실행


            }
        });



    }




    // 서버작업 : 사용자의 계정 확인 하는 부분

    private class CheckAccount extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Json_reuslt = result;

            System.out.println("result : " + result);



            try {
                JSONObject jsonObject = new JSONObject(result);
                String state = jsonObject.getString("state"); // 닉네임있는 지 확인
                Logg.i("------------------test----------------------state : " + state) ;
                String active = jsonObject.getString("active"); // 활성화상태 확인
                Logg.i("------------------test----------------------active : " + active) ;
                String id = jsonObject.getString("id"); // 서버로부터 전달받은 아이디 값(unique)
                Logg.i("------------------test----------------------id : " + id);


                if(active.equals("0")){ // 비활성화 상태 => 활성화 다이얼로그 띄움
                    Dialog();
                }else if(active.equals("1") && state.equals("닉네임없음")){ // 활성화 & 닉네임 없음 => 닉네임 설정 화면으로 이동
                    Intent intent = new Intent(LoginActivity.this, SettingNameActivity.class);
                    intent.putExtra("id", id);
                    startActivity(intent);
                }else if(active.equals("1") && state.equals("닉네임있음")){ // 활성화 & 닉네임 있음 => 메인 화면으로 이동

                    // 로그인 성공 시 자동로그인을 위해 기기에 해당 유저의 정보를 저장한다.
                    // 해당 유일한 아이디 값을 저장
                    // 초기 화면에서 기기에 아이디 값이 존재한다면 로그인화면으로 이동하지 않고 바로 메인화면으로 이동시킨다.
                    SharedPreferences sharedPreferences = getSharedPreferences("file", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("id", id);
                    editor.commit();


                    Snackbar.make(getWindow().getDecorView().getRootView(), "로그인 성공", Snackbar.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, HomeActivitiy.class);
                    intent.putExtra("id", id);
                    startActivity(intent);
                    finish();
                }else if(id.equals("null")){
                    System.out.println("result2 : " + result);
                    Toast.makeText(LoginActivity.this, "없는 계정입니다", Toast.LENGTH_SHORT).show();
                }else if(id.equals("notAllow")){
                    System.out.println("result3 : " + result);
                    Toast.makeText(LoginActivity.this, "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                }


            } catch (JSONException e) {

                e.printStackTrace();
            }
//
//            if(result.equals("notAllow")){ //비밀번호 틀렸을 경우
//                System.out.println("result1 : " + result);
//                Snackbar.make(getWindow().getDecorView().getRootView(), "비밀번호가 틀렸습니다", Snackbar.LENGTH_SHORT).show();
//            }else if(result.equals("notExist")){ //존재하지 않는 경우
//                System.out.println("result2 : " + result);
//                Snackbar.make(getWindow().getDecorView().getRootView(), "이메일을 다시 입력하세요", Snackbar.LENGTH_SHORT).show();
//            }
        }

        @Override
        protected String doInBackground(String... params) {

            String user_email = (String)params[1];
            System.out.println("user_email 파라미터 : " + user_email);
            String user_pwd = (String)params[2];
            System.out.println("user_pwd 파라미터 : " + user_pwd);

            String serverURL = (String)params[0];
            System.out.println("serverURL 파라미터 : " + serverURL);

            String postParameters = "email=" + user_email + "&pwd=" + user_pwd;



            try{
                URL url = new URL(serverURL);
                 HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();

                 httpURLConnection.setReadTimeout(5000);
                 httpURLConnection.setConnectTimeout(5000);
                 httpURLConnection.setRequestMethod("POST");
                 httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();

                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                //이제 요청한 결과를 받아오자. string 으로




                int responseStatusCode = httpURLConnection.getResponseCode();
                String TAG = "phptest";
                Log.d(TAG, "POST response code - " + responseStatusCode);


                InputStream inputStream;

                if(responseStatusCode == HttpURLConnection.HTTP_OK){
                    inputStream = httpURLConnection.getInputStream();

                }else{
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuffer sb = new StringBuffer();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();

                System.out.println("가져온 값 : " + sb.toString());

                return  sb.toString();


            }catch (Exception e){
                e.printStackTrace();


                //??
                return new String("Error" + e.getMessage());
            }
        }
    }





    // 메뉴

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_register, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId()){ // 뒤로가기 클릭시
            case android.R.id.home:
                onBackPressed(); //뒤로 가기
                break;
        }

        return super.onOptionsItemSelected(item);
    }



    public void Dialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Logg.i("-----------------------TEST------------------");
        builder.setTitle("인증되지 않은 계정");
        Logg.i("-----------------------TEST------------------");
        builder.setMessage("로그인을 진행하기 위해선 계정을 \n인증 해야합니다. \n메일을 확인해 이메일 계정을 인증 시키세요");
        Logg.i("-----------------------TEST------------------");
        builder.setCancelable(true);
        Logg.i("-----------------------TEST------------------");
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Logg.i("-----------------------TEST------------------");

                    }
                });

        builder.show();
    }

}
