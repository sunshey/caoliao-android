package com.yc.liaolive.mine.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.yc.liaolive.base.adapter.BaseMultiItemQuickAdapter;
import com.yc.liaolive.base.adapter.BaseViewHolder;
import com.yc.liaolive.bean.TabMineUserInfo;
import com.yc.liaolive.msg.view.ServerConversationLietLayout;
import com.yc.liaolive.view.widget.CommenItemLayout;
import com.yc.liaolive.view.widget.SwitchButton;
import java.util.List;

 /**
 * TinyHung@Outlook.com
 * 2019/1/24
 * 首页我的适配器
 */
public class IndexMineAdapter extends BaseMultiItemQuickAdapter<TabMineUserInfo, BaseViewHolder> {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     * @param data A new list is created out of this one to avoid mutable list
     */
    public IndexMineAdapter(List<TabMineUserInfo> data) {
        super(data);
        addItemType(TabMineUserInfo.ITEM_1, R.layout.re_index_mine_tab_item1);//普通条目
        addItemType(TabMineUserInfo.ITEM_2, R.layout.re_index_mine_tab_item2);//来电勿扰
        addItemType(TabMineUserInfo.ITEM_3, R.layout.re_index_mine_tab_item1);
        addItemType(TabMineUserInfo.ITEM_5, R.layout.re_index_mine_tab_item5);
        addItemType(TabMineUserInfo.ITEM_DEFAULT, R.layout.recyler_item_empty);
    }

    @Override
    protected void convert(BaseViewHolder helper, TabMineUserInfo item) {
        if (null != item) {
            helper.itemView.setTag(item);
            switch (item.getItemType()) {
                //普通条目
                case TabMineUserInfo.ITEM_1:
                case TabMineUserInfo.ITEM_3:
                    CommenItemLayout itemLayout = (CommenItemLayout) helper.getView(R.id.item_view_tab);
                    itemLayout.setItemTitle(item.getTitle());
                    itemLayout.setItemMoreTitle(TextUtils.isEmpty(item.getSub_title())?"":item.getSub_title());
                    itemLayout.setItemIcon(item.getIcon());
                    boolean showLine=false;
                    if(!TextUtils.isEmpty(item.getShow_line())){
                        showLine="1".equals(item.getShow_line())?true:false;
                    }
                    itemLayout.showLine(showLine,showLine);
                    break;
                //来电勿扰
                case TabMineUserInfo.ITEM_2:
                    Glide.with(mContext)
                            .load(item.getIcon())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .centerCrop()
                            .into(((ImageView) helper.getView(R.id.view_item_icon)));
                    ((TextView) helper.getView(R.id.view_tv_item_title)).setText(item.getTitle());
                    SwitchButton switchButton = (SwitchButton) helper.getView(R.id.item_switch_btn);
                    switchButton.setChecked(item.getQuite().equals("0")?true:false);
                    switchButton.setEnabled(false);
                    helper.getView(R.id.view_top_space).setVisibility(View.VISIBLE);
                    helper.getView(R.id.view_bottom_space).setVisibility(View.VISIBLE);
                    View tabLayout = helper.getView(R.id.item_tab_layout);
                    tabLayout.setTag(item.getQuite());
                    helper.addOnClickListener(R.id.item_tab_layout);
                    break;
                case TabMineUserInfo.ITEM_5:
                    ServerConversationLietLayout conversationLietLayout = (ServerConversationLietLayout) helper.getView(R.id.message_conversation_layout);
                    conversationLietLayout.setItemData(item);
                    break;
            }
        }
    }
}