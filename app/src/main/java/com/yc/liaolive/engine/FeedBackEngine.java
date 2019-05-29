package com.yc.liaolive.engine;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.kaikai.securityhttp.net.entry.UpFileInfo;
import com.yc.liaolive.contants.NetContants;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import rx.Observable;

/**
 * Created by wanglin  on 2018/7/8 14:50.
 */
public class FeedBackEngine extends BaseEngine {
    public FeedBackEngine(Context context) {
        super(context);
    }

    public Observable<ResultInfo<JSONObject>> feedBack(String userid, String content, File file, String contact) {

        Map<String, String> params = new HashMap<>();
        params.put("userid", userid);
        params.put("content", content);
        params.put("contact", contact);
        UpFileInfo upFileInfo;
        if (null != file) {
            upFileInfo = new UpFileInfo();
            upFileInfo.file = file;
            upFileInfo.filename = upFileInfo.file.getName();
            upFileInfo.name = "image";
            return HttpCoreEngin.get(mContext).rxuploadFile(NetContants.getInstance().URL_SUGGEST(), new TypeToken<ResultInfo<JSONObject>>() {
            }.getType(), upFileInfo, params, getHeaders(), false);
        } else {
            return HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_SUGGEST(), new TypeToken<ResultInfo<JSONObject>>() {
            }.getType(), params, getHeaders(), false, false, false);
        }

    }
}
