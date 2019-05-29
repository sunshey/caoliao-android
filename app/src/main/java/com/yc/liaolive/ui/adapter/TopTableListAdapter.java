package com.yc.liaolive.ui.adapter;

import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.base.adapter.BaseViewHolder;
import com.yc.liaolive.bean.FansInfo;
import com.yc.liaolive.live.mode.UserModelUtil;
import com.yc.liaolive.live.util.LiveUtils;
import com.yc.liaolive.model.GlideCircleTransform;
import java.util.List;
import java.util.Locale;

/**
 * @time 2018/6/12
 * @des 用户下积分排行榜
 */

public class TopTableListAdapter extends BaseQuickAdapter<FansInfo,BaseViewHolder> {

    private final int mType;

    public TopTableListAdapter(List<FansInfo> listBeanList, int type) {
        super(R.layout.re_attach_top_list_item,listBeanList);
        this.mType=type;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final FansInfo item) {
        if(null!=item){
            TextView itemTvNum = (TextView) helper.getView(R.id.item_tv_num);
            itemTvNum.setText(String.valueOf(helper.getAdapterPosition()+3));
            helper.setText(R.id.tv_item_content,item.getNickname()).setText(R.id.item_tv_total_points,0==mType?String.format(Locale.CHINA, "%d亲密度", item.getTotal_points()):String.format(Locale.CHINA, "%d亲密度", item.getDay_points()));
            ImageView item_user_gradle = (ImageView) helper.getView(R.id.item_user_gradle);
            ImageView item_vip_gradle = (ImageView) helper.getView(R.id.item_vip_gradle);
            ImageView item_user_sex = (ImageView) helper.getView(R.id.item_user_sex);

            LiveUtils.setUserSex(item_user_sex,item.getSex());
            LiveUtils.setUserGradle(item_user_gradle,item.getLevel_integral());
//            LiveUtils.setUserVipGradle(item_vip_gradle,item.getVip());
            LiveUtils.setUserBlockVipGradle(item_vip_gradle,item.getVip());
            //等级
            item_user_gradle.setImageResource(UserModelUtil.getUserGradleRes(item.getLevel_integral()));
            //会员等级
//            item_vip_gradle.setImageResource(UserModelUtil.getUserVipGradleRes(item.getVip()));

            Glide.with(mContext)
                    .load(item.getAvatar())
                    .error(R.drawable.ic_default_user_head)
                    .crossFade()//渐变
                    .animate(R.anim.item_alpha_in)//加载中动画
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)//缓存源资源和转换后的资源
                    .centerCrop()//中心点缩放
                    .skipMemoryCache(true)//跳过内存缓存
                    .transform(new GlideCircleTransform(mContext))
                    .into((ImageView) helper.getView(R.id.item_ic_icon));
        }
    }
}
