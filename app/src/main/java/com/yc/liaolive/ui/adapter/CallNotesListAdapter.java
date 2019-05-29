package com.yc.liaolive.ui.adapter;

import android.support.annotation.Nullable;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.yc.liaolive.base.adapter.BaseMultiItemQuickAdapter;
import com.yc.liaolive.base.adapter.BaseViewHolder;
import com.yc.liaolive.model.GlideCircleTransform;
import com.yc.liaolive.msg.model.bean.CallMessageInfo;
import com.yc.liaolive.util.DateUtil;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/10/16
 * 我的通话、预约、钻石、积分 记录
 */

public class CallNotesListAdapter extends BaseMultiItemQuickAdapter<CallMessageInfo,BaseViewHolder> {

    public static final int ITEM_TYPE_LET=0;//通话记录
    public static final int ITEM_TYPE_MAKE=1;//预约记录
    public static final int ITEM_TYPE_DIAMOND=2;//钻石记录
    public static final int ITEM_TYPE_INTEGRAL=3;//积分记录

    public CallNotesListAdapter(@Nullable List<CallMessageInfo> data) {
        super(data);
        addItemType(ITEM_TYPE_LET,R.layout.re_call_notes_list_item);
        addItemType(ITEM_TYPE_MAKE,R.layout.re_call_make_list_item);
        addItemType(ITEM_TYPE_DIAMOND,R.layout.re_call_diamond_list_item);
        addItemType(ITEM_TYPE_INTEGRAL,R.layout.re_call_diamond_list_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, CallMessageInfo item) {
        switch (item.getItemType()) {
            case ITEM_TYPE_LET:
                setItemDataLET(helper,item);
                break;
            case ITEM_TYPE_MAKE:
                setItemDataMake(helper,item);
                break;
            case ITEM_TYPE_DIAMOND:
                setItemDataDiamond(helper,item);
                break;
            case ITEM_TYPE_INTEGRAL:
                setItemDataDiamond(helper,item);
                break;
        }
    }

    /**
     * 通话记录
     * @param helper
     * @param item
     */
    private void setItemDataLET(BaseViewHolder helper, CallMessageInfo item) {
        if(null!=item){
            long millis=item.getTime()*1000;
            helper.setText(R.id.item_name,item.getNickname())
                    .setText(R.id.item_last_message,item.getContent())
                    .setText(R.id.item_message_time, DateUtil.timeFormatDay(millis)+" "+DateUtil.timeFormatMinute(millis));// 月：日  时：分
            TextView lastMessage = (TextView) helper.getView(R.id.item_last_message);
            lastMessage.setTextColor(1==item.getState()?mContext.getResources().getColor(R.color.gray):mContext.getResources().getColor(R.color.coment_color_66));
            //用户头像
            Glide.with(mContext)
                    .load(item.getAvatar())
                    .error(R.drawable.ic_default_user_head)
                    .crossFade()//渐变
                    .animate(R.anim.item_alpha_in)//加载中动画
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                    .centerCrop()//中心点缩放
                    .skipMemoryCache(true)//跳过内存缓存
                    .transform(new GlideCircleTransform(mContext))
                    .into((ImageView) helper.getView(R.id.item_icon));
            helper.addOnClickListener(R.id.re_user_view);
            helper.getView(R.id.re_user_view).setTag(item);
            helper.itemView.setTag(item);
        }
    }

    /**
     * 预约记录
     * @param helper
     * @param item
     */
    private void setItemDataMake(BaseViewHolder helper, CallMessageInfo item) {
        if(null!=item){
            if(2==item.getType()){
                //主播身份，并且预约中，显示回拨按钮
                helper.getView(R.id.item_anchor_call).setVisibility(0==item.getState()?View.VISIBLE:View.INVISIBLE);
            }else{
                //用户身份
                helper.getView(R.id.item_anchor_call).setVisibility(View.INVISIBLE);
            }
            TextView lastMessage = (TextView) helper.getView(R.id.item_last_message);
            lastMessage.setText(0==item.getState()?"预约中":1==item.getState()?"预约成功":"预约失败");
            lastMessage.setBackgroundResource(0==item.getState()?R.drawable.bg_make_ing:R.drawable.bg_make_end);

            long millis=item.getTime()*1000;
            helper.setText(R.id.item_name,item.getNickname())
                    .setText(R.id.item_message_time, DateUtil.timeFormatDay(millis)+" "+DateUtil.timeFormatMinute(millis));
            //用户头像
            Glide.with(mContext)
                    .load(item.getAvatar())
                    .error(R.drawable.ic_default_user_head)
                    .crossFade()//渐变
                    .animate(R.anim.item_alpha_in)//加载中动画
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                    .centerCrop()//中心点缩放
                    .skipMemoryCache(true)//跳过内存缓存
                    .transform(new GlideCircleTransform(mContext))
                    .into((ImageView) helper.getView(R.id.item_icon));

            helper.addOnClickListener(R.id.ll_anchor_make).addOnClickListener(R.id.re_user_view);
            helper.getView(R.id.ll_anchor_make).setTag(item);
            helper.getView(R.id.re_user_view).setTag(item);
            helper.itemView.setTag(item);
        }
    }

    /**
     * 积分、钻石记录
     * @param helper
     * @param item
     */
    private void setItemDataDiamond(BaseViewHolder helper, CallMessageInfo item) {
        if(null!=item){
            long millis=item.getTime()*1000;
            helper.setText(R.id.item_name,item.getTitle())
                    .setText(R.id.item_price, Html.fromHtml(""))
                    .setText(R.id.item_nick_name,item.getNickname())
                    .setText(R.id.item_time_minute, DateUtil.timeFormatDay(millis)+" "+DateUtil.timeFormatMinute(millis));//月:日 时:分
            TextView textPrice = (TextView) helper.getView(R.id.item_price);
            //如果是负数，红色标识
            textPrice.setText(Html.fromHtml(item.getContentState()+"："+(item.getPrice()<0?"<font color='#FF7575'>"+item.getPrice()+"</font>":item.getPrice())+(ITEM_TYPE_INTEGRAL==item.getItemType()?"积分":"钻石")+""));
            //用户头像
            Glide.with(mContext)
                    .load(item.getAvatar())
                    .error(R.drawable.ic_default_user_head)
                    .crossFade()//渐变
                    .animate(R.anim.item_alpha_in)//加载中动画
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                    .centerCrop()//中心点缩放
                    .skipMemoryCache(true)//跳过内存缓存
                    .transform(new GlideCircleTransform(mContext))
                    .into((ImageView) helper.getView(R.id.item_icon));
            helper.addOnClickListener(R.id.item_ll_user_item);
            helper.getView(R.id.item_ll_user_item).setTag(item);
            helper.itemView.setTag(item);
        }
    }
}
