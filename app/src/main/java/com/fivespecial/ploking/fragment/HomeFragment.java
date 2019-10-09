package com.fivespecial.ploking.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fivespecial.ploking.R;
import com.fivespecial.ploking.activity.TrashActivity;
import com.fivespecial.ploking.async.CheckTypesTask;
import com.fivespecial.ploking.base.BaseFragment;

import org.jetbrains.annotations.NotNull;

public class HomeFragment extends BaseFragment {

    public static HomeFragment newInstance(){
        return new HomeFragment();
    }

    private TextView tvDistance;
    private TextView tvKcal;
    private ImageView trashButton;

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

        if (getActivity().getApplicationContext() != null) {
            SharedPreferences sFile = getActivity().getApplicationContext().getSharedPreferences("sFile", Context.MODE_PRIVATE);

            float ftDistance = sFile.getFloat("distance", 0);
            float ftKcal = sFile.getFloat("Kcal", 0);

            ftDistance = ftDistance / 1000; // 미터->킬로미터

            tvDistance.setText("총 활동량 \n" + String.format("%.1f", ftDistance) + " km");
            tvKcal.setText("백선욱 님은 총\n"+ String.format("%.0f", ftKcal) + "kcal를 소비했습니다.");
        }
    }
}
