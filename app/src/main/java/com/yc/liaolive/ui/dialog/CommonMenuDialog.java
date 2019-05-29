package com.yc.liaolive.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.yc.liaolive.R;
import com.yc.liaolive.ui.adapter.CommonMenuAdapter;
import com.yc.liaolive.bean.VideoDetailsMenu;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.view.widget.IndexLinLayoutManager;

import java.util.List;

/**
 * @time 2016/10/26 15:41
 * @des 通用的底部弹窗界面，调用者传入ListItem
 */
public class CommonMenuDialog extends BottomSheetDialog {

    private CommonMenuAdapter mCommonMenuAdapter;

    public static CommonMenuDialog getInstance(Activity activity) {
        return new CommonMenuDialog(activity);
    }


    public CommonMenuDialog(Activity context) {
        super(context, R.style.ButtomDialogAnimationStyle);
        setContentView(R.layout.dialog_commend_menu);
        initLayoutPrams();
        initViews();
    }

    private void initViews() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new IndexLinLayoutManager(getContext(),IndexLinLayoutManager.VERTICAL,false));
        mCommonMenuAdapter = new CommonMenuAdapter(null);
        recyclerView.setAdapter(mCommonMenuAdapter);
        mCommonMenuAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                List<VideoDetailsMenu> data = mCommonMenuAdapter.getData();
                if(null!=data&&data.size()>0){
                    VideoDetailsMenu videoDetailsMenu = data.get(position);
                    if(null!=videoDetailsMenu&&null!=mOnItemClickListener){
                        CommonMenuDialog.this.dismiss();
                        mOnItemClickListener.onItemClick(videoDetailsMenu.getItemID(),videoDetailsMenu);
                    }
                }
            }
        });
        findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonMenuDialog.this.dismiss();
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

    /**
     * 设置菜单数据
     * @param list
     * @return
     */
    public CommonMenuDialog setData(List<VideoDetailsMenu> list){
        if(null!= mCommonMenuAdapter){
            mCommonMenuAdapter.setNewData(list);
        }
        return this;
    }

    /**
     * 设置取消文本内容
     * @param cancleText
     * @return
     */
    public CommonMenuDialog setCancleText(String cancleText) {
        ((TextView) findViewById(R.id.btn_submit)).setText(cancleText);
        return this;
    }

    /**
     * 设置取消字体颜色
     * @param color
     * @return
     */
    public CommonMenuDialog setCancleTextColor(int color) {
        ((TextView) findViewById(R.id.btn_submit)).setTextColor(color);
        return this;
    }

    public interface OnItemClickListener{
        void onItemClick(int itemID,VideoDetailsMenu videoDetailsMenu);
    }
    private OnItemClickListener mOnItemClickListener;

    public CommonMenuDialog setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
        return this;
    }
}
