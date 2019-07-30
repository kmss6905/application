package com.example.application.Login_Sign;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.application.IPclass;
import com.example.application.Logg;
import com.example.application.MainActivity;
import com.example.application.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.kakao.auth.ApiResponseCallback;
import com.kakao.auth.AuthService;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.auth.network.response.AccessTokenInfoResponse;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

import request.GetHash;


public class IndexActivity extends AppCompatActivity{

    private SessionCallback callback;


    //계정 로그인 버튼, 카카오 로그인 버튼, 페이스북 버튼, 구글 커스텀 버튼
    Button email_login_btn, btn_custom_login, btn_custom_facebook_login, register_btn, find_pwd_btn;
//    TextView register_btn, find_pwd_btn; //회원가입 버튼, 비밀번호 찾기 버튼
    com.kakao.usermgmt.LoginButton kako_login_btn;

    private LoginButton btn_facebook_login;
    private FacebookLoginCallback mLoginCallback;
    private CallbackManager mCallbackManager;






    //카카오톡
    private void requestMe() {
        List<String> keys = new ArrayList<>();
        keys.add("properties.nickname");
        keys.add("properties.profile_image");
        keys.add("kakao_account.email");

        UserManagement.getInstance().me(keys, new MeV2ResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Logg.i("---------------test------------------");
                String message = "failed to get user info. msg=" + errorResult;
                Logg.i("---------------test------------------");
                Logger.d(message);
                Logg.i("---------------test------------------");
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Logg.i("---------------test------------------");
                redirectLoginActivity();
                Logg.i("---------------test------------------");
            }



            @Override
            public void onSuccess(MeV2Response response) {
                Logg.i("---------------test------------------");
                Logg.i("---------------test------------------ response.toString() : " + response.toString());
                String name = response.getNickname();
                String id = String.valueOf(response.getId());
                Logg.i("---------------test------------------");
                String sns_type = "kakao";
                Logg.i("---------------test------------------");
                SnsLogin snsLogin = new SnsLogin();
                Logg.i("---------------test------------------");
                snsLogin.execute("http://" + IPclass.IP_ADDRESS + "/SignUp_login/login.php", id, sns_type, name);
                Logg.i("---------------test------------------");
            }

        });
    }


        // (카톡) 토큰을 요청한다.
    private void requestAccessTokenInfo() {
        AuthService.getInstance().requestAccessTokenInfo(new ApiResponseCallback<AccessTokenInfoResponse>() {
            @Override

            //세션이 닫히면 다시 초기 로그인 선택창으로 이동한다.
            public void onSessionClosed(ErrorResult errorResult) {
                redirectLoginActivity();
            }

            //가입되어 있지 않은 경우
            @Override
            public void onNotSignedUp() {
                // not happened
            }

            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.e("failed to get access token info. msg=" + errorResult);
            }

            @Override
            public void onSuccess(AccessTokenInfoResponse accessTokenInfoResponse) {
                long userId = accessTokenInfoResponse.getUserId();
                Logger.d("this access token is for userId=" + userId);

                long expiresInMilis = accessTokenInfoResponse.getExpiresInMillis();
                Logger.d("this access token expires after " + expiresInMilis + " milliseconds.");
            }
        });
    }





    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //카카오
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        Logg.i("---------------test------------------");


        //페이스북
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        Logg.i("---------------test------------------");
        super.onActivityResult(requestCode, resultCode, data);


    }



    private class SessionCallback implements ISessionCallback {
        /**
         *
         * 로그인 성공 후 메인화면 이동
         */
        @Override
        public void onSessionOpened() {
            Logg.i("---------------test------------------");
            //해당 카카오의 정보(이메일, 닉네임, 사진) 을 가져옴과 동시에 메인화면으로 이동
            requestMe();
        }


        /**
         * 로그인 실패 , 로그인 거절, 동의 안함 등등?
         */
        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if(exception != null) {
                Logg.i("---------------test------------------");
                Logger.e(exception);
            }
        }
    }



    protected void redirectLoginActivity() {
        final Intent intent = new Intent(this, IndexActivity.class);
        Logg.i("---------------test------------------");
        startActivity(intent);
        Logg.i("---------------test------------------");
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        email_login_btn = findViewById(R.id.email_login_btn); // 계정으로 로그인 버튼
        register_btn = findViewById(R.id.register_btn); //회원가입 버튼
        btn_custom_login = findViewById(R.id.btn_custom_login); //카카오 로그인(커스텀) 버튼
        kako_login_btn = findViewById(R.id.kako_login_btn);
        btn_custom_facebook_login = findViewById(R.id.btn_custom_facebook_login); // 페이스북 계정으로 로그인 버튼
        find_pwd_btn = findViewById(R.id.find_pwd_btn); //비밀번호 찾기


        // 비밀번호 찾기
        find_pwd_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FindPwdActivity.class);
                startActivity(intent);
            }
        });








        /*
         * 페이스북의 현재 발행된 토큰을 얻어온다.
         * 만약에 이미 토큰이 있다면?! (즉, 한 번 로그인 했다는 의미)
         */
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        Logg.i("-----------------------------token--------------------" + isLoggedIn + "");
        if(isLoggedIn == true){
            Logg.i("-----------------------------isLoggedIn == true----------------");
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
            Logg.i("-----------------------------isLoggedIn == true----------------");
        }
        Logg.i("-----------------------------token--------------------" + isLoggedIn + "");


        /*
         * 페이스북 으로 로그인
         *
         */
        mCallbackManager = CallbackManager.Factory.create(); // 로그인 응답을 처리할 콜백 관리자를 만듬.
        Logg.i("---------------test------------------");

        mLoginCallback = new FacebookLoginCallback();
        Logg.i("---------------test------------------");

        btn_facebook_login = (LoginButton)findViewById(R.id.btn_facebook_login);
        Logg.i("---------------test------------------");

        btn_facebook_login.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends"));



        btn_facebook_login.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() { //토큰 없이?
            @Override
            public void onSuccess(LoginResult loginResult) {

                System.out.println("loginResult.getAccessToken() : " + loginResult.getAccessToken());

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {

                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("IndexActivity", response.toString());

                                // Application code
                                try {
                                    String id = object.getString("id");
                                    Logg.i("-------test------");
                                    String email = object.getString("email");
                                    Logg.i("-------test------");
                                    System.out.println("email : " + email);
                                    System.out.println("id : " + id);
                                    Logg.i("-------test------");
                                    String name = object.getString("name");
                                    Logg.i("-------test------ name : " + name);

                                    SnsLogin snsLogin = new SnsLogin();

                                    Logg.i("---------test---------");
                                    Logg.i("-------test------");
                                    String sns_type = "facebook";
                                    Logg.i("-------test------");

                                    snsLogin.execute("http://" + IPclass.IP_ADDRESS + "/SignUp_login/login.php", id, sns_type, name);
                                    Logg.i("-------test------");



                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                                try {
                                    String birthday = object.getString("birthday"); // 01/31/1980 format
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        });


                /**
                 *
                 * 이것의 정체는 잘 모르겠음 ...
                 *
                 */

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                // App code
                Log.v("IndexActivity", "cancel");
            }

            @Override
            public void onError(FacebookException error) {
                // App code
                Log.v("IndexActivity", error.toString());
            }
        });



        btn_custom_facebook_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_facebook_login.performClick();
            }
        });




        /*
         * 카카오 로그인
         *
         * 전역 어플리케이션에 있는 KakaoSDKAdapter을 호출하여 앱와 KakaoSDK를 연결한다.
         *
         */
        callback = new SessionCallback();
        Logg.i("---------------test------------------");
        Session.getCurrentSession().addCallback(callback);
        Logg.i("---------------test------------------");
        Session.getCurrentSession().checkAndImplicitOpen();



        /*
         *  구글 로그인
         */










        //회원가입 버튼 클릭시에 회원가입 엑티비티로 이동
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });


        //계정으로 로그인 버튼 클릭시 이메일 로그인 엑티비티로 이동
        email_login_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });


        //카카오 로그인 버튼 클릭
        btn_custom_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kako_login_btn.performClick();
            }
        });


    }


    @Nullable

    public static String getHashKey(Context context) {

        final String TAG = "KeyHash";

        String keyHash = null;

        try {

            PackageInfo info =

                    context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);



            for (Signature signature : info.signatures) {

                MessageDigest md;

                md = MessageDigest.getInstance("SHA");

                md.update(signature.toByteArray());

                keyHash = new String(Base64.encode(md.digest(), 0));

                Log.d(TAG, keyHash);

            }

        } catch (Exception e) {

            Log.e("name not found", e.toString());

        }



        if (keyHash != null) {

            return keyHash;

        } else {

            return null;

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }



    // login.php 로 데이터 전달(요청)

    class SnsLogin extends AsyncTask<String, Void, String>{



        @Override
        protected String doInBackground(String... params) {
            String strUrl = params[0];
            String id = params[1];
            String type = params[2];
            String name = params[3];

            String postParameter = "id=" + id + "&type=" + type + "&name=" + name;

            Logg.i("------------test -------------id : " + id + " // type : "  + type + "// name : " + name);
            Logg.i("----------test--------------" + "postParameter : " + postParameter);


            try{
                URL url = new URL(strUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();

                outputStream.write(postParameter.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();
                Logg.i("------------test -------------");
                InputStream inputStream; // 준비
                Logg.i("------------test -------------");
                int responseCode = httpURLConnection.getResponseCode();
                Logg.i("------------test -------------");
                if(responseCode == HttpURLConnection.HTTP_OK){
                    Logg.i("------------test -------------");
                    inputStream = httpURLConnection.getInputStream();
                }else{
                    Logg.i("------------test -------------");
                    inputStream = httpURLConnection.getErrorStream();
                }

                Logg.i("------------test -------------");
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                Logg.i("------------test -------------");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                Logg.i("------------test -------------");
                StringBuffer stringBuffer = new StringBuffer();
                Logg.i("------------test -------------");
                String line;
                Logg.i("------------test -------------");

                while ((line = bufferedReader.readLine()) != null){
                    Logg.i("------------test -------------");
                    stringBuffer.append(line);
                }


                bufferedReader.close();
                System.out.println("가져온 값 : " + stringBuffer.toString());
                Logg.i("------------test -------------");
                return stringBuffer.toString();






            }catch (Exception e){
                e.getMessage();
                return new String("Error: " + e.getMessage());

            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        /**
         * sns 계정으로 로그인한 결과 값
         * login.php 에서 처리한 결과값을 받음
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject jsonObject = new JSONObject(result);

                jsonObject.get("state");
                jsonObject.get("id");

                if(jsonObject.get("state").toString().equals("닉네임 없음")){
                    Logg.i("----------------------------TEST-----------------------");
                    System.out.println("jsonObject1.get(\"state\"); : "  + jsonObject.get("state"));
                    Logg.i("----------------------------TEST-----------------------");
                    System.out.println("jsonObject1.get(\"id\"); : "  + jsonObject.get("id"));
                    Logg.i("----------------------------TEST-----------------------");
                    System.out.println("jsonObject1.get(\"name\"); : "  + jsonObject.get("name"));
                    Logg.i("----------------------------TEST-----------------------");

                    Intent intent = new Intent(getApplicationContext(), SettingNameActivity.class);
                    Logg.i("----------------------------TEST-----------------------");
                    intent.putExtra("id", jsonObject.get("id").toString()); // 어떤 계정인지 알기위해 id 값을 닉네임 설정 액티비티로넘긴다.
                    Logg.i("----------------------------TEST-----------------------");
                    intent.putExtra("name", jsonObject.get("name").toString()); // 카톡에서 가져온 닉네임을 닉네임 설정 액티비티로 넘긴다.
                    Logg.i("----------------------------TEST-----------------------");


                    startActivity(intent);
                    finish();
                }else if(jsonObject.get("state").toString().equals("닉네임 있음")){

                    //자동 로그인을 위해 저장함
                    SharedPreferences sharedPreferences = getSharedPreferences("file", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("id", jsonObject.getString("id"));
                    editor.commit();




                    System.out.println("jsonObject1.get(\"state\"); : "  + jsonObject.get("state"));
                    System.out.println("jsonObject1.get(\"id\"); : "  + jsonObject.get("id"));
                    Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                    intent.putExtra("id", jsonObject.getString("id")); // 고유의 key값을 넘겨준다(db)
                    startActivity(intent);
                    finish();
                }



//6788139361505472

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}
