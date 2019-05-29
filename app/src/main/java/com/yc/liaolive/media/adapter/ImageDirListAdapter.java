package com.yc.liaolive.media.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.yc.liaolive.bean.ImageDirInfo;
import com.yc.liaolive.util.Logger;

import java.io.File;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2017/9/19
 * 本地相册文件夹列表
 */

public class ImageDirListAdapter extends BaseAdapter {

    private static final String TAG = "ImageDirListAdapter";
    private final Context mContext;
    private List<ImageDirInfo> mDatat;
    private LayoutInflater mInflater;

    public ImageDirListAdapter(Context context, List<ImageDirInfo> mDatat) {
        this.mDatat = mDatat;
        this.mContext=context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mDatat ==null?0: mDatat.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatat ==null?null: mDatat.get(position);
    }

    public List<ImageDirInfo> getData(){
        return mDatat;
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(null==convertView){
            convertView=mInflater.inflate(R.layout.list_image_dir_item_layout,null);
            viewHolder=new ViewHolder();
            viewHolder.re_root_item= (View) convertView.findViewById(R.id.re_root_item);
            viewHolder.tv_item_title= (TextView) convertView.findViewById(R.id.tv_item_title);
            viewHolder.tv_item_count= (TextView) convertView.findViewById(R.id.tv_item_count);
            viewHolder.item_ic_cover= (ImageView) convertView.findViewById(R.id.item_ic_cover);
            viewHolder.item_selected_state= (ImageView) convertView.findViewById(R.id.item_selected_state);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        ImageDirInfo imageDirInfo = mDatat.get(position);
        if(null!=imageDirInfo){
            viewHolder.tv_item_title.setText(imageDirInfo.getDirName());
            viewHolder.tv_item_count.setText(imageDirInfo.getCount()+"张");
            try{
                viewHolder.item_selected_state.setImageResource(imageDirInfo.isSelected()?R.drawable.ic_folder_selector_true:0);
                Glide.with(mContext)
                    .load(Uri.fromFile(new File(imageDirInfo.getFilePath())))
                    .error(R.drawable.ic_default_item_cover)
                    .override(200, 200)
                    .dontAnimate()
                    .animate(R.anim.item_alpha_in)//加载中动画
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                    .centerCrop()//中心点缩放
                    .skipMemoryCache(false)
                    .into(viewHolder.item_ic_cover);
            }catch (RuntimeException e){

            }catch (Exception e){

            }
        }
        return convertView;
    }

    /**
     * 设置数据
     */
    public void setNewData( List<ImageDirInfo> data) {
        this.mDatat =data;
        this.notifyDataSetChanged();
    }

    private class ViewHolder{
        private View re_root_item;
        private ImageView item_ic_cover;
        private TextView  tv_item_title;
        private TextView  tv_item_count;
        private ImageView  item_selected_state;
    }
}
