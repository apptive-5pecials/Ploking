package com.fivespecial.ploking.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.fivespecial.ploking.R;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;

import static android.content.Context.LOCATION_SERVICE;

public class GPSFragment extends Fragment {

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


    //txtTimer
    TextView txtDistance;
    private TextView txtTime;
    TextView txtKcal;

    double currentLon=0;
    double currentLat=0;
    double lastLon = 0;
    double lastLat = 0;
    double distance_sum = 0;

    //timer
    private Button startBtn, stopBtn, pauseBtn;
    private Boolean isRunning = false;
    private Boolean tButtonclicked = false;
    Thread timeThread;


    //kcal
    int weight = 70;
    final double coef = 0.001; // 1초당 운동계수
    double calory = 0;

    private FusedLocationSource locationSource;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private MapFragment mapFragment;

    public GPSFragment() {

    }

    public static GPSFragment newInstance(){
        return new GPSFragment();
    }

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        NaverMapSdk.getInstance(getActivity()).setClient(
                new NaverMapSdk.NaverCloudPlatformClient("0fwsvimx0a"));

        //API를 호출해 클라이언트 ID를 지정
        NaverMapSdk.getInstance(getActivity()).setClient(
                new NaverMapSdk.NaverCloudPlatformClient("0fwsvimx0a"));

        FragmentManager fm = getFragmentManager();
        mapFragment = (MapFragment)this.getChildFragmentManager().
                findFragmentById(R.id.map);
        if(mapFragment == null){
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.fragmentBorc, mapFragment).commit();
        }

        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
        //test
        mapFragment.getMapAsync(this::onMapReady);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view;
        view = inflater.inflate(R.layout.fragment_gps, null);

        //define views...
        startBtn = view.findViewById(R.id.btn_fragment_four_start);
        stopBtn = view.findViewById(R.id.btn_fragment_four_stop);
        pauseBtn = view.findViewById(R.id.btn_fragment_four_pause);

        txtDistance = (TextView) view.findViewById(R.id.tv_fragment_four_distance);
        txtTime = (TextView) view.findViewById(R.id.tv_fragment_four_time);
        txtKcal = (TextView) view.findViewById(R.id.tv_fragment_four_calorie);

        startBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                v.setVisibility(View.GONE);
                pauseBtn.setVisibility(View.VISIBLE);
                stopBtn.setVisibility(View.VISIBLE);

                isRunning = true;
                timeThread = new Thread(new timeThread());
                timeThread.start();
                tButtonclicked = true;
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startBtn.setVisibility(View.VISIBLE);
                pauseBtn.setVisibility(View.GONE);
                v.setVisibility(View.GONE);
                isRunning = false;
                timeThread.interrupt();
                txtTime.setText("00:00");
                distance_sum = 0;
                txtDistance.setText("0.0 m");
            }
        });

        pauseBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                isRunning = !isRunning;
                if(isRunning){
                    pauseBtn.setText("일시정지");
                }else{
                    tButtonclicked = true;
                    pauseBtn.setText("시작");
                }
            }
        });

        //GPS start...
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);   //test

        if(permissionCheck == PackageManager.PERMISSION_DENIED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)){}  //test
            else{
                ActivityCompat.requestPermissions(getActivity(),                                    //test
                        new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }



        LocationManager lm =(LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        lm.requestLocationUpdates(lm.GPS_PROVIDER, 0,0, Loclist);
        Location loc = lm.getLastKnownLocation(lm.GPS_PROVIDER);

        if(loc==null){
            txtDistance.setText("No GPS location found");
        }
        else{
            //set Current latitude and longitude
            currentLon=loc.getLongitude();
            currentLat=loc.getLatitude();
        }
        //Set the last latitude and longitude
        lastLat=currentLat;
        lastLon=currentLon;
        //GPS end...


        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,  @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions, grantResults)) {
            return;
        }
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);
    }

    // 맵을 생성할 준비가 되었을 때 가장 먼저 호출
    public void onMapReady(@NonNull NaverMap naverMap) {

        double longitude, latitude;

        final LocationManager lm = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        Location location;

        //uisetting
        UiSettings uiSettings = naverMap.getUiSettings();

        naverMap.setLocationSource(locationSource);

        LocationOverlay locationOverlay = naverMap.getLocationOverlay();
        locationOverlay.setVisible(true);

        //marker
        final InfoWindow binsInfo = new InfoWindow();
        binsInfo.setPosition(new LatLng(35.2315244, 129.0848256));
        binsInfo.setAdapter(new InfoWindowAdapter(getActivity()));
        binsInfo.setOnClickListener(overlay -> {
            binsInfo.close();
            return true;
        });


        Marker bins = new Marker();
        bins.setPosition(new LatLng(35.2315244,129.0848256));
        bins.setIcon(OverlayImage.fromResource(R.drawable.bin_marker));
        bins.setHeight(120);
        bins.setWidth(120);
        bins.setOnClickListener(overlay -> {
            binsInfo.open(bins);
            return true;
        });
        bins.setTag("부산대 정문(칠성사이다)");
        bins.setMap(naverMap);


        try {
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            //position 속성을 지정하면 위치 오버레이의 좌표를 변경할 수 있다.
            //처음 생성된 위치 오버레이는 카메라의 초기 좌표에 위치해 있다.
        }
        catch(SecurityException | NullPointerException e){
            longitude = 129.04378;
            latitude = 35.237396;
            //권한이 없으면 oncreate시 내 위치 찾는 것을 못한다.
        }
        locationOverlay.setPosition(new LatLng(latitude, longitude));

        Marker marker = new Marker();
        marker.setPosition(new LatLng(latitude, longitude));


        //bearing 속성을 지정하면 위치 오버레이의 베어링을 변경할 수 있다.
        // flat이 true인 마커의 angle과 유사하게 아이콘이 지도를 기준으로 회전한다.
        locationOverlay.setBearing(90);

        locationOverlay.setIconWidth(100);
        locationOverlay.setIconHeight(100);

        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

        //uiSettings...
        uiSettings.setLocationButtonEnabled(true);

    }

    LocationListener Loclist = new LocationListener(){

        @Override
        public void onLocationChanged(Location location) {

            if(isRunning){
                try{
                    //start location manager
                    LocationManager lm =(LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

                    //Request new location
                    lm.requestLocationUpdates(lm.GPS_PROVIDER, 0,0, Loclist);

                    //Get new location
                    Location loc = lm.getLastKnownLocation(lm.GPS_PROVIDER);

                    //get the current lat and long
                    currentLat = loc.getLatitude();
                    currentLon = loc.getLongitude();

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

                    double distanceMeters = locationA.distanceTo(locationB);
                    Log.v("lastLocation", String.format("%f", lastLon));
                    Log.v("currentLocation", String.format("%f", currentLon));

                    distance_sum += distanceMeters;

                    lastLat = currentLat;
                    lastLon = currentLon;

                    txtDistance.setText(String.format("%.1f m",distance_sum ));
                }
                catch(SecurityException e){
                }
            }

        }

        @Override
        public void onProviderDisabled(String provider) {
        }



        @Override
        public void onProviderEnabled(String provider) {
        }



        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

    };

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int sec_sum = msg.arg1;
            int sec = msg.arg1 % 60;
            int min = msg.arg1 / 60;
            //1000이 1초 1000*60 은 1분 1000*60*10은 10분 1000*60*60은 한시간

            //소모 칼로리
            calory = weight * coef * sec_sum;

            @SuppressLint("DefaultLocale") String result = String.format("%02d:%02d", min, sec);
            @SuppressLint("DefaultLocale") String kcalResult = String.format("%.0f", calory) + " kcal";

            txtTime.setText(result);
            txtKcal.setText(kcalResult);
        }
    };

    public class timeThread implements Runnable {
        @Override
        public void run() {
            int i = 0;

            while (true) {
                while (isRunning) { //일시정지를 누르면 멈춤
                    Log.d("stopwatch", "while start");
                    Message msg = new Message();
                    msg.arg1 = i++;
                    handler.sendMessage(msg);

                    try {
                        Thread.sleep(1000);
                        Log.d("stopwatch", "sleep");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        getActivity().runOnUiThread(new Runnable(){     //test
                            @Override
                            public void run() {
                                txtTime.setText("");
                                txtTime.setText("00:00");
                            }
                        });
                        return;
                    }
                }
            }
        }
    }


}
