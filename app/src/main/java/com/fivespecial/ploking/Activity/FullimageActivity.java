package com.fivespecial.ploking.Activity;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.fivespecial.ploking.AdapterEtc.DbHelper;
import com.fivespecial.ploking.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class FullimageActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView FullImage;
    Button delete_btn, share_btn;
    File f;
    int req;
    String path = "";
    String FILE_NAME = "";
    int id = 0;
    int pos=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_fullimage);

        FullImage = (ImageView) findViewById(R.id.full_image);
        delete_btn = (Button) findViewById(R.id.delete);
        share_btn = (Button) findViewById(R.id.share);

        Bundle extras = getIntent().getExtras();
        path = extras.getString("path");
        FILE_NAME = extras.getString("name");
        id = extras.getInt("id");
        pos=extras.getInt("position");

        f = new File(path, FILE_NAME);
        Bitmap b = null;
        try {
            b = BitmapFactory.decodeStream(new FileInputStream(f));
            FullImage.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            DbHelper database = new DbHelper(FullimageActivity.this);
            database.deleteData(FILE_NAME);
            Toast.makeText(getApplicationContext(), "파일이 정상적으로 삭제 되었습니다 ", Toast.LENGTH_SHORT).show();
            finish();

            e.printStackTrace();
        }

        share_btn.setOnClickListener(this);
        delete_btn.setOnClickListener(this);


    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.share:
                sendShare(path);

                break;
            case R.id.delete:
                DbHelper database = new DbHelper(FullimageActivity.this);
                database.deleteData(FILE_NAME);

                try {
                    File file = new File(path);
                    File[] flist = file.listFiles();
                    for (int i = 0; i < flist.length; i++) {
                        String fname = flist[i].getName();
                        if (fname.equals(FILE_NAME)) {
                            flist[i].delete();
                        }
                    }
                    Toast.makeText(getApplicationContext(), "파일이 정상적으로 삭제 되었습니다 ", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();


                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "파일 삭제 실패 ", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void sendShare(String path) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        File file = new File(path + "/" + FILE_NAME);
        Uri contentUri = FileProvider.getUriForFile(FullimageActivity.this,
                getApplicationContext().getPackageName() + ".fileprovider", file);
        Uri uri = Uri.fromFile(file);
        intent.setType("image/jpg");
        intent.putExtra(Intent.EXTRA_STREAM, contentUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Choose"));
    }

}