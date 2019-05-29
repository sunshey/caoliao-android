package com.yc.liaolive.search.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.yc.liaolive.R;
import com.yc.liaolive.bean.SearchAutoResult;
import java.util.List;

/**
 * @time 2017/3/7 16:49
 * @des $搜索界面自动补全列表适配器
 */
public class AutoCompletSearchAdapter extends BaseAdapter {

    private List<SearchAutoResult> remmonedDataBeen;
    private LayoutInflater mInflater;

    public AutoCompletSearchAdapter(Context context, List<SearchAutoResult> remmonedDataBeen) {
        this.remmonedDataBeen=remmonedDataBeen;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return remmonedDataBeen==null?0:remmonedDataBeen.size();
    }

    @Override
    public Object getItem(int position) {
        return remmonedDataBeen==null?null:remmonedDataBeen.get(position);
    }

    public List<SearchAutoResult> getData(){
        return remmonedDataBeen;
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(null==convertView){
            convertView=mInflater.inflate(R.layout.list_search_actocomple_item_layout,null);
            viewHolder=new ViewHolder();
            viewHolder.iv_item_icon= (ImageView) convertView.findViewById(R.id.iv_item_icon);
            viewHolder.iv_item_delete= (ImageView) convertView.findViewById(R.id.iv_item_delete);
            viewHolder.tv_item_title= (TextView) convertView.findViewById(R.id.tv_item_title);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        try {
            SearchAutoResult searchAutoResult = remmonedDataBeen.get(position);
            if(null!=searchAutoResult){
                viewHolder.iv_item_delete.setVisibility(View.GONE);
                viewHolder.tv_item_title.setText(searchAutoResult.getKey());
                viewHolder.iv_item_icon.setImageResource(0==searchAutoResult.getType()?R.drawable.iv_search_author:R.drawable.iv_search_video);
            }
        }catch (Exception e){

        }
        return convertView;
    }

    /**
     * 设置数据
     */
    public void setNewData( List<SearchAutoResult> autoList) {
        this.remmonedDataBeen=autoList;
        this.notifyDataSetChanged();
    }

    private class ViewHolder{
        private ImageView iv_item_icon;
        private ImageView iv_item_delete;
        private TextView  tv_item_title;
    }
}
