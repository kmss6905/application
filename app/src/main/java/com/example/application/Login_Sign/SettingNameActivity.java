package com.example.application.Login_Sign;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.example.application.IPclass;
import com.example.application.Logg;
import com.example.application.R;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *  이메일로 가입하기를 끝내고 활성화링크 까지 클릭한 유저는 최종적으로 닉네임을 설정해야 메인엑티비티로 이동할 수 있다.
 *  닉네임의 경우 중복을 허용하지 않으며, 특수문자, 영어를 사용하여 만들 수 있으며 10글자 이내만 가능하다
 *  닉네임을 입력하고 확인 버튼을 누르면
 *  입력했던 닉네임이 서버로 전달된다.
 *  서버에서는 해당 닉네임이 올바른 닉네임 조건을 충족하고있는 지 검사한다.
 *  1. 닉네임 형식이 올바른지
 *  2. 중복된 닉네임이 아닌지
 *  이 두가지를 체크한 후 1.닉네임 형식이 올바르지 않은, 경우에는 닉네임형식이 올바르지
 *  않다라고 하는 메세지를 띄운다.
 * 중복된 닉네임일  경우 중복된 닉네임이라는 메세지를 닉네임 설정 화면에 띄운다.
 *
 */

public class SettingNameActivity extends AppCompatActivity {


    Button ok_btn;
    EditText editText_nick;
    Toolbar toolbar;

    Intent intent; // id 값 받기위한 intent


    String id; //받은 id 값
    String get_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_name);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);



        // 로그인시 전달받은 id 값

        intent = getIntent();
        id = intent.getStringExtra("id");
        get_name = intent.getStringExtra("name");


        System.out.println("id(받은 유일한 아이디값) : " + id);
        System.out.println("name(이름) : " + get_name);


        ok_btn = findViewById(R.id.ok_btn); //확인 버튼
        editText_nick = findViewById(R.id.editText_nick); // 닉네임 입력창
        editText_nick.setText(get_name);


        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nick_name = editText_nick.getText().toString();

                /**
                 * 우선 클라에서도 기본적인 닉네임조건을 확인한다.
                 * 닉네임이 입력되어있는지 확인한다.
                 *
                 */

                //아무것도 입력되어 있지 않은 경우
                if(nick_name.equals("")){
                    Toast.makeText(SettingNameActivity.this, "닉네임이 없습니다. \n 닉네임을 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                    SettingNickName settingNickName = new SettingNickName();
                    Logg.i("---------test---------");
                    settingNickName.execute("http://" + IPclass.IP_ADDRESS + "/SignUp_login/settingNickName.php", id, nick_name);
                    Logg.i("http://" + IPclass.IP_ADDRESS + "/SignUp_login/settingNickName.php");

            }
        });

    }


    class SettingNickName extends AsyncTask<String, Void, String>{


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            System.out.println("POST response : " + result);

            try {

                JSONObject jsonObject = new JSONObject(result); // 결과를 가져와서 jsonobject로 만든다. 왜 ? 그게 편하니까!
                System.out.println("state" +  jsonObject.getString("state"));


                switch (jsonObject.getString("state")){

                    case "양식안맞음": // 입력한 닉네임이 양식에 맞지않을 경우
                        Toast.makeText(SettingNameActivity.this, "닉네임 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                        break;

                    case "닉네임넣음": // 입력한 닉네임이 등록되었을 경우
                        Toast.makeText(SettingNameActivity.this, "닉네임이 등록되었습니다.", Toast.LENGTH_SHORT).show();

                        //이제 메인화면으로 이동
                        Intent intent = new Intent(SettingNameActivity.this, MainActivity2.class);
                        Logg.i("-------------------------TEST--------------------");
                        intent.putExtra("id", jsonObject.getString("id"));
                        Logg.i("-------------------------TEST--------------------");
                        startActivity(intent);
                        Logg.i("-------------------------TEST--------------------");
                        finish();
                        break;

                    case "닉네임중복": // 입력한 닉네임이 중복되는 경우
                        Toast.makeText(SettingNameActivity.this, "중복된 닉네임입니다.", Toast.LENGTH_SHORT).show();
                        break;
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }




        }

        @Override
        protected String doInBackground(String... params) {
            String severUrl = (String)params[0];

            String id = (String)params[1];
            String nick = (String)params[2];


            String postParameter = "id=" + id + "&nick=" + nick;




            try{
                URL serverUrl = new URL(severUrl);
                Logg.i("---------test---------");
                HttpURLConnection httpURLConnection = (HttpURLConnection) serverUrl.openConnection();
                Logg.i("---------test---------");
                httpURLConnection.setRequestMethod("POST");
                Logg.i("---------test---------");
                httpURLConnection.setConnectTimeout(5000);
                Logg.i("---------test---------");
                httpURLConnection.setReadTimeout(5000);
                Logg.i("---------test---------");
                httpURLConnection.setDoOutput(true);
                Logg.i("---------test---------");
                httpURLConnection.setDoInput(true);
                Logg.i("---------test---------");
                httpURLConnection.connect(); // 연결!
                Logg.i("---------test---------");


                OutputStream outputStream = httpURLConnection.getOutputStream();
                Logg.i("---------test---------");

                System.out.println("postParameter : " + postParameter);
                Logg.i("---------test---------");
                outputStream.write(postParameter.getBytes("UTF-8")); // 나가는 통로에 데이터 입력!
                Logg.i("---------test---------");
                outputStream.flush();
                Logg.i("---------test---------");
                outputStream.close(); // 나가는 통로는 닫고!
                Logg.i("---------test---------");


                /**
                 *
                 * 연결 되었을 떄 값을 받아오자!
                 */

                InputStream inputStream; //미리 받아올 통로!
                Logg.i("---------test---------");

                int responseStatus = httpURLConnection.getResponseCode(); // 우선 잘 연결 되었나?
                Logg.i("---------test---------");

                if(responseStatus == HttpURLConnection.HTTP_OK){ // 잘 연결 되었다면 200
                    Logg.i("---------test---------");
                    inputStream = httpURLConnection.getInputStream(); // 연결된 녀석의 들어오는 통로를 가져온 후 내가 만든 통로에!
                }else{ // 잘 못받아 왔어?
                    Logg.i("---------test---------");
                    inputStream = httpURLConnection.getErrorStream(); // inputStream에는 없을 테니 에러 통로를 가져오장
                }

                //이제 가져온 것을 해석해야지! (inputStream에 넣은 것은 날것 그대로야! 우리가 알아 볼 수 있게 해석해야해 !
                // 그떄 사용하는 스트림이 있는 데 inputStreamReader!

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                Logg.i("---------test---------");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);//하지만 존나 정보가 마구잡이로 날라오기 떄문에 버퍼리더로 해결하자구! 다시 새로운 통로~
                Logg.i("---------test---------");
                StringBuffer sb = new StringBuffer(); //이게 진짜 우리의 익숙한 형태로 유용한 기능을 하는 StringBuffer!
                Logg.i("---------test---------");
                String line = null;
                Logg.i("---------test---------");



                while((line = bufferedReader.readLine()) != null) { //이제 버퍼리더에 들어간 녀석을 라인별로 가져오자구! 읽고 넣고 했는 데 다 했어
                    Logg.i("---------test---------");
                    sb.append(line);
                }



                //그리고 아까 설치했던 버퍼리더는 닫자!
                bufferedReader.close();
                Logg.i("---------test---------");

                System.out.println("가져온 값 : " + sb.toString());

                return  sb.toString();




            }catch (Exception e){
                Logg.i("---------test---------");
                return new String("Error: " + e.getMessage());
            }
        }
    }





    /**
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_register, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
