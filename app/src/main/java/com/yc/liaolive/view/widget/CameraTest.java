package com.yc.liaolive.view.widget;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by YanZhenjie on 2018/1/15.
 */
public class CameraTest {

    private Context mContext;

    public CameraTest(Context context) {
        this.mContext = context;
    }


    public boolean test()  {
        SurfaceView surfaceView = new SurfaceView(mContext);
        SurfaceHolder holder = surfaceView.getHolder();
        holder.addCallback(CALLBACK);

        Camera camera = null;
        try {
            camera = Camera.open();
            Camera.Parameters parameters = camera.getParameters();
            camera.setParameters(parameters);
            camera.setPreviewDisplay(holder);
            camera.setPreviewCallback(PREVIEW_CALLBACK);
            camera.startPreview();
            return true;
        } catch (Throwable e) {
            PackageManager packageManager = mContext.getPackageManager();
            return !packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA);
        } finally {
            if (camera != null) {
                camera.stopPreview();
                try {
                    camera.setPreviewDisplay(null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                camera.setPreviewCallback(null);
                camera.release();
            }
        }
    }

    private static final Camera.PreviewCallback PREVIEW_CALLBACK = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
        }
    };

    private static final SurfaceHolder.Callback CALLBACK = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        }
    };
}