package com.yc.liaolive.mine.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yc.liaolive.R;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.base.adapter.BaseViewHolder;
import com.yc.liaolive.bean.FansInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by wanglin  on 2018/7/5 10:10.
 */
public class BlackListAdapter extends BaseQuickAdapter<FansInfo, BaseViewHolder> {
    public BlackListAdapter(@Nullable List<FansInfo> data) {
        super(R.layout.blacklist_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, FansInfo item) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
        long time = item.getEdittime();
        if (time == 0) {
            time = item.getAddtime();
        }

        helper.setText(R.id.item_name, item.getNickname()).setText(R.id.item_date, sdf.format(new Date(time * 1000)));
        Glide.with(mContext).load(item.getAvatar()).error(R.drawable.ic_face_boart).into((ImageView) helper.getView(R.id.item_icon));

    }
}
