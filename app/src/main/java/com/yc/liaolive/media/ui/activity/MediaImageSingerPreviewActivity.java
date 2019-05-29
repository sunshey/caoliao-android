package com.yc.liaolive.media.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseActivity;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.ActivityImageSingerPreviewBinding;
import com.yc.liaolive.util.CommonUtils;
import com.yc.liaolive.util.FileUtils;
import com.yc.liaolive.util.ScreenUtils;
import com.yc.liaolive.util.SystemUtils;
import com.yc.liaolive.util.ToastUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


/**
 * TinyHung@Outlook.com
 * 2018/10/12
 * 预览单张图片 支持长按下载
 */

public class MediaImageSingerPreviewActivity extends BaseActivity<ActivityImageSingerPreviewBinding> {

    private String imageUrl=null;//图片下载地址
    private boolean isDownload=false;
    private boolean download=true;
    private String mActionBar;
    private Handler mHandler;

    public static void start(Activity context, String cover, String actionBar,View view) {
        Intent intent = new Intent(context, MediaImageSingerPreviewActivity.class);
        intent.putExtra("imape_url",cover);
        intent.putExtra("action_bar",actionBar);
        if(null!=view){
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(context, view, CommonUtils.getString(R.string.transition_movie_img));//与xml文件对应
            ActivityCompat.startActivity(context,intent, options.toBundle());
        }else{
            context.startActivity(intent);
        }
    }


    @Override
    public void initViews() {
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.imageView:
                    case R.id.root_view:
                    case R.id.btn_back:
                        onBackPressed();
                        break;

                }
            }
        };
        bindingView.rootView.setOnClickListener(onClickListener);
        bindingView.imageView.setOnClickListener(onClickListener);
        bindingView.btnBack.setOnClickListener(onClickListener);

        bindingView.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                SystemUtils.startVibrator(100);
                showPopupWindown();
                return false;
            }
        });
    }



    @Override
    public void initData() {
        Intent intent = getIntent();
        imageUrl = intent.getStringExtra("imape_url");
        mActionBar = intent.getStringExtra("action_bar");
        //设置背景封面
        Glide.with(this)
                .load(imageUrl)
                .error(R.drawable.bg_live_transit)
                .dontAnimate()
                .thumbnail(0.1f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                .skipMemoryCache(true)//跳过内存缓存
                .into(bindingView.imageView);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_singer_preview);
        download=true;
        mHandler = new Handler(Looper.getMainLooper());
        mHandler.postAtTime(new Runnable() {
            @Override
            public void run() {
                if(null!=bindingView) bindingView.btnBack.setVisibility(View.VISIBLE);
            }
        }, SystemClock.uptimeMillis()+560);
    }

    /**
     * 显示保存图片弹窗
     */
    private void showPopupWindown() {
        View conentView= View.inflate(this,R.layout.popupwindown_copy_image_layout,null);
        conentView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        final PopupWindow popupWindow= new PopupWindow(conentView, ScreenUtils.dpToPxInt(200f), ViewGroup.LayoutParams.WRAP_CONTENT, false);
        popupWindow.setClippingEnabled(false);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#50000000")));
        popupWindow.setAnimationStyle(R.style.CenterDialogAnimationStyle);
        popupWindow.setFocusable(true);
        TextView saveBar = (TextView) conentView.findViewById(R.id.tv_save_image);
        if(!TextUtils.isEmpty(mActionBar)){
            saveBar.setText(mActionBar);
        }
        conentView.findViewById(R.id.tv_save_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                if(TextUtils.isEmpty(imageUrl)){
                    ToastUtils.showCenterToast("保存失败!");
                    return;
                }
                if(isDownload){
                    ToastUtils.showCenterToast("图片正在下载!");
                    return;
                }
                //检查SD卡权限
                RxPermissions.getInstance(MediaImageSingerPreviewActivity.this).request(Manifest.permission.WRITE_EXTERNAL_STORAGE).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if(null!=aBoolean&&aBoolean){
                            new DownloadFileTask().execute(imageUrl);
                        }else{
                            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MediaImageSingerPreviewActivity.this)
                                    .setTitle("SD写入权限被拒绝!")
                                    .setMessage(getResources().getString(R.string.permissions_image_tips));
                            builder.setNegativeButton("去设置", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    SystemUtils.getInstance().startAppDetailsInfoActivity(MediaImageSingerPreviewActivity.this,141);
                                }
                            });
                            builder.show();
                        }
                    }
                });
            }
        });
        conentView.findViewById(R.id.tv_canale).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        popupWindow.showAtLocation(conentView, Gravity.CENTER, 0, 0);
    }

    /**
     * 图片下载
     */
    private class DownloadFileTask extends AsyncTask<String,Integer,File> {

        private int laterate = 0;//当前已读字节

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isDownload=true;
            bindingView.circleProgressbar.setVisibility(View.VISIBLE);
            bindingView.circleProgressbar.setProgress(0);
        }

        @Override
        protected File doInBackground(String... params) {
            File file=new File(Constant.IMAGE_PATH);
            if(!file.exists()){
                file.mkdirs();
            }
            String name=FileUtils.getFileName(params[0]);
            try {
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(900000);
                conn.setConnectTimeout(900000);
                conn.setDoInput(true);
                conn.setRequestMethod("GET");
                if(conn.getResponseCode()==200){
                    int length = conn.getContentLength();
                    int count = 0;
                    File outPutPath = new File(file.getAbsolutePath());
                    if (!outPutPath.exists()) {
                        outPutPath.mkdirs();
                    }
                    File apkDownloadPath = new File(outPutPath, name);
                    InputStream in = conn.getInputStream();
                    FileOutputStream os = new FileOutputStream(apkDownloadPath);
                    byte[] buffer = new byte[1024];
                    do {
                        int numread = in.read(buffer);
                        count += numread;
                        int progress = (int) (((float) count / length) * 100);// 得到当前进度
                        if (progress >= laterate + 1) {// 只有当前进度比上一次进度大于等于1，才可以更新进度
                            laterate = progress;
                            this.publishProgress(progress);
                        }
                        if (numread <= 0) {//下载完毕
                            break;
                        }
                        os.write(buffer, 0, numread);
                    } while (download);
                    in.close();
                    os.close();
                    return apkDownloadPath;
                }else{
                    Log.d("下载更新", "doInBackground: conn.getResponseCode()="+conn.getResponseCode());
                    return null;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.toString();
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if(null!=bindingView) bindingView.circleProgressbar.setProgressNotInUiThread(values[0]);
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            isDownload=false;
            if(null!=bindingView) bindingView.circleProgressbar.setVisibility(View.GONE);
            if(null!=file&&file.exists()&&file.isFile()){
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                MediaStore.Images.Media.insertImage(MediaImageSingerPreviewActivity.this.getApplicationContext().getContentResolver(), bitmap, file.getName(), "用户头像");
                showFinlishToast(null,null,"已保存至相册"+file.getAbsolutePath());
            }else{
                showErrorToast(null,null,"下载失败！");
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(null!=bindingView) bindingView.btnBack.setVisibility(View.GONE);
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        download=false;
        if(null!=mHandler) mHandler.removeMessages(0);
        super.onDestroy();
        imageUrl=null;
        Runtime.getRuntime().gc();
    }
}
