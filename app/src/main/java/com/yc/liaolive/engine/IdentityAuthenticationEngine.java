package com.yc.liaolive.engine;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.kaikai.securityhttp.net.entry.UpFileInfo;
import com.yc.liaolive.bean.UploadInfo;
import com.yc.liaolive.contants.NetContants;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import rx.Observable;

/**
 * Created by wanglin  on 2018/7/8 11:10.
 */
public class IdentityAuthenticationEngine extends BaseEngine {
    public IdentityAuthenticationEngine(Context context) {
        super(context);
    }

    public Observable<ResultInfo<UploadInfo>> upload(String userid, File file) {
        Map<String, String> params = new HashMap<>();
        params.put("userid", userid);

        UpFileInfo upFileInfo = new UpFileInfo();
        upFileInfo.file = new File(file.getPath());
        upFileInfo.name = upFileInfo.file.getName();
        upFileInfo.filename = upFileInfo.file.getName();

        return HttpCoreEngin.get(mContext).rxuploadFile(NetContants.getInstance().URL_UPLOAD(), new TypeToken<ResultInfo<UploadInfo>>() {
        }.getType(), upFileInfo, params, getHeaders(), isEncrypt);
    }

    public Observable<ResultInfo<JSONObject>> identityAuthentication(String userid, String name, String id_number, String expiration_date
            , String card_front, String card_back) {

        Map<String, String> params = new HashMap<>();
        params.put("userid", userid);
        params.put("name", name);
        params.put("id_number", id_number);
        params.put("expiration_date", expiration_date);
        params.put("card_front", card_front);
        params.put("card_back", card_back);

        return HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_IDENTITY_AUTHENTICATION(), new TypeToken<ResultInfo<JSONObject>>() {
        }.getType(), params, getHeaders(), isrsa, iszip, isEncrypt);
    }
}
