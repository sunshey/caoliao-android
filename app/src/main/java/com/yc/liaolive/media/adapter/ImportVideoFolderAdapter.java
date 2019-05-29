package com.yc.liaolive.media.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.ImageView;
import com.yc.liaolive.R;
import com.yc.liaolive.bean.VideoFolder;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.base.adapter.BaseViewHolder;
import com.yc.liaolive.bean.WeiXinVideo;
import com.yc.liaolive.util.ImageCache;
import com.yc.liaolive.util.attach.LoadLocalBigImgTask;
import com.yc.liaolive.util.ScanWeixin;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * TinyHung@outlook.com
 * 2017-9-7
 * 本地文件夹列表选择
 */

public class ImportVideoFolderAdapter extends BaseQuickAdapter<VideoFolder,BaseViewHolder> {

	private final Context context;
	private final ExecutorService mExecutorService;

	public ImportVideoFolderAdapter(List<VideoFolder> folderlist, Context context) {
		super(R.layout.list_item_import_image_folder,folderlist);
		this.context=context;
		mExecutorService = Executors.newCachedThreadPool();
	}

	@Override
	protected void convert(BaseViewHolder helper, VideoFolder item) {

		if(null==item) return;
		ImageView icon = (ImageView) helper.getView(R.id.icon);
		icon.setImageResource(R.drawable.iv_empty_bg_error);//占位图
		helper.setText(R.id.count,"");
		icon.setTag(item);
		helper.setText(R.id.title,item.name).setText(R.id.count,0==item.count?"":item.count + "");
		helper.setVisible(R.id.view_line,helper.getPosition()==getData().size()-1?false:true);
		//微信
		if(999999999==item._id){
			helper.setText(R.id.count,item.count>0?item.count+"+":"");
		}
		if(888888888==item._id){
			helper.setText(R.id.count,"");
			icon.setImageResource(R.drawable.iv_folder_all);
        }else{
			//普通的
			if(!TextUtils.isEmpty(item.url)){
				Bitmap bitmap = ImageCache.getInstance().get(item.url);
				if(null!=bitmap){
					icon.setImageBitmap(bitmap);
				}else{
					new LoadLocalBigImgTask(icon,R.drawable.iv_folder_all,item.url).executeOnExecutor(mExecutorService);
				}
			}else{
				if(!TextUtils.isEmpty(item.path)){
					new ScanVideoTask(icon,item.path).execute();
				}
			}
		}
	}

	/**
	 * 先扫描视频在获取封面
	 */
	private class ScanVideoTask extends AsyncTask<Void,Void,List<WeiXinVideo>> {

		private final ImageView icon;
		private final String path;

		public ScanVideoTask(ImageView icon, String path) {
			this.icon=icon;
			this.path=path;
		}

		@Override
		protected List<WeiXinVideo> doInBackground(Void... params) {
			ScanWeixin scanWeixin = new ScanWeixin();
			scanWeixin.setEvent(false);
			scanWeixin.setExts("mp4","mov","3gp");
			scanWeixin.setMax(1);
			List<WeiXinVideo> weiXinVideos = scanWeixin.scanFiles(path);
			return weiXinVideos;
		}

		@Override
		protected void onPostExecute(List<WeiXinVideo> scanWeixins) {
			super.onPostExecute(scanWeixins);
			if(null!=scanWeixins&&scanWeixins.size()>0){
				WeiXinVideo weiXinVideo = scanWeixins.get(0);
				if(null!=weiXinVideo){
					Bitmap bitmap = ImageCache.getInstance().get(weiXinVideo.getVideoPath());
					if(null!=bitmap){
						if(null!=icon){
							icon.setImageBitmap(bitmap);
						}
					}else{
						new LoadLocalBigImgTask(icon,R.drawable.iv_folder_all,weiXinVideo.getVideoPath()).execute();
					}
				}
			}
		}
	}
}
