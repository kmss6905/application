package com.example.application.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.application.Adapter.AdapterLIVEitem;
import com.example.application.IPclass;
import com.example.application.Logg;
import com.example.application.Login_Sign.HomeActivitiy;
import com.example.application.MainActivity;
import com.example.application.R;
import com.example.application.Retrofit2.Repo.GETS.SNS.post;
import com.example.application.Retrofit2.Repo.GETS.USERS.USERINFO;
import com.example.application.Retrofit2.Repo.PostResult;
import com.example.application.Retrofit2.RequestApi;
import com.example.application.SNS.Adpater.SnsPostAdapter;
import com.example.application.SNS.Class.Snspost;
import com.example.application.SNS.LikeListActivity;
import com.example.application.SNS.SNSCommentActivity;
import com.example.application.SNS.SNSPostActicity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class fragment_map extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private static final String TAG = "fragment_map";

    SharedPreferences sharedPreferences;


    // 사용자 닉네임
    private String nickName;
    private String id;


    // 레트로핏
    private RequestApi requestApi;


    //리사이클러뷰를 위한 변수;
    private RecyclerView postRecyclerView;
    private SnsPostAdapter snsPostAdapter;



    //데이터리스트;
    private ArrayList<Snspost> postArrayList;


    // 새로고침
    SwipeRefreshLayout swipeRefreshLayout;


    public static fragment_map newInstance(){
        return new fragment_map();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true); // 메뉴
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.layout_fragment_map, container, false);


        getActivity().findViewById(R.id.toolbar).setVisibility(View.GONE);

        //뷰 세팅
        ImageButton sns_search = rootView.findViewById(R.id.sns_search); // 검색하기




        sharedPreferences = this.getActivity().getSharedPreferences("file", Context.MODE_PRIVATE);
        id = sharedPreferences.getString("id","");
        System.out.println("id  : " + id);


        // 새로고침
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);




        // 데이터 참조
        postArrayList = new ArrayList<>();



        // 레트로핏
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + IPclass.IP_ADDRESS + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        requestApi = retrofit.create(RequestApi.class);


        GET_SNS_POST_LIST("contents.php");
        GET_USER_INFO();





        // 어뎁터 만들기
        snsPostAdapter = new SnsPostAdapter(getActivity(), postArrayList);


        //리사이클러뷰에 어뎁터세팅, 레이아웃 매니져 세팅
        postRecyclerView = rootView.findViewById(R.id.postRecyclerView);
        postRecyclerView.setAdapter(snsPostAdapter);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        Log.e(TAG, "onCreateView: HomeFragment");






        return rootView;
    }
    // ============================================================= 생명주기 ================================================================

    @Override
    public void onResume() {
        super.onResume();
        snsPostAdapter.setItemClick(new SnsPostAdapter.ItemClick() {
            @Override
            public void onClick(View view, int position, int id, SnsPostAdapter.SnsPostViewHolder viewHolder) {
                switch (id){
                    case 0: // 내용더보기 레이아웃
                        break;
                    case 1: // 좋아요
                        Toast.makeText(getActivity(), "post num : " + postArrayList.get(position).getId() + " // 이전 : islike? : " +
                                postArrayList.get(position).getIslike() , Toast.LENGTH_SHORT).show();




                        if(postArrayList.get(position).getIslike()){ // 좋아요 누른 상태 일경우
                            // 클릭하면 좋아요 취소

                            postArrayList.get(position).setLikenum(String.valueOf(Integer.parseInt(postArrayList.get(position).getLikenum()) -1));
                            viewHolder.likeNum.setText(String.valueOf(Integer.parseInt(postArrayList.get(position).getLikenum())));

                            postArrayList.get(position).setIslike(false);

                            // 좋아요 취소
                            //서버 통신
                            POST_SNS_LIKE(postArrayList.get(position).getId(),"delete.php");
                            viewHolder.likeBtn.setColorFilter(ContextCompat.getColor(getActivity(), R.color.black));


                        }else{  // 좋아요 아닌 상태에서 누르면
                            postArrayList.get(position).setLikenum(String.valueOf(Integer.parseInt(postArrayList.get(position).getLikenum()) + 1));

                            viewHolder.likeNum.setText(String.valueOf(Integer.parseInt(postArrayList.get(position).getLikenum())));

                            postArrayList.get(position).setIslike(true);

                            // 서버 통신
                            POST_SNS_LIKE(postArrayList.get(position).getId(),"add.php");


                            viewHolder.likeBtn.setColorFilter(ContextCompat.getColor(getActivity(), R.color.red));
                        }

                        break;
                    case 2: // 댓글 더보기(상/하단)

                        // 보내야 하는 것
                        // 게시물 넘버, 사용자 유닉 아이디, 닉네임, 프로필이미지 경로, 내용, 태그, 등록시간
                        startActivity(new Intent(getActivity(), SNSCommentActivity.class)
                                .putExtra("POST_NUM",  postArrayList.get(position).getId())
                                .putExtra("POST_CONTENT", postArrayList.get(position).getContent())
                                .putExtra("POST_DATE", postArrayList.get(position).getDate())
                                .putExtra("POST_NICK_NAME",postArrayList.get(position).getUser_id())
                                .putExtra("POST_PROFILE_IMG", postArrayList.get(position).getProfileImgUrl())
                                .putExtra("POST_TAG", postArrayList.get(position).getTag()));

                        break;
                    case 3: // 닉네임 버튼( 해당 유저 방송국 이동), 닉네임 버튼 하단, 프로필 이미지 버튼
                        // 프래그 먼트 덮어씌움

                                fragment_account fragment_account = new fragment_account();
                                Bundle args = new Bundle();
                                args.putString("user_id", postArrayList.get(position).getUser_unic_id()); // 인텐트로 고유의 아이디 값을 넘김니다.
                                fragment_account.setArguments(args);
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment_account).commitAllowingStateLoss();




                        break;
                    case 7: //
                        break;
                    case 8: //정보 버튼
                        PopupMenu popupMenu = new PopupMenu(getActivity(), view);
                        getActivity().getMenuInflater().inflate(R.menu.sns_post_menu, popupMenu.getMenu());

                        // 만약 클릭했을 떄 그 게시물의 아이디(유닉) 현재 접속하고 있는 아이디(유닉) 가 같다면 수정/ 삭제 메뉴가 나올 수 있도록 한다
                        System.out.println("nickName " + nickName + " // postArrayList.get(position).getUser_id() : " + postArrayList.get(position).getUser_id());

                        if(nickName.equals(postArrayList.get(position).getUser_id())){ // 해당 게시물이 현재 접속하고 있는 닉네임과 같다?

                            popupMenu.getMenu().findItem(R.id.menu_sns_edit).setVisible(true); // 수정하기 버튼
                            popupMenu.getMenu().findItem(R.id.menu_sns_delete).setVisible(true); // 삭제 버튼 생김
                            popupMenu.getMenu().findItem(R.id.menu_sns_subscribe).setVisible(false); // 구독하기 버튼 숨김



                        }else{ // 다른 사람의 게시물인 경우
                            popupMenu.getMenu().findItem(R.id.menu_sns_edit).setVisible(false);
                            popupMenu.getMenu().findItem(R.id.menu_sns_delete).setVisible(false);
                            popupMenu.getMenu().findItem(R.id.menu_sns_subscribe).setVisible(true); // 구독하기 버튼 생김
                        }






                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getItemId()){

                                    case R.id.menu_sns_subscribe: // 팔로우
                                        Toast.makeText(getActivity(), "메뉴클릭 포지션: " + position + " / 팔로우", Toast.LENGTH_SHORT).show();
                                        break;

                                    case R.id.menu_sns_more_see_post: // 게시물 더보기
                                        Toast.makeText(getActivity(), "메뉴클릭 포지션: " + position + " / 게시물 더보기", Toast.LENGTH_SHORT).show();
                                        break;
                                    case R.id.menu_sns_see_broadcast: // 방송국 가기
                                    Toast.makeText(getActivity(), "메뉴클릭 포지션: " + position + " / 방송국 가기", Toast.LENGTH_SHORT).show();
                                        break;
                                    case R.id.menu_sns_edit: // 수정
                                        Intent intent = new Intent(getActivity(), SNSPostActicity.class);
                                        intent.putExtra("SNS_POST", postArrayList.get(position));
                                        startActivity(intent);


                                    Toast.makeText(getActivity(), "메뉴클릭 포지션: " + position + " / 수정", Toast.LENGTH_SHORT).show();
                                        break;

                                    case R.id.menu_sns_delete:
                                    Toast.makeText(getActivity(), "게시물 번호 / 삭제 : " + postArrayList.get(position).getId(), Toast.LENGTH_SHORT).show();

                                    // 네트워크 통신
                                        POST_SNS_DELETE(postArrayList.get(position).getId(), position); // 지워야할 게시물 번호를 서버로 전송한다.





                                        break;
                        }
                        return false;
                }
                        });
                        popupMenu.show();


                        break;


                    case 9: // 좋아요 더보기 버튼
                        Toast.makeText(getActivity(),"게시물 넘버 : " + postArrayList.get(position).getId(),Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), LikeListActivity.class);
                        intent.putExtra("POST_NUM", postArrayList.get(position).getId()); // string 입니다.
                        startActivity(intent);
                        break;

                }
            }
        });
    }


    // ============================================================= 네크워크 메소드 ================================================================

    public void GET_SNS_POST_LIST(String endpoint){


        Call<List<post>> snsPostList = requestApi.SNS_GET_POST_LIST(endpoint);
        System.out.println("실행1");

        snsPostList.enqueue(new Callback<List<post>>() {
            @Override
            public void onResponse(Call<List<post>> call, Response<List<post>> response) {
                if(!response.isSuccessful()){
                    Log.i(TAG, "onResponse:  " + response.message());
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

                            snspost.setUser_unic_id(post.getUser_id()); // 유저 고유 유닉 아이디
                            snspost.setUser_id(userinfo.getNick_name()); // 닉네임
                            System.out.println("실행7");
                            snspost.setProfileImgUrl(userinfo.getProfile_img()); // 프로필 이미지 경로
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
                    postRecyclerView.removeViewAt(position1);
                    snsPostAdapter.notifyItemRemoved(position1);
                    snsPostAdapter.notifyItemRangeChanged(position1, postArrayList.size());
                    snsPostAdapter.notifyDataSetChanged();


                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.detach(fragment_map.this).attach(fragment_map.this).commit();

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



    // 좋아요 체크
    public void POST_SNS_CHECK_LIKE(String post_id){
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("post_id", post_id);
        stringStringHashMap.put("id", id);

        Call<PostResult> postResultCall = requestApi.SNS_POST_LIKE_CHECK(stringStringHashMap);
        postResultCall.enqueue(new Callback<PostResult>() {
            @Override
            public void onResponse(Call<PostResult> call, Response<PostResult> response) {
                if(!response.isSuccessful()){
                    Log.d(TAG, "onResponse: " + response.message());
                    return;
                }

                PostResult postResult = response.body();
                if(postResult.getResult().equals("success")){

                }else {

                }


            }

            @Override
            public void onFailure(Call<PostResult> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    //게시물 댓글 수 가져오기
    public void GET_SNS_COMMENT_NUM(){

    }


    // 새로고침하기
    @Override
    public void onRefresh() {


        postArrayList.clear(); // 모든데이터를 지운다
        snsPostAdapter.notifyDataSetChanged(); // 갱신한다.

        GET_SNS_POST_LIST("contents.php");
        swipeRefreshLayout.setRefreshing(false);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_register, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
