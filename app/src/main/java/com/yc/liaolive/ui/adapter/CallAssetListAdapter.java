package com.yc.liaolive.ui.adapter;

import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.yc.liaolive.base.adapter.BaseMultiItemQuickAdapter;
import com.yc.liaolive.base.adapter.BaseViewHolder;
import com.yc.liaolive.bean.DiamondInfo;
import com.yc.liaolive.model.GlideCircleTransform;
import com.yc.liaolive.util.DateUtil;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/10/16
 * 我的 钻石、积分 记录
 */

public class CallAssetListAdapter extends BaseMultiItemQuickAdapter<DiamondInfo,BaseViewHolder> {

    public static final int ITEM_TYPE_DIAMOND=2;//钻石记录
    public static final int ITEM_TYPE_INTEGRAL=3;//积分记录
    private final String mIdType;

    public CallAssetListAdapter(@Nullable List<DiamondInfo> data, String typeID) {
        super(data);
        this.mIdType=typeID;
        addItemType(ITEM_TYPE_DIAMOND,R.layout.re_call_diamond_list_item);
        addItemType(ITEM_TYPE_INTEGRAL,R.layout.re_call_diamond_list_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, DiamondInfo item) {
        switch (item.getItemType()) {
            case ITEM_TYPE_DIAMOND:
                setItemDataDiamond(helper,item);
                break;
            case ITEM_TYPE_INTEGRAL:
                setItemDataDiamond(helper,item);
                break;
        }
    }

    /**
     * 积分、钻石记录
     * @param helper
     * @param item
     */
    private void setItemDataDiamond(BaseViewHolder helper, DiamondInfo item) {
        if(null!=item){
            long millis=item.getAddtime()*1000;
            Spanned spanned;
            //积分
            if(TextUtils.equals("3",mIdType)){
                spanned = Html.fromHtml(item.getPoints() > 0 ? "收入金额："+item.getPoints() : "支出金额：" +item.getPoints() );
            //钻石
            }else{
                spanned = Html.fromHtml(item.getCash() > 0 ? "收入金额："+item.getCoin() : "支出金额：" +item.getCoin() );
            }
            helper.setText(R.id.item_name,item.getTitle())
                    .setText(R.id.item_price, spanned)
                    .setText(R.id.item_time_minute, DateUtil.timeFormatDay(millis)+" "+DateUtil.timeFormatMinute(millis));//月:日 时:分
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
            View userView = helper.getView(R.id.item_ll_user_item);
            userView.setTag(item);
            helper.itemView.setTag(item);
        }
    }
}
