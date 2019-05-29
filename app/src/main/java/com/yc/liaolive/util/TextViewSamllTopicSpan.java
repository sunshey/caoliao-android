package com.yc.liaolive.util;

import android.graphics.Bitmap;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.interfaces.TopicClickListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TinyHung@outlook.com
 * 2017/6/27 15:49
 * 这是一个处理包含 #XXXX# @XXX httpxxx 等关键字的辅助类
 */
public class TextViewSamllTopicSpan {

	// 定义正则表达式
	private static final String AT = "@[\u4e00-\u9fa5\\w]+";// @人
	private static final String TOPIC = "#[\u4e00-\u9fa5\\w]+#";// ##话题
	private static final String EMOJI = "\\[[\u4e00-\u9fa5\\w]+\\]";// 表情
	private static final String URL = "http://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";// url
	private static final String REGEX = "(" + AT + ")|(" + TOPIC + ")|("
			+ EMOJI + ")|(" + URL + ")";

	/**
	 * 设置内容样式
	 * @param source
	 * @param textView
	 * @param topicClickListener
	 * @param userID 这个不需要的低档可以为空
	 * @return
	 */
	public static SpannableString getTopicStyleContent(String source, int color, TextView textView, final TopicClickListener topicClickListener, final String userID) {

		if(TextUtils.isEmpty(source)){
			return null;
		}

		int tvSize=10;
		if(ScreenUtils.getScreenHeight()>=1920){
			tvSize=20;
		} else if(ScreenUtils.getScreenHeight()>=1280){
			tvSize=15;
		}

		SpannableString spannableString = new SpannableString(source);

		// 设置正则
		Pattern pattern = Pattern.compile(REGEX);
		Matcher matcher = pattern.matcher(spannableString);

		if (matcher.find()) {
			// 要实现文字的点击效果，这里需要做特殊处理
			textView.setMovementMethod(LinkMovementMethod.getInstance());
			// 重置正则位置
			matcher.reset();
		}

		while (matcher.find()) {
			// 根据group的括号索引，可得出具体匹配哪个正则(0代表全部，1代表第一个括号)
			final String at = matcher.group(1);
			final String topic = matcher.group(2);
			String emoji = matcher.group(3);
			final String url = matcher.group(4);

			// 处理@符号
			if (at != null) {
				// 获取匹配位置
				int start = matcher.start(1);
				int end = start + at.length();
				TextSamllClickSpan clickableSpan = new TextSamllClickSpan(color) {
					@Override
					public void onClick(View widget) {
						// TODO: 2017/6/27 待传入点击@人物后的监听
						if(null!=topicClickListener&&!TextUtils.isEmpty(userID)){
							topicClickListener.onAuthoeClick(userID);
						}
					}
				};
				spannableString.setSpan(clickableSpan, start, end,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}

			// 处理话题##符号
			if (topic != null) {
				int start = matcher.start(2);
				int end = start + topic.length();
				TextSamllClickSpan clickableSpan = new TextSamllClickSpan(color) {

					@Override
					public void onClick(View widget) {
						if(null!=topicClickListener){
							topicClickListener.onTopicClick(topic);
						}
					}
				};
				spannableString.setSpan(clickableSpan, start, end,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}

			if (emoji != null) {
				int start = matcher.start(3);
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

			// 处理url地址
			if (url != null) {
				int start = matcher.start(4);
				int end = start + url.length();
				TextSamllClickSpan clickableSpan = new TextSamllClickSpan(color) {
					@Override
					public void onClick(View widget) {
						// TODO: 2017/6/27 待传入点击网址的监听  url
						if(null!=topicClickListener){
							topicClickListener.onUrlClick(url);
						}
					}
				};
				spannableString.setSpan(clickableSpan, start, end,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
		return spannableString;
	}
}
