package com.yc.liaolive.live.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.live.bean.CustomMsgInfo;
import com.yc.liaolive.live.listener.OnSpannableClickListener;
import com.yc.liaolive.live.listener.OnSpannableUserClickListener;
import com.yc.liaolive.live.mode.SpannableTexViewClick;
import com.yc.liaolive.live.mode.UserModelUtil;
import com.yc.liaolive.util.EmotionUtils;
import com.yc.liaolive.util.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TinyHung@outlook.com
 * 2017/5/20
 * 直播间聊天内容用户等级格式化辅助类
 */

public class LiveChatUserGradleSpan {

    private static final String TAG = "LiveChatUserGradleSpan";
    //自定义正则表达式
    private static final String NOTIFY = "#notify#[\u4e00-\u9fa5\\w]+#notify#";//通知
    private static final String GRADLE = "#gradle#[\u4e00-\u9fa5\\w]+#gradle#";//等级
    private static final String HIGHT_LIGHT = "#hl#[\u4e00-\u9fa5\\w]+#hl#";//高亮显示
    private static final String LIVE_ANCHOR = "#anchor#[\u4e00-\u9fa5\\w]+#anchor#";//主播
    private static final String AUTH = "#auth#[\u4e00-\u9fa5\\w]+#auth#";//官方
    private static final String USER = "#user#[\u4e00-\u9fa5\\w]+#user#";//用户
    private static final String EMOJI = "\\[[\u4e00-\u9fa5\\w]+\\]";// 表情

    private static String REGEX = "(" + NOTIFY + ")|(" + GRADLE + ")|(" + HIGHT_LIGHT + ")|(" + LIVE_ANCHOR + ")|(" + AUTH + ")|(" + USER + ")|(" + EMOJI + ")";

    /**
     * 将字符串中包含表情的内容解析成表情图片
     *
     * @param spanned
     * @return
     */
    public static SpannableString stringFormatEmoji(Spanned spanned, TextView textView) {
        SpannableString spannableString = new SpannableString(spanned);
        Pattern pattern = Pattern.compile("(" + EMOJI + ")");
        Matcher matcher = pattern.matcher(spannableString);
        while (matcher.find()) {
            String emoji = matcher.group(1);
            int start = matcher.start(1);
            int end = start + emoji.length();
            Bitmap bitmap = EmotionUtils.getInstance().getBitmapByAssets(EmotionUtils.getInstance().getImgByName(emoji));
            if (bitmap != null) {
                // 压缩Bitmap
                int textSize = (int) textView.getTextSize();
//                bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(),bitmap.getHeight(), true);
                bitmap = Bitmap.createScaledBitmap(bitmap, textSize+10,textSize+10, true);
                // 设置表情
                ImageSpan imageSpan = new ImageSpan(VideoApplication.getInstance().getApplicationContext(), bitmap);
                spannableString.setSpan(imageSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spannableString;
    }

    /**
     * 字符串部分点击事件格式化
     *
     * @param spanned
     * @param regex
     * @param textView
     * @param clickListener
     * @return
     */
    public static SpannableString stringFormat(Spanned spanned, String regex, TextView textView, int color, final OnSpannableUserClickListener clickListener) {
        SpannableString spannableString = new SpannableString(spanned);
        Pattern pattern = Pattern.compile("(" + regex + ")");
        Matcher matcher = pattern.matcher(spannableString);
        if (matcher.find()) {
            // 要实现文字的点击效果，这里需要做特殊处理
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            // 重置正则位置
            matcher.reset();
        }
        while (matcher.find()) {
            String group = matcher.group(1);
            if (null != group) {
                int start = matcher.start(1);
                int end = start + group.length();
                SpannableTexViewClick clickableSpan = new SpannableTexViewClick(color) {
                    @Override
                    public void onClick(View widget) {
                        if (null != clickListener) {
                            clickListener.onClick(null);
                        }
                    }
                };
                spannableString.setSpan(clickableSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }
        return spannableString;
    }

    /**
     * 字符串部分点击事件格式化
     *
     * @param spanned
     * @param regex
     * @param textView
     * @param clickListener
     * @param isUnderline   是否显示下划线
     * @return
     */
    public static SpannableString stringFormat(String spanned, String regex, TextView textView, int color, boolean isUnderline, final OnSpannableUserClickListener clickListener) {
        SpannableString spannableString = new SpannableString(spanned);
        Pattern pattern = Pattern.compile("(" + regex + ")");
        Matcher matcher = pattern.matcher(spannableString);
        if (matcher.find()) {
            // 要实现文字的点击效果，这里需要做特殊处理
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            // 重置正则位置
            matcher.reset();
        }
        while (matcher.find()) {
            String group = matcher.group(1);
            if (null != group) {
                int start = matcher.start(1);
                int end = start + group.length();
                SpannableTexViewClick clickableSpan = new SpannableTexViewClick(color, isUnderline) {
                    @Override
                    public void onClick(View widget) {
                        if (null != clickListener) {
                            clickListener.onClick(null);
                        }
                    }
                };
                spannableString.setSpan(clickableSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }
        return spannableString;
    }

    /**
     * 将一条礼物消息解析成富文本显示
     *
     * @return
     */
    public static SpannableString getSpannableDrawableFotGift(CustomMsgInfo msgInfo) {
        if (null == msgInfo) return new SpannableString("");
        if (null == msgInfo.getGift()) return new SpannableString("");
        boolean isVip = false;//是否是会员
        String userGradle = "#gradle#" + msgInfo.getSendUserGradle() + "#gradle#";
        if (msgInfo.getSendUserVIP() > 0) {
            isVip = true;
            userGradle = "#gradle#" + msgInfo.getSendUserVIP() + "#gradle#";
        }
        String context = "#notify#通知#notify# " + "<font color='#FFEDA6'>【" + msgInfo.getSendUserName()
                + "】</font><font color='#FFFFFF'>给</font>"
                + "<font color='#FFF000'>【" + msgInfo.getAccapUserName() + "】</font>"
                + "<font color='#FFFFFF'>送出了" + msgInfo.getGift().getCount()
                + "个</font>" + "<font color='#FF0000'>【" + msgInfo.getGift().getTitle() + "】</font>";
        SpannableString spannableString = new SpannableString(Html.fromHtml(context));
        Pattern pattern = Pattern.compile("(" + NOTIFY + ")|(" + GRADLE + ")");
        Matcher matcher = pattern.matcher(spannableString);
        //遍历整条语句找出匹配的项，替换图片
        while (matcher.find()) {
            String notify = matcher.group(1);
            String gradle = matcher.group(2);
            //通知图标
            if (notify != null) {
                // 获取匹配位置
                int start = matcher.start(1);
                int end = start + notify.length();
                Drawable drawable = UserModelUtil.getDrawable(UserModelUtil.REGEX_NOTIFY);
                if (null != drawable) {
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
                    spannableString.setSpan(imageSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                }
            }
            //用户等级
            if (gradle != null) {
                int start = matcher.start(2);
                int end = start + gradle.length();
                String substring = gradle.substring(8, gradle.length() - 8);
                Drawable drawable = AppEngine.getApplication().getResources().getDrawable(UserModelUtil.getUserGradleRes(Integer.parseInt(substring)));
                if (isVip) {
                    drawable = AppEngine.getApplication().getResources().getDrawable(UserModelUtil.getUserVipGradleRes(Integer.parseInt(substring)));
                }
                if (null != drawable) {
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
                    spannableString.setSpan(imageSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                }
            }
        }
        return spannableString;
    }


    /**
     * 将一条中奖消息解析成富文本显示
     *
     * @return
     */
    public static SpannableString getSpannableByDrawTips(CustomMsgInfo msgInfo) {
        if (null == msgInfo) return new SpannableString("");
        if (null == msgInfo.getGift()) return new SpannableString("");
        boolean isVip = false;//是否是会员
        String userGradle = "#gradle#" + msgInfo.getSendUserGradle() + "#gradle#";
        if (msgInfo.getSendUserVIP() > 0) {
            isVip = true;
            userGradle = "#gradle#" + msgInfo.getSendUserVIP() + "#gradle#";
        }
        String context = "恭喜 "
                + " <font color='#FFFF00'>" + msgInfo.getSendUserName() + "</font>"
                + "<font color='#FFFFFF'>送出了" + msgInfo.getGift().getCount() + "个</font>"
                + "<font color='#FFCC00'>" + msgInfo.getGift().getTitle() + "</font>"
                + "中了<font color='#FFCC00'>" + msgInfo.getGift().getDrawTimes() + "</font>倍超级大奖获得"
                + "<font color='#FFCC00'>" + msgInfo.getGift().getDrawIntegral() + "</font>积分";
        SpannableString spannableString = new SpannableString(Html.fromHtml(context));
        Pattern pattern = Pattern.compile("(" + GRADLE + ")");
        Matcher matcher = pattern.matcher(spannableString);
        //遍历整条语句找出匹配的项，替换图片
        while (matcher.find()) {
            String gradle = matcher.group(1);

            //用户等级
            if (gradle != null) {
                int start = matcher.start(1);
                int end = start + gradle.length();
                String substring = gradle.substring(8, gradle.length() - 8);
                Drawable drawable = AppEngine.getApplication().getResources().getDrawable(UserModelUtil.getUserGradleRes(Integer.parseInt(substring)));
                if (isVip) {
                    drawable = AppEngine.getApplication().getResources().getDrawable(UserModelUtil.getUserVipGradleRes(Integer.parseInt(substring)));
                }
                if (null != drawable) {
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
                    spannableString.setSpan(imageSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                }
            }
        }
        return spannableString;
    }


    /**
     * 会员进场
     *
     * @return
     */
    public static SpannableString getSpannableByVipUserEnter(CustomMsgInfo msgInfo) {
        if (null == msgInfo) return new SpannableString("");
        if (null == msgInfo.getGift()) return new SpannableString("");
        if (msgInfo.getSendUserVIP() <= 0) return new SpannableString("");
        String context = "<font color='#FFEC1E'>【" + msgInfo.getSendUserName() + "】</font>"
                        + "进入房间,掌声响起来!";
        SpannableString spannableString = new SpannableString(Html.fromHtml(context));
        Pattern pattern = Pattern.compile("(" + GRADLE + ")");
        Matcher matcher = pattern.matcher(spannableString);
        //遍历整条语句找出匹配的项，替换图片
        while (matcher.find()) {
            String gradle = matcher.group(1);
            //用户等级
            if (gradle != null) {
                int start = matcher.start(1);
                int end = start + gradle.length();
                String substring = gradle.substring(8, gradle.length() - 8);
//                UserModelUtil.getUserVipGradleRes(Integer.parseInt(substring))
                Drawable drawable = AppEngine.getApplication().getResources().getDrawable(R.drawable.ic_vip_icon);
                if (null != drawable) {
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
                    spannableString.setSpan(imageSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                }
            }
        }
        return spannableString;
    }


    /**
     * 将礼物数字替换成图片显示
     *
     * @param content 数字内容，内容必须是 int 类型字符串
     * @return
     */
    public static SpannableStringBuilder giftNumFromat(String content) {
        if (null == content) return null;
        char[] chars = content.toCharArray();
        SpannableStringBuilder stringBuilde = new SpannableStringBuilder();

        try {
            for (char aChar : chars) {
                SpannableString stringSpannable = new SpannableString(String.valueOf(aChar));
                Drawable drawable = AppEngine.getApplication().getResources().getDrawable(UserModelUtil.getNumCount(Integer.parseInt(String.valueOf(aChar))));
                if (null != drawable) {
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
                    stringSpannable.setSpan(imageSpan, 0, String.valueOf(aChar).length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                }
                stringBuilde.append(stringSpannable);
            }
            return stringBuilde;
        } catch (RuntimeException e) {
        }
        return null;
    }


    /**
     * 将礼物数字替换成图片显示
     * @param content 数字内容，内容必须是 int 类型字符串
     * @return
     */
    public static SpannableStringBuilder giftSendNumFromat(String content) {
        if (null == content) return null;
        char[] chars = content.toCharArray();
        SpannableStringBuilder stringBuilde = new SpannableStringBuilder();
        try {
            for (char aChar : chars) {
                SpannableString stringSpannable = new SpannableString(String.valueOf(aChar));
                Drawable drawable = AppEngine.getApplication().getResources().getDrawable(UserModelUtil.giftSendNumFromat(Integer.parseInt(String.valueOf(aChar))));
                if (null != drawable) {
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
                    stringSpannable.setSpan(imageSpan, 0, String.valueOf(aChar).length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                }
                stringBuilde.append(stringSpannable);
            }
            return stringBuilde;
        } catch (RuntimeException e) {
        }
        return null;
    }

    /**
     * 将终将动画中的礼物数字替换成图片显示
     *
     * @param content 数字内容，内容必须是 int 类型字符串
     * @return
     */
    public static SpannableStringBuilder drawNumFromat(String content) {
        if (null == content) return null;
        char[] chars = content.toCharArray();
        SpannableStringBuilder stringBuilde = new SpannableStringBuilder();
        try {
            for (char aChar : chars) {
                SpannableString stringSpannable = new SpannableString(String.valueOf(aChar));
                Drawable drawable = AppEngine.getApplication().getResources().getDrawable(UserModelUtil.getDrawNumCount(Integer.parseInt(String.valueOf(aChar))));
                if (null != drawable) {
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
                    stringSpannable.setSpan(imageSpan, 0, String.valueOf(aChar).length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                }
                stringBuilde.append(stringSpannable);
            }
            return stringBuilde;
        } catch (RuntimeException e) {
        }
        return null;
    }

    /**
     * 倍率转化
     *
     * @param content
     * @return
     */
    public static SpannableString drawPowerFromat(String content) {
        SpannableString stringSpannable = new SpannableString(content);
        Drawable drawable = AppEngine.getApplication().getResources().getDrawable(R.drawable.ic_draw_text);
        if (null != drawable) {
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
            stringSpannable.setSpan(imageSpan, 0, stringSpannable.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }
        return stringSpannable;
    }


    /**
     * 根据业务需求，封装的针对TextView上显示复杂内容，图文并排
     *
     * @param spanned       已经参考 REGEX 规则封装好的字符串
     * @param userID        userID和clickListener必须同时不为空
     * @param textView
     * @param clickListener 和userID必须同时不为空
     * @return 注意：严格参照上述自定义正则表达式，否则输出为普通的文本
     */
    public static SpannableString getSpannableDrawableText(Spanned spanned, String regex, final String userID, TextView textView, final OnSpannableUserClickListener clickListener) {
        if (TextUtils.isEmpty(spanned)) {
            return null;
        }
        SpannableString spannableString = new SpannableString(spanned);
        // 设置正则
        if (regex != null) {
            REGEX = REGEX + "|(" + regex + ")";//高亮部分需要实现点击事件,添加此规则
        }
        Pattern pattern = Pattern.compile(REGEX);
        Matcher matcher = pattern.matcher(spannableString);
        if (matcher.find()) {
            // 要实现文字的点击效果，这里需要做特殊处理
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            // 重置正则位置
            matcher.reset();
        }
        //遍历整条语句找出匹配的项，替换图片
        while (matcher.find()) {
            String notify = matcher.group(1);
            String gradle = matcher.group(2);
            String hightLight = matcher.group(3);
            String liveAnchor = matcher.group(4);
            String auth = matcher.group(5);
            String user = matcher.group(6);
            String emoji = matcher.group(7);
            //通知
            if (notify != null) {
                // 获取匹配位置
                int start = matcher.start(1);
                int end = start + notify.length();
                Bitmap bitmap = BitmapFactory.decodeResource(VideoApplication.getInstance().getApplicationContext().getResources(), R.drawable.ic_home_notic);
                if (bitmap != null) {
                    // 压缩Bitmap
                    bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
                    // 设置表情
                    ImageSpan imageSpan = new ImageSpan(VideoApplication.getInstance().getApplicationContext(), bitmap);
                    spannableString.setSpan(imageSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
//				Drawable drawable=UserModelUtil.getDrawable(UserModelUtil.REGEX_NOTIFY);
//				if(null!=drawable){
//					drawable.setBounds(0, 15, drawable.getIntrinsicWidth()+10, drawable.getIntrinsicHeight()+10);
//					ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
//					spannableString.setSpan(imageSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//				}
            }
            //等级
            if (gradle != null) {
                int start = matcher.start(2);
                int end = start + gradle.length();
                String substring = gradle.substring(8, gradle.length() - 8);
                Bitmap bitmap = BitmapFactory.decodeResource(VideoApplication.getInstance().getApplicationContext().getResources(), UserModelUtil.getUserGradleRes(Integer.parseInt(substring)));
                if (bitmap != null) {
                    // 压缩Bitmap
                    bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
                    // 设置表情
                    ImageSpan imageSpan = new ImageSpan(VideoApplication.getInstance().getApplicationContext(), bitmap);
                    spannableString.setSpan(imageSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            //高亮
            if (hightLight != null) {
                int start = matcher.start(3);
                int end = start + hightLight.length();
                String substring = hightLight.substring(4, hightLight.length() - 4);
                SpannableString newSpannableString = new SpannableString(substring);
                ForegroundColorSpan span = new ForegroundColorSpan(Color.parseColor("#FFEDA6"));
                newSpannableString.setSpan(span, 0, substring.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(span, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }
            // 主播
            if (liveAnchor != null) {
                int start = matcher.start(4);
                int end = start + liveAnchor.length();
                Bitmap bitmap = BitmapFactory.decodeResource(VideoApplication.getInstance().getApplicationContext().getResources(), R.drawable.ic_mine_service);
                if (bitmap != null) {
                    // 压缩Bitmap
                    bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
                    // 设置表情
                    ImageSpan imageSpan = new ImageSpan(VideoApplication.getInstance().getApplicationContext(), bitmap);
                    spannableString.setSpan(imageSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            //官方
            if (auth != null) {
                int start = matcher.start(5);
                int end = start + auth.length();
                Bitmap bitmap = BitmapFactory.decodeResource(VideoApplication.getInstance().getApplicationContext().getResources(), R.drawable.im_lv_official);
                if (bitmap != null) {
                    // 压缩Bitmap
                    bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
                    // 设置表情
                    ImageSpan imageSpan = new ImageSpan(VideoApplication.getInstance().getApplicationContext(), bitmap);
                    spannableString.setSpan(imageSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            //用户
            if (user != null) {
                int start = matcher.start(6);
                int end = start + user.length();
                String newContent = user.substring(6, user.length() - 6);
                SpannableString newSpannableString = new SpannableString(newContent);
                ForegroundColorSpan span = new ForegroundColorSpan(Color.parseColor("#FFEDA6"));
                //设置颜色
                spannableString.setSpan(span, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                ////设置点击事件
                if (null != clickListener && !TextUtils.isEmpty(userID)) {
                    SpannableTexViewClick clickableSpan = new SpannableTexViewClick(Color.parseColor("#FFEDA6")) {
                        @Override
                        public void onClick(View widget) {
                            clickListener.onClick(userID);
                        }
                    };
                    spannableString.setSpan(clickableSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                }
                //最后替换整个匹配到的文本和设置的属性
                spannableString.setSpan(newSpannableString, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            //表情
            if (emoji != null) {
                int start = matcher.start(7);
                int end = start + emoji.length();
                Bitmap bitmap = EmotionUtils.getInstance().getBitmapByAssets(EmotionUtils.getInstance().getImgByName(emoji));
                if (bitmap != null) {
                    // 压缩Bitmap
                    bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(),bitmap.getHeight(), true);
                    // 设置表情
                    ImageSpan imageSpan = new ImageSpan(VideoApplication.getInstance().getApplicationContext(), bitmap);
                    spannableString.setSpan(imageSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }

            //需要设置点击事件的正则部分，抽出来设置点击事件，暂时不想写多个部分点击事件，请参考本类中spannableFromat()方法
            if (null != regex) {
                String regexContext = matcher.group(8);
                if (null != regexContext) {
                    int start = matcher.start(8);
                    int end = start + regexContext.length();
                    ////设置点击事件
                    if (null != clickListener && !TextUtils.isEmpty(userID)) {
                        SpannableTexViewClick clickableSpan = new SpannableTexViewClick(Color.parseColor("#FFEDA6")) {
                            @Override
                            public void onClick(View widget) {
                                clickListener.onClick(userID);
                            }
                        };
                        spannableString.setSpan(clickableSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    }
                }
            }
        }
        return spannableString;
    }

    /**
     * 格式化Html文字内容+SpannableString 处理局部点击事件或者其他Style
     *
     * @param htmlContent
     * @param regexParams   key：正则表达式，也是高亮显示的内容，vaule：是触发高亮文字内容点击事件的时候，回传给监听器的参数，如果varlue内容为空，则点击事件失效
     * @param textView      View
     * @param clickListener 点击监听器(想要实现高亮内容点击事件时实现)
     * @return
     */
    public static SpannableString spannableFromat(String htmlContent, Map<String, Object> regexParams, TextView textView, final OnSpannableClickListener clickListener) {
        Spanned spanned = Html.fromHtml(htmlContent);
        SpannableString spannableString = new SpannableString(spanned);
        if (null != regexParams && regexParams.size() > 0) {
            StringBuilder regrxBuilder = new StringBuilder();
            Iterator<Map.Entry<String, Object>> iterator = regexParams.entrySet().iterator();
            int regexCount = 0;//这个记录正则匹配规则的数量
            while (iterator.hasNext()) {
                Map.Entry<String, Object> next = iterator.next();
                //第一个regrx
                if (0 == regexCount) {
                    regrxBuilder.append("(" + next.getKey() + ")");
                } else {
                    regrxBuilder.append("|(" + next.getKey() + ")");
                }
                regexCount++;
            }
            if (null != regrxBuilder && regrxBuilder.length() > 0) {
                Pattern pattern = Pattern.compile(regrxBuilder.toString());
                Matcher matcher = pattern.matcher(spannableString);
                if (matcher.find()) {
                    textView.setMovementMethod(LinkMovementMethod.getInstance());
                    matcher.reset();
                }
                while (matcher.find()) {
                    int index = 1;
                    //break+index 循环不会重复
                    for (int i = index; i <= regexCount; i++) {
                        String groupRegex = matcher.group(i);
                        if (null != groupRegex) {
                            int start = matcher.start(index);
                            int end = start + groupRegex.length();
                            while (iterator.hasNext()) {
                                final Map.Entry<String, Object> next = iterator.next();
                                if (next.getKey().equals(groupRegex) && null != next.getValue()) {
                                    SpannableTexViewClick clickableSpan = new SpannableTexViewClick() {
                                        @Override
                                        public void onClick(View widget) {
                                            if (null != clickListener) {
                                                clickListener.onClick(next.getKey());
                                            }
                                        }
                                    };
                                    spannableString.setSpan(clickableSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                }
                            }
                            index++;
                            break;
                        }
                    }
                }
                return spannableString;
            }
        }
        return spannableString;
    }


    /**
     * 获取本地资源图片
     *
     * @param source
     * @return
     */
    public Drawable getDrawable(String source) {
        Drawable drawable = null;
        InputStream is = null;
        try {
            is = VideoApplication.getInstance().getApplicationContext().getResources().getAssets().open(source);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            TypedValue typedValue = new TypedValue();
            typedValue.density = TypedValue.DENSITY_DEFAULT;
            drawable = Drawable.createFromResourceStream(null, typedValue, is, "src");
            DisplayMetrics dm = VideoApplication.getInstance().getApplicationContext().getResources().getDisplayMetrics();
            int dwidth = dm.widthPixels - 10;//padding left + padding right
            float dheight = (float) drawable.getIntrinsicHeight() * (float) dwidth / (float) drawable.getIntrinsicWidth();
            int dh = (int) (dheight + 0.5);
            int wid = dwidth;
            int hei = dh;
            drawable.setBounds(0, 0, wid, hei);
            return drawable;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }

    }

    /**
     * 获取网络图片
     */
    private class MImageGetter implements Html.ImageGetter {

        Context context;
        TextView container;

        public MImageGetter(TextView text, Context c) {
            this.context = c;
            this.container = text;
        }

        public Drawable getDrawable(String source) {
            if (null == container) return null;
            final LevelListDrawable drawable = new LevelListDrawable();
            Glide.with(context).load(source).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    if (resource != null) {
                        BitmapDrawable bitmapDrawable = new BitmapDrawable(resource);
                        drawable.addLevel(1, 1, bitmapDrawable);
                        drawable.setBounds(0, 0, resource.getWidth(), resource.getHeight());
                        drawable.setLevel(1);
                        container.invalidate();
                        container.setText(container.getText());
                    }
                }
            });
            return drawable;
        }
    }
}
