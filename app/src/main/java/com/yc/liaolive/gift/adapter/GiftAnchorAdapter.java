package com.yc.liaolive.gift.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.yc.liaolive.R;
import com.yc.liaolive.live.bean.PusherInfo;
import java.util.List;

/**
 * @time 2018/6/8
 * @des 礼物接收人适配器
 */
public class GiftAnchorAdapter extends BaseAdapter {

    private List<PusherInfo> mData;
    private LayoutInflater mInflater;

    public GiftAnchorAdapter(Context context, List<PusherInfo> mData) {
        this.mData = mData;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mData ==null?0: mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData ==null?null: mData.get(position);
    }

    public List<PusherInfo> getData(){
        return mData;
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(null==convertView){
            convertView=mInflater.inflate(R.layout.list_gift_anchor_item_layout,null);
            viewHolder=new ViewHolder();
            viewHolder.tv_item_title= (TextView) convertView.findViewById(R.id.tv_item_title);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        try {
            PusherInfo pusherInfo = mData.get(position);
            if(null!=pusherInfo){
                viewHolder.tv_item_title.setText(pusherInfo.getUserName());
            }
        }catch (Exception e){

        }
        return convertView;
    }

    /**
     * 设置数据
     */
    public void setNewData( List<PusherInfo> data) {
        this.mData =data;
        this.notifyDataSetChanged();
    }

    private class ViewHolder{
        private TextView  tv_item_title;
    }
}
