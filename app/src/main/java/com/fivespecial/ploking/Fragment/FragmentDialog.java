package com.fivespecial.ploking.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.fivespecial.ploking.AdapterEtc.DbHelper;
import com.fivespecial.ploking.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class FragmentDialog extends DialogFragment implements View.OnClickListener {

    File f;
    ImageView dialog_img;
    ImageView delete_btn,share_btn;
    String path = "";
    String file_name = "";
    Double longitude, latitude;
    private Fragment fragment;


    private DialogInterface.OnDismissListener onDismissListener;

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener){
        this.onDismissListener= onDismissListener;
    }
    @Override
    public void onDismiss(DialogInterface dialog){
        super.onDismiss(dialog);
        if(onDismissListener != null){
            onDismissListener.onDismiss(dialog);
        }
    }





    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialogfragment,container,false);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Bundle args=getArguments();
        String value= args.getString("key");
        path=args.getString("path");
        file_name=args.getString("name");
        longitude=args.getDouble("longitude");
        latitude=args.getDouble("latitude");
        dialog_img = (ImageView) view.findViewById(R.id.dailog_img);
        delete_btn=(ImageView) view.findViewById(R.id.dailog_delete);
        share_btn=(ImageView)view.findViewById(R.id.dialog_share);

        String cityName = null;
        Geocoder gcd = new Geocoder(getActivity().getBaseContext(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(latitude,
                    longitude, 1);
            Log.d("w", String.valueOf(latitude) + String.valueOf(longitude));
            if (addresses.size() == 0)
                throw new IOException();
            cityName = addresses.get(0).getLocality();
        } catch (IOException e) {
            cityName = "알 수 없는 도시";
            e.printStackTrace();
        }

        TextView txt = (TextView)view.findViewById(R.id.dialog_time);
        Log.d("w", file_name.split(".", 1)[0]);
        long timel = Long.parseLong(file_name.split(".jpg")[0]);
        String pattern = "yyyy. MM. dd HH:mm";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        String date = (String) formatter.format(new Timestamp(timel));
        txt.setText(date + '\n' + cityName);

        f = new File(path, file_name);
        Bitmap b = null;
        try {
            b = BitmapFactory.decodeStream(new FileInputStream(f));
            dialog_img.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        fragment = getActivity().getSupportFragmentManager().findFragmentByTag("tag");


        delete_btn.setOnClickListener(this);
        share_btn.setOnClickListener(this);

        return view;
    }
    public void onResume() {

        super.onResume();

        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((WindowManager.LayoutParams) params);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_share:
                sendShare(path);
                finish();
                break;
            case R.id.dailog_delete:
                DbHelper database = new DbHelper(getContext());
                database.deleteData(file_name);

                try {
                    File file = new File(path);
                    File[] flist = file.listFiles();
                    for (int i = 0; i < flist.length; i++) {
                        String fname = flist[i].getName();
                        if (fname.equals(file_name)) {
                            flist[i].delete();
                        }
                    }
                    Toast.makeText(getActivity(), "파일이 정상적으로 삭제 되었습니다 ", Toast.LENGTH_SHORT).show();
                    //  setResult(RESULT_OK);
                    finish();


                } catch (Exception e) {
                    Toast.makeText(getActivity(), "파일 삭제 실패 ", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    private void sendShare(String path) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        File file = new File(path + "/" + file_name);
        Uri contentUri = FileProvider.getUriForFile(getContext(),
                getActivity().getPackageName() + ".fileprovider", file);
        Uri uri = Uri.fromFile(file);
        intent.setType("image/jpg");
        intent.putExtra(Intent.EXTRA_STREAM, contentUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Choose"));

    }

    public void finish(){
        if (fragment != null) {
            DialogFragment dialogFragment = (DialogFragment) fragment;
            dialogFragment.dismiss();
        }
    }

}
