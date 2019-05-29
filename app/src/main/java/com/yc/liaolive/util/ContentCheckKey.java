package com.yc.liaolive.util;

import android.text.TextUtils;
import com.yc.liaolive.VideoApplication;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2017/9/30.
 * 过滤文字内容
 */

public class ContentCheckKey {

    private static final String TAG = ContentCheckKey.class.getSimpleName();
    private static ContentCheckKey cContentCheckKey;
    private static List<String> keys=new ArrayList<>();

    public static synchronized ContentCheckKey getInstance() {
        synchronized (ContentCheckKey.class){
            if(null==cContentCheckKey){
                cContentCheckKey=new ContentCheckKey();
            }
        }
        return cContentCheckKey;
    }


    public void init() {
        readKeyTxt();
    }


    private void readKeyTxt() {
        if(null!=keys) keys.clear();
        long startTime = System.currentTimeMillis();
        try {
            InputStream is = VideoApplication.getInstance().getAssets().open("content/key.txt");
            BufferedReader br = null;
            br = new BufferedReader(new InputStreamReader(is));
            try {
                for (String line = br.readLine();
                     line != null;
                     line = br.readLine()) {
                     keys.add(line);
                }
                br.close();
                long endTime = System.currentTimeMillis();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 对比关键字
     * @param wordsmMessage
     * @return
     */
    public boolean contrastKey(String wordsmMessage) {
        boolean iscontrasts=false;
        if(TextUtils.isEmpty(wordsmMessage)) return iscontrasts;

        if(null==keys||keys.size()<=0){
            init();
        }
        for (String key : keys) {
            if(wordsmMessage.contains(key)){
                iscontrasts=true;
                break;
            }
        }
        return iscontrasts;
    }
}
