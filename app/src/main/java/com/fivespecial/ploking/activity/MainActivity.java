package com.fivespecial.ploking.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;

import com.fivespecial.ploking.R;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 권한 요청이 필요한 Permission 을 String Array 에 추가
        String[] requiredPermissions = new String[]{ Manifest.permission.ACCESS_FINE_LOCATION };

        //권한 요청
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        //권한 설정이 되어 있는지 확인
        if(permissionCheck != PackageManager.PERMISSION_GRANTED){

            if(!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                // 사용자가 권한 요청을 거부하지 않았다면, 이 분기를 타게 됨.
                ActivityCompat.requestPermissions(this, requiredPermissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }

        Handler handler = new Handler();
        handler.postDelayed(() -> {

            // 설정한 delay 후에 TabbedActivity 실행
            startActivity(new Intent(getBaseContext(),TabbedActivity.class));

            finish();
        }, 1500);


    }

}
