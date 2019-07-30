package com.example.application.Login_Sign;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.application.IPclass;
import com.example.application.R;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;


/**
 *
 *
 * 이메일 입력창에 이메일을 입력한다.
 * 아무것도 입력되어 있지 않을 경우 이메일을 입력하라는 메세지를 띄운다.
 * 이메일을 입력하고 이메일 전송 버튼을 누른다
 * 이메일 전송 버튼을 누르면 입력한 이메일 주소가 서버로 전송된다.
 * 서버에서는 받은 이메일주소가 이메일 형식에 맞는 지 체크한다.
 * 이메일 형식이 아니면 그 즉시 이메일 전송 절차를 종료함과 동시에 클라이언트에게 이메일 형식이 잘 못되었다는 것을
 * 알리기 위해 notEmailForm 이라는 string 값을 전달한다.
 * 이메일 형식이 잘못되었을 경우 올바른 이메일 형식이 아닙니다 라고 하는 메세지를 띄운다
 * 이메일 형식이 올바를 경우 해당 이메일로 가입된 유저가 있는 지 확인한다.
 * 만약 해당 이메일로 가입된 유저가 없을 경우 가입된 유저가 없다고 알린다.
 * 가입된 유저가 없다고 하는 서버의 응답을 받은 클라이언트는 해당 이메일주소로 가입된 유저가 존재하지 않습니다 라는 메세지를 화면에 띄운다
 * 가입된 유저가 있을 경우 해당 이메일 주소로 비밀번호를 바꿀 수 있도록 하는 링크를 메일로 보낸다.
 *
 */


public class FindPwdActivity extends AppCompatActivity {

    EditText send_user_email_text;
    Button btn_send_mail, btn_back_login, btn_re_send_mail;
    Toolbar toolbar;


    private static String TAG = "phptest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pwd);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //홈버튼 기능
        getSupportActionBar().setDisplayShowTitleEnabled(false); //툴바 타이틀 안보여줌


        send_user_email_text = findViewById(R.id.send_user_email_text);
        btn_send_mail = findViewById(R.id.btn_send_mail);
        btn_re_send_mail = findViewById(R.id.btn_re_send_mail);
        btn_back_login = findViewById(R.id.btn_back_login);


        //이메일 전송 버튼
        btn_send_mail.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                String user_email_text = send_user_email_text.getText().toString();

                if(user_email_text.equals("")){ // 이메일주소에 아무것도 입력되지 않았을 경우
                    Snackbar.make(view, "가입 시 사용했던 이메일 주소를 입력하세요", Snackbar.LENGTH_SHORT).show();
                    return;
                }



                String strUrl = "http://" + IPclass.IP_ADDRESS + "/SignUp_login/findpwd.php";


                SendMail sendMail = new SendMail();
                sendMail.execute(strUrl, user_email_text);



            }
        });



        //로그인화면으로 이동
        btn_back_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FindPwdActivity.this, LoginActivity.class);
                startActivity(intent);

                finish();
            }
        });



        btn_re_send_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String re_email_text = send_user_email_text.getText().toString();

                if(re_email_text.equals("")){ // 이메일주소에 아무것도 입력되지 않았을 경우
                    Snackbar.make(view, "가입 시 사용했던 이메일 주소를 입력하세요", Snackbar.LENGTH_SHORT).show();
                    return;
                }



                String restrUrl = "http://" + IPclass.IP_ADDRESS + "/SignUp_login/findpwd.php";


                SendMail resendMail = new SendMail();
                resendMail.execute(restrUrl, re_email_text);
            }
        });



    }



    /**
     *
     * 메뉴
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_register, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     *
     * 서버로 이메일 전송 / 결과 받기
     */

    private class SendMail extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(FindPwdActivity.this, "이메일 전송 중입니다.", null, true,true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

            if(result.equals("notMailForm")){
                Snackbar.make(getWindow().getDecorView().getRootView(), "올바른 이메일 형식을 입력하세요", Snackbar.LENGTH_SHORT).show();
            }else if(result.equals("notUser")){
                Snackbar.make(getWindow().getDecorView().getRootView(), "해당 이메일로 가입된 유저가 존재하지 않습니다", Snackbar.LENGTH_SHORT).show();
            }else{


                if(btn_send_mail.getVisibility() == View.VISIBLE){
                    btn_send_mail.setVisibility(View.GONE); // 이메일 전송 -> 안보이게
                    btn_re_send_mail.setVisibility(View.VISIBLE); // 이메일 재전송
                    btn_back_login.setVisibility(View.VISIBLE); // 로그인으로 가기 버튼
                }




            }
        }

        @Override
        protected String doInBackground(String... params) {


            String serverUrl = params[0];
            System.out.println("serverUrl : " + serverUrl);

            String email = params[1];
            System.out.println("email : " + email);


            String postParameters = "email=" + email;


            try {
                URL url = new URL(serverUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();


                httpURLConnection.setReadTimeout(10000);
                httpURLConnection.setConnectTimeout(10000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);


                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK){
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
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

                return sb.toString();

            }catch (Exception e){
                System.out.println(" e.getMessage()  : " + e.getMessage());
                return new String("Error: " + e.getMessage());

            }


        }


    }

    public static String getUuid(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

}
