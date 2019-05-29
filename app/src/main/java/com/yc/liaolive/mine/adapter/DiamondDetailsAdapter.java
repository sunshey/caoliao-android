package com.yc.liaolive.mine.adapter;

import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import com.yc.liaolive.R;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.base.adapter.BaseViewHolder;
import com.yc.liaolive.bean.DiamondInfo;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * TinyHung@Outlook.com
 * 2019/1/25
 * 钻石详情-适配器
 */

public class DiamondDetailsAdapter extends BaseQuickAdapter<DiamondInfo, BaseViewHolder> {

    private final String mTypeId;

    public DiamondDetailsAdapter(@Nullable List<DiamondInfo> data, String typeId) {
        super(R.layout.diamond_detail_item, data);
        this.mTypeId=typeId;
    }

    @Override
    protected void convert(BaseViewHolder helper, DiamondInfo item) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
        //积分
        if(TextUtils.equals("3",mTypeId)){
            helper.setTextColor(R.id.item_create_count, ContextCompat.getColor(mContext, R.color.app_style));
            helper.setText(R.id.item_create_count, item.getPoints()>0?"+"+item.getPoints():""+item.getPoints());
        }else{
            //钻石
            helper.setTextColor(R.id.item_create_count,item.getCash() > 0 ?ContextCompat.getColor(mContext, R.color.app_style) : ContextCompat.getColor(mContext, R.color.colorContent));
            helper.setText(R.id.item_create_count,item.getCash() > 0 ? "+"+item.getCoin() : "-"+item.getCoin() );
        }
        helper.setText(R.id.item_name, item.getTitle()).setText(R.id.item_time, sdf.format(new Date(item.getAddtime() * 1000))).setText(R.id.item_nickname,item.getTo_nickname()+"(ID:"+item.getTo_userid()+")");
    }
}
