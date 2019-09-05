package com.example.application.SNS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.application.IPclass;
import com.example.application.R;
import com.example.application.Retrofit2.Repo.GETS.SNS.COMMENT.comment_list;
import com.example.application.Retrofit2.Repo.GETS.SNS.post;
import com.example.application.Retrofit2.Repo.PostResult;
import com.example.application.Retrofit2.RequestApi;
import com.example.application.SNS.Adpater.SnsCommentParentAdapter;
import com.example.application.SNS.Class.comment;
import com.example.application.SNS.Class.parentComment;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 *  이전 액티비티으로 부터 게시물의 넘버를 가져 옵니다.
 *  서버로부터 가져오게 될 endpoint 는 총 2개이다.
 *  게시물의 넘버를 가져와 해당 게시물로 서버로부터
 *      프로필 이미지
 *      닉네임
 *      내용
 *      태그
 *      등록시간
 *  의 정보를 가져옵니다.
 *
 *
 */

public class SNSCommentActivity extends AppCompatActivity{
    private static final String TAG = "SNSCommentActivity";
    RequestApi requestApi;

    // 참조변수들
    CircleImageView post_profile_img;
    TextView post_nick_name;
    TextView post_content;
    TextView post_tag;
    TextView post_regi_time;
    CircleImageView post_comment_profile_img;
    EditText comment_txt;
    Button comment_txt_ok;



    // 게시물 번호를 가져오게 될 intent
    Intent intent;
    String post_num_; // 가져오게되는 게시물 번호
    String post_profile_; // 가져오는 프로필 이미지  url
    String post_id_; // 아이디
    String post_date_; // 게시물의 날짜
    String post_nickname_; // 닉네임
    String post_content_;
    String post_tag_; // 태그


    // RecyclerView 를 위한 변수
    RecyclerView recyclerView_comment_list; // 리사이클러뷰
    LinearLayoutManager linearLayoutManager; // 레이아웃 메니져
    private ArrayList<parentComment> parentCommentArrayList; // 댓글 데이터 리스트;
    SnsCommentParentAdapter snsCommentParentAdapter; // 어뎁터





    Toolbar toolbar; // 툴바
    TextView toolbar_title; // 툴바 타이틀






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snscomment);


        // 이전 게시물로 부터 받아오는 것들
        intent = getIntent();
        post_num_ = intent.getStringExtra("POST_NUM");
        post_profile_ = intent.getStringExtra("POST_PROFILE_IMG");
        post_nickname_ = intent.getStringExtra("POST_NICK_NAME");
        post_date_ = intent.getStringExtra("POST_DATE");
        post_content_ = intent.getStringExtra("POST_CONTENT");
        post_tag_ = intent.getStringExtra("POST_TAG");





        // Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + IPclass.IP_ADDRESS + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        requestApi = retrofit.create(RequestApi.class);




        // 툴바
        toolbar = findViewById(R.id.toolbar);
        toolbar_title = findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // 변수 초기화
        post_profile_img = findViewById(R.id.post_profile_img);
        post_nick_name = findViewById(R.id.post_nick_name);
        post_content = findViewById(R.id.post_content);
        post_tag = findViewById(R.id.post_tag);
        post_regi_time = findViewById(R.id.post_regi_time);
        post_comment_profile_img = findViewById(R.id.post_comment_profile_img);
        comment_txt = findViewById(R.id.comment_txt);
        comment_txt_ok = findViewById(R.id.comment_txt_ok);
        recyclerView_comment_list = findViewById(R.id.recyclerView_comment_list);


        comment_txt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 예외처리 1
                if(comment_txt.getText().toString().equals("")){ // 아무것도 입력 안되어있을 경우
                    Snackbar.make(view, "댓글을 입력해주세요", Snackbar.LENGTH_SHORT).show();
                    return;
                }


                Log.d(TAG, "onClick: 등록하기 버튼");
                POST_COMMENT("add.php");
            }
        });


        // 이전 게시물로 부터 받은 데이터를 뷰에 각각 넣어줌
        Glide.with(getApplicationContext()).load(post_profile_).into(post_profile_img); // 프로필 이미지
        Glide.with(getApplicationContext()).load(post_profile_).into(post_comment_profile_img); // 하단 이미지
        post_nick_name.setText(post_nickname_); // 닉네임
        post_content.setText(post_content_); // 내용
        post_regi_time.setText(post_date_); //시간
        post_tag.setText(post_tag_);// 태그


        // 리사이클러뷰 초기화 밑 데이터 세팅
        parentCommentArrayList = new ArrayList<>();
        // 이 사이에 데이터를 집어넣는다.

        // 서버로 부터 댓글 데이터를 가져온다.
        GET_COMMENT_LIST();


        snsCommentParentAdapter = new SnsCommentParentAdapter(parentCommentArrayList, getApplicationContext());
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView_comment_list.setAdapter(snsCommentParentAdapter); // 어뎁터 세팅
        recyclerView_comment_list.setLayoutManager(linearLayoutManager);
        recyclerView_comment_list.setHasFixedSize(true);

    }






    // ----------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //                                                                                  툴바 메뉴
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

    // -----------------------------------------------------------------------------------------------------------------------------
    //                                                      생명주기
    // -----------------------------------------------------------------------------------------------------------------------------

    @Override
    protected void onResume() {
        super.onResume();

        snsCommentParentAdapter.setItemClick(new SnsCommentParentAdapter.ItemClick() {
            @Override
            public void onClick(View view, int position, int id, SnsCommentParentAdapter.snsCommentViewHolder snsCommentViewHolder) {
                switch (id){
                    case 6: // 삭제

                        AlertDeletDialog(position);

                        Toast.makeText(getApplicationContext(), "삭제버튼 클릭 " + position, Toast.LENGTH_SHORT).show();

                        break;
                }
            }
        });
    }


//    // 클릭리스너
//    @Override
//    public void onClick(View view) {
//        switch (view.getId()){
//            case R.id.post_profile_img: // 프로필 이미지 클릭
//                break;
//            case R.id.post_nick_name: // 닉네임 클릭
//                break;
//            case R.id.comment_txt_ok: // 등록하기 버튼
//                Log.d(TAG, "onClick: 등록하기 버튼");
//                POST_COMMENT("add.php");
//                break;
//        }
//    }




    // -------------------------------------------------------------------------------------------------
    //                                              네트워크
    // -------------------------------------------------------------------------------------------------


    public void POST_COMMENT(String endpoint){

        SharedPreferences sharedPreferences = getSharedPreferences("file",  MODE_PRIVATE);
        String user_id = sharedPreferences.getString("id", "");


        HashMap<String, String> stringStringHashMap = new HashMap<>();

        Log.d(TAG, "POST_COMMENT: comment_txt.getText().toString() // " + comment_txt.getText().toString());
        Log.d(TAG, "POST_COMMENT: user_id // " + user_id);
        Log.d(TAG, "POST_COMMENT: post_num_ // " + post_num_);

        stringStringHashMap.put("content", comment_txt.getText().toString());
        stringStringHashMap.put("user_id", user_id);
        stringStringHashMap.put("post_num", post_num_);
        stringStringHashMap.put("is_child", "false");

        Call<PostResult> postResultCall = requestApi.SNS_POST_COMMENT(endpoint, stringStringHashMap);
        postResultCall.enqueue(new Callback<PostResult>() {
            @Override
            public void onResponse(Call<PostResult> call, Response<PostResult> response) {
                if(!response.isSuccessful()){
                    Log.d(TAG, "onResponse: //" + response.message());
                    return;
                }

                PostResult postResult = response.body();
                if(postResult.getResult().equals("success")){
                    Log.d(TAG, "onResponse: success? // " + response.message());
                }else if(postResult.getResult().equals("fail")){
                Log.d(TAG, "onResponse: fail? // " + response.message());
                    Log.d(TAG, "onResponse: fail?// " + response.body().getResult());
                    Log.d(TAG, "onResponse: fail " + response.raw());
                }
            }

            @Override
            public void onFailure(Call<PostResult> call, Throwable t) {
                Log.d(TAG, "onFailure: onFailure // " + t.getMessage());
            }

        });
    }

    public void GET_COMMENT_LIST(){
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("post_num", post_num_);

        Call<List<comment_list>> listCall = requestApi.SNS_GET_COMMENT_LIST_CALL(hashMap);
        listCall.enqueue(new Callback<List<comment_list>>() {
            @Override
            public void onResponse(Call<List<comment_list>> call, Response<List<comment_list>> response) {
                if(!response.isSuccessful()){
                    Log.d(TAG, "onResponse: " + response.body());
                    return;
                }


                List<comment_list> comment_lists = response.body();

                Log.d(TAG, "onResponse: " + response.body());
                Log.d(TAG, "onResponse: " + response.raw());

                for(comment_list commentList : comment_lists){

                    parentComment parentComment = new parentComment();
                    parentComment.setProfile(commentList.getProfile_img());
                    parentComment.setNickname(commentList.getNick_name());
                    parentComment.setContent(commentList.getContent());
                    parentComment.setDate(commentList.getDate());
                    parentComment.setUser_id(commentList.getUser_id());

                    ArrayList<comment> childCommentArrayList = new ArrayList<>();

                    if(commentList.getComment_lists() != null){


                        for(comment_list commentChild : commentList.getComment_lists()){ //  parent Comment 데이터 객체에 들어가는 childComment 리스트를 만든다

                            comment childComment = new comment();

                            childComment.setDate(commentChild.getDate()); // 댓글 시간
                            childComment.setContent(commentChild.getContent()); // 댓글 내용
                            childComment.setComment_id(commentChild.getId()); // 댓글 번호
                            childComment.setNickname(commentChild.getNick_name()); // 댓글 닉네임
                            childComment.setProfile(commentChild.getProfile_img());  // 댓글 프로필 이미지
                            childComment.setUser_id(commentChild.getUser_id()); // 댓글 사용자 유닉 번호
                            childComment.setParent_id(commentChild.getParent_comment_id()); // 댓글 부모 번호
                            childComment.setPostid(commentChild.getSns_post_id()); // 댓글이 소속된 게시물 번호



                            childCommentArrayList.add(childComment); // 대댓글 하나하나 추가 시킵니다.


                        }
                    }

                    // 추가를 모두 시킨 댓글을 부모 대댓글 데이터 리스트에 집어 넣습니다.
                    parentComment.setChildComments(childCommentArrayList);

                    parentCommentArrayList.add(parentComment);
                }

                snsCommentParentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<comment_list>> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }


    public void AlertDeletDialog(int position){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("댓글을 삭제합니다");
        builder.setPositiveButton(R.string.sns_comment_delete,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"예를 선택했습니다.",Toast.LENGTH_LONG).show();
                        parentCommentArrayList.remove(position);



                        snsCommentParentAdapter.notifyDataSetChanged(); //갱신

                    }
                });
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"아니오를 선택했습니다.",Toast.LENGTH_LONG).show();
                    }
                });
        builder.show();
    }

}
