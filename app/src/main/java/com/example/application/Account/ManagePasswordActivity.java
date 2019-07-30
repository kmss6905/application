package com.example.application.Account;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.application.R;
import com.example.application.Retrofit2.Repo.Password;
import com.example.application.Retrofit2.RequestApi;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ManagePasswordActivity extends AppCompatActivity {
    private static final String TAG = "ManagePasswordActivity";



    // 통신에 필요한 레트로핏 요청 api, json을 gson으로 바꿀 컨버터
    RequestApi requestApi;
    GsonConverterFactory gsonConverterFactory;
    String awsServerIp  = "http://13.124.74.188/";
    Map<String, String> postParameter;




    Toolbar toolbar;
    EditText editText_current_pw;
    EditText editText_new_pw;
    EditText editText_new_againg_pw;
    Button btn_change_password;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_password);




        // 레트로핏 구현
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(awsServerIp)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        requestApi = retrofit.create(RequestApi.class);




        // 툴바 참조
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // 참조
        btn_change_password = findViewById(R.id.btn_change_password);
        editText_current_pw = findViewById(R.id.editText_current_pw);
        editText_new_pw = findViewById(R.id.editText_new_pw);
        editText_new_againg_pw = findViewById(R.id.editText_new_againg_pw);


        // 비밀번호 변경 버튼 클릭
        btn_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("file", MODE_PRIVATE);


                if(editText_current_pw.getText().toString().equals("") || editText_new_pw.getText().toString().equals("") || editText_new_againg_pw.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "모두 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                postParameter.put("currentPw", editText_current_pw.getText().toString());
                postParameter.put("newPw", editText_new_pw.getText().toString());
                postParameter.put("newAgainPw", editText_new_againg_pw.getText().toString());
                postParameter.put("id", sharedPreferences.getString("id",""));


                PostChangePw(postParameter);

            }
        });






    }






    //=======================================================메뉴======================================
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_register, menu);


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





    //====================================================메소드 들 ====================================

    /**
     * 비밀번호 변경을 위해 파라미터를 post방식으로 보냄
     * @param postParametersMap
     */
    // 비밀번호 변경 요청 메소드
     void PostChangePw(Map<String, String> postParametersMap){
        Map<String, String> parameters = new HashMap<>();
        parameters = postParametersMap;

        Call<PasswordCheckResponse> passwordCall = requestApi.PASSWORD_CALL(parameters);

        passwordCall.enqueue(new Callback<PasswordCheckResponse>() {
            @Override
            public void onResponse(Call<PasswordCheckResponse> call, Response<PasswordCheckResponse> response) {
                if(!response.isSuccessful()) {
                    Log.e(TAG, "onResponse: " + response.message());
                    return;
                }

                PasswordCheckResponse passwordCheckResponse = response.body();

                switch (passwordCheckResponse.getResult()){
                    case "notUserPw":
                        Toast.makeText(getApplicationContext(), "현재 비밀번호와 일치하지 않습니다", Toast.LENGTH_SHORT).show();

                        break;
                    case "notSamePw":
                        Toast.makeText(getApplicationContext(), "새로운 비밀번호와 비밀번호 재확인이 일치하지 않습니다", Toast.LENGTH_SHORT).show();

                        break;
                    case "notFormPw":
                        Toast.makeText(getApplicationContext(), "올바른 비밀번호 형식이 아닙니다", Toast.LENGTH_SHORT);

                        break;
                    case "success":
                        Toast.makeText(getApplicationContext(), "성공적으로 비밀번호를 변경하였습니다", Toast.LENGTH_SHORT).show();
                        onBackPressed(); // 뒤로 갑니다.
                        break;

                    default:
                        break;

                }
            }

            @Override
            public void onFailure(Call<PasswordCheckResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }

        });

    }






    // 패스워드 변경 요청의 결과값을 받기위한 클래스
    public class PasswordCheckResponse{
        String result;

        public String getResult() {
            return result;
        }
    }
}
