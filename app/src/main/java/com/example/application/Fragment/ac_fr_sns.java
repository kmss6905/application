package com.example.application.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.application.IPclass;
import com.example.application.Logg;
import com.example.application.R;
import com.example.application.Retrofit2.Repo.GETS.SNS.post;
import com.example.application.Retrofit2.Repo.GETS.USERS.USERINFO;
import com.example.application.Retrofit2.Repo.PostResult;
import com.example.application.Retrofit2.RequestApi;
import com.example.application.SNS.Adpater.SnsCommentParentAdapter;
import com.example.application.SNS.Adpater.SnsPostAdapter;
import com.example.application.SNS.Class.Snspost;
import com.example.application.SNS.Class.ac_photo_item;
import com.example.application.SNS.Class.parentComment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class ac_fr_sns extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private static final String TAG = "ac_fr_sns";
    // 나의 게시물과 아닌 것 구분하는 녀석
    Intent intent;
    String hasIntentId;



    //레트로핏
    RequestApi requestApi;


    //쉐어드에 저장된 아이디
    String id;
    String nickName;


    // 새로고침
    SwipeRefreshLayout swipeRefreshLayout;


    // 가져올 게시물 수의 페이지 번호
    private int pageNum = 1;

    // 리사이클러뷰위한 변수들
    RecyclerView SnsRecyclerView;
    SnsPostAdapter snsPostAdapter;
    private ArrayList<Snspost> postArrayList;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) { // sns 게시물 닉네임 클릭시 이동
            hasIntentId = getArguments().getString("user_id"); // 다른 사람 계정을 클릭한 경우 해당 사용자의 유일한 아이디 가져오기 , 해당 아이디를 파라미터로 서버로 부터 해당 유저의 정보 가져옴
            Log.d(TAG, "onCreate: hasIntentId : "  + hasIntentId);
        }

        // 쉐어드에 저장된 아이디를 불러옴
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("file", Context.MODE_PRIVATE);
        id = sharedPreferences.getString("id", "");

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ac_fr_sns, container,false);
        // 쉐어드에 저장된 유저 아이디 가져오기
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("file", Context.MODE_PRIVATE);
        id = sharedPreferences.getString("id", "");




        // 레트로핏
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + IPclass.IP_ADDRESS + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        requestApi = retrofit.create(RequestApi.class);



        // 뷰 초기화
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        // 리사이클러뷰 세팅
        SnsRecyclerView = view.findViewById(R.id.SnsRecyclerView);
        postArrayList = new ArrayList<>(); // 아이템 들어갈 데이터 리스트
        snsPostAdapter = new SnsPostAdapter(getActivity(), postArrayList);
        SnsRecyclerView.setAdapter(snsPostAdapter); //어뎁터 세팅
        SnsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity())); // 레이아웃 메니저 세팅



        if(hasIntentId != null){ // 홈 게시글에서 sns 계정 클릭해서 들어왔을 경우 통신

            if(hasIntentId.equals(id)){ // 그런데 그 녀석이 내 계정이다?
                GET_SNS_POST_LIST("ac_contents.php", pageNum, id);
            }else{ // 내 계정이 아니다?
                GET_SNS_POST_LIST("ac_contents.php", pageNum, hasIntentId); // 상대방의 아이디에 맞는 게시물 찾음
            }

        }else{ // 홈 게시글이 아닌 바로 나의 계정 sns에서 들어갔을 경우
            GET_SNS_POST_LIST("ac_contents.php", pageNum, id);
        }




        return view;
    }



    // ------------------------------------------------------ 새로고침 ---------------------------------------------------
    // 데이터를 다시 가져옵니다.
    @Override
    public void onRefresh() {

        postArrayList.clear(); // 모든데이터를 지운다
        snsPostAdapter.notifyDataSetChanged(); // 갱신한다.

        pageNum = 1; // 다시 리셋

        if(hasIntentId != null){ // 홈 게시글에서 sns 계정 클릭해서 들어왔을 경우 통신

            if(hasIntentId.equals(id)){ // 그런데 그 녀석이 내 계정이다?
                GET_SNS_POST_LIST("ac_contents.php", pageNum, id);
            }else{ // 내 계정이 아니다?
                GET_SNS_POST_LIST("ac_contents.php", pageNum, hasIntentId); // 상대방의 아이디에 맞는 게시물 찾음
            }

        }else{ // 홈 게시글이 아닌 바로 나의 계정 sns에서 들어갔을 경우
            GET_SNS_POST_LIST("ac_contents.php", pageNum, id);
        }

        swipeRefreshLayout.setRefreshing(false);
    }


    // ============================================================================================================================
    //                                                              네트워크 통신 메서드
    // ============================================================================================================================

    /**
     *
     * @param endpoint
     * @param page
     * @param id
     */
    public void GET_SNS_POST_LIST(String endpoint, int page, String id){

//        @Path("endpoint") String endpoint,
//        @Query("user_id") String id,
//        @Query("photos") String is_photo_frg,
//        @Query("page_num") int page_num

        Call<List<post>> snsPostList = requestApi.SNS_GET_POST_LIST_AC(endpoint, id, "false", page);
        System.out.println("실행1");

        snsPostList.enqueue(new Callback<List<post>>() {
            @Override
            public void onResponse(Call<List<post>> call, Response<List<post>> response) {
                if(!response.isSuccessful()){
                    Log.d(TAG, "onResponse:  " + response.message());
                    System.out.println("실행2");
                    return;
                }

                if(response.body().toString().equals("[null]")){ // 없을 경우를 방지하자 뻑나면 안되니까!
                    return;
                }

                System.out.println("실행3");
                List<post> snsPostLists = response.body();
                System.out.println("실행4");



                for(post post : snsPostLists){

                    Log.d(TAG, "onResponse: (확인) post.toString() / " + post.toString());





                    // 좋아요 체크하기
                    HashMap<String, String> stringStringHashMap = new HashMap<>();
                    stringStringHashMap.put("post_id", post.getId());
                    stringStringHashMap.put("id", id);

                    System.out.println("poost.getId() : " + post.getId());
                    System.out.println("id : " + id);


                    System.out.println("실행5");
                    Snspost snspost = new Snspost();




                    // 작성자의 프로필이미지, 닉네임 가져오기
                    Call<USERINFO> userinfoCall = requestApi.GET_USER_INFO((post.getUser_id()));
                    userinfoCall.enqueue(new Callback<USERINFO>() {
                        @Override
                        public void onResponse(Call<USERINFO> call, Response<USERINFO> response) {
                            if(!response.isSuccessful()){
                                Log.i(TAG, "onResponse: " + response.message());
                                return;
                            }

                            USERINFO userinfo = response.body();
                            System.out.println("실행6");

                            Log.d(TAG, "(확인) onResponse: userInfoCall /" + userinfo.toString());

                            snspost.setUser_id(userinfo.getNick_name());
                            System.out.println("실행7");
                            snspost.setProfileImgUrl(userinfo.getProfile_img());
                            System.out.println("실행8");
                        }

                        @Override
                        public void onFailure(Call<USERINFO> call, Throwable t) {
                            Log.d(TAG, "onFailure: " + t.getMessage());

                        }
                    });



                    System.out.println("실행10");
                    Call<PostResult> postResultCall = requestApi.SNS_POST_LIKE_CHECK(stringStringHashMap);
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
                            if(postResult.getResult().equals("success")){
                                snspost.setIslike(true);
                                System.out.println("실행15");
                                System.out.println("result true");
                                System.out.println("실행16");
                            }else {
                                snspost.setIslike(false);
                                System.out.println("실행17");
                                System.out.println("result fail");
                            }
                        }

                        @Override
                        public void onFailure(Call<PostResult> call, Throwable t) {
                            Log.d(TAG, "onFailure: " + t.getMessage());
                            snspost.setIslike(false);
                            System.out.println("result complete fail");
                        }
                    });
                    System.out.println("실행18");


                    snspost.setIslike(false);
                    snspost.setId(post.getId());
                    snspost.setDate(post.getDate());
                    snspost.setAddress(post.getAddress());
                    snspost.setContent(post.getContent());
                    snspost.setLat(post.getLat());
                    snspost.setLon(post.getLon());
                    snspost.setLikenum(post.getLike());
                    snspost.setTag(post.getTag());


                    System.out.println("post.getId() : " + post.getId());



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
                                snspost.setCommentNum(postResult.getResult());
                            }


                        }

                        @Override
                        public void onFailure(Call<PostResult> call, Throwable t) {
                            Logg.i("==============================================testt===========================================");
                            Log.d(TAG, "onFailure: " + t.getMessage());
                            Logg.i("==============================================testt===========================================");
                        }
                    });

                    Logg.i("==============================================testt===========================================");




                    String[] str = post.getPhoto_list().split(",");

                    ArrayList<String> photoStringArrayList = new ArrayList<>();

                    for(int i= 0; i < str.length; i ++){
                        photoStringArrayList.add(str[i]);
                        System.out.println("실행 str[" + i + "] :" + str[i]);
                    }
                    snspost.setPhotoUriArrayList(photoStringArrayList); //사진 이미지


                    Log.d(TAG, "onResponse: snspost // " + snspost.toString() );


                    postArrayList.add(snspost);

                }


                snsPostAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<List<post>> call, Throwable t) {
                Log.i(TAG, "onFailure: " + t.getMessage());
            }
        });
    }



    public void GET_USER_INFO(){
        Log.i(TAG, "GET_USER_INFO ");
        // 닉네임 가져오기

        Call<USERINFO> GET_USER_INFO_CALL = requestApi.GET_USER_INFO(id);

        GET_USER_INFO_CALL.enqueue(new Callback<USERINFO>() {
            @Override
            public void onResponse(Call<USERINFO> call, Response<USERINFO> response) {
                if(!response.isSuccessful()){
                    Log.i(TAG, "onResponse " + response.message());
                    Log.i(TAG, "onResponse / code ; " + response.code());
                    return;
                }
                USERINFO userinfo = response.body();
                System.out.println("userinfo.getNick_name(); : " + userinfo.getNick_name());
                nickName = userinfo.getNick_name();


            }

            @Override
            public void onFailure(Call<USERINFO> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }


    public void POST_SNS_DELETE(String postId, Integer position1){


        Call<PostResult> postResultCall = requestApi.SNS_POST_DELETE_RESULT_CALL(postId);
        postResultCall.enqueue(new Callback<PostResult>() {
            @Override
            public void onResponse(Call<PostResult> call, Response<PostResult> response) {
                if(!response.isSuccessful()){
                    Log.d(TAG, "onResponse: " + response.message());
                    return;
                }

                PostResult postResult = response.body();
                if(postResult.getResult().equals("success")){
                    Toast.makeText(getActivity(), "성공적으로 삭제되었습니다.", Toast.LENGTH_SHORT).show();

                    postArrayList.remove(position1);
                    SnsRecyclerView.removeViewAt(position1);
                    snsPostAdapter.notifyItemRemoved(position1);
                    snsPostAdapter.notifyItemRangeChanged(position1, postArrayList.size());
                    snsPostAdapter.notifyDataSetChanged();


                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.detach(ac_fr_sns.this).attach(ac_fr_sns.this).commit();

                }else {
                    Toast.makeText(getActivity(), "게시물 삭제 실패", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<PostResult> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }


    /**
     *
     * @param post_id
     * @param endpoint add.php / delete.php
     */
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
                        Toast.makeText(getActivity(), "좋아요 누르셨습니다", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getActivity(), "좋아요를 취소하셨습니다", Toast.LENGTH_SHORT).show();
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

    // 생명주기

    @Override
    public void onResume() {
        super.onResume();

        // 리사이클러뷰 스크롤 감지
        SnsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);


                // 밑 바닥에 스크롤이 닿았을 때(더 이상 아이템이 없을 경우)
                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {
                    pageNum = pageNum + 5;

                    // 서버로 부터 데이터를 4개 더 가져옴
                    if(hasIntentId != null){ // 홈 게시글에서 sns 계정 클릭해서 들어왔을 경우 통신

                        if(hasIntentId.equals(id)){ // 그런데 그 녀석이 내 계정이다?
                            GET_SNS_POST_LIST("ac_contents.php", pageNum, id);
                        }else{ // 내 계정이 아니다?
                            GET_SNS_POST_LIST("ac_contents.php", pageNum, hasIntentId); // 상대방의 아이디에 맞는 게시물 찾음
                        }

                    }else{ // 홈 게시글이 아닌 바로 나의 계정 sns에서 들어갔을 경우
                        Log.d(TAG, "onScrollStateChanged: 홈 게시글이 아닌 바로 나의 계정 SNS에서 들어갔을 경우" );
                        GET_SNS_POST_LIST("ac_contents.php", pageNum, id);
                    }
                    Log.d("-----","end");
                }
            }
        });
    }
}
