package com.faceunity.beauty.ui;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewStub;
import android.view.WindowManager;
import com.faceunity.R;
import com.faceunity.beauty.util.CameraRenderer;
import com.faceunity.beauty.util.NotchInScreenUtil;

/**
 * Base Activity, 主要封装FUBeautyActivity与FUEffectActivity的公用界面与方法
 * CameraRenderer相关回调实现
 * Created by tujh on 2018/1/31.
 */
public abstract class FUBaseUIActivity extends AppCompatActivity implements View.OnClickListener,
        CameraRenderer.OnRendererStatusListener, SensorEventListener {
    public final static String TAG = FUBaseUIActivity.class.getSimpleName();

//    protected ImageView mTopBackground;
    protected GLSurfaceView mGLSurfaceView;
    protected CameraRenderer mCameraRenderer;
    protected ViewStub mBottomViewStub;

    private SensorManager mSensorManager;
    private Sensor mSensor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (NotchInScreenUtil.hasNotch(this)) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_fu_base);

        mGLSurfaceView = (GLSurfaceView) findViewById(R.id.fu_base_gl_surface);
        mGLSurfaceView.setEGLContextClientVersion(2);
        mCameraRenderer = new CameraRenderer(this, mGLSurfaceView, this);
        mGLSurfaceView.setRenderer(mCameraRenderer);
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mBottomViewStub = (ViewStub) findViewById(R.id.fu_base_bottom);
        mBottomViewStub.setInflatedId(R.id.fu_base_bottom);
        onCreate();
    }

    protected abstract void onCreate();

    @Override
    protected void onResume() {
        super.onResume();
        mCameraRenderer.onCreate();
        mCameraRenderer.onResume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        mCameraRenderer.onPause();
        mCameraRenderer.onDestroy();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.fu_base_back) {
            onBackPressed();

        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            if (Math.abs(x) > 3 || Math.abs(y) > 3) {
                if (Math.abs(x) > Math.abs(y)) {
                    onSensorChanged(x > 0 ? 0 : 180);
                } else {
                    onSensorChanged(y > 0 ? 90 : 270);
                }
            }
        }
    }

    protected abstract void onSensorChanged(int rotation);

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

}
