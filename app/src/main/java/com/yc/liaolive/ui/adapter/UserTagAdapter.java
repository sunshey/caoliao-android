package com.yc.liaolive.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.content.Context;
import com.yc.liaolive.R;
import com.yc.liaolive.bean.TagInfo;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/10/17
 * 用户标签TAG
 */

public class UserTagAdapter extends TagAdapter {

    private final LayoutInflater mInflater;
    private final List<TagInfo> mData;

    public UserTagAdapter(Context context,  List<TagInfo> datas) {
        super(datas);
        this.mData=datas;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(FlowLayout parent, int position, Object o) {
        TextView inflate = (TextView) mInflater.inflate(R.layout.item_user_tags, parent, false);
        inflate.setText(mData.get(position).getContent());
        return inflate;
    }

    @Override
    public void notifyDataChanged() {
        super.notifyDataChanged();
    }
}
