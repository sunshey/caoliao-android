package com.yc.liaolive.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.bean.ChatEmoji;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TinyHung@outlook.com
 * 2018-07-22
 * 自定义表情的处理
 */

public class EmotionUtils {

	private static final String TAG = "EmotionUtils";
	//每页得分页大小
	private int pageSize = 20;
	private static EmotionUtils mInstance;
	//保存于内存中的表情HashMap
	private HashMap<String, String> emojiMap = new HashMap<String, String>();
	//保存于内存中的表情对象集合
	public List<ChatEmoji> emojis = new ArrayList<ChatEmoji>();
	//即时通讯使用得表情[key]
    public List<String> emoticonData=new ArrayList<>();
	//表情分页的结果集合
	public List<List<ChatEmoji>> emojiLists = new ArrayList<List<ChatEmoji>>();
	//资产目录管理者
	private AssetManager mManager;

    public static synchronized EmotionUtils getInstance(){
        synchronized (EmotionUtils.class){
            if(null==mInstance){
                mInstance=new  EmotionUtils();
            }
        }
        return mInstance;
    }

    /**
     * 初始化
     * @param context
     */
    public void init(Context context) {
        ParseData(FileUtils.getEmojiFile(context));
    }

	/**
	 * 添加表情
	 * @param context
	 * @param absolutePath
	 * @param spannableString
	 * @return
	 */
	public SpannableString addFace(Context context, String absolutePath, String spannableString,int textSize) {
		if (TextUtils.isEmpty(spannableString)) {
			return null;
		}
        if(null==mManager){
            mManager = context.getResources().getAssets();
        }
        try {
            InputStream stream = mManager.open("emot/" + absolutePath);
            if(null!=stream){
                Bitmap bitmap = BitmapFactory.decodeStream(stream);
                if(null!=bitmap){
                    bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
                    ImageSpan imageSpan = new ImageSpan(context, bitmap);
                    SpannableString spannable = new SpannableString(spannableString);
                    spannable.setSpan(imageSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    return spannable;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
	}

    /**
     * 解析字符
     * @param data
     */
    private void ParseData(List<String> data) {
        if (data == null) {
            return;
        }
        ChatEmoji emojEentry;
        try {
            for (String str : data) {
                String[] text = str.split(",");
                String fileName = text[0].substring(0, text[0].lastIndexOf("."));
                emojiMap.put(text[1], text[0]);//此时得value是资产目录下得绝对路径
                emoticonData.add(text[1]);//将key添加进即时通讯表情列表中
                emojEentry = new ChatEmoji();
                emojEentry.setAbsolutePath(text[0]);
                emojEentry.setCharacter(text[1]);
                emojEentry.setFaceName(fileName);
                emojis.add(emojEentry);
            }
            emoticonData.add(0,"");//添加一个占位
            int pageCount = (int) Math.ceil(emojis.size() / 20 + 0.1);
            for (int i = 0; i < pageCount; i++) {
                emojiLists.add(getData(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取分页数据
     * @param page
     * @return
     */
    private List<ChatEmoji> getData(int page) {
        int startIndex = page * pageSize;
        int endIndex = startIndex + pageSize;

        if (endIndex > emojis.size()) {
            endIndex = emojis.size();
        }
        List<ChatEmoji> list = new ArrayList<ChatEmoji>();
        list.addAll(emojis.subList(startIndex, endIndex));
        if (list.size() < pageSize) {
            for (int i = list.size(); i < pageSize; i++) {
                ChatEmoji object = new ChatEmoji();
                list.add(object);
            }
        }
        if (list.size() == pageSize) {
            ChatEmoji object = new ChatEmoji();
            object.setId(R.drawable.face_del_icon);
            list.add(object);
        }
        return list;
    }

    /**
     * 根据别名获取绝对路径
     * @param imgName
     * @return
     */
    public String getImgByName(String imgName) {
        if(null==emojiMap) return "";
        return emojiMap.get(imgName);
    }

    /**
     * 根据资产目录获取
     * @param absolutePath
     * @return
     */
    public Bitmap getBitmapByAssets(String absolutePath) {
        if(null==mManager) mManager=VideoApplication.getInstance().getApplicationContext().getResources().getAssets();
        try {
            InputStream stream = mManager.open("emot/" + absolutePath);
            if(null!=stream){
                Bitmap bitmap = BitmapFactory.decodeStream(stream);
                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

	/**
	 * 对spanableString进行正则判断，如果符合要求，则以表情图片代替
	 * @param context
	 * @param spannableString
	 * @param patten
	 * @param start
	 * @throws Exception
	 */
	private void dealExpression(Context context, SpannableString spannableString, Pattern patten, int start,int textSize) throws Exception {
		if(null==mManager){
			mManager = context.getResources().getAssets();
		}
		Matcher matcher = patten.matcher(spannableString);
		while (matcher.find()) {
			String key = matcher.group();
			// 返回第一个字符的索引的文本匹配整个正则表达式,ture 则继续递归
			if (matcher.start() < start) {
				continue;
			}
			String value = emojiMap.get(key);//获取表情文件得绝对路径
			if (TextUtils.isEmpty(value)) {
				continue;
			}
            InputStream stream = mManager.open("emot/" + value);
            if(null!=stream){
                Bitmap bitmap = BitmapFactory.decodeStream(stream);
                if(null!=bitmap){
                    bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(),bitmap.getHeight(), true);
                    // 通过图片资源id来得到bitmap，用一个ImageSpan来包装
                    ImageSpan imageSpan = new ImageSpan(context, bitmap);
                    // 计算该图片名字的长度，也就是要替换的字符串的长度
                    int end = matcher.start() + key.length();
                    // 将该图片替换字符串中规定的位置中
                    spannableString.setSpan(imageSpan, matcher.start(), end,
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    if (end < spannableString.length()) {
                        // 如果整个字符串还未验证完，则继续。。
                        dealExpression(context, spannableString, patten, end,textSize);
                    }
                }
                break;
            }
		}
	}
}