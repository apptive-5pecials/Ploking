package com.fivespecial.ploking.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.fivespecial.ploking.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, TabbedActivity.class);
        startActivity(intent);
        finish();

        /*
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent1 = new Intent(getBaseContext(),TabbedActivity.class);
                startActivity(intent1);
                finish();
            }
        }, 2000);

         */
    }

}
