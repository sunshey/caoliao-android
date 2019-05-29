package com.yc.liaolive.engine;

import android.content.Context;
import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.bean.MineTabData;
import com.yc.liaolive.bean.PersonCenterInfo;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.user.manager.UserManager;
import java.util.HashMap;
import java.util.Map;
import rx.Observable;

/**
 * Created by wanglin  on 2018/7/9 16:01.
 */
public class PersonCenterEngine extends BaseEngine {

    public PersonCenterEngine(Context context) {
        super(context);
    }

    public Observable<ResultInfo<PersonCenterInfo>> getPersonCenterInfo(String userid, String to_userid) {
        Map<String, String> params = new HashMap<>();
        params.put("userid", userid);
        params.put("to_userid", to_userid);
        params.put("data_more", "1");//获取全部数据
        return HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_PERSONAL_CENTER(), new TypeToken<ResultInfo<PersonCenterInfo>>() {
        }.getType(), params, getHeaders(),isrsa, iszip, isEncrypt);
    }


    public Observable<ResultInfo<MineTabData>> getItemList() {
        Map<String, String> params = new HashMap<>();
        params.put("userid", UserManager.getInstance().getUserId());
        return HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_PERSONAL_MY_LIST(), new TypeToken<ResultInfo<MineTabData>>() {
        }.getType(), params,getHeaders(), isrsa, iszip, isEncrypt);
    }
}
