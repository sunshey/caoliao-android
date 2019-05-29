package com.yc.liaolive.media.view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Camera;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.yc.liaolive.util.Logger;

import java.io.IOException;

/**
 * TinyHung@Outlook.com
 * 2018/5/26
 * 首页抢聊-圆形相机预览
 */

public class CircleCameraView extends SurfaceView implements SurfaceHolder.Callback {

	private static final String TAG = "CircleCameraView";
	private Paint paint;
	private Camera camera;
	private int height; // 圆的半径
	private SurfaceHolder surfaceHolder;

	public CircleCameraView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	public CircleCameraView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public CircleCameraView(Context context) {
		super(context);
		initView();
	}
	
	private void initView() {
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
		getHolder().addCallback(this);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		height=widthSize;
		Log.e("onMeasure", "draw: widthMeasureSpec = " +widthSize + "  heightMeasureSpec = " + heightSize);
		setMeasuredDimension(widthSize, heightSize);
	}

	@Override
	public void draw(Canvas canvas) {
		Log.e("onDraw", "draw: test");
//		Path path = new Path();
//		path.addCircle(height / 2, height / 2, height / 2, Path.Direction.CCW);
//		canvas.clipPath(path, Region.Op.REPLACE);
		super.draw(canvas);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Log.e("onDraw", "onDraw");
		super.onDraw(canvas);
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.e("onDraw", "surfaceCreated");
		this.surfaceHolder=holder;
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.e("onDraw", "surfaceChanged");
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.e("onDraw", "surfaceDestroyed");
		if (camera != null) {
			camera.setPreviewCallback(null);
			camera.stopPreview();
			camera.release();
			camera=null;
		}
	}


	/**
	 * 开启预览
	 */
	public void openCamera() {
		if(null!=surfaceHolder){
			startC(surfaceHolder);
		}
	}

	/**
	 * 关闭预览
	 */
	public void closeCamera() {
		if(null!=camera){
			camera.stopPreview();
			camera.release();
			camera=null;
		}
	}

	/**
	 * 开始预览
	 * @param holder
	 */
	private void startC(SurfaceHolder holder) {
		try {
			//获取camera对象
			if(null==camera) camera = Camera.open(1);//CAMERA_FACE_DETECTION_SW
			//设置预览监听
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				camera.autoFocus(null);//自动对焦
			}
			camera.setPreviewDisplay(holder);
			Camera.Parameters parameters = camera.getParameters();
			if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
				parameters.set("orientation", "portrait");
				camera.setDisplayOrientation(90);
				parameters.setRotation(90);
			} else {
				parameters.set("orientation", "landscape");
				camera.setDisplayOrientation(0);
				parameters.setRotation(0);
			}
			camera.setParameters(parameters);
			//启动摄像头预览
			camera.startPreview();
		} catch (IOException e) {
			e.printStackTrace();
			closeCamera();
		} catch (RuntimeException e){
			closeCamera();
		}
	}
}
