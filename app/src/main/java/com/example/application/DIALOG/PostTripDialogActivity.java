package com.example.application.DIALOG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActivityChooserView;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.example.application.R;

public class PostTripDialogActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_post_trip_info);
    }


}
