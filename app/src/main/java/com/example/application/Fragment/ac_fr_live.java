package com.example.application.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.application.R;

public class ac_fr_live extends Fragment {
    private static final String TAG = "ac_fr_live";
    String hasIntentId;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) { // sns 게시물 닉네임 클릭시 이동
            hasIntentId = getArguments().getString("user_id"); // 다른 사람 계정을 클릭한 경우 해당 사용자의 유일한 아이디 가져오기 , 해당 아이디를 파라미터로 서버로 부터 해당 유저의 정보 가져옴
            Log.d(TAG, "onCreate: hasIntentId : "  + hasIntentId);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ac_fr_live, container,false);
        return view;
    }
}
