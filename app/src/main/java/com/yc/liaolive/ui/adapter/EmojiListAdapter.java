package com.yc.liaolive.ui.adapter;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import com.yc.liaolive.R;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.base.adapter.BaseViewHolder;
import com.yc.liaolive.bean.ChatEmoji;
import com.yc.liaolive.util.EmotionUtils;
import com.yc.liaolive.util.ScreenUtils;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2017/9/27.
 * 表情面板
 */

public class EmojiListAdapter extends BaseQuickAdapter<ChatEmoji,BaseViewHolder> {

    private final int mItemHeight;

    public EmojiListAdapter(int layoutResId, @Nullable List<ChatEmoji> data) {
        super(layoutResId, data);
        mItemHeight = ScreenUtils.getScreenWidth()/7;
    }

    @Override
    protected void convert(BaseViewHolder helper, ChatEmoji item) {
        if(null!=item){
            helper.getView(R.id.re_item_view).getLayoutParams().height=mItemHeight;
            ImageView itemIvFace = (ImageView) helper.getView(R.id.item_iv_face);
            Bitmap bitmap = EmotionUtils.getInstance().getBitmapByAssets(item.getAbsolutePath());
            if (null!=bitmap) {
                // 压缩Bitmap
                bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(),bitmap.getHeight(), true);
                itemIvFace.setImageBitmap(bitmap);
            }else{
                itemIvFace.setImageResource(R.drawable.ic_gift_board_money);
            }
            helper.itemView.setTag(item);
        }
    }
}
