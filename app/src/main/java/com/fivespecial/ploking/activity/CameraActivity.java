package com.fivespecial.ploking.fragment;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.fivespecial.ploking.R;
import com.fivespecial.ploking.adapterEtc.AlbumDbHelper;
import com.fivespecial.ploking.adapterEtc.CameraUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


public class CameraActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener, View.OnClickListener {
    private TextureView textureView;
    private ImageView imageView;
    private ImageView preimg;

    private Camera camera;
    private List<Camera.Size> supportedPreviewSizes;
    private android.hardware.Camera.Size previewSize;
    private AlbumDbHelper dbHelper;
    private String path;
    private String FILE_NAME;

    private SoundPool pool;
    private int ddok;

    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.frament_camera);

        pool = new SoundPool(1, AudioManager.STREAM_MUSIC,0);
        ddok = pool.load(getApplicationContext(), R.raw.sound, 1);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 200);

        } else {

            textureView = findViewById(R.id.textureview);
            imageView = findViewById(R.id.shutter_btn);
            preimg = findViewById(R.id.preimage);

            dbHelper= new AlbumDbHelper(this);

            textureView.setSurfaceTextureListener(this);
            imageView.setOnClickListener(this);

        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        try{
            camera = android.hardware.Camera.open();

            android.hardware.Camera.Parameters parameters = camera.getParameters();
            supportedPreviewSizes= ((Camera.Parameters) parameters).getSupportedPreviewSizes();
            if(supportedPreviewSizes != null){
                previewSize= CameraUtil.getOptimalPreviewSize(supportedPreviewSizes, width, height);
                ((Camera.Parameters) parameters).setPreviewSize(previewSize.width, previewSize.height);
            }
            int result=CameraUtil.setCameraDisplayOrientation(this , 0);
            ((Camera.Parameters) parameters).setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            ((Camera.Parameters) parameters).setRotation(result);

            camera.setParameters(parameters);
            camera.setDisplayOrientation(result);
            camera.setPreviewTexture(surface);
            camera.startPreview();
        } catch(Exception e) {
            //FragmentTransaction transaction = getFragmentManager().beginTransaction();
            //transaction.detach(this).attach(this).commit();
        }

    }
    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void onClick(View v) {
        if(camera != null){
            pool.play(ddok,1,0.5f,0,0,1);
            camera.takePicture(null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {

                    FileOutputStream fos = null;
                    Bitmap bmp = BitmapFactory.decodeByteArray(data,0,data.length);

                    ContextWrapper cw= new ContextWrapper(getApplicationContext());
                    File dir= cw.getDir("imageDir", MODE_PRIVATE);
                    if(!dir.exists()) {
                        dir.mkdir();
                    }
                    FILE_NAME = System.currentTimeMillis()+".jpg";
                    File mypath = new File(dir,FILE_NAME);
                    if(!mypath.exists()){
                        mypath.canRead();
                    }

                    try {
                        fos = new FileOutputStream(mypath);
                        Bitmap rotatedBitmap1 = rotateImage(bmp, 90); //화면이 90도 돌아가는 것을 막아준다.
                        rotatedBitmap1.compress(Bitmap.CompressFormat.JPEG, 100, fos); //파일을 jpg로 내부 저장소에 저장한다.


                    }catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                    path = dir.getAbsolutePath();

                    SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);

                    double longitude = Double.longBitsToDouble(pref.getLong("lon", Double.doubleToLongBits(0)));
                    double latitude = Double.longBitsToDouble(pref.getLong("lat", Double.doubleToLongBits(0)));

                    try{
                        File f= new File(path,FILE_NAME);
                        Bitmap b= BitmapFactory.decodeStream(new FileInputStream(f));
                        preimg.setImageBitmap(b);
                        preimg.setScaleType(ImageView.ScaleType.FIT_XY);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    dbHelper.insertData(path, FILE_NAME, longitude, latitude);

                    //((TabbedActivity)getActivity()).refreshAlbumFragment();

                    camera.startPreview();

                }
            });
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;

        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

}

