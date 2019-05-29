package com.yc.liaolive.recharge.adapter;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.yc.liaolive.R;
import com.yc.liaolive.base.adapter.BaseMultiItemQuickAdapter;
import com.yc.liaolive.base.adapter.BaseViewHolder;
import com.yc.liaolive.recharge.model.bean.RechargeGoodsInfo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

/**
 * TinyHung@Outlook.com
 * 2018/6/13
 * 充值-商品列表
 */

public class RechargeGoldItemAdapter extends BaseMultiItemQuickAdapter<RechargeGoodsInfo, BaseViewHolder> {

    public static final int ITEM_TYPE_GOLD=0;
    public static final int ITEM_TYPE_MORE=1;
    private Drawable unSelectedDrawable;
    private Drawable selectedDrawable;
    private int itemStyle;//0:默认的黑色字体 1：主题色样式
    private int selectPosition;
    private RechargeGoodsInfo selectGoodsInfo;

    public RechargeGoldItemAdapter(@Nullable List<RechargeGoodsInfo> data) {
        super(data);
        addItemType(ITEM_TYPE_GOLD,R.layout.re_recharge_glod_goods_item);
        addItemType(ITEM_TYPE_MORE,R.layout.re_recharge_glod_more_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, RechargeGoodsInfo item) {
        if (null != item) {
            switch (item.getItemType()) {
                case ITEM_TYPE_GOLD:
                    setGoodsItem(helper,item, helper.getAdapterPosition());
                    break;
                case ITEM_TYPE_MORE:
                    setMoreItem(helper,item);
                    break;
            }
        }
    }

    /**
     * 商品列表
     * @param helper
     * @param item
     */
    private void setGoodsItem(BaseViewHolder helper, RechargeGoodsInfo item, int position) {
        if(null!=item){
            //价格
            TextView itemPrice = (TextView) helper.getView(R.id.item_price);
            BigDecimal bd = new BigDecimal(TextUtils.isEmpty(item.getPrice())?"0.00":item.getPrice());
            itemPrice.setText(String.format(Locale.CHINA, "%s元", bd.setScale(2, BigDecimal.ROUND_HALF_DOWN).toString()));
            if(1==itemStyle){
                itemPrice.setTextColor(item.isSelected() ? Color.parseColor("#FFFFFF") : Color.parseColor("#FF5E69"));
            }else{
                itemPrice.setTextColor(item.isSelected() ? Color.parseColor("#FA4D77") : Color.parseColor("#000000"));
            }
            //金币
            TextView priceMonry = (TextView) helper.getView(R.id.item_price_monry);
            priceMonry.setText(String.format(Locale.CHINA, "%d钻石", item.getUse_number()));
            if(1==itemStyle){
                priceMonry.setTextColor(item.isSelected() ? Color.parseColor("#FFFFFF") : Color.parseColor("#FF5E69"));
            }
            //赠送
            TextView giveTextView = (TextView) helper.getView(R.id.item_give_money);
            giveTextView.setText(item.getGive_use_number() > 0 ? String.format(Locale.CHINA, "赠%d钻石", item.getGive_use_number()) : "");
            giveTextView.setVisibility(item.getGive_use_number() > 0 ? View.VISIBLE : View.GONE);
            helper.getView(R.id.item_empty_view).setVisibility(item.getGive_use_number() > 0 ? View.GONE : View.VISIBLE);
            //背景选中颜色
            View rootView = helper.getView(R.id.ll_root_view);
            Drawable drawable;
            if(item.isSelected()){
                selectPosition = position;
                selectGoodsInfo = item;
                drawable=(null==selectedDrawable?mContext.getResources().getDrawable(R.drawable.ic_good_selected):selectedDrawable);
            }else{
                drawable=(null==unSelectedDrawable?mContext.getResources().getDrawable(R.drawable.bg_gift_item_gray):unSelectedDrawable);
            }
            rootView.setBackground(drawable);
            helper.itemView.setTag(item);
        }
    }

    /**
     * 更多...
     * @param helper
     * @param item
     */
    private void setMoreItem(BaseViewHolder helper, RechargeGoodsInfo item) {
        helper.itemView.setTag(item);
    }

    @Override
    protected void convert(BaseViewHolder helper, RechargeGoodsInfo item, List<Object> payloads) {
        super.convert(helper, item, payloads);
        if(null!=item){
            if(payloads.isEmpty()){
                convert(helper,item);
            }else {
                if(item.getItemType()==ITEM_TYPE_GOLD){
                    //价格
                    TextView itemPrice = (TextView) helper.getView(R.id.item_price);
                    if(1==itemStyle){
                        itemPrice.setTextColor(item.isSelected() ? Color.parseColor("#FFFFFF") : Color.parseColor("#FF5E69"));
                    }else{
                        itemPrice.setTextColor(item.isSelected() ? Color.parseColor("#FA4D77") : Color.parseColor("#000000"));
                    }
                    //金币
                    TextView priceMonry = (TextView) helper.getView(R.id.item_price_monry);
                    if(1==itemStyle){
                        priceMonry.setTextColor(item.isSelected() ? Color.parseColor("#FFFFFF") : Color.parseColor("#FF5E69"));
                    }
                    //背景选中颜色
                    View rootView = helper.getView(R.id.ll_root_view);
                    Drawable drawable;
                    if(item.isSelected()){
                        selectPosition = helper.getAdapterPosition();
                        selectGoodsInfo = item;
                        drawable=(null==selectedDrawable?mContext.getResources().getDrawable(R.drawable.ic_good_selected):selectedDrawable);
                    }else{
                        drawable=(null==unSelectedDrawable?mContext.getResources().getDrawable(R.drawable.bg_gift_item_gray):unSelectedDrawable);
                    }
                    rootView.setBackground(drawable);
                }
                helper.itemView.setTag(item);
            }
        }
    }

    /**
     * 选中样式
     * @param drawable
     */
    public void setSelectedDrawable(Drawable drawable) {
        this.selectedDrawable=drawable;
    }

    /**
     * 未选中样式
     * @param drawable
     */
    public void setUnSelectedDrawable(Drawable drawable) {
        this.unSelectedDrawable=drawable;
    }

    public void setItemStyle(int itemStyle) {
        this.itemStyle=itemStyle;
    }

    public int getSelectPosition() {
        return selectPosition;
    }

    public RechargeGoodsInfo getSelectGoodsInfo() {
        return selectGoodsInfo;
    }
}
