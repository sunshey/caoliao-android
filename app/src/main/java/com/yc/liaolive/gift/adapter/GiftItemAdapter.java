package com.yc.liaolive.gift.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.opensource.svgaplayer.SVGAImageView;
import com.yc.liaolive.R;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.gift.manager.GiftHelpManager;
import com.yc.liaolive.live.bean.GiftInfo;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.util.ScreenUtils;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/5/14.
 * 礼物选择面板适配器
 */

public class GiftItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final LayoutInflater mInflater;
    private List<GiftInfo> mData;
    private final int mItemHeight;
    private final int mIconWidth;

    public GiftItemAdapter(List<GiftInfo> data, Context context) {
        this.mData=data;
        int screenWidth = ScreenUtils.getScreenWidth();
        mInflater = LayoutInflater.from(context);
        mItemHeight = screenWidth/4;
        mIconWidth = mItemHeight - ScreenUtils.dpToPxInt(40f);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.re_gift_item_layout,null));
    }

    /**
     * 单条目全部刷新
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        GiftInfo giftInfo = mData.get(position);
        if(null!=giftInfo){
            ViewHolder viewHolder= (ViewHolder) holder;
            viewHolder.item_view_group.getLayoutParams().height=mItemHeight;
            //ICON
            ViewGroup.LayoutParams layoutParams = viewHolder.ic_item_icon.getLayoutParams();
            layoutParams.height=mIconWidth;
            layoutParams.width=mIconWidth;
            viewHolder.ic_item_icon.setLayoutParams(layoutParams);
            //SVGA
            RelativeLayout.LayoutParams layoutParamsSvga = (RelativeLayout.LayoutParams) viewHolder.view_svga_icon.getLayoutParams();
            layoutParamsSvga.height=mIconWidth;
            layoutParamsSvga.width=mIconWidth;
            viewHolder.view_svga_icon.setLayoutParams(layoutParamsSvga);
            //静止的
            RelativeLayout.LayoutParams layoutParamsIcon = (RelativeLayout.LayoutParams) viewHolder.item_selected_icon.getLayoutParams();
            layoutParamsIcon.height=mIconWidth;
            layoutParamsIcon.width=mIconWidth;
            viewHolder.item_selected_icon.setLayoutParams(layoutParamsIcon);

            viewHolder.tv_item_title.setText(giftInfo.getTitle());
            viewHolder.item_tv_price.setText(String.valueOf(giftInfo.getPrice()));
            //优先现实分类，其次显示TAG
            //标签处理
            if(null!=giftInfo.getTag()&&giftInfo.getTag().length()>0){
                viewHolder.item_tag.setVisibility(View.VISIBLE);
                if(Constant.STRING_TAG_NEW.equals(giftInfo.getTag())){
                    viewHolder.item_tag.setText("");
                    viewHolder.item_tag.setBackgroundResource(R.drawable.ic_gift_new);
                }else{
                    viewHolder.item_tag.setBackgroundResource(R.drawable.bg_gift_tag_shape);
                    viewHolder.item_tag.setText(giftInfo.getTag());
                }
            }else{
                viewHolder.item_tag.setVisibility(View.GONE);
            }
            //普通的ICON设置
            Glide.with(viewHolder.ic_item_icon.getContext())
                    .load(giftInfo.getSrc())
                    .centerCrop()
                    .skipMemoryCache(true)//跳过内存缓存
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(viewHolder.ic_item_icon);
            viewHolder.itemView.setTag(giftInfo);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(null!=mOnItemClickListener){
                        mOnItemClickListener.onItemClick(position,v, (GiftInfo) v.getTag());
                    }
                }
            });
            //记录需要恢复的项
            if(GiftHelpManager.getInstance().isExitRecoveryState()&&GiftHelpManager.getInstance().getOldGiftInfo().getId()==giftInfo.getId()){
                giftInfo.setSelector(true);
                GiftHelpManager.getInstance().setOldItemView(viewHolder.itemView);
                ApplicationManager.getInstance().observerUpdata(Constant.OBSERVER_GIFT_RECOVERY_ADAPTER_INIT);
            }
        }
    }

    @Override
    public int getItemCount() {
        return null==mData?0:mData.size();
    }

    public List<GiftInfo> getData() {
        return mData;
    }

    /**
     * 为适配器设置新的数据
     * @param data
     */
    public synchronized void setNewData(List<GiftInfo> data) {
        mData=data;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView item_selected_icon;
        private SVGAImageView view_svga_icon;
        private View item_view_group;
        private ImageView ic_item_icon;
        private TextView tv_item_title;
        private TextView item_tv_price;
        private TextView item_tag;

        public ViewHolder(View itemView) {
            super(itemView);
            item_selected_icon=itemView.findViewById(R.id.item_selected_icon);
            view_svga_icon=itemView.findViewById(R.id.view_svga_icon);
            item_view_group=itemView.findViewById(R.id.item_view_group);
            ic_item_icon=itemView.findViewById(R.id.ic_item_icon);
            tv_item_title=itemView.findViewById(R.id.tv_item_title);
            item_tv_price=itemView.findViewById(R.id.item_tv_price);
            item_tag=itemView.findViewById(R.id.item_tag);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int poistion,View view,GiftInfo giftInfo);
    }
    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
