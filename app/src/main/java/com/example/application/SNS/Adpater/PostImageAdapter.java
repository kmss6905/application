package com.example.application.SNS.Adpater;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.application.R;

import java.util.ArrayList;

import retrofit2.http.Url;

public class PostImageAdapter extends PagerAdapter {

    private ArrayList<String> uriArrayList;
    private Context mContext = null;

    //아이템 클릭시 실행 함수
    private PostImageAdapter.ItemClick itemClick;

    public interface ItemClick {
        public void onClick(View view,int position, ArrayList<String> uriArrayList);
    }



    //아이템 클릭시 실행 함수 등록 함수
    public void setItemClick(PostImageAdapter.ItemClick itemClick) {
        this.itemClick = itemClick;
    }



    public PostImageAdapter(ArrayList<String> uriArrayList, Context mContext) {
        this.uriArrayList = uriArrayList;
        this.mContext = mContext;
    }


    @Override
    public int getCount() {
        return uriArrayList.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = null;

        if(mContext != null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.add_photo_page_item, container, false);

            ImageView imageView = view.findViewById(R.id.postPhotoImgItem);
            // 들어온 데이터를 순서대로 넣자구 !!


            Glide.with(mContext).load(uriArrayList.get(position)).into(imageView);
        }

        container.addView(view); // 컨테이너에 뷰를 추가


        // 뷰페이저에 클릭 리스너를 등록하자
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemClick != null){
                    itemClick.onClick(view, position, uriArrayList);
                }
            }
        });


        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        // 뷰페이저 삭제
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == (View)object);
    }


}
