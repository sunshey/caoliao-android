package com.yc.liaolive.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.yc.liaolive.R;
import com.yc.liaolive.ui.adapter.ShareAdapter;
import com.yc.liaolive.bean.ShareMenuItemInfo;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.ui.contract.ShareContract;
import com.yc.liaolive.ui.presenter.SharePresenter;
import com.yc.liaolive.util.Logger;
import java.util.ArrayList;
import java.util.List;

 /**
 * @time 2016/10/26 15:41
 * @des $分享选择界面
 */
public class ShareDialog extends BottomSheetDialog implements ShareContract.View {

     private static final String TAG = "ShareDialog";

     private String mRoomID;//房间ID
     private final SharePresenter mPresenter;

     public static ShareDialog getInstance(Activity activity) {
         return new ShareDialog(activity);
     }

     public ShareDialog(Activity context) {
         super(context, R.style.ButtomDialogAnimationStyle);
         setContentView(R.layout.dialog_share);
         initLayoutPrams();
         initViews();
         mPresenter = new SharePresenter();
         mPresenter.attachView(this);
         if(null!=mRoomID&&!mPresenter.isLoading()){
             mPresenter.getWebUrl(mRoomID);
         }
     }

     private void initViews() {
         List<ShareMenuItemInfo> homeItemInfos = new ArrayList<>();
         homeItemInfos.add(new ShareMenuItemInfo("微信",R.drawable.iv_share_weichat, SHARE_MEDIA.WEIXIN));
         homeItemInfos.add(new ShareMenuItemInfo("朋友圈",R.drawable.iv_share_weichatfriend,SHARE_MEDIA.WEIXIN_CIRCLE));
         homeItemInfos.add(new ShareMenuItemInfo("QQ好友",R.drawable.iv_share_qq,SHARE_MEDIA.QQ));
         homeItemInfos.add(new ShareMenuItemInfo("QQ空间",R.drawable.iv_share_qzone,SHARE_MEDIA.QZONE));
         RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
         recyclerView.setHasFixedSize(true);
         recyclerView.setLayoutManager(new GridLayoutManager(getContext(),4,GridLayoutManager.VERTICAL,false));
         final ShareAdapter shareAdapter = new ShareAdapter(homeItemInfos);
         recyclerView.setAdapter(shareAdapter);
         shareAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
             @Override
             public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                 if(null!=mOnShareItemClickListener){
                     ShareDialog.this.dismiss();
                     List<ShareMenuItemInfo> data = shareAdapter.getData();
                     ShareMenuItemInfo shareMenuItemInfo = data.get(position);
                     mOnShareItemClickListener.onItemClick(shareMenuItemInfo);
                 }
             }
         });
         findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 ShareDialog.this.dismiss();
             }
         });
     }

     protected void initLayoutPrams(){
         Window window = getWindow();
         window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
         WindowManager.LayoutParams attributes = window.getAttributes();//得到布局管理者
         WindowManager systemService = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);//得到窗口管理者
         DisplayMetrics displayMetrics=new DisplayMetrics();//创建设备屏幕的管理者
         systemService.getDefaultDisplay().getMetrics(displayMetrics);//得到屏幕的宽高
         attributes.height= FrameLayout.LayoutParams.WRAP_CONTENT;
         attributes.width= systemService.getDefaultDisplay().getWidth();
         attributes.gravity= Gravity.BOTTOM;
     }

     public ShareDialog setRoomID(String roomId) {
         this.mRoomID=roomId;
         return this;
     }

     @Override
     public void dismiss() {
         super.dismiss();
         if(null!=mPresenter) mPresenter.detachView();
     }

     @Override
     public void showErrorView() {

     }

     @Override
     public void complete() {

     }

     @Override
     public void showWebResult(String data) {
     }

     @Override
     public void showWebResultError(int code, String data) {

     }


     public interface OnShareItemClickListener{
        void onItemClick(ShareMenuItemInfo shareMenuItemInfo);
     }

     private OnShareItemClickListener mOnShareItemClickListener;

     public ShareDialog setOnItemClickListener(OnShareItemClickListener onShareItemClickListener) {
        mOnShareItemClickListener = onShareItemClickListener;
        return this;
     }
}
