package com.example.application.Account;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.application.Broadcast.LiveBroadcastActivity;
import com.example.application.Logg;
import com.example.application.R;
import com.example.application.Retrofit2.Repo.Account;
import com.example.application.Retrofit2.Repo.CheckNickResult;
import com.example.application.Retrofit2.Repo.GETS.USERS.USERINFO;
import com.example.application.Retrofit2.Repo.PostResult;
import com.example.application.Retrofit2.RequestApi;
import com.facebook.internal.LoginAuthorizationType;

import org.w3c.dom.Comment;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
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
    private static final int PICK_FROM_ALBUM = 1;

    // Content contentImgUri; 앨범 중 사진선택시 받아온 content uri
    Uri contentImgUri;

    // 홈 엑티비티로 부터 닉네임을 받아오자
    Intent intent;

    // 쉐어드 에 저장되어 있는 ID
    String id;


    //네트워크 통신 RequestApi
    private RequestApi requestApi;



//    //닉네임 변경 다이얼로그 안의 텍스트
//
//    EditText editText_nick;


    // 툴바
    Toolbar toolbar;

    // 참조
    ImageView account_profile_img;  //프로필 이미지
    Button btn_edit_profile_img; // 프로필 이미지 변경 버튼
    Button btn_edit_nick; // 닉네임 변경 버튼
    Button btn_change_password; // 비밀번호 변경 버튼
    Button btn_show_mycoin_status; //내 코인 현황 버튼
    Button btn_logout; // 로그아웃 버튼
    TextView account_nick; // 닉네임



    //** 임시
    ImageView account_profile_img2;


    //==========================================================권한요청==================================================

    private void requestReadExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        0);
                // MY_PERMISSIONS_REQUEST_READ_EXT_STORAGE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }



    //==========================================================권한거부시==================================================

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0 : {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /**
     *
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        // 앨범에서 아무것도 선택하지 않았을 경우
        if(intent.getData() == null){
            return;
        }else{
            contentImgUri = intent.getData();
            System.out.println("실행 : contentImgUri : " + contentImgUri);
            Glide.with(this).load(contentImgUri).apply(new RequestOptions().circleCrop()).into(account_profile_img);
            POST_USER_IMG_PROFILE();
        }
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_account);

        // 쉐어드 에서 저장되어 있는 ID 를 가져온다.
        SharedPreferences sharedPreferences = getSharedPreferences("file", MODE_PRIVATE);
        id = sharedPreferences.getString("id","");




        // ================================================================레트로핏 객체 생성==============================================
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://13.124.74.188/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        requestApi = retrofit.create(RequestApi.class);




        intent = getIntent();


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("내 정보");






        //============================================================버튼 참조============================================================

        account_profile_img = findViewById(R.id.account_profile_img); // 프로필 이미지
        btn_edit_profile_img = findViewById(R.id.btn_edit_profile_img); // 프로필 이미지 수정 버튼
        btn_edit_nick = findViewById(R.id.btn_edit_nick); // 닉네임 수정 버튼
        btn_change_password = findViewById(R.id.btn_change_password); // 비밀번호 변경 버튼
        btn_show_mycoin_status = findViewById(R.id.btn_show_mycoin_status); // 내 코인 보기 버튼
        btn_logout = findViewById(R.id.btn_logout); // 로그아웃 버튼
        account_nick = findViewById(R.id.account_nick); // 닉네임

        account_nick.setText(intent.getStringExtra("nickname"));
        GET_USER_INFO(); // 유저 프로필 이미지 가져옴


        //============================================================버튼 클릭리스너============================================================


        // 프로필 이미지 수정 버튼 클릭
        btn_edit_profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, PICK_FROM_ALBUM);
                requestReadExternalStoragePermission();
            }
        });


        // 닉네임 변경 버튼 클릭
        btn_edit_nick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                dialog_edit_nick();
//                Toast.makeText(getApplicationContext(), "닉네임변경 클릭", Toast.LENGTH_SHORT).show();
        dialog_edit_nick();
    }
});

        // 비밀번호 변경 버튼 클릭
        btn_change_password.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View view) {
        Intent intent = new Intent(getApplicationContext(), ManagePasswordActivity.class);
        startActivity(intent);
        }
        });

        // 내 코인보기 버튼 클릭
        btn_show_mycoin_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        // 로그아웃 버튼 클릭
        btn_show_mycoin_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });




    }


    //============================================================툴바 메뉴============================================================

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







    //============================================================Dialog 메소드============================================================
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
        final EditText editText_nick = view.findViewById(R.id.editText_nick);

        editText_nick.setText(account_nick.getText().toString());

        final AlertDialog alertDialog = builder.create();

        editText_nick.setSelection(editText_nick.length()); // 방송 전체 선택
        editText_nick.setSelectAllOnFocus(true); // 방송 제목 포커스

        editText_nick.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                editText_nick.post(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager inputMethodManager= (InputMethodManager) ManageAccountActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(editText_nick, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
            }
        });
        editText_nick.requestFocus();






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



        //중복체크 버튼 누를 떄 현재 레이아웃에는 없음
        btn_edit_nick_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getNickName();
            }
        });


        alertDialog.show();
    }





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
//                    editText_nick.setText(account1.getNick_name());
                }
            }
            @Override
            public void onFailure(Call<List<Account>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });



    }



    //===========================================================이미지 파일 업로드=====================================
    public void POST_USER_IMG_PROFILE(){

        System.out.println("실행 1 : POST_USER_IMG_PROFILE : ");
        String filePath = getPathFromUri(contentImgUri); // 기존의 content 경로를 file 경로로 바꿉니다.
        System.out.println("실행 2 : POST_USER_IMG_PROFILE : filepath : " + filePath);  // 바뀌었는지 반드시 확인

        File originFile = new File(filePath); // content 를 file 경로로 바꾼후 file 객체를 만듬
        System.out.println("실행 3 : POST_USER_IMG_PROFILE : ");




        // 요청 바디 1 : 유저 아이디
        RequestBody requestId = RequestBody.create(MediaType.parse("text/plain"), id);


        System.out.println("실행 4 : requestId : " + requestId.contentType());

        // 요청 바디 2 : 이미지 파일
        RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), originFile);
        //Create MultipartBody.Part    using file request-body,file name and part name 파일 부분을 만듬
        MultipartBody.Part filepart = MultipartBody.Part.createFormData("uploaded_file", originFile.getName(), fileReqBody);


        System.out.println("실행 6 : file body to String  : " + filepart.body().toString());


//        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
//        RequestBody requestId = RequestBody.create(MultipartBody.FORM, id);


//        System.out.println("실행 4 : POST_USER_IMG_PROFILE : requestFile : " + requestFile);



//
//        System.out.println("실행 5 : POST_USER_IMG_PROFILE : body : " + body.body());
//        System.out.println("실행 5 : POST_USER_IMG_PROFILE : body : " + body.body());

        Call<PostResult> postResultCall = requestApi.POST_UPLOAD_IMG_CALL(filepart, requestId); // 파라미터에 두개의 요청 바디 part를 넘김

//        System.out.println("실행 7 : postResultCall : " + postResultCall.isExecuted());

        postResultCall.enqueue(new Callback<PostResult>() {
            @Override
            public void onResponse(Call<PostResult> call, Response<PostResult> response) {
                if(!response.isSuccessful()){
                    System.out.println("실행 1 :" + response.message());
                    return;
                }

                PostResult postResult = response.body();
                System.out.println("실행 2 response.message(): " + response.message());
                System.out.println("실행 2 response.code(): " + response.code());
                System.out.println("실행 2 postResult.getResult() : " + postResult.getResult());

                if(postResult.getResult().equals("success")){
                    Toast.makeText(getApplicationContext(), "새로운 프로필 사진이 등록되었습니다", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "새로운 프로필 사진이 등록 실패", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<PostResult> call, Throwable t) {
                System.out.println("오류 발생");
            }
        });

    }


    // Content uri to Path uri
    /**
     *
     * @param uri content 경로
     * @return file 경로
     *
     * 조금더 공부하자 이것이 어떤 역할을 하는 건지??
     */
    public String getPathFromUri(Uri uri){

        Cursor cursor = getContentResolver().query(uri, null, null, null, null );

        System.out.println("실행1 to Filepath : " + cursor);
        System.out.println("실행2 to Filepath : " + cursor.toString());
        cursor.moveToNext(); //??  커서의 위치를 가리키는데 사용> cursor 를 다음 행으로 이동 시킨다. ??
        String path = cursor.getString( cursor.getColumnIndex( "_data" ) );
        System.out.println("실행3 to Filepath : " + path);
        cursor.close();
        System.out.println("실행4 close : ");

        return path;

    }


    public void GET_USER_INFO(){
        Log.i(TAG, "GET_USER_INFO ");


        Call<USERINFO> GET_USER_INFO_CALL = requestApi.GET_USER_INFO(id);

        GET_USER_INFO_CALL.enqueue(new Callback<USERINFO>() {
            @Override
            public void onResponse(Call<USERINFO> call, Response<USERINFO> response) {
                if(!response.isSuccessful()){
                    Log.i(TAG, "onResponse " + response.message());
                    Log.i(TAG, "onResponse / code ; " + response.code());
                    return;
                }


                USERINFO userinfo = response.body();
                Glide.with(getApplicationContext()).load(userinfo.getProfile_img()).apply(new RequestOptions().circleCrop()).into(account_profile_img);
            }

            @Override
            public void onFailure(Call<USERINFO> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }


}
