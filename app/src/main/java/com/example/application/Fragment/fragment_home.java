package com.example.application.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.util.LogTime;
import com.example.application.Adapter.AdapterLIVEitem;
import com.example.application.Adapter.AdapterVODitemMini;
import com.example.application.Broadcast.ViewerLiveBroadcastActivity;
import com.example.application.IPclass;
import com.example.application.ItemData.ItemLiveData;
import com.example.application.ItemData.ItemVodMiniData;
import com.example.application.Logg;
import com.example.application.R;
import com.example.application.Retrofit2.Repo.GETS.BROADCAST.LIVEINFO;
import com.example.application.Retrofit2.Repo.LivestreamInfo;
import com.example.application.Retrofit2.RequestApi;
import com.google.android.material.snackbar.Snackbar;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class fragment_home extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "fragment_home";

    // 레트로핏
    private RequestApi requestApi;


    // 리사이클러뷰를 위한 변수
    AdapterLIVEitem adapterLIVEitem;
    AdapterVODitemMini adapterVODitemMini;


    // 데이터 리스트
    ArrayList<ItemLiveData> itemLiveDataArrayList;
    ArrayList<ItemVodMiniData> itemVodMiniDataArrayList;

    // 리사이클러뷰들
    private RecyclerView recyclerView_hot_live_list;
    private RecyclerView recyclerView_hot_vod_list;


    // 레이아웃 참조 변수

    LinearLayout layout_hide_setting;


    // 새로고침
    SwipeRefreshLayout swipeRefreshLayout;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.layout_fragment_home, container, false);

        // 데이터 리스트
        itemLiveDataArrayList = new ArrayList<>();





                Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + IPclass.IP_ADDRESS + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        requestApi = retrofit.create(RequestApi.class);

        GET_LIVEINFO();


        // 새로고침
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLo);
        swipeRefreshLayout.setOnRefreshListener(this);




        recyclerView_hot_live_list = rootView.findViewById(R.id.recyclerView_hot_live_list);
        recyclerView_hot_live_list.setHasFixedSize(true); //??





        // 어뎁터 만들기
        adapterLIVEitem = new AdapterLIVEitem(itemLiveDataArrayList, getActivity());

        //리사이클러뷰에 어뎁터세팅, 레이아웃 매니져 세팅
        recyclerView_hot_live_list.setAdapter(adapterLIVEitem);
        recyclerView_hot_live_list.setLayoutManager(new LinearLayoutManager(getActivity()));


        Log.e(TAG, "onCreateView: HomeFragment");



        adapterLIVEitem.setItemClick(new AdapterLIVEitem.ItemClick() {
            @Override
            public void onClick(View view, final int position, int type) {
                switch (type){
                    case 1: // 방송보기
                        itemLiveDataArrayList.get(position).getLive_stream_user_pri_id(); // 스트리머의 user_pri_key 를 넘긴다.
                        Intent intent = new Intent(getActivity(), ViewerLiveBroadcastActivity.class);
                        intent.putExtra("STRAMER_PRIMARY_ID", itemLiveDataArrayList.get(position).getLive_stream_user_pri_id());
                        intent.putExtra("ROUTE_STREAM", itemLiveDataArrayList.get(position).getLive_stream_route_stream());
                        startActivity(intent);


                        break;
                    case 2: // 메뉴 클릭
                        PopupMenu popupMenu = new PopupMenu(getActivity(), view);
                        getActivity().getMenuInflater().inflate(R.menu.menu_broadcast, popupMenu.getMenu());

                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getItemId()){
                                    case R.id.menu_visit:
                                        Toast.makeText(getActivity(), "메뉴클릭 포지션: " + position + " / 방송국가기", Toast.LENGTH_SHORT).show();


                                        break;
                                    case R.id.menu_subscribe:
                                        Toast.makeText(getActivity(), "메뉴클릭 포지션: " + position + " / 구독하기", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                                return false;
                            }
                        });
                        popupMenu.show();


                        break;
                }
            }
        });


        return rootView;
    }













    //===================================================통신 메소드들=======================================


    // 라이브 리스트 가져오기
    public Boolean GET_LIVEINFO(){

        Log.i(TAG, "GET_LIVEINFO: 1");
        Call<List<LIVEINFO>> GET_LIVE_INFO_CALL = requestApi.GET_LIST_LIVE_STREAM_CALL();


        GET_LIVE_INFO_CALL.enqueue(new Callback<List<LIVEINFO>>() {
            @Override
            public void onResponse(Call<List<LIVEINFO>> call, Response<List<LIVEINFO>> response) {
                if(!response.isSuccessful()){
                    Log.i(TAG, "onResponse " + response.message());
                    return;
                }

                if(response.body().toString().equals("[null]")){
                    return;
                }

                Log.i(TAG, "GET_LIVEINFO: 2" + response.body().toString());
                List<LIVEINFO> liveinfos = response.body();
                Log.i(TAG, "GET_LIVEINFO: 3" + response.body().toString());




                for(LIVEINFO liveinfo : liveinfos){

                    ItemLiveData itemLiveData = new ItemLiveData();
                    itemLiveData.setLive_stream_title(liveinfo.getLive_stream_title());
                    System.out.println("확인 꺼내보자? 타이틀 : " + liveinfo.getLive_stream_title());
                    Log.i(TAG, "onResponse: liveinfo.getLive_stream_title() : " + liveinfo.getLive_stream_title());
                    itemLiveData.setLive_stream_tag(liveinfo.getLive_stream_tag());
                    System.out.println("확인 꺼내보자? 태그 : " + liveinfo.getLive_stream_tag());
                    Log.i(TAG, "onResponse: liveinfo.getLive_stream_tag(): " + liveinfo.getLive_stream_tag());
                    itemLiveData.setLive_stream_streamer_nick(liveinfo.getNick_name());
                    System.out.println("확인 꺼내보자? 이름 : " + liveinfo.getNick_name());
                    Log.i(TAG, "onResponse: liveinfo.getNick_name() : " + liveinfo.getNick_name());
                    itemLiveData.setLive_stream_route_stream(liveinfo.getLive_stream_route_stream());
                    System.out.println("확인 꺼내보자? 루트스트림 : " + liveinfo.getLive_stream_route_stream());
                    Log.i(TAG, "onResponse: liveinfo.getLive_stream_user_pri_id() : " + liveinfo.getLive_stream_user_pri_id());
                    itemLiveData.setLive_stream_user_pri_id(liveinfo.getLive_stream_user_pri_id());
                    System.out.println("확인 꺼내보자? 유닉아이디 : " + liveinfo.getLive_stream_user_pri_id());
                    itemLiveData.setLive_stream_viewer(liveinfo.getLive_stream_viewers());
                    System.out.println("확인 꺼내보자? 뷰어 : " + liveinfo.getLive_stream_viewers());


                    itemLiveDataArrayList.add(itemLiveData);
                    System.out.println("확인 사이즈" + itemLiveDataArrayList.size());
                }

                adapterLIVEitem.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<LIVEINFO>> call, Throwable t) {
                Log.i(TAG, "onFailure: " + t.getMessage());
            }
        });

        return false;
    }

    @Override
    public void onRefresh() {
        itemLiveDataArrayList.clear(); // 모든데이터를 지운다
        adapterLIVEitem.notifyDataSetChanged(); // 갱신한다.

        GET_LIVEINFO();
        swipeRefreshLayout.setRefreshing(false);
    }
}
