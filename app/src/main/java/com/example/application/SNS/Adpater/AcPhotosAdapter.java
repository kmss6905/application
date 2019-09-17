package com.example.application.SNS.Adpater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.application.R;
import com.example.application.SNS.Class.ac_photo_item;

import java.util.ArrayList;

public class AcPhotosAdapter extends RecyclerView.Adapter<AcPhotosAdapter.PhotoViewHolder>{

    private ArrayList<ac_photo_item> ac_photo_itemArrayList;
    Context mContext;
    private AcPhotosAdapter.ItemClick itemClick;


    public interface ItemClick {
        public void onClick(View view, int position, int id);
    }



    //아이템 클릭시 실행 함수 등록 함수
    public void setItemClick(AcPhotosAdapter.ItemClick itemClick) {
        this.itemClick = itemClick;
    }



    public AcPhotosAdapter(ArrayList<ac_photo_item> ac_photo_itemArrayList, Context mContext) {
        this.ac_photo_itemArrayList = ac_photo_itemArrayList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ac_fr_sns_photo_item, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {



        if(ac_photo_itemArrayList.get(position).getIs_photos().equals("true")){ // 게시물의 여러개의 포토가 들어있는 경우 ?

            holder.is_photos.setVisibility(View.VISIBLE); // 여러개의 이미지라는 것을 알려줌

        }else{

            holder.is_photos.setVisibility(View.GONE);

        }


        Glide.with(mContext).load(ac_photo_itemArrayList.get(position).getPhoto_url())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(holder.photos_present); //이미지 넣음



        // 이미지를 클릭합니다.
        holder.photos_present.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemClick != null){
                    itemClick.onClick(view, position, 0);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return ac_photo_itemArrayList.size();
    }

    class PhotoViewHolder extends RecyclerView.ViewHolder{
        ImageView photos_present;
        ImageView is_photos;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            photos_present = itemView.findViewById(R.id.photos_present);
            is_photos = itemView.findViewById(R.id.is_photos);
        }
    }
}
