package com.example.application.SNS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.application.Broadcast.LiveBroadcastActivity;
import com.example.application.IPclass;
import com.example.application.Logg;
import com.example.application.R;
import com.example.application.Retrofit2.Repo.GETS.SNS.COMMENT.comment_list;
import com.example.application.Retrofit2.Repo.GETS.SNS.post;
import com.example.application.Retrofit2.Repo.GETS.USERS.USERINFO;
import com.example.application.Retrofit2.Repo.PostResult;
import com.example.application.Retrofit2.RequestApi;
import com.example.application.SNS.Adpater.SnsCommentParentAdapter;
import com.example.application.SNS.Class.comment;
import com.example.application.SNS.Class.parentComment;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.POST;


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
 */

public class SNSCommentActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    private static final String TAG = "SNSCommentActivity";
    RequestApi requestApi;


    // 새로고침
    SwipeRefreshLayout swipeRefreshLayout;



    // 참조변수들
    CircleImageView post_profile_img;
    TextView post_nick_name;
    TextView post_content;
    TextView post_tag;
    TextView post_regi_time;
    CircleImageView post_comment_profile_img;
    EditText comment_txt;
    Button comment_txt_ok;


    //대댓글 구분용 참조변수
    LinearLayout recomment_layout;  // 전체 대댓글 구분용 레이아웃 -> 나중에 이게 있냐 없냐 유무로 대댓글 판단함
    TextView text_recomment_target; // ... 님에게 답글을 작성중입니다. 의  ... 부분
    ImageButton cancel_recomment_btn; //  x 버튼 취소시 레이아웃 gone





    // 게시물 번호를 가져오게 될 intent
    Intent intent;
    public String post_num_; // 가져오게되는 게시물 번호
    public String post_profile_; // 가져오는 프로필 이미지  url
    public String post_id_; // 아이디
    String post_date_; // 게시물의 날짜
    public String post_nickname_; // 닉네임
    public String post_content_;
    String post_tag_; // 태그
    public String is_child = "false"; // 부모(댓글) 구분 변수


    // RecyclerView 를 위한 변수
    RecyclerView recyclerView_comment_list; // 리사이클러뷰
    LinearLayoutManager linearLayoutManager; // 레이아웃 메니져
    private ArrayList<parentComment> parentCommentArrayList; // 댓글 데이터 리스트;
    SnsCommentParentAdapter snsCommentParentAdapter; // 어뎁터



    Toolbar toolbar; // 툴바
    TextView toolbar_title; // 툴바 타이틀


    // 대댓글에 대한 정보를 담기위한 임시 객체
    comment comment;
    list list;


    // 사용자의 기본적인 정보를 가져와서 넣어줌
    String user_profile;
    String user_nick_name;
    String user_uni_id;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snscomment);

        comment = new comment();


        //대댓글 용 댓글정보 돌리기
        list = new list();

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


        //서버로 부터 사용자의 정보를 가져옵니다.
        GET_USER_INFO();




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
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);


        // 대댓글용 뷰 초기화
        recomment_layout = findViewById(R.id.recomment_layout);
        text_recomment_target = findViewById(R.id.text_recomment_target);
        cancel_recomment_btn = findViewById(R.id.cancel_recomment_btn);







        // 이전 게시물로 부터 받은 데이터를 뷰에 각각 넣어줌
        Glide.with(getApplicationContext()).load(post_profile_).into(post_profile_img); // 프로필 이미지


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
            public void onClick(View view, int position, int id, SnsCommentParentAdapter.snsCommentViewHolder snsCommentViewHolder, ArrayList<comment> comments) {
                switch (id){
                    case 1: // 편집 (대댓글)


                    case 2: // 삭제 (대댓글)

                        AlertRecommentDeleteDialog(position, comments);

                        Toast.makeText(getApplicationContext(), "삭제버튼 클릭 포지션 :  "+ position +"//"+ comments.size() , Toast.LENGTH_SHORT).show();
//                        comments.remove(position);
//                        snsCommentParentAdapter.notifyDataSetChanged();


                        break;

                    case 3: // 답글달기(대댓글)




                        Log.d(TAG, "onClick: " + snsCommentParentAdapter.getItemId(position));
                        Log.d(TAG, "onClick: " + snsCommentViewHolder.getItemId() + " 의 " );


                        comment = comments.get(position); // 대댓글의 데이터 리스트 이녀석이 어디에 속해있는지 확인하자
                        text_recomment_target.setText(comments.get(position).getNickname()); // ... 님에게 답글을 남기는 중입니다 의 ... 부분

                        recomment_layout.setVisibility(View.VISIBLE);
                        comment_txt.setText(" @" + comments.get(position).getNickname() + " "); //
                        comment_txt.setSelection(comment_txt.getText().length()); // 글자뒤에 커서 나둠

                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); // 사용자가 작성하기 쉽게 바로 소프트 키보드가 나오도록 함
                        if (imm != null) {
                            imm.showSoftInput(comment_txt, InputMethodManager.SHOW_FORCED);
                        }


                        list.setComments(comments);



                        break;


                    case 5: // 답글달기


                        if(comments.size() == 0){ // 아무것도 없는 상태 ( 이제 자식 댓글 추가해야하는 상태)


                            list.setComments(comments); // 넘기기용

                            comment.setPostid(post_num_);
                            comment.setParent_id(parentCommentArrayList.get(position).getComment_id());

                            Log.d(TAG, "onClick: 답글달기 클릭" + comment.toString());

                            text_recomment_target.setText(parentCommentArrayList.get(position).getNickname()); // ... 님에게 답글을 남기는 중입니다 의 ... 부분
                            recomment_layout.setVisibility(View.VISIBLE);


                            comment_txt.setText(" @" + parentCommentArrayList.get(position).getNickname() + " "); //
                            comment_txt.setSelection(comment_txt.getText().length()); // 글자뒤에 커서 나둠

                            InputMethodManager imm1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); // 사용자가 작성하기 쉽게 바로 소프트 키보드가 나오도록 함
                            if (imm1 != null) {
                                imm1.showSoftInput(comment_txt, InputMethodManager.SHOW_IMPLICIT);
                            }

                            break;
                            
                        }


                        list.setComments(comments); // 넘기기용
                        Log.d(TAG, "onClick: comments의 사이즈 : " + comments.size()
                                        + " / 포지션 : " + position);


                        comment.setPostid(post_id_);
                        comment.setParent_id(parentCommentArrayList.get(position).getComment_id());

                        Log.d(TAG, "onClick: " + parentCommentArrayList.get(position).toString());



                        text_recomment_target.setText(parentCommentArrayList.get(position).getNickname()); // ... 님에게 답글을 남기는 중입니다 의 ... 부분
                        recomment_layout.setVisibility(View.VISIBLE);


                        comment_txt.setText(" @" + parentCommentArrayList.get(position).getNickname() + " "); //
                        comment_txt.setSelection(comment_txt.getText().length()); // 글자뒤에 커서 나둠

                        InputMethodManager imm1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); // 사용자가 작성하기 쉽게 바로 소프트 키보드가 나오도록 함
                        if (imm1 != null) {
                            imm1.showSoftInput(comment_txt, InputMethodManager.SHOW_FORCED);
                        }

                        break;




                    case 6: // 삭제 데이터는 그대로 두되 정보만 지운다?


                        // 만약 대댓글이 없는 댓글이라면 바로 지운다.
                        if(parentCommentArrayList.get(position).getChildComments() == null || parentCommentArrayList.get(position).getChildComments().size() == 0){


                            // 클라 데이터 지우기 + 서버 데이터 삭제
                            AlertDeletDialog(position);




                        }else{ // 대댓글이 있는 경우면 블락 처리한다.


                            AlertDeletDialogWithChildComment(position);

                        }


                        break;

                    case 7: //댓글 편집
                        editComment(position);

                        break;


                    case 8: // 댓글 숨기기 or 보이기

                            snsCommentViewHolder.item_comment_parent_show_more_comment.setOnClickListener(new View.OnClickListener() {
                                @Override
                            public void onClick(View view) {

                                if(snsCommentViewHolder.item_comment_child_text.getText().toString().equals("답글 숨기기")){ //댓글창이 내려온 상태 -> 다시 가리기

                                    snsCommentViewHolder.item_comment_child_text.setText("답글 더보기"); // 답글더보기로 바꿈
                                    snsCommentViewHolder.recyclerView_comment_child.setVisibility(View.GONE);     // 라이사이클러뷰 닫고

                                }else{ // 댓글창이 올라가서 보여야하는 경우
                                    snsCommentViewHolder.item_comment_child_text.setText("답글 숨기기"); // 답글더보기로 바꿈
                                    snsCommentViewHolder.recyclerView_comment_child.setVisibility(View.VISIBLE);
                                }


                            }
                        });

                }
            }


        });


        // 댓글 작성 버튼
        comment_txt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences( "file", MODE_PRIVATE);

                // 예외처리 1
                if(comment_txt.getText().toString().equals("")){ // 아무것도 입력 안되어있을 경우
                    Snackbar.make(view, "댓글을 입력해주세요", Snackbar.LENGTH_SHORT).show();
                    return;
                }


                if(recomment_layout.getVisibility() == View.GONE){ // 댓글을 작성합니다.
                    //클라이언트 추가

                    SimpleDateFormat format1 = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss", Locale.KOREAN);
                    Date time = new Date();
                    String time1 = format1.format(time);
                    System.out.println(time1);


                    parentComment parent_comment = new parentComment();
                    parent_comment.setPost_id(post_num_);
                    parent_comment.setContent(comment_txt.getText().toString());
                    parent_comment.setUser_id(sharedPreferences.getString("id", ""));
                    parent_comment.setDate(time1);
                    parent_comment.setNickname(user_nick_name);
                    parent_comment.setProfile(user_profile);
                    parent_comment.setIs_delete("0");
                    parent_comment.setIs_edit("0");

                    parentCommentArrayList.add(parent_comment);
                    snsCommentParentAdapter.notifyDataSetChanged();


                    // 서버추가
                    POST_COMMENT("add.php");



                }else if(recomment_layout.getVisibility() == View.VISIBLE){ // 대댓글을 작성합니다.

                    if(comment != null){



                        comment.setNickname(user_nick_name);
                        comment.setProfile(user_profile);
                        comment.setPostid(post_id_);
                        comment.setContent(comment_txt.getText().toString());
                        comment.setUser_id(sharedPreferences.getString("id",""));

                        Log.d(TAG, "onClick: 대댓글에 들어가는 녀석 // " + comment.toString());

                        list.getComments().add(comment);
                        snsCommentParentAdapter.notifyDataSetChanged();





                        // 서버 추가
                        Log.d(TAG, "onClick: POST_RECOMMENT 의 파라미터 확인 : "
                                + "post_id_" + post_id_
                                + "/ post_nickname_  : " + post_nickname_
                                + " / post_profile_ : /" + post_profile_);


                        POST_RECOMMENT("add.php",list.getComments(), post_id_, post_nickname_, post_profile_);
                    }

                }




            }
        });



        // 대댓글 달지 않기 버튼
        cancel_recomment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                recomment_layout.setVisibility(View.GONE);
                Snackbar.make(view, "답글달기를 취소하였습니다", Snackbar.LENGTH_SHORT).show();
                comment_txt.setText("");
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
    public void GET_USER_INFO(){
        Log.i(TAG, "GET_USER_INFO ");

        SharedPreferences sharedPreferences = getSharedPreferences("file", MODE_PRIVATE);


        Call<USERINFO> GET_USER_INFO_CALL = requestApi.GET_USER_INFO(sharedPreferences.getString("id", ""));

        GET_USER_INFO_CALL.enqueue(new Callback<USERINFO>() {
            @Override
            public void onResponse(Call<USERINFO> call, Response<USERINFO> response) {
                if(!response.isSuccessful()){
                    Log.i(TAG, "onResponse " + response.message());
                    Log.i(TAG, "onResponse / code ; " + response.code());
                    return;
                }


                USERINFO userinfo = response.body();

                user_nick_name = userinfo.getNick_name();
                user_profile = userinfo.getProfile_img();


                Log.d(TAG, "onResponse: " + user_nick_name);
                Log.d(TAG, "onResponse: " + user_profile);

                Glide.with(getApplicationContext()).load(user_profile).into(post_comment_profile_img); // 하단 이미지 넣기



            }

            @Override
            public void onFailure(Call<USERINFO> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }


    public void POST_COMMENT_DELETE(int position){

        HashMap <String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("comment_id", parentCommentArrayList.get(position).getComment_id());

        Call<PostResult> postResultCall = requestApi.SNS_POST_COMMENT("delete.php", stringStringHashMap);
        postResultCall.enqueue(new Callback<PostResult>() {
            @Override
            public void onResponse(Call<PostResult> call, Response<PostResult> response) {
                if(!response.isSuccessful()){
                    Log.d(TAG, "onResponse: " + response.message());
                    return;
                }

                PostResult postResult = response.body();
                if(postResult.getResult().equals("success")){
                    Snackbar.make(getWindow().getDecorView().getRootView(), "댓글이 성공적으로 삭제되었습니다", Snackbar.LENGTH_SHORT).show();
                }else{
                    Snackbar.make(getWindow().getDecorView().getRootView(), "댓글이 삭제되지 않았습니다.", Snackbar.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<PostResult> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    public void POST_RECOMMENT_DELETE(int position, ArrayList<comment> comments){
        HashMap <String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("comment_id", comments.get(position).getComment_id());

        Call<PostResult> postResultCall = requestApi.SNS_POST_COMMENT("delete.php", stringStringHashMap);
        postResultCall.enqueue(new Callback<PostResult>() {
            @Override
            public void onResponse(Call<PostResult> call, Response<PostResult> response) {
                if(!response.isSuccessful()){
                    Log.d(TAG, "onResponse: " + response.message());
                    return;
                }

                PostResult postResult = response.body();
                if(postResult.getResult().equals("success")){
                    Snackbar.make(getWindow().getDecorView().getRootView(), "댓글이 성공적으로 삭제되었습니다", Snackbar.LENGTH_SHORT).show();
                }else{
                    Snackbar.make(getWindow().getDecorView().getRootView(), "댓글이 삭제되지 않았습니다.", Snackbar.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<PostResult> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }


    public void POST_COMMENT(String endpoint){

        SharedPreferences sharedPreferences = getSharedPreferences("file",  MODE_PRIVATE);
        String user_id = sharedPreferences.getString("id", "");


        HashMap <String, String> stringStringHashMap = new HashMap<>();
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

                    // 키보드를 다시 가립니다.
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(comment_txt.getWindowToken(), 0);

                    Log.d(TAG, "onResponse: success? // " + response.message());
                    comment_txt.setText("");
                    Snackbar.make(getWindow().getDecorView().getRootView(), "답글이 등록되었습니다", Snackbar.LENGTH_SHORT).show();

                    parentCommentArrayList.clear(); // 모든데이터를 지운다
                    snsCommentParentAdapter.notifyDataSetChanged(); // 갱신한다.
                    GET_COMMENT_LIST();



                    recyclerView_comment_list.scrollToPosition(parentCommentArrayList.size() - 1); // 맨 밑으로 이동


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




    public void POST_RECOMMENT(String endpoint, ArrayList<comment> comments, String post_id, String nickname, String profile){

        // 서버로 전송하기 전에 어떤 녀석이 왔는 지 확인하자
        Log.d(TAG, "POST_RECOMMENT: 서버전송 메소드로 넘어온 comment 의 정보 : " + comment.toString());



        SharedPreferences sharedPreferences = getSharedPreferences("file" , MODE_PRIVATE);
        String uni_id = sharedPreferences.getString("id", "");




        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("is_child", "true");
        stringStringHashMap.put("content", comment_txt.getText().toString());
        stringStringHashMap.put("post_num", post_num_);
        stringStringHashMap.put("user_id", uni_id);
        stringStringHashMap.put("parent_comment_id", comment.getParent_id());



        Call<PostResult> postResultCall = requestApi.SNS_POST_COMMENT(endpoint, stringStringHashMap);
        postResultCall.enqueue(new Callback<PostResult>() {
            @Override
            public void onResponse(Call<PostResult> call, Response<PostResult> response) {
                if(!response.isSuccessful()){
                    Log.d(TAG, "onResponse: " + response.message());
                    return;
                }

                PostResult postResult = response.body();

                if(postResult.getResult().equals("success")){
                    Log.d(TAG, "onResponse: " + postResult.getResult());


                    // 키보드를 다시 가립니다.
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(comment_txt.getWindowToken(), 0);


                    comment_txt.setText("");
                    Snackbar.make(getWindow().getDecorView().getRootView(), "답글이 등록되었습니다", Snackbar.LENGTH_SHORT).show();
                    if(recomment_layout.getVisibility() == View.VISIBLE){ // 대댓글 안내창 켜져 있다면
                        recomment_layout.setVisibility(View.GONE); // 안내창 끈다
                    }



                }else{
                    Log.d(TAG, "onResponse: " + postResult.getResult());
                }

                Log.d(TAG, "onResponse: " + postResult.getResult());

            }

            @Override
            public void onFailure(Call<PostResult> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }

        });

    }



    // 모든 댓글 리스트가져오기
    public void GET_COMMENT_LIST(){

        // 게시글 해쉬맵
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("post_num", post_num_);


        // 통신
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
                    parentComment.setPost_id(commentList.getSns_post_id());
                    parentComment.setComment_id(commentList.getId());
                    parentComment.setIs_edit(commentList.getIs_edit());
                    parentComment.setIs_delete(commentList.getIs_delete());

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

    // 부모 댓글 수정하기
    public void EDIT_COMMENT(int position){
        HashMap<String, String> Edit_Comment_HashMap = new HashMap<>();


        Edit_Comment_HashMap.put("post_id", parentCommentArrayList.get(position).getPost_id());
        Edit_Comment_HashMap.put("content", parentCommentArrayList.get(position).getContent());
        Edit_Comment_HashMap.put("comment_id", parentCommentArrayList.get(position).getComment_id());



        Call<PostResult> postResultCall = requestApi.SNS_POST_COMMENT("update.php", Edit_Comment_HashMap);
        postResultCall.enqueue(new Callback<PostResult>() {
            @Override
            public void onResponse(Call<PostResult> call, Response<PostResult> response) {
                if(!response.isSuccessful()){
                    Log.d(TAG, "onResponse: " + response.message());
                    return;
                }

                PostResult postResult = response.body();
                if(postResult.getResult().equals("success")){
                    Snackbar.make(getWindow().getDecorView().getRootView(), "댓글이 수정되었습니다", Snackbar.LENGTH_SHORT).show();
                }else{
                    Snackbar.make(getWindow().getDecorView().getRootView(), "수정이 실패", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PostResult> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });



    }


    // 부모 댓글 삭제하기(자식있는 경우)
    public void SET_IS_DELETE(int position){
        HashMap<String, String> Edit_Comment_HashMap = new HashMap<>();


        Edit_Comment_HashMap.put("post_id", parentCommentArrayList.get(position).getPost_id());
        Edit_Comment_HashMap.put("content", parentCommentArrayList.get(position).getContent());
        Edit_Comment_HashMap.put("comment_id", parentCommentArrayList.get(position).getComment_id());



        Call<PostResult> postResultCall = requestApi.SNS_POST_COMMENT("isdelete.php", Edit_Comment_HashMap);
        postResultCall.enqueue(new Callback<PostResult>() {
            @Override
            public void onResponse(Call<PostResult> call, Response<PostResult> response) {
                if(!response.isSuccessful()){
                    Log.d(TAG, "onResponse: " + response.message());
                    return;
                }

                PostResult postResult = response.body();
                if(postResult.getResult().equals("success")){
                    Snackbar.make(getWindow().getDecorView().getRootView(), "댓글이 삭제되었습니다.", Snackbar.LENGTH_SHORT).show();
                }else{
                    Snackbar.make(getWindow().getDecorView().getRootView(), "댓글 삭제 실패", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PostResult> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });



    }


    // 자식 댓글 수정하기
    public void EDIT_REOMMENT(){

    }


    public void AlertDeletDialog(int position){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("댓글을 삭제합니다");
        builder.setPositiveButton(R.string.sns_comment_delete,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                        // 서버
                        POST_COMMENT_DELETE(position);

                        // 클라
                        parentCommentArrayList.remove(position);
                        snsCommentParentAdapter.notifyDataSetChanged(); //갱신





                    }
                });
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.show();
    }



    public void AlertDeletDialogWithChildComment(int position){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("댓글을 삭제합니다");
        builder.setPositiveButton(R.string.sns_comment_delete,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                        // 서버
                        SET_IS_DELETE(position);

                        // 클라
                        parentCommentArrayList.get(position).setIs_delete("1");
                        snsCommentParentAdapter.notifyDataSetChanged(); //갱신

                    }
                });
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.show();
    }


    // 대댓글 지우기
    // 서버통신 및 클라에서 데이터 지우기
    public void AlertRecommentDeleteDialog(int position, ArrayList<comment> comment){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("댓글을 삭제합니다");
        builder.setPositiveButton(R.string.sns_comment_delete,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                        // 서버
                        POST_RECOMMENT_DELETE(position, comment);

                        // 클라
                        comment.remove(position);
                        snsCommentParentAdapter.notifyDataSetChanged(); // 갱신

                    }
                });
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.show();
    }



    // 새로고침 매소드
    @Override
    public void onRefresh() {


        parentCommentArrayList.clear(); // 모든데이터를 지운다
        snsCommentParentAdapter.notifyDataSetChanged(); // 갱신한다.
        GET_COMMENT_LIST();
        swipeRefreshLayout.setRefreshing(false);
    }





    // 방송 수정
    void editComment(int position){



        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_comment_dialog_layout, null);
        builder.setView(view);
        final Button btn_change_edit_cancel = (Button) view.findViewById(R.id.btn_change_tag_cancel);
        final Button btn_change_edit_ok = (Button) view.findViewById(R.id.btn_change_tag_ok);
        final EditText editText_edit = view.findViewById(R.id.editText_tag);

        editText_edit.setSelection(editText_edit.length());
        editText_edit.setText(parentCommentArrayList.get(position).getContent());
        editText_edit.setSelectAllOnFocus(true); //태그 전체 선택

        // 바로 뜨도록 함
        editText_edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                editText_edit.post(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager inputMethodManager= (InputMethodManager) SNSCommentActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(editText_edit, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
            }
        });
        editText_edit.requestFocus();


        final AlertDialog dialog = builder.create();





        // (다이얼로그) 태그 수정 확인 버튼
        btn_change_edit_ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // 수정된 시간을 다시 집어넣습니다.
                SimpleDateFormat format1 = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss", Locale.KOREAN);
                Date time = new Date();
                String time1 = format1.format(time);
                System.out.println(time1);



                // 클라이언트 데이터 수정
                parentCommentArrayList.get(position).setContent(editText_edit.getText().toString());
                parentCommentArrayList.get(position).setDate(time1 + "(수정)");


                //서버데이터 수정
                EDIT_COMMENT(position);


                //클라데이터 수정
                snsCommentParentAdapter.notifyItemChanged(position);







//                Toast.makeText(getApplicationContext(), "여행 태그가 추가되었습니다.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });


        // (다이얼로그) 타이틀 수정 취소버튼
        btn_change_edit_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        Window window = dialog.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }



    public class list{
        ArrayList<comment> comments;
        comment comment;



        public com.example.application.SNS.Class.comment getComment() {
            return comment;
        }

        public void setComment(com.example.application.SNS.Class.comment comment) {
            this.comment = comment;
        }

        public ArrayList<com.example.application.SNS.Class.comment> getComments() {
            return comments;
        }

        public void setComments(ArrayList<com.example.application.SNS.Class.comment> comments) {
            this.comments = comments;
        }
    }


}
