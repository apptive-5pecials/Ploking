package com.fivespecial.ploking.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fivespecial.ploking.R;

public class SettingFragment extends Fragment {

    TextView log_out,wifi,saver,tag,feedback,licence,version;

    public static CameraFragment newInstance(){
        return new CameraFragment();
    }

    public SettingFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view;
        view = inflater.inflate(R.layout.fragment_setting, null);

        log_out = (TextView)view.findViewById(R.id.log_out);
        wifi = (TextView)view.findViewById(R.id.wifi);
        saver = (TextView)view.findViewById(R.id.saver);
        tag = (TextView)view.findViewById(R.id.tag);
        feedback = (TextView)view.findViewById(R.id.feedback);
        licence = (TextView)view.findViewById(R.id.licence);
        version = (TextView)view.findViewById(R.id.version);

        return view;
    }
}
