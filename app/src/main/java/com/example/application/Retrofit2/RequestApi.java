package com.example.application.Retrofit2;

import com.example.application.Account.ManagePasswordActivity;
import com.example.application.Retrofit2.Repo.Account;
import com.example.application.Retrofit2.Repo.CheckNickResult;
import com.example.application.Retrofit2.Repo.Password;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
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






}
