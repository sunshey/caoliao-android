package com.faceunity.beauty.ui;

import com.faceunity.FURenderer;
import com.faceunity.R;
import com.faceunity.beauty.view.BeautyControlView;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 美颜设置
 * Created by yangxueqin on 2018/12/7.
 */

public class BeautySettingActivity extends FUBaseUIActivity
        implements FURenderer.OnFUDebugListener,
        FURenderer.OnTrackingStatusChangedListener {
    public final static String TAG = BeautySettingActivity.class.getSimpleName();

//    private byte[] mFuNV21Byte;

    private BeautyControlView mBeautyControlView;
    private FURenderer mFURenderer;

    @Override
    protected void onCreate() {
        //初始化FU相关 authpack 为证书文件
        mFURenderer = new FURenderer
                .Builder(this)
                .maxFaces(4)
                .inputTextureType(FURenderer.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE)
                .createEGLContext(false)
                .needReadBackImage(false)
                .defaultEffect(null)
                .setOnFUDebugListener(this)
                .setOnTrackingStatusChangedListener(this)
                .build();

        mBottomViewStub.setLayoutResource(R.layout.layout_fu_beauty);
        mBottomViewStub.inflate();

        mBeautyControlView = (BeautyControlView) findViewById(R.id.fu_beauty_control);
        mBeautyControlView.setFURenderer(mFURenderer);
//        mGLSurfaceView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mBeautyControlView.hideBottomLayoutAnimator();
//            }
//        });
        mBeautyControlView.showBottomLayoutAnimator();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBeautyControlView != null)
            mBeautyControlView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onSensorChanged(int rotation) {
        mFURenderer.setTrackOrientation(rotation);
    }

    @Override
    public void onCameraChange(int currentCameraType, int cameraOrientation) {
        mFURenderer.onCameraChange(currentCameraType, cameraOrientation);
    }

    @Override
    public void onFpsChange(final double fps, final double renderTime) {

    }

    @Override
    public void onTrackingStatusChanged(final int status) {

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mFURenderer.onSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
    }

    @Override
    public int onDrawFrame(byte[] cameraNV21Byte, int cameraTextureId, int cameraWidth, int cameraHeight, float[] mtx, long timeStamp) {
        int fuTextureId;
//        if (isDoubleInputType) { //双输入
            fuTextureId = mFURenderer.onDrawFrame(cameraNV21Byte, cameraTextureId, cameraWidth, cameraHeight);
//        } else { //单输入
//            if (mFuNV21Byte == null) {
//                mFuNV21Byte = new byte[cameraNV21Byte.length];
//            }
//            System.arraycopy(cameraNV21Byte, 0, mFuNV21Byte, 0, cameraNV21Byte.length);
//            fuTextureId = mFURenderer.onDrawFrame(mFuNV21Byte, cameraWidth, cameraHeight);
//        }
//        sendRecordingData(fuTextureId, mtx, timeStamp / Constant.NANO_IN_ONE_MILLI_SECOND);
//        checkPic(fuTextureId, mtx, cameraHeight, cameraWidth);
        return fuTextureId;
    }

    @Override
    public void onSurfaceDestroy() {
        //通知FU销毁
        if(null!=mFURenderer) mFURenderer.onSurfaceDestroyed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null!=mBeautyControlView) {
            mBeautyControlView.onDestroy();
            mBeautyControlView=null;
        }
        if(null!=mFURenderer){
            mFURenderer.destroyItems();
            mFURenderer=null;
        }
    }
}
