package com.example.application.SNS.Adpater;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.application.IPclass;
import com.example.application.R;
import com.example.application.Retrofit2.Repo.PostResult;
import com.example.application.Retrofit2.RequestApi;
import com.example.application.SNS.Class.Snspost;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SnsPostAdapter extends RecyclerView.Adapter<SnsPostAdapter.SnsPostViewHolder>{



    private static final String TAG = "SnsPostAdapter";
    private PostImageAdapter postImageAdapter;

    private Context mContext;
    private ArrayList<Snspost> snspostArrayList;

    //아이템 클릭시 실행 함수
    private SnsPostAdapter.ItemClick itemClick;

    public interface ItemClick {
        public void onClick(View view, int position, int id, SnsPostViewHolder SnsPostViewHolder);
    }



    //아이템 클릭시 실행 함수 등록 함수
    public void setItemClick(SnsPostAdapter.ItemClick itemClick) {
        this.itemClick = itemClick;
    }




    public SnsPostAdapter(Context mContext, ArrayList<Snspost> snspostArrayList) {
        this.mContext = mContext;
        this.snspostArrayList = snspostArrayList;
    }



    @NonNull
    @Override
    public SnsPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder: ");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sns_post_item, parent, false);
        return new SnsPostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SnsPostViewHolder holder, final int position) {

//        holder.setIsRecyclable(false);
        System.out.println("snspostArrayList.get(position).getIslike() : " + snspostArrayList.get(position).getIslike());

        if(snspostArrayList.get(position).getIslike()){ // like 되어 있는 상태일 경우
            holder.likeBtn.setColorFilter(ContextCompat.getColor(mContext, R.color.red));
        }else if(!snspostArrayList.get(position).getIslike()){
            holder.likeBtn.setColorFilter(ContextCompat.getColor(mContext, R.color.black));
        }


        Log.d(TAG, "onBindViewHolder: snspostArrayList.get(" + position + ").getCommentNum() : "  + snspostArrayList.get(position).getCommentNum());


        if(snspostArrayList.get(position).getCommentNum() == null || snspostArrayList.get(position).getCommentNum().equals("0")){
            holder.postCommentMoreSeeBtn.setVisibility(View.GONE); // 개수가 0인경우에는 아예보여주지 않음
            Log.d(TAG, "onBindViewHolder: " + "댓글 보여주지않음");
        }else{
            holder.postCommentMoreSeeBtn.setVisibility(View.VISIBLE);
            holder.postCommentNumText.setText(snspostArrayList.get(position).getCommentNum()); // 댓글 수
            Log.d(TAG, "onBindViewHolder: " + "댓글 보여줌");
        }



        holder.nicknameText.setText(snspostArrayList.get(position).getUser_id()); // 닉네임 1 (상단)
        holder.nicknameText2.setText(snspostArrayList.get(position).getUser_id()); // 닉네임 2(하단)
        holder.locationText.setText(snspostArrayList.get(position).getAddress()); // 주소
        holder.likeNum.setText(snspostArrayList.get(position).getLikenum()); // 좋아요 수
        holder.postDateText.setText(snspostArrayList.get(position).getDate()); // 등록 일시
        holder.contentText.setText(snspostArrayList.get(position).getContent()); // 짧은 내용
        holder.moreContentText.setText(snspostArrayList.get(position).getContent()); // 긴 내용
        holder.tagText.setText(snspostArrayList.get(position).getTag()); // 태그


        Glide.with(mContext).load(snspostArrayList.get(position).getProfileImgUrl()).into(holder.profileImg); // 프로필 이미지




        // 내용 더보기
        holder.moreContentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemClick != null){
                    holder.moreContentLayout.setVisibility(View.VISIBLE);
                    holder.contentText.setVisibility(View.GONE);
                    itemClick.onClick(view, position, 0, holder);
        }

    }
        });

        //좋아요 버튼
        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemClick != null){
                    itemClick.onClick(view, position, 1, holder);
                }
            }
        });

        //댓글 더보기 버튼(하단)
        holder.postCommentMoreSeeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemClick != null){
                    itemClick.onClick(view, position, 2, holder);
                }
            }
        });

        // 댓글 더보기 버튼(상단)

        holder.moreChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemClick != null){
                    itemClick.onClick(view, position, 2, holder);
                }
            }
        });

        // 장소 버튼


        //닉네임 버튼 (해당 유저 방송국 이동)
        holder.nicknameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemClick != null){
                    itemClick.onClick(view, position, 3, holder);
                }
            }
        });

        // 닉네임 버튼 (하단) ( 해당 유저 방송국 이동)
        holder.nicknameText2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemClick != null){
                    itemClick.onClick(view, position, 3, holder);
                }
            }
        });


        //프로필 이미지 버튼 ( 해당 유저 방송국 이동)
        holder.profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemClick != null){
                    itemClick.onClick(view, position, 3, holder);
                }
            }
        });

        // 메뉴 버튼 클릭
        holder.moreText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemClick != null){
                    itemClick.onClick(view, position, 7, holder);
                }
            }
        });


        holder.postInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemClick != null){
                    itemClick.onClick(view,position,8,holder);
                }
            }
        });

        holder.moreLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemClick != null){
                    itemClick.onClick(view,position,9,holder);
                }
            }
        });


        if(snspostArrayList.get(position).getProfileImgUrl() != null) {
            holder.profileImg.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);
        }else{
            Glide.with(mContext).load(snspostArrayList.get(position).getProfileImgUrl()).into(holder.profileImg); // 프로필 이미지
        }







            // 뷰페이져에 데이터 세팅하기(어레이 리스트로) 뷰페이져 어뎁터에 데이터를 넣는다.
            postImageAdapter = new PostImageAdapter(snspostArrayList.get(position).getPhotoUriArrayList(), mContext);
            for(int i=0; i < snspostArrayList.get(position).getPhotoUriArrayList().size(); i ++){

                System.out.println("실행 포지션 " + position + "의 뷰페이져 이미지 어레이 사이즈 " +
                        snspostArrayList.get(position).getPhotoUriArrayList().size() +" : //이고 그 어레이의 " + i + " 번째 의 경로"
                        + snspostArrayList.get(position).getPhotoUriArrayList().get(i));
            }

            if(snspostArrayList.get(position).getPhotoUriArrayList().size() == 1){ //이미지의 개수가 0인 경우에는 인디케이터를 달지 않는다
                holder.viewPager.setAdapter(postImageAdapter);
                holder.photoNumLayout.setVisibility(View.GONE);
                holder.circleIndicator.setVisibility(View.GONE);
        }else{ // 이미지의 개수가 1이상인 경우에는 인디케이터를 달아 준다.
            holder.viewPager.setAdapter(postImageAdapter);
                holder.photoNumLayout.setVisibility(View.VISIBLE);
                holder.circleIndicator.setVisibility(View.VISIBLE);
            holder.totalPhotoNumText.setText("/" + snspostArrayList.get(position).getPhotoUriArrayList().size());
            // 뷰페이져 페이지 체인지 리스너





            holder.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    Log.d(TAG, "onPageScrolled: " + position);
                }

                @Override
                public void onPageSelected(int position) {
                    Log.d(TAG, "onPageSelected: " + position + 1);
                    holder.nowPhotoNumText.setText(String.valueOf(position+1));
                    Log.d(TAG, "onPageSelected: holder.nowPhotoNumText 의 포지션" + position + 1);
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    Log.d(TAG, "onPageScrollStateChanged: " + state);
                }
            });

            holder.circleIndicator.setViewPager(holder.viewPager);
            System.out.println("holder : " + holder + " 가 파괴됨? " + holder.isRecyclable());


        }


    }

    @Override
    public int getItemCount() {
        return snspostArrayList.size();
    }

    public class SnsPostViewHolder extends RecyclerView.ViewHolder{
        ImageView profileImg;
        TextView nicknameText;
        TextView locationText;
        ImageButton postInfoBtn;
        LinearLayout photoNumLayout;
        TextView nowPhotoNumText;
        TextView totalPhotoNumText;
        ViewPager viewPager;
        public ImageButton likeBtn;
        ImageButton moreChatBtn;
        CircleIndicator indicator;
        public TextView likeNum;
        TextView nicknameText2;
        TextView contentText;
        public TextView moreText;
        LinearLayout moreContentLayout;
        TextView moreContentText;
        LinearLayout postCommentMoreSeeBtn;
        TextView postCommentNumText;
        TextView postDateText;
        TextView tagText;
        CircleIndicator circleIndicator;
        LinearLayout moreLike;



        public SnsPostViewHolder(@NonNull View itemView) {
            super(itemView);
            moreLike = itemView.findViewById(R.id.moreLike);
            profileImg = itemView.findViewById(R.id.profileImg);
            nicknameText  = itemView.findViewById(R.id.nicknameText);
            locationText = itemView.findViewById(R.id.locationText);
            postInfoBtn = itemView.findViewById(R.id.postInfoBtn);
            photoNumLayout = itemView.findViewById(R.id.photoNumLayout);
            nowPhotoNumText = itemView.findViewById(R.id.nowPhotoNumText);
            totalPhotoNumText = itemView.findViewById(R.id.totalPhotoNumText);
            viewPager = itemView.findViewById(R.id.viewPager);
            likeBtn  = itemView.findViewById(R.id.likeBtn);
            moreChatBtn = itemView.findViewById(R.id.moreChatBtn);
            indicator = itemView.findViewById(R.id.indicator);
            likeNum = itemView.findViewById(R.id.likeNum);
            nicknameText2 = itemView.findViewById(R.id.nicknameText2);
            contentText = itemView.findViewById(R.id.contentText);
            moreText = itemView.findViewById(R.id.moreBtn);
            moreContentLayout = itemView.findViewById(R.id.moreContentLayout);
            moreContentText = itemView.findViewById(R.id.moreContentText);
            postCommentMoreSeeBtn = itemView.findViewById(R.id.postCommentMoreSeeBtn);
            postCommentNumText = itemView.findViewById(R.id.postCommentNumText);
            postDateText = itemView.findViewById(R.id.postDateText);
            tagText = itemView.findViewById(R.id.tagText);
            circleIndicator = itemView.findViewById(R.id.indicator);

        }
    }

    public void POST_SNS_CHECK_LIKE(String post_id, SnsPostViewHolder snsPostViewHolder){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("file",Context.MODE_PRIVATE);

        RequestApi requestApi;

        Retrofit retrofit =new Retrofit.Builder()
                .baseUrl("http://" + IPclass.IP_ADDRESS + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        requestApi = retrofit.create(RequestApi.class);



        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("post_id", post_id);
        stringStringHashMap.put("id", sharedPreferences.getString("id", ""));

        Call<PostResult> postResultCall = requestApi.SNS_POST_LIKE_CHECK(stringStringHashMap);
        postResultCall.enqueue(new Callback<PostResult>() {
            @Override
            public void onResponse(Call<PostResult> call, Response<PostResult> response) {
                if(!response.isSuccessful()){
                    Log.d(TAG, "onResponse: " + response.message());
                    return;
                }

                PostResult postResult = response.body();
                if(postResult.getResult().equals("success")){ // 해당 게시물을 좋아요 했었다면 ?
                        snsPostViewHolder.likeBtn.setColorFilter(ContextCompat.getColor(mContext,R.color.red));
                }else {

                }


            }

            @Override
            public void onFailure(Call<PostResult> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
}