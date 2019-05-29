package com.yc.liaolive.media.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.yc.liaolive.base.adapter.BaseMultiItemQuickAdapter;
import com.yc.liaolive.base.adapter.BaseViewHolder;
import com.yc.liaolive.bean.PrivateMedia;
import com.yc.liaolive.index.adapter.IndexVideoListAdapter;
import com.yc.liaolive.util.ScreenUtils;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/10/12
 * 用户头像相册,支持编辑等操作能力
 */

public class UserHeadAdapter extends BaseMultiItemQuickAdapter<PrivateMedia,BaseViewHolder> {

    private final int mItemHeight;//ITEM高度


    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public UserHeadAdapter(List<PrivateMedia> data) {
        super(data);
        addItemType(IndexVideoListAdapter.ITEM_TYPE_VIDEO, R.layout.recyler_user_head_list_layout);
        addItemType(IndexVideoListAdapter.ITEM_TYPE_ADD,R.layout.list_item_head_image_add);
        mItemHeight = (ScreenUtils.getScreenWidth()-ScreenUtils.dpToPxInt(30f))/4;
    }


    public int getItemHeight() {
        return mItemHeight;
    }

    @Override
    protected void convert(BaseViewHolder helper, PrivateMedia item) {
        helper.getView(R.id.item_root_view).getLayoutParams().height=mItemHeight;
        switch (item.getItemType()) {
            //普通条目
            case IndexVideoListAdapter.ITEM_TYPE_VIDEO:
                setItemList(helper,item);
                break;
            //添加按钮
            case IndexVideoListAdapter.ITEM_TYPE_ADD:
                setItemAdd(helper,item);
                break;
        }
    }

    /**
     * 普通条目类型
     * @param helper
     * @param item
     */
    private void setItemList(final BaseViewHolder helper, PrivateMedia item) {
        //删除
//        ImageView btnDelete = (ImageView) helper.getView(R.id.ic_item_delete);
//        btnDelete.setTag(item);
//
//        btnDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(null!=mHeadFunctionListener) mHeadFunctionListener.onDeltte((PrivateMedia) v.getTag(),helper.getAdapterPosition());
//            }
//        });
        //封面
        ImageView imageView = (ImageView) helper.getView(R.id.item_iv_icon);
        Glide.with(mContext)
                .load(item.getImg_path())
                .crossFade()//渐变
                .error(R.drawable.ic_default_item_cover)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                .centerCrop()//中心点缩放
                .skipMemoryCache(false)
                .into(imageView);

        //是否正在审核中,如果正在审核中，禁止设为头像，反之相反
        TextView mediaState = (TextView) helper.getView(R.id.item_media_state);
        if(0==helper.getAdapterPosition()){
            mediaState.setVisibility(View.VISIBLE);
            //是否正在审核中
            if(0==item.getState()){
                mediaState.setText("审核中...");
            }else{
                mediaState.setText("封面");
            }
        }else{
            //是否正在审核中
            if(0==item.getState()){
                mediaState.setVisibility(View.VISIBLE);
                mediaState.setText("审核中...");
            }else{
                mediaState.setVisibility(View.GONE);
                mediaState.setText("");
            }
        }
        helper.itemView.setTag(item);
    }

    /**
     * 添加按钮类型
     * @param helper
     * @param item
     */
    private void setItemAdd(BaseViewHolder helper, PrivateMedia item) {
        ImageView iconImage = (ImageView) helper.getView(R.id.item_add_icon);
        iconImage.setImageResource(item.getIcon());
        helper.itemView.setTag(item);
    }

    public interface OnHeadFunctionListener{
        void onDeltte(PrivateMedia mediaInfo,int position);
        void onSetHeadToUser(PrivateMedia mediaInfo,int position);
    }

    private OnHeadFunctionListener mHeadFunctionListener;

    public void setHeadFunctionListener(OnHeadFunctionListener headFunctionListener) {
        mHeadFunctionListener = headFunctionListener;
    }
}
