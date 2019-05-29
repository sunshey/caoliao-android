package com.yc.liaolive.gift.adapter;

import android.support.annotation.Nullable;
import android.widget.TextView;
import com.yc.liaolive.R;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.base.adapter.BaseViewHolder;
import com.yc.liaolive.live.bean.GiftLevelInfo;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/6/8
 * 礼物选择面板的档次选择列表适配器
 */

public class GiftLevelAdapter extends BaseQuickAdapter<GiftLevelInfo,BaseViewHolder> {

    public GiftLevelAdapter( @Nullable List<GiftLevelInfo> data) {
        super(R.layout.re_gift_lever_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, GiftLevelInfo item) {
        TextView textView = (TextView) helper.getView(R.id.tv_item_count);
        textView.setText(String.valueOf(item.getCount()));
        textView.setSelected(item.isSeleted());
    }

    /**
     * 所有选中状态还原为未选中
     */
    public void recover() {
        for (GiftLevelInfo giftLevelInfo : getData()) {
            giftLevelInfo.setSeleted(false);
        }
        this.notifyDataSetChanged();
    }
}
