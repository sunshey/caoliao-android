package com.yc.liaolive.videocall.manager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import com.yc.liaolive.R;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.index.ui.MainActivity;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.user.ui.SettingActivity;
import com.yc.liaolive.util.DeviceUtils;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ScreenUtils;
import com.yc.liaolive.util.SystemUtils;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.videocall.bean.CallExtraInfo;
import com.yc.liaolive.videocall.ui.activity.LiveCallActivity;
import com.yc.liaolive.videocall.view.LiveCallInLayout;
import com.yc.liaolive.videocall.view.LiveCallInSmallLayout;

/**
 * TinyHung@Outlook.com
 * 2018/12/10
 * 视频来电 悬浮窗交互辅助
 */

public class CallInWindowManager {

	private static final String TAG = "CallWindowManager";
	private static CallInWindowManager mInstance;
	private LiveCallInLayout cLiveCallInLayout;//全屏幕版来电
	private static LiveCallInSmallLayout mLiveCallInSmallLayout;//迷你版来电
	private static WindowManager mWindowManager;
	private Context mContext;

	public static synchronized CallInWindowManager getInstance(){
		synchronized (CallInWindowManager.class){
			if(null==mInstance){
				mInstance=new CallInWindowManager();
			}
		}
		return mInstance;
	}

	/**
	 * 初始化
	 * @param context
	 */
	public CallInWindowManager init(Context context){
		this.mContext=context;
		return mInstance;
	}

	public Context getContext() {
		return mContext;
	}

	/**
	 * 全屏来电提示框初始化
	 * @param callExtraInfo
	 */
	public CallInWindowManager createFullCallInLayoutToWindow(CallExtraInfo callExtraInfo) {
		if(null==getContext()) throw new IllegalArgumentException("Please make call to init method");
		removeCallInFullLayout();
		if(!DeviceUtils.hasAuthorFloatWin()){
			startWindownPermissis(callExtraInfo);
			return null;
		}
		try {
			WindowManager windowManager = getWindowManager(getContext());
			cLiveCallInLayout = new LiveCallInLayout(getContext());
			LayoutParams layoutParams = new LayoutParams();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
			} else {
				layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
			}
			layoutParams.format = PixelFormat.RGBA_8888;
			layoutParams.gravity = Gravity.CENTER;
			layoutParams.width = ScreenUtils.getScreenWidth();
			layoutParams.height = ScreenUtils.getScreenHeight();
			cLiveCallInLayout.setLayoutParams(layoutParams);
			cLiveCallInLayout.setOnFunctionListener(new LiveCallInLayout.OnFunctionListener() {
				@Override
				public void onAcceptCall() {
					if(null!=mOnFunctionListener) mOnFunctionListener.onAcceptCall();
				}

				@Override
				public void onRejectCall() {
					if(null!=mOnFunctionListener) mOnFunctionListener.onRejectCall();
				}
			});
			windowManager.addView(cLiveCallInLayout, layoutParams);
		}catch (WindowManager.BadTokenException e){
			startWindownPermissis(callExtraInfo);
			return null;
		}
		return mInstance;
	}

	/**
	 * 开始悬浮窗权限申请
	 * @param callExtraInfo
	 */
	private void startWindownPermissis(CallExtraInfo callExtraInfo) {
		if(null==callExtraInfo){
			callExtraInfo.setEnterIdentify(1);//标识为接听来电
			try {
				//Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getApplicationContext().getPackageName()));
				Intent intent=new Intent(getContext(), SettingActivity.class);
				intent.putExtra("is_open_windown",1);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				getContext().startActivity(intent);
			}catch (RuntimeException e){

			}
		}else{
			if(SystemUtils.isAppRunning(getContext(), getContext().getPackageName())){
				Intent mainIntent = new Intent(getContext(), MainActivity.class);
				mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
				//启动CallEmptyActivity传入参数
				Intent intent = new Intent(getContext(), LiveCallActivity.class);
				intent.putExtra(Constant.APP_START_EXTRA_CALL,callExtraInfo);
				final Intent[] intents = {mainIntent, intent};
				getContext().startActivities(intents);
			}else{
				Intent launchIntent = getContext().getPackageManager().getLaunchIntentForPackage(getContext().getPackageName());
				launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				launchIntent.putExtra(Constant.APP_START_EXTRA_DOALOG_CHAT, callExtraInfo);
				getContext().startActivity(launchIntent);
			}
		}
	}

	/**
	 * 开始响铃、震动
	 * @param callExtraInfo
	 * @return
	 */
	public CallInWindowManager onStart(CallExtraInfo callExtraInfo) {
		if(null==cLiveCallInLayout||null==callExtraInfo) return mInstance;
		cLiveCallInLayout.setBackgroundResource(R.drawable.shape_mackcall_bg);
        boolean equals = TextUtils.equals(UserManager.getInstance().getUserId(), callExtraInfo.getCallUserID());
        cLiveCallInLayout.onCreate(callExtraInfo,equals?1:0);
		cLiveCallInLayout.showCallTipsView(equals);
		return mInstance;
	}

	/**
	 * 移除全屏来电提示框
	 */
	public CallInWindowManager removeCallInFullLayout() {
		if(null==getContext()) return mInstance;
		if (null!=cLiveCallInLayout) {
			WindowManager windowManager = getWindowManager(getContext());
			windowManager.removeView(cLiveCallInLayout);
			cLiveCallInLayout = null;
		}
		return mInstance;
	}

	/**
	 * 创建一个迷你版的来电提示窗口
	 */
	public CallInWindowManager createSmallWindow() {
		if(null==getContext()) throw new IllegalArgumentException("Please make call to init method");
		removeSmallWindow();
		if(!DeviceUtils.hasAuthorFloatWin()){
			startWindownPermissis(null);
			return null;
		}
		try {
			WindowManager windowManager = getWindowManager(getContext());
			PackageManager pm = getContext().getPackageManager();
			mLiveCallInSmallLayout = new LiveCallInSmallLayout(getContext());
			LayoutParams smallWindowParams = new LayoutParams();
			boolean permission = (PackageManager.PERMISSION_GRANTED == pm.checkPermission("android.permission.SYSTEM_ALERT_WINDOW", Utils.getAppProcessName(getContext())));
			if(permission){
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
					smallWindowParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
				} else {
					smallWindowParams.type = WindowManager.LayoutParams.TYPE_PHONE;
				}
			}else{
				smallWindowParams.type = LayoutParams.TYPE_TOAST;
			}
			smallWindowParams.format = PixelFormat.RGBA_8888;
			smallWindowParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
					| LayoutParams.FLAG_NOT_FOCUSABLE;
			smallWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
			smallWindowParams.width = ScreenUtils.getScreenWidth();
			smallWindowParams.height = LayoutParams.WRAP_CONTENT;
			smallWindowParams.x = 0;
			smallWindowParams.y = 0;
			mLiveCallInSmallLayout.setLayoutParams(smallWindowParams);
			windowManager.addView(mLiveCallInSmallLayout, smallWindowParams);
		}catch (WindowManager.BadTokenException e){
			startWindownPermissis(null);
			return null;
		}
		return mInstance;
	}

	/**
	 * 移除迷你版的来电提示窗口
	 */
	public CallInWindowManager removeSmallWindow() {
		if(null==getContext()) return mInstance;
		if (null!=mLiveCallInSmallLayout) {
			WindowManager windowManager = getWindowManager(getContext());
			windowManager.removeView(mLiveCallInSmallLayout);
			mLiveCallInSmallLayout = null;
		}
		return mInstance;
	}

	/**
	 * 创建一个窗口管理者
	 * @param context
	 * @return
	 */
	private  WindowManager getWindowManager(Context context) {
		if (mWindowManager == null) {
			mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		}
		return mWindowManager;
	}

	/**
	 * 对应函数调用
	 */
	public void onDestroy(){
		if(null!=cLiveCallInLayout) cLiveCallInLayout.onDestroy();
		if(DeviceUtils.hasAuthorFloatWin()){
			removeSmallWindow();
			removeCallInFullLayout();
		}
		mOnFunctionListener=null;mContext=null;mWindowManager=null;
	}

	public interface OnFunctionListener{
		//接听
		void onAcceptCall();
		//挂断
		void onRejectCall();
	}

	private OnFunctionListener mOnFunctionListener;

	public CallInWindowManager setOnFunctionListener(OnFunctionListener onFunctionListener) {
		mOnFunctionListener = onFunctionListener;
		return mInstance;
	}
}