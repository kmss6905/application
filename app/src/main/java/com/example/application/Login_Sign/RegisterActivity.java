package com.example.application.Login_Sign;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.application.IPclass;
import com.example.application.R;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterActivity extends AppCompatActivity {



    EditText user_email, usr_pwd, usr_pwdConfim_text;
    Button ok_btn;
    TextView reponse_text;
    static String aws_server;


    private static String TAG = "phptest";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        user_email = findViewById(R.id.user_email);
        usr_pwd = findViewById(R.id.usr_pwd);
        usr_pwdConfim_text = findViewById(R.id.usr_pwdConfim_text);

        ok_btn = findViewById(R.id.ok_btn);
        reponse_text = findViewById(R.id.response_text);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); //툴바를 추가
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //툴바에 뒤로가기 기능 추가
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        //이메일 텍스트 감지







        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                String id = user_email.getText().toString();
                String pwd = usr_pwd.getText().toString();
                String pwd_confirm = usr_pwdConfim_text.getText().toString();


                if(id.equals("") || pwd.equals("") || pwd_confirm.equals("")){//값이 모드 들어오지 않았을 경우
                    Snackbar.make(view, "모두 입력해주시기 바랍니다", Snackbar.LENGTH_SHORT).show();
                    return; // 종료
                }
                if(!pwd.equals(pwd_confirm)){
                    Snackbar.make(view, "비밀번호가 다릅니다", Snackbar.LENGTH_SHORT).show();
                    return; // 종료
                }


                InsertData task = new InsertData(); //스레드로 돌릴 네트워크 작업
                task.execute("http://" + IPclass.IP_ADDRESS + "/SignUp_login/vertify.php", id,pwd,pwd_confirm); //네트워크 작업 실행



            }
        });






    }








    class InsertData extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(RegisterActivity.this, "잠시만 기다리세요", null, true,true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

            Log.d(TAG, "POST response  - " + result);


            if(result.equals("notEmailForm")){
//                Snackbar.make(getWindow().getDecorView().getRootView(), "올바른 이메일 형식이 아닙니다.", Snackbar.LENGTH_SHORT).show();
                Toast.makeText(RegisterActivity.this, "올바른 이메일 형식이 아닙니다.", Toast.LENGTH_SHORT).show();
            }else if(result.equals("notSamePwd")){
                Toast.makeText(RegisterActivity.this, "비밀번호가 다릅니다.", Toast.LENGTH_SHORT).show();
//                Snackbar.make(getWindow().getDecorView().getRootView(), "비밀번호가 다릅니다.", Snackbar.LENGTH_SHORT).show();
            }else if(result.equals("sendSuccess")){
                Toast.makeText(RegisterActivity.this, "해당 이메일로 인증메일을 보냈습니다.", Toast.LENGTH_SHORT).show();
//                Snackbar.make(getWindow().getDecorView().getRootView(), "해당 이메일로 인증메일을 보냈습니다.", Snackbar.LENGTH_SHORT).show();
            }else if(result.equals("sendFail")){
                Toast.makeText(RegisterActivity.this, "이메일 전송 실패", Toast.LENGTH_SHORT).show();
//                Snackbar.make(getWindow().getDecorView().getRootView(), "이메일 전송 실패", Snackbar.LENGTH_SHORT).show();
            }else if(result.equals("already")){
                Toast.makeText(RegisterActivity.this, "해당 이메일로 가입된 계정이 이미 존재합니다", Toast.LENGTH_SHORT).show();
//                Snackbar.make(getWindow().getDecorView().getRootView(), "해당 이메일로 가입된 계정이 이미 존재합니다", Snackbar.LENGTH_SHORT).show();
            }else if(result.equals("notlen")){
                Toast.makeText(RegisterActivity.this, "비밀번호는 6자이상 20자 이하 로 입력해주세요", Toast.LENGTH_SHORT).show();
//                Snackbar.make(getWindow().getDecorView().getRootView(), "비밀번호는 6자이상 20자 이하 이어야 합니다.", Snackbar.LENGTH_SHORT).show();
            }else if(result.equals("notSpace")){
                Toast.makeText(RegisterActivity.this, "비밀번호는 공백없이 입력해주세요", Toast.LENGTH_SHORT).show();
            }else{
                Dialog();
            }

        }

        @Override
        protected String doInBackground(String... params) {


            String email  = (String)params[1];
            System.out.println(email);
            String pwd = (String)params[2];
            System.out.println(pwd);
            String pwd_confirm = (String)params[3];
            System.out.println(pwd_confirm);


            String serverURL = (String)params[0];


            String postParameters = "email=" + email + "&pwd=" + pwd + "&pwd_confirm=" + pwd_confirm;

            try{
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
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
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);//버퍼 리더를 통해 우리가 읽을 수 있게 해석함

                StringBuffer sb = new StringBuffer();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();
                System.out.println("가져온 값 : " + sb.toString());

                return sb.toString();


            }catch (Exception e){
                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }








    //홈으로 가는 버튼
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_register, menu); //메뉴를 인플레이터 해서 메모리를 참조할 수 있도록 한다
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //이거 하나만 기능 구현하면 됨(다른 액션버튼 필요 없어)
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    public void Dialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("회원가입 완료");
        builder.setMessage("가입하신 이메일 계정으로 계정 인증 메일을 보냈습니다\n메일을 확인해주세요");
        builder.setCancelable(true);
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

        builder.show();
    }

}




