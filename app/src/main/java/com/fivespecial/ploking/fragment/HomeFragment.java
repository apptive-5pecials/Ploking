package com.fivespecial.ploking.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Looper;
import android.text.Html;
import android.util.Pair;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fivespecial.ploking.R;
import com.fivespecial.ploking.RequestCode;
import com.fivespecial.ploking.activity.TabbedActivity;
import com.fivespecial.ploking.base.BaseFragment;
import com.fivespecial.ploking.maps.BinLocation;
import com.fivespecial.ploking.maps.DataAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends BaseFragment {

    private static final int LOCATION_REQUEST_PRIORITY = LocationRequest.PRIORITY_HIGH_ACCURACY;
    private static final long LOCATION_REQUEST_INTERVAL = 3000;
    private static final long LOCATION_REQUEST_FASTEST_INTERVAL = LOCATION_REQUEST_INTERVAL / 2;

    private TextView tvDistance;
    private TextView tvKcal;
    private ImageView trashButton;
    private ImageButton cameraButton;

    private TextView nearestBinDescription;
    private TextView nearestBinDistance;

    private List<BinLocation> mBinLocationList;
    private DataAdapter mHomeDbHelper;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;

    @Override
    public int getResourceId() {
        return R.layout.fragment_home;
    }

    @Override
    public void initComponent(@NotNull View view) {
        mHomeDbHelper = new DataAdapter(getActivity());

        //tvDistance = view.findViewById(R.id.home_steps);
        tvKcal = view.findViewById(R.id.fragment_home_text_view_today_cal);
        cameraButton = view.findViewById(R.id.fragment_home_image_button_camera);

        nearestBinDescription = view.findViewById(R.id.fragment_home_nearest_bin_description_text_view);
        nearestBinDistance = view.findViewById(R.id.fragment_home_nearest_bin_distance_text_view);
    }

    @Override
    public void setupListeners(@NotNull View view) {

        cameraButton.setOnClickListener(v -> {
            if(getActivity() != null) {
                Intent intent = new Intent(getActivity().getApplicationContext(), CameraActivity.class);
                startActivityForResult(intent, RequestCode.FRAGMENT_HOME_CAMERA_ACTIVITY_REQ_CODE);
            }
        });

    }

    @Override
    public void setupImplementation() {

        if (getActivity() != null) {

            mBinLocationList = mHomeDbHelper.initLoadData();

            setupFusedLocationClient();
            setLocationRequest();
            setLocationCallback();

            SharedPreferences sFile = getActivity().getApplicationContext().getSharedPreferences("sFile", Context.MODE_PRIVATE);

            //float ftKcal = sFile.getFloat("Kcal", 0);
            //tvKcal.setText("백선욱 님은 총\n"+ String.format("%.0f", ftKcal) + "kcal를 소비했습니다.");
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK) {

            if(requestCode == RequestCode.FRAGMENT_HOME_CAMERA_ACTIVITY_REQ_CODE) {
                if(getActivity() != null){
                    TabbedActivity tabbedActivity = ((TabbedActivity) getActivity());
                    tabbedActivity.refreshAlbumFragment();
                }
            }
        }
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

                if(locationResult != null && locationResult.getLocations().size() == 1) {

                    // 가장 근접한 쓰레기통을 구하고, 두 지점 사이의 거리를 구함.
                    Location myLocation = locationResult.getLocations().get(0);
                    ArrayList<Pair<BinLocation, Double>> tempBinList = new ArrayList<>();

                    for(BinLocation bin : mBinLocationList) {

                        double distanceTo = myLocation.distanceTo(bin.toLocation());

                        tempBinList.add(new Pair<>(bin, distanceTo));
                    }

                    Pair<BinLocation, Double> nearestBin = tempBinList.get(0); // 임의로 가장 가까운 쓰레기통을 tempList[0] 으로 설정

                    for(Pair<BinLocation, Double> pair : tempBinList) {
                        if(nearestBin.second.compareTo(pair.second) >= 0) {
                            nearestBin = pair;
                        }
                    }

                    showNearestBinLocation(nearestBin);

                }
            }
        };
    }

    private void showNearestBinLocation(Pair<BinLocation, Double> nearBinLocation) {

        if(nearestBinDescription != null && nearestBinDistance != null) {

            String description = Html.fromHtml("<u>" + nearBinLocation.first.getDescription() + "</u>").toString();
            nearestBinDescription.setText(description);

            int integerDistance = nearBinLocation.second.intValue();

            if(integerDistance >= 1000) { // 거리가 1000m 이상이라면 KM로 나타냄.
                nearestBinDistance.setText(getString(R.string.fragment_home_nearest_bin_distance_format_kilo_meter, (integerDistance / 1000)));

            } else { // 거리가 1000m 이하라면 Meter 로 나타냄.
                nearestBinDistance.setText(getString(R.string.fragment_home_nearest_bin_distance_format_meter, integerDistance));
            }

        }
    }
}