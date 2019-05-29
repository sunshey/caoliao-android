package com.android.imusic.video.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import com.android.imusic.R;
import com.android.imusic.video.adapter.holder.VideoBannerViewHolder;
import com.android.imusic.video.adapter.holder.VideoCardViewHolder;
import com.android.imusic.video.adapter.holder.VideoTitleViewHolder;
import com.android.imusic.video.adapter.holder.VideoVideoViewHolder;
import com.android.imusic.video.bean.OpenEyesIndexItemBean;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.music.player.lib.adapter.base.BaseAdapter;
import com.music.player.lib.util.Logger;
import com.music.player.lib.util.MusicUtils;
import com.video.player.lib.model.VideoPlayerScene;
import com.video.player.lib.utils.VideoUtils;
import com.video.player.lib.view.VideoTextrueProvider;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2019/4/8
 * Index List Adapter
 */

public class VideoIndexVideoAdapter extends BaseAdapter<OpenEyesIndexItemBean,RecyclerView.ViewHolder> {

    private final int mScreenWidth;

    public VideoIndexVideoAdapter(Context context, @Nullable List<OpenEyesIndexItemBean> data) {
        super(context,data);
        mScreenWidth = MusicUtils.getInstance().getScreenWidth(context);
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
        if(viewType== OpenEyesIndexItemBean.ITEM_CARD){
            View inflate = mInflater.inflate(R.layout.video_index_video_card, null);
            return new VideoCardViewHolder(inflate);
        }else if(viewType== OpenEyesIndexItemBean.ITEM_TITLE){
            View inflate = mInflater.inflate(R.layout.video_index_video_title, null);
            return new VideoTitleViewHolder(inflate);
        }else if(viewType== OpenEyesIndexItemBean.ITEM_FOLLOW){
            View inflate = mInflater.inflate(R.layout.video_index_video_video, null);
            return new VideoVideoViewHolder(inflate);
        }else if(viewType== OpenEyesIndexItemBean.ITEM_VIDEO){
            View inflate = mInflater.inflate(R.layout.video_index_video_video, null);
            return new VideoVideoViewHolder(inflate);
        }else if(viewType== OpenEyesIndexItemBean.ITEM_BANNER){
            View inflate = mInflater.inflate(R.layout.video_index_video_banner, null);
            return new VideoBannerViewHolder(inflate);
        }
        return new UnKnownView(mInflater.inflate(R.layout.music_unknown_layout, null));
    }

    @Override
    public void inBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        int itemViewType = getItemViewType(position);
        OpenEyesIndexItemBean itemData = getItemData(position);
        viewHolder.itemView.setTag(null);
        if(null!=itemData){
            //精品推荐
            if(itemViewType==OpenEyesIndexItemBean.ITEM_CARD){
                VideoCardViewHolder cardViewHolder= (VideoCardViewHolder) viewHolder;
                if(null!=itemData.getData()){
                    cardViewHolder.mTransformerVideoPager.setVideos(itemData.getData().getItemList());
                }
            //标题
            }else if(itemViewType==OpenEyesIndexItemBean.ITEM_TITLE){
                VideoTitleViewHolder titleViewHolder= (VideoTitleViewHolder) viewHolder;
                if(null!=itemData.getData()){
                    titleViewHolder.textTitle.setText(itemData.getData().getText());
                }
            //喜欢的视频
            }else if(itemViewType==OpenEyesIndexItemBean.ITEM_FOLLOW){
                VideoVideoViewHolder videoViewHolder= (VideoVideoViewHolder) viewHolder;
                int itemHeight = (mScreenWidth - MusicUtils.getInstance().dpToPxInt(getContext(), 30f)) * 9 / 16;
                videoViewHolder.trackVideo.getLayoutParams().height=itemHeight;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    videoViewHolder.itemRoomItem.setOutlineProvider(new VideoTextrueProvider(VideoUtils.getInstance().dpToPxInt(getContext(),8f)));
                }
                if(null!=itemData.getData()&&null!=itemData.getData().getContent()){
                    OpenEyesIndexItemBean indexItemBean = itemData.getData().getContent().getData();
                    viewHolder.itemView.setTag(indexItemBean);
                    videoViewHolder.trackVideo.setDataSource(indexItemBean.getPlayUrl(),indexItemBean.getTitle(), VideoPlayerScene.LIST);
                    //视频时长
                    videoViewHolder.trackVideo.getCoverController().mPreDurtion.setText(MusicUtils.getInstance().stringForAudioTime(indexItemBean.getDuration()*1000));
                    //观看人次
                    if(null!=indexItemBean.getConsumption()){
                        videoViewHolder.trackVideo.getCoverController().mPreCount.setText(indexItemBean.getConsumption().getReplyCount()+"人观看");
                    }
                    videoViewHolder.itemMenu.setTag(null);
                    if(!TextUtils.isEmpty(indexItemBean.getDescription())){
                        videoViewHolder.itemMenu.setVisibility(View.VISIBLE);
                        videoViewHolder.itemMenu.setTag(indexItemBean);
                        videoViewHolder.itemMenu.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(null!=mOnMenuClickListener){
                                    mOnMenuClickListener.onMenuClick(viewHolder.itemView,v);
                                }
                            }
                        });
                    }else{
                        videoViewHolder.itemMenu.setVisibility(View.INVISIBLE);
                    }
                    videoViewHolder.itemTitle.setText(indexItemBean.getTitle());
                    //封面
                    Glide.with(getContext())
                            .load(indexItemBean.getCover().getFeed())
                            .placeholder(R.drawable.ic_video_default_cover)
                            .error(R.drawable.ic_video_default_cover)
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(videoViewHolder.trackVideo.getCoverController().mVideoCover);
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
                VideoVideoViewHolder videoViewHolder= (VideoVideoViewHolder) viewHolder;
                int itemHeight = (mScreenWidth - MusicUtils.getInstance().dpToPxInt(getContext(), 30f)) * 9 / 16;
                videoViewHolder.trackVideo.getLayoutParams().height=itemHeight;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    videoViewHolder.itemRoomItem.setOutlineProvider(new VideoTextrueProvider(VideoUtils.getInstance().dpToPxInt(getContext(),8f)));
                }
                if(null!=itemData.getData()){
                    OpenEyesIndexItemBean indexItemBean = itemData.getData();
                    viewHolder.itemView.setTag(indexItemBean);
                    videoViewHolder.trackVideo.setDataSource(indexItemBean.getPlayUrl(),indexItemBean.getTitle(), VideoPlayerScene.LIST);
                    //视频时长
                    videoViewHolder.trackVideo.getCoverController().mPreDurtion.setText(MusicUtils.getInstance().stringForAudioTime(indexItemBean.getDuration()*1000));
                    //观看人次
                    if(null!=indexItemBean.getConsumption()){
                        videoViewHolder.trackVideo.getCoverController().mPreCount.setText(indexItemBean.getConsumption().getReplyCount()+"人观看");
                    }
                    if(!TextUtils.isEmpty(indexItemBean.getDescription())){
                        videoViewHolder.itemMenu.setVisibility(View.VISIBLE);
                        videoViewHolder.itemMenu.setTag(indexItemBean);
                        videoViewHolder.itemMenu.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(null!=mOnMenuClickListener){
                                    mOnMenuClickListener.onMenuClick(viewHolder.itemView,v);
                                }
                            }
                        });
                    }else{
                        videoViewHolder.itemMenu.setVisibility(View.INVISIBLE);
                    }
                    videoViewHolder.itemTitle.setText(indexItemBean.getTitle());
                    //封面
                    Glide.with(getContext())
                            .load(indexItemBean.getCover().getFeed())
                            .placeholder(R.drawable.ic_video_default_cover)
                            .error(R.drawable.ic_video_default_cover)
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(videoViewHolder.trackVideo.getCoverController().mVideoCover);
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
            //Banner
            }else if(itemViewType==OpenEyesIndexItemBean.ITEM_BANNER){
                Logger.d(TAG,"BANNER--1");
                VideoBannerViewHolder bannerViewHolder= (VideoBannerViewHolder) viewHolder;
                int itemHeight = (mScreenWidth - MusicUtils.getInstance().dpToPxInt(getContext(), 30f)) * 9 / 16;
                bannerViewHolder.itemBannerCover.getLayoutParams().height=itemHeight;
                Glide.with(getContext())
                        .load(MusicUtils.getInstance().formatImageUrl(itemData.getData().getImage()))
                        .asBitmap()
                        .placeholder(R.drawable.ic_video_default_cover)
                        .error(R.drawable.ic_video_default_cover)
                        .dontAnimate()
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(new BitmapImageViewTarget(bannerViewHolder.itemBannerCover) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                super.setResource(resource);
                            }
                        });
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

    public interface OnMenuClickListener{
        void onMenuClick(View itemView,View view);
    }

    private OnMenuClickListener mOnMenuClickListener;

    public void setOnMenuClickListener(OnMenuClickListener onMenuClickListener) {
        mOnMenuClickListener = onMenuClickListener;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}