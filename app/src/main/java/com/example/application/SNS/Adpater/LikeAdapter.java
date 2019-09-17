package com.example.application.SNS.Adpater;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.application.R;
import com.example.application.SNS.Class.likeListItemData;

import java.util.ArrayList;
import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;

public class LikeAdapter extends RecyclerView.Adapter<LikeAdapter.likeViewHolder> {
    private static final String TAG = "LikeAdapter";
    SharedPreferences sharedPreferences;


    private ArrayList<likeListItemData> likeListItemDataArrayList;
    private Context mContext;


    //아이템 클릭시 실행 함수
    private LikeAdapter.ItemClick itemClick;

    public interface ItemClick {
        public void onClick(View view, int position, int id, likeViewHolder likeViewHolder);
    }



    //아이템 클릭시 실행 함수 등록 함수
    public void setItemClick(LikeAdapter.ItemClick itemClick) {
        this.itemClick = itemClick;
    }


    public LikeAdapter(ArrayList<likeListItemData> likeListItemDataArrayList, Context mContext) {
        this.likeListItemDataArrayList = likeListItemDataArrayList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public likeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.like_list_item, parent, false);
        return new likeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull likeViewHolder holder, int position) {
        sharedPreferences = mContext.getSharedPreferences("file", Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("id", "");

        // 프로필 이미지
        Glide.with(mContext)
                .load(likeListItemDataArrayList.get(position).getProfile())
                .into(holder.profileImg);


        if(likeListItemDataArrayList.get(position).getUnique_id().equals(id)){ // 만약 본인이 클릭한다면?
            Log.d(TAG, "onBindViewHolder: " + likeListItemDataArrayList.get(position).toString());
            holder.followingBtn.setVisibility(View.GONE);
            holder.followBtn.setVisibility(View.GONE);
        }else{
            // 버튼
            if(likeListItemDataArrayList.get(position).getIsFollowing().equals("true")){
                holder.followingBtn.setVisibility(View.VISIBLE);
                holder.followBtn.setVisibility(View.GONE);
            }else{
                holder.followingBtn.setVisibility(View.GONE);
                holder.followBtn.setVisibility(View.VISIBLE);
            }
        }








        // 닉네임
        holder.nicknameText.setText(likeListItemDataArrayList.get(position).getNickname());



        holder.likelistitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemClick != null){
                    itemClick.onClick(view, position, 1, holder);
                }
            }
        });


        // 팔로우 하기 버튼
        holder.followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemClick != null){
                    itemClick.onClick(view, position, 2, holder);
                }
            }
        });


        // 팔로우잉 된 상태 -> 팔로우 취소 버튼
        holder.followingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemClick != null){
                    itemClick.onClick(view, position, 3, holder);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return likeListItemDataArrayList.size();
    }



    public class likeViewHolder extends RecyclerView.ViewHolder{
        CircleImageView profileImg;
        TextView nicknameText;
        public Button followBtn;
        public Button followingBtn;
        LinearLayout likelistitem;

        public likeViewHolder(@NonNull View itemView) {
            super(itemView);

            likelistitem = itemView.findViewById(R.id.likelistitem);
            profileImg = itemView.findViewById(R.id.profileImg);
            nicknameText = itemView.findViewById(R.id.nicknameText);
            followBtn = itemView.findViewById(R.id.followBtn);
            followingBtn = itemView.findViewById(R.id.followingBtn);
        }
    }
}
