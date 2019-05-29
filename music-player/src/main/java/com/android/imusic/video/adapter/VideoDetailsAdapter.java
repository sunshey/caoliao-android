package com.android.imusic.video.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.android.imusic.R;
import com.android.imusic.video.adapter.holder.VideoDetailsItemViewHolder;
import com.android.imusic.video.adapter.holder.VideoTitleViewHolder;
import com.android.imusic.video.bean.OpenEyesIndexItemBean;
import com.android.imusic.video.bean.VideoParams;
import com.android.imusic.video.view.VideoDetailsHeaderView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.music.player.lib.adapter.base.BaseAdapter;
import com.music.player.lib.util.MusicUtils;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2019/4/10
 * Video Details List Adapter
 */

public class VideoDetailsAdapter extends BaseAdapter<OpenEyesIndexItemBean,RecyclerView.ViewHolder> {

    private int mCoverItemWidth;
    private int mCoverItemHeight;
    private int mScreenWidth;

    public VideoDetailsAdapter(Context context, @Nullable List<OpenEyesIndexItemBean> data) {
        super(context,data);
        mScreenWidth = MusicUtils.getInstance().getScreenWidth(context);
        mCoverItemWidth = mScreenWidth /2;
        mCoverItemHeight = mCoverItemWidth*9/16;
    }

    @Override
    public int getItemCount() {
        return null==mData?0:mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(null!=getData()){
            return getData().get(position).getItemType();
        }
        return OpenEyesIndexItemBean.ITEM_UNKNOWN;
    }

    @Override
    public RecyclerView.ViewHolder inCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if(viewType== OpenEyesIndexItemBean.ITEM_TITLE){
            View inflate = mInflater.inflate(R.layout.video_details_video_title, null);
            return new VideoTitleViewHolder(inflate);
        }else if(viewType== OpenEyesIndexItemBean.ITEM_FOLLOW){
            View inflate = mInflater.inflate(R.layout.video_details_item_video, null);
            return new VideoDetailsItemViewHolder(inflate);
        }else if(viewType== OpenEyesIndexItemBean.ITEM_VIDEO){
            View inflate = mInflater.inflate(R.layout.video_details_item_video, null);
            return new VideoDetailsItemViewHolder(inflate);
        }else if(viewType== OpenEyesIndexItemBean.ITEM_VIDEO_HEADER){
            View inflate = mInflater.inflate(R.layout.video_details_header_layout, null);
            return new VideoHeaderViewHolder(inflate);
        }
        return new UnKnownView(mInflater.inflate(R.layout.music_unknown_layout, null));
    }

    @Override
    public void inBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        int itemViewType = getItemViewType(position);
        OpenEyesIndexItemBean itemData = getItemData(position);
        if(null!=itemData){
            //标题
            if(itemViewType==OpenEyesIndexItemBean.ITEM_TITLE){
                VideoTitleViewHolder titleViewHolder= (VideoTitleViewHolder) viewHolder;
                titleViewHolder.textTitle.getLayoutParams().width=mScreenWidth;
                if(null!=itemData.getData()){
                    titleViewHolder.textTitle.setText(itemData.getData().getText());
                }
            //喜欢的视频
            }else if(itemViewType==OpenEyesIndexItemBean.ITEM_FOLLOW){
                VideoDetailsItemViewHolder videoViewHolder= (VideoDetailsItemViewHolder) viewHolder;
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) videoViewHolder.itemCoverRoot.getLayoutParams();
                layoutParams.width=mCoverItemWidth;
                layoutParams.height=mCoverItemHeight;
                videoViewHolder.itemCoverRoot.setLayoutParams(layoutParams);
                if(null!=itemData.getData()&&null!=itemData.getData().getContent()){
                    OpenEyesIndexItemBean indexItemBean = itemData.getData().getContent().getData();
                    viewHolder.itemView.setTag(indexItemBean);
                    //视频时长
                    videoViewHolder.itemDurtion.setText(MusicUtils.getInstance().stringForAudioTime(indexItemBean.getDuration()*1000));
                    //观看人次
                    if(null!=indexItemBean.getConsumption()){
                        videoViewHolder.itemTime.setText("更新于"+MusicUtils.getInstance().getTimeNow(indexItemBean.getAuthor().getLatestReleaseTime()));
                    }
                    videoViewHolder.itemUserName.setText(indexItemBean.getAuthor().getName());
                    videoViewHolder.itemTitle.setText(indexItemBean.getAuthor().getDescription());
                    //封面
                    Glide.with(getContext())
                            .load(indexItemBean.getCover().getFeed())
                            .asBitmap()
                            .placeholder(R.drawable.ic_video_default_cover)
                            .error(R.drawable.ic_video_default_cover)
                            .dontAnimate()
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(new BitmapImageViewTarget(videoViewHolder.itemCover) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    super.setResource(resource);
                                }
                            });
                    //用户头像
                    Glide.with(getContext())
                            .load(indexItemBean.getAuthor().getIcon())
                            .asBitmap()
                            .error(R.drawable.ic_music_default_cover)
                            .dontAnimate()
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(new BitmapImageViewTarget(videoViewHolder.itemUserCover) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    super.setResource(resource);
                                }
                            });
                }
            //Video
            }else if(itemViewType==OpenEyesIndexItemBean.ITEM_VIDEO){
                VideoDetailsItemViewHolder videoViewHolder= (VideoDetailsItemViewHolder) viewHolder;
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) videoViewHolder.itemCoverRoot.getLayoutParams();
                layoutParams.width=mCoverItemWidth;
                layoutParams.height=mCoverItemHeight;
                videoViewHolder.itemCoverRoot.setLayoutParams(layoutParams);
                if(null!=itemData.getData()){
                    OpenEyesIndexItemBean indexItemBean = itemData.getData();
                    viewHolder.itemView.setTag(indexItemBean);
                    //视频时长
                    videoViewHolder.itemDurtion.setText(MusicUtils.getInstance().stringForAudioTime(indexItemBean.getDuration()*1000));
                    //观看人次
                    if(null!=indexItemBean.getConsumption()){
                        videoViewHolder.itemTime.setText("更新于"+MusicUtils.getInstance().getTimeNow(indexItemBean.getAuthor().getLatestReleaseTime()));
                    }
                    videoViewHolder.itemUserName.setText(indexItemBean.getAuthor().getName());
                    videoViewHolder.itemTitle.setText(indexItemBean.getAuthor().getDescription());
                    //封面
                    Glide.with(getContext())
                            .load(indexItemBean.getCover().getFeed())
                            .asBitmap()
                            .placeholder(R.drawable.ic_video_default_cover)
                            .error(R.drawable.ic_video_default_cover)
                            .dontAnimate()
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(new BitmapImageViewTarget(videoViewHolder.itemCover) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    super.setResource(resource);
                                }
                            });
                    //用户头像
                    Glide.with(getContext())
                            .load(indexItemBean.getAuthor().getIcon())
                            .asBitmap()
                            .error(R.drawable.ic_music_default_cover)
                            .dontAnimate()
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(new BitmapImageViewTarget(videoViewHolder.itemUserCover) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    super.setResource(resource);
                                }
                            });
                }
            }else if(itemViewType==OpenEyesIndexItemBean.ITEM_VIDEO_HEADER){
                VideoHeaderViewHolder headerViewHolder= (VideoHeaderViewHolder) viewHolder;
                if(null!=itemData.getVideoParams()){
                    VideoParams videoParams = itemData.getVideoParams();
                    headerViewHolder.headerView.setVideoDetailsData(videoParams);
                }
            }
        }
    }

    @Override
    protected void inBindViewHolder(RecyclerView.ViewHolder viewHolder, int position, List<Object> payloads) {
        super.inBindViewHolder(viewHolder, position, payloads);
        OpenEyesIndexItemBean itemData = getItemData(position);
        if(null!=itemData){

        }
    }

    private class UnKnownView extends RecyclerView.ViewHolder{

        public UnKnownView(View itemView) {
            super(itemView);
        }
    }

    private class VideoHeaderViewHolder extends RecyclerView.ViewHolder {

        private VideoDetailsHeaderView headerView;

        public VideoHeaderViewHolder(View inflate) {
            super(inflate);
            headerView = inflate.findViewById(R.id.view_item_header);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mScreenWidth=0;mCoverItemWidth=0;mCoverItemHeight=0;
    }
}