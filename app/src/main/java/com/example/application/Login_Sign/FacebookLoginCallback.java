package com.example.application.Login_Sign;

import android.os.Bundle;
import android.util.Log;

import com.example.application.Logg;
import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import java.io.IOException;

public class FacebookLoginCallback  implements FacebookCallback<LoginResult> {
    // 로그인 성공 시 호출 됩니다. Access Token 발급 성공.

    @Override
    public void onSuccess(LoginResult loginResult) {
        Log.e("Callback :: ", "onSuccess");

        requestMe(loginResult.getAccessToken());

        Logg.i("---------test---------");
    }


    // 로그인 창을 닫을 경우, 호출됩니다.

    @Override

    public void onCancel() {

        Log.e("Callback :: ", "onCancel");
        Logg.i("---------test---------");
    }



    // 로그인 실패 시에 호출됩니다.

    @Override

    public void onError(FacebookException error) {

        Log.e("Callback :: ", "onError : " + error.getMessage());
        Logg.i("---------test---------");
    }



    // 사용자 정보 요청

    public void requestMe(AccessToken token) {

        GraphRequest graphRequest = GraphRequest.newMeRequest(token,

                new GraphRequest.GraphJSONObjectCallback() {

                    @Override

                    public void onCompleted(JSONObject object, GraphResponse response) {

                        Logg.i("---------test---------");
                        Log.e("result",object.toString());
                        try {
                            System.out.println("response.getConnection().getHeaderField() : " + response.getConnection().getResponseCode() +"");
                            System.out.println("response.getConnection().getPermission(); : " + response.getConnection().getPermission());
                            System.out.println("response.getRawResponse() : " + response.getRawResponse());
                            System.out.println("response.toString() : " + response.toString());
                            System.out.println("response.getError() : " + response.getError());
                            System.out.println("response.getConnection().getResponseMessage() : " + response.getConnection().getResponseMessage() );
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                });



        Bundle parameters = new Bundle();

        parameters.putString("fields", "id,name,email,gender,birthday");

        graphRequest.setParameters(parameters);

        graphRequest.executeAsync();

    }
}
