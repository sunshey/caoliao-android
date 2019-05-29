package com.yc.liaolive.util;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TinyHung@outlook.com
 * 2017/6/27 15:49
 * 将输入框中的content转义成带#XX#的文本内容
 */
public class EditTextTopicSpan {

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
	 * @return
	 */
	public static SpannableString getTopicStyleContent(String source, int color) {

		if(TextUtils.isEmpty(source)){
			return null;
		}
		SpannableString spannableString = new SpannableString(source);

		// 设置正则
		Pattern pattern = Pattern.compile(REGEX);
		Matcher matcher = pattern.matcher(spannableString);

		if (matcher.find()) {
			// 重置正则位置
			matcher.reset();
		}
		while (matcher.find()) {
			// 根据group的括号索引，可得出具体匹配哪个正则(0代表全部，1代表第一个括号)
			final String topic = matcher.group(2);
			// 处理话题##符号
			if (topic != null) {
				int start = matcher.start(2);
				int end = start + topic.length();
				TextClickSpan clickableSpan = new TextClickSpan(color) {

					@Override
					public void onClick(View widget) {

					}
				};
				spannableString.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
		return spannableString;
	}
}
