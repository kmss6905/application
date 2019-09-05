package com.example.application.SNS.Adpater;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.application.R;
import com.example.application.SNS.Class.comment;
import com.example.application.SNS.Class.likeListItemData;
import com.example.application.SNS.Class.parentComment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class SnsCommentParentAdapter extends RecyclerView.Adapter<SnsCommentParentAdapter.snsCommentViewHolder> {
    private static final String TAG = "SnsCommentParentAdapter";



    //아이템 클릭시 실행 함수
    private SnsCommentParentAdapter.ItemClick itemClick;

    public interface ItemClick {
        public void onClick(View view, int position, int id, snsCommentViewHolder snsCommentViewHolder);
    }



    //아이템 클릭시 실행 함수 등록 함수
    public void setItemClick(SnsCommentParentAdapter.ItemClick itemClick) {
        this.itemClick = itemClick;
    }


    private ArrayList<parentComment> parentComments;
    private Context mContext;


    public SnsCommentParentAdapter(ArrayList<parentComment> parentComments, Context mContext) {
        this.parentComments = parentComments;
        this.mContext = mContext;
    }



    @NonNull
    @Override
    public snsCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment_parent, parent, false);
        return new snsCommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull snsCommentViewHolder holder, int position) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("file", Context.MODE_PRIVATE);
        String user_id = sharedPreferences.getString("id", "");





        // 자기가 쓴 댓글인 경우에만 편집과 삭제가 가능하도록 함
        if(!user_id.equals(parentComments.get(position).getUser_id())){ //같지 않다면 숨김

            Log.d(TAG, "onBindViewHolder: user_id : " + user_id +  " // " + "parentComments.get(" + position + " ).getuser_id : " +
                    parentComments.get(position).getUser_id());

            holder.item_comment_parent_edit.setVisibility(View.GONE);
            holder.item_comment_parent_delete.setVisibility(View.GONE);

        }


        // 대댓글에 대한 리사이클러뷰
        if(parentComments.get(position).getChildComments() == null){ //대댓글이 하나도 없는 경우

        }else{
            // < -- 이미 참조가 끝난 상태이다.
            // 여기서 대댓글에 대한 리사이클러뷰와 어뎁터, 레이아웃 매니저를 설정한다.
            holder.recyclerView_comment_child.setLayoutManager(new LinearLayoutManager(mContext)); // 리사이클러뷰
            holder.recyclerView_comment_child.setHasFixedSize(true);


            SnsCommentChildAdapter snsCommentChildAdapter =
                    new SnsCommentChildAdapter(parentComments.get(position).getChildComments(), mContext);

            snsCommentChildAdapter.setHasStableIds(true);


            final SpannableStringBuilder sp = new SpannableStringBuilder(parentComments.get(position).getNickname() + " " +  parentComments.get(position).getContent());
            sp.setSpan(new ForegroundColorSpan(Color.BLACK), 0, parentComments.get(position).getNickname().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.item_comment_parent_content.append(sp);



            holder.recyclerView_comment_child.setAdapter(snsCommentChildAdapter);
            // 부모 댓글이 가지고 있던 데이터를가져오자.
        }







//        holder.item_comment_parent_nickname.setText(parentComments.get(position).getNickname());
        holder.item_comment_parent_time.setText(parentComments.get(position).getDate());
//        holder.item_comment_parent_content.setText(parentComments.get(position).getContent());



        // 프로필 이미지
        Glide.with(mContext).
                load(parentComments.get(position).getProfile()).
                into(holder.item_comment_parent_profileImg);



        // 이미지 버튼 -> 해당 유저의 정보보기로 이동
        holder.item_comment_parent_profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemClick != null){
                    itemClick.onClick(view, position, 4,holder);
                }
            }
        });

        // 닉네임 버튼 -> 해당 유저의 정보보기로 이동
        holder.item_comment_parent_nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemClick != null){
                    if(itemClick != null){
                        itemClick.onClick(view,position,4,holder);
                    }
                }
            }
        });


        // 답글달기
        holder.item_comment_parent_re_comment_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemClick != null){
                    itemClick.onClick(view,position,5,holder);
                }
            }
        });


        // 삭제
        holder.item_comment_parent_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemClick != null){
                    itemClick.onClick(view,position,6,holder);
                }
            }
        });

        // 편집
        holder.item_comment_parent_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemClick != null){
                    itemClick.onClick(view,position,7,holder);
                }
            }
        });






    }

    @Override
    public int getItemCount() {
        return parentComments.size();
    }

    @Override
    public long getItemId(int position) {
        return parentComments.get(position).hashCode();
    }

    public class snsCommentViewHolder extends RecyclerView.ViewHolder{
        RecyclerView recyclerView_comment_child;
        ImageView item_comment_parent_profileImg; // ?
        TextView item_comment_parent_nickname;
        TextView item_comment_parent_content;
        TextView item_comment_parent_time;
        TextView item_comment_parent_re_comment_text;
        TextView item_comment_parent_edit;
        TextView item_comment_parent_delete;
        LinearLayout item_comment_parent_show_more_comment;
        TextView item_comment_rest_child_comment_num;



        public snsCommentViewHolder(@NonNull View itemView) {
            super(itemView);

            recyclerView_comment_child = itemView.findViewById(R.id.recyclerView_comment_child);
            item_comment_parent_profileImg = itemView.findViewById(R.id.item_comment_parent_profileImg);
            item_comment_parent_nickname = itemView.findViewById(R.id.item_comment_parent_nickname);
            item_comment_parent_content = itemView.findViewById(R.id.item_comment_parent_content);
            item_comment_parent_time = itemView.findViewById(R.id.item_comment_parent_time);
            item_comment_parent_re_comment_text = itemView.findViewById(R.id.item_comment_parent_re_comment_text);
            item_comment_parent_edit = itemView.findViewById(R.id.item_comment_parent_edit);
            item_comment_parent_delete = itemView.findViewById(R.id.item_comment_parent_delete);
            item_comment_parent_show_more_comment = itemView.findViewById(R.id.item_comment_parent_show_more_comment);
            item_comment_rest_child_comment_num = itemView.findViewById(R.id.item_comment_rest_child_comment_num);

        }
    }








    // ---------------------------------------------------------------------------------------------------------
    //                                                  대댓글 위한 어뎁터
    // ---------------------------------------------------------------------------------------------------------

    public static class SnsCommentChildAdapter extends RecyclerView.Adapter<SnsCommentChildAdapter.child_comment_viewholder>{
        private ArrayList<comment> childComments;
        private Context mmContext;


        //아이템 클릭시 실행 함수
        private SnsCommentChildAdapter.ItemClick itemClick;

        public interface ItemClick {
            public void onClick(View view, int position, int id, child_comment_viewholder SnsPostViewHolder);
        }



        //아이템 클릭시 실행 함수 등록 함수
        public void setItemClick(SnsCommentChildAdapter.ItemClick itemClick) {
            this.itemClick = itemClick;
        }






        public SnsCommentChildAdapter(ArrayList<comment> childComments, Context mmContext) {
            this.childComments = childComments;
            this.mmContext = mmContext;
        }



        @NonNull
        @Override
        public child_comment_viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mmContext).inflate(R.layout.item_comment_child, parent, false);
            return new child_comment_viewholder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull child_comment_viewholder holder, int position) {


            SharedPreferences sharedPreferences = mmContext.getSharedPreferences("file",  Context.MODE_PRIVATE);
            String user_id = sharedPreferences.getString("id", "");

            if(!user_id.equals(childComments.get(position).getUser_id())){ //아이디가 같지 않다면
                holder.item_comment_child_edit.setVisibility(View.GONE); // 편집
                holder.item_comment_child_delete.setVisibility(View.GONE);  // 삭제 버튼을 지운다
            }


            final SpannableStringBuilder sp = new SpannableStringBuilder(childComments.get(position).getNickname() + " " +  childComments.get(position).getContent());
            sp.setSpan(new ForegroundColorSpan(Color.BLACK), 0, childComments.get(position).getNickname().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.item_comment_child_content.append(sp);




//            holder.item_comment_child_nickname.setText(childComments.get(position).getNickname());
//            holder.item_comment_chile_time.setText(childComments.get(position).getDate());




            // 댓글 사용자의 프로필 이미지
            Glide.with(mmContext).load(childComments.get(position).getProfile()).into(holder.item_comment_child_profileImg);


            // 편집
            holder.item_comment_child_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(itemClick != null){
                        itemClick.onClick(view, position, 1, holder);
                    }
                }
            });


            //삭제
            holder.item_comment_child_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(itemClick != null) {
                        itemClick.onClick(view, position, 2,holder);
                    }
                }
            });

            // 답글달기
            holder.item_comment_child_re_comment_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(itemClick != null){
                        itemClick.onClick(view,position,3,holder);
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return childComments.size();
        }


        class child_comment_viewholder extends RecyclerView.ViewHolder{
            CircleImageView item_comment_child_profileImg;
            TextView item_comment_child_nickname;
            TextView item_comment_child_content;
            TextView item_comment_chile_time;
            TextView item_comment_child_re_comment_text;
            TextView item_comment_child_edit;
            TextView item_comment_child_delete;


            public child_comment_viewholder(@NonNull View itemView) {
                super(itemView);

                item_comment_child_profileImg = itemView.findViewById(R.id.item_comment_child_profileImg);
                item_comment_child_nickname = itemView.findViewById(R.id.item_comment_child_nickname);
                item_comment_child_content = itemView.findViewById(R.id.item_comment_child_content);
                item_comment_chile_time = itemView.findViewById(R.id.item_comment_chile_time);
                item_comment_child_re_comment_text = itemView.findViewById(R.id.item_comment_child_re_comment_text);
                item_comment_child_edit = itemView.findViewById(R.id.item_comment_child_edit);
                item_comment_child_delete = itemView.findViewById(R.id.item_comment_child_delete);
            }
        }
    }
}
