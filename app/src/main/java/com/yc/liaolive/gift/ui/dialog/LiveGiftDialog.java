package com.yc.liaolive.gift.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import com.umeng.analytics.MobclickAgent;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.base.BaseDialog;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.DialogLiveGiftBinding;
import com.yc.liaolive.gift.interfaceView.GiftInterfaceView;
import com.yc.liaolive.gift.manager.GiftHelpManager;
import com.yc.liaolive.gift.ui.pager.GiftFaceplatePager;
import com.yc.liaolive.interfaces.PerfectClickListener;
import com.yc.liaolive.live.bean.GiftInfo;
import com.yc.liaolive.live.bean.GiftTypeInfo;
import com.yc.liaolive.live.bean.GiveGiftResultInfo;
import com.yc.liaolive.live.bean.PusherInfo;
import com.yc.liaolive.live.manager.GiftManager;
import com.yc.liaolive.live.ui.contract.LiveGiftContact;
import com.yc.liaolive.live.ui.presenter.LiveGiftPresenter;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.recharge.ui.VipActivity;
import com.yc.liaolive.ui.activity.WaterDetailsActivity;
import com.yc.liaolive.ui.dialog.QuireDialog;
import com.yc.liaolive.ui.dialog.TodayWaterDialog;
import com.yc.liaolive.user.IView.UserServerContract;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.AnimationUtil;
import com.yc.liaolive.util.ScreenUtils;
import com.yc.liaolive.util.SharedPreferencesUtil;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.videocall.ui.activity.CallRechargeActivity;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * TinyHung@Outlook.com
 * 2018/5/17
 * 礼物面板显示父容器
 * 负责填充父容器和礼物结算逻辑, 结合 GiftFaceplatePager 、 GiftBoardView 、 CountdownGiftView 、GiftHelpManager 达到最佳的复原体验效果
 */

public class LiveGiftDialog extends BaseDialog<DialogLiveGiftBinding> implements LiveGiftContact.View,GiftInterfaceView {

    private static final String TAG ="LiveGiftDialog";
    //礼物发射场景
    public static final int GIFT_MODE_ROOM = 0;//直播间模式
    public static final int GIFT_MODE_PRIVATE_ROOM = 1;//视频通话
    public static final int GIFT_MODE_PRIVATE_CHAT = 2;//私信模式
    public static final int GIFT_MODE_PRIVATE_MEDIA = 3;//多媒体预览
    public static final int GIFT_MODE_PRIVATE_USER = 4;//用户中心
    public static final int GIFT_MODE_ONESELF = 5;//自己对自己
    public static final int GIFT_MODE_TEST = 6;//开发调试模式,跳过支付结算，直达礼物赠送效果
    private int sourceApiType;//资源文件API TYPE
    //接受者用户数据
    private PusherInfo mAccepUserInfo;//接收人信息
    private int mGiftMode;//场景模式
    private String mRoomID;//所在的房间ID
    private boolean mIsRecovery;//是否回显至上次选中状态
    //操作逻辑
    private int count;//当前选中的礼物个数
    //连击\倒计时相关
    private long SECOND=21;//默认20秒连击倒计时
    private boolean isCountdownRuning;
    private Timer mBroadcastTimer;
    private BroadcastTimerTask mBroadcastTimerTask;
    //充值询问对话框
    private QuireDialog mQuireDialog;
    private GiftPagerAdapter mPagerAdapter;
    private LiveGiftPresenter mPresenter;//礼物赠送
    private List<GiftTypeInfo> mData;
    //生命周期必须
    private Map<Integer,GiftFaceplatePager> mPagerMapr = new HashMap<>();//存放片段的集合
    private static final int CHANGE_ODE_RESUME = 1;//可见
    private static final int CHANGE_ODE_PAUSE = 2;//不可见
    private static final int CHANGE_ODE_DESTROY= 3;//销毁
    public int mMdiaType=-1;

    /**
     * 直播间(公、私)、多媒体预览
     * @param activity
     * @param pusher
     * @param roomID
     * @param giftMode GIFT_MODE_ROOM、GIFT_MODE_PRIVATE_ROOM、GIFT_MODE_PRIVATE_MEDIA 除了私信场景外是结算完成后回调礼物对象外，其他场景同步播放礼物动画
     * @param isRecovery
     * @param mediaType 多媒体类型,默认-1，其它详见 Constant.MEDIA_TYPE_IMAGE 等常量定义,用途：数据统计
     * @return
     */
    public static LiveGiftDialog getInstance(Activity activity, PusherInfo pusher,
                                             String roomID, int giftMode, boolean isRecovery,int mediaType) {
        return createInstance(activity, pusher, roomID, giftMode, isRecovery,mediaType);
    }

    /**
     * 私信
     * @param activity
     * @param pusher
     * @param isRecovery
     * @return
     */
    public static LiveGiftDialog getInstance(Activity activity,PusherInfo pusher, boolean isRecovery) {
        return createInstance(activity, pusher,"", GIFT_MODE_PRIVATE_CHAT, isRecovery,-1);
    }

    /**
     * 统一入口
     * @param activity
     * @param pusher 接收人
     * @param roomID 房间ID
     * @param giftMode 场景模式
     * @param isRecovery 是否回显选中状态
     * @param mediaType 多媒体类型,默认-1，其它详见 Constant.MEDIA_TYPE_IMAGE 等常量定义,用途：数据统计
     * @return
     */
    private static LiveGiftDialog createInstance(Activity activity,PusherInfo pusher, String roomID,
                                                 int giftMode, boolean isRecovery,int mediaType){
        if(null==pusher){
            throw new NullPointerException("LiveGiftDialog--Gift recipients cannot be empty!");
        }
        return new LiveGiftDialog(activity, pusher, roomID, giftMode, isRecovery,mediaType);
    }

    /**
     * 构造入口
     * @param activity 依附关系
     * @param pusher 接受者
     * @param roomID 群ID
     * @param giftMode 交互场景
     * @param isRecovery 是否回显
     * @param mediaType 多媒体类别
     */
    public LiveGiftDialog(Activity activity,PusherInfo pusher, String roomID,
                          int giftMode,boolean isRecovery,int mediaType) {
        super(activity,R.style.ButtomDialogTransparentAnimationStyle);
        mAccepUserInfo = pusher;
        mRoomID = roomID;
        mGiftMode = giftMode;
        setSourceApiType(mGiftMode);
        mIsRecovery = isRecovery;
        this.mMdiaType=mediaType;
        setContentView(R.layout.dialog_live_gift);
    }

    /**
     * 设定礼物数据使用场景
     * @param giftMode
     */
    private void setSourceApiType(int giftMode) {
        switch (giftMode) {
            //视频通话
            case GIFT_MODE_PRIVATE_ROOM:
                sourceApiType=1;
                break;
            //直播间
            case GIFT_MODE_ROOM:
                sourceApiType=2;
                break;
            //私信模式
            case GIFT_MODE_PRIVATE_CHAT:
                sourceApiType=3;
                break;
            //用户中心
            case GIFT_MODE_PRIVATE_USER:
                sourceApiType=4;
                break;
            //多媒体预览
            case GIFT_MODE_PRIVATE_MEDIA:
                sourceApiType=5;
                break;
        }
    }

    /**
     * 初始化礼物面板显示在屏幕中的位置
     */
    protected void initLayoutPrams(){
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        WindowManager.LayoutParams attributes = window.getAttributes();//得到布局管理者
        WindowManager systemService = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);//得到窗口管理者
        DisplayMetrics displayMetrics=new DisplayMetrics();//创建设备屏幕的管理者
        systemService.getDefaultDisplay().getMetrics(displayMetrics);//得到屏幕的宽高
        attributes.height= FrameLayout.LayoutParams.WRAP_CONTENT;
        attributes.width= systemService.getDefaultDisplay().getWidth();
        attributes.gravity= Gravity.BOTTOM;
    }

    @Override
    public void initViews() {
        //默认ViewPager高度
        int itemLayoutHeight=(ScreenUtils.getScreenWidth() / 2)+ScreenUtils.dpToPxInt(25f);
        ViewGroup.LayoutParams viewPagerLayoutParams = bindingView.viewPager.getLayoutParams();
        viewPagerLayoutParams.height=itemLayoutHeight;
        bindingView.viewPager.setLayoutParams(viewPagerLayoutParams);
        //遮罩图层的高度
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        bindingView.giftPlatface.measure(width, width);
        bindingView.viewGuidLayout.getLayoutParams().height= bindingView.giftPlatface.getMeasuredHeight();
        initLayoutPrams();
        //背景颜色样式
        bindingView.giftPlatface.setBackgroundColor(Color.parseColor("#FF100017"));
        bindingView.giftPlatface.getBackground().setAlpha(230);
        new android.os.Handler().postAtTime(new Runnable() {
            @Override
            public void run() {
                init();
            }
        }, SystemClock.uptimeMillis()+100);
    }

    /**
     * 初始化
     */
    private void init() {
        if(null==bindingView) return;
        mPresenter=new LiveGiftPresenter();
        mPresenter.attachView(this);
        //分片数据适配器
        mData=ApplicationManager.getInstance().getGiftTypeList(sourceApiType);
        mPagerAdapter = new GiftPagerAdapter();
        bindingView.viewPager.setAdapter(mPagerAdapter);
        bindingView.viewPager.setOffscreenPageLimit(5);
        bindingView.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                GiftHelpManager.getInstance().setFragmentIndex(position);//恢复界面用到
                onLifeChange(position,CHANGE_ODE_RESUME);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        bindingView.giftTabLayout.setTabMode(TabLayout.MODE_FIXED);
        bindingView.giftTabLayout.setupWithViewPager(bindingView.viewPager);

        //回显上次选中状态
        if(mIsRecovery&&GiftHelpManager.getInstance().isExitRecoveryState()){
            bindingView.viewPager.setCurrentItem(GiftHelpManager.getInstance().getFragmentIndex());
        }
        //引导遮罩层
        showGuidLayout();
        //用户余额信息
        bindingView.tvRechargTips.setText("充值>");
        if(null!=mAccepUserInfo) bindingView.accpetName.setText(Html.fromHtml("送给:<font color='#E6646E'>"+(TextUtils.isEmpty(mAccepUserInfo.getUserName())?mAccepUserInfo.getUserID():mAccepUserInfo.getUserName())+"</font>"));
        //设置钻石总数
        setMoney(UserManager.getInstance().getDiamonds());
        //礼物赠送
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_send:
                        if (TextUtils.isEmpty(GiftManager.getInstance().getShowWhiteTips())) {
                            GiftInfo giftInfo = (GiftInfo) bindingView.btnSend.getTag();
                            postGift(giftInfo);
                        } else {
                            ToastUtils.showCenterToast(GiftManager.getInstance().getShowWhiteTips());
                        }
                        break;
                    //充值
                    case R.id.ll_btn_recharg:
                        if(null!=getActivity()){
                            //直播间启用快捷充值
                            if(mGiftMode==LiveGiftDialog.GIFT_MODE_ROOM||mGiftMode==LiveGiftDialog.GIFT_MODE_PRIVATE_ROOM){
                                CallRechargeActivity.start(getActivity(),18,null);
                                return;
                            }
                            VipActivity.startForResult(getActivity(),0);
                        }
                        break;
                    //充值、消费明细
                    case R.id.btn_day_detail:
                        if(null!=getActivity()) TodayWaterDialog.newInstance(getActivity(),UserManager.getInstance().getUserId()).setContentTitle("今日明细").setOnSubmitClickListener(new TodayWaterDialog.OnSubmitClickListener() {
                            @Override
                            public void onSubmit() {
                                //查看看更多明细
                                WaterDetailsActivity.start(getActivity(),UserManager.getInstance().getUserId());
                            }
                        }).show();
                        break;
                    case R.id.root_view:
                        break;
                }
            }
        };
        bindingView.btnSend.setOnClickListener(onClickListener);
        bindingView.llBtnRecharg.setOnClickListener(onClickListener);
        bindingView.btnDayDetail.setOnClickListener(onClickListener);
        //连击赠送
        bindingView.btnCountdown.setOnClickListener(new PerfectClickListener(100) {
            @Override
            protected void onNoDoubleClick(View v) {
                if(null==bindingView) return;
                if (TextUtils.isEmpty(GiftManager.getInstance().getShowWhiteTips())) {
                    AnimationUtil.doubleCountAnimation(bindingView.btnCountdown);
                    GiftInfo giftInfo = (GiftInfo) bindingView.btnSend.getTag();
                    postGift(giftInfo);
                } else {
                    ToastUtils.showCenterToast(GiftManager.getInstance().getShowWhiteTips());
                }
            }
        });
    }

    /**
     * 礼物提交
     * @param giftInfo
     */
    private void postGift(GiftInfo giftInfo) {
        if(null==giftInfo) {
            ToastUtils.showCenterToast("未选中任何礼物");
            return;
        }
        if(null==mAccepUserInfo){
            ToastUtils.showCenterToast("接收对象不存在");
            return;
        }
        //倒计时开始
        statrCountdown();
        //除测试环境绕过购买
        if(mGiftMode==GIFT_MODE_TEST){
            //跳过支付场景，直接赠送礼物
            if(null!=mOnGiftSelectedListener) mOnGiftSelectedListener.onSendEvent(giftInfo,count,(giftInfo.getPrice()*count),mAccepUserInfo);
            return;
        }
        if(null==mPresenter) return;
        long diamonds = UserManager.getInstance().getDiamonds();
        //拦截余额不足,非白名单用户
        if(0==UserManager.getInstance().getIs_white()&&diamonds<(count*giftInfo.getPrice())){
            showRechargeTips();
            return;
        }
        //礼物结算
        if(null != mPresenter){
            mPresenter.givePresentGift(giftInfo, mAccepUserInfo.getUserID(),
                    String.valueOf(giftInfo.getId()), count,mRoomID, false, mGiftMode);
        }
        //除视频通话、私信等私聊模块外，本地礼物动画先行
        if(null!=mOnGiftSelectedListener&&mGiftMode!=LiveGiftDialog.GIFT_MODE_PRIVATE_CHAT&&mGiftMode!=LiveGiftDialog.GIFT_MODE_PRIVATE_ROOM){
            mOnGiftSelectedListener.onSendEvent(giftInfo,count,(count*giftInfo.getPrice()),mAccepUserInfo);
        }
    }

    /**
     * 生命周期调度
     * @param position
     * @param CHANGE_MODE
     */
    private void onLifeChange(int position,int CHANGE_MODE){
        if(null!= mPagerMapr && mPagerMapr.size()>0){
            Iterator<Map.Entry<Integer, GiftFaceplatePager>> iterator = mPagerMapr.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, GiftFaceplatePager> next = iterator.next();
                if(position==next.getKey()){
                    GiftFaceplatePager viewPager = next.getValue();
                    if(null!=viewPager){
                        if(CHANGE_ODE_RESUME==CHANGE_MODE){
                            viewPager.onResume();
                            return;
                        }else if(CHANGE_ODE_PAUSE==CHANGE_MODE){
                            viewPager.onPause();
                            return;
                        }else if(CHANGE_ODE_DESTROY==CHANGE_MODE){
                            viewPager.onDestroy();
                            return;
                        }
                    }
                }
            }
        }
    }

    /**
     * 礼物面板分页适配器
     */
    private class GiftPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return null==mData?0:mData.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null==mData?"":mData.get(position).getTitle();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            GiftTypeInfo giftTypeInfo = mData.get(position);
            if(null!=giftTypeInfo){
                GiftFaceplatePager pager = new GiftFaceplatePager(getActivity(),giftTypeInfo,position,mIsRecovery,LiveGiftDialog.this,sourceApiType);
                View view = pager.getView();
                view.setId(position);
                if(null!=mPagerMapr) mPagerMapr.put(position,pager);
                container.addView(view);
                return view;
            }
            return null;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if(null!=container){
                container.removeView(container.findViewById(position));
                if(null!=mPagerMapr) mPagerMapr.remove(position);
            }
        }
    }

    /**
     * 显示首次进入引导图层
     */
    private void showGuidLayout() {
        if(0== SharedPreferencesUtil.getInstance().getInt(Constant.SP_GIFT_FIRST_ENTER,0)){
            AnimationUtil.visibTransparentView(bindingView.viewGuidLayout);
            bindingView.viewGuidLayout.setBackgroundColor(getContext().getResources().getColor(R.color.translucent_65));
            bindingView.icGuidIcon.setImageResource(R.drawable.ic_guid_gift_item);
            bindingView.viewGuidLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bindingView.icGuidIcon.setImageResource(0);
                    AnimationUtil.goneTransparentView(bindingView.viewGuidLayout);
                }
            });
            SharedPreferencesUtil.getInstance().putInt(Constant.SP_GIFT_FIRST_ENTER,1);
        }
    }


    /**
     * 设置余额信息
     * @param pintaiMoney
     */
    public void setMoney(long pintaiMoney) {
        if(null!=bindingView) bindingView.tvSurplusMoney.setText(Utils.formatWan(pintaiMoney,true));
    }

    /**
     * 开始20秒的连击效果
     */
    private void statrCountdown() {
        SECOND=21;
        if(null==bindingView) return;
        if(isCountdownRuning) return;
        bindingView.btnSend.setVisibility(View.INVISIBLE);
        bindingView.btnCountdown.setVisibility(View.VISIBLE);
        AnimationUtil.doubleCountAnimation(bindingView.btnCountdown);
        bindingView.btnCountdown.setText(SECOND+"");
        isCountdownRuning=true;
        if(null!=mBroadcastTimer) mBroadcastTimer.cancel();
        if (mBroadcastTimer == null) {
            mBroadcastTimer = new Timer(true);
            mBroadcastTimerTask = new BroadcastTimerTask();
            mBroadcastTimer.schedule(mBroadcastTimerTask, 1000, 1000);
        }
    }

    @Override
    public void stopCountdown() {
        if(null!=mBroadcastTimer)mBroadcastTimer.cancel();mBroadcastTimer=null;
        if(null!=mBroadcastTimerTask) mBroadcastTimerTask.cancel();
        SECOND=0; isCountdownRuning=false;
        if(null!=bindingView){
            bindingView.btnCountdown.setVisibility(View.INVISIBLE);
            bindingView.btnSend.setVisibility(View.VISIBLE);
            bindingView.btnCountdown.setText(SECOND+"");
        }
    }

    /**
     * 礼物选中对象发生了变化
     * @param giftInfo
     * @param count
     */
    @Override
    public void selectedGiftChanged(GiftInfo giftInfo, int count) {
        //刷新TAG
        bindingView.btnSend.setTag(giftInfo);
        this.count=count;
        //任何时候不为空都要回调至直播间倒计时按钮
        if(null!=giftInfo&&null!=mOnGiftSelectedListener) mOnGiftSelectedListener.selectedCountChanged(giftInfo,count,mAccepUserInfo);
    }

    /**
     * 充值对话框
     */
    private void showRechargeTips() {
        if(null==getActivity()) return;
        //直播大厅、视频通话 启用快速充值面板
        if(mGiftMode==LiveGiftDialog.GIFT_MODE_ROOM||mGiftMode==LiveGiftDialog.GIFT_MODE_PRIVATE_ROOM){
            if(!CallRechargeActivity.isRunning()) CallRechargeActivity.start(getActivity(),18,null);
            return;
        }
        if(null!=mQuireDialog) return;
        mQuireDialog = QuireDialog.getInstance(getActivity());
        mQuireDialog.setTitleText("赠送礼物失败")
                .setContentText(getContext().getResources().getString(R.string.money_name)+getContext().getResources().getString(R.string.gift_monery_error))
                .setContentTextColor(getContext().getResources().getColor(R.color.app_red_style))
                .setSubmitTitleText("充值")
                .setSubmitTitleTextColor(getContext().getResources().getColor(R.color.app_red_style))
                .setCancelTitleText("放弃")
                .setOnQueraConsentListener(new QuireDialog.OnQueraConsentListener() {
                    @Override
                    public void onConsent() {
                        if(null!=getContext()){
                            MobclickAgent.onEvent(getContext(), "send_gift_type_"+mMdiaType);
                            if(mMdiaType==Constant.MEDIA_TYPE_ASMR_VIDEO){
                                CallRechargeActivity.start(getActivity(), 20, null);
                            }else{
                                VipActivity.startForResult(getActivity(),0);
                            }
                        }
                    }

                    @Override
                    public void onRefuse() {

                    }
                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mQuireDialog=null;
            }
        });
        mQuireDialog.show();
    }

    /**
     * 倒计时 记时器
     */
    private class BroadcastTimerTask extends TimerTask {
        public void run() {
            //Log.i(TAG, "timeTask ");
            if(null==getContext()) return;
            SECOND--;
            if(null!=getActivity()){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(SECOND<=0){
                            stopCountdown();
                        }else{
                            if(null!=bindingView) bindingView.btnCountdown.setText(SECOND+"");
                        }
                    }
                });
            }
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if(null!=bindingView&&null!=mData){
            for (int i = 0; i < mData.size(); i++) {
                onLifeChange(i,CHANGE_ODE_DESTROY);
            }
            bindingView.viewPager.removeAllViews();
        }
        if(null!=mPresenter) mPresenter.detachView();
        if(null!=mBroadcastTimer)mBroadcastTimer.cancel();
        if(null!=mBroadcastTimerTask) mBroadcastTimerTask.cancel();
        if(null!=mQuireDialog) mQuireDialog.dismiss();
        if(null!=mPagerMapr) mPagerMapr.clear();
        mQuireDialog=null;mBroadcastTimerTask=null;mBroadcastTimer=null;mPagerMapr=null;
        mBroadcastTimerTask=null;mGiftMode=0;mRoomID=null;SECOND=0; isCountdownRuning=false;
        if(null!=mOnGiftSelectedListener) mOnGiftSelectedListener.onDissmiss();
    }

    @Override
    public void show() {
        super.show();
        //更新用户余额
        UserManager.getInstance().getFullUserData(UserManager.getInstance().getUserId(), UserManager.getInstance().getUserId(), new UserServerContract.OnNetCallBackListener() {
            @Override
            public void onSuccess(Object object) {
                setMoney(UserManager.getInstance().getDiamonds());
                //通知所有需要关心余额的地方，更新本地余额
                ApplicationManager.getInstance().observerUpdata(Constant.OBSERVER_CMD_USER_LOCATION_INTEGRAL_CHANGED);
            }

            @Override
            public void onFailure(int code, String errorMsg) {

            }
        });
    }

    public abstract static class  OnGiftSelectedListener{
        public void onSendEvent(GiftInfo data, int count, int totalPrice, PusherInfo accepUserInfo){}
        public void  onDissmiss(){}
        //观众列表-最新的排行名词
        public void selectedCountChanged(GiftInfo giftInfo,int count,PusherInfo accepUserInfo){}
    }

    private OnGiftSelectedListener mOnGiftSelectedListener;

    public void setOnGiftSelectedListener(OnGiftSelectedListener onGiftSelectedListener) {
        mOnGiftSelectedListener = onGiftSelectedListener;
    }


    @Override
    public void showErrorView() {

    }

    @Override
    public void complete() {

    }

    @Override
    public void showGifts(List<GiftInfo> data,String type) {

    }

    @Override
    public void showGiftEmpty(String type) {

    }

    @Override
    public void showGiftError(int code, String errMsg) {

    }

    /**
     * 交易回执
     * @param giftInfo
     * @param giftCount
     * @param data
     * @param isDoubleClick
     */
    @Override
    public void showGivePresentSuccess(GiftInfo giftInfo,int giftCount,GiveGiftResultInfo data, boolean isDoubleClick) {
        VideoApplication.getInstance().setMineRefresh(true);
        //私信会话下，需要结算成功后发送自定义消息
        if(null!=mAccepUserInfo&&(mGiftMode==LiveGiftDialog.GIFT_MODE_PRIVATE_CHAT||mGiftMode == LiveGiftDialog.GIFT_MODE_PRIVATE_ROOM)){
            if(null!=mOnGiftSelectedListener&&null!=giftInfo){
                mOnGiftSelectedListener.onSendEvent(giftInfo,giftCount,(giftInfo.getPrice()*giftCount),mAccepUserInfo);
            }
        }
        //更新用户本地能量余额
        if(null!=data.getUserinfo()){
            UserManager.getInstance().setDiamonds((data.getUserinfo().getPintai_coin()+data.getUserinfo().getRmb_coin()));
            setMoney(UserManager.getInstance().getDiamonds());
            //通知所有需要关心余额的地方，更新本地余额
            ApplicationManager.getInstance().observerUpdata(Constant.OBSERVER_CMD_USER_LOCATION_INTEGRAL_CHANGED);
        }
    }

    /**
     * 交易失败
     * @param code
     * @param data
     */
    @Override
    public void showGivePresentError(int code, String data) {
        stopCountdown();//结算失败立即结束连击动作
        if(-1!=code)ToastUtils.showCenterToast(data);
    }

    /**
     * 提示充值
     */
    @Override
    public void onRecharge() {
        stopCountdown();//金币不足应该结束连击动作
        showRechargeTips();
    }
}