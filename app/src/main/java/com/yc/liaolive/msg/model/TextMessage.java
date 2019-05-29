package com.yc.liaolive.msg.model;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.kaikai.securityhttp.utils.LogUtil;
import com.tencent.TIMElem;
import com.tencent.TIMElemType;
import com.tencent.TIMFaceElem;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageDraft;
import com.tencent.TIMTextElem;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.R;
import com.yc.liaolive.live.util.LiveChatUserGradleSpan;
import com.yc.liaolive.msg.adapter.ChatAdapter;
import com.yc.liaolive.msg.manager.FriendManager;
import com.yc.liaolive.user.manager.UserManager;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 文本消息数据
 */

public class TextMessage extends Message {

    public TextMessage(TIMMessage message) {
        this.message = message;
    }

    public TextMessage(String s) {
        message = new TIMMessage();
        TIMTextElem elem = new TIMTextElem();
        elem.setText(s);
        message.addElement(elem);
    }

    public TextMessage(TIMMessageDraft draft) {
        message = new TIMMessage();
        for (TIMElem elem : draft.getElems()) {
            message.addElement(elem);
        }
    }

    private List<ImageSpan> sortByIndex(final Editable editInput, ImageSpan[] array) {
        ArrayList<ImageSpan> sortList = new ArrayList<>();
        for (ImageSpan span : array) {
            sortList.add(span);
        }
        Collections.sort(sortList, new Comparator<ImageSpan>() {
            @Override
            public int compare(ImageSpan lhs, ImageSpan rhs) {
                return editInput.getSpanStart(lhs) - editInput.getSpanStart(rhs);
            }
        });

        return sortList;
    }

    public TextMessage(Editable s) {
        message = new TIMMessage();
        TIMTextElem textElem = new TIMTextElem();
        textElem.setText(s.toString());
        message.addElement(textElem);
    }

    /**
     * 在聊天界面显示消息
     *
     * @param viewHolder 界面样式
     * @param context    显示消息的上下文
     */
    @Override
    public void showMessage(ChatAdapter.ViewHolder viewHolder, final Context context) {
        clearView(viewHolder);
        boolean hasText = false;
        TextView tv = new TextView(AppEngine.getApplication());
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        tv.setPadding(3, 0, 3, 0);
        tv.setTextColor(AppEngine.getApplication().getResources().getColor(isSelf() ? R.color.black : R.color.black));
        List<TIMElem> elems = new ArrayList<>();
        for (int i = 0; i < message.getElementCount(); ++i) {
            elems.add(message.getElement(i));
            if (message.getElement(i).getType() == TIMElemType.Text) {
                hasText = true;
            }
        }
        SpannableStringBuilder stringBuilder = getString(elems, context);
        if (!hasText) {
            stringBuilder.insert(0, " ");
        }

        viewHolder.rightAvatar.setVisibility(View.VISIBLE);
        viewHolder.leftAvatar.setVisibility(View.VISIBLE);

        Glide.with(context).load(UserManager.getInstance().getAvatar()).error(R.drawable.ic_default_user_head).into(viewHolder.rightAvatar);

        FriendProfile profile = FriendshipInfo.getInstance().getProfile(message.getSender());
        if (profile == null) {
            profile = FriendManager.getInstance().getFriendShipsById(message.getSender());
        }

        if (profile != null) {
            Glide.with(context).load(profile.getAvatarUrl()).error(R.drawable.ic_default_user_head).into(viewHolder.leftAvatar);
        } else {
            viewHolder.leftAvatar.setImageResource(R.drawable.ic_default_user_head);
        }
        SpannableString spannableString = LiveChatUserGradleSpan.stringFormatEmoji(Html.fromHtml(TextUtils.isEmpty(stringBuilder) ? "[未知消息]" : stringBuilder.toString()), tv);

        tv.setText(spannableString);

        RelativeLayout.MarginLayoutParams leftLayoutParams = (RelativeLayout.MarginLayoutParams) viewHolder.item_left_user_icon_view.getLayoutParams();

        leftLayoutParams.topMargin = 0;
        viewHolder.item_left_user_icon_view.setLayoutParams(leftLayoutParams);

        if (isSelf()) {
            getBubbleView(viewHolder).setBackgroundResource(R.drawable.ic_msg_item_right);
        } else {
            getBubbleView(viewHolder).setBackgroundResource(R.drawable.ic_msg_item_left);
        }
        getBubbleView(viewHolder).addView(tv);
        showStatus(viewHolder);
    }

    /**
     * 获取消息摘要
     */
    @Override
    public String getSummary() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < message.getElementCount(); ++i) {
            switch (message.getElement(i).getType()) {
                case Face:
                    TIMFaceElem faceElem = (TIMFaceElem) message.getElement(i);
                    byte[] data = faceElem.getData();
                    if (data != null) {
                        result.append(new String(data, Charset.forName("UTF-8")));
                    }
                    break;
                case Text:
                    TIMTextElem textElem = (TIMTextElem) message.getElement(i);
                    result.append(textElem.getText());
                    break;
            }

        }
        return result.toString();
    }

    /**
     * 保存消息或消息文件
     */
    @Override
    public void save() {

    }

    private static int getNumLength(int n) {
        return String.valueOf(n).length();
    }

    public static SpannableStringBuilder getString(List<TIMElem> elems, Context context) {

        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
        for (int i = 0; i < elems.size(); ++i) {
            switch (elems.get(i).getType()) {
                //此Face已被弃用
                case Face:
                    TIMFaceElem faceElem = (TIMFaceElem) elems.get(i);
                    int startIndex = stringBuilder.length();
                    try {
                        AssetManager am = context.getAssets();
                        InputStream is = am.open(String.format("emot/emoji_%d.png", faceElem.getIndex()));
                        LogUtil.msg("TAG:;" + faceElem.getIndex());
                        if (is == null) continue;
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        Matrix matrix = new Matrix();
                        int width = bitmap.getWidth();
                        int height = bitmap.getHeight();
                        matrix.postScale(1, 1);
                        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                                width, height, matrix, true);
                        ImageSpan span = new ImageSpan(context, resizedBitmap, ImageSpan.ALIGN_BASELINE);
                        stringBuilder.append(String.valueOf(faceElem.getIndex()));
                        stringBuilder.setSpan(span, startIndex, startIndex + getNumLength(faceElem.getIndex()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        is.close();
                    } catch (IOException e) {

                    }
                    break;
                case Text:
                    TIMTextElem textElem = (TIMTextElem) elems.get(i);
                    stringBuilder.append(textElem.getText());
                    break;
            }

        }
        return stringBuilder;
    }
}
