package com.yc.liaolive.recharge.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import com.yc.liaolive.R;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.base.adapter.BaseViewHolder;
import com.yc.liaolive.bean.TaskInfo;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/7/6
 * 充值奖励
 */

public class RechargeAwardListAdapter extends BaseQuickAdapter<TaskInfo,BaseViewHolder> {

    public RechargeAwardListAdapter(@Nullable List<TaskInfo> data) {
        super(R.layout.re_item_recharge_award_list, data);
    }

    @Override
    protected void convert(final BaseViewHolder helper, TaskInfo item) {
                helper.setText(R.id.item_tv_title,item.getName())
                .setText(R.id.item_tv_desp,item.getDesp());
        TextView stateView = (TextView) helper.getView(R.id.item_state);
        stateView.setText("领取");
        stateView.setBackgroundResource(R.drawable.bt_bg_app_style_radius_selector);
        stateView.setTag(item);
        stateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=mOnGiftChangedListener) mOnGiftChangedListener.onDraw(helper.getAdapterPosition(),(TaskInfo) v.getTag());
            }
        });
    }

    public interface OnGiftChangedListener{
        void onDraw(int pisition, TaskInfo data);
    }

    private OnGiftChangedListener mOnGiftChangedListener;

    public void setOnGiftChangedListener(OnGiftChangedListener onGiftChangedListener) {
        mOnGiftChangedListener = onGiftChangedListener;
    }
}
