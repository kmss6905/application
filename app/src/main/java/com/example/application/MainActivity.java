package com.example.application;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.JsonReader;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import request.URLConnector;

public class MainActivity extends AppCompatActivity {

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String test = "http://" + IPclass.IP_ADDRESS + "/test1.php";
        URLConnector task = new URLConnector(test);

        task.start();





        try{
            task.join();
            System.out.println("waiting... for result");
        }
        catch(InterruptedException e){

    }

        String result = task.getResult();

        System.out.println(result);

        TextView textView = findViewById(R.id.textView);



        try {
            JSONObject obj = new JSONObject(result);

            System.out.println(obj);

            id = obj.getString("id"); //ket 값으로 가져옴

            System.out.println(id);

            textView.setText(id);


        } catch (JSONException e) {
            e.printStackTrace();
        }



    }



}
