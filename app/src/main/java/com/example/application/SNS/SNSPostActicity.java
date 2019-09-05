package com.example.application.SNS;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.application.IPclass;
import com.example.application.MainActivity;
import com.example.application.R;
import com.example.application.Retrofit2.Repo.PostResult;
import com.example.application.Retrofit2.RequestApi;
import com.example.application.SNS.Adpater.SnsRegiPostPhotoAdapter;
import com.example.application.SNS.Class.Snspost;
import com.example.application.SNS.Class.photo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gun0912.tedbottompicker.TedBottomPicker;
import gun0912.tedbottompicker.TedBottomSheetDialogFragment;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Part;

public class SNSPostActicity extends AppCompatActivity{
    private static final String TAG = "SNSPostActicity";
    private static final int CLICK_LOCATION_BTN = 101;
    private static final int PHOTOPICK = 102;



    // 수정으로 들어오는 것을 확인하기 위함.
    Intent intent;

    // 수정 시 받아논 post num
    public static String postNum;



    // 레트로핏
    private RequestApi requestApi;



    // 참조 변수들

    TextView locationText; // 주소 텍스트
    EditText contentEdit; // 내용 텍스트
    EditText tagEdit; // 테크 에딧
    public static LatLng latLng; // 위도,경도
    public static String address; // 주소
    public static ArrayList<Uri> uriArrayList; // 리사이클러뷰 데이터
    public ArrayList<photo> photoArrayList;


    //Recycler 준비물
    LinearLayoutManager linearLayoutManager; // 레이아웃 매니저
    SnsRegiPostPhotoAdapter snsRegiPostPhotoAdapter; // 어뎁터





    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case CLICK_LOCATION_BTN: // 위치추가후의 결과값 받아오기
                    locationText.setText(data.getStringExtra("address"));

                    if(latLng != null){
                        latLng = null;
                    }

                    latLng = new LatLng(data.getExtras().getDouble("lat"), data.getExtras().getDouble("lot"));



                    address = data.getStringExtra("address");

                    break;
                case PHOTOPICK: // 사진 추가후의 결과값 받아오기
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snspost_acticity);

        // 가져온 것
        intent = getIntent();
        Snspost snspost = (Snspost) intent.getSerializableExtra("SNS_POST");
        // 우선 다른 함수에도 사용해야하기 때문에 포스트 넘버는 전역으로 받아놓자.
        latLng = new LatLng(37.566, 126.9784);


        if(snspost != null){

            postNum = snspost.getId();
            Log.d(TAG, "onCreate: postNum // " + postNum);
        }


        Gson gson = new GsonBuilder()
                .setLenient()
                .create();


        // Retrofit 서버 통신 위한 세팅
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + IPclass.IP_ADDRESS + "/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        requestApi = retrofit.create(RequestApi.class);





        // 참조 변수들

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("게시물추가");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        contentEdit = findViewById(R.id.contentEdit);
        locationText = findViewById(R.id.locationText);
        tagEdit = findViewById(R.id.tagEdit);
        RecyclerView photoListRecyclerview = findViewById(R.id.photoListRecyclerview);


        // Button
        Button photoBtn = findViewById(R.id.photoBtn);
        Button uploadBtn = findViewById(R.id.uploadBtn);
        Button locationBtn = findViewById(R.id.locationBtn);

        // RecyclerView
        photoArrayList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false);
        snsRegiPostPhotoAdapter = new SnsRegiPostPhotoAdapter(photoArrayList, getApplicationContext());
        photoListRecyclerview.setLayoutManager(linearLayoutManager);
        photoListRecyclerview.setAdapter(snsRegiPostPhotoAdapter);







        // Button Click Event
        // 1. add location btn
        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SNSPostLocationActivity.class);
                startActivityForResult(intent, CLICK_LOCATION_BTN);
            }
        });


        // 2. upload photo btn
        photoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uriArrayList = new ArrayList<>();
                int i;


                TedBottomPicker.with(SNSPostActicity.this)
                        .setPeekHeight(1600)
                        .showTitle(false)
                        .setTitle("사진 선택")
                        .setCompleteButtonText("확인")
                        .setEmptySelectionText("선택 되지 않음")
                        .setSelectedUriList(uriArrayList)
                        .setSelectMaxCount(10)
                        .showMultiImage(new TedBottomSheetDialogFragment.OnMultiImageSelectedListener() {
                            @Override
                            public void onImagesSelected(List<Uri> uriList) {
                                if(uriList.size() == 0){
                                    return;
                                }

                                photoArrayList.clear();

                                // here is selected image uri list
                                for(int i = 0; i < uriList.size(); i ++){
                                    System.out.println("uriList.get(" + i + ")" + " : " + uriList.get(i));
                                    photo photo = new photo();
                                    photo.setUrl(uriList.get(i));
                                    photoArrayList.add(photo);
                                }

                                snsRegiPostPhotoAdapter.notifyDataSetChanged();
                            }
                        });
            }
        });



        // 3. photoCancleBtn
        snsRegiPostPhotoAdapter.setItemClick(new SnsRegiPostPhotoAdapter.ItemClick() {
            @Override
            public void onClick(View view, int position, int i) {
                photoArrayList.remove(position);
                snsRegiPostPhotoAdapter.notifyDataSetChanged();
            }
        });


        // 4. uploadBtn
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // 예외처리 -> 사진파일이 없을 경우는 무조건 안됨
                if(photoArrayList.size() == 0) {
                    Snackbar.make(view, "적어도 1개의 사진이 등록되어야 합니다", Snackbar.LENGTH_SHORT).show();
                    return;
                }



                requestReadExternalStoragePermission();

                if(snspost == null){ // 수정하기 아닌 경우
                    // 추후에 프로그레스바 넣음


                    /**
                     *  들어가는 값들을 확인하자
                     */

                    Log.d(TAG, "onClick: 클라 latLng.latitude :" + latLng.latitude);
                    Log.d(TAG, "onClick: 클라 latLng.longitude :" + latLng.longitude);
                    Log.d(TAG, "onClick: 클라 tagEdit.getText() : " + tagEdit.getText().toString());
                    Log.d(TAG, "onClick: 클라 content.getText() : " + contentEdit.getText().toString());





                    SNS_POST("add.php", latLng.latitude, latLng.longitude, photoArrayList);


                }else if(snspost != null){ // 수정하기 인 경우

                    Log.d(TAG, "onClick: 수정하기 부분입니다.");
                    Log.d(TAG, "onClick: tag / " + tagEdit.getText().toString());
                    Log.d(TAG, "onClick: content " + contentEdit.getText().toString());

                    String strr = photoArrayList.get(0).getUrl().toString();
                    String str[] =  strr.split("://");


                    if(str[0].equals("http")){ // 사진은 수정하지 않고 업로드 누른 경우
                        SNS_POST_NOT_PHOTO_CHANGE(latLng.latitude, latLng.longitude, photoArrayList);
                    }else{

                        Log.d(TAG, "onClick: 넘기는 파라미터 // latLng.latitude : " + latLng.latitude + " // latLng.longitude" + latLng.longitude  +" //");
                        Log.d(TAG, "onClick: 넘기는 파라미터 //  photoArrayList.size() :" + photoArrayList.size());

                        for(int i = 0; i < photoArrayList.size(); i ++){
                            Log.d(TAG, "onClick: 넘기는 파라미터 // " + i + "번째 : "+ photoArrayList.get(i));
                        }


                        SNS_POST("update.php", latLng.latitude, latLng.longitude, photoArrayList);


                    }




                }
            }
        });



        // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //                                                                          수정하기로 들어왔을 때
        // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------


        if(snspost != null){ // 뿌려주자

            snspost.getId();



            getSupportActionBar().setTitle("게시물 수정");
            uploadBtn.setText("수정하기");

            System.out.println(snspost.toString());

            // 넣는 와중에 null 경우도 있으니까 조심하자.


            // 컨텐트
            contentEdit.setText(snspost.getContent());


            // 위치가 없는 경우
            if(snspost.getAddress() != null){
                locationText.setText(snspost.getAddress());
            }

            // 태그가 없는경우
            if(snspost.getTag() != null){
                tagEdit.setText(snspost.getTag());
            }

            // 위치 위도 경도 넣기

            if(snspost.getLat() != null && snspost.getLon() != null){


            }


            // 사진데이터를 넣어주자
            for(int i = 0; i < snspost.getPhotoUriArrayList().size(); i ++) {

                photo photo = new photo();
                photo.setUrl(Uri.parse(snspost.getPhotoUriArrayList().get(i)));

                Log.d(TAG, "수정 사진데이터 가져오는 중 : " + i + " 번쨰 /  " + snspost.getPhotoUriArrayList().get(i));
                Log.d(TAG, "수정 가져온 사진데이터 배열에 넣는 중 : " + i + " /번째" + photo.getUrl().toString());


                photoArrayList.add(photo);

                Log.d(TAG, "수정 실제로 리스트에 들어갔는 지 확인 : " + i + " 번쨰" + photoArrayList.get(photoArrayList.size() - 1).toString());
            }

            snsRegiPostPhotoAdapter.notifyDataSetChanged(); // 노티파이

        }

    }







    //============================================================툴바 메뉴============================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_register, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    //======================================================== 서버 통신 ================================================================

    /**
     *
     * @param endpoint add.php, update.php. delete.php
     */
    public void SNS_POST(String endpoint, Double lat, Double lon, ArrayList<photo> photoArrayList){



        SharedPreferences sharedPreferences = getSharedPreferences("file", MODE_PRIVATE);
        String user_id = sharedPreferences.getString("id", ""); // pri ID


        HashMap<String, RequestBody> requestBodyHashMap = new HashMap<>(); // 기타 파라미터A
        List<MultipartBody.Part> files = new ArrayList<>();



        requestBodyHashMap.put("content", RequestBody.create(MediaType.parse("text/plain"), contentEdit.getText().toString()));
        requestBodyHashMap.put("tag", RequestBody.create(MediaType.parse("text/plain"), tagEdit.getText().toString()));
        requestBodyHashMap.put("id", RequestBody.create(MediaType.parse("text/plain"), user_id));
        requestBodyHashMap.put("lat", RequestBody.create(MediaType.parse("text/plain"), String.valueOf(lat)));
        requestBodyHashMap.put("lon", RequestBody.create(MediaType.parse("text/plain"), String.valueOf(lon)));


        if(postNum != null){
            requestBodyHashMap.put("post_num", RequestBody.create(MediaType.parse("text/plain"), postNum)); // 게시물 번호
            Log.d(TAG, "SNS_POST_NOT_PHOTO_CHANGE: 들어가는 데이터 : "  + postNum);
        }

        if(address != null){ // 주소가 입력되어 있다면?
            requestBodyHashMap.put("address", RequestBody.create(MediaType.parse("text/plain"), address));

            Log.d(TAG, "SNS_POST_NOT_PHOTO_CHANGE: 들어가는 데이터 : "  + address);
        }

        requestBodyHashMap.put("size", RequestBody.create(MediaType.parse("text/plain"), String.valueOf(photoArrayList.size())));

        Log.d(TAG, "SNS_POST_NOT_PHOTO_CHANGE: 들어가는 데이터 내용: "  + contentEdit.getText().toString());
        Log.d(TAG, "SNS_POST_NOT_PHOTO_CHANGE: 들어가는 데이터 태그: "  + tagEdit.getText().toString());
        Log.d(TAG, "SNS_POST_NOT_PHOTO_CHANGE: 들어가는 데이터 아이디: "  + user_id);
        Log.d(TAG, "SNS_POST_NOT_PHOTO_CHANGE: 들어가는 데이터 위도: "  + lat);
        Log.d(TAG, "SNS_POST_NOT_PHOTO_CHANGE: 들어가는 데이터 경도: "  + lon);
        Log.d(TAG, "SNS_POST_NOT_PHOTO_CHANGE: 들어가는 데이터 사이즈: "  + photoArrayList.size());







        for(int i = 0; i < photoArrayList.size(); i ++){

            System.out.println("photoArrayList.get(" + i + ").getUrl() : " + photoArrayList.get(i).getUrl());
            String str = String.valueOf(photoArrayList.get(i).getUrl());


                    String[] values = str.split("file://");

//                System.out.println("문자 values[" + x + "] : " + values[1]);
                    File file = new File(values[1]); // 파일 경로를 통해 파일 객체 생

                    if(!file.exists()){
                        System.out.println("파일 존재X , values[1] : " + values[1]);
                        Log.d(TAG, "SNS_POST: 파일" + values[1]);
                        return;
                    }


                    RequestBody fileRequestBody = RequestBody.create(MediaType.parse("image/*"),file); // 서버로 보낼 바디 작성
                    MultipartBody.Part filepart = MultipartBody.Part.createFormData("uploaded_file_" + i, file.getName(), fileRequestBody);
                    Log.d(TAG, "SNS_POST: filepart " + filepart.body());


                    files.add(filepart);
                    Log.d(TAG, "SNS_POST: " + files.size());

                    for(int x=0; x < files.size() ; x ++){
                        Log.d(TAG, "SNS_POST: files 에 들어가는 파일들" + x + " 번째" + files.get(x));
                    }

            }



        Call<PostResult> postResultCall = requestApi.SNS_POST_RESULT_CALL(endpoint, files, requestBodyHashMap);
        postResultCall.enqueue(new Callback<PostResult>() {
            @Override
            public void onResponse(Call<PostResult> call, Response<PostResult> response) {
                if (!response.isSuccessful()) {
                    Log.i(TAG, "onResponse: " + response.message());
                    System.out.println("에러 ? 코드  : " + response.code());
                    return;
                }

                PostResult postResult = response.body();
                if (postResult.getResult().equals("success")) {

                    if(endpoint.equals("add.php")){
                        Toast.makeText(getApplicationContext(), "게시물이 업로드 되었습니다", Toast.LENGTH_SHORT).show();
                        finish();
                    }else if(endpoint.equals("update.php")){
                        Snackbar.make(getWindow().getDecorView().getRootView(),"게시글이 수정되었습니다.", Snackbar.LENGTH_SHORT).show();
                        finish();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "서버쪽 실패", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onResponse: 실패 meg" + response.message());
                    Log.d(TAG, "onResponse: 실패 body" + response.body());
                    Log.d(TAG, "onResponse: 실패 " + response.raw());
                    Log.d(TAG, "onResponse: 실패 " + response.errorBody());
                }

            }
            @Override
            public void onFailure(Call<PostResult> call, Throwable t) {

                Toast.makeText(getApplicationContext(),"에러내용 :  " + t.getMessage() + " // t.getStackTrace() : "  + t.getStackTrace(), Toast.LENGTH_SHORT).show();
            }
        });
    }




    // 수정 하였을 떄, 게시글의 사진은 변경하지 않았을 떄
    public void SNS_POST_NOT_PHOTO_CHANGE(Double lat, Double lon, ArrayList<photo> photoArrayList){
        SharedPreferences sharedPreferences = getSharedPreferences("file", MODE_PRIVATE);
        String user_id = sharedPreferences.getString("id", ""); // pri ID
        HashMap<String, RequestBody> requestBodyHashMap = new HashMap<>(); // 기타 파라미터A

        String strPhotoUrl = "";

        for(int i = 0; i < photoArrayList.size(); i++){

            strPhotoUrl += photoArrayList.get(i).getUrl().toString() + ',';
        }






        requestBodyHashMap.put("content", RequestBody.create(MediaType.parse("text/plain"), contentEdit.getText().toString()));
        requestBodyHashMap.put("tag", RequestBody.create(MediaType.parse("text/plain"), tagEdit.getText().toString()));
        requestBodyHashMap.put("id", RequestBody.create(MediaType.parse("text/plain"), user_id));
        requestBodyHashMap.put("lat", RequestBody.create(MediaType.parse("text/plain"), String.valueOf(lat)));
        requestBodyHashMap.put("lon", RequestBody.create(MediaType.parse("text/plain"), String.valueOf(lon)));
        requestBodyHashMap.put("post_num", RequestBody.create(MediaType.parse("text/plain"), postNum)); // 게시물 번호
        requestBodyHashMap.put("photo_list", RequestBody.create(MediaType.parse("text/plain"), strPhotoUrl)); // photo_list



        Log.d(TAG, "SNS_POST_NOT_PHOTO_CHANGE: 들어가는 데이터 : "  + contentEdit.getText().toString());
        Log.d(TAG, "SNS_POST_NOT_PHOTO_CHANGE: 들어가는 데이터 : "  + tagEdit.getText().toString());
        Log.d(TAG, "SNS_POST_NOT_PHOTO_CHANGE: 들어가는 데이터 : "  + user_id);
        Log.d(TAG, "SNS_POST_NOT_PHOTO_CHANGE: 들어가는 데이터 : "  + lat);
        Log.d(TAG, "SNS_POST_NOT_PHOTO_CHANGE: 들어가는 데이터 : "  + lon);
        Log.d(TAG, "SNS_POST_NOT_PHOTO_CHANGE: 들어가는 데이터 : "  + postNum);
        Log.d(TAG, "SNS_POST_NOT_PHOTO_CHANGE: 들어가는 데이터 : "  + strPhotoUrl);







        if(address != null){ // 주소가 입력되어 있다면?
            requestBodyHashMap.put("address", RequestBody.create(MediaType.parse("text/plain"), address));
            Log.d(TAG, "SNS_POST_NOT_PHOTO_CHANGE: 들어가는 데이터 : "  + address);
        }
        requestBodyHashMap.put("size", RequestBody.create(MediaType.parse("text/plain"), String.valueOf(photoArrayList.size())));



        Call<PostResult> postResultCall = requestApi.SNS_POST_RESULT_CALL_NOT_PHOTO_CHANGE(requestBodyHashMap);
        postResultCall.enqueue(new Callback<PostResult>() {
            @Override
            public void onResponse(Call<PostResult> call, Response<PostResult> response) {
                if(!response.isSuccessful()){
                    Log.d(TAG, "onResponse: " + response.message());
                    return;
                }

                PostResult postResult = response.body();
                if(postResult.getResult().equals("success")){
                    Snackbar.make(getWindow().getDecorView().getRootView(),"게시글이 수정되었습니다.", Snackbar.LENGTH_SHORT).show();
                    finish();
                }else{
                    Snackbar.make(getWindow().getDecorView().getRootView(),"게시글이 수정되지 못하였습니다.\n 네트워크를 확인해주세요", Snackbar.LENGTH_SHORT).show();
                    finish();
                }

            }

            @Override
            public void onFailure(Call<PostResult> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    //==========================================================권한요청==================================================

    private void requestReadExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        0);
                // MY_PERMISSIONS_REQUEST_READ_EXT_STORAGE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

}

