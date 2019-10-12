package com.fivespecial.ploking.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fivespecial.ploking.R;
import com.fivespecial.ploking.activity.TrashActivity;
import com.fivespecial.ploking.async.CheckTypesTask;
import com.fivespecial.ploking.base.BaseFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.jetbrains.annotations.NotNull;

public class HomeFragment extends BaseFragment {

    private static final int LOCATION_REQUEST_PRIORITY = LocationRequest.PRIORITY_HIGH_ACCURACY;
    private static final long LOCATION_REQUEST_INTERVAL = 3000;
    private static final long LOCATION_REQUEST_FASTEST_INTERVAL = LOCATION_REQUEST_INTERVAL / 2;

    private TextView tvDistance;
    private TextView tvKcal;
    private ImageView trashButton;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;

    @Override
    public int getResourceId() {
        return R.layout.fragment_home;
    }

    @Override
    public void initComponent(@NotNull View view) {
        tvDistance = view.findViewById(R.id.home_steps);
        tvKcal = view.findViewById(R.id.home_kcals);
        trashButton = view.findViewById(R.id.button_trash);
    }

    @Override
    public void setupListeners(@NotNull View view) {

        trashButton.setOnClickListener(v -> {

            if(getActivity() != null) {
                CheckTypesTask task = new CheckTypesTask(getActivity());
                task.execute();
            }

            Intent intent = new Intent(getActivity(), TrashActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void setupImplementation() {

        if (getActivity() != null) {

            setupFusedLocationClient();
            setLocationRequest();
            setLocationCallback();

            SharedPreferences sFile = getActivity().getApplicationContext().getSharedPreferences("sFile", Context.MODE_PRIVATE);

            float ftDistance = sFile.getFloat("distance", 0);
            float ftKcal = sFile.getFloat("Kcal", 0);

            ftDistance = ftDistance / 1000; // 미터->킬로미터

            tvDistance.setText("총 활동량 \n" + String.format("%.1f", ftDistance) + " km");
            tvKcal.setText("백선욱 님은 총\n"+ String.format("%.0f", ftKcal) + "kcal를 소비했습니다.");
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        requestLocationUpdates(); // Fragment 의 생명주기가 onStart 상태이면, 위치 업데이트를 등록함.
    }

    @Override
    public void onStop() {
        super.onStop();

        removeLocationUpdates(); // Fragment 의 생명주기가 onStop 상태이면, 위치 업데이트를 해제함.
    }

    private void setupFusedLocationClient() {

        if(getActivity() != null) {

            // 주기적인 위치 업데이트를 받기 위해서 FusedLocationClient 를 사용함.
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        }
    }

    private void requestLocationUpdates() {
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private void removeLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private void setLocationRequest() {
        // 위치 업데이트를 등록할때, 등록되는 업데이트의 속성을 설정해주는 함수

        mLocationRequest = LocationRequest.create();

        mLocationRequest.setPriority(LOCATION_REQUEST_PRIORITY);
        mLocationRequest.setInterval(LOCATION_REQUEST_INTERVAL);
        mLocationRequest.setFastestInterval(LOCATION_REQUEST_FASTEST_INTERVAL);

    }

    private void setLocationCallback() {
        // 정상적으로 위치 업데이트를 받았을 경우, 수신받는 LocationResult 콜백을 설정해주는 함수

        mLocationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {

                if(locationResult != null) {

                    for(Location location : locationResult.getLocations()) {
                        Log.d("LocationResult", "LocationResult.location size : " + locationResult.getLocations().size());
                        Log.d("LocationResult", "위도 : " + location.getLatitude() + ", 경도 : " + location.getLongitude());
                    }

                }
            }
        };

    }
}
