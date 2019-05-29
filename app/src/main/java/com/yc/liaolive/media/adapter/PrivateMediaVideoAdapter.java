package com.yc.liaolive.media.adapter;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.yc.liaolive.base.adapter.BaseMultiItemQuickAdapter;
import com.yc.liaolive.base.adapter.BaseViewHolder;
import com.yc.liaolive.bean.PrivateMedia;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.model.GlideCircleTransform;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ScreenUtils;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.util.Utils;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/9/13
 * 私密视频、照片
 */

public class PrivateMediaVideoAdapter extends BaseMultiItemQuickAdapter<PrivateMedia,BaseViewHolder> {

    public static final int ITEM_TYPE_LIST=0;
    public static final int ITEM_TYPE_ADD=1;
    private final String mHomeUserID;//用户身份
    private final int mItemHeight;
    private boolean isEdit;//是否处于编辑状态

    /**
     * 构造函数
     * @param data
     * @param homeUserID
     */
    public PrivateMediaVideoAdapter(List<PrivateMedia> data, String homeUserID) {
        super(data);
        addItemType(ITEM_TYPE_LIST,R.layout.recyler_private_media_list_video_item);
        addItemType(ITEM_TYPE_ADD,R.layout.list_item_import_image_add);
        this.mHomeUserID=homeUserID;
        //4:3比例显示
        mItemHeight = ((ScreenUtils.getScreenWidth()-ScreenUtils.dpToPxInt(6f))/2)/3*4;
    }

    /**
     * 条目全局更新
     * @param helper A fully initialized helper.
     * @param item   The item that needs to be displayed.
     */
    @Override
    protected void convert(final BaseViewHolder helper, final PrivateMedia item) {
        switch (item.getItemType()) {
            //普通条目
            case ITEM_TYPE_LIST:
                setItemList(helper,item);
                break;
            //添加按钮
            case ITEM_TYPE_ADD:
                setItemAdd(helper,item);
                break;
        }
    }

    /**
     * 单条局部、全部刷新
     * @param helper
     * @param item
     * @param payloads
     */
    @Override
    protected void convert(BaseViewHolder helper, PrivateMedia item, List<Object> payloads) {
        super.convert(helper, item, payloads);
        //局部的
        if(!payloads.isEmpty()){
            switch (item.getItemType()) {
                //普通条目
                case ITEM_TYPE_LIST:
                    //隐私、公开状态设置
                    TextView itemChangedState = (TextView) helper.getView(R.id.item_changed_state);
                    itemChangedState.setText(0==item.getIs_private()?"设为私密":"设为公开");
                    //访问权限状态
                    ImageView slantedTextView = (ImageView) helper.getView(R.id.item_media_tag);
                    slantedTextView.setVisibility(1 == item.getIs_private()? View.VISIBLE : View.INVISIBLE);
                    itemChangedState.setTag(item);
                    helper.itemView.setTag(item);
                    break;
                //添加按钮
                case ITEM_TYPE_ADD:

                    break;
            }
            //全局的
        }else{
            convert(helper,item);
        }
    }

    /**
     * 普通条目类型
     * @param helper
     * @param item
     */
    private void setItemList(final BaseViewHolder helper, PrivateMedia item) {
        helper.getView(R.id.item_root_view).getLayoutParams().height=mItemHeight;
        //用户信息
        View userDataView = helper.getView(R.id.item_user_data_view);
        //是否正在审核中
        TextView mediaState = (TextView) helper.getView(R.id.item_media_state);
        //访问权限状态
        ImageView slantedTextView = (ImageView) helper.getView(R.id.item_media_tag);
        //隐私、公开状态设置
        TextView itemChangedState = (TextView) helper.getView(R.id.item_changed_state);
        itemChangedState.setText(0==item.getIs_private()?"设为私密":"设为公开");
        slantedTextView.setVisibility(1 == item.getIs_private()? View.VISIBLE : View.INVISIBLE);
        //作者自己预览自己的
        if(TextUtils.equals(mHomeUserID, UserManager.getInstance().getUserId())){
            //编辑状态
            mediaState.setText(0==item.getState()?"审核中...":"");
            mediaState.setBackgroundResource(0==item.getState()?R.drawable.ic_media_look:0);
            //正在编辑中
            if(isEdit){
                userDataView.setVisibility(View.GONE);
                if(0==item.getState()){
                    itemChangedState.setVisibility(View.GONE);
                }else{
                    itemChangedState.setVisibility(View.VISIBLE);
                }
            }else{
                if(0==item.getState()){
                    userDataView.setVisibility(View.GONE);
                }else{
                    userDataView.setVisibility(View.VISIBLE);
                }
                itemChangedState.setVisibility(View.GONE);
            }
            mediaState.setVisibility(0==item.getState()?View.VISIBLE:View.GONE);
        //观众端
        }else{
            //审核状态
            mediaState.setText("");
            mediaState.setVisibility(View.GONE);
            //编辑状态
            itemChangedState.setVisibility(View.GONE);
            //用户信息
            userDataView.setVisibility(View.VISIBLE);
        }
        ImageView btnDelete = (ImageView) helper.getView(R.id.ic_item_delete);
        btnDelete.setVisibility(isEdit? View.VISIBLE:View.GONE);
        btnDelete.setImageResource(isEdit?R.drawable.ic_private_media_detele:0);
        btnDelete.setTag(item);
        //删除
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getData().size() > 1) {
                    if(null!=mOnMediaStateListener) mOnMediaStateListener.onDeleteMedia((PrivateMedia) v.getTag(),helper.getAdapterPosition());
                } else {
                    ToastUtils.showCenterToast("该视频不可删除");
                }
            }
        });

        //基本信息
        helper.setText(R.id.item_user_name,item.getNickname())
                .setText(R.id.item_look_num, Utils.formatWan(item.getBrowse_number(),true))
                .setText(R.id.item_title_desp,0==item.getFile_type()?item.getSignature():item.getVideo_desp());

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
                .into(((ImageView) helper.getView(R.id.item_user_avater)));
        //封面
        ImageView imageView = (ImageView) helper.getView(R.id.item_iv_icon);
        Glide.with(mContext)
                .load(item.getImg_path())
                .error(R.drawable.ic_default_item_cover)
                .placeholder(R.drawable.ic_default_item_cover)
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                .centerCrop()//中心点缩放
                .dontAnimate()
                .skipMemoryCache(false)
                .into(imageView);
        //状态改变
        itemChangedState.setTag(item);
        itemChangedState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=mOnMediaStateListener) mOnMediaStateListener.onChangedPrivateState((PrivateMedia) v.getTag(),helper.getAdapterPosition());
            }
        });
        helper.itemView.setTag(item);
    }

    /**
     * 添加按钮类型
     * @param helper
     * @param item
     */
    private void setItemAdd(BaseViewHolder helper, PrivateMedia item) {
        int itemHeight = (ScreenUtils.getScreenWidth()-ScreenUtils.dpToPxInt(9f))/2;
        helper.getView(R.id.item_root_view).getLayoutParams().height=itemHeight;
        ImageView iconImage = (ImageView) helper.getView(R.id.item_add_icon);
        iconImage.setImageResource(item.getIcon());
        iconImage.setColorFilter(Color.parseColor("#FF555555"));
        helper.setText(R.id.item_add_title,item.getAction());
        helper.itemView.setTag(item);
    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }

    /**
     * 切换编辑模式
     */
    public void changedEditMode() {
        isEdit=!isEdit;
        notifyDataSetChanged();
    }


    /**
     * 多媒体文件状态监听
     */
    public interface OnMediaStateListener{
        void onDeleteMedia(PrivateMedia mediaInfo,int position);
        void onChangedPrivateState(PrivateMedia mediaInfo,int position);
    }

    private OnMediaStateListener mOnMediaStateListener;

    public void setOnMediaStateListener(OnMediaStateListener onMediaStateListener) {
        mOnMediaStateListener = onMediaStateListener;
    }
}