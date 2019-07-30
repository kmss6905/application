package com.example.application.Account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.application.Logg;
import com.example.application.R;
import com.example.application.Retrofit2.Repo.Account;
import com.example.application.Retrofit2.Repo.CheckNickResult;
import com.example.application.Retrofit2.RequestApi;
import com.facebook.internal.LoginAuthorizationType;

import org.w3c.dom.Comment;

import java.nio.file.Path;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *  세션에 저장되어 있는 ID를 꺼낸다
 *  ID 에 맞는 닉네임과 프포필 정보를 DB로 부터 가져온다
 *  닉네임은 account_nick 에 넣고, 프로필 이미지는 circleview에 넣는다
 *
 */

public class ManageAccountActivity extends AppCompatActivity{
    private static final String TAG = "ManageAccountActivity";


    // 홈 엑티비티로 부터 닉네임을 받아오자
    Intent intent;


    // 쉐어드 에 저장되어 있는 ID
    String id;




    //네트워크 통신 RequestApi
    private RequestApi requestApi;



    //닉네임 변경 다이얼로그 안의 텍스트

    TextView editText_nick;


    // 툴바
    Toolbar toolbar;

    // 참조
    CircleImageView account_profile_img;  //프로필 이미지
    Button btn_edit_profile_img; // 프로필 이미지 변경 버튼
    Button btn_edit_nick; // 닉네임 변경 버튼
    Button btn_change_password; // 비밀번호 변경 버튼
    Button btn_show_mycoin_status; //내 코인 현황 버튼
    Button btn_logout; // 로그아웃 버튼
    TextView account_nick; // 닉네임



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_account);

        intent = getIntent();




        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("내 정보");

        // 쉐어드 에서 저장되어 있는 ID 를 가져온다.
        SharedPreferences sharedPreferences = getSharedPreferences("file", MODE_PRIVATE);
        id = sharedPreferences.getString("id","");






        //------------------------------------------------------ 버튼 참조------------------------------------------------------------------------

        account_profile_img = findViewById(R.id.account_profile_img); // 프로필 이미지
        btn_edit_profile_img = findViewById(R.id.btn_edit_profile_img); // 프로필 이미지 수정 버튼
        btn_edit_nick = findViewById(R.id.btn_edit_nick); // 닉네임 수정 버튼
        btn_change_password = findViewById(R.id.btn_change_password); // 비밀번호 변경 버튼
        btn_show_mycoin_status = findViewById(R.id.btn_show_mycoin_status); // 내 코인 보기 버튼
        btn_logout = findViewById(R.id.btn_logout); // 로그아웃 버튼
        account_nick = findViewById(R.id.account_nick); // 닉네임


        account_nick.setText(intent.getStringExtra("nickname"));


        //--------------------------------------------버튼 클릭리스너----------------------------------------------------------------------


        // 프로필 이미지 수정 버튼 클릭
        btn_edit_profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        // 닉네임 변경 버튼 클릭
        btn_edit_nick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                dialog_edit_nick();
                Toast.makeText(getApplicationContext(), "닉네임변경 클릭", Toast.LENGTH_SHORT).show();
                dialog_edit_nick();
            }
        });




    }


    //------------------------------------------------툴바 메뉴-----------------------------------------------------------------------------

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







    //-----------------------------------------Dialog 메소드--------------------------------------------------------
    // 1. 이미지 변경 다이얼로그 : dialog_edit_img()
    // 2. 닉네임 변경 다이얼로그 : dialog_edit_nick()
    // 3. 비번 변경 다이얼로그 : dialog_change_password()


    void dialog_edit_img(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_profile_img, null);
        builder.setView(view);


    }

    void dialog_edit_nick(){

        /**
         * 해당 id 에 맞는 닉네임을 가져오기 위해 서버에 요청
         * getAccountNickName() 메소드를 사용 해 서버로부터 닉네임을 가져온다.
         * 가져온 닉네임은  editText_nick 에 넣는다.
         * 원하는 닉네임을 입력한 후 중복확인 버튼을 누른다/
         * 이떄 해당 닉네임 checkNickName() 메소드를 이용해 서버로 전송한다.
         * 서버는 해당 닉네임이 닉네임 양식에 맞는 지 체크한다. 양식에 맞지 않을 경우 아무런 값을 반환하지 않는다.
         * 만약 양식에 맞을 경우 전달받은 닉네임이 중복되는 닉네임인지 확인한다.
         * 중복되는 닉네임일 경우 아무런 값을 반환하지 않는다.
         * 중복되지 않는 닉네임일 경우 DB에 해당 닉네임을 업데이트 한다.
         * 저장이 완료되었다는 메세지를 클라이언트로 전달하면 클라이언트는 해당 메세지를 전달받아 "닉네임을 성공적으로 변경하였습니다." 라는 메세지를 띄운다.
         *
         */

        AlertDialog.Builder builder = new AlertDialog.Builder(ManageAccountActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_nick, null);
        builder.setView(view);

        final Button btn_edit_nick_check = (Button)view.findViewById(R.id.btn_edit_nick_check);
        final Button btn_change_nick_cancel = (Button)view.findViewById(R.id.btn_change_nick_cancel);
        final Button btn_change_nick_ok = (Button)view.findViewById(R.id.btn_change_nick_ok);
        editText_nick = (TextView)view.findViewById(R.id.editText_nick);



        final AlertDialog alertDialog = builder.create();


        editText_nick.setText(id);


        // 레트로핏 이용한 통신
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://13.124.74.188/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        requestApi = retrofit.create(RequestApi.class);

        getNickName();





        // 취소 버튼 누를 때
        btn_change_nick_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss(); //그냥 종료
            }
        });



        //확인 버튼 누를 떄
        btn_change_nick_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /**
                 *  닉네임을 입력하지 않고 확인 버튼을 눌렀을 경우 닉네임을 입력하라는 토스트 메세지를 띄운다.
                 *  닉네임을 입력하고 확인버튼을 누른다
                 *  입력한 닉네임을 서버로 전달한다.
                 *  서버에서는 닉네임을 전달받아 닉네임 조건을 확인한다.
                 *  1. 닉네임 양식을 체크한다.
                 *      이떄 닉네임 양식 조건이 다를 경우 클라이언트 에서는 "올바른 닉네임 양식을 입력하세요" 라는 토스트 메세지를 띄운다.
             *      2. 닉네임 중복여부를 체크한다.
                 *      DB 에서 닉네임의 중복여부를 체크한다.
                 *      중복된 닉네임의 경우 클라이언트 에서는 "중복된 닉네임 입니다. 다른 닉네임을 입력하세요." 라는 토스트 메세지를 띄운다
                 *      중복되지 않을 경우 해당 계정 DB에 닉네임을 업데이트 한다.
                 *      클라이언트에서는 성공적으로 닉네임을 변경하였습니다. 라는 토스트 메세지를 띄운다. 그리고 닉네임 변경 다이얼로그 창을 닫는다.
                 *
                 */
                Call<List<CheckNickResult>> listCall = requestApi.checkNick(editText_nick.getText().toString(), id);
                
                listCall.enqueue(new Callback<List<CheckNickResult>>() {

                    @Override
                    public void onResponse(Call<List<CheckNickResult>> call, Response<List<CheckNickResult>> response) {
                        if(!response.isSuccessful()){
                            Log.e(TAG, "onResponse: " + response.message());
                        }


                        List<CheckNickResult> checkNickResults = response.body();



                        for(CheckNickResult checkNickResult : checkNickResults ){
                            Logg.i("================================test============================ : " + checkNickResult.getResult());

                            switch (checkNickResult.getResult()){
                                case "same":
                                    Toast.makeText(getApplicationContext(), "현재 사용하고 있는 닉네임 입니다", Toast.LENGTH_SHORT).show();
                                    break;
                                case "notUpdate":
                                    Toast.makeText(getApplicationContext(), "닉네임 변경 실패(네트워크 문제)", Toast.LENGTH_SHORT).show();
                                    break;
                                case "successUpdate":
                                    Toast.makeText(getApplicationContext(), "성공적으로 닉네임을 변경하였습니다", Toast.LENGTH_LONG).show();
                                    account_nick.setText(checkNickResult.getNick());
                                    alertDialog.dismiss();
                                    break;
                                case "notForm":
                                    Toast.makeText(getApplicationContext(), "닉네임 형식에 맞지 않습니다", Toast.LENGTH_LONG).show();
                                    break;
                                case "overlap":
                                    Toast.makeText(getApplicationContext(), "이미 사용하고 있는 닉네임 입니다", Toast.LENGTH_LONG).show();
                                    break;
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<List<CheckNickResult>> call, Throwable t) {
                        Log.e(TAG, "onFailure: " + t.getMessage());

                    }
                });
                
                
            }
        });



        //중복체크 버튼 누를 떄
        btn_edit_nick_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getNickName();
            }
        });
        
        
        
        
        
        alertDialog.show();
    }




    void dialog_change_password(){

    }

//     void getAccountInfo(){
//        Call<List<Account>> call = requestApi.getAccount();
//    }

    // 닉네임 가져옵니다.
    void getNickName(){

        /**
         * 입력한 닉네임이 중복되지 않는 닉네임인지 확인함
         * 서버로 해당 닉네임을 전달해 닉네임 중복여부를 판단해 사용가능한지 아닌지 체크한다.
         * 중복되지 않을 경우 사용가능 합니다 라는 토스트 메시지를 띄운다.
         * 중복될 경우 이미 사용중인 닉네임이다 라는 토스트 메세지를 띄운다.
         */
// 해당 아이디에 맞는 닉네임을 서버로부터 가져옵니다.
        Call<List<Account>> call = requestApi.getAccount(id);

        Logg.i("=======================================test=========================");
        call.enqueue(new Callback<List<Account>>() {
            @Override
            public void onResponse(Call<List<Account>> call, Response<List<Account>> response) {
                if(!response.isSuccessful()){
                    Log.e(TAG, "onResponse: " + response.message());
                    return;
                }
                List<Account> account =  response.body();
                for(Account account1 : account) {
                    editText_nick.setText(account1.getNick_name());
                }
            }
            @Override
            public void onFailure(Call<List<Account>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });



    }



    // 닉네임을 수정합니다.
    void postUpdateNickName(){
        
    }



//    void post



}
