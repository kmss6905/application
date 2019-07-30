package com.example.application.Login_Sign;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.example.application.Account.ManageAccountActivity;
import com.example.application.Broadcast.LiveBroadcastActivity;
import com.example.application.Broadcast.ManageMyBroadcastActivity;
import com.example.application.Fragment.fragment_home;
import com.example.application.Fragment.fragment_map;
import com.example.application.Fragment.fragment_travel_info;
import com.example.application.Fragment.fragment_account;
import com.example.application.IPclass;
import com.example.application.Logg;
import com.example.application.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;


public class HomeActivitiy extends AppCompatActivity {
    private static final String TAG = "HomeActivitiy";

    Toolbar toolbar;
    String nickname;


    // DrawerLayout 에 있는 헤더 참조
    // 프로필이미지, 닉네임, 이메일, 개인 정보 상세보기 버튼
    CircleImageView imageView_profile;
    TextView nav_header_email;
    TextView nav_header_nick_name;
    ImageButton nav_header_info_account;



    String id;
    Intent intent;  // 값을 받아오기 위한 intent


    // onBackPress 버튼 시간체크
    long first_time;
    long second_time;



    //fragment 사용위한 frament manager 만들기
    //framlayout 에 각각의 프레그먼트를 넣는다.
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private fragment_home  fragment_home = new fragment_home();
    private fragment_map fragment_map = new fragment_map();
    private fragment_account fragment_account = new fragment_account();
    private fragment_travel_info fragment_travel_info = new fragment_travel_info();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);








        // 툴바 만들기
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //툴바 타이틀 안보여줌


        // Drawerlayout        과 그 안에 있는 NavigationView 참조
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);



//        btn_info_account = findViewById(R.id.btn_info_account);

//
//        // nav_header 의 개인정보 상세보기 버튼 클릭
//        btn_info_account.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(), "클릭중", Toast.LENGTH_SHORT).show();
//            }
//        });






        // Drawerlayout 을 안에 있는 child를 꺼냅니다!
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();


        // NavigationView의 nav_header_view 의 값을 바꾸기 위해서는 getHeaderView 함수를 이용한다.

        View nav_header_view = navigationView.getHeaderView(0);
        nav_header_email = (TextView)nav_header_view.findViewById(R.id.textView_email);
        nav_header_nick_name = (TextView)nav_header_view.findViewById(R.id.textView_nickname);
        nav_header_info_account = (ImageButton)nav_header_view.findViewById(R.id.btn_info_account);



        // 프로필 화면으로 이동
        nav_header_info_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(), "정보더보기 클릭", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), ManageAccountActivity.class);
                intent.putExtra("nickname", nickname);
                startActivity(intent);
            }
        });


        //drawlayer 네비게이션 아이템 선택 리스너
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_live:
                        Toast.makeText(getApplicationContext(), "라이브 방송 클릭", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), LiveBroadcastActivity.class);
                        startActivity(intent);

                        break;
                    case R.id.nav_vod:
                        Toast.makeText(getApplicationContext(), "vod 방송 올리기", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.nav_my_broadcast_room:
                        Toast.makeText(getApplicationContext(), "내 방송국 가기", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.nav_my_coin_status:
                        Toast.makeText(getApplicationContext(), "코인현황", Toast.LENGTH_SHORT).show();
                        break;
                }

                return true;
            }
        });



        /**
         *
         * 이제 밑 부분인 bottonNavication 부분이다.
         */
        // bottomNavigationView 에 대한 참조를 한다
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);


        // fragment manager를 활용해 어디에 fragment를 나둘지 정하고.
        // fragmentReansaction 을 활용해 어디에 프레그먼트를 두고, 처음에 오게되는 프레그먼트를 정한다.
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment_home).commitAllowingStateLoss();


        //bottomNavigation의 아이템선택 리스너를 만든다. 선택시에는 fragmentTransaction 을 이용해 해당 fragment로 교체한다.

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                switch (menuItem.getItemId()){
                    case R.id.navigation_home:
                        transaction.replace(R.id.frame_layout, fragment_home).commitAllowingStateLoss();
                        break;
                    case R.id.navigation_map:
                        transaction.replace(R.id.frame_layout, fragment_map).commitAllowingStateLoss();
                        break;
                    case R.id.navigation_trip:
                        transaction.replace(R.id.frame_layout, fragment_travel_info).commitAllowingStateLoss();
                        break;
                    case R.id.navigation_account:
                        transaction.replace(R.id.frame_layout, fragment_account).commitAllowingStateLoss();
                }
                return true;
            }
        });






    }

    @Override
    protected void onStart() {
        super.onStart();
        intent = getIntent();
        Logg.i("-------------------------TEST--------------------");
        id = intent.getStringExtra("id");
        Logg.i("------------------------TEST---------------------- id : " + id);
        String strUrl = "http://" + IPclass.IP_ADDRESS + "/main/main.php";
        RequsetAccount requsetAccount = new RequsetAccount();
        requsetAccount.execute(strUrl, id); //접속할 서버주소와 아이디를 key 값인 id를 넘긴다.
    }

    //--------------------------------------------------서버 요청---------------------------------------------
    class RequsetAccount extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String param_strUrl = params[0];
            String param_id = params[1];

            Logg.i("-------------------------TEST----------------------" + "id : " + param_id + "// strurl : " + param_strUrl);

            String postParameter = "id=" + param_id;

            Logg.i("--------------------------TEST-------------------" + "postParameter : " + postParameter);



            try{
                URL url = new URL(param_strUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                Logg.i("--------------------TEST-----------------");

                httpURLConnection.setRequestMethod("POST");
                Logg.i("--------------------TEST-----------------");
                httpURLConnection.setConnectTimeout(5000);
                Logg.i("--------------------TEST-----------------");
                httpURLConnection.setReadTimeout(5000);
                Logg.i("--------------------TEST-----------------");
                httpURLConnection.setDoInput(true); //?
                Logg.i("--------------------TEST-----------------");
                httpURLConnection.setDoOutput(true);
                Logg.i("--------------------TEST-----------------");
                httpURLConnection.connect();
                Logg.i("--------------------TEST-----------------");


                OutputStream outputStream = httpURLConnection.getOutputStream();
                Logg.i("--------------------TEST-----------------");

                outputStream.write(postParameter.getBytes("UTF-8"));
                Logg.i("--------------------TEST-----------------");
                outputStream.flush();
                Logg.i("--------------------TEST-----------------");
                outputStream.close();
                Logg.i("--------------------TEST-----------------");


                Logg.i("--------------------TEST-----------------");
                int requsetStatus = httpURLConnection.getResponseCode();
                Logg.i("--------------------TEST-----------------");

                InputStream inputStream;

                if(requsetStatus == HttpURLConnection.HTTP_OK){
                    Logg.i("--------------------TEST-----------------");
                    inputStream = httpURLConnection.getInputStream();
                    Logg.i("--------------------TEST-----------------응답코드 : " + requsetStatus);
                }else{
                    Logg.i("--------------------TEST-----------------");
                    inputStream = httpURLConnection.getErrorStream();
                    Logg.i("--------------------TEST-----------------");
                }
                Logg.i("--------------------TEST-----------------");
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                Logg.i("--------------------TEST-----------------");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                Logg.i("--------------------TEST-----------------");

                StringBuffer stringBuffer = new StringBuffer();
                Logg.i("--------------------TEST-----------------");
                String line = null;
                Logg.i("--------------------TEST-----------------");

                Logg.i("--------------------TEST-----------------");

                while((line = bufferedReader.readLine()) != null){
                    Logg.i("--------------------TEST-----------------");
                    stringBuffer.append(line);
                    Logg.i("--------------------TEST-----------------");
                }

                Logg.i("--------------------TEST-----------------");
                bufferedReader.close();
                Logg.i("--------------------TEST-----------------");
                System.out.println("가져온 값 : " + stringBuffer.toString());
                Logg.i("--------------------TEST-----------------");

                return  stringBuffer.toString();


            }catch (Exception e){
                e.getMessage();
                Logg.i("---------------------TEST--------------------" + e.getMessage());
                return  e.getMessage();
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            Logg.i("--------------TEST---------------- response : " + response);

            try {
                JSONObject jsonObject =new JSONObject(response);
                System.out.println("jsonObject.getString(\"user_email\") : " +jsonObject.getString("user_email"));
                System.out.println("jsonObject.getString(\"nick_name\"); : "+ jsonObject.getString("nick_name"));

                /*
                전달 받은 것을 넣어주자
                 */


                /**
                 *
                 * 우선 발표
                 * 모든 버튼이 있으면 이상하니까
                 *
                 */
                Logg.i("--------------------무엇을 가져올까?--------------- jsonObject.getString(\"sns\").equals(\"kakao\") : " + jsonObject.getString("sns").equals("kakao"));


                if(!(jsonObject.getString("sns").equals(""))){
                    jsonObject.getString("sns");
                    Logg.i("--------------TEST----------------- user_email" + jsonObject.getString("user_email"));
                }
                nav_header_nick_name.setText(jsonObject.getString("nick_name"));
                nickname = jsonObject.getString("nick_name");

                Logg.i("--------------TEST----------------- nick name : " + jsonObject.getString("nicK_name"));
                nav_header_email.setText(jsonObject.getString("user_email"));

                Logg.i("--------------TEST----------------- sns : " + jsonObject.getString("sns"));





                // 저장한다.
                SharedPreferences sharedPreferences = getSharedPreferences("file", MODE_PRIVATE);
                Logg.i("--------------------test---------------------");
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Logg.i("--------------------test---------------------");
                Logg.i("--------------------test--------------------- id :" + id);
                editor.putString("id", id);
                Logg.i("--------------------test---------------------");
                editor.commit();
                Logg.i("--------------------test---------------------");



            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }




    // ===========================================================툴바 메뉴==============================================================


    // Toolbar에 menu_home_toolbar.xml 을 인플레이트 함
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_home_toolbar, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_notice: // 알림 버튼
                break;


            case R.id.menu_item_search: // 검색 버튼
                break;


            case R.id.menu_item_subscribe: // 구독한 비디오 보기 내역 버튼
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
