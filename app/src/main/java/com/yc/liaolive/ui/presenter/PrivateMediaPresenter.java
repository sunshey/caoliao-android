package com.yc.liaolive.ui.presenter;

import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.bean.PrivateMedia;
import com.yc.liaolive.bean.ResultList;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.engine.HttpCoreEngin;
import com.yc.liaolive.ui.contract.PrivateMediaContract;
import org.json.JSONObject;
import java.util.Map;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * TinyHung@outlook.com
 * 2018/9/14
 * 私密相册、视频
 */
public class PrivateMediaPresenter extends RxBasePresenter<PrivateMediaContract.View> implements PrivateMediaContract.Presenter<PrivateMediaContract.View> {

    /**
     * 获取作者、他人的 私密相册、视频
     * @param homeUserID
     * @param mediaType 0：照片 1：视频
     * @param page 第几页
     */
    @Override
    public void getPrivateMedia(String homeUserID, final int mediaType,int page) {
        if(isLoading()) return;
        isLoading=true;
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_FILE_LIST());
        params.put("file_type",String.valueOf(mediaType));
        params.put("page",String.valueOf(page));
        params.put("to_userid",homeUserID);

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
                if(null!=mView) mView.showPrivateMediaError(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<ResultList<PrivateMedia>> data) {
                isLoading=false;
                if(null!=data){
                    if(NetContants.API_RESULT_CODE == data.getCode()){
                        if(null!=data.getData()&&null!=data.getData().getList()&&data.getData().getList().size()>0){
                            if(null!=mView) mView.showPrivateMedias(data.getData().getList());
                        }else if(null!=data.getData()&&null!=data.getData().getList()&&data.getData().getList().size()<=0){
                            if(null!=mView) mView.showPrivateMediaEmpty();
                        }else{
                            if(null!=mView) mView.showPrivateMediaError(-1,NetContants.NET_REQUST_JSON_ERROR);
                        }
                    }else{
                        if(null!=mView) mView.showPrivateMediaError(data.getCode(), NetContants.getErrorMsg(data));
                    }
                }else{
                    if(null!=mView) mView.showPrivateMediaError(-1,  NetContants.NET_REQUST_ERROR);
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 删除多媒体文件
     * @param mediaInfo
     * @param position
     */
    @Override
    public void deleteMediaFile(final PrivateMedia mediaInfo, final int position) {
        if(isLoading()) return;
        isLoading=true;
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
                isLoading=false;
                if(null!=mView) mView.showDeleteMediaFileResult(mediaInfo,position,0,"删除失败");
            }

            @Override
            public void onNext(ResultInfo<JSONObject> data) {
                isLoading=false;
                if(null!=data){
                    if(NetContants.API_RESULT_CODE == data.getCode()){
                        if(null!=mView) mView.showDeleteMediaFileResult(mediaInfo,position,data.getCode(),"删除成功");
                    }else{
                        if(null!=mView) mView.showDeleteMediaFileResult(mediaInfo,position,data.getCode(),NetContants.getErrorMsg(data));
                    }
                }else{
                    if(null!=mView) mView.showDeleteMediaFileResult(mediaInfo,position,data.getCode(),"删除失败");
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 修改多媒体文件访问权限
     * @param mediaInfo
     * @param position
     */
    @Override
    public void modifyMediaFilePrivatePermission(final PrivateMedia mediaInfo, final int position) {
        if(isLoading()) return;
        isLoading=true;

        //0：公开 1：私有
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_FILE_PRIVATE_CHANGED());
        params.put("ids",String.valueOf(mediaInfo.getId()));
        params.put("is_private", String.valueOf(0==mediaInfo.getIs_private()?1:0));
        params.put("file_type", String.valueOf(mediaInfo.getFile_type()));

        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_FILE_PRIVATE_CHANGED(),
                new TypeToken<ResultInfo<JSONObject>>(){}.getType(), params,getHeaders(),isRsa,isZip,isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<JSONObject>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                isLoading=false;
                if(null!=mView) mView.showModifyMediaFilePermissionResult(mediaInfo,position,0,"修改失败");
            }

            @Override
            public void onNext(ResultInfo<JSONObject> data) {
                isLoading=false;
                if(null!=data){
                    if(NetContants.API_RESULT_CODE == data.getCode()){
                        mediaInfo.setIs_private(0==mediaInfo.getIs_private()?1:0);
                        if(null!=mView) mView.showModifyMediaFilePermissionResult(mediaInfo,position,data.getCode(),"修改成功");
                    }else{
                        if(null!=mView) mView.showModifyMediaFilePermissionResult(mediaInfo,position,data.getCode(),NetContants.getErrorMsg(data));
                    }
                }else{
                    if(null!=mView) mView.showModifyMediaFilePermissionResult(mediaInfo,position,data.getCode(),"修改失败");
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
                        if(null!=mView) mView.showSetImageFrontResult(-1,"设置封面失败");
                    }

                    @Override
                    public void onNext(ResultInfo<JSONObject> data) {
                        if(null!=data){
                            if(null!=mView) mView.showSetImageFrontResult(data.getCode(),data.getMsg());
                        }else{
                            if(null!=mView) mView.showSetImageFrontResult(-1,"设置封面失败");
                        }
                    }
                });
        addSubscrebe(subscribe);
    }
}