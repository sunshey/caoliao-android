package com.yc.liaolive.mine.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;

import com.yc.liaolive.R;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.base.adapter.BaseViewHolder;
import com.yc.liaolive.bean.HelpInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by wanglin  on 2018/7/5 15:11.
 */
public class HelpAdapter extends BaseQuickAdapter<HelpInfo, BaseViewHolder> {


    public HelpAdapter(@Nullable List<HelpInfo> data) {
        super(R.layout.activity_help_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, HelpInfo item) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
        String time = sdf.format(new Date(item.getAddtime() * 1000));
        if (TextUtils.isEmpty(time)) {
            time = sdf.format(new Date());
        }
        TextView titleView = helper.getView(R.id.item_title);
        titleView.setText(item.getTitle());


        TextView timeView = helper.getView(R.id.item_time);
        timeView.setText(time);

//        helper.setText(R.id.item_title, item.getTitle()).setText(R.id.item_time, time);
    }
}
