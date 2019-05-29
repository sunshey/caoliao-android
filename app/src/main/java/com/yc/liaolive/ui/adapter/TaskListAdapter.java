package com.yc.liaolive.ui.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.base.adapter.BaseViewHolder;
import com.yc.liaolive.bean.TaskInfo;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/7/6
 * 任务礼包适配器
 */

public class TaskListAdapter extends BaseQuickAdapter<TaskInfo,BaseViewHolder> {

    public TaskListAdapter( @Nullable List<TaskInfo> data) {
        super(R.layout.re_item_task_list, data);
    }

    @Override
    protected void convert(final BaseViewHolder helper, TaskInfo item) {
                helper.setText(R.id.item_tv_title,item.getName())
                .setText(R.id.item_tv_desp,item.getDesp());
        TextView stateView = (TextView) helper.getView(R.id.item_state);
//        helper.setImageResource(R.id.item_iv_icon,item.getIcon());
        Glide.with(mContext)
                .load(item.getSrc())
                .error(R.drawable.ic_task_vip)
                .crossFade()//渐变
                .animate(R.anim.item_alpha_in)//加载中动画
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                .skipMemoryCache(true)//跳过内存缓存
                .into((ImageView) helper.getView(R.id.item_iv_icon));
        stateView.setText(1==item.getComplete()?"已完成":1==item.getIs_get()?"去完成":"领取");
        stateView.setBackgroundResource(1==item.getComplete()?R.drawable.bt_bg_app_gray_radius_noimal:1==item.getIs_get()?R.drawable.bt_bg_app_style_radius_selector:R.drawable.bt_bg_app_style_radius_selector);
        stateView.setTag(item);
        stateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=mOnGiftChangedListener) mOnGiftChangedListener.onDraw(v,helper.getAdapterPosition(),(TaskInfo) v.getTag());
            }
        });
    }

    public interface OnGiftChangedListener{
        void onDraw(View view,int pisition,TaskInfo data);
    }

    private OnGiftChangedListener mOnGiftChangedListener;

    public void setOnGiftChangedListener(OnGiftChangedListener onGiftChangedListener) {
        mOnGiftChangedListener = onGiftChangedListener;
    }
}
