package com.fivespecial.ploking.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fivespecial.ploking.Activity.TabbedActivity;
import com.fivespecial.ploking.Activity.TrashActivity;
import com.fivespecial.ploking.R;

public class HomeFragment extends Fragment {

    public HomeFragment() {

    }

    public static HomeFragment newInstance(){
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_home, null);

        TextView tvDistance = view.findViewById(R.id.home_steps);
        TextView tvKcal = view.findViewById(R.id.home_kcals);

        SharedPreferences sFile = getActivity().getSharedPreferences("sFile", Context.MODE_PRIVATE);

        Float ftDistance = sFile.getFloat("distance", 0);
        Float ftKcal = sFile.getFloat("Kcal", 0);

        ftDistance /= 1000; // meter to kilometer
        Log.d("homeFragment", ftDistance.toString());

        tvDistance.setText("총 활동량 \n" + String.format("%.1f", ftDistance) + " km");
        tvKcal.setText("백선욱 님은 총\n"+ String.format("%.0f", ftKcal) + "kcal를 소비했습니다.");

        view.findViewById(R.id.button_trash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckTypesTask task = new CheckTypesTask();
                task.execute();
                Intent intent = new Intent(getActivity(), TrashActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    public class CheckTypesTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog asyncDialog = new ProgressDialog(
                getActivity());

        @Override
        protected void onPreExecute() {
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("로딩중입니다..");

            // show dialog
            asyncDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                for (int i = 0; i < 5; i++) {
                    //asyncDialog.setProgress(i * 30);
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            asyncDialog.dismiss();
            super.onPostExecute(result);
        }
    }
}
