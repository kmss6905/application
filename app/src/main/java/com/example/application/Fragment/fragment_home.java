package com.example.application.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.util.LogTime;
import com.example.application.Adapter.AdapterLIVEitem;
import com.example.application.Adapter.AdapterVODitemMini;
import com.example.application.IPclass;
import com.example.application.ItemData.ItemLiveData;
import com.example.application.ItemData.ItemVodMiniData;
import com.example.application.R;
import com.example.application.Retrofit2.Repo.LivestreamInfo;
import com.example.application.Retrofit2.RequestApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class fragment_home extends Fragment {
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







    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.layout_fragment_home, container, false);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + IPclass.IP_ADDRESS + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        requestApi = retrofit.create(RequestApi.class);




        recyclerView_hot_live_list = rootView.findViewById(R.id.recyclerView_hot_live_list);
        recyclerView_hot_live_list.setHasFixedSize(true);//??


        // 데이터 리스트
        itemLiveDataArrayList = new ArrayList<>();


        // 어뎁터 만들기
        adapterLIVEitem = new AdapterLIVEitem(itemLiveDataArrayList, getActivity());

        //리사이클러뷰에 어뎁터세팅, 레이아웃 매니져 세팅
        recyclerView_hot_live_list.setAdapter(adapterLIVEitem);
        recyclerView_hot_live_list.setLayoutManager(new LinearLayoutManager(getActivity()));


        Log.e(TAG, "onCreateView: HomeFragment");


        return rootView;
    }





    //===================================================통신 메소드들=======================================

    public void get_live_list(){
        Call<List<LivestreamInfo>> call = requestApi.LIVE_STREAM_CALL();

        call.enqueue(new Callback<List<LivestreamInfo>>() {
            @Override
            public void onResponse(Call<List<LivestreamInfo>> call, Response<List<LivestreamInfo>> response) {
                if(!response.isSuccessful()){
                    Log.e(TAG, "onResponse: " + response.message() );
                    return;
                }

                List<LivestreamInfo> livestreamInfos = response.body();

                for(LivestreamInfo livestreamInfo : livestreamInfos){
                    livestreamInfo.getLive_stream_title();
                    livestreamInfo.getNick_name();
                    livestreamInfo.getLive_stream_tag();
                    livestreamInfo.getLive_stream_url();
                }



            }

            @Override
            public void onFailure(Call<List<LivestreamInfo>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }


}
