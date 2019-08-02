package com.example.application.Retrofit2;

import com.example.application.Account.ManagePasswordActivity;
import com.example.application.Retrofit2.Repo.Account;
import com.example.application.Retrofit2.Repo.AddLiveStream;
import com.example.application.Retrofit2.Repo.CheckNickResult;
import com.example.application.Retrofit2.Repo.GETS.BROADCAST.LIVEINFO;
import com.example.application.Retrofit2.Repo.GETS.SUBSCRIBE.GET_REPO_CHECK;
import com.example.application.Retrofit2.Repo.GETS.SUBSCRIBE.SUBCRIBE;
import com.example.application.Retrofit2.Repo.GETS.USERS.USERINFO;
import com.example.application.Retrofit2.Repo.LivestreamInfo;
import com.example.application.Retrofit2.Repo.Password;
import com.example.application.Retrofit2.Repo.PostResult;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
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

    @GET("GETS/BROADCAST/LIVE/get.php")
    Call<LIVEINFO> GET_LIVE_STREAM_INFO(@Query("primary_id") String user_primary_id);




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








    // live 방송 수정
    // live 방송 종료(삭제)






    // vod  방송 시작(추가)
    // vod 방송 수정
    // vod 방송 종료(삭제)

}
