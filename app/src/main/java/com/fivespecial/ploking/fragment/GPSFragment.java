package com.fivespecial.ploking.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.fivespecial.ploking.R;
import com.fivespecial.ploking.base.BaseFragment;
import com.fivespecial.ploking.maps.BinLocation;
import com.fivespecial.ploking.maps.Calculation;
import com.fivespecial.ploking.maps.DataAdapter;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapOptions;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GPSFragment extends BaseFragment {

    //custom-info-window
    private static class InfoWindowAdapter extends InfoWindow.DefaultTextAdapter {

        private InfoWindowAdapter(@NonNull Context context) {
            super(context);
        }

        @NonNull
        @Override
        public CharSequence getText(@NonNull InfoWindow infoWindow) {
            if (infoWindow.getMarker() != null) {
                return getContext().getString(R.string.format_info_window_on_marker, infoWindow.getMarker().getTag());
            } else {
                return getContext().getString(R.string.format_info_window_on_map,
                        infoWindow.getPosition().latitude, infoWindow.getPosition().longitude);
            }
        }
    }

    private static final String TAG = "GPSFragment";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;

    private static final double DEFAULT_CAMERA_LATITUDE = 35.232286;
    private static final double DEFAULT_CAMERA_LONGITUDE = 129.085166;
    private static final double DEFAULT_BIN_LATITUDE = 35.232286;
    private static final double DEFAULT_BIN_LONGITUDE = 129.085166;

    // Garbage Bins
    private Calculation calculationNearBin;
    private List<BinLocation> binLocationList;
    private TextView binNear1kmTextView;
    private int binCountNear1km = 0;

    // TextTimer
    private TextView txtDistance;
    private TextView txtTime;
    private TextView txtKcal;

    private double currentLat = DEFAULT_CAMERA_LATITUDE;
    private double currentLon = DEFAULT_CAMERA_LONGITUDE;
    private double lastLat = DEFAULT_CAMERA_LATITUDE;
    private double lastLon = DEFAULT_CAMERA_LONGITUDE;
    private float distance_sum = 0f;

    // Timer
    private ImageButton startButton, stopButton, pauseButton;
    private Boolean isRunning = false;
    private Boolean tButtonclicked = false;
    private Thread timeThread;

    // Calorie
    private float calorie = 0;
    private FusedLocationSource locationSource;

    // View Components
    private View runLayout;
    private View informLayout;
    private Animation pauseAppear;
    private Animation stopAppear;

    // DataAdapter
    private DataAdapter mDbHelper;

    @Override
    public int getResourceId() {
        return R.layout.fragment_gps;
    }

    @Override
    public void initComponent(View view) {

        calculationNearBin = new Calculation();
        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
        mDbHelper = new DataAdapter(getActivity());

        startButton = view.findViewById(R.id.btn_fragment_four_start);
        stopButton = view.findViewById(R.id.btn_fragment_four_stop);
        pauseButton = view.findViewById(R.id.btn_fragment_four_pause);
        runLayout = view.findViewById(R.id.layout_running);
        informLayout = view.findViewById(R.id.layout_inform);

        //animation
        pauseAppear = AnimationUtils.loadAnimation(getActivity(), R.anim.pausebtn_appear);
        stopAppear = AnimationUtils.loadAnimation(getActivity(), R.anim.stopbtn_appear);

        binNear1kmTextView = view.findViewById(R.id.tv_near_bin_notice);
        txtDistance = view.findViewById(R.id.tv_fragment_four_distance);
        txtTime = view.findViewById(R.id.tv_fragment_four_time);
        txtKcal = view.findViewById(R.id.tv_fragment_four_calorie);
    }

    @Override
    public void setupListeners(@NotNull View view) {

        // 시작버튼 클릭 리스너
        startButton.setOnClickListener((View v) -> {

            v.setVisibility(View.GONE); // start 버튼 숨김
            runLayout.setVisibility(View.VISIBLE);
            informLayout.setVisibility(View.VISIBLE);

            pauseButton.startAnimation(pauseAppear);
            stopButton.startAnimation(stopAppear);

            isRunning = true;
            timeThread = new Thread(new timeThread());
            timeThread.start();
            tButtonclicked = true;
        });

        // 스탑버튼 클릭 리스너
        stopButton.setOnClickListener((View v) ->{

            startButton.setVisibility(View.VISIBLE); // 시작버튼을 다시 보이게 함.
            runLayout.setVisibility(View.GONE);
            informLayout.setVisibility(View.GONE);

            SharedPreferences sFile = getActivity().getSharedPreferences("sFile", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sFile.edit();

            float updateDistance = sFile.getFloat("distance", 0);
            float updateKcal = sFile.getFloat("Kcal", 0);

            updateDistance += distance_sum;
            updateKcal += calorie;

            editor.putFloat("distance", updateDistance);
            editor.putFloat("Kcal", updateKcal);

            editor.apply();

            isRunning = false;
            timeThread.interrupt();
            distance_sum = 0;
        });

        // 멈춤 버튼 클릭 리스너
        pauseButton.setOnClickListener((View v) -> {
            isRunning = !isRunning;
            
            if(isRunning){
                pauseButton.setImageResource(R.drawable.pause_button);
            }else{
                tButtonclicked = true;
                pauseButton.setImageResource(R.drawable.start_button);
            }
        });
    }

    @Override
    public void setupImplementation() {

        binLocationList = mDbHelper.initLoadData();

        try {

            //API를 호출해 클라이언트 ID를 지정
            if(getActivity() != null) {
                NaverMapSdk.getInstance(getActivity()).setClient(
                        new NaverMapSdk.NaverCloudPlatformClient("0fwsvimx0a"));
            }

            // 초기 위치 설정
            NaverMapOptions options = new NaverMapOptions()
                    .camera(new CameraPosition(new LatLng(35.232286, 129.085166), 14));

            MapFragment mapFragment = MapFragment.newInstance(options);
            getFragmentManager().beginTransaction().add(R.id.fragmentBorc, mapFragment).commit();

            //비동기로 NaverMap 객체를 가져옴. NaverMap 객체가 준비되면 callback 의 onMapReady(NaverMap) 호출됨.
            mapFragment.getMapAsync(this::onMapReady);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e + "에러 발생");
        }

        // GPS 시작..
        try {

            if(getActivity() != null && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(
                        getActivity(), // activity
                        new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, // permissions
                        LOCATION_PERMISSION_REQUEST_CODE // requestCode
                );

            }

            LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            Location loc = null;

            if (lm != null) {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, setLocationListener());
                loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }

            if(loc == null){
                txtDistance.setText("No GPS location found");

            } else{
                // 현재 위도와 경도를 설정함.
                currentLon = loc.getLongitude();
                currentLat = loc.getLatitude();
            }
            // 마지막으로 측정된 위도, 경도를 저장
            lastLat = currentLat;
            lastLon = currentLon;

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,  @NonNull int[] grantResults) {

        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // 맵을 생성할 준비가 되었을 때 가장 먼저 호출
    private void onMapReady(@NonNull NaverMap naverMap) {

        InfoWindow binsInfo = new InfoWindow();
        
        double myLatitude = DEFAULT_CAMERA_LATITUDE, myLongitude = DEFAULT_CAMERA_LONGITUDE;
        double binLatitude, binLongitude;

        try {
            //position 속성을 지정하면 위치 오버레이의 좌표를 변경할 수 있다.
            //처음 생성된 위치 오버레이는 카메라의 초기 좌표에 위치해 있다.
            if(getActivity() != null) {
                LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                
                if(lm != null) {
                    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    
                    if(location != null) {
                        myLatitude = location.getLatitude();
                        myLongitude = location.getLongitude();
                    }
                }
            }

            Marker marker = new Marker();
            marker.setPosition(new LatLng(myLatitude, myLongitude));

            // UI settings
            UiSettings uiSettings = naverMap.getUiSettings();
            uiSettings.setLocationButtonEnabled(true);

            naverMap.setLocationSource(locationSource);
            naverMap.setOnMapClickListener((coord, point) -> {
                binsInfo.close();
            });
            naverMap.getLocationOverlay().setVisible(true);
            naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

            // 오버레이 : 지리적 정보를 시작적으로 나타내느 요소. 개발자가 지도 위에 자유롭게 배치할 수 있음. 마커, 정보창(InfoWindow), 셰이프 등
            LocationOverlay locationOverlay = naverMap.getLocationOverlay();
            locationOverlay.setVisible(true);
            locationOverlay.setBearing(90); // 방위를 지정함.
            locationOverlay.setIconWidth(100); // 아이콘의 너비를 지정함
            locationOverlay.setIconHeight(100); // 아이콘의 높이를 지정함.
            locationOverlay.setPosition(new LatLng(myLatitude, myLongitude)); // 위치 지정
            
        }  catch(SecurityException | NullPointerException e){
            Log.v(TAG, "CANNOT be find GPS signal.");
        }
        

        for(BinLocation binLocation : binLocationList){

            if(binLocation.getLatitude() != null && binLocation.getLongitude() != null) {
                binLatitude = binLocation.getLatitude();
                binLongitude = binLocation.getLongitude();
            } else {
                binLatitude = DEFAULT_BIN_LATITUDE;
                binLongitude = DEFAULT_BIN_LONGITUDE;
            }

            binsInfo.setAdapter(new InfoWindowAdapter(getActivity()));
            binsInfo.setOnClickListener(overlay -> {
                binsInfo.close();
                return true;
            });

            Marker binMarker = new Marker();
            binMarker.setPosition(new LatLng(binLatitude, binLongitude));
            binMarker.setIcon(OverlayImage.fromResource(R.drawable.bin_marker));
            binMarker.setOnClickListener(overlay -> {
                binsInfo.open(binMarker);
                return true;
            });
            binMarker.setTag(binLocation.getDescription());
            binMarker.setMap(naverMap);
        }

        binCountNear1km = calculationNearBin.nearBins(binLocationList, myLatitude, myLongitude);
        binNear1kmTextView.setText(getString(R.string.near_bin_notice, binCountNear1km));

    }

    private LocationListener setLocationListener() {

        return new LocationListener(){

            @Override
            public void onLocationChanged(Location location) {

                try{
                    //start location manager
                    if(getActivity() != null) {
                        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

                        if(lm != null) {
                            //Request new location
                            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, this);

                            //Get new location
                            Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                            if(loc != null) {
                                //get the current lat and long
                                currentLat = loc.getLatitude();
                                currentLon = loc.getLongitude();
                            }

                            binCountNear1km = calculationNearBin.nearBins(binLocationList, currentLat, currentLon);

                            binNear1kmTextView.setText(getString(R.string.near_bin_notice, binCountNear1km));
                        }
                    }
                } catch(SecurityException | NullPointerException e){
                    e.printStackTrace();
                }

                if(isRunning){
                    if(tButtonclicked){
                        lastLat = currentLat;
                        lastLon = currentLon;
                        tButtonclicked = false;
                    }

                    Location locationA = new Location("point A");
                    locationA.setLatitude(lastLat);
                    locationA.setLongitude(lastLon);

                    Location locationB = new Location("point B");
                    locationB.setLatitude(currentLat);
                    locationB.setLongitude(currentLon);

                    float distanceMeters = locationA.distanceTo(locationB);
                    Log.v(TAG, String.format("%f", lastLon));
                    Log.v(TAG, String.format("%f", currentLon));

                    distance_sum += distanceMeters;

                    lastLat = currentLat;
                    lastLon = currentLon;

                    txtDistance.setText(String.format("%.1f m",distance_sum ));
                }
            }

            @Override
            public void onProviderDisabled(String provider) { }

            @Override
            public void onProviderEnabled(String provider) { }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) { }

        };
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            //1000이 1초 1000*60 은 1분 1000*60*10은 10분 1000*60*60은 한시간
            int sec_sum = msg.arg1;
            int sec = msg.arg1 % 60;
            int min = msg.arg1 / 60;

            int weight = 70; //소모 칼로리(kcal)
            float coef = 0.001f; // 1초당 운동계수
            calorie = weight * coef * sec_sum;

            @SuppressLint("DefaultLocale") String result = String.format("%02d:%02d", min, sec);
            @SuppressLint("DefaultLocale") String kcalResult = String.format("%.0f", calorie) + " kcal";

            txtTime.setText(result);
            txtKcal.setText(kcalResult);
        }
    };

    public class timeThread implements Runnable {
        @Override
        public void run() {
            int i = 0;

            while (true) {

                //일시정지를 누르면 멈춤
                while (isRunning) {
                    Log.d("stopwatch", "while start");
                    Message msg = new Message();
                    msg.arg1 = i++;
                    handler.sendMessage(msg);

                    try {
                        Thread.sleep(1000);
                        Log.d("stopwatch", "sleep");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        //test
                        getActivity().runOnUiThread(() -> {

                            txtTime.setText("");
                            txtTime.setText("00:00");
                        });
                        return;
                    }
                }
            }
        }
    }
}
