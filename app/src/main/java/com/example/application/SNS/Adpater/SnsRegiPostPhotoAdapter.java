package com.example.application.SNS.Adpater;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.application.Adapter.AdapterLIVEitem;
import com.example.application.R;
import com.example.application.SNS.Class.photo;

import java.util.ArrayList;

import retrofit2.http.Url;

public class SnsRegiPostPhotoAdapter extends RecyclerView.Adapter<SnsRegiPostPhotoAdapter.PhotoViewHolder> {
    private static final String TAG = "SnsRegiPostPhotoAdapter";

    private ArrayList<photo> photoArrayList;
    Context mContext;



    private ItemClick itemClick;


    public interface ItemClick{
        public void onClick(View view, int position, int i);
    }

    //아이템 클릭시 실행 함수 등록 함수
    public void setItemClick(ItemClick itemClick) {
        this.itemClick = itemClick;
    }



    public SnsRegiPostPhotoAdapter(ArrayList<photo> photoArrayList, Context mContext) {
        this.photoArrayList = photoArrayList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public SnsRegiPostPhotoAdapter.PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_photo_item, parent, false);
        return new PhotoViewHolder(view);
    }




    @Override
    public void onBindViewHolder(@NonNull SnsRegiPostPhotoAdapter.PhotoViewHolder holder, int position) {
        Log.i(TAG, "onBindViewHolder ");

        // 이미지 넣음
        Glide.with(mContext).load(photoArrayList.get(position).getUrl()).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(holder.photoImg);


        // 이미지 삭제 버튼
        holder.photoCancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemClick != null){
                    itemClick.onClick(view, position, 1);
                }
            }
        });


    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return photoArrayList.size();
    }


    // viewHolder

    class PhotoViewHolder extends RecyclerView.ViewHolder{
        ImageView photoImg;
        ImageButton photoCancleBtn;


        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);

            photoImg = itemView.findViewById(R.id.photoImg);
            photoCancleBtn = itemView.findViewById(R.id.photoCancleBtn);
        }
    }
}
