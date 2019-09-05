package com.example.application.SNS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.application.IPclass;
import com.example.application.R;
import com.example.application.Retrofit2.Repo.GETS.SNS.Like;
import com.example.application.Retrofit2.Repo.PostResult;
import com.example.application.Retrofit2.RequestApi;
import com.example.application.SNS.Adpater.LikeAdapter;
import com.example.application.SNS.Class.likeListItemData;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LikeListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "LikeListActivity";


    // 툴바
    Toolbar toolbar;


    // 쉐어드
    SharedPreferences sharedPreferences;


    // 게시물 넘버 받아올 인텐트
    Intent intent;
    int postNum; //  게시물 번호
    String id; // 유닉 아이디

    // 새로고침
    private SwipeRefreshLayout swipeRefreshLayout;



    // 리사이클러뷰 변수
    private RecyclerView recyclerView; // 리사
    private LikeAdapter likeAdapter; // 어뎁터
    private LinearLayoutManager linearLayoutManager; // 레이아웃 매니져
    private ArrayList<likeListItemData> likeListItemDataArrayList; // 데이터 리스트


    // 프로그레스바
    private ProgressBar progressBar;



    // 리사이클러뷰 페이지 넘버
    private int pageNum = 1;




    RequestApi requestApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like_list);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("좋아요");


        //프로그레스바
        progressBar = findViewById(R.id.progress_bar);


        // 쉐어드에서 유닉 아이디를 가져옵니다.
        sharedPreferences = getSharedPreferences("file", MODE_PRIVATE);
        id = sharedPreferences.getString("id", "");


        //새로고침
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        // 레트로핏 객체 생성
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + IPclass.IP_ADDRESS + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        requestApi = retrofit.create(RequestApi.class);



        // 인텐트로 게시물 번호를 가져옴
        intent = getIntent();
        postNum = Integer.parseInt(intent.getStringExtra("POST_NUM")); // 게시물 번호 -> int로 바꾸어 주자
        Log.d(TAG, "onCreate: 게시물 번호 가져옴 : " + postNum);


        // 리사이클러뷰 관련 변수 초기화
        recyclerView = findViewById(R.id.recyclerview_likelist);
        progressBar = findViewById(R.id.progress_bar);

        likeListItemDataArrayList = new ArrayList<>(); // 데이터 리스트
        linearLayoutManager = new LinearLayoutManager(LikeListActivity.this); // 레이아웃 매니져
        likeAdapter = new LikeAdapter(likeListItemDataArrayList, LikeListActivity.this); // 어뎁터
        recyclerView.setAdapter(likeAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);



        CALL_LIKE_LIST(postNum, pageNum, id); // 처음에 10개만 불러옵니다.

    }

    // ============================================= 네트워크 메소드 ==============================================


    public void CALL_LIKE_LIST(int postNum, int page, String id){
        progressBar.setVisibility(View.VISIBLE);

        Call<List<Like>> likeListCall = requestApi.SNS_GET_LIKE_LIST_CALL(postNum, page, id);
        likeListCall.enqueue(new Callback<List<Like>>() {
            @Override
            public void onResponse(Call<List<Like>> call, Response<List<Like>> response) {
                if(!response.isSuccessful()){
                    Log.d(TAG, "onResponse: " + response.message());
                    return;
                }


                List<Like> likeList = response.body();


                for(Like like : likeList){
                    likeListItemData likeListItemData = new likeListItemData();


                    likeListItemData.setId(like.getId()); // post id
                    likeListItemData.setNickname(like.getNick_name());
                    likeListItemData.setProfile(like.getProfile_img());
                    likeListItemData.setIsFollowing(like.getIsFollowing());
                    likeListItemData.setUnique_id(like.getUser_id()); // unique id

                    Log.d(TAG, "onResponse: 서버에서 가져온 데이터\n" + like.toString());
                    

                    likeListItemDataArrayList.add(likeListItemData);


                    Log.d(TAG, "onResponse: 어레이에 넣었는지 확인\n" + likeListItemData.toString());
                }

                likeAdapter.notifyDataSetChanged();


                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<Like>> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
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
                        Snackbar.make(getWindow().getDecorView().getRootView(), "팔로잉 중입니다", Snackbar.LENGTH_SHORT).show();
                    }else if(endpoint.equals("delete.php")){
//                        Toast.makeText(getApplicationContext(),"팔로잉을 취소하였습니다.", Toast.LENGTH_SHORT).show();
                        Snackbar.make(getWindow().getDecorView().getRootView(), "팔로잉을 취소하였습니다.", Snackbar.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"서버통신 실패", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<PostResult> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });


    }


    // 새로고침 메소드
    @Override
    public void onRefresh() {

        likeListItemDataArrayList.clear(); // 모든데이터를 지운다
        likeAdapter.notifyDataSetChanged(); // 갱신한다.

        pageNum = 1; // 다시 리셋

        CALL_LIKE_LIST(postNum, 1, id);
        swipeRefreshLayout.setRefreshing(false);
    }



    // ================================================ 툴바 메뉴 ============================================

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



    //================================================== 생명주기 ==========================================
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
                    pageNum = pageNum + 20;
                    CALL_LIKE_LIST(postNum, pageNum, id); // 서버로 부터 데이터를 더 가져온다.
                    Log.d("-----","end");
                }
            }
        });



        // 클릭
        likeAdapter.setItemClick(new LikeAdapter.ItemClick() {
            @Override
            public void onClick(View view, int position, int id, LikeAdapter.likeViewHolder likeViewHolder) {
                SharedPreferences sharedPreferences = getSharedPreferences("file", MODE_PRIVATE);
                String Id = sharedPreferences.getString("id", "");


                switch (id){
                    case 1: // 닉네임 밑 프로필 클릭
                        break;


                    case 2: // 팔로우 하기 버튼 누르기
                        UPDATE_SUBSCRIBE("update.php", likeListItemDataArrayList.get(position).getUnique_id(), Id);
                        likeViewHolder.followingBtn.setVisibility(View.VISIBLE);
                        likeViewHolder.followBtn.setVisibility(View.GONE);
                        break;


                    case 3: // 팔로잉된상태임 -> 팔로잉 취소 버튼 누르기
                        UPDATE_SUBSCRIBE("delete.php", likeListItemDataArrayList.get(position).getUnique_id(), Id);
                        likeViewHolder.followBtn.setVisibility(View.VISIBLE);
                        likeViewHolder.followingBtn.setVisibility(View.GONE);
                        break;
                }
            }
        });
    }
}
