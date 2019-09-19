package com.example.application.Retrofit2;

import com.example.application.Account.ManagePasswordActivity;
import com.example.application.Retrofit2.Repo.Account;
import com.example.application.Retrofit2.Repo.AddLiveStream;
import com.example.application.Retrofit2.Repo.CheckNickResult;
import com.example.application.Retrofit2.Repo.GETS.BROADCAST.LIVEINFO;
import com.example.application.Retrofit2.Repo.GETS.BROADCAST.REAL_TIME_LOCATION;
import com.example.application.Retrofit2.Repo.GETS.SNS.COMMENT.comment_list;
import com.example.application.Retrofit2.Repo.GETS.SNS.Like;
import com.example.application.Retrofit2.Repo.GETS.SNS.post;
import com.example.application.Retrofit2.Repo.GETS.SUBSCRIBE.Following;
import com.example.application.Retrofit2.Repo.GETS.SUBSCRIBE.GET_REPO_CHECK;
import com.example.application.Retrofit2.Repo.GETS.SUBSCRIBE.SUBCRIBE;
import com.example.application.Retrofit2.Repo.GETS.USERS.USERINFO;
import com.example.application.Retrofit2.Repo.LivestreamInfo;
import com.example.application.Retrofit2.Repo.Password;
import com.example.application.Retrofit2.Repo.PostResult;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface RequestApi {




    /**
     * @param id
     * @return 계정정보
     */
    @GET("account.php")
    Call<List<Account>> getAccount(@Query("id") String id);



    /**
     * @param nick
     * @param id
     * @return
     */
    @GET("checknick.php")
    Call<List<CheckNickResult>> checkNick(
            @Query("nick_name") String nick,
            @Query("id") String id
    );



    /**
     * post로 비밀번호 변경을 요청합니다
     * @param fields 서버로 비밀번호 변경에 필요한 파라미터를 Map의 형태로 보냄
     *               총 4개 보냄
     *               현재의 password, 바꾸고자하는 password, 재확인 password, 사용자의 primary key값
     * @return
     */
    @FormUrlEncoded
    @POST("changepw.php")
    Call<ManagePasswordActivity.PasswordCheckResponse> PASSWORD_CALL(
            @FieldMap Map<String, String> fields
    );


    /**
     * 현재 라이브 방송중인 리스트 요청
     * @return
     */
    @GET("get_live_info.php")
    Call<List<LivestreamInfo>> LIVE_STREAM_CALL();





    // POST USER IMG
    // 파일전송
    @Multipart
    @POST("POSTS/USER/update.php")
    Call<PostResult> POST_UPLOAD_IMG_CALL(
            @Part MultipartBody.Part File,
            @Part("id") RequestBody id
    );








//    // 방송 시작
//    @FormUrlEncoded
//    @POST("postlivestream.php")
//    Call<AddLiveStream> ADD_LIVE_STREAM_CALL(@FieldMap Map<String, String> parameters);


    // 방송 수정
    @FormUrlEncoded
    @POST("postlivestream.php")
    Call<AddLiveStream> EDIT_LIVE_STREAM_CALL(@FieldMap Map<String, String> parameters);


    // 방송 종료
    @FormUrlEncoded
    @POST("quitBroadCast.php")
    Call<AddLiveStream> QUIT_LIVE_STREAM_CALL(@FieldMap Map<String, String> parameters);






    // POST LIVE BROADCAST ( 스트리머 )
    /**
     *
     * @param parameters title, id, type, tag, routeStream,
     * @param endpoint  add.php(방송 추가) , delete.php(방송 삭제), update.php(방송 수정
     * @return
     */
    @FormUrlEncoded
    @POST("POSTS/BROADCAST/LIVE/{endpoint}")
    Call<PostResult> POST_LIVE_STREAM_CALL(@FieldMap Map<String, String> parameters, @Path("endpoint") String endpoint);


    //POST_VOD_BROADCAST
    @FormUrlEncoded
    @POST("POSTS/BROADCAST/VOD/{endpoint}")
    Call<PostResult> POST_VOD_STREAM_CALL(@FieldMap Map<String, String> parameters, @Path("endpoint") String endpoint);


    // POST LIVE BROADCAST ( 시청자 )


    //POST LIVE BROADCAST
    /**
     *
     * @param parameters
     * @param endpoint
     * @return
     */
    @FormUrlEncoded
    @POST("POSTS/BROADCAST/LIVE/VIEWERS/{endpoint}")
    Call<PostResult> POST_BROADCAST_BY_VIEWERS(@FieldMap Map<String, String> parameters, @Path("endpoint") String endpoint);





    // GET LIVE BROADCAST WHOLE
    @GET("GETS/BROADCAST/LIVE/get.php")
    Call<List<LIVEINFO>> GET_LIST_LIVE_STREAM_CALL();


    // GET LIVE BROADCAST ONE
    @GET("GETS/BROADCAST/LIVE/get.php")
    Call<LIVEINFO> GET_LIVE_STREAM_INFO(@Query("streamerid") String streamer_primary_id);




    //GET USER INFO
    @GET("GETS/USER/get.php")
    Call<USERINFO> GET_USER_INFO(@Query("id") String id);





    /**
     *
     * @param user_primary_id
     * @return 객체
     */
    //GET SUBSCRIBE INFO(CHECK)
    @GET("GETS/SUBSCRIBE/get.php")
    Call<GET_REPO_CHECK> GET_SUBSCRIBE_CHECK_INFO(@Query("id") String user_primary_id,  @Query("streamerid") String user_streamer_primary_id);

    /**
     *
     * @param user_primary_id
     * @return 배열
     */
    //GET SUBSCRIBE INFO(배열)
    @GET("GETS/SUBSCRIBE/get.php")
    Call<List<SUBCRIBE>> GET_SUBSCRIBE_INFO(@Query("id") String user_primary_id);

    /**
     *
     * @param endpoint 추가 / 수정 / 삭제
     * @param parameters
     * @return
     */
    //POST SUBCRIBE INFO(배열)
    @FormUrlEncoded
    @POST("POSTS/SUBCRIBE/{endpoint}")
    Call<List<SUBCRIBE>> POST_SUBSCRIBE(
            @Path("endpoint") String endpoint,
            @FieldMap Map<String, String> parameters
    );



    //POST SUBCRIBE INFO CHECK( 확인 )
    /**
     *
     * @param endpoint 추가 / 수정 / 삭제
     * @return
     *
     */
    @FormUrlEncoded
    @POST("POSTS/SUBSCRIBE/{endpoint}")
    Call<PostResult> POST_RESULT_CALL(
            @Path("endpoint") String endpoint,
            @FieldMap Map<String, String> parameters
    );



    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //                                                                          POST 여행 정보
    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------


    // POST 여행 정보
    /**
     *
     * @param File 이미지
     * @param stringRequestBodyMap  제목, 태그 , 위도 , 경도, 방송 시각, 루트 스트림, 유저 아이디
     * @return
     */
    @Multipart
    @POST("POSTS/TRIPINFO/{endpoint}")
    Call<PostResult> POST_TRIPINFO_RESULT_CALL(
            @Path("endpoint") String endpoint,
            @Part MultipartBody.Part File,
            @PartMap Map<String, RequestBody> stringRequestBodyMap
    );

    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //                                                                          POST 실시간 위치 정보(방송 시)
    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------


    @FormUrlEncoded
    @POST("POSTS/BROADCAST/LIVE/{endpoint}")
    Call<PostResult> POST_REAL_LOCATION_RESULT_CALL(
            @Path("endpoint") String endpoint,
            @FieldMap Map<String, String> parameters
    );

    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //                                                                          GET 실시간 위치 정보(방송 시) viewer
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * @param endpoint         realtime_location.php
     * @param route_stream  broad_cast_time
     * @return
     */
    @GET("GETS/BROADCAST/LIVE/{endpoint}")
    Call<REAL_TIME_LOCATION> GET_REAL_LOCATION_RESULT_CALL(
            @Path("endpoint") String endpoint,
            @Query("route_stream") String route_stream
    );



    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //                                                                            POST SNS 게시물
    // --------------------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     *
     * @param endpoint add.php, update.php, delete.php
     * @param files
     * @return
     */
    // 게시물 POST
    @Multipart
    @POST("POSTS/SNS/{endpoint}")
    Call<PostResult> SNS_POST_RESULT_CALL(
            @Path("endpoint") String endpoint,
            @Part List<MultipartBody.Part> files,
            @PartMap Map<String, RequestBody> stringRequestBodyMap
    );

    // 게시물 POST
    @Multipart
    @POST("POSTS/SNS/update.php")
    Call<PostResult> SNS_POST_RESULT_CALL_NOT_PHOTO_CHANGE(
            @PartMap Map<String, RequestBody> stringRequestBodyMap
    );


    // 게시물 삭제
    @FormUrlEncoded
    @POST("POSTS/SNS/delete.php")
    Call<PostResult> SNS_POST_DELETE_RESULT_CALL(
            @Field("post_id") String postId
    );



    // 게시물들 가져오기
    @GET("GETS/SNS/{endpoint}")
    Call<List<post>> SNS_GET_POST_LIST(
            @Path("endpoint") String endpoint
    );

    // 하나의 게시물만을 가져옵니다.

    /**
     *
     * @param endpoint php파일
     * @param post_id 게시물 번호
     * @return
     *
     *  사용 되는 곳 -> 하나의 게시물에 대한 정보를 필요로 하는 곳
     *      하나의 게시물에 대한 정보를 보여주는 곳은 어디든지 사용가능
     *      현재 : 개인 SNS 계정 포토 리스트에 대한 포토 클릭 -> 게시물 상세히 볼 수 있음
     */
    @GET("GETS/SNS/{endpoint}")
    Call<post> SNS_GET_ONE_POST(
            @Path("endpoint") String endpoint,
            @QueryMap Map<String, String> getSnsParmeter
    );

    // 특정 유저의 게시글 리스트를 가져옵니다.
    @GET("GETS/SNS/{endpoint}")
    Call<List<post>> SNS_GET_POST_LIST_AC(
            @Path("endpoint") String endpoint,
            @Query("user_id") String id,
            @Query("photos") String is_photo_frg,
            @Query("page_num") int page_num
    );


    // 게시물 좋아요 추가하기
    @FormUrlEncoded
    @POST("POSTS/SNS/LIKE/{endpoint}")
    Call<PostResult> SNS_POST_LIKE_ADD_RECULT_CALL(
            @Path("endpoint") String endpoint,
            @FieldMap Map<String, String>GstringStringMap
    );


    // 게시물 좋아요 체크하기
    @FormUrlEncoded
    @POST("GETS/SNS/LIKE/islike.php")
    Call<PostResult> SNS_POST_LIKE_CHECK(
            @FieldMap Map<String, String> stringStringMap
    );


    // 게시물 좋아요 리스트 가져오기
    @GET("GETS/SNS/LIKE/list.php")
    Call<List<Like>> SNS_GET_LIKE_LIST_CALL(
            @Query("post_num") int postNum,
            @Query("page_num") int pageNum,
            @Query("id") String id
    );


    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //                                                                            GET SNS COMMENT
    // --------------------------------------------------------------------------------------------------------------------------------------------------------------------

    // 해당 게시물의 댓글 리스트를 전부 가져옵니다.
    @GET("GETS/SNS/COMMENT/comment_list.php")
    Call<List<comment_list>> SNS_GET_COMMENT_LIST_CALL(
            @QueryMap Map<String, String> stringStringMap
    );

    //해당 게시물의 댓글 개수를 가져옵니다.
    @FormUrlEncoded
    @POST("GETS/SNS/COMMENT/comment_num.php")
    Call<PostResult>SNS_GET_COMMENT_NUM_CALL(
            @Field("post_num") String post_num
    );






    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //                                                                            POST SNS COMMENT
    // --------------------------------------------------------------------------------------------------------------------------------------------------------------------




    /**
     *
     * @param endpoint add.php, update.php, delete.php
     * @param stringStringMap
     * @return
     */
    @FormUrlEncoded
    @POST("POSTS/SNS/COMMENT/{endpoint}")
    Call<PostResult> SNS_POST_COMMENT(
            @Path("endpoint") String endpoint,
            @FieldMap Map<String, String> stringStringMap
    );





    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //                                                                            GET NUM
    // --------------------------------------------------------------------------------------------------------------------------------------------------------------------

    @GET("GETS/NUM/{endpoint}")
    Call<PostResult> GET_NUM(
            @Path("endpoint") String endpoint,
            @QueryMap Map<String, String> stringStringMap
    );


    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //                                                                        팔로잉 리스트 가져오기
    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------

    // 팔로잉 여부 체크


    @GET("GETS/SUBSCRIBE/{endpoint}")
    Call<PostResult> GET_CHAECK_FOLLOWING(
            @Path("endpoint") String endpoint,
            @Query("user_id") String user_id,
            @Query("id") String id
    );

    @GET("GETS/SUBSCRIBE/{endpoint}")
    Call<List<Following>> GET_FOLLOWINF_OR_FOLLOWER_LIST(
            @Path("endpoint") String endpoint,
            @Query("user_id") String user_id,
            @Query("id") String id,
            @Query("page_num") int page_num
    );




    // 댓글 수정

    // 댓글 삭제



    // live 방송 수정
    // live 방송 종료(삭제)





    // vod  방송 시작(추가)
    // vod 방송 수정
    // vod 방송 종료(삭제)

}
