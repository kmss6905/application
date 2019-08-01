package com.example.application.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.application.ItemData.ItemLiveData;
import com.example.application.R;

import java.util.ArrayList;

public class AdapterLIVEitem extends RecyclerView.Adapter<AdapterLIVEitem.LiveItemViewHolder>{
    private static final String TAG = "AdapterLIVEitem";
    private ArrayList<ItemLiveData> itemLiveDataArrayList;
    Context mcontext;


    private ItemClick itemClick;


    public interface ItemClick{
        public void onClick(View view, int position);
    }

    //아이템 클릭시 실행 함수 등록 함수
    public void setItemClick(ItemClick itemClick) {
        this.itemClick = itemClick;
    }





    public AdapterLIVEitem(ArrayList<ItemLiveData> itemLiveDataArrayList, Context mcontext) {
        this.itemLiveDataArrayList = itemLiveDataArrayList;
        this.mcontext = mcontext;
    }



    @NonNull
    @Override
    public AdapterLIVEitem.LiveItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder");

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_live, parent, false);
        return new LiveItemViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull AdapterLIVEitem.LiveItemViewHolder holder, final int position) {
        Log.i(TAG, "onBindViewHolder ");

        holder.textView_title_liveStream.setText(itemLiveDataArrayList.get(position).getLive_stream_title());
        holder.textView_bj_nick_liveStream.setText(itemLiveDataArrayList.get(position).getLive_stream_streamer_nick());
        holder.textView_viewer_liveStream.setText(itemLiveDataArrayList.get(position).getLive_stream_viewer());
        holder.textView_tag_liveStream.setText(itemLiveDataArrayList.get(position).getLive_stream_tag());

//        "http://13.209.208.103:8086/transcoderthumbnail?application=live&streamname=" + eaca672afc494c1994adbb36f76490d2 + "&size=640x360&fitmode=letterbox";

//        // 썸네일 넣음
//        Glide.with(mcontext).load(
//                "http://13.209.208.103:8086/transcoderthumbnail?application=live&streamname="
//                        + itemLiveDataArrayList.get(position).getLive_stream_route_stream()
//                        + "&size=640x360&fitmode=letterbox").into(holder.imageView_thumbnail_liveStream);




        // 아이템 메뉴 클릭
        holder.btn_menu_liveStream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemClick != null){
                    itemClick.onClick(view, position);
                }
            }
        });


        // 방송 아이템 클릭
        holder.item_layout_liveStream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemClick != null){
                    itemClick.onClick(view, position);
                }
            }
        });





    }

    @Override
    public int getItemCount() {
        return itemLiveDataArrayList.size();
    }


    class LiveItemViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView_thumbnail_liveStream;
        TextView textView_title_liveStream;
        TextView textView_bj_nick_liveStream;
        TextView textView_tag_liveStream;
        TextView textView_viewer_liveStream;
        ImageButton btn_menu_liveStream;
        LinearLayout item_layout_liveStream;


        public LiveItemViewHolder(@NonNull View itemView) {
            super(itemView);

            item_layout_liveStream = itemView.findViewById(R.id.item_layout_liveStream);
            textView_title_liveStream = itemView.findViewById(R.id.textView_title_liveStream);
            textView_bj_nick_liveStream = itemView.findViewById(R.id.textView_bj_nick_liveStream);
            textView_viewer_liveStream = itemView.findViewById(R.id.textView_viewer_liveStream);
            textView_tag_liveStream = itemView.findViewById(R.id.textView_tag_liveStream);
            btn_menu_liveStream = itemView.findViewById(R.id.btn_menu_liveStream);
            imageView_thumbnail_liveStream = itemView.findViewById(R.id.imageView_thumbnail_liveStream);
        }
    }
}
