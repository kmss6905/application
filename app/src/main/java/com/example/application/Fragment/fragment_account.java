package com.example.application.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.application.IPclass;
import com.example.application.R;
import com.example.application.Retrofit2.Repo.GETS.USERS.USERINFO;
import com.example.application.Retrofit2.Repo.PostResult;
import com.example.application.Retrofit2.RequestApi;
import com.example.application.SNS.SNSFollowListActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class fragment_account extends Fragment {
    private static final String TAG = "fragment_account";
    // 나의 계정인지 아닌지 구분하기 용 intent
    String hasIntentId;


    TabLayout tablayout; // 텝레이아웃
    ViewPager viewPager; // 뷰 페이저

    ImageView user_profile;
    TextView user_nick;
    TextView vod_num;
    TextView sns_num;
    TextView following_num;
    TextView follower_num;
    Button followBtn; // 팔로우 하기 버튼
    Button followingBtn; // 팔로잉 취소하기 버튼
    ImageButton account_toolbar_back; // 뒤로가기 이미지 버튼
    TextView account_toolbar_layout_title; // 해당 계정 닉네임
    LinearLayout account_toolbar_layout; // 레이아웃

    String nick_name; // 우저 닉네임



    String id; // shared id


    RequestApi requestApi;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) { // sns 게시물 닉네임 클릭시 이동
            hasIntentId = getArguments().getString("user_id"); // 다른 사람 계정을 클릭한 경우 해당 사용자의 유일한 아이디 가져오기 , 해당 아이디를 파라미터로 서버로 부터 해당 유저의 정보 가져옴
            Log.d(TAG, "onCreate: hasIntentId : "  + hasIntentId);
        }
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("file", Context.MODE_PRIVATE);
        id = sharedPreferences.getString("id", "");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.layout_fragment_account, container, false);


        // Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + IPclass.IP_ADDRESS + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        requestApi = retrofit.create(RequestApi.class);


        //뷰 초기화
        user_profile = rootView.findViewById(R.id.user_profile);
        vod_num = rootView.findViewById(R.id.vod_num);
        sns_num = rootView.findViewById(R.id.sns_num);
        following_num = rootView.findViewById(R.id.following_num);
        follower_num = rootView.findViewById(R.id.follower_num);
        user_nick = rootView.findViewById(R.id.user_nick);
        followBtn = rootView.findViewById(R.id.followBtn); // 팔로우 할 수 있는 버튼
        followingBtn = rootView.findViewById(R.id.followingBtn); // 이미 팔로잉 된 버튼
        account_toolbar_back = rootView.findViewById(R.id.account_toolbar_back);
        account_toolbar_layout_title = rootView.findViewById(R.id.account_toolbar_layout_title);
        account_toolbar_layout = rootView.findViewById(R.id.account_toolbar_layout); //레이아웃


        if(hasIntentId != null){ // 홈 게시글에서 sns 계정 클릭해서 들어왔을 경우 통신 ( 나의 계정 클릭도 포함됨 )


            if(hasIntentId.equals(id)){ // 그런데 그 녀석이 내 계정이다?
                account_toolbar_layout.setVisibility(View.GONE); // 닉네임만 표시되는 툴바 레이아웃은 없애고


                followBtn.setVisibility(View.INVISIBLE);
                followingBtn.setVisibility(View.INVISIBLE);

                // 서버로부터 정보 가져오기
                GET_USER_INFO(id); // 사용자 정보 가져오기
                GET_FOLLOWING_NUM(id); // 팔로잉 수 가져오기
                GET_SNS_POST_NUM(id); // SNS 게시물 수 가져오기
                GET_FOLLOW_NUM(id, hasIntentId); // 팔로워 리스트 가져옴
                // VOD 수 가져오기
                // 팔로워 수 가져오기

            }else{ // 내 계정이 아니다?
                Log.d(TAG, "onCreateView: " + "내 계정 아님");


                // 서버로부터 정보 가져오기
                GET_USER_INFO(hasIntentId); // 사용자 정보 가져오기
                GET_FOLLOWING_NUM(hasIntentId); // 팔로잉 수 가져오기
                GET_SNS_POST_NUM(hasIntentId); // SNS 게시물 수 가져오기
                CHECK_SUBSCRIBE(hasIntentId, id); // 해당 계정을 체크해야함.
                GET_FOLLOW_NUM(id, hasIntentId); // 팔로워 리스트 가져옴

                // VOD 수 가져오기
                // 팔로워 수 가져오기



                getActivity().findViewById(R.id.toolbar).setVisibility(View.GONE); //기존의 툴바를 지움
                account_toolbar_layout.setVisibility(View.VISIBLE); // 닉네임만 표시되는 풀바레이아웃은 살린다.







            }

        }else{ // 홈 게시글이 아닌 바로 나의 계정 sns에서 들어갔을 경우


            //  나의 게시물 이기 때문에 팔로우/팔로잉 버튼 숨김
            followBtn.setVisibility(View.INVISIBLE);
            followingBtn.setVisibility(View.INVISIBLE);


            // 서버로부터 정보 가져오기
            GET_USER_INFO(id); // 사용자 정보 가져오기
            GET_FOLLOWING_NUM(id); // 팔로잉 수 가져오기
            GET_SNS_POST_NUM(id); // SNS 게시물 수 가져오기
            GET_FOLLOW_NUM(id, id); // 팔로워 리스트 가져오기


            // VOD 수 가져오기
            // 팔로워 수 가져오기
        }




        SectionPageAdapter sectionPageAdapter = new SectionPageAdapter(getChildFragmentManager());

        tablayout = rootView.findViewById(R.id.tablayout); // 텝레이아웃
        viewPager = rootView.findViewById(R.id.container); // 뷰페이저



        // 4개의 프레그먼트 페이지들에게 데이터를 전송합니다.



        if(hasIntentId != null){ // sns 선택에서 들어왔을 경우

            Bundle args = new Bundle();
            args.putString("user_id", hasIntentId); // 인텐트로 고유의 아이디 값을 넘김니다.

            ac_fr_live ac_fr_live = new ac_fr_live();
            ac_fr_sns_photo ac_fr_sns_photo = new ac_fr_sns_photo();
            ac_fr_sns ac_fr_sns = new ac_fr_sns();
            ac_fr_vod ac_fr_vod =  new ac_fr_vod();

            ac_fr_live.setArguments(args);
            ac_fr_sns_photo.setArguments(args);
            ac_fr_vod.setArguments(args);
            ac_fr_sns.setArguments(args);


            sectionPageAdapter.addFragment(ac_fr_live,"LIVE"); // 넘어가는 지 확인하자
            sectionPageAdapter.addFragment(ac_fr_vod,"VOD");
            sectionPageAdapter.addFragment(ac_fr_sns,"SNS");
            sectionPageAdapter.addFragment(ac_fr_sns_photo,"PHOTO");
            viewPager.setAdapter(sectionPageAdapter);

            tablayout.setupWithViewPager(viewPager);

        }else{

            sectionPageAdapter.addFragment(new ac_fr_live(),"LIVE"); // 넘어가는 지 확인하자
            sectionPageAdapter.addFragment(new ac_fr_vod(),"VOD");
            sectionPageAdapter.addFragment(new ac_fr_sns(),"SNS");
            sectionPageAdapter.addFragment(new ac_fr_sns_photo(),"PHOTO");
            viewPager.setAdapter(sectionPageAdapter);

            tablayout.setupWithViewPager(viewPager);
        }






        return rootView;
    }



    // 생명주기 resume
    @Override
    public void onResume() {
        super.onResume();

        followBtn.setOnClickListener(new View.OnClickListener() { // 팔로잉 할 수 있는 버튼 -> 클릭시 팔로잉 하기
            @Override
            public void onClick(View view) {
                UPDATE_SUBSCRIBE("update.php", hasIntentId, id);
            }
        });


        followingBtn.setOnClickListener(new View.OnClickListener() { //이미 팔로잉 된 버튼 -> 클릭시 팔로잉 취소
            @Override
            public void onClick(View view) {
                UPDATE_SUBSCRIBE("delete.php", hasIntentId, id);
            }
        });


        //뒤로가기
        //손봐야할 필요성이 있다.
        account_toolbar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getActivity().finish();


//                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                fragmentManager.beginTransaction().replace(R.id.frame_layout,new fragment_map()); //???


            }
        });


        // 팔로잉 리스트 보기 버튼
        following_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // intent로 넘겨야 하는 것도 케이스를 나눈다.

                if(hasIntentId != null){ // 홈 게시글에서 sns 계정 클릭해서 들어왔을 경우 통신


                    Intent intent = new Intent(getActivity(), SNSFollowListActivity.class);
                    intent.putExtra("tag", "following");
                    intent.putExtra("user_id", hasIntentId); // 해당 계정의 유닉 아이디를 넘긴다.
                    startActivity(intent);


                }else{ // 홈 게시글이 아닌 바로 나의 계정 sns에서 들어갔을 경우

                    Intent intent = new Intent(getActivity(), SNSFollowListActivity.class);
                    intent.putExtra("tag", "following");
                    intent.putExtra("user_id", id); // 사용자 유닉 아이디를 넘긴다.
                    startActivity(intent);

                }
            }
        });


        // 팔로워 리스트 보기 버튼
        follower_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(hasIntentId != null){ // 홈 게시글에서 sns 계정 클릭해서 들어왔을 경우 통신


                    Intent intent = new Intent(getActivity(), SNSFollowListActivity.class);
                    intent.putExtra("tag", "follower");
                    intent.putExtra("user_id", hasIntentId); // 해당 계정의 유닉 아이디를 넘긴다.
                    startActivity(intent);


                }else{ // 홈 게시글이 아닌 바로 나의 계정 sns에서 들어갔을 경우

                    Intent intent = new Intent(getActivity(), SNSFollowListActivity.class);
                    intent.putExtra("tag", "follower");
                    intent.putExtra("user_id", id); // 사용자 유닉 아이디를 넘긴다.
                    startActivity(intent);

                }
            }
        });




    }

    //--------------------------------------------------------------------------------------------------------------------------------------------------
    //                                                                      서버통신 메소드
    //--------------------------------------------------------------------------------------------------------------------------------------------------

    public void GET_USER_INFO(String id){
        Log.i(TAG, "GET_USER_INFO ");
        // 닉네임 가져오기

        Call<USERINFO> GET_USER_INFO_CALL = requestApi.GET_USER_INFO(id);

        GET_USER_INFO_CALL.enqueue(new Callback<USERINFO>() {
            @Override
            public void onResponse(Call<USERINFO> call, Response<USERINFO> response) {
                if(!response.isSuccessful()){
                    Log.i(TAG, "onRes" +
                            "ponse " + response.message());
                    Log.i(TAG, "onResponse / code ; " + response.code());
                    return;
                }
                USERINFO userinfo = response.body();


                System.out.println("userinfo.getNick_name(); : " + userinfo.getNick_name());
                account_toolbar_layout_title.setText(userinfo.getNick_name()); // 닉네임을 설정
                user_nick.setText(userinfo.getNick_name()); // 닉네임
                Glide.with(getActivity()).load(userinfo.getProfile_img()).into(user_profile); // 프로필 이미지


            }

            @Override
            public void onFailure(Call<USERINFO> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    public void GET_VOD_NUM(String id){

    }

    public void GET_SNS_POST_NUM(String id){
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("user_id", id);


        Call<PostResult> postResultCall = requestApi.GET_NUM("acpostnum.php", parameters);
        postResultCall.enqueue(new Callback<PostResult>() {
            @Override
            public void onResponse(Call<PostResult> call, Response<PostResult> response) {
                if(!response.isSuccessful()){
                    Log.d(TAG, "onResponse: " + response.message());
                    return;
                }

                PostResult postResult = response.body();

                // 제대로 받아 왔는 지확인
                Log.d(TAG, "onResponse: postResult.getResult() : // " + postResult.getResult());
                sns_num.setText(postResult.getResult());

            }

            @Override
            public void onFailure(Call<PostResult> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());

            }
        });
    }


    public void GET_FOLLOW_NUM(String id, String user_id){ // 해당 계정의 아이디, 클릭 아이디
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("id", id);
        parameters.put("user_id", user_id);
        Log.d(TAG, "GET_FOLLOW_NUM: id" + id);
        Log.d(TAG, "GET_FOLLOW_NUM: user_id" + user_id);


        Call<PostResult> postResultCall = requestApi.GET_NUM("acfollowernum.php", parameters);

        postResultCall.enqueue(new Callback<PostResult>() {
            @Override
            public void onResponse(Call<PostResult> call, Response<PostResult> response) {
                if(!response.isSuccessful()){
                    Log.d(TAG, "onResponse: " + response.message());
                    Log.d(TAG, "onResponse: GET_FOLLOW_NUM " + response.message());
                    Log.d(TAG, "onResponse: GET_FOLLOW_NUM " + response.raw() );
                    return;
                }

                PostResult postResult = response.body();

                if(!postResult.equals(null)){
                    follower_num.setText(postResult.getResult());
                }





            }

            @Override
            public void onFailure(Call<PostResult> call, Throwable t) {
                Log.d(TAG, "onFailure: GET_FOLLOW_NUM" + t.getMessage());
            }
        });

    }

    public void GET_FOLLOWING_NUM(String id){
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("user_id", id);

        Call<PostResult> postResultCall = requestApi.GET_NUM("acscribenum.php", parameters);
        postResultCall.enqueue(new Callback<PostResult>() {
            @Override
            public void onResponse(Call<PostResult> call, Response<PostResult> response) {
                if(!response.isSuccessful()){
                    Log.d(TAG, "onResponse: GET_FOLLOWING_NUM" + response.message());
                    return;
                }

                PostResult postResult = response.body();

                // 제대로 받아 왔는 지확인
                Log.d(TAG, "onResponse: postResult.getResult() : /GET_FOLLOWING_NUM/ " + postResult.getResult());
                Log.d(TAG, "onResponse: " + response.raw());
                Log.d(TAG, "onResponse: " + response.body());
                String followingUser[] = postResult.getResult().split(",");
                following_num.setText(String.valueOf(followingUser.length));

            }

            @Override
            public void onFailure(Call<PostResult> call, Throwable t) {
                Log.d(TAG, "onFailure: GET_FOLLOWING_NUM" + t.getMessage());

            }
        });
    }


    public void UPDATE_SUBSCRIBE(String endpoint, String subscribd_id, String id){
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("id", id);
        stringStringHashMap.put("streamerid", subscribd_id);

        Call<PostResult> postResultCall = requestApi.POST_RESULT_CALL(endpoint, stringStringHashMap);
        postResultCall.enqueue(new Callback<PostResult>() {
            @Override
            public void onResponse(Call<PostResult> call, Response<PostResult> response) {
                if(!response.isSuccessful()){
                    Log.d(TAG, "onResponse: " + response.message());
                    return;
                }


                PostResult postResult = response.body();
                if(postResult.getResult().equals("success")){
                    if(endpoint.equals("update.php")){
//                        Toast.makeText(getApplicationContext(),"팔로잉 중입니다.", Toast.LENGTH_SHORT).show();
                        Snackbar.make(getActivity().getWindow().getDecorView().getRootView(), "팔로잉 중입니다", Snackbar.LENGTH_SHORT).show();
                    }else if(endpoint.equals("delete.php")){
//                        Toast.makeText(getApplicationContext(),"팔로잉을 취소하였습니다.", Toast.LENGTH_SHORT).show();
                        Snackbar.make(getActivity().getWindow().getDecorView().getRootView(), "팔로잉을 취소하였습니다.", Snackbar.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getActivity(),"서버통신 실패", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<PostResult> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });


    }



    public void CHECK_SUBSCRIBE(String subscribe_id, String id){
        Call<PostResult> checkFollowResult = requestApi.GET_CHAECK_FOLLOWING("is_subscribe.php", subscribe_id, id);
        checkFollowResult.enqueue(new Callback<PostResult>() {
            @Override
            public void onResponse(Call<PostResult> call, Response<PostResult> response) {
                if(!response.isSuccessful()){
                    Log.d(TAG, "onResponse: " + response.message());
                    return;
                }


                PostResult postResult = response.body();
                if(postResult.getResult().equals("following")){ // 해당 유저가 이미 팔로잉 상태라면
                    // 버튼을 팔로잉 중으로 바꾼다.
                    followBtn.setVisibility(View.VISIBLE);
                    followingBtn.setVisibility(View.GONE);

                }else{ // 해당 유저가 팔로잉 상태가 아니라면

                    //버튼을 팔로우 할수 있는 파란색 버튼으로 바꾼다.
                    followBtn.setVisibility(View.GONE);
                    followingBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<PostResult> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }



}
