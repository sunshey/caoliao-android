package com.yc.liaolive.ui.presenter;

import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.live.bean.RoomList;
import com.yc.liaolive.ui.contract.IndexVideoLiveContract;
import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2018/8/19
 */
public class IndexVideoLivePresenter extends RxBasePresenter<IndexVideoLiveContract.View> implements IndexVideoLiveContract.Presenter<IndexVideoLiveContract.View> {

    /**
     * 获取推荐的一对一在线用户列表
     * rtmp://live.hkstv.hk.lxdns.com/live/hks
     */
    @Override
    public void getPrivateVideos(final boolean isRelease) {
        if(isLoading) return;
        isLoading=true;
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<RoomList> roomLists=new ArrayList<>();
                for (int i = 0; i < 20; i++) {
                    RoomList roomList=new RoomList();
                    roomList.setTitle("曹操直播间"+i);
                    roomList.setNickname("貂蝉"+(i+1)+"号");
                    roomList.setChat_minite(1);
                    roomList.setChat_deplete(1998+i);
//                    roomList.setUserid("44104321");//梦一场
                    roomList.setUserid("57578839");//那就这样吧
//                    roomList.setUserid("48615068");//皇太极
                    roomList.setCity("武汉市");
                    roomList.setId(i);
                    roomList.setMember_total(18+i);
                    if(i==0){
                        roomList.setFrontcover(Constant.DEFAULT_FRONT_COVER);
                        roomList.setPlayUrl("http://sleep-bshu.oss-cn-shenzhen.aliyuncs.com/rewustart.flv");
                    }else if(i==1){
                        roomList.setFrontcover("http://i02.cztv.com/2014/06/1402363209_17692000.jpg");
                        roomList.setPlayUrl("http://sleep-bshu.oss-cn-shenzhen.aliyuncs.com/3.flv");
                    }else if(i==2){
                        roomList.setFrontcover("https://b-ssl.duitang.com/uploads/item/201411/18/20141118165102_sd8YJ.thumb.700_0.jpeg");
                        roomList.setPlayUrl("http://sleep-bshu.oss-cn-shenzhen.aliyuncs.com/xingganrewu.flv");
                    }else if(i==3){
                        roomList.setPlayUrl("http://sleep-bshu.oss-cn-shenzhen.aliyuncs.com/rewu.flv");
                        roomList.setFrontcover("https://b-ssl.duitang.com/uploads/item/201411/07/20141107170710_nS5w3.thumb.700_0.png");
                    }else if(i==4){
                        roomList.setPlayUrl("http://sleep-bshu.oss-cn-shenzhen.aliyuncs.com/rewuzhong.flv");
                        roomList.setFrontcover("http://d.hiphotos.baidu.com/image/pic/item/3bf33a87e950352aadfff8c55f43fbf2b3118b65.jpg");
                    }else if(i==5){
                        roomList.setPlayUrl("http://sleep-bshu.oss-cn-shenzhen.aliyuncs.com/xingganrewu.flv");
                        roomList.setFrontcover("https://b-ssl.duitang.com/uploads/item/201506/13/20150613222251_f8eTQ.thumb.1900_0.jpeg");
                    }else if(i%2==0){
                        roomList.setPlayUrl("http://sleep-bshu.oss-cn-shenzhen.aliyuncs.com/yisuwan.flv");
                        roomList.setFrontcover(Constant.DEFAULT_FRONT_COVER);
                    }else{
                        roomList.setPlayUrl("rtmp://live.hkstv.hk.lxdns.com/live/hks");
                        roomList.setFrontcover("https://b-ssl.duitang.com/uploads/item/201410/30/20141030193003_xJvCz.thumb.700_0.jpeg");
                    }
                    roomList.setAvatar(Constant.DEFAULT_FRONT_COVER);
                    roomLists.add(roomList);
                }
                isLoading=false;
                if(null!=mView) mView.showLiveRooms(roomLists,isRelease);
            }
        },800);

//        Map<String, String> params = getDefaultPrames(NetContants.URL_ROOM_HOT);
//        params.put("last_userid",lastUserID);
//        params.put("page_size", "20");
//        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(url, new TypeReference<ResultInfo<ResultList<RoomList>>>(){}.getType(), params,getHeaders(),isRsa,isZip,isEncryptResponse).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<ResultList<RoomList>>>() {
//            @Override
//            public void onCompleted() {
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Logger.d(TAG,"e:"+e.getMessage()+"ERROR:"+e.toString());
//                isLoading=false;
//                if(null!=mView) mView.showLiveRoomError(-1,NetContants.NET_REQUST_ERROR);
//            }
//
//            @Override
//            public void onNext(ResultInfo<ResultList<RoomList>> data) {
//                isLoading=false;
//                if(null!=data){
//                    if(NetContants.API_RESULT_CODE == data.getCode()){
//                        if(null!=data.getData()&&null!=data.getData().getList()&&data.getData().getList().size()>0){
//                            if(null!=mView) mView.showLiveRooms(data.getData().getList());
//                        }else if(null!=data.getData()&&null!=data.getData().getList()&&data.getData().getList().size()<=0){
//                            if(null!=mView) mView.showLiveRoomEmpty();
//                        }else{
//                            if(null!=mView) mView.showLiveRoomError(-1,NetContants.NET_REQUST_JSON_ERROR);
//                        }
//                    }else{
//                        Logger.d(TAG,"ERROR");
//                        if(null!=mView) mView.showLiveRoomError(data.getCode(), NetContants.getErrorMsg(data));
//                    }
//                }else{
//                    if(null!=mView) mView.showLiveRoomError(-1,  NetContants.NET_REQUST_ERROR);
//                }
//            }
//        });
//        addSubscrebe(subscribe);
    }
}
