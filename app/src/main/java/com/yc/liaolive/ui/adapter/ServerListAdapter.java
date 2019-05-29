package com.yc.liaolive.ui.adapter;

import android.view.View;
import com.yc.liaolive.R;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.base.adapter.BaseViewHolder;
import com.yc.liaolive.bean.ServerListBean;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/10/31
 * 客服列表
 */

public class ServerListAdapter extends BaseQuickAdapter<ServerListBean,BaseViewHolder> {


    public ServerListAdapter(List<ServerListBean> data) {
        super(R.layout.re_server_list_item,data);
    }

    @Override
    protected void convert(final BaseViewHolder helper, final ServerListBean item) {
        try {
            if(null!=item){
               helper.setText(R.id.item_name,item.getName()+":"+item.getIdentify()).setText(R.id.item_submit,"复制微信");
                View btnSubmit = helper.getView(R.id.item_submit);
                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(null!=mOnCopyListener) mOnCopyListener.onCopy((ServerListBean) v.getTag());
                    }
                });
                btnSubmit.setTag(item);
            }
        }catch (Exception e){

        }
    }
    public interface OnCopyListener{
        void onCopy(ServerListBean data);
    }

    private OnCopyListener mOnCopyListener;

    public void setOnCopyListener(OnCopyListener onCopyListener) {
        mOnCopyListener = onCopyListener;
    }
}
