package com.fivespecial.ploking.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.fivespecial.ploking.Activity.TabbedActivity;
import com.fivespecial.ploking.AdapterEtc.CameraUtil;
import com.fivespecial.ploking.AdapterEtc.DbHelper;
import com.fivespecial.ploking.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


public class CameraFragment extends Fragment implements TextureView.SurfaceTextureListener, View.OnClickListener {
    TextureView textureView;
    ImageView imageView,preimg;

    Camera camera;
    List<Camera.Size> supportedPreviewSizes;
    android.hardware.Camera.Size previewSize;
    DbHelper dbHelper;
    AlbumFragment albumFragment;
    String path;
    String FILE_NAME;

    public static CameraFragment newInstance(){
        return new CameraFragment();
    }

    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frament_camera, null, false);
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 200);

        }else {

            textureView = (TextureView)view.findViewById(R.id.textureview);
            imageView=(ImageView)view.findViewById(R.id.shutter_btn);
            preimg=(ImageView)view.findViewById(R.id.preimage);

            dbHelper= new DbHelper(getActivity());


            textureView.setSurfaceTextureListener(this);
            imageView.setOnClickListener(this);

        }
        return view;
    }
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        try{
            camera= android.hardware.Camera.open();

            android.hardware.Camera.Parameters parameters=camera.getParameters();
            supportedPreviewSizes= ((Camera.Parameters) parameters).getSupportedPreviewSizes();
            if(supportedPreviewSizes != null){
                previewSize= CameraUtil.getOptimalPreviewSize(supportedPreviewSizes, width, height);
                ((Camera.Parameters) parameters).setPreviewSize(previewSize.width, previewSize.height);
            }
            int result=CameraUtil.setCameraDisplayOrientation(getActivity(), 0);
            ((Camera.Parameters) parameters).setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            ((Camera.Parameters) parameters).setRotation(result);

            camera.setParameters(parameters);
            camera.setDisplayOrientation(result);
            camera.setPreviewTexture(surface);
            camera.startPreview();
        } catch(Exception e) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.detach(this).attach(this).commit();
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
            camera.takePicture(null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    int i;
                    FileOutputStream fos = null;
                    Bitmap bmp = BitmapFactory.decodeByteArray(data,0,data.length);

                    ContextWrapper cw= new ContextWrapper(getContext().getApplicationContext());
                    File dir= cw.getDir("imageDir", Context.MODE_PRIVATE);
                    if(!dir.exists()) {
                        dir.mkdir();
                    }
                    FILE_NAME = System.currentTimeMillis()+".jpg";
                    File mypath=new File(dir,FILE_NAME);
                    if(!mypath.exists()){
                        mypath.canRead();
                    }

                    try {
                        fos = new FileOutputStream(mypath);
                        //화면이 90도 돌아가는 것을 막아준다.
                        Bitmap rotatedBitmap1 = null;
                        rotatedBitmap1= rotateImage(bmp, 90);
                        //파일을 jpg로 내부 저장소에 저장한다.
                        rotatedBitmap1.compress(Bitmap.CompressFormat.JPEG, 100, fos);


                    }catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                    path =dir.getAbsolutePath();

                    try{
                        File f= new File(path,FILE_NAME);
                        Bitmap b= BitmapFactory.decodeStream(new FileInputStream(f));
                        preimg.setImageBitmap(b);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    dbHelper.insertData(path,FILE_NAME);
                    ((TabbedActivity)getActivity()).refresh();
                    camera.startPreview();

                }
            });
        }
    }
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
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
}

