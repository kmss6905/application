package com.example.application.SNS;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.application.IPclass;
import com.example.application.Logg;
import com.example.application.R;
import com.example.application.Retrofit2.Repo.GETS.SNS.post;
import com.example.application.Retrofit2.Repo.GETS.USERS.USERINFO;
import com.example.application.Retrofit2.Repo.PostResult;
import com.example.application.Retrofit2.RequestApi;
import com.example.application.SNS.Adpater.PostImageAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;

import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *
 *  이미지를 클릭했을 떄 들어오는 곳 입니다.
 *  이미지에 해당하는 게시물로 이동합니다.
 *  먼저 이미지로 클릭 시 해당 해당 이미지가 게시되어 있는 게시물 번호를 인텐트로 전달받는다.
 *  인텐트로 받은 게시물번호를 이용해 서버로 부터 해당 게시물에 대한 데이터를 가져온다.
 *
 */

public class SNSOnePostActivity extends AppCompatActivity {
    private static final String TAG = "SNSOnePostActivity";

    // 좋아요 버튼 토글
    Boolean islike;

    // 좋아요 개수
    String like_Num;

    Intent intent;
    String post_id; // 게시물 번호(사진 이미지 클릭해서 들고온 게시물 번호)

    // 쉐어드에서 가져올 아이디
    String id;

    // 레트로핏
    RequestApi requestApi;


    // 뷰
    ImageView profileImg;
    TextView nicknameText;
    TextView locationText;
    ImageButton postInfoBtn;
    ViewPager viewPager;
    LinearLayout photoNumLayout;
    TextView nowPhotoNumText;
    TextView totalPhotoNumText;
    ImageButton likeBtn;
    ImageButton moreChatBtn;
    LinearLayout moreLike;
    TextView likeNum;
    TextView nicknameText2;
    TextView contentText;
    TextView moreBtn;
    LinearLayout moreContentLayout;
    TextView tagText;
    LinearLayout postCommentMoreSeeBtn; // 댓글 더보기 레이아웃
    TextView postCommentNumTextBefore1;
    TextView postCommentNumText;
    TextView postCommentNumTextBefore2;
    TextView postDateText;
    CircleIndicator CircleIndicator;
    PostImageAdapter postImageAdapter; // 이미지 어뎁터




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snsone_post);
        SharedPreferences sharedPreferences = getSharedPreferences("file", MODE_PRIVATE);
        id = sharedPreferences.getString("id", "");



        // 뷰 참조
        profileImg = findViewById(R.id.profileImg);
                nicknameText = findViewById(R.id.nicknameText);
        locationText = findViewById(R.id.locationText);
                postInfoBtn = findViewById(R.id.postInfoBtn);
        viewPager = findViewById(R.id.viewPager);
                photoNumLayout = findViewById(R.id.photoNumLayout);
        nowPhotoNumText = findViewById(R.id.nowPhotoNumText);
                totalPhotoNumText = findViewById(R.id.totalPhotoNumText);
        likeBtn = findViewById(R.id.likeBtn);
                moreChatBtn = findViewById(R.id.moreChatBtn);
        moreLike = findViewById(R.id.moreLike);
                likeNum = findViewById(R.id.likeNum);
        nicknameText2 = findViewById(R.id.nicknameText2);
                contentText = findViewById(R.id.contentText);
        moreBtn = findViewById(R.id.moreBtn);
                moreContentLayout = findViewById(R.id.moreContentLayout);
        tagText = findViewById(R.id.tagText);
                postCommentMoreSeeBtn = findViewById(R.id.postCommentMoreSeeBtn);
        postCommentNumTextBefore1 = findViewById(R.id.postCommentNumTextBefore1);
                postCommentNumText = findViewById(R.id.postCommentNumText);
        postCommentNumTextBefore2 = findViewById(R.id.postCommentNumTextBefore2);
                postDateText = findViewById(R.id.postDateText);
        CircleIndicator= findViewById(R.id.indicator);





        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 세팅
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 기본 툴바 타이틀 제목 제거


        // 레트로핏 통신 세팅
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + IPclass.IP_ADDRESS + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        requestApi = retrofit.create(RequestApi.class);


    intent = getIntent();
    post_id = intent.getStringExtra("post_id"); // 서버로 넘어가는 게시물번호


        GET_ONE_SNS_POST("content.php", post_id); // 엔드포인트와 게시물넘버
        CHECK_IS_LIKE(post_id, id); // 게시물 번호 , 접속중인 계정 아이디


}





    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //                                                                                       메뉴툴바 인플레이트 및 선택
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }



    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //                                                                                       생명주기
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------


    @Override
    protected void onResume() {
        super.onResume();

        // 닉네임 클릭
        nicknameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // 좋아요 클릭
        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(islike.equals(true)){ // 좋아요 상태 인 경우

                    // 서버
                    // 해당 게시물에 좋아요데이터를 지우자
                    POST_SNS_LIKE(post_id, "delete.php");


                    //클라이언트
                    // 버튼 색깔검정으로 바꿈
                    // 토글 바꿈
                    likeBtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    islike = false;

                    //좋아요 개수 처리하기 - 1

                    int likenum = Integer.parseInt(like_Num);
                    likenum -= 1;
                     like_Num= String.valueOf(likenum);

                     likeNum.setText(like_Num);



                }else if(islike.equals(false)){ //좋아요 상태가 아닌경우
                    // 서버 해당 게시물에 좋아요를 추가하자
                    POST_SNS_LIKE(post_id, "add.php");

                    //클라이언트
                    // 버튼 색깔빨강으로 바꿈
                    // 토글 바꿈
                    likeBtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.red));
                    islike = true;


                    //좋아요 개수 처리하기 + 1

                    int likenum = Integer.parseInt(like_Num);
                    likenum += 1;
                    like_Num= String.valueOf(likenum);

                    likeNum.setText(like_Num);


                }
            }
        });

        //댓글 더보기 버튼
        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //댓글 더보기 버튼 하단
        postCommentMoreSeeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });








    }

    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //                                                                                       네트워크 통신
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public void GET_ONE_SNS_POST(String endpoint, String post_id){
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("post_id", post_id);


        Call<post> postCall = requestApi.SNS_GET_ONE_POST(endpoint, parameters);
        postCall.enqueue(new Callback<post>() {
            @Override
            public void onResponse(Call<post> call, Response<post> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "onResponse: " + response.message());
                    return;
                }
                post post = response.body(); // 서버로 부터 받아온 게시물 정보

                if (post == null) {
                    Log.d(TAG, "onResponse: post is null");
                    return;
                }

                Log.d(TAG, "onResponse: " + post.toString());
                locationText.setText(post.getAddress()); // 주소

                contentText.setText(post.getContent()); // 내용
                tagText.setText(post.getTag()); // 태그
                like_Num = post.getLike();
                likeNum.setText(like_Num); // 좋아요 수



                postDateText.setText(post.getDate()); // 날짜



                String[] str = post.getPhoto_list().split(",");


                // 이미지 경로등 넣는 부분
                ArrayList<String> photoStringArrayList = new ArrayList<>();

                for (int i = 0; i < str.length; i++) {
                    photoStringArrayList.add(str[i]);
                    System.out.println("실행 str[" + i + "] :" + str[i]);
                }
                // 뷰페이져에 데이터 세팅하기(어레이 리스트로) 뷰페이져 어뎁터에 데이터를 넣는다.
                postImageAdapter = new PostImageAdapter(photoStringArrayList, getApplicationContext());

                if (photoStringArrayList.size() == 1) { //이미지의 개수가 0인 경우에는 인디케이터를 달지 않는다

                    viewPager.setAdapter(postImageAdapter);
                    photoNumLayout.setVisibility(View.GONE); // 개수 숫자 표시도 가림
                    CircleIndicator.setVisibility(View.GONE); // 인디케이터 가림

                } else { // 이미지의 개수가 1이상인 경우에는 인디케이터를 달아 준다.
                    viewPager.setAdapter(postImageAdapter);
                    photoNumLayout.setVisibility(View.VISIBLE); // 개수 숫자 표시도 가림
                    CircleIndicator.setVisibility(View.VISIBLE); // 인디케이터 가림
                }

                    totalPhotoNumText.setText("/" + photoStringArrayList.size());
                    // 뷰페이져 페이지 체인지 리스너

                    // 뷰페이저 페이지 리스너
                    viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                            Log.d(TAG, "onPageScrolled: " + position);
                        }

                        @Override
                        public void onPageSelected(int position) {
                            Log.d(TAG, "onPageSelected: " + position + 1);
                            nowPhotoNumText.setText(String.valueOf(position + 1));
                            Log.d(TAG, "onPageSelected: holder.nowPhotoNumText 의 포지션" + position + 1);
                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {
                            Log.d(TAG, "onPageScrollStateChanged: " + state);
                        }
                    });

                    // 인디케이터에 뷰페이저를 장착한다.
                    CircleIndicator.setViewPager(viewPager);


                    // 받아온 게시물 정보를 알맞게 다시 데이터로 집어넣는다.
                    //post_id의 사용자 아이디를 통해 사용자 정보를 다시 서버로 부터 가져온다(프로필 이미지,닉네임)
                    Log.d(TAG, "onResponse: 실행 " + 1);
                    Log.d(TAG, "onResponse: ??" + post.toString());
                    Log.d(TAG, "onResponse: 실행 " + 2);
                    Log.d(TAG, "onResponse: post.getUser_id : " + post.getUser_id());
                    Log.d(TAG, "onResponse: 실행 " + 3);
                    Call<USERINFO> GET_USER_INFO_CALL = requestApi.GET_USER_INFO(post.getUser_id()); // 유닉한 사용자 아이디를 바탕으로 서버로 부터 사용자 정보 를 가져옴
                    GET_USER_INFO_CALL.enqueue(new Callback<USERINFO>() {
                        @Override
                        public void onResponse(Call<USERINFO> call, Response<USERINFO> response) {
                            if (!response.isSuccessful()) {
                                Log.d(TAG, "onResponse: " + response.message());
                                return;
                            }

                            USERINFO userinfo = response.body();


                            Log.d(TAG, "onResponse: " + response.raw());
                            Log.d(TAG, "onResponse: user_info : " + userinfo.toString());


                            Glide.with(getApplicationContext()).load(userinfo.getProfile_img()).into(profileImg); // 이미지 가져옵니다.
                            nicknameText.setText(userinfo.getNick_name()); // 닉네임 세팅
                            nicknameText2.setText(userinfo.getNick_name()); // 닉네임 세팅


                        }

                        @Override
                        public void onFailure(Call<USERINFO> call, Throwable t) {
                            Log.d(TAG, "onFailure: 유저 정보 못가져옴" + t.getMessage());
                        }
                    });






                // 서버로 부터 해당 게시물의 댓글 수를 가져옵니다.
                Call<PostResult> postResultCall1 = requestApi.SNS_GET_COMMENT_NUM_CALL(post.getId()); // 줄 떄 스트링임 받을 떄 int 로 바꾸어야함
                Logg.i("==============================================testt===========================================");

                postResultCall1.enqueue(new Callback<PostResult>() {
                    @Override
                    public void onResponse(Call<PostResult> call, Response<PostResult> response) {
                        if(!response.isSuccessful()){
                            Logg.i("==============================================testt===========================================");
                            Log.d(TAG, "onResponse: " + response.message());
                            Logg.i("==============================================testt===========================================");
                            Log.d(TAG, "onResponse: " + response.raw());
                            Logg.i("==============================================testt===========================================");
                            return;
                        }

                        PostResult postResult = response.body();

                        if (postResult != null) {
                            Log.d(TAG, "(확인) onResponse: postResult.getResult() 댓글 수// " + postResult.getResult());
                            if(postResult.getResult().equals("0")){ // 댓글이 하나도 없다면
                                postCommentMoreSeeBtn.setVisibility(View.GONE); // 댓글 더보기 텍스트 레이아웃
                                moreChatBtn.setVisibility(View.INVISIBLE); // 하트 옆 댓글 더보기 이미지 버튼
                            }else{ // 댓글이 있다면 보여줌
                                postCommentMoreSeeBtn.setVisibility(View.VISIBLE);
                                moreChatBtn.setVisibility(View.VISIBLE);
                            }
                            postCommentNumText.setText(postResult.getResult());
                        }


                    }

                    @Override
                    public void onFailure(Call<PostResult> call, Throwable t) {
                        Logg.i("==============================================testt===========================================");
                        Log.d(TAG, "onFailure: " + t.getMessage());
                        Logg.i("==============================================testt===========================================");
                    }
                });


            }


            @Override
            public void onFailure(Call<post> call, Throwable t) {
                Log.d(TAG, "onFailure: post 정보 못가져옴" + t.getMessage());
            }
        });
    }


    // 좋아요 추가/ 제거
    public void POST_SNS_LIKE(String post_id, String endpoint){
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("post_id", post_id);
        stringStringHashMap.put("id", id);

        Call<PostResult> postResultCall = requestApi.SNS_POST_LIKE_ADD_RECULT_CALL(endpoint, stringStringHashMap);
        postResultCall.enqueue(new Callback<PostResult>() {
            @Override
            public void onResponse(Call<PostResult> call, Response<PostResult> response) {
                if(!response.isSuccessful()){
                    Log.d(TAG, "onResponse: " + response.message());
                    return;
                }

                PostResult postResult = response.body();
                if(postResult.getResult().equals("success")){
                    if(endpoint.equals("add.php")){
                        Snackbar.make(getWindow().getDecorView().getRootView(), "해당 게시물을 좋아합니다", Snackbar.LENGTH_SHORT).show();
                    }else{
                        Snackbar.make(getWindow().getDecorView().getRootView(), "해당 게시물을 좋아요를 취소합니다.", Snackbar.LENGTH_SHORT).show();
                    }
                }else{

                }

            }

            @Override
            public void onFailure(Call<PostResult> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    public void CHECK_IS_LIKE(String post_id, String shared_id){
        HashMap<String, String> checkparameter = new HashMap<>();
        checkparameter.put("post_id", post_id);
        checkparameter.put("id", shared_id);

        System.out.println("실행10");
        Call<PostResult> postResultCall = requestApi.SNS_POST_LIKE_CHECK(checkparameter);
        System.out.println("실행11");
        postResultCall.enqueue(new Callback<PostResult>() {
            @Override
            public void onResponse(Call<PostResult> call, Response<PostResult> response) {
                if(!response.isSuccessful()){
                    Log.d(TAG, "onResponse: " + response.message());
                    System.out.println("result responce isnotsuccessful");
                    System.out.println("실행12");
                    return;
                }

                System.out.println("실행13");
                PostResult postResult = response.body();
                System.out.println("실행14");

                // 해당 게시물을 내가 좋아요 한 녀석일 경우
                if(postResult.getResult().equals("success")){
                    islike = true; // 토글 -> true
                    likeBtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.red)); // 버튼 색깔 빨강으로 바꿈
                    System.out.println("실행15");
                    System.out.println("result true");
                    System.out.println("실행16");

                // 해당 게시물을 좋아요 하지 않은 녀석일 경우
                }else {
                    islike = false; // 토글 -> false
                    likeBtn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.black)); // 버튼 색깔 검정으로 바꿈
                    System.out.println("실행17");
                    System.out.println("result fail");
                }
            }

            @Override
            public void onFailure(Call<PostResult> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                System.out.println("result complete fail");
            }
        });
    }



}
