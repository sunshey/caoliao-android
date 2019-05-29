package com.yc.liaolive.ui.adapter;

import android.graphics.Color;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.yc.liaolive.R;
import com.yc.liaolive.base.adapter.BaseMultiItemQuickAdapter;
import com.yc.liaolive.base.adapter.BaseViewHolder;
import com.yc.liaolive.bean.IndexMineUserInfo;
import com.yc.liaolive.bean.PrivateMedia;
import com.yc.liaolive.media.adapter.PrivateMinMediaAdapter;
import com.yc.liaolive.view.widget.CommenItemLayout;
import com.yc.liaolive.view.widget.IndexMineFansListLayout;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/10/11
 * 用户中心适配器
 */

public class PersonCenterAdapter extends BaseMultiItemQuickAdapter<IndexMineUserInfo,BaseViewHolder> {

    public static final int ITEM_0=0;//粉丝、关注
    public static final int ITEM_1=1;//粉丝贡献榜
    public static final int ITEM_2=2;//普通条目
    public static final int ITEM_3=3;//小视频、相册
    public static final int ITEM_4=4;//视频通话

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public PersonCenterAdapter(List<IndexMineUserInfo> data) {
        super(data);
        addItemType(ITEM_0,R.layout.re_person_center_item0);
        addItemType(ITEM_1,R.layout.re_person_center_item1);
        addItemType(ITEM_2,R.layout.re_person_center_item2);
        addItemType(ITEM_3,R.layout.re_person_center_item3);
        addItemType(ITEM_4,R.layout.re_person_center_item4);
    }

    @Override
    protected void convert(BaseViewHolder helper, IndexMineUserInfo item) {
        helper.itemView.setTag(item);
        switch (item.getItemType()) {
            case ITEM_0:
                setItemData0(helper,item);
                break;
            case ITEM_1:
                setItemData1(helper,item);
                break;
            case ITEM_2:
                setItemData2(helper,item);
                break;
            case ITEM_3:
                setItemData3(helper,item);
                break;
            case ITEM_4:
                setItemData4(helper,item);
                break;

        }
    }

    /**
     * 粉丝、关注
     * @param helper
     * @param item
     */
    private void setItemData0(BaseViewHolder helper, IndexMineUserInfo item) {
        if(null!=item){
            helper.setText(R.id.tv_item_fans_count,String.valueOf(item.getFansCount())).setText(R.id.tv_item_follow_count,String.valueOf(item.getFollowCount()));
            TextView followText = (TextView) helper.getView(R.id.btn_item_follow);
            followText.setText(1==item.getIs_follow()?"已关注":"关注");
            followText.setBackgroundResource(1==item.getIs_follow()?R.drawable.full_room_gray_bg_pre_8:R.drawable.full_room_follow_selector);
            followText.setTag(item.getIs_follow());
            helper.addOnClickListener(R.id.btn_item_follow);
        }
    }

    /**
     * 粉丝贡献榜
     * @param helper
     * @param item
     */
    private void setItemData1(final BaseViewHolder helper, IndexMineUserInfo item) {
        if(null!=item){
            IndexMineFansListLayout fansListLayout = helper.getView(R.id.person_face_item);
            if(null!=item.getFansInfos()) fansListLayout.setUserData(item.getFansInfos());
            helper.addOnClickListener(R.id.fans_root_item);
        }
    }

    /**
     * 普通条目
     * @param helper
     * @param item
     */
    private void setItemData2(BaseViewHolder helper, IndexMineUserInfo item) {
        if(null!=item){
            CommenItemLayout tabView = (CommenItemLayout) helper.getView(R.id.item_view_tab);
            tabView.setItemTitle(item.getTitle());
            tabView.setItemMoreTitle(item.getMoreText());
            if(TextUtils.equals("ID",item.getTitle())){
                tabView.setTag(item.getMoreText());
                helper.addOnClickListener(R.id.item_view_tab);
            }
        }
    }

    /**
     * 小视频、相册
     * @param helper
     * @param item
     */
    private void setItemData3(final BaseViewHolder helper, final IndexMineUserInfo item) {
        if(null!=item){
            helper.setText(R.id.item_tv_title,item.getTitle()).setText(R.id.item_media_count,String.valueOf(item.getMediaCount()));
            helper.setText(R.id.item_sub_title,Html.fromHtml("<font color='#16A7EA'>"+(TextUtils.isEmpty(item.getSubTitle())?"":item.getSubTitle())+"</font>"));
            final GridView gridView = helper.getView(R.id.item_grid_view);
            if(null!=item.getMediaList()&&item.getMediaList().size()>0){
                gridView.setVisibility(View.VISIBLE);
            }else{
                gridView.setVisibility(View.GONE);
            }
            final PrivateMinMediaAdapter adapter = new PrivateMinMediaAdapter(mContext,item.getMediaList(),item.getMediaType());
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(null!=mOnMediaItemClickListener){
                        try {
                            PrivateMedia privateMedia = (PrivateMedia) adapter.getItem(position);
                            mOnMediaItemClickListener.onItemClick(privateMedia,view,position);
                        }catch (RuntimeException e){

                        }
                    }
                }
            });
            gridView.setAdapter(adapter);
            helper.addOnClickListener(R.id.media_root_item);
        }
    }

    /**
     * 视频通话
     * @param helper
     * @param item
     */
    private void setItemData4(BaseViewHolder helper, IndexMineUserInfo item) {
        if(null!=item){
            if(1==item.getType()){
                helper.setText(R.id.item_title,Html.fromHtml("<font color='#EF6280'>"+item.getTitle()+"</font>"))
                        .setText(R.id.tv_item_price,Html.fromHtml("<font color='#EF6280'>"+item.getChat_deplete()+"</font> 钻石/分钟"))
                        .setText(R.id.item_sub_title,Html.fromHtml("<font color='#16A7EA'>"+item.getSubTitle()+"</font>"));
                helper.addOnClickListener(R.id.item_sub_title);
                ((ImageView) helper.getView(R.id.item_state_icon)).setColorFilter(Color.parseColor("#EF6280"));
            }else{
                ((ImageView) helper.getView(R.id.item_state_icon)).setColorFilter(Color.parseColor("#999999"));
                helper.setText(R.id.item_title,Html.fromHtml("<font color='#333333'>"+item.getTitle()+"</font>"))
                        .setText(R.id.tv_item_price,Html.fromHtml("<font color='#999999'>"+item.getChat_deplete()+" 钻石/分钟</font>"));
            }
        }
    }

    public interface OnMediaItemClickListener{
        void onItemClick(PrivateMedia privateMedia,View view, int pisition);
    }

    private OnMediaItemClickListener mOnMediaItemClickListener;

    public void setOnMediaItemClickListener(OnMediaItemClickListener onMediaItemClickListener) {
        mOnMediaItemClickListener = onMediaItemClickListener;
    }
}