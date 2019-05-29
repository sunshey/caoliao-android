package com.yc.liaolive.music.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.music.player.lib.listener.MusicOnItemClickListener;
import com.music.player.lib.util.Logger;
import com.music.player.lib.util.MusicUtils;
import com.music.player.lib.view.MusicRoundImageView;
import com.yc.liaolive.R;
import com.yc.liaolive.music.bean.MediaInfo;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2019/3/7
 */

public class IndexMusicListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "IndexMusicListAdapter";
    private List<MediaInfo> mData;
    private Context mContext;
    private LayoutInflater mInflater;
    private MusicOnItemClickListener mListener;
    private int mItemWidth;

    public IndexMusicListAdapter(Context context, List<MediaInfo> musicList, MusicOnItemClickListener listener) {
        this.mContext=context;
        this.mData=musicList;
        this.mListener=listener;
        mInflater = LayoutInflater.from(context);
        int screenWidth = MusicUtils.getInstance().getScreenWidth(context);
        mItemWidth = (screenWidth - MusicUtils.getInstance().dpToPxInt(context, 56f)) /3;
        Logger.d(TAG,"mItemWidth:"+mItemWidth);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = mInflater.inflate(R.layout.music_item_index_music_list, null);
        return new MusicHolderView(inflate);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(null!=mData){
            MusicHolderView holderView= (MusicHolderView) holder;
            MediaInfo mediaInfo = mData.get(position);
            holderView.textTitle.setText(mediaInfo.getVideo_desp());
            holderView.textAnchor.setText(mediaInfo.getNickname());
            if(!TextUtils.isEmpty(mediaInfo.getImg_path())){
                Glide.with(mContext)
                        .load(mediaInfo.getImg_path())
                        .asBitmap()
                        .placeholder(R.drawable.ic_music_disc_bg_mini)
                        .error(R.drawable.ic_music_disc_bg_mini)
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .into(new BitmapImageViewTarget(holderView.imageCover) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                super.setResource(resource);
                            }
                        });
            }
            holderView.itemRootView.setTag(mediaInfo);
            holderView.itemRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(null!=mListener){
                        mListener.onItemClick(v,position,mediaInfo.getId());
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return null==mData?0:mData.size();
    }

    public List<MediaInfo> getData(){
        return mData;
    }

    private class MusicHolderView extends RecyclerView.ViewHolder{

        private MusicRoundImageView imageCover;
        private TextView textTitle;
        private TextView textAnchor;
        private View itemRootView;

        public MusicHolderView(View itemView) {
            super(itemView);
            imageCover = (MusicRoundImageView) itemView.findViewById(R.id.view_item_cover);
            textTitle = (TextView) itemView.findViewById(R.id.view_item_title);
            textAnchor = (TextView) itemView.findViewById(R.id.view_item_anchor);
            itemRootView = itemView.findViewById(R.id.item_root_view);
            imageCover.getLayoutParams().height=mItemWidth;
        }
    }

    public void onDestroy() {
        mContext=null;mInflater=null;mListener=null;mItemWidth=0;
        if(null!=mData){
            mData.clear();
            mData=null;
            this.notifyDataSetChanged();
        }
    }
}