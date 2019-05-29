package com.yc.liaolive.media.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.yc.liaolive.bean.PrivateMedia;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ScreenUtils;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/9/13
 * 私密视频、照片
 * 适用于用户中心，不允许编辑,最多显示4个
 */

public class PrivateMinMediaAdapter extends BaseAdapter {

    private static final String TAG = "PrivateMinMediaAdapter";
    private final List<PrivateMedia> mData;
    private final Context mContext;
    private int mItemType;//列表类型 0：图片 1：视频
    private final int mItemHeight;
    private final LayoutInflater mInflater;


    /**
     * @param data
     * @param itemType 列表类型 0：图片 1：视频
     */
    public PrivateMinMediaAdapter(android.content.Context context,List<PrivateMedia> data, int itemType) {
        this.mData=data;
        this.mContext=context;
        mInflater = LayoutInflater.from(context);
        this.mItemType=itemType;
        mItemHeight =(ScreenUtils.getScreenWidth()-ScreenUtils.dpToPxInt(62f))/4;
    }

    @Override
    public int getCount() {
        return null==mData?0:mData.size()>4?4:mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null==mData?null:mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(null==convertView) {
            convertView=mInflater.inflate(R.layout.recyler_private_media_min_item,null);
            viewHolder=new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }

        ViewGroup.LayoutParams layoutParams = viewHolder.coord_root_view.getLayoutParams();
        layoutParams.width= ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height=mItemHeight;
        viewHolder.coord_root_view.setLayoutParams(layoutParams);

        PrivateMedia privateMedia = mData.get(position);
        if(null!=privateMedia){
            if(mItemType==Constant.MEDIA_TYPE_VIDEO){
                viewHolder.ic_item_video_player.setImageResource(R.drawable.ic_person_private_video);
            }else{
                viewHolder.ic_item_video_player.setImageResource(0);
            }
//            viewHolder.tv_item_durtion.setText(mItemType== Constant.MEDIA_TYPE_VIDEO? DateUtil.minuteFormat(privateMedia.getVideo_durtion()):"");
            viewHolder.tv_item_durtion.setText("");
            Glide.with(mContext)
                    .load(privateMedia.getImg_path())
                    .placeholder(R.drawable.ic_default_live_min_icon)
                    .error(R.drawable.ic_default_live_min_icon)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                    .centerCrop()//中心点缩放
                    .dontAnimate()
                    .skipMemoryCache(true)//跳过内存缓存
                    .into(viewHolder.item_iv_icon);
        }
        return convertView;
    }

    private class ViewHolder{
        private View coord_root_view;
        private ImageView ic_item_video_player;
        private TextView tv_item_durtion;
        private ImageView item_iv_icon;

        public ViewHolder(View convertView) {
            coord_root_view=convertView.findViewById(R.id.coord_root_view);
            ic_item_video_player=convertView.findViewById(R.id.ic_item_video_player);
            tv_item_durtion=convertView.findViewById(R.id.tv_item_durtion);
            item_iv_icon=convertView.findViewById(R.id.item_iv_icon);
        }
    }
}
