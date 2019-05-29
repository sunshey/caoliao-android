package com.yc.liaolive.index.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.base.adapter.BaseViewHolder;
import com.yc.liaolive.bean.UserInfo;
import com.yc.liaolive.interfaces.PerfectClickListener;
import com.yc.liaolive.interfaces.PerfectClickViewListener;
import com.yc.liaolive.live.util.LiveUtils;
import com.yc.liaolive.model.GlideCircleTransform;
import com.yc.liaolive.view.widget.TouchFilterImageView;
import java.util.List;
import java.util.Locale;

/**
 * TinyHung@Outlook.com
 * 2017/12/14.
 * 附近的人列表
 */

public class IndexPeopleListAdapter extends BaseQuickAdapter<UserInfo,BaseViewHolder> {

    public IndexPeopleListAdapter(List<UserInfo> data) {
        super(R.layout.recyler_live_index_people_item, data);
    }

    @Override
    protected void convert(final BaseViewHolder helper, final UserInfo item) {
        if(null==item) return;
        helper.setText(R.id.item_tv_user_name,item.getNickname())
                .setText(R.id.item_tv_user_singer, TextUtils.isEmpty(item.getSignature())?"该宝宝暂无个性签名":item.getSignature())
                .setText(R.id.item_tv_location,item.getCity())
                .setText(R.id.tv_live_price,"("+String.format(Locale.CHINA,"%d钻石",item.getChat_deplete())+String.format(Locale.CHINA,"/%d分钟",item.getChat_minute())+")");
        //设置用户性别
        LiveUtils.setUserSex(((ImageView) helper.getView(R.id.item_iv_user_sex)),item.getSex());

        View view = helper.getView(R.id.user_icon_view);
        view.setTag(item.getUserid());
        view.setOnClickListener(new PerfectClickViewListener() {
            @Override
            protected void onClickView(View view) {
                if(null!=mOnItemCallClickListener)mOnItemCallClickListener.onUserClick((String) view.getTag());
            }
        });
        helper.itemView.setTag(item);
        helper.itemView.setOnClickListener(new PerfectClickListener(500) {
            @Override
            protected void onNoDoubleClick(View v) {
                if(null!=mOnItemCallClickListener)mOnItemCallClickListener.onMakeCall((UserInfo) v.getTag());
            }
        });
        //作者封面
        Glide.with(mContext)
                .load(item.getAvatar())
                .error(R.drawable.ic_default_user_head)
                .crossFade()//渐变
                .animate(R.anim.item_alpha_in)//加载中动画
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                .centerCrop()//中心点缩放
                .skipMemoryCache(true)//跳过内存缓存
                .transform(new GlideCircleTransform(mContext))
                .into((TouchFilterImageView) helper.getView(R.id.item_iv_user_icon));
    }

    public interface OnItemCallClickListener{
        void onUserClick(String userID);
        void onMakeCall(UserInfo userInfo);
    }

    private OnItemCallClickListener mOnItemCallClickListener;

    public void setOnItemCallClickListener(OnItemCallClickListener onItemCallClickListener) {
        mOnItemCallClickListener = onItemCallClickListener;
    }
}
