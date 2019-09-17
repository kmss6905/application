package com.example.application.SNS;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.application.IPclass;
import com.example.application.R;
import com.example.application.Retrofit2.Repo.GETS.SUBSCRIBE.Following;
import com.example.application.Retrofit2.RequestApi;
import com.example.application.SNS.Adpater.LikeAdapter;
import com.example.application.SNS.Class.likeListItemData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SNSFollowListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "SNSFollowListActivity";
    // 새로고침
    SwipeRefreshLayout swipeRefreshLayout;



    // 쉐어드 아이디
    String id;



    // 받아야할 인텐트
    Intent intent;
    String tag;
    String user_id;


    // 레트로핏
    RequestApi requestApi;



     // 리사이클러뷰
    LikeAdapter followingAdpater; // 사실 좋아요 리스트에 쓰던건데 여기서 똑같이 적용되니까 쓰자
    ArrayList<likeListItemData> snsfollowArrayList;
    RecyclerView recyclerView;


    // 페이징을 위한 변수
    private int pageNum = 1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snsfollow_list);
        intent = getIntent();
        tag = intent.getStringExtra("tag");
        user_id = intent.getStringExtra("user_id");

        // 툴바
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true); // 타이틀 가림=
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(tag.equals("following")){  // 팔로잉이면 팔로잉으로 이름 바꾸어줌
            getSupportActionBar().setTitle("팔로잉");
        }else if(tag.equals("follower")){ // 팔러워 이면 팔로워로 이름 바꿔줌
            getSupportActionBar().setTitle("팔로워");
        }

        //새로고침
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);



        // 쉐어드에 저장된 사용자 아이디
        SharedPreferences sharedPreferences = getSharedPreferences("file", MODE_PRIVATE);
        id = sharedPreferences.getString("id", "");


        Gson gson =new GsonBuilder().setLenient().create();


        // 레트로핏 초기화
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + IPclass.IP_ADDRESS + "/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        requestApi = retrofit.create(RequestApi.class);


        // 리사이클러뷰 , 어뎁터 세팅하기
        snsfollowArrayList = new ArrayList<>();
        followingAdpater = new LikeAdapter(snsfollowArrayList, getApplicationContext());
        recyclerView = findViewById(R.id.ReyclerViewFollowList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(followingAdpater);






        // 구분은 팔로잉리스트를 보러왔는지? 아니면 팔로워 리스트를 보러왔는지
        if(tag.equals("following")){

            // 하지만 다른 사람의 계정의 팔로잉 리스트를 보고 싶은건지?
            // 나의 계정의 팔로잉 리스트를 보고 싶은건지 확인
            if(user_id.equals(id)){ // 본인의 계정인 경우
                GET_FOLLOWING_LIST("following_list.php", user_id, id, pageNum); // 상대방의 아이디( 나의 유닉아이디) , 나의 유닉아이디 ex 3, 3
                Log.d(TAG, "onCreate: " + 1);
            }else{ // 본인의 계정이 아닌경우
                GET_FOLLOWING_LIST("following_list.php", user_id, id,pageNum); // 상대방의 유닉 아이디, 나의 유닉아이디 ex 5, 3
                Log.d(TAG, "onCreate: " + 2);
            }



         // 팔로워를 보고 싶은 경우
        }else if(tag.equals("follower")){
            // 하지만 다른 사람의 계정의 팔로잉 리스트를 보고 싶은건지?
            // 나의 계정의 팔로잉 리스트를 보고 싶은건지 확인
            if(user_id.equals(id)){ // 본인의 계정인 경우
                GET_FOLLOWING_LIST("follower_list.php", user_id, id,pageNum); // 상대방의 아이디( 나의 유닉아이디) , 나의 유닉아이디 ex 3, 3
                Log.d(TAG, "onCreate: " + 3);
            }else{ // 본인의 계정이 아닌경우
                GET_FOLLOWING_LIST("follower_list.php", user_id, id,pageNum); // 상대방의 유닉 아이디, 나의 유닉아이디 ex 5, 3
                Log.d(TAG, "onCreate: " + 4);
            }

        }






   }


    /**
     *
     * @param endpoint endpoint 의 경우 following.php 와 follower.php 가 있다.
     * @param user_id 사용자의 아이디를 보낸다.
     */

   public void GET_FOLLOWING_LIST(String endpoint, String user_id, String id, int pageNum){

       Log.d(TAG, "GET_FOLLOWING_LIST: " + 2);
        Call<List<Following>> CallFollowingList = requestApi.GET_FOLLOWINF_OR_FOLLOWER_LIST(endpoint, user_id, id, pageNum);
       Log.d(TAG, "GET_FOLLOWING_LIST: " + 3);
        CallFollowingList.enqueue(new Callback<List<Following>>() {
            @Override
            public void onResponse(Call<List<Following>> call, Response<List<Following>> response) {
                if(!response.isSuccessful()){
                    Log.d(TAG, "onResponse: " + response.message());
                    return;
                }

                List<Following> followingList = response.body();
                for (Following following : followingList) {
                    // 서버로 부터 받아온 팔로잉하고 있는 유저의 데이터 개수만큼 반복문을 돌려 하나하나의 팔로잉 하고 있는 유저의 데이터를 만든다.

                    // 하나의 팔로잉 유저 정보
                    likeListItemData likeListItemData = new likeListItemData();

                    likeListItemData.setUnique_id(following.getUser_id());
                    likeListItemData.setNickname(following.getNick_name()); // 팔로잉 유저의 닉네임
                    likeListItemData.setProfile(following.getProfile_img()); // 팔로잉 유저의 프로필 이미지 경로
                    likeListItemData.setIsFollowing(following.getFollowing()); // 팔로잉 유저의 필로잉 유무

                    // 팔로잉 유저정보를 해당 리스트에 추가한다.
                    snsfollowArrayList.add(likeListItemData);
                }

                // 서버로 부터 데이터 모두를 받고
                // 어뎁터에게 데이터가 추가된 사실을 알린다.
                followingAdpater.notifyDataSetChanged();


            }

            @Override
            public void onFailure(Call<List<Following>> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
   }
   // 생명주기


    @Override
    protected void onResume() {
        super.onResume();

        // 리사이클러뷰 스크롤 감지
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);


                // 밑 바닥에 스크롤이 닿았을 때(더 이상 아이템이 없을 경우)
                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {
                    pageNum = pageNum + 10;

                    if(tag.equals("following")){ // 팔로잉이라면
                        Log.d(TAG, "onScrollStateChanged: " + "end");
                        GET_FOLLOWING_LIST("following_list.php", user_id, id, pageNum);

                    }else if(tag.equals("follower")){ // 팔로워라면
                        GET_FOLLOWING_LIST("follower_list.php", user_id, id, pageNum);
                    }


                    Log.d("-----","end");
                }
            }
        });
    }

    // 메뉴 세팅
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

    @Override
    public void onRefresh() {
        snsfollowArrayList.clear(); // 데이터 지우고
        followingAdpater.notifyDataSetChanged(); // 갱신한다.

        pageNum = 1;
        if(tag.equals("following")){ // 팔로잉이라면
            GET_FOLLOWING_LIST("following_list.php", user_id, id, pageNum);
        }else if(tag.equals("follower")){ // 팔로워라면
            GET_FOLLOWING_LIST("follower_list.php", user_id, id, pageNum);
        }

        swipeRefreshLayout.setRefreshing(false);


    }
}
