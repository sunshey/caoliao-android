package com.yc.liaolive.ui.presenter;

import android.net.Uri;
import android.os.AsyncTask;

import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.kaikai.securityhttp.net.entry.UpFileInfo;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.engine.HttpCoreEngin;
import com.yc.liaolive.ui.contract.UploadImageContract;
import com.yc.liaolive.util.ImageUtils;
import com.yc.liaolive.util.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import rx.Subscriber;
import rx.Subscription;

/**
 * TinyHung@outlook.com
 * 2018/6/4
 * 图片上传
 */
public class UploadImagePresenter extends RxBasePresenter<UploadImageContract.View> implements UploadImageContract.Presenter<UploadImageContract.View> {

    private static final String TAG ="UploadImagePresenter";

    private boolean isLoading=false;

    public boolean isLoading() {
        return isLoading;
    }

    /**
     * 上传图片
     * @param userID
     */

    @Override
    public void onPostImagePhoto(String url, String userID, String filePathm,String paramkey) {
        if(isLoading) return;
        isLoading=true;
        Map<String,String> params=new HashMap<>();
        params.put("userid",userID);
        new CompressAsyncTask(url,params,paramkey).execute(filePathm);
    }


    /**
     * 异步裁剪图片，并上传
     */
    private class CompressAsyncTask extends AsyncTask<String,Void,String>{

        private final Map<String, String> params;
        private final String url;
        private final String key;

        public CompressAsyncTask(String url,Map<String, String> params,String key) {
            this.url=url;
            this.params=params;
            this.key=key;
        }

        //异步裁剪
        @Override
        protected String doInBackground(String... params) {
            return ImageUtils.changeFileSizeByLocalPath(params[0]);
        }

        @Override
        protected void onPostExecute(String filePath) {
            super.onPostExecute(filePath);
            try {
                //上传文件
                UpFileInfo upFileInfo = new UpFileInfo();
                Uri uriForFile= Uri.fromFile(new File(filePath));
                if(null==uriForFile) {
                    if(null!=mView) mView.showPostImageError(-1, "本地文件读取失败");
                    return;
                }
                upFileInfo.file = new File(uriForFile.getPath());
                upFileInfo.filename = upFileInfo.file.getName();
                upFileInfo.name = upFileInfo.file.getName();

                Subscription subscribe = HttpCoreEngin.get(mContext).rxuploadFile(url,
                        new TypeToken<ResultInfo<JSONObject>>() {}.getType(), upFileInfo, params, getHeaders(),isEncryptResponse)
                        .subscribe(new Subscriber<ResultInfo<JSONObject>>() {
                    @Override
                    public void onCompleted() {
                        isLoading = false;
                    }

                    @Override
                    public void onError(Throwable e) {
                        isLoading = false;
                        if (null != mView) mView.showPostImageError(-1, NetContants.NET_REQUST_ERROR);
                    }

                    @Override
                    public void onNext(ResultInfo<JSONObject> data) {
                        isLoading = false;
                        if (NetContants.API_RESULT_CODE == data.getCode() && null != data.getData()) {
                            try {
                                JSONObject jsonObject = data.getData();
                                if (null != jsonObject && jsonObject.length() > 0) {
                                    if (null != mView)
                                        mView.showPostImageResult(jsonObject.getString(key));
                                } else {
                                    if (null != mView)
                                        mView.showPostImageError(-1, "上传封面失败，服务器返回数据有误！");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                if (null != mView)
                                    mView.showPostImageError(-1, "上传封面失败：" + e.getMessage());
                            }
                        } else {
                            if (null != mView)
                                mView.showPostImageError(data.getCode(), data.getMsg());
                        }
                    }
                });
                addSubscrebe(subscribe);
            }  catch (RuntimeException e){
                isLoading = false;
                mView.showPostImageError(-1,"上传封面失败,兼容失败");
            }
        }
    }
}
