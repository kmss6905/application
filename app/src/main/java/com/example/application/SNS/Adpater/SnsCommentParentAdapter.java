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
import android.widget.Button;
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
import com.example.application.TimeString;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class SnsCommentParentAdapter extends RecyclerView.Adapter<SnsCommentParentAdapter.snsCommentViewHolder> {
    private static final String TAG = "SnsCommentParentAdapter";


    public SnsCommentChildAdapter snsCommentChildAdapter;



    //아이템 클릭시 실행 함수
    private SnsCommentParentAdapter.ItemClick itemClick;

    public interface ItemClick {
        public void onClick(View view, int position, int id, snsCommentViewHolder snsCommentViewHolder, ArrayList<comment> comments);
    }




    //아이템 클릭시 실행 함수 등록 함수
    public void setItemClick(SnsCommentParentAdapter.ItemClick itemClick) {
        this.itemClick = itemClick;
    }


    public ArrayList<parentComment> parentComments;
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
        /**
         *  리사이클러뷰 어뎁터 안에 들어온 리스트를 제대로 받고 있는 지 확인한다
         */

        Log.d(TAG, "onBindViewHolder: 어뎁터안에 들어온 댓글 데이터 리스트 확인 : " + parentComments.get(position).toString());



        SharedPreferences sharedPreferences = mContext.getSharedPreferences("file", Context.MODE_PRIVATE);
        String user_id = sharedPreferences.getString("id", "");






       // 자기가 쓴 댓글인 경우에만 편집과 삭제가 가능하도록 함
        if(!user_id.equals(parentComments.get(position).getUser_id())){ //같지 않다면 숨김

            Log.d(TAG, "onBindViewHolder: user_id : " + user_id +  " // " + "parentComments.get(" + position + " ).getuser_id : " +
                    parentComments.get(position).getUser_id());

            holder.item_comment_parent_edit.setVisibility(View.GONE);
            holder.item_comment_parent_delete.setVisibility(View.GONE);

        }else{
            Log.d(TAG, "onBindViewHolder: user_id : " + user_id +  " // " + "parentComments.get(" + position + " ).getuser_id : " +
                    parentComments.get(position).getUser_id());

            holder.item_comment_parent_edit.setVisibility(View.VISIBLE);
            holder.item_comment_parent_delete.setVisibility(View.VISIBLE);
        }


        // 대댓글에 대한 리사이클러뷰
        if(parentComments.get(position).getChildComments() == null || parentComments.get(position).getChildComments().size() == 0){ //대댓글이 하나도 없는 경우
            holder.item_comment_parent_show_more_comment.setVisibility(View.GONE); // 댓글 더보기 가림

        }else{

            holder.item_comment_parent_show_more_comment.setVisibility(View.VISIBLE); // 더보기 표시 버튼
            holder.item_comment_rest_child_comment_num.setText(String.valueOf(parentComments.get(position).getChildComments().size())); // 댓글 개수 표시
            holder.recyclerView_comment_child.setVisibility(View.GONE); // 리사이클러뷰 가림





            // < -- 이미 참조가 끝난 상태이다.
            // 여기서 대댓글에 대한 리사이클러뷰와 어뎁터, 레이아웃 매니저를 설정한다.
            holder.recyclerView_comment_child.setLayoutManager(new LinearLayoutManager(mContext)); // 리사이클러뷰
            holder.recyclerView_comment_child.setHasFixedSize(true);


            snsCommentChildAdapter =
                    new SnsCommentChildAdapter(parentComments.get(position).getChildComments(), mContext);

            snsCommentChildAdapter.setHasStableIds(true);



            holder.recyclerView_comment_child.setAdapter(snsCommentChildAdapter);
            // 부모 댓글이 가지고 있던 데이터를가져오자.
        }




        SpannableStringBuilder sp = new SpannableStringBuilder(parentComments.get(position).getNickname() + " " +  parentComments.get(position).getContent());
        Log.d(TAG, "onBindViewHolder: sp : " + sp);

        if(sp != null){
            sp.setSpan(new ForegroundColorSpan(Color.BLACK), 0, parentComments.get(position).getNickname().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.item_comment_parent_content.setText(sp);
        }else{
            holder.item_comment_parent_content.setText(parentComments.get(position).getContent());
        }






        // 날짜입력
        if(parentComments.get(position).getIs_edit().equals("1")){ // 수정된 녀석이라면
            holder.item_comment_parent_time.setText(parentComments.get(position).getDate() + " (수정)"); // 뒤에 수정이라고 붙혀주고
        }else{  // 그렇지 않다면
            holder.item_comment_parent_time.setText(parentComments.get(position).getDate()); // 그냥 date 넣어준다.
        }






        // 만약 삭제된 게시물이라면(대댓글이 있는 경우는 그 내용은 지우되 흔적만 남겨놓는다.)
        if(parentComments.get(position).getIs_delete().equals("1")){ //삭제되었던 녀석이다?

            holder.item_delete_layout.setVisibility(View.VISIBLE);
            holder.item_layout.setVisibility(View.GONE);

        }else if(parentComments.get(position).getIs_delete().equals("0")){ // 삭제가 안된녀석이다.

            holder.item_delete_layout.setVisibility(View.GONE);
            holder.item_layout.setVisibility(View.VISIBLE);
        }







        //        holder.item_comment_parent_nickname.setText(parentComments.get(position).getNickname());
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
                    itemClick.onClick(view, position, 4,holder, parentComments.get(position).getChildComments());
                }
            }
        });

        // 닉네임 버튼 -> 해당 유저의 정보보기로 이동
        holder.item_comment_parent_nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemClick != null){
                    if(itemClick != null){
                        itemClick.onClick(view,position,4,holder, parentComments.get(position).getChildComments());
                    }
                }
            }
        });


        // 답글달기
        holder.item_comment_parent_re_comment_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemClick != null){

                    if(parentComments.get(position).getChildComments() != null){ // 자식댓글들이 이미 있다면
                        itemClick.onClick(view,position,5, holder, parentComments.get(position).getChildComments());
                    }else{ //자식댓글들이 없다면

                        ArrayList<comment> comments = new ArrayList<>();
                        parentComments.get(position).setChildComments(comments);


                        itemClick.onClick(view,position,5, holder, parentComments.get(position).getChildComments());
                    }
                }
            }
        });


        // 삭제
        holder.item_comment_parent_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemClick != null){
                    itemClick.onClick(view,position,6,holder, parentComments.get(position).getChildComments());
                }
            }
        });

        // 편집
        holder.item_comment_parent_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(itemClick != null){
                    itemClick.onClick(view,position,7,holder, parentComments.get(position).getChildComments());
                }
            }
        });

        holder.item_comment_parent_show_more_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemClick != null){
                    itemClick.onClick(view,position,8, holder, parentComments.get(position).getChildComments());
                }
            }
        });



        snsCommentChildAdapter.setItemClick(new SnsCommentChildAdapter.ItemClick() {

            @Override
            public void onClick(View view, int position, int id, ArrayList<comment> comments) {

                switch (id){
                    case 1:
                        itemClick.onClick(view, position,1, holder, comments); // 편집
                        break;
                    case 2:
                        itemClick.onClick(view, position, 2, holder, comments); // 댓글 삭제
                        break;
                    case 3:
                        itemClick.onClick(view,position,3, holder, comments); // 댓글 달기
                        break;
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
        return position;
    }


    public class snsCommentViewHolder extends RecyclerView.ViewHolder{


        public RecyclerView recyclerView_comment_child;
        ImageView item_comment_parent_profileImg; // ?
        TextView item_comment_parent_nickname;
        TextView item_comment_parent_content;
        TextView item_comment_parent_time;
        TextView item_comment_parent_re_comment_text;
        ImageView item_comment_parent_edit;
        ImageView item_comment_parent_delete;
        public LinearLayout item_comment_parent_show_more_comment;
        TextView item_comment_rest_child_comment_num;
        public LinearLayout item_delete_layout;
        public LinearLayout item_layout;
        public TextView item_comment_child_text;



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
            item_delete_layout = itemView.findViewById(R.id.item_delete_layout);
            item_layout = itemView.findViewById(R.id.item_layout);
            item_comment_child_text = itemView.findViewById(R.id.item_comment_child_text);

        }
    }









    // ---------------------------------------------------------------------------------------------------------
    //                                                  대댓글 위한 어뎁터
    // ---------------------------------------------------------------------------------------------------------

    public static class SnsCommentChildAdapter extends RecyclerView.Adapter<SnsCommentChildAdapter.child_comment_viewholder>{
        private ArrayList<comment> childComments;
        private Context mmContext;


        //아이템 클릭시 실행 함수
        public SnsCommentChildAdapter.ItemClick itemClick;

        public interface ItemClick{
            public void onClick(View view, int position, int id, ArrayList<comment> comment);
        }



        //아이템 클릭시 실행 함수 등록 함수
        public void setItemClick(SnsCommentChildAdapter.ItemClick itemClick) {
            this.itemClick = itemClick;
        }






        public  SnsCommentChildAdapter(ArrayList<comment> childComments, Context mmContext) {
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


//
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREAN);
//
//            try {
//                TimeString timeString = new TimeString();
//                Date date = simpleDateFormat.parse(childComments.get(position).getDate());
//                Log.d(TAG, "onBindViewHolder: " + date.toString());
//
//                holder.item_comment_chile_time.setText(timeString.formatTimeString(date)); // 날짜를 집어넣는다
//            } catch (ParseException e) {
//                e.printStackTrace();
//                Log.d(TAG, "onBindViewHolder: " + e.getMessage());
//            }

            holder.item_comment_chile_time.setText(childComments.get(position).getDate());



            // 댓글 사용자의 프로필 이미지
            Glide.with(mmContext).load(childComments.get(position).getProfile()).into(holder.item_comment_child_profileImg);


            // 편집
            holder.item_comment_child_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(itemClick != null){
                        itemClick.onClick(view, position, 1, childComments);
                    }
                }
            });


            //삭제
            holder.item_comment_child_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(itemClick != null) {
                        itemClick.onClick(view, position, 2, childComments);
                    }
                }
            });

            // 답글달기
            holder.item_comment_child_re_comment_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(itemClick != null){
                        itemClick.onClick(view,position,3, childComments);
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return childComments.size();
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public void setHasStableIds(boolean hasStableIds) {
            super.setHasStableIds(hasStableIds);
        }

        public class child_comment_viewholder extends RecyclerView.ViewHolder{
            CircleImageView item_comment_child_profileImg;
            TextView item_comment_child_nickname;
            TextView item_comment_child_content;
            TextView item_comment_chile_time;
            TextView item_comment_child_re_comment_text;
            ImageView item_comment_child_edit;
            ImageView item_comment_child_delete;


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
