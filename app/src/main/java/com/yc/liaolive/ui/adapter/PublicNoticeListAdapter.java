package com.yc.liaolive.ui.adapter;

import android.support.annotation.Nullable;
import com.yc.liaolive.R;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.base.adapter.BaseViewHolder;
import com.yc.liaolive.bean.HomeNoticeInfo;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * TinyHung@Outlook.com
 * 2018/7/6
 * 系统通告
 */

public class PublicNoticeListAdapter extends BaseQuickAdapter<HomeNoticeInfo,BaseViewHolder>{

    public PublicNoticeListAdapter(@Nullable List<HomeNoticeInfo> data) {
        super(R.layout.re_item_public_notice, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, HomeNoticeInfo item) {
        long time = item.getAddtime();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
        helper.setText(R.id.item_title,item.getTitle())
                .setText(R.id.item_time,sdf.format(new Date(time*1000)));
    }
}
