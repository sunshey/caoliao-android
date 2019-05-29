package com.yc.liaolive.live.adapter;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.base.adapter.BaseViewHolder;
import com.yc.liaolive.bean.FansInfo;
import com.yc.liaolive.live.util.LiveUtils;
import com.yc.liaolive.model.GlideCircleTransform;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017/10/7
 * 直播间在线观众列表
 */

public class LiveFansListAdapter extends BaseQuickAdapter<FansInfo,BaseViewHolder> {

    public LiveFansListAdapter(List<FansInfo> data) {
        super(R.layout.re_user_fans_item,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, FansInfo item) {
        if(null==item) return;
        //用户封面
        Glide.with(mContext)
                .load(item.getAvatar())
                .placeholder(R.drawable.ic_default_user_head)
                .error(R.drawable.ic_default_user_head)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()//中心点缩放
                .transform(new GlideCircleTransform(mContext))
                .into((ImageView) helper.getView(R.id.iv_user_icon));
        //用户等级，显示用户的会员区间等级
        ImageView userGradle = (ImageView) helper.getView(R.id.item_user_gradle);
        if(item.getVip()>0){
            LiveUtils.setUserBlockVipGradle(userGradle,item.getVip());
        }else if(item.getLevel_integral()>0){
            LiveUtils.setUserGradle(userGradle,item.getLevel_integral());
        }else{
            userGradle.setImageResource(0);
        }
        helper.itemView.setTag(item);
    }
    /**
     * 添加一个元素
     * @param data
     */
    @Override
    public synchronized void addData(@NonNull FansInfo data) {
        if(null==data) return ;
        if(null!=mData) {
            boolean isContain = false;
            for (FansInfo datum : mData) {
                if (TextUtils.equals(datum.getUserid(), data.getUserid())) {
                    isContain = true;
                    break;
                }
            }
            if (isContain) return;
            //根据送礼物金额，等级，进房间时间排序
            mData.add(data);//暂时排在第一位
            notifyItemInserted(mData.size() + getHeaderLayoutCount());
            final int dataSize = mData == null ? 0 : mData.size();
            if (dataSize == 1) {
                notifyDataSetChanged();
            }
        }
    }


    /**
     * 有返回值的添加单条数据
     * @param data
     * @return
     */
    public synchronized boolean addItemData(@NonNull FansInfo data) {
        if(null==data) return false;
        if(null!=mData) {
            boolean isContain = false;
            for (FansInfo datum : mData) {
                if (TextUtils.equals(datum.getUserid(), data.getUserid())) {
                    isContain = true;
                    break;
                }
            }
            if (isContain) return false;
            //根据送礼物金额，等级，进房间时间排序
            mData.add(data);//暂时排在第一位
            notifyItemInserted(mData.size() + getHeaderLayoutCount());
            final int dataSize = mData == null ? 0 : mData.size();
            if (dataSize == 1) {
                notifyDataSetChanged();
            }
        }
        return true;
    }

    /**
     * 移除个元素
     * @param data
     */
    public synchronized void removeData(FansInfo data) {
        if(null==data) return;
        if(null!=mData){
            boolean isContain=false;
            int index=0;
            for (int i = 0; i < mData.size(); i++) {
                FansInfo audienceInfo = mData.get(i);
                if(TextUtils.equals(audienceInfo.getUserid(),data.getUserid())){
                    isContain=true;
                    index=i;
                    break;
                }
            }
            if(isContain) {
                mData.remove(index);
                notifyDataSetChanged();
            }
        }
    }
}
