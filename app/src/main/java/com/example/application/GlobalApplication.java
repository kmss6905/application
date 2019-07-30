package com.example.application;

import android.app.Activity;
import android.app.Application;

import android.app.Application;
import android.content.Context;

import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthType;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;


public class GlobalApplication extends Application {
    private static volatile GlobalApplication instance = null;
    private static volatile Activity currentActivity = null;


    /**
     * singleton 애플리케이션 객체를 얻는다.
     * @return singleton 애플리케이션 객체
     */
    public static GlobalApplication getGlobalApplicationContext() {
        if(instance == null)
            throw new IllegalStateException("this application does not inherit com.kakao.GlobalApplication");
        Logg.i("---------------test------------------");
        return instance;
    }


    private static class KakaoSDKAdapter extends KakaoAdapter {

        /**
         * Session Config에 대해서는 default값들이 존재한다.
         * 필요한 상황에서만 override해서 사용하면 됨.
         * @return Session의 설정값.
         */


        @Override
        public ISessionConfig getSessionConfig() {
            return new ISessionConfig() {
                @Override
                public AuthType[] getAuthTypes() {
                    return new AuthType[] {AuthType.KAKAO_LOGIN_ALL};
                }

                @Override
                public boolean isUsingWebviewTimer() {
                    Logg.i("---------------test------------------");
                    return false;
                }

                @Override
                public boolean isSecureMode() {
                    Logg.i("---------------test------------------");
                    return false;
                }

                @Override
                public ApprovalType getApprovalType() {
                    Logg.i("---------------test------------------");
                    return ApprovalType.INDIVIDUAL;
                }

                @Override
                public boolean isSaveFormData() {
                    Logg.i("---------------test------------------");
                    return false;
                }
            };
        }

        @Override
        public IApplicationConfig getApplicationConfig() {
            return new IApplicationConfig() {
                @Override
                public Context getApplicationContext() {
                    Logg.i("---------------test------------------");
                    return GlobalApplication.getGlobalApplicationContext(); //??
                }
            };
        }
    }



    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        Logg.i("---------------test------------------");
        KakaoSDK.init(new KakaoSDKAdapter());
        Logg.i("---------------test------------------");

    }



    /**
     * 애플리케이션 종료시 singleton 어플리케이션 객체 초기화한다.
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
        Logg.i("---------------test------------------");
        instance = null;
        Logg.i("---------------test------------------");
    }


    public static Activity getCurrentActivity() {
        Logg.i("---------------test------------------");
        return currentActivity;
    }

    public static void setCurrentActivity(Activity currentActivity) {
        Logg.i("---------------test------------------");
        GlobalApplication.currentActivity = currentActivity;
    }
}