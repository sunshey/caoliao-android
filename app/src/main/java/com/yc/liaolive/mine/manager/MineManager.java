package com.yc.liaolive.mine.manager;

import android.text.TextUtils;

import com.yc.liaolive.R;
import com.yc.liaolive.bean.IndexMineUserInfo;
import com.yc.liaolive.bean.PersonCenterInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.user.manager.UserManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangxueqin on 2018/12/7.
 */

public class MineManager {

    /**
     * 初始化个人中心菜单列表
     * @param data 用户数据
     * @param isBeauty 是否支持美颜设置
     * @return
     */
    public static List<IndexMineUserInfo> createMineItemList(PersonCenterInfo data,boolean isBeauty) {
        if(null==data) return null;
        List<IndexMineUserInfo> mineListInfos = new ArrayList<>();
        if(UserManager.getInstance().isAuthenState()){
            //主播
            IndexMineUserInfo indexMineUserInfo1 = new IndexMineUserInfo();
            indexMineUserInfo1.setIcon(R.drawable.ic_mine_item_room);
            indexMineUserInfo1.setItemType(IndexMineUserInfo.ITEM_1);
            indexMineUserInfo1.setTitle("我的直播间");
            indexMineUserInfo1.setSubTitle("进入");
            indexMineUserInfo1.setLine(false);
            indexMineUserInfo1.setItemID(Constant.INDEX_MINE_TAB_ROOM);
            mineListInfos.add(indexMineUserInfo1);

            IndexMineUserInfo indexMineUserInfo2 = new IndexMineUserInfo();
            indexMineUserInfo2.setIcon(R.drawable.ic_mine_item_online);
            indexMineUserInfo2.setItemType(IndexMineUserInfo.ITEM_2);
            indexMineUserInfo2.setExcuse(0==UserManager.getInstance().getQuite());
            indexMineUserInfo2.setTitle("接收视频来电");
            indexMineUserInfo2.setShowBottomSpace(true);
            mineListInfos.add(indexMineUserInfo2);

            IndexMineUserInfo indexMineUserInfo3 = new IndexMineUserInfo();
            indexMineUserInfo3.setIcon(R.drawable.ic_mine_item_note);
            indexMineUserInfo3.setItemType(IndexMineUserInfo.ITEM_1);
            indexMineUserInfo3.setTitle("我的钱包");
            indexMineUserInfo3.setLine(false);
            indexMineUserInfo3.setShowTopSpace(true);
            indexMineUserInfo3.setItemID(Constant.INDEX_MINE_TAB_NOTEC);
            mineListInfos.add(indexMineUserInfo3);

            if(!TextUtils.isEmpty(data.getUser_seting_ad())){
                IndexMineUserInfo indexMineUserInfo7 = new IndexMineUserInfo();
                indexMineUserInfo7.setIcon(R.drawable.ic_mine_game);
                indexMineUserInfo7.setItemType(IndexMineUserInfo.ITEM_1);
                indexMineUserInfo7.setTitle("玩游戏得钻石");
                indexMineUserInfo7.setDesp(data.getUser_seting_ad());
                indexMineUserInfo7.setLine(false);
                indexMineUserInfo7.setItemID(Constant.INDEX_MINE_TAB_GAME);
                mineListInfos.add(indexMineUserInfo7);
            }

            IndexMineUserInfo indexMineUserInfo4 = new IndexMineUserInfo();
            indexMineUserInfo4.setIcon(R.drawable.ic_mine_item_integral);
            indexMineUserInfo4.setItemType(IndexMineUserInfo.ITEM_1);
            indexMineUserInfo4.setTitle("我的积分");
            indexMineUserInfo4.setLine(false);
            indexMineUserInfo4.setItemID(Constant.INDEX_MINE_TAB_INTEGRAL);
            mineListInfos.add(indexMineUserInfo4);

            IndexMineUserInfo indexMineUserInfo5 = new IndexMineUserInfo();
            indexMineUserInfo5.setIcon(R.drawable.ic_mine_item_video);
            indexMineUserInfo5.setItemType(IndexMineUserInfo.ITEM_1);
            indexMineUserInfo5.setTitle("我的通话");
            indexMineUserInfo5.setLine(false);
            indexMineUserInfo5.setItemID(Constant.INDEX_MINE_TAB_CALL);
            mineListInfos.add(indexMineUserInfo5);

            IndexMineUserInfo indexMineUserInfo6 = new IndexMineUserInfo();
            indexMineUserInfo6.setIcon(R.drawable.ic_mine_item_make);
            indexMineUserInfo6.setItemType(IndexMineUserInfo.ITEM_1);
            indexMineUserInfo6.setTitle("我的预约");
            indexMineUserInfo6.setLine(false);
            indexMineUserInfo6.setItemID(Constant.INDEX_MINE_TAB_MAKE);
            mineListInfos.add(indexMineUserInfo6);

            IndexMineUserInfo indexMineUserInfo7 = new IndexMineUserInfo();
            indexMineUserInfo7.setIcon(R.drawable.ic_mine_item_video);
            indexMineUserInfo7.setItemType(IndexMineUserInfo.ITEM_1);
            indexMineUserInfo7.setTitle("我的视频");
            indexMineUserInfo7.setSubTitle(String.valueOf(data.getMy_video_count()));
            indexMineUserInfo7.setLine(false);
            indexMineUserInfo7.setItemID(Constant.INDEX_MINE_TAB_VIDEO);
            mineListInfos.add(indexMineUserInfo7);

            IndexMineUserInfo indexMineUserInfo8 = new IndexMineUserInfo();
            indexMineUserInfo8.setIcon(R.drawable.ic_mine_item_photo);
            indexMineUserInfo8.setItemType(IndexMineUserInfo.ITEM_1);
            indexMineUserInfo8.setTitle("我的相册");
            indexMineUserInfo8.setSubTitle(String.valueOf(data.getMy_image_count()));
            indexMineUserInfo8.setLine(false);
            indexMineUserInfo8.setItemID(Constant.INDEX_MINE_TAB_PHOTO);
            mineListInfos.add(indexMineUserInfo8);

            if(isBeauty){
                IndexMineUserInfo indexMineUserInfo9 = new IndexMineUserInfo();
                indexMineUserInfo9.setIcon(R.drawable.ic_mine_item_fair);
                indexMineUserInfo9.setItemType(IndexMineUserInfo.ITEM_1);
                indexMineUserInfo9.setTitle("美颜设置");
                indexMineUserInfo9.setLine(true);
                indexMineUserInfo9.setShowBottomSpace(true);
                indexMineUserInfo9.setItemID(Constant.INDEX_MINE_TAB_BEAUTY);
                mineListInfos.add(indexMineUserInfo9);
            }

            if(null!= UserManager.getInstance().getServer()&&!UserManager.getInstance().getUserId().equals(UserManager.getInstance().getServerIdentify())){
                IndexMineUserInfo indexMineUserInfo10 = new IndexMineUserInfo();
                indexMineUserInfo10.setIcon(R.drawable.ic_mine_item_srever);
                indexMineUserInfo10.setItemType(IndexMineUserInfo.ITEM_1);
                indexMineUserInfo10.setTitle("在线客服");
                indexMineUserInfo10.setLine(false);
                indexMineUserInfo10.setShowTopSpace(true);
                indexMineUserInfo10.setItemID(Constant.INDEX_MINE_TAB_SERVER);
                mineListInfos.add(indexMineUserInfo10);
            }

            IndexMineUserInfo indexMineUserInfo11 = new IndexMineUserInfo();
            indexMineUserInfo11.setIcon(R.drawable.ic_mine_item_setting);
            indexMineUserInfo11.setItemType(IndexMineUserInfo.ITEM_1);
            indexMineUserInfo11.setTitle("系统设置");
            indexMineUserInfo11.setLine(false);
            indexMineUserInfo11.setItemID(Constant.INDEX_MINE_TAB_SETTING);
            mineListInfos.add(indexMineUserInfo11);
        }else{
            //用户
            IndexMineUserInfo indexMineUserInfo1 = new IndexMineUserInfo();
            indexMineUserInfo1.setIcon(R.drawable.ic_mine_item_note);
            indexMineUserInfo1.setItemType(IndexMineUserInfo.ITEM_1);
            indexMineUserInfo1.setTitle("我的钱包");
            indexMineUserInfo1.setLine(false);
            indexMineUserInfo1.setItemID(Constant.INDEX_MINE_TAB_NOTEC);
            mineListInfos.add(indexMineUserInfo1);

            if(!TextUtils.isEmpty(data.getUser_seting_ad())){
                IndexMineUserInfo indexMineUserInfo7 = new IndexMineUserInfo();
                indexMineUserInfo7.setIcon(R.drawable.ic_mine_game);
                indexMineUserInfo7.setItemType(IndexMineUserInfo.ITEM_1);
                indexMineUserInfo7.setTitle("玩游戏得钻石");
                indexMineUserInfo7.setDesp(data.getUser_seting_ad());
                indexMineUserInfo7.setLine(false);
                indexMineUserInfo7.setItemID(Constant.INDEX_MINE_TAB_GAME);
                mineListInfos.add(indexMineUserInfo7);
            }

            IndexMineUserInfo indexMineUserInfo2 = new IndexMineUserInfo();
            indexMineUserInfo2.setIcon(R.drawable.ic_mine_item_video);
            indexMineUserInfo2.setItemType(IndexMineUserInfo.ITEM_1);
            indexMineUserInfo2.setTitle("我的通话");
            indexMineUserInfo2.setLine(false);
            indexMineUserInfo2.setItemID(Constant.INDEX_MINE_TAB_CALL);
            mineListInfos.add(indexMineUserInfo2);

            IndexMineUserInfo indexMineUserInfo3 = new IndexMineUserInfo();
            indexMineUserInfo3.setIcon(R.drawable.ic_mine_item_make);
            indexMineUserInfo3.setItemType(IndexMineUserInfo.ITEM_1);
            indexMineUserInfo3.setTitle("我的预约");
            indexMineUserInfo3.setLine(false);
            indexMineUserInfo3.setItemID(Constant.INDEX_MINE_TAB_MAKE);
            mineListInfos.add(indexMineUserInfo3);

//            IndexMineUserInfo indexMineUserInfo4 = new IndexMineUserInfo();
//            indexMineUserInfo4.setIcon(R.drawable.ic_mine_item_room);
//            indexMineUserInfo4.setItemType(IndexMineUserInfo.ITEM_1);
//            indexMineUserInfo4.setTitle("成为主播");
//            indexMineUserInfo4.setLine(true);
//            indexMineUserInfo4.setShowBottomSpace(true);
//            indexMineUserInfo4.setItemID(Constant.INDEX_MINE_TAB_ANCHOR);
//            mineListInfos.add(indexMineUserInfo4);

            IndexMineUserInfo indexMineUserInfo5 = new IndexMineUserInfo();
            indexMineUserInfo5.setIcon(R.drawable.ic_mine_item_srever);
            indexMineUserInfo5.setItemType(IndexMineUserInfo.ITEM_1);
            indexMineUserInfo5.setTitle("在线客服");
            indexMineUserInfo5.setLine(false);
            indexMineUserInfo5.setShowTopSpace(true);
            indexMineUserInfo5.setItemID(Constant.INDEX_MINE_TAB_SERVER);
            mineListInfos.add(indexMineUserInfo5);

            IndexMineUserInfo indexMineUserInfo6 = new IndexMineUserInfo();
            indexMineUserInfo6.setIcon(R.drawable.ic_mine_item_setting);
            indexMineUserInfo6.setItemType(IndexMineUserInfo.ITEM_1);
            indexMineUserInfo6.setTitle("系统设置");
            indexMineUserInfo6.setLine(false);
            indexMineUserInfo6.setItemID(Constant.INDEX_MINE_TAB_SETTING);
            mineListInfos.add(indexMineUserInfo6);
        }
        return mineListInfos;
    }
}
