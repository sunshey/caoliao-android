package com.yc.liaolive.recharge.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.yc.liaolive.R;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.base.adapter.BaseViewHolder;
import com.yc.liaolive.recharge.model.bean.RechargeGoodsInfo;

import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/10/22
 * 会员商品
 */

public class VipGoodsAdapter extends BaseQuickAdapter<RechargeGoodsInfo,BaseViewHolder> {


    public VipGoodsAdapter(@Nullable List<RechargeGoodsInfo> data) {
        super(R.layout.re_item_vip_goods,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, RechargeGoodsInfo item) {
        if(null!=item){
            TextView actTips = helper.getView(R.id.item_act_tips);
            TextView subTitle = helper.getView(R.id.item_sub_title);
            helper.setText(R.id.item_goods_title,item.getName()).
                    setText(R.id.item_title_price,item.getPrice()+"元")
                    .setText(R.id.item_title_desc,item.getDesc());
            subTitle.setText(item.getSub_title());
            subTitle.setVisibility(TextUtils.isEmpty(item.getSub_title())?View.GONE:View.VISIBLE);
            actTips.setText(item.getText_show_hn());
            actTips.setVisibility(TextUtils.isEmpty(item.getText_show_hn())?View.GONE:View.VISIBLE);
            helper.getView(R.id.root_item).setSelected(item.isSelected());
            if (TextUtils.isEmpty(item.getText_show_xs())) {
                helper.getView(R.id.item_xianshi).setVisibility(View.GONE);
            } else {
                helper.getView(R.id.item_xianshi).setVisibility(View.VISIBLE);
                ((TextView)helper.getView(R.id.item_xianshi)).setText(item.getText_show_xs());
            }

            TextView huasuan = helper.getView(R.id.item_goods_huasuan);
            //优惠政策
            if (TextUtils.isEmpty(item.getText_show_yh())) {
                huasuan.setVisibility(View.GONE);
            } else {
                huasuan.setVisibility(View.VISIBLE);
                huasuan.setText(item.getText_show_yh());
                huasuan.setBackgroundResource(1==item.getIs_show_yh()?R.drawable.vip_recharge_yellow_icon_bg:3==item.getIs_show_yh()?R.drawable.oval_bg_half_gray:0);
            }
            helper.itemView.setTag(item);
        }
    }

    @Override
    protected void convert(BaseViewHolder helper, RechargeGoodsInfo item, List<Object> payloads) {
        if(payloads.isEmpty()){
            convert(helper,item);
        }else{
            if(null!=item){
                helper.getView(R.id.root_item).setSelected(item.isSelected());
                helper.itemView.setTag(item);
            }
        }
    }
}
