package com.yc.liaolive.ui.presenter;

import android.app.Activity;

import com.kaikai.securityhttp.domain.ResultInfo;
import com.kaikai.securityhttp.net.contains.HttpConfig;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.bean.MineTabData;
import com.yc.liaolive.bean.PersonCenterInfo;
import com.yc.liaolive.bean.TabMineUserInfo;
import com.yc.liaolive.engine.PersonCenterEngine;
import com.yc.liaolive.ui.contract.PersonCenterContract;
import com.yc.liaolive.user.manager.UserManager;

import rx.Subscriber;
import rx.Subscription;

/**
 * Created by wanglin  on 2018/7/9 15:59.
 */
public class PersonCenterPresenter extends RxBasePresenter<PersonCenterContract.View> implements PersonCenterContract.Presenter<PersonCenterContract.View> {

    private final PersonCenterEngine mEngine;

    public PersonCenterPresenter(Activity activity) {
        mEngine = new PersonCenterEngine(activity);
    }

    @Override
    public void getPersonCenterInfo(String to_userid) {
        if (isLoading) return;
        isLoading = true;
        Subscription subscription = mEngine.getPersonCenterInfo(UserManager.getInstance().getUserId(), to_userid).subscribe(new Subscriber<ResultInfo<PersonCenterInfo>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                isLoading=false;
                if(null!=mView) mView.showPersonInfoError(-1,"请求失败");
            }

            @Override
            public void onNext(ResultInfo<PersonCenterInfo> data) {
                isLoading=false;
                if(null!=mView){
                    if(null!=data){
                        if( data.getCode() == HttpConfig.STATUS_OK){
                            mView.showPersonInfo(data.getData());
                        }else{
                            mView.showPersonInfoError(data.getCode(),data.getMsg());
                        }
                    }else{
                        mView.showPersonInfoError(-1,"请求失败");
                    }
                }
            }
        });
        addSubscrebe(subscription);
    }

    @Override
    public void getItemList() {
//        boolean isAnchor=false;
//        String content;
//        if(isAnchor){
//            content="[\n" +
//                    "    {\n" +
//                    "      \"icon\": \"http://t.tn990.com/upload/friends-check.png\",\n" +
//                    "      \"title\": \"我的直播间\",\n" +
//                    "      \"sub_title\": \"进入、审核中\",\n" +
//                    "      \"show_line\": \"0\",\n" +
//                    "      \"type\": \"1\",\n" +
//                    "      \"jump_url\": \"com.yc.liaolive.live.ui.activity.LiveRoomPusherActivity\"\n" +
//                    "    },\n" +
//                    "    {\n" +
//                    "      \"icon\": \"http://t.tn990.com/upload/friends-check.png\",\n" +
//                    "      \"title\": \"接收视频通话\",\n" +
//                    "      \"sub_title\": \"\",\n" +
//                    "      \"show_line\": \"1\",\n" +
//                    "      \"type\": \"2\",\n" +
//                    "      \"jump_url\": \"\"\n" +
//                    "    },\n" +
//                    "    {\n" +
//                    "      \"icon\": \"http://t.tn990.com/upload/friends-check.png\",\n" +
//                    "      \"title\": \"我的钱包\",\n" +
//                    "      \"sub_title\": \"\",\n" +
//                    "      \"show_line\": \"0\",\n" +
//                    "      \"type\": \"0\",\n" +
//                    "      \"jump_url\": \"com.yc.liaolive.user.ui.NotecaseActivity\"\n" +
//                    "    },\n" +
//                    "    {\n" +
//                    "      \"icon\": \"http://t.tn990.com/upload/friends-check.png\",\n" +
//                    "      \"title\": \"我的积分\",\n" +
//                    "      \"sub_title\": \"\",\n" +
//                    "      \"show_line\": \"0\",\n" +
//                    "      \"type\": \"0\",\n" +
//                    "      \"jump_url\": \"com.yc.liaolive.user.ui.IntegralDetailsActivity?typeId=3\"\n" +
//                    "    },\n" +
//                    "    {\n" +
//                    "      \"icon\": \"http://t.tn990.com/upload/friends-check.png\",\n" +
//                    "      \"title\": \"我的通话\",\n" +
//                    "      \"sub_title\": \"\",\n" +
//                    "      \"show_line\": \"0\",\n" +
//                    "      \"type\": \"0\",\n" +
//                    "      \"jump_url\": \"com.yc.liaolive.user.ui.MakeCallDetailsActivity?activityType=0&showIndex=0&wwww=www.baudi.com\"\n" +
//                    "    },\n" +
//                    "    {\n" +
//                    "      \"icon\": \"http://t.tn990.com/upload/friends-check.png\",\n" +
//                    "      \"title\": \"我的预约\",\n" +
//                    "      \"sub_title\": \"\",\n" +
//                    "      \"show_line\": \"0\",\n" +
//                    "      \"type\": \"1\",\n" +
//                    "      \"jump_url\": \"com.yc.liaolive.user.ui.MakeCallDetailsActivity?activityType=1showIndex=1&wwww=www.baudi.com\"\n" +
//                    "    },\n" +
//                    "    {\n" +
//                    "      \"icon\": \"http://t.tn990.com/upload/friends-check.png\",\n" +
//                    "      \"title\": \"我的视频\",\n" +
//                    "      \"sub_title\": \"12\",\n" +
//                    "      \"show_line\": \"0\",\n" +
//                    "      \"type\": \"1\",\n" +
//                    "      \"jump_url\": \"com.yc.liaolive.media.ui.activity.PrivateMediaVideoActivity?homeUserID=123456\"\n" +
//                    "    },\n" +
//                    "    {\n" +
//                    "      \"icon\": \"http://t.tn990.com/upload/friends-check.png\",\n" +
//                    "      \"title\": \"我的相册\",\n" +
//                    "      \"sub_title\": \"21\",\n" +
//                    "      \"show_line\": \"0\",\n" +
//                    "      \"type\": \"1\",\n" +
//                    "      \"jump_url\": \"com.yc.liaolive.media.ui.activity.PrivateMediaVideoActivity?homeUserID=123456\"\n" +
//                    "    },\n" +
//                    "    {\n" +
//                    "      \"icon\": \"http://t.tn990.com/upload/friends-check.png\",\n" +
//                    "      \"title\": \"美颜设置\",\n" +
//                    "      \"sub_title\": \"\",\n" +
//                    "      \"show_line\": \"1\",\n" +
//                    "      \"type\": \"1\",\n" +
//                    "      \"jump_url\": \"com.faceunity.beauty.ui.BeautySettingActivity\"\n" +
//                    "    },\n" +
//                    "    {\n" +
//                    "      \"icon\": \"http://t.tn990.com/upload/friends-check.png\",\n" +
//                    "      \"title\": \"在线客服\",\n" +
//                    "      \"sub_title\": \"\",\n" +
//                    "      \"show_line\": \"0\",\n" +
//                    "      \"type\": \"1\",\n" +
//                    "      \"jump_url\": \"com.yc.liaolive.msg.ui.activity.ChatActivity\"\n" +
//                    "    },\n" +
//                    "    {\n" +
//                    "      \"icon\": \"http://t.tn990.com/upload/friends-check.png\",\n" +
//                    "      \"title\": \"系统设置\",\n" +
//                    "      \"sub_title\": \"\",\n" +
//                    "      \"show_line\": \"0\",\n" +
//                    "      \"type\": \"1\",\n" +
//                    "      \"jump_url\": \"com.yc.liaolive.user.ui.SettingActivity\"\n" +
//                    "    }\n" +
//                    "  ]";
//        }else{
//            content="[\n" +
//                    "    {\n" +
//                    "      \"icon\": \"http://t.tn990.com/upload/friends-check.png\",\n" +
//                    "      \"title\": \"我的钱包\",\n" +
//                    "      \"sub_title\": \"\",\n" +
//                    "      \"show_line\": \"0\",\n" +
//                    "      \"type\": \"0\",\n" +
//                    "      \"jump_url\": \"com.yc.liaolive.user.ui.NotecaseActivity\"\n" +
//                    "    },\n" +
//                    "    {\n" +
//                    "      \"icon\": \"http://t.tn990.com/upload/friends-check.png\",\n" +
//                    "      \"title\": \"我的通话\",\n" +
//                    "      \"sub_title\": \"\",\n" +
//                    "      \"show_line\": \"0\",\n" +
//                    "      \"type\": \"1\",\n" +
//                    "      \"jump_url\": \"com.yc.liaolive.user.ui.MakeCallDetailsActivity?activityType=0\"\n" +
//                    "    },\n" +
//                    "    {\n" +
//                    "      \"icon\": \"http://t.tn990.com/upload/friends-check.png\",\n" +
//                    "      \"title\": \"我的预约\",\n" +
//                    "      \"sub_title\": \"\",\n" +
//                    "      \"show_line\": \"0\",\n" +
//                    "      \"type\": \"1\",\n" +
//                    "      \"jump_url\": \"com.yc.liaolive.user.ui.MakeCallDetailsActivity?activityType=1\"\n" +
//                    "    },\n" +
//                    "    {\n" +
//                    "      \"icon\": \"http://t.tn990.com/upload/friends-check.png\",\n" +
//                    "      \"title\": \"成为主播\",\n" +
//                    "      \"sub_title\": \"\",\n" +
//                    "      \"show_line\": \"1\",\n" +
//                    "      \"type\": \"1\",\n" +
//                    "      \"jump_url\": \"com.yc.liaolive.user.ui.UserAuthenticationActivity\"\n" +
//                    "    },\n" +
//                    "    {\n" +
//                    "      \"icon\": \"http://t.tn990.com/upload/friends-check.png\",\n" +
//                    "      \"title\": \"在线客服\",\n" +
//                    "      \"sub_title\": \"\",\n" +
//                    "      \"show_line\": \"0\",\n" +
//                    "      \"type\": \"1\",\n" +
//                    "      \"jump_url\": \"com.yc.liaolive.msg.ui.activity.ChatActivity\"\n" +
//                    "    },\n" +
//                    "    {\n" +
//                    "      \"icon\": \"http://t.tn990.com/upload/friends-check.png\",\n" +
//                    "      \"title\": \"系统设置\",\n" +
//                    "      \"sub_title\": \"\",\n" +
//                    "      \"show_line\": \"0\",\n" +
//                    "      \"type\": \"1\",\n" +
//                    "      \"jump_url\": \"com.yc.liaolive.user.ui.SettingActivity\"\n" +
//                    "    }\n" +
//                    "  ]";
//        }

//        List<TabMineUserInfo> resultList = new Gson().fromJson(content, new TypeToken<List<TabMineUserInfo>>(){}.getType());
//        mView.showPersonList(resultList);

        Subscription subscription = mEngine.getItemList().subscribe(new Subscriber<ResultInfo<MineTabData>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if(null!=mView) mView.showPersonListError(-1,"请求失败");
            }

            @Override
            public void onNext(ResultInfo<MineTabData> data) {
                if(null!=mView){
                    if(null!=data){
                        if( data.getCode() == HttpConfig.STATUS_OK){
                            if(null!=data&&null!=data.getData()&&null!=data.getData().getList()){
                                for (TabMineUserInfo mineUserInfo : data.getData().getList()) {
                                    mineUserInfo.setQuite(data.getData().getQuite());
                                    mineUserInfo.setIdentity_audit(data.getData().getIdentity_audit());
                                }
                                mView.showPersonList(data.getData().getList());
                            }else{
                                mView.showPersonListError(-1,data.getMsg());
                            }
                        }else{
                            mView.showPersonListError(data.getCode(),data.getMsg());
                        }
                    }else{
                        mView.showPersonListError(-1,"请求失败");
                    }
                }
            }
        });
        addSubscrebe(subscription);
    }
}
