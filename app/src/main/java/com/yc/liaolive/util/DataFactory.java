package com.yc.liaolive.util;

import android.text.TextUtils;

import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.bean.FragmentMenu;
import com.yc.liaolive.bean.PrivateMedia;
import com.yc.liaolive.bean.RoomTaskDataInfo;
import com.yc.liaolive.bean.TagInfo;
import com.yc.liaolive.bean.TaskInfo;
import com.yc.liaolive.contants.Cheeses;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.live.bean.GiftTypeInfo;
import com.yc.liaolive.msg.model.bean.CallMessageInfo;
import com.yc.liaolive.recharge.model.bean.VipListItem;

import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/10/11
 */

public class DataFactory {

    private static final String TAG = "DataFactory";



    public static List<CallMessageInfo> createMsgList(int itemType) {
        List<CallMessageInfo> data=new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            CallMessageInfo callMessageInfo=new CallMessageInfo();
            callMessageInfo.setNickname("张三："+i);
            callMessageInfo.setAvatar(Constant.DEFAULT_FRONT_COVER);
            callMessageInfo.setItemType(itemType);
            callMessageInfo.setTime(System.currentTimeMillis());
            callMessageInfo.setUserid("36821447");
            if(i%2==0){
                callMessageInfo.setAnswerState(1);
                callMessageInfo.setLevel_integral(21+i);
                callMessageInfo.setIntimate_value(315+i);
                callMessageInfo.setContent("未接来电");
                callMessageInfo.setContentState("收入金额");
                callMessageInfo.setPrice(251+i);
                callMessageInfo.setTitle("礼物赠送");
            }else{
                callMessageInfo.setAnswerState(0);
                callMessageInfo.setLevel_integral(11+i);
                callMessageInfo.setIntimate_value(565+i);
                callMessageInfo.setContent("通话2小时，收入2000钻石");
                callMessageInfo.setContentState("支出金额");
                callMessageInfo.setPrice(-(35+i));
                callMessageInfo.setTitle("视频通话");
            }
            data.add(callMessageInfo);
        }
        return data;
    }


    public static List<PrivateMedia> createPrivateMedia(int mediaType) {
        List<PrivateMedia> privateMedias =new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            PrivateMedia privateMedia =new PrivateMedia();
            if(0==i||1==i){
                privateMedia.setIs_private(0);
                privateMedia.setState(0);
            }else{
                privateMedia.setState(1);
                privateMedia.setIs_private(0);
            }
            privateMedia.setImg_path(1==mediaType?"http://www.sinaimg.cn/dy/slidenews/24_img/2015_32/66095_1249432_186814.jpg":"http://c.hiphotos.baidu.com/image/h%3D300/sign=4bc239aadda20cf45990f8df46094b0c/9d82d158ccbf6c81924a92c5b13eb13533fa4099.jpg");
            privateMedia.setVideo_durtion(56354);
            privateMedia.setPrice(25);
            privateMedia.setIs_private(1);
            privateMedia.setFile_type(mediaType);
            if(i%3==0){
                privateMedia.setIs_private(1);//设置为公开
            }
            privateMedias.add(privateMedia);
        }
        return privateMedias;
    }


    public static List<GiftTypeInfo> createGiftTabs() {
        List<GiftTypeInfo> tabs=new ArrayList<>();
        GiftTypeInfo giftTypeInfo1=new GiftTypeInfo();
        giftTypeInfo1.setName("hot");
        giftTypeInfo1.setTitle("热门");
        giftTypeInfo1.setId(1);

        GiftTypeInfo giftTypeInfo2=new GiftTypeInfo();
        giftTypeInfo2.setName("normal");
        giftTypeInfo2.setTitle("普通");
        giftTypeInfo2.setId(2);

        GiftTypeInfo giftTypeInfo3=new GiftTypeInfo();
        giftTypeInfo3.setName("expensive");
        giftTypeInfo3.setTitle("奢侈");
        giftTypeInfo3.setId(3);

        GiftTypeInfo giftTypeInfo4=new GiftTypeInfo();
        giftTypeInfo4.setName("whole");
        giftTypeInfo4.setTitle("全站");
        giftTypeInfo4.setId(4);

        tabs.add(giftTypeInfo1);
        tabs.add(giftTypeInfo2);
        tabs.add(giftTypeInfo3);
        tabs.add(giftTypeInfo4);

        return tabs;
    }

    public static List<TaskInfo> shortList(List<RoomTaskDataInfo> data) {
        if(null!=data){
            List<TaskInfo> taskInfos=new ArrayList<>();
            for (RoomTaskDataInfo datum : data) {
                for (int i = 0; i < datum.getList().size(); i++) {
                    TaskInfo taskInfo = datum.getList().get(i);
                    taskInfo.setType(datum.getTitle());
                    //如果是分类下的最后一个
                    if(i==(datum.getList().size()-1)){
                        taskInfo.setLastPosition(true);
                    }
                    taskInfos.add(taskInfo);
                }
            }
            return taskInfos;
        }
        return null;
    }
    /**
     * 身高、体重、星座数据生产
     * @param subTitle
     * @return
     */
    public static List<String> createPickerData(String subTitle) {
        List<String> data=new ArrayList<>();
        if(TextUtils.equals("kg",subTitle)){
            for (int i = 30; i < 120; i++) {
                data.add(i+"");
            }
        }else if(TextUtils.equals("cm",subTitle)){
            for (int i = 120; i < 240; i++) {
                data.add(i+"");
            }
        }else{
            for (int i = 0; i < 12; i++) {
                data.add(Cheeses.MEMBER_STAR[i]);
            }
        }
        return data;
    }


    /**
     * 返回用户选中的身高、体重、星座等index
     * @param data
     * @param hintSelected 选中的项 如 170 双子座
     * @return
     */
    public static int getCurrentHint(List<String> data, String hintSelected) {
        if(null!=data&&data.size()>0&&!TextUtils.isEmpty(hintSelected)){
            for (int i = 0; i < data.size(); i++) {
                if(TextUtils.equals(hintSelected,data.get(i))){
                    return i;
                }
            }
        }
        return 0;
    }


    /**
     * 格式化用户标签
     * @param label
     * @return 标签1、标签2
     */
    public static String framtTags(String label) {
        if(TextUtils.isEmpty(label)) return "";
        try {
            if(null!= VideoApplication.getInstance().getTags()){
                //根据索引获取对应的TAGS
                StringBuilder stringBuilder=new StringBuilder();
                String[] split = label.split("\\,");
                int count=0;
                //内存中存在的TAGS
                List<TagInfo> tagInfos = VideoApplication.getInstance().getTags();
                for (int i = 0; i < split.length; i++) {
                    for (int i1 = 0; i1 < tagInfos.size(); i1++) {
                        if(Integer.parseInt(split[i])==tagInfos.get(i1).getId()){
                            if(0!=count) stringBuilder.append("、");
                            stringBuilder.append(tagInfos.get(i1).getContent());
                        }
                    }
                    count++;
                }
                return stringBuilder.toString();
            }
        }catch (RuntimeException e){

        }
        return "";
    }

    public static List<FragmentMenu> createIndexFragments() {
        List<FragmentMenu> fragmentMenus=new ArrayList<>();

        FragmentMenu fragmentMenu=new FragmentMenu();
        fragmentMenu.setFragment_type(1);
        fragmentMenu.setFragment_title("小视频");
        fragmentMenus.add(fragmentMenu);

        FragmentMenu fragmentMenu1=new FragmentMenu();
        fragmentMenu1.setFragment_type(-1);
        fragmentMenu1.setFragment_title("1v1快聊");
        fragmentMenus.add(fragmentMenu1);

        FragmentMenu fragmentMenu2=new FragmentMenu();
        fragmentMenu2.setFragment_type(0);
        fragmentMenu2.setFragment_title("图片");
        fragmentMenus.add(fragmentMenu2);
        return fragmentMenus;
    }

    public static List<VipListItem> createVipRewardList() {
        List<VipListItem> itemList=new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            VipListItem vipRewardItem=new VipListItem();
            vipRewardItem.setText("送15000钻");
            vipRewardItem.setDay_text("第"+(i+1)+"天");
            if(0==i){
                vipRewardItem.setImg_url("http://a.tn990.com/uploads/task/coin_receive_1.jpg");
            }else{
                vipRewardItem.setImg_url("http://a.tn990.com/uploads/task/coin_receive_2.jpg");
            }
            itemList.add(vipRewardItem);
        }
        return itemList;
    }
}