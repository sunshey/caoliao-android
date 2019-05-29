package com.yc.liaolive.recharge.manager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.engine.HttpCoreEngin;
import com.yc.liaolive.recharge.model.bean.RechargeBean;
import com.yc.liaolive.recharge.model.bean.RechargeGoodsInfo;
import com.yc.liaolive.ui.contract.GoodsContract;
import com.yc.liaolive.user.manager.UserManager;

import java.util.List;
import java.util.Map;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * TinyHung@outlook.com
 * 2018/6/6
 * 充值
 */

public class GoodsPresenter extends RxBasePresenter<GoodsContract.View> implements GoodsContract.Presenter<GoodsContract.View> {

    /**
     * 获取充值列表
     * @param type  11:大厅  14：视频通话 18：直播间 19:amsr声音  20：amsr视频
     */
    @Override
    public void getGoldGoods(final int type) {
        getGoldGoods(type, false);
    }

    /**
     * 获取商品列表
     * @param type
     * @param forbidSelected 是否禁用选中
     */
    @Override
    public void getGoldGoods(final int type, final boolean forbidSelected) {
//        getGoldGoodsData(type, forbidSelected);
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_RECHARGE_LIST());
        params.put("page", "1");
        params.put("page_size", "100");
        params.put("goods_type", String.valueOf(type));

        Subscription subscription = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_RECHARGE_LIST(),
                new TypeToken<ResultInfo<RechargeBean>>() {}.getType(), params, getHeaders(), isRsa, isZip, isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<RechargeBean>>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) mView.showGoldError(-1, NetContants.NET_REQUST_ERROR);
                    }

                    @Override
                    public void onNext(ResultInfo<RechargeBean> data) {
                        if (null != data) {
                            if (NetContants.API_RESULT_CODE == data.getCode()) {
                                if (null != data.getData() && null != data.getData().getList() && data.getData().getList().size() > 0) {
                                    if ((type == 14 ||type==18 ) && !forbidSelected) {
                                        data.getData().getList().get(0).setSelected(true);//默认第一个选中
                                    } else if (!forbidSelected){
                                        data.getData().getList().get(1).setSelected(true);//默认第二个选中
                                    }
                                    if(null!=mView) mView.showGoldInfo(data.getData());
                                } else if (null != data.getData() && null != data.getData().getList() && data.getData().getList().size() <= 0) {
                                    if (null != mView) mView.showGoldEmpty();
                                } else {
                                    if (null != mView) mView.showGoldError(-1, NetContants.NET_REQUST_JSON_ERROR);
                                }
                            } else {
                                if (null != mView) mView.showGoldError(data.getCode(), NetContants.getErrorMsg(data));
                            }
                        } else {
                            if (null != mView) mView.showGoldError(-1, NetContants.NET_REQUST_ERROR);
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 获取会员商品列表
     */
    @Override
    public void getVipGoogsList() {
//        getVipGoogsData();
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().BUY_VIP3());
        params.put("page", "1");
        params.put("page_size", "100");

        Subscription subscription = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().BUY_VIP3(),
                new TypeToken<ResultInfo<RechargeBean>>() {}.getType(), params, getHeaders(), isRsa, isZip, isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<RechargeBean>>() {

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if (null != mView) mView.showGoldError(-1, NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<RechargeBean> data) {
                if (null != data) {
                    if (NetContants.API_RESULT_CODE == data.getCode()) {
                        if(null!=data.getData()){
                            //data.getData().getList().get(0).setSelected(true);//默认第一个选中
                            if(null!=mView) mView.showGoldInfo(data.getData());
                        }else{
                            if (null != mView) mView.showGoldEmpty();
                        }
                    } else {
                        if (null != mView) mView.showGoldError(data.getCode(), NetContants.getErrorMsg(data));
                    }
                } else {
                    if (null != mView) mView.showGoldError(-1, NetContants.NET_REQUST_ERROR);
                }
            }
        });
        addSubscrebe(subscription);
    }

    /**
     * 直接返回固定钻石充值数据
     * @param type
     * @param forbidSelected 是否禁用选中
     */
    private void getGoldGoodsData(int type, boolean forbidSelected) {
        //大厅
        String data11 = "[{\n" +
                "\t\t\t\"id\": 11,\n" +
                "\t\t\t\"name\": \"10000钻石\",\n" +
                "\t\t\t\"type_id\": 11,\n" +
                "\t\t\t\"desp\": \"\",\n" +
                "\t\t\t\"price\": \"10.00\",\n" +
                "\t\t\t\"m_price\": \"10.00\",\n" +
                "\t\t\t\"vip_price\": \"10.00\",\n" +
                "\t\t\t\"unit\": \"钻石\",\n" +
                "\t\t\t\"sort\": 1,\n" +
                "\t\t\t\"status\": 1,\n" +
                "\t\t\t\"use_number\": 10000,\n" +
                "\t\t\t\"give_unit\": \"钻石\",\n" +
                "\t\t\t\"give_use_number\": 0,\n" +
                "\t\t\t\"icon\": \"\",\n" +
                "\t\t\t\"father_id\": 0\n" +
                "\t\t}, {\n" +
                "\t\t\t\"id\": 3,\n" +
                "\t\t\t\"name\": \"30000钻石\",\n" +
                "\t\t\t\"type_id\": 11,\n" +
                "\t\t\t\"desp\": \"\",\n" +
                "\t\t\t\"price\": \"30.00\",\n" +
                "\t\t\t\"m_price\": \"30.00\",\n" +
                "\t\t\t\"vip_price\": \"30.00\",\n" +
                "\t\t\t\"unit\": \"钻石\",\n" +
                "\t\t\t\"sort\": 2,\n" +
                "\t\t\t\"status\": 1,\n" +
                "\t\t\t\"use_number\": 30000,\n" +
                "\t\t\t\"give_unit\": \"钻石\",\n" +
                "\t\t\t\"give_use_number\": 0,\n" +
                "\t\t\t\"icon\": \"\",\n" +
                "\t\t\t\"father_id\": 0\n" +
                "\t\t}, {\n" +
                "\t\t\t\"id\": 4,\n" +
                "\t\t\t\"name\": \"100000钻石\",\n" +
                "\t\t\t\"type_id\": 11,\n" +
                "\t\t\t\"desp\": \"\",\n" +
                "\t\t\t\"price\": \"100.00\",\n" +
                "\t\t\t\"m_price\": \"100.00\",\n" +
                "\t\t\t\"vip_price\": \"100.00\",\n" +
                "\t\t\t\"unit\": \"钻石\",\n" +
                "\t\t\t\"sort\": 3,\n" +
                "\t\t\t\"status\": 1,\n" +
                "\t\t\t\"use_number\": 100000,\n" +
                "\t\t\t\"give_unit\": \"钻石\",\n" +
                "\t\t\t\"give_use_number\": 0,\n" +
                "\t\t\t\"icon\": \"\",\n" +
                "\t\t\t\"father_id\": 0\n" +
                "\t\t}, {\n" +
                "\t\t\t\"id\": 5,\n" +
                "\t\t\t\"name\": \"300000钻石\",\n" +
                "\t\t\t\"type_id\": 11,\n" +
                "\t\t\t\"desp\": \"\",\n" +
                "\t\t\t\"price\": \"300.00\",\n" +
                "\t\t\t\"m_price\": \"300.00\",\n" +
                "\t\t\t\"vip_price\": \"300.00\",\n" +
                "\t\t\t\"unit\": \"钻石\",\n" +
                "\t\t\t\"sort\": 4,\n" +
                "\t\t\t\"status\": 1,\n" +
                "\t\t\t\"use_number\": 300000,\n" +
                "\t\t\t\"give_unit\": \"钻石\",\n" +
                "\t\t\t\"give_use_number\": 0,\n" +
                "\t\t\t\"icon\": \"\",\n" +
                "\t\t\t\"father_id\": 0\n" +
                "\t\t}, {\n" +
                "\t\t\t\"id\": 6,\n" +
                "\t\t\t\"name\": \"600000钻石\",\n" +
                "\t\t\t\"type_id\": 11,\n" +
                "\t\t\t\"desp\": \"\",\n" +
                "\t\t\t\"price\": \"600.00\",\n" +
                "\t\t\t\"m_price\": \"600.00\",\n" +
                "\t\t\t\"vip_price\": \"600.00\",\n" +
                "\t\t\t\"unit\": \"钻石\",\n" +
                "\t\t\t\"sort\": 5,\n" +
                "\t\t\t\"status\": 1,\n" +
                "\t\t\t\"use_number\": 600000,\n" +
                "\t\t\t\"give_unit\": \"钻石\",\n" +
                "\t\t\t\"give_use_number\": 0,\n" +
                "\t\t\t\"icon\": \"\",\n" +
                "\t\t\t\"father_id\": 0\n" +
                "\t\t}, {\n" +
                "\t\t\t\"id\": 7,\n" +
                "\t\t\t\"name\": \"1000000钻石\",\n" +
                "\t\t\t\"type_id\": 11,\n" +
                "\t\t\t\"desp\": \"\",\n" +
                "\t\t\t\"price\": \"1000.00\",\n" +
                "\t\t\t\"m_price\": \"1000.00\",\n" +
                "\t\t\t\"vip_price\": \"1000.00\",\n" +
                "\t\t\t\"unit\": \"钻石\",\n" +
                "\t\t\t\"sort\": 6,\n" +
                "\t\t\t\"status\": 1,\n" +
                "\t\t\t\"use_number\": 1000000,\n" +
                "\t\t\t\"give_unit\": \"钻石\",\n" +
                "\t\t\t\"give_use_number\": 0,\n" +
                "\t\t\t\"icon\": \"\",\n" +
                "\t\t\t\"father_id\": 0\n" +
                "\t\t}, {\n" +
                "\t\t\t\"id\": 8,\n" +
                "\t\t\t\"name\": \"1600000钻石\",\n" +
                "\t\t\t\"type_id\": 11,\n" +
                "\t\t\t\"desp\": \"\",\n" +
                "\t\t\t\"price\": \"1600.00\",\n" +
                "\t\t\t\"m_price\": \"1600.00\",\n" +
                "\t\t\t\"vip_price\": \"1600.00\",\n" +
                "\t\t\t\"unit\": \"钻石\",\n" +
                "\t\t\t\"sort\": 7,\n" +
                "\t\t\t\"status\": 1,\n" +
                "\t\t\t\"use_number\": 1600000,\n" +
                "\t\t\t\"give_unit\": \"钻石\",\n" +
                "\t\t\t\"give_use_number\": 0,\n" +
                "\t\t\t\"icon\": \"\",\n" +
                "\t\t\t\"father_id\": 0\n" +
                "\t\t}, {\n" +
                "\t\t\t\"id\": 9,\n" +
                "\t\t\t\"name\": \"3000000钻石\",\n" +
                "\t\t\t\"type_id\": 11,\n" +
                "\t\t\t\"desp\": \"\",\n" +
                "\t\t\t\"price\": \"3000.00\",\n" +
                "\t\t\t\"m_price\": \"3000.00\",\n" +
                "\t\t\t\"vip_price\": \"3000.00\",\n" +
                "\t\t\t\"unit\": \"钻石\",\n" +
                "\t\t\t\"sort\": 8,\n" +
                "\t\t\t\"status\": 1,\n" +
                "\t\t\t\"use_number\": 3000000,\n" +
                "\t\t\t\"give_unit\": \"钻石\",\n" +
                "\t\t\t\"give_use_number\": 0,\n" +
                "\t\t\t\"icon\": \"\",\n" +
                "\t\t\t\"father_id\": 0\n" +
                "\t\t}]";
        //快速充值 视频通话
        String data14 = "[{\n" +
                "\t\t\t\"id\": 22,\n" +
                "\t\t\t\"name\": \"30000钻石\",\n" +
                "\t\t\t\"type_id\": 14,\n" +
                "\t\t\t\"desp\": \"购买30000钻石\",\n" +
                "\t\t\t\"price\": \"30.00\",\n" +
                "\t\t\t\"m_price\": \"30.00\",\n" +
                "\t\t\t\"vip_price\": \"30.00\",\n" +
                "\t\t\t\"unit\": \"钻石\",\n" +
                "\t\t\t\"sort\": 1,\n" +
                "\t\t\t\"status\": 1,\n" +
                "\t\t\t\"use_number\": 30000,\n" +
                "\t\t\t\"give_unit\": \"钻石\",\n" +
                "\t\t\t\"give_use_number\": 0,\n" +
                "\t\t\t\"icon\": \"\",\n" +
                "\t\t\t\"father_id\": 0\n" +
                "\t\t}, {\n" +
                "\t\t\t\"id\": 23,\n" +
                "\t\t\t\"name\": \"50000钻石\",\n" +
                "\t\t\t\"type_id\": 14,\n" +
                "\t\t\t\"desp\": \"\",\n" +
                "\t\t\t\"price\": \"50.00\",\n" +
                "\t\t\t\"m_price\": \"50.00\",\n" +
                "\t\t\t\"vip_price\": \"50.00\",\n" +
                "\t\t\t\"unit\": \"钻石\",\n" +
                "\t\t\t\"sort\": 2,\n" +
                "\t\t\t\"status\": 1,\n" +
                "\t\t\t\"use_number\": 50000,\n" +
                "\t\t\t\"give_unit\": \"钻石\",\n" +
                "\t\t\t\"give_use_number\": 0,\n" +
                "\t\t\t\"icon\": \"\",\n" +
                "\t\t\t\"father_id\": 0\n" +
                "\t\t}, {\n" +
                "\t\t\t\"id\": 24,\n" +
                "\t\t\t\"name\": \"100000钻石\",\n" +
                "\t\t\t\"type_id\": 14,\n" +
                "\t\t\t\"desp\": \"\",\n" +
                "\t\t\t\"price\": \"100.00\",\n" +
                "\t\t\t\"m_price\": \"100.00\",\n" +
                "\t\t\t\"vip_price\": \"100.00\",\n" +
                "\t\t\t\"unit\": \"钻石\",\n" +
                "\t\t\t\"sort\": 3,\n" +
                "\t\t\t\"status\": 1,\n" +
                "\t\t\t\"use_number\": 100000,\n" +
                "\t\t\t\"give_unit\": \"钻石\",\n" +
                "\t\t\t\"give_use_number\": 0,\n" +
                "\t\t\t\"icon\": \"\",\n" +
                "\t\t\t\"father_id\": 0\n" +
                "\t\t}]";
        //快速充值，直播间
        String data18 = "[{\"id\":41,\"name\":\"10000钻石\",\"type_id\":18,\"desp\":\"购买10000 钻石\",\"price\":\"10.00\",\"m_price\":\"10.00\",\"vip_price\":\"10.00\",\"unit\":\"钻石\",\"sort\":0,\"status\":1,\"use_number\":10000,\"give_unit\":\"钻石\",\"give_use_number\":0,\"icon\":\"\",\"father_id\":0}," +
                "{\"id\":42,\"name\":\"30000钻石\",\"type_id\":18,\"desp\":\"购买30000钻石\",\"price\":\"30.00\",\"m_price\":\"30.00\",\"vip_price\":\"30.00\",\"unit\":\"钻石\",\"sort\":1,\"status\":1,\"use_number\":30000,\"give_unit\":\"钻石\",\"give_use_number\":0,\"icon\":\"\",\"father_id\":0}," +
                "{\"id\":43,\"name\":\"100000钻石\",\"type_id\":18,\"desp\":\"购买100000钻石\",\"price\":\"100.00\",\"m_price\":\"100.00\",\"vip_price\":\"100.00\",\"unit\":\"钻石\",\"sort\":2,\"status\":1,\"use_number\":100000,\"give_unit\":\"钻石\",\"give_use_number\":0,\"icon\":\"\",\"father_id\":0}]";

        List<RechargeGoodsInfo> resultList;
        //视频通话
        if (type == 14) {
            resultList = new Gson().fromJson(data14, new TypeToken<List<RechargeGoodsInfo>>(){}.getType());
            if(!forbidSelected) resultList.get(0).setSelected(true);//默认第一个选中
            //直播间
        } else if(type==18){
            resultList = new Gson().fromJson(data18, new TypeToken<List<RechargeGoodsInfo>>(){}.getType());
            if(!forbidSelected) resultList.get(0).setSelected(true);//默认第一个选中
            //大厅
        }else {
            resultList = new Gson().fromJson(data11, new TypeToken<List<RechargeGoodsInfo>>(){}.getType());
            if(!forbidSelected)  resultList.get(1).setSelected(true);//默认第二个选中
        }
        if (null != mView) {
            RechargeBean rechargeBean=new RechargeBean();
            rechargeBean.setList(resultList);
            mView.showGoldInfo(rechargeBean);
        }
    }

    /**
     * 获取开通VIP默认数据
     */
    private void getVipGoogsData () {
        String data = "[{\n" +
                "\t\t\t\"id\": 19,\n" +
                "\t\t\t\"price\": \"99.00\",\n" +
                "\t\t\t\"name\": \"终身\",\n" +
                "\t\t\t\"give_num\": 99000,\n" +
                "\t\t\t\"sub_title\": \"0元\\/天\",\n" +
                "\t\t\t\"gift_info\": \"赠99000钻石(100%返还,价值99元)\",\n" +
                "\t\t\t\"desc\": \"\",\n" +
                "\t\t\t\"is_show_xs\": 1,\n" +
                "\t\t\t\"is_show_hn\": 1,\n" +
                "\t\t\t\"is_show_yh\": 1,\n" +
                "\t\t\t\"activity_tips\": \"专属红娘1V1服务\"\n" +
                "\t\t}, {\n" +
                "\t\t\t\"id\": 20,\n" +
                "\t\t\t\"price\": \"98.00\",\n" +
                "\t\t\t\"name\": \"六个月\",\n" +
                "\t\t\t\"give_num\": 0,\n" +
                "\t\t\t\"sub_title\": \"0.55元\\/天\",\n" +
                "\t\t\t\"gift_info\": \"\",\n" +
                "\t\t\t\"desc\": \"（只需加1元变终身）\",\n" +
                "\t\t\t\"is_show_xs\": 0,\n" +
                "\t\t\t\"is_show_hn\": 0,\n" +
                "\t\t\t\"is_show_yh\": 2,\n" +
                "\t\t\t\"activity_tips\": \"专属红娘1V1服务\"\n" +
                "\t\t}, {\n" +
                "\t\t\t\"id\": 21,\n" +
                "\t\t\t\"price\": \"68.00\",\n" +
                "\t\t\t\"name\": \"一个月\",\n" +
                "\t\t\t\"give_num\": 34000,\n" +
                "\t\t\t\"sub_title\": \"\",\n" +
                "\t\t\t\"gift_info\": \"赠34000钻石(50%返还,价值34元)\",\n" +
                "\t\t\t\"desc\": \"\",\n" +
                "\t\t\t\"is_show_xs\": 0,\n" +
                "\t\t\t\"is_show_hn\": 0,\n" +
                "\t\t\t\"is_show_yh\": 3\n" +
                "\t\t}]";

        String dataRecharge = "[{\n" +
                "\t\t\t\"id\": 25,\n" +
                "\t\t\t\"price\": \"99.00\",\n" +
                "\t\t\t\"name\": \"终身\",\n" +
                "\t\t\t\"give_num\": 99000,\n" +
                "\t\t\t\"sub_title\": \"0元\\/天\",\n" +
                "\t\t\t\"gift_info\": \"赠99000钻石(100%返还,价值99元)\",\n" +
                "\t\t\t\"desc\": \"\",\n" +
                "\t\t\t\"is_show_xs\": 1,\n" +
                "\t\t\t\"is_show_hn\": 1,\n" +
                "\t\t\t\"is_show_yh\": 1,\n" +
                "\t\t\t\"activity_tips\": \"促销活动，额外赠送100话费\"\n" +
                "\t\t}, {\n" +
                "\t\t\t\"id\": 20,\n" +
                "\t\t\t\"price\": \"98.00\",\n" +
                "\t\t\t\"name\": \"六个月\",\n" +
                "\t\t\t\"give_num\": 0,\n" +
                "\t\t\t\"sub_title\": \"0.55元\\/天\",\n" +
                "\t\t\t\"gift_info\": \"\",\n" +
                "\t\t\t\"desc\": \"（只需加1元变终身）\",\n" +
                "\t\t\t\"is_show_xs\": 0,\n" +
                "\t\t\t\"is_show_hn\": 0,\n" +
                "\t\t\t\"is_show_yh\": 2\n" +
                "\t\t}, {\n" +
                "\t\t\t\"id\": 21,\n" +
                "\t\t\t\"price\": \"68.00\",\n" +
                "\t\t\t\"name\": \"一个月\",\n" +
                "\t\t\t\"give_num\": 34000,\n" +
                "\t\t\t\"sub_title\": \"\",\n" +
                "\t\t\t\"gift_info\": \"赠34000钻石(50%返还,价值34元)\",\n" +
                "\t\t\t\"desc\": \"\",\n" +
                "\t\t\t\"is_show_xs\": 0,\n" +
                "\t\t\t\"is_show_hn\": 0,\n" +
                "\t\t\t\"is_show_yh\": 3\n" +
                "\t\t}]";
        List<RechargeGoodsInfo> list;
        if (UserManager.getInstance().getVip_phone() == 1) { //不显示话费赠送
            list = new Gson().fromJson(data, new TypeToken<List<RechargeGoodsInfo>>(){}.getType());
        } else {
            list = new Gson().fromJson(dataRecharge, new TypeToken<List<RechargeGoodsInfo>>(){}.getType());
        }

        list.get(0).setSelected(true);//默认第一个选中
        if (null != mView){
            RechargeBean rechargeBean=new RechargeBean();
            rechargeBean.setList(list);
            mView.showGoldInfo(rechargeBean);
        }
    }
}
