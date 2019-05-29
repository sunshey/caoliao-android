package com.yc.liaolive.search.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.base.adapter.BaseViewHolder;
import com.yc.liaolive.bean.UserInfo;
import com.yc.liaolive.interfaces.AttachFirendCliskListener;
import com.yc.liaolive.model.GlideCircleTransform;
import com.yc.liaolive.view.widget.TouchFilterImageView;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

/**
 * @time 2018/6/12
 * @des 搜索结果适配器
 */

public class SearchResultListAdapter extends BaseQuickAdapter<UserInfo,BaseViewHolder> {

    private final AttachFirendCliskListener mCliskListener;

    public SearchResultListAdapter(List<UserInfo> listBeanList, AttachFirendCliskListener attachFirendCliskListener) {
        super(R.layout.re_attach_firend_list_item,listBeanList);
        this.mCliskListener = attachFirendCliskListener;
    }


    @Override
    protected void convert(final BaseViewHolder helper, final UserInfo item) {
        try {
            if(null!=item){
                try {
                    String decode = URLDecoder.decode(TextUtils.isEmpty(item.getSignature())?"对方还没有设置个性签名":item.getSignature().replaceAll("%","25%"), "UTF-8");
                    String nickName = URLDecoder.decode(TextUtils.isEmpty(item.getNickname())?"":item.getNickname().replaceAll("%","25%"), "UTF-8");
                    helper.setText(R.id.item_tv_title,nickName);
                    helper.setText(R.id.item_tv_desp,decode);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                TextView userState = (TextView) helper.getView(R.id.item_user_state);
                userState.setVisibility(View.GONE);
                userState.setTag(item.getUserid());
                userState.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                //用户头像
                Glide.with(mContext)
                        .load(item.getAvatar())
                        .error(R.drawable.ic_default_user_head)
                        .placeholder(R.drawable.ic_default_user_head)
                        .crossFade()//渐变
                        .animate(R.anim.item_alpha_in)//加载中动画
                        .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                        .centerCrop()//中心点缩放
                        .skipMemoryCache(true)//跳过内存缓存
                        .transform(new GlideCircleTransform(mContext))
                        .into((TouchFilterImageView) helper.getView(R.id.item_user_icon));

                //头像点击处理
                View userIconView = helper.getView(R.id.re_user_icon);
                userIconView.setTag(item.getUserid());
                userIconView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(null!= mCliskListener) mCliskListener.onUserHeadClick(item.getUserid(),v);
                    }
                });
                //条目点击监听
                View rootItem = helper.getView(R.id.re_root_item);
                rootItem.setTag(item.getUserid());
                rootItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(null!= mCliskListener) mCliskListener.onItemClick(helper.getAdapterPosition(), ((String) v.getTag()),v);
                    }
                });
            }
        }catch (Exception e){

        }
    }
}