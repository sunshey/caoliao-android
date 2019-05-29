package com.yc.liaolive.user.manager;

import android.text.TextUtils;
import com.yc.liaolive.bean.IndexMineUserInfo;
import com.yc.liaolive.bean.PersonCenterInfo;
import com.yc.liaolive.contants.Constant;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangxueqin on 2018/12/7.
 */

public class PersonCenterManager {

    /**
     * 初始化个人中心菜单列表
     * @return
     * @param data 用户信息
     */
    public static List<IndexMineUserInfo> createPersonItemList(String to_userid, PersonCenterInfo data) {
        if(null==data) return null;
        List<IndexMineUserInfo> mineListInfos = new ArrayList<>();
        //粉丝
        IndexMineUserInfo indexMineUserInfo0 = new IndexMineUserInfo();
        indexMineUserInfo0.setItemType(0);
        indexMineUserInfo0.setFansCount(data.getFans_number());
        indexMineUserInfo0.setFollowCount(data.getAttent_number());
        indexMineUserInfo0.setIs_follow(data.getIs_attention());
        indexMineUserInfo0.setItemID(Constant.INDEX_PERCENTER_ITEM_FANS);
        mineListInfos.add(indexMineUserInfo0);
        //主播身份
        if(2==data.getIdentity_audit()){
            IndexMineUserInfo indexMineUserInfo1 = new IndexMineUserInfo();
            indexMineUserInfo1.setItemType(4);
            indexMineUserInfo1.setTitle("与我聊天需要");
            indexMineUserInfo1.setChat_deplete(data.getChat_deplete());
            indexMineUserInfo1.setFollowCount(0);
            indexMineUserInfo1.setItemID(Constant.INDEX_PERCENTER_ITEM_PRICE);
            mineListInfos.add(indexMineUserInfo1);
//            IndexMineUserInfo indexMineUserInfo2 = new IndexMineUserInfo();
//            indexMineUserInfo2.setItemType(4);
//            indexMineUserInfo2.setTitle("VIP专属价");
//            indexMineUserInfo2.setSubTitle(UserManager.getInstance().isVip()?"":"享50%优惠 立即开通>>");
//            indexMineUserInfo2.setChat_deplete(data.getChat_deplete()/2);
//            indexMineUserInfo2.setType(1);
//            indexMineUserInfo2.setFollowCount(0);
//            indexMineUserInfo2.setItemID(Constant.INDEX_PERCENTER_ITEM_VIP);
//            mineListInfos.add(indexMineUserInfo2);
        }
        //视频
        IndexMineUserInfo indexMineUserInfo3 = new IndexMineUserInfo();
        indexMineUserInfo3.setTitle("小视频");
        indexMineUserInfo3.setMediaCount(TextUtils.equals(to_userid,UserManager.getInstance().getUserId())?data.getMy_video_count():data.getVideo_count());
        indexMineUserInfo3.setMediaList(data.getVideo_list());
        indexMineUserInfo3.setSubTitle("");
        indexMineUserInfo3.setItemType(3);
        indexMineUserInfo3.setItemID(Constant.INDEX_PERCENTER_ITEM_VIDEO);
        indexMineUserInfo3.setMediaType(Constant.MEDIA_TYPE_VIDEO);
        mineListInfos.add(indexMineUserInfo3);
        //相册
        IndexMineUserInfo indexMineUserInfo8 = new IndexMineUserInfo();
        indexMineUserInfo8.setTitle("相册");
        indexMineUserInfo8.setMediaCount(TextUtils.equals(to_userid,UserManager.getInstance().getUserId())?data.getMy_image_count():data.getImage_count());
        indexMineUserInfo8.setMediaList(data.getImage_list());
        indexMineUserInfo8.setItemType(3);
        indexMineUserInfo8.setItemID(Constant.INDEX_PERCENTER_ITEM_IMAGE);
        indexMineUserInfo8.setMediaType(Constant.MEDIA_TYPE_IMAGE);
        mineListInfos.add(indexMineUserInfo8);
        //粉丝贡献榜
        IndexMineUserInfo indexMineUserInfo2 = new IndexMineUserInfo();
        indexMineUserInfo2.setItemType(1);
        indexMineUserInfo2.setItemID(Constant.INDEX_PERCENTER_ITEM_TOP);
        mineListInfos.add(indexMineUserInfo2);
        //昵称
        IndexMineUserInfo indexMineUserInfo4 = new IndexMineUserInfo();
        indexMineUserInfo4.setMoreText(data.getNickname());
        indexMineUserInfo4.setItemType(2);
        indexMineUserInfo4.setTitle("昵称");
        mineListInfos.add(indexMineUserInfo4);
        //ID
        IndexMineUserInfo indexMineUserInfo5 = new IndexMineUserInfo();
        indexMineUserInfo5.setMoreText(data.getUserid());
        indexMineUserInfo5.setItemType(2);
        indexMineUserInfo5.setTitle("ID");
        mineListInfos.add(indexMineUserInfo5);
        //性别
        IndexMineUserInfo indexMineUserInfo6 = new IndexMineUserInfo();
        indexMineUserInfo6.setMoreText(0==data.getSex()?"男":"女");
        indexMineUserInfo6.setItemType(2);
        indexMineUserInfo6.setTitle("性别");
        mineListInfos.add(indexMineUserInfo6);
        //位置
        IndexMineUserInfo indexMineUserInfo7 = new IndexMineUserInfo();
        indexMineUserInfo7.setMoreText(TextUtils.isEmpty(data.getPosition())?"地球":data.getPosition());
        indexMineUserInfo7.setItemType(2);
        indexMineUserInfo7.setTitle("TA的位置");
        mineListInfos.add(indexMineUserInfo7);
        return mineListInfos;
    }
}
