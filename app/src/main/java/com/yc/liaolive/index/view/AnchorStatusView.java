package com.yc.liaolive.index.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.yc.liaolive.R;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.util.Utils;

/**
 * 主播在线状态标签
 * Created by yangxueqin on 2018/12/4.
 */

public class AnchorStatusView extends LinearLayout{
    private View dot;
    private TextView textView;
    public AnchorStatusView(Context context) {
        super(context);
        initView();
    }

    public AnchorStatusView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView () {
        this.removeAllViews();
        this.setOrientation(LinearLayout.HORIZONTAL);
        this.setGravity(Gravity.CENTER_VERTICAL);
        this.setPadding(Utils.dip2px(4), Utils.dip2px(1),
                Utils.dip2px(4), Utils.dip2px(1));
        this.setBackgroundResource(R.drawable.common_black20_icon_bg);

        dot = new View(getContext());
        dot.setBackgroundResource(R.drawable.arice_gray_dot);
        this.addView(dot, new LayoutParams(Utils.dip2px(4), Utils.dip2px(4)));

        textView = new TextView(getContext());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        textView.setPadding(Utils.dip2px(3), 0, 0, 0);
        textView.setIncludeFontPadding(false);
        textView.setTextColor(Color.parseColor("#ffffff"));
        this.addView(textView);
        this.setVisibility(View.INVISIBLE);
    }

    public void setData (String user_state, int chat_time) {
        if (!TextUtils.isEmpty(user_state)){
            this.setVisibility(View.VISIBLE);
//            GradientDrawable bg = (GradientDrawable) this.getBackground();
            String color;
            String textString;
            //离线:user_state-->offline itemCategory-->type_offline
            if (TextUtils.equals(Constant.USER_STATE_OFLINE, user_state)
                    || TextUtils.equals(Constant.INDEX_ITEM_TYPE_OFFLINE, user_state)){
                color = "#51b5fb";
                textString = "离线";
            } else //直播中:user_state-->live  直播间直播中:itemCategory-->type_room
                if (TextUtils.equals(Constant.USER_STATE_LIVE, user_state)
                        || TextUtils.equals(Constant.INDEX_ITEM_TYPE_ROOM, user_state)){
                    color = "#fb51c6";
                    textString = "直播中";
                } else //在聊:user_state-->videocall  itemCategory-->type_videocall
                    if (TextUtils.equals(Constant.USER_STATE_VIDEOCALL, user_state)
                            || TextUtils.equals(Constant.INDEX_ITEM_TYPE_VIDEOCALL, user_state)){
                        if (chat_time >= 5) {
                            color = "#ff9630";
                            textString = "在聊>5分钟";
                        } else {
                            color = "#fb516d";
                            textString = chat_time >= 1 ? "在聊>1分钟" : "在聊";
                        }
                    } else //勿扰:user_state-->disturbed  itemCategory-->type_quite
                        if (TextUtils.equals(Constant.USER_STATE_DISTURBED, user_state)
                                || TextUtils.equals(Constant.INDEX_ITEM_TYPE_QUITE, user_state)){
                            color = "#a0a0a0";
                            textString = "勿扰";
                        } else //在线、空闲:user_state-->free  实际显示在线:itemCategory-->type_free
                            if (TextUtils.equals(Constant.USER_STATE_FREE, user_state)
                                    || TextUtils.equals(Constant.INDEX_ITEM_TYPE_FREE, user_state)){
                                color = "#3af978";
                                textString = "在线";
                            } else {
                                //识别不到的:离线
                                color = "#51b5fb";
                                textString = "离线";
                            }
//        } else {
//            color = "#51b5fb";
//            textString = "离线";

//            bg.setStroke(Utils.dip2px(0.67f), Color.parseColor(color));

            GradientDrawable dotDrawable = (GradientDrawable) dot.getBackground();
            dotDrawable.setColor(Color.parseColor(color));

            textView.setText(textString);
//            textView.setTextColor(Color.parseColor(color));
        } else {
            this.setVisibility(View.INVISIBLE);
        }
    }
}
