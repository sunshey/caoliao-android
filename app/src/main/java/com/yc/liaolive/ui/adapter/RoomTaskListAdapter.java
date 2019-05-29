package com.yc.liaolive.ui.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.base.adapter.BaseViewHolder;
import com.yc.liaolive.bean.TaskInfo;
import com.yc.liaolive.view.widget.TaskProgressBar;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/7/6
 * 任务礼包适配器
 */

public class RoomTaskListAdapter extends BaseQuickAdapter<TaskInfo,BaseViewHolder> {

    public RoomTaskListAdapter(@Nullable List<TaskInfo> data) {
        super(R.layout.re_item_room_task_list, data);
    }

    @Override
    protected void convert(final BaseViewHolder helper, TaskInfo item) {
        if(null==item) return;
        try {
            //分类处理
            View view = helper.getView(R.id.item_header_view);
            if(0==helper.getAdapterPosition()){
                view.setVisibility(View.VISIBLE);
                helper.getView(R.id.item_group_line).setVisibility(View.GONE);//第一个条目隐藏顶部线条
            }else if(item.getType().equals(getData().get(helper.getAdapterPosition()-1).getType())){
                view.setVisibility(View.GONE);
            }else{
                view.setVisibility(View.VISIBLE);
                helper.getView(R.id.item_group_line).setVisibility(View.VISIBLE);//其他的显示顶部线条
            }
            //如果是统一分类下的最后一个，隐藏分割线
            View itemLine = helper.getView(R.id.view_line);
            itemLine.setVisibility(item.isLastPosition()?View.GONE:View.VISIBLE);
            //分类标题
            helper.setText(R.id.tv_item_title,item.getType())
                    .setText(R.id.item_tv_title,item.getName())//任务标题
                    .setText(R.id.item_tv_desp,item.getDesp());//任务说明
            final TextView stateView = (TextView) helper.getView(R.id.item_state);
            //作者封面
            Glide.with(mContext)
                    .load(item.getSrc())
                    .error(R.drawable.ic_video_live)
                    .crossFade()//渐变
                    .animate(R.anim.item_alpha_in)//加载中动画
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                    .skipMemoryCache(true)//跳过内存缓存
                    .into((ImageView) helper.getView(R.id.item_iv_icon));

            stateView.setText(TextUtils.isEmpty(item.getBut_title())?getButtonText(item.getApp_id()):item.getBut_title());
            TaskProgressBar progressBar = (TaskProgressBar) helper.getView(R.id.view_task_progress);
            int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            progressBar.setMovesDuration(item.getCount_num()>10?1000:200);
            progressBar.measure(width,width);
            progressBar.setGroupWidth(progressBar.getMeasuredWidth());
            progressBar.setGroupHeight(progressBar.getMeasuredHeight());
            progressBar.setMaxProgress(item.getCount_num());
            progressBar.setTextIsMoves(true);
            //如果是已完成
            if(1==item.getComplete()){
                item.setCurrent_num(item.getCount_num());
            }
            //任务待领取，统一更换文字
            if(0==item.getIs_get()&&TextUtils.isEmpty(item.getBut_title())){
                stateView.setText("领取");
            }
            stateView.setBackgroundResource(0==item.getIs_get()?R.drawable.bt_bg_app_style_radius_noimal:R.drawable.bt_bg_app_orgin_radius_noimal);
            progressBar.setProgress(item.getCurrent_num());
            View stateClick = helper.getView(R.id.item_state_click);
            stateClick.setTag(item);
            stateClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(null!=mOnGiftChangedListener) mOnGiftChangedListener.onDraw(stateView,helper.getAdapterPosition(),(TaskInfo) v.getTag());
                }
            });
        }catch (RuntimeException e) {

        }
    }

    private String getButtonText(int appID) {
        String content="去完成";
        switch (appID) {
            case TaskInfo.TASK_ACTION_MODIFY_NAME:
                content="去修改";
                break;
            case TaskInfo.TASK_ACTION_BIND_PHONE:
            case TaskInfo.TASK_ACTION_BIND_QQ:
            case TaskInfo.TASK_ACTION_BIND_WEXIN:
                content="去绑定";
                break;
            case TaskInfo.TASK_ACTION_LOOK_LIVE:
                content="去观看";
                break;
            case TaskInfo.TASK_ACTION_SEND_GIFT:
                content="去完成";
                break;
            case TaskInfo.TASK_ACTION_SHARE:
                content="去分享";
                break;
        }
        return content;
    }

    public interface OnGiftChangedListener{
        void onDraw(View view,int pisition, TaskInfo data);
    }

    private OnGiftChangedListener mOnGiftChangedListener;

    public void setOnGiftChangedListener(OnGiftChangedListener onGiftChangedListener) {
        mOnGiftChangedListener = onGiftChangedListener;
    }
}
