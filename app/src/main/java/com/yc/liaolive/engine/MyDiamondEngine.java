package com.yc.liaolive.engine;

import android.content.Context;
import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.bean.DiamondInfoWrapper;
import com.yc.liaolive.contants.NetContants;
import java.util.HashMap;
import java.util.Map;
import rx.Observable;

/**
 * Created by wanglin  on 2018/7/10 14:36.
 */
public class MyDiamondEngine extends BaseEngine {
    public MyDiamondEngine(Context context) {
        super(context);
    }

    /**
     *
     * @param userid
     * @param type 3: 积分 4：钻石
     * @param page
     * @param assetsType 0：全部  1：支出 2：收入
     * @return
     */
    public Observable<ResultInfo<DiamondInfoWrapper>> getDiamondInfoList(String userid, String type, int page, int assetsType) {
        Map<String, String> params = new HashMap<>();
        params.put("userid", userid);
        params.put("type", type);
        params.put("assets_type", String.valueOf(assetsType));
        params.put("page", page + "");
        params.put("page_size", "100");

        return HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_PERSONAL_DETAILS_LIST(), new TypeToken<ResultInfo<DiamondInfoWrapper>>() {
        }.getType(), params,getHeaders(), isrsa, iszip, isEncrypt);
    }
}
