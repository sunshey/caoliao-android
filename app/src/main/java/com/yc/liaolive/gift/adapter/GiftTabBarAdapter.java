package com.yc.liaolive.gift.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import com.yc.liaolive.R;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.base.adapter.BaseViewHolder;
import com.yc.liaolive.interfaces.PerfectClickListener;
import com.yc.liaolive.live.bean.GiftTypeInfo;
import com.yc.liaolive.util.ScreenUtils;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/6/29
 * 礼物面板分类
 */

public class GiftTabBarAdapter extends BaseQuickAdapter<GiftTypeInfo,BaseViewHolder> {

    private final int mItemWidth;

    public GiftTabBarAdapter(@Nullable List<GiftTypeInfo> data) {
        super(R.layout.re_gift_tab_item, data);
        mItemWidth = (ScreenUtils.getScreenWidth()-ScreenUtils.dpToPxInt(20f))/4;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final GiftTypeInfo item) {
        helper.getView(R.id.ll_root_item).getLayoutParams().width=mItemWidth;
        helper.setText(R.id.item_tv_title,item.getTitle());
        TextView titleView =(TextView) helper.getView(R.id.item_tv_title);
        View itemUnderline = helper.getView(R.id.item_tv_underline);
        titleView.setSelected(item.isSelected()?true:false);
        itemUnderline.setVisibility(item.isSelected()? View.VISIBLE:View.INVISIBLE);
        //避免过快点击
        helper.getView(R.id.ll_root_item).setOnClickListener(new PerfectClickListener(200) {
            @Override
            protected void onNoDoubleClick(View v) {
                if(null!=mOnItemClickListener) mOnItemClickListener.onItemClick(helper.getAdapterPosition(),item.getId(),v);
            }
        });
    }

    public interface OnItemClickListener{
        void onItemClick(int position, int type, View view);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
