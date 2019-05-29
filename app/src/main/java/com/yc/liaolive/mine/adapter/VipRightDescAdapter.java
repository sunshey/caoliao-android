package com.yc.liaolive.mine.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.yc.liaolive.R;
import com.yc.liaolive.bean.VipInfoBean;
import com.yc.liaolive.view.widget.BaseItemDecoration;

import java.util.List;

/**
 * Created by wanglin  on 2018/7/11 11:01.
 */
public class VipRightDescAdapter extends PagerAdapter {

    private Context mContext;
    private List<List<VipInfoBean>> mVipInfos;

    public VipRightDescAdapter(Context context, List<List<VipInfoBean>> vipInfoBeans) {
        this.mContext = context;
        this.mVipInfos = vipInfoBeans;
    }

    @Override
    public int getCount() {
        return mVipInfos == null ? 0 : mVipInfos.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = View.inflate(mContext, R.layout.vip_right_desc, null);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
        VipRightDescItemAdapter adapter = new VipRightDescItemAdapter(mVipInfos.get(position));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new BaseItemDecoration(mContext));
        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
