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
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ScreenUtils;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/9/13
 * 私密视频、照片
 */

public class PrivateMediaPhotoAdapter extends BaseMultiItemQuickAdapter<PrivateMedia,BaseViewHolder> {

    public static final int ITEM_TYPE_LIST=0;
    public static final int ITEM_TYPE_ADD=1;
    private final String mHomeUserID;//用户身份
    private final int mItemHeight;

    /**
     * @param data
     * @param homeUserID 作者ID
     */
    public PrivateMediaPhotoAdapter(List<PrivateMedia> data, String homeUserID) {
        super(data);
        addItemType(ITEM_TYPE_LIST,R.layout.recyler_private_media_list_photo_item);
        addItemType(ITEM_TYPE_ADD,R.layout.list_item_import_image_add);
        this.mHomeUserID=homeUserID;
        mItemHeight = (ScreenUtils.getScreenWidth()-ScreenUtils.dpToPxInt(32f))/3;
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
        //封面
        ImageView imageView = (ImageView) helper.getView(R.id.item_iv_icon);
        Glide.with(mContext)
                .load(item.getImg_path())
                .error(R.drawable.ic_default_item_cover)
                .placeholder(R.drawable.ic_default_item_cover)
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                .centerCrop()//中心点缩放
                .dontAnimate()
                .override(300, 300)
                .skipMemoryCache(false)
                .into(imageView);

        //是否正在审核中
        TextView mediaState = (TextView) helper.getView(R.id.item_media_state);
        //作者自己预览自己的
        if(TextUtils.equals(mHomeUserID, UserManager.getInstance().getUserId())){
            //文件状态
            if(0==helper.getAdapterPosition()){
                if(item.getState()>0){
                    mediaState.setText("封面");
                }else{
                    mediaState.setText(0==item.getState()?"审核中...":"审核未通过");
                }
                mediaState.setBackgroundResource(R.drawable.ic_media_look);
            }else{
                if(item.getState()>0){
                    mediaState.setText("");
                    mediaState.setBackgroundResource(0);
                }else{
                    mediaState.setText(0==item.getState()?"审核中...":"审核未通过");
                    mediaState.setBackgroundResource(R.drawable.ic_media_look);
                }
            }
            //观众端
        }else{
            //审核状态
            mediaState.setText("");
            mediaState.setVisibility(View.GONE);
            mediaState.setBackgroundResource(0);
        }
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
}