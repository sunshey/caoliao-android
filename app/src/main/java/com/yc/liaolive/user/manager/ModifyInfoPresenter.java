package com.yc.liaolive.user.manager;

import android.net.Uri;
import android.os.AsyncTask;
import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.kaikai.securityhttp.net.entry.UpFileInfo;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.bean.PrivateMedia;
import com.yc.liaolive.bean.ResultList;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.engine.HttpCoreEngin;
import com.yc.liaolive.user.IView.ModifyInfoContract;
import com.yc.liaolive.util.ImageUtils;
import com.yc.liaolive.util.Logger;
import org.json.JSONObject;
import java.io.File;
import java.util.Map;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;


/**
 * TinyHung@outlook.com
 * 2017/10/17
 * 用户资料修改
 */

public class ModifyInfoPresenter extends RxBasePresenter<ModifyInfoContract.View> implements ModifyInfoContract.Presenter<ModifyInfoContract.View> {


    private boolean isDelete;//是否正在删除文件
    private boolean isSetFront;//是否正在设置封面
    private boolean isSetHead;//是否正在设置头像

    public boolean isDelete() {
        return isDelete;
    }

    public boolean isSetFront() {
        return isSetFront;
    }

    public boolean isSetHead() {
        return isSetHead;
    }

    /**
     * 获取用户头像
     */
    @Override
    public void getUserHeads() {
        if(isLoading()) return;
        isLoading=true;
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_FILE_LIST());
        params.put("file_type",String.valueOf(0));
        params.put("page",String.valueOf(1));
        params.put("to_userid",UserManager.getInstance().getUserId());

        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_FILE_LIST(),
                new TypeToken<ResultInfo<ResultList<PrivateMedia>>>(){}.getType(), params,getHeaders(),isRsa,isZip,isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<ResultList<PrivateMedia>>>() {

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                isLoading=false;
                if(null!=mView) mView.showHeadError(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<ResultList<PrivateMedia>> data) {
                isLoading=false;
                if(null!=data){
                    if(NetContants.API_RESULT_CODE == data.getCode()){
                        if(null!=data.getData()&&null!=data.getData().getList()&&data.getData().getList().size()>0){
                            if(null!=mView) mView.showHeadList(data.getData().getList());
                        }else if(null!=data.getData()&&null!=data.getData().getList()&&data.getData().getList().size()<=0){
                            if(null!=mView) mView.showHeadError(0,"数据为空");
                        }else{
                            if(null!=mView) mView.showHeadError(-1,NetContants.NET_REQUST_JSON_ERROR);
                        }
                    }else{
                        if(null!=mView) mView.showHeadError(data.getCode(), NetContants.getErrorMsg(data));
                    }
                }else{
                    if(null!=mView) mView.showHeadError(-1,  NetContants.NET_REQUST_ERROR);
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 获取用户发布的视频
     */
    @Override
    public void getUserVideos() {
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_FILE_LIST());
        params.put("file_type",String.valueOf(1));
        params.put("page",String.valueOf(1));
        params.put("to_userid",UserManager.getInstance().getUserId());

        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_FILE_LIST(),
                new TypeToken<ResultInfo<ResultList<PrivateMedia>>>(){}.getType(), params,getHeaders(),isRsa,isZip,isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<ResultList<PrivateMedia>>>() {

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if(null!=mView) mView.showVideoError(-1,NetContants.NET_REQUST_ERROR);
                    }

                    @Override
                    public void onNext(ResultInfo<ResultList<PrivateMedia>> data) {
                        if(null!=data){
                            if(NetContants.API_RESULT_CODE == data.getCode()){
                                if(null!=data.getData()&&null!=data.getData().getList()&&data.getData().getList().size()>0){
                                    if(null!=mView) mView.showVideoList(data.getData().getList());
                                }else if(null!=data.getData()&&null!=data.getData().getList()&&data.getData().getList().size()<=0){
                                    if(null!=mView) mView.showVideoError(0,"数据为空");
                                }else{
                                    if(null!=mView) mView.showVideoError(-1,NetContants.NET_REQUST_JSON_ERROR);
                                }
                            }else{
                                if(null!=mView) mView.showVideoError(data.getCode(), NetContants.getErrorMsg(data));
                            }
                        }else{
                            if(null!=mView) mView.showVideoError(-1,  NetContants.NET_REQUST_ERROR);
                        }
                    }
                });
        addSubscrebe(subscribe);
    }

    /**
     * 删除头像
     * @param mediaInfo
     * @param position
     */
    @Override
    public void deleteHeadImage(final PrivateMedia mediaInfo, final int position) {
        if(isDelete()) return;
        isDelete=true;
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_FILE_DELETE());
        params.put("id",String.valueOf(mediaInfo.getId()));

        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_FILE_DELETE(),
                new TypeToken<ResultInfo<JSONObject>>(){}.getType(), params,getHeaders(),isRsa,isZip,isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<JSONObject>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                isDelete=false;
                mView.showDeleteHeadImageResult(mediaInfo,position,0,"删除失败");
            }

            @Override
            public void onNext(ResultInfo<JSONObject> data) {
                isDelete=false;
                if(null!=data){
                    if(NetContants.API_RESULT_CODE == data.getCode()){
                        mView.showDeleteHeadImageResult(mediaInfo,position,data.getCode(),"删除成功");
                    }else{
                        mView.showDeleteHeadImageResult(mediaInfo,position,data.getCode(),NetContants.getErrorMsg(data));
                    }
                }else{
                    mView.showDeleteHeadImageResult(mediaInfo,position,data.getCode(),"删除失败");
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 设置某图片为用户默认头像
     * @param mediaInfo
     * @param position
     */
    @Override
    public void setImageFront(PrivateMedia mediaInfo, int position) {

        if(null==mediaInfo) return;
        if(isSetFront()) return;
        isSetFront =true;
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_SET_HEAD());
        params.put("file_id",String.valueOf(mediaInfo.getId()));

        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_SET_HEAD(),
                new TypeToken<ResultInfo<JSONObject>>(){}.getType(), params,getHeaders(),isRsa,isZip,isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<JSONObject>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                isSetFront =false;
                mView.showSetImageFrontResult(-1,"设置封面失败");
            }

            @Override
            public void onNext(ResultInfo<JSONObject> data) {
                isSetFront =false;
                if(null!=data){
                    mView.showSetImageFrontResult(data.getCode(),data.getMsg());
                }else{
                    mView.showSetImageFrontResult(-1,"设置封面失败");
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 设置用户头像
     * @param mediaInfo
     * @param position
     */
    @Override
    public void setUserHead(PrivateMedia mediaInfo, int position) {
        if(null==mediaInfo) return;
        if(isSetHead()) return;
        isSetHead =true;
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_UPLOAD_USER_INFO());
        params.put("userid", UserManager.getInstance().getUserId());
        params.put("fileId",String.valueOf(mediaInfo.getId()));

        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_UPLOAD_USER_INFO(),
                new TypeToken<ResultInfo<JSONObject>>(){}.getType(), params,getHeaders(),isRsa,isZip,isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<JSONObject>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        isSetHead =false;
                        mView.showSetUserHeadResult(-1,"设置头像失败");
                    }

                    @Override
                    public void onNext(ResultInfo<JSONObject> data) {
                        isSetHead =false;
                        if(null!=data){
                            mView.showSetUserHeadResult(data.getCode(),data.getMsg());
                        }else{
                            mView.showSetUserHeadResult(-1,"设置头像失败");
                        }
                    }
                });
        addSubscrebe(subscribe);
    }


    /**
     *
     * Map<String, String> params = new HashMap<>();
     params.put("userid", userID);
     new CompressAsyncTask(url, params, paramkey).execute(filePathm);
     */
    /**
     * 异步裁剪图片，并上传
     */
    private class CompressAsyncTask extends AsyncTask<String, Void, String> {

        private final Map<String, String> params;
        private final String url;
        private final String key;

        public CompressAsyncTask(String url, Map<String, String> params, String key) {
            this.url = url;
            this.params = params;
            this.key = key;
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
                Uri uriForFile = Uri.fromFile(new File(filePath));
                if (null == uriForFile) {
//                    if (null != mView) mView.showPostImageError(-1, "本地文件读取失败");
                    return;
                }
                upFileInfo.file = new File(uriForFile.getPath());
                upFileInfo.filename = upFileInfo.file.getName();
                upFileInfo.name = upFileInfo.file.getName();


//                progressView.showMessage("正在上传图像，请稍候...");
                Subscription subscribe = HttpCoreEngin.get(mContext).rxuploadFile(url,
                        new TypeToken<ResultInfo<JSONObject>>() {}.getType(), upFileInfo, params, UserManager.getInstance().getHeaders(), true)
                        .subscribe(new Subscriber<ResultInfo<JSONObject>>() {
                    @Override
                    public void onCompleted() {
                        isLoading = false;
                    }

                    @Override
                    public void onError(Throwable e) {
                        isLoading = false;
//                        if (progressView != null) progressView.dismiss();
//                        if (null != mView) mView.showPostImageError(-1, e.getLocalizedMessage());
                    }

                    @Override
                    public void onNext(ResultInfo<JSONObject> data) {
                        isLoading = false;
//                        if (progressView != null) progressView.dismiss();
                        if (NetContants.API_RESULT_CODE == data.getCode() && null != data.getData()) {
                            JSONObject jsonObject = data.getData();
                            if (null != jsonObject && jsonObject.length() > 0) {
//                                    if (null != mView) mView.showPostImageResult(jsonObject.getString(key));
                            } else {
//                                    if (null != mView) mView.showPostImageError(-1, "上传封面失败，服务器返回数据有误！");
                            }
                        } else {
//                            if (null != mView) mView.showPostImageError(data.getCode(), data.getMsg());
                        }
                    }
                });
                addSubscrebe(subscribe);
            } catch (RuntimeException e) {
                isLoading = false;
//                mView.showPostImageError(-1, "上传封面失败,兼容失败");
            }
        }
    }
}
