package com.example.application.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.application.IPclass;
import com.example.application.R;
import com.example.application.Retrofit2.Repo.GETS.SNS.post;
import com.example.application.Retrofit2.RequestApi;
import com.example.application.SNS.Adpater.AcPhotosAdapter;
import com.example.application.SNS.Class.ac_photo_item;
import com.example.application.SNS.SNSOnePostActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ac_fr_sns_photo extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private static final String TAG = "ac_fr_sns_photo";

    // 페이지 넘버
    // 리사이클러뷰 페이지 넘버
    private int pageNum = 1;


    // 새로고침
    SwipeRefreshLayout swipeRefreshLayout;


    // 쉐어드 저장된 아이디
    String id;


    //레트로핏
    RequestApi requestApi;

    // 리사이클러뷰 변수들
    RecyclerView photoListRecyclerview; // 리사이클러뷰
    AcPhotosAdapter acPhotosAdapter; // 어댑터
    public ArrayList<ac_photo_item> ac_photo_itemArrayList; // 데이터 리스트




    // 남의 계정을 통해 들어옴
    String hasIntentId;

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
        View view = inflater.inflate(R.layout.ac_fr_lsns_photo, container, false);


        // 뷰 초기화
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);



        // 리사이클러뷰 세팅
        ac_photo_itemArrayList = new ArrayList<>(); // 데이터 리스트
        photoListRecyclerview = view.findViewById(R.id.photoListRecyclerview); // 리사이클러뷰 참조
        acPhotosAdapter = new AcPhotosAdapter(ac_photo_itemArrayList, getActivity()); // 어뎁터 생성
//        gridLayoutManager = new GridLayoutManager(getActivity(),3); // 레이아웃 매니저 생성

        photoListRecyclerview.setAdapter(acPhotosAdapter);
        photoListRecyclerview.setLayoutManager(new GridLayoutManager(getActivity(),3));
        photoListRecyclerview.setHasFixedSize(true);




        //레트로핏 초기화 및 네트워크 통신 준비
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + IPclass.IP_ADDRESS + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        requestApi = retrofit.create(RequestApi.class);



        if(hasIntentId != null){  // 남의 계정
            GET_PHOTO(hasIntentId, pageNum);
        }else{ //나의 계정
            GET_PHOTO(id, pageNum);
        }



        return view;
    }


    // 생명주기 - resume
    @Override
    public void onResume() {
        super.onResume();

        acPhotosAdapter.setItemClick(new AcPhotosAdapter.ItemClick() {
            @Override
            public void onClick(View view, int position, int id) {
                switch (id){
                    case 0: // 사진을 클릭하면
                        Intent intent = new Intent(getActivity(), SNSOnePostActivity.class); // 사진에 해당하는 게시물을 볼 수 있는 액티비티로 이동
                        intent.putExtra("post_id", ac_photo_itemArrayList.get(position).getPost_id()); // 동시에 해당 사진이 속한 게시물의 게시물 번호를 넘김
                        startActivity(intent);
                        break;
                }
            }
        });


        // 페이징 , 리사이클러뷰 하단 감지
        // 리사이클러뷰 스크롤 감지
        photoListRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);


                // 밑 바닥에 스크롤이 닿았을 때(더 이상 아이템이 없을 경우)
                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {
                    pageNum = pageNum + 5;

                    // 서버로 부터 데이터를 4개 더 가져옴

                    if(hasIntentId != null){  // 남의 계정
                        GET_PHOTO(hasIntentId, pageNum);
                    }else{ //나의 계정
                        GET_PHOTO(id, pageNum);
                    }
                    Log.d("-----","end");
                }
            }
        });

    }

    public void GET_PHOTO(String id, int page){
        Call<List<post>> PhotoList = requestApi.SNS_GET_POST_LIST_AC("ac_contents.php", id, "true", page);

        PhotoList.enqueue(new Callback<List<post>>() {
            @Override
            public void onResponse(Call<List<post>> call, Response<List<post>> response) {
                if(!response.isSuccessful()){
                    Log.d(TAG, "onResponse: " + response.body());
                    return;
                }

                List<post> postList = response.body();

                // for 문 돌리면서 개수만큼 포토 데이터를 생성한다.
                for(post post: postList){
                    ac_photo_item ac_photo_item = new ac_photo_item(); // 하나의 포토 데이터(객체)

                    // 가져온 게시물의 사진이 여러개인지 확인
                    String photos[] = post.getPhoto_list().split(",");

                    if(photos.length > 1){ // 사진이 한 장 초과인경우
                        ac_photo_item.setIs_photos("true"); // 여러개의 포토가 아니라는 정보를 넣고
                        ac_photo_item.setPhoto_url(photos[0]); // 대표 사진을 보여줘야 하기 때문에 여러 사진 중 첫번째의 사진 경로를 집어 넣는다.
                    }else{ // 한개만 있는 경우
                        ac_photo_item.setIs_photos("false"); // 여러개의 포토라는 데이터를 넣는다.
                        ac_photo_item.setPhoto_url(photos[0]); // 대표 사진을 보여줘야 하기 때문에 여러 사진 중 첫번째의 사진 경로를 집어 넣는다.
                    }

                    ac_photo_item.setPost_id(post.getId()); // 고유의 게시물 번호
                    Log.d(TAG, "onResponse: 들어가는 포토 템 확인하기" + ac_photo_item.toString());

                    ac_photo_itemArrayList.add(ac_photo_item); // 만든 포토 데이터를 리스트에 집어넣고
                    Log.d(TAG, "onResponse: size : " + ac_photo_itemArrayList.size());
                }

                // for문을 다 돌려서 리스트에 집어 넣은후에는
                // 어뎁터에게 알려준다.
                acPhotosAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<List<post>> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });

    }


    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //                                                                                       새로고침
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void onRefresh() {

        ac_photo_itemArrayList.clear(); // 모든데이터를 지운다
        acPhotosAdapter.notifyDataSetChanged(); // 갱신한다.
        pageNum = 1; // 다시 1부터 10개까지 가져옵니다.
        if(hasIntentId != null){  // 남의 계정일 경우
            GET_PHOTO(hasIntentId, pageNum);
        }else{ //나의 계정일 경우
            GET_PHOTO(id, pageNum);
        }
        swipeRefreshLayout.setRefreshing(false);

    }

}
