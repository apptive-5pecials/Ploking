package com.fivespecial.ploking.Fragment;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.fivespecial.ploking.maps.BinLocation;
import com.fivespecial.ploking.maps.Calculation;
import com.fivespecial.ploking.maps.DataAdapter;
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

import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

public class GPSFragment extends Fragment {

    //TAG
    protected static final String TAG = "GPSFragment";


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

    //garbage bins
    Calculation calculation;
    public List<BinLocation> binLocationList;
    BinLocation binLocation;
    TextView txtNearBin;
    int binCount;


    //txtTimer
    TextView txtDistance;
    private TextView txtTime;
    TextView txtKcal;

    double currentLon=0;
    double currentLat=0;
    double lastLon = 0;
    double lastLat = 0;
    float distance_sum = 0;

    //timer
    private ImageButton startBtn, stopBtn, pauseBtn;
    private Boolean isRunning = false;
    private Boolean tButtonclicked = false;
    Thread timeThread;


    //kcal
    int weight = 70;
    private float coef = 0.001f; // 1초당 운동계수
    float calorie = 0;



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

        binCount = 0;

        //API를 호출해 클라이언트 ID를 지정
        NaverMapSdk.getInstance(getActivity()).setClient(
                new NaverMapSdk.NaverCloudPlatformClient("0fwsvimx0a"));

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view;
        view = inflater.inflate(R.layout.fragment_gps, null);



        calculation = new Calculation();

        //define views...
        startBtn = view.findViewById(R.id.btn_fragment_four_start);
        stopBtn = view.findViewById(R.id.btn_fragment_four_stop);
        pauseBtn = view.findViewById(R.id.btn_fragment_four_pause);
        View runLayout = view.findViewById(R.id.layout_running);
        View informLayout = view.findViewById(R.id.layout_inform);

        //animation
        Animation pauseAppear = AnimationUtils.loadAnimation(getActivity(), R.anim.pausebtn_appear);
        Animation stopAppear = AnimationUtils.loadAnimation(getActivity(), R.anim.stopbtn_appear);

        txtNearBin = view.findViewById(R.id.tv_near_bin_notice);
        txtDistance = (TextView) view.findViewById(R.id.tv_fragment_four_distance);
        txtTime = (TextView) view.findViewById(R.id.tv_fragment_four_time);
        txtKcal = (TextView) view.findViewById(R.id.tv_fragment_four_calorie);

        //naverMapfragment start
        FragmentManager fm = getFragmentManager();
        mapFragment = (MapFragment)this
                .getChildFragmentManager()
                .findFragmentById(R.id.map);
        if(mapFragment == null){
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.fragmentBorc, mapFragment).commit();
        }

        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
        //test
        mapFragment.getMapAsync(this::onMapReady);

        startBtn.setOnClickListener((View v) -> {
            v.setVisibility(View.GONE);
            runLayout.setVisibility(View.VISIBLE);
            informLayout.setVisibility(View.VISIBLE);

            pauseBtn.startAnimation(pauseAppear);
            stopBtn.startAnimation(stopAppear);

            isRunning = true;
            timeThread = new Thread(new timeThread());
            timeThread.start();
            tButtonclicked = true;
        });

        stopBtn.setOnClickListener((View v) ->{
            startBtn.setVisibility(View.VISIBLE);
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
//                txtTime.setText("00:00");
            distance_sum = 0;
//                txtDistance.setText("0.0 m");
        });

        pauseBtn.setOnClickListener((View v) -> {
            isRunning = !isRunning;
            if(isRunning){
                pauseBtn.setImageResource(R.drawable.pause_button);
            }else{
                tButtonclicked = true;
                pauseBtn.setImageResource(R.drawable.start_button);
            }
        });

        //GPS start...
        try {

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

        } catch (NullPointerException e) {
            e.printStackTrace();
        }


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
        double dLat, dLong;
        final InfoWindow binsInfo = new InfoWindow();



        final LocationManager lm = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        Location location;

        //uisetting
        UiSettings uiSettings = naverMap.getUiSettings();

        naverMap.setLocationSource(locationSource);

        LocationOverlay locationOverlay = naverMap.getLocationOverlay();
        locationOverlay.setVisible(true);

        //marker
        initLoadDB();

        naverMap.setOnMapClickListener((coord, point) -> {
            binsInfo.close();
        });

        for(int i = 0; i < binLocationList.size(); i++){
            binLocation = binLocationList.get(i);


            if(binLocation.getLatitude() != null && binLocation.getLongitude() != null) {
                dLat = binLocation.getLatitude();
                dLong = binLocation.getLongitude();
            } else {
                dLat = 0.0;
                dLong = 0.0;
            }

//            final InfoWindow binsInfo = new InfoWindow();
//            binsInfo.setPosition(new LatLng(dLat, dLong));
            binsInfo.setAdapter(new InfoWindowAdapter(getActivity()));
            binsInfo.setOnClickListener(overlay -> {
                binsInfo.close();
                return true;
            });


            Marker bins = new Marker();
            bins.setPosition(new LatLng(dLat, dLong));
            bins.setIcon(OverlayImage.fromResource(R.drawable.bin_marker));
            bins.setOnClickListener(overlay -> {
                binsInfo.open(bins);
                return true;
            });
            bins.setTag(binLocation.getDescription());
            bins.setMap(naverMap);
        }



        try {
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            //position 속성을 지정하면 위치 오버레이의 좌표를 변경할 수 있다.
            //처음 생성된 위치 오버레이는 카메라의 초기 좌표에 위치해 있다.
        }
        catch(SecurityException | NullPointerException e){
            Log.v(TAG, "CANNOT be find GPS signal.");
            longitude = 129.04378;
            latitude = 35.237396;
        }
        binCount = calculation.nearBins(binLocationList, latitude, longitude);
        txtNearBin.setText(getString(R.string.near_bin_notice, binCount));
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

            int binCount = 0;

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

                binCount = calculation.nearBins(binLocationList, currentLat, currentLon);

                txtNearBin.setText(getString(R.string.near_bin_notice, binCount));


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
            catch(SecurityException | NullPointerException e){
                e.printStackTrace();
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
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int sec_sum = msg.arg1;
            int sec = msg.arg1 % 60;
            int min = msg.arg1 / 60;
            //1000이 1초 1000*60 은 1분 1000*60*10은 10분 1000*60*60은 한시간

            //소모 칼로리
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

    private void initLoadDB(){

        DataAdapter mDBHelper = new DataAdapter(getActivity());
        mDBHelper.createDatabase();
        mDBHelper.open();

        //db에 있는 값들을 model에 적용해서 넣는다.
        binLocationList = mDBHelper.getTableData();

        //db 닫기;
        mDBHelper.close();
    }
}
