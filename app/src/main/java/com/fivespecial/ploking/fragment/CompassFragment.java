package com.fivespecial.ploking.fragment;

import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.fivespecial.ploking.R;
import com.fivespecial.ploking.adapterEtc.Compass;
import com.fivespecial.ploking.base.BaseFragment;

import org.jetbrains.annotations.NotNull;

public class CompassFragment extends BaseFragment {

    private static final String TAG = "CompassActivity";

    private Compass compass;
    private ImageView arrowView;

    private float currentAzimuth;

    @Override
    public int getResourceId() {
        return R.layout.fragment_compass;
    }

    @Override
    public void initComponent(@NotNull View view) {

        arrowView = view.findViewById(R.id.hands);
    }

    @Override
    public void setupListeners(@NotNull View view) {

    }

    @Override
    public void setupImplementation() {

        setupCompass();

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "start fragment_compass");
        compass.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        compass.stop();
    }

    @Override
    public void onResume() {
        super.onResume();
        compass.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        compass.stop();
    }

    private void setupCompass() {
        if(getActivity() != null) {
            compass = new Compass(getActivity());

            Compass.CompassListener cl = getCompassListener();
            compass.setListener(cl);
        }
    }

    private Compass.CompassListener getCompassListener() {
        return azimuth -> {
            if(getActivity() != null) {
                getActivity().runOnUiThread(() ->
                    adjustCompassArrow(azimuth)
                );
            }
        };
    }

    private void adjustCompassArrow(float azimuth) {
        Animation animation = new RotateAnimation(-currentAzimuth, -azimuth,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        currentAzimuth = azimuth;

        animation.setDuration(500);
        animation.setRepeatCount(0);
        animation.setFillAfter(true);

        arrowView.startAnimation(animation);
    }
}
