package com.yc.liaolive.user.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ScrollView;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.base.BaseActivity;
import com.yc.liaolive.bean.TaskInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.ActivityBindPhoneBinding;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.manager.ScreenLayoutChangedHelp;
import com.yc.liaolive.user.IView.UserServerContract;
import com.yc.liaolive.user.bean.VerificationInfo;
import com.yc.liaolive.user.manager.BindMobileManager;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.view.widget.CountdownBotton;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/8/27
 * 绑定手机号领取奖励
 */

public class BindPhoneTaskActivity extends BaseActivity<ActivityBindPhoneBinding> {

    private static final String TAG = "BindPhoneTaskActivity";
    private Animation mInputAnimation;
    private ScreenLayoutChangedHelp mScreenLayoutChangedHelp;

    /**
     * 领取成功
     */
    private boolean bindSuccess = false;

    @Override
    public void initViews() {
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    //提交
                    case R.id.btn_submit:
                        bindPhone();
                        break;
                    //返回
                    case R.id.btn_back:
                        onBackPressed();
                        break;
                }
            }
        };
        bindingView.btnBack.setOnClickListener(onClickListener);
        bindingView.btnSubmit.setOnClickListener(onClickListener);
        //验证码获取
        bindingView.btnGetCode.setOnCountdownClickListener(new CountdownBotton.OnCountdownClickListener() {
            @Override
            public void onCountDown() {
                getCode();
            }
        });
        //用户已经绑定了手机号
        if(!TextUtils.isEmpty(UserManager.getInstance().getPhone())){
            bindFinlish(false);
        }
    }

    @Override
    public void initData() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_phone);
        mInputAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);
        getData();
    }

    private void getData () {
        UserManager.getInstance().getTasks("2", new UserServerContract.OnNetCallBackListener() {
            @Override
            public void onSuccess(Object object) {
                if(null!=object && object instanceof List){
                    List<TaskInfo> taskInfos= (List<TaskInfo>) object;
                    //会员任务
                    for (TaskInfo task : taskInfos) {
                        //绑定手机号任务已经完成了
                        if(Constant.APP_TASK_BINDPHONE==task.getApp_id()||TaskInfo.TASK_ACTION_BIND_PHONE==task.getApp_id()){
                            if(null!=bindingView) bindingView.btnGetDraw.setTag(task);
                            if(1==task.getComplete()&&!TextUtils.isEmpty(UserManager.getInstance().getPhone())){
                                taskSuccess(task,false);
                            }else{
                                //可以领取
                                if(0==task.getIs_get()&&!TextUtils.isEmpty(UserManager.getInstance().getPhone())){
                                    //领取任务奖励
                                    getDraw(task);
                                }else {
                                    if(TextUtils.isEmpty(UserManager.getInstance().getPhone())){
                                        unBind();
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {

            }
        });
        mScreenLayoutChangedHelp=ScreenLayoutChangedHelp.get(BindPhoneTaskActivity.this).setOnSoftKeyBoardChangeListener(new ScreenLayoutChangedHelp.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                bindingView.viewEmpty.setVisibility(View.VISIBLE);
                bindingView.scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        bindingView.scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }

            @Override
            public void keyBoardHide(int height) {
                bindingView.viewEmpty.setVisibility(View.GONE);
                bindingView.scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        bindingView.scrollView.fullScroll(ScrollView.FOCUS_UP);
                    }
                });
            }
        });
    }

    /**
     * 绑定手机号
     */
    private void bindPhone() {
        if(null==bindingView) return;
        final String account = bindingView.inputAccount.getText().toString().trim();
        String code = bindingView.inputCode.getText().toString().trim();
        if(TextUtils.isEmpty(account)){
            ToastUtils.showCenterToast("手机号码不能为空");
            if(null!=mInputAnimation) bindingView.inputAccount.startAnimation(mInputAnimation);
            return;
        }
        if(!Utils.isPhoneNumber(account)){
            ToastUtils.showCenterToast("手机号码格式不正确");
            return;
        }
        if(TextUtils.isEmpty(code)){
            ToastUtils.showCenterToast("验证码不能为空");
            if(null!=mInputAnimation) bindingView.inputCode.startAnimation(mInputAnimation);
            return;
        }
        showProgressDialog("操作中，请稍后...",false);
        UserManager.getInstance().onBindPlatformAccount(Constant.LOGIN_TYPE_PHONE, "",
                "", account, code, new UserServerContract.OnNetCallBackListener() {
            @Override
            public void onSuccess(Object object) {
                closeProgressDialog();
                UserManager.getInstance().setPhone(account);
                bindFinlish(true);
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                bindSuccess = false;
                closeProgressDialog();
                ToastUtils.showCenterToast(errorMsg);
            }
        });
    }


    /**
     * 奖励领取成功
     * @param taskInfo
     * @param isShowAwardTips 是否弹出奖励领取弹窗
     */
    private void taskSuccess(TaskInfo taskInfo,boolean isShowAwardTips) {
        if(null==taskInfo||null==bindingView) return;
        bindingView.llBindView.setVisibility(View.GONE);
        bindingView.llBindsuccessView.setVisibility(View.VISIBLE);
        bindingView.viewLine.setVisibility(View.VISIBLE);
        //恭喜您，您的手机号188XXXX1212已绑定成功！20000钻石福利已存入至我的账户里，请注意查收.
//        bindingView.tvSuccessTips.setText(Html.fromHtml("恭喜您，您的手机号"+Utils.submitPhone(UserManager.getInstance().getPhone(),3,7)+"已经绑定成功！"+"<font color='#FA6274'><strong>"+taskInfo.getCoin()+"</strong></font>"+"钻石福利已存入至"+"<font color='#FA6274'><strong>我的</strong>→钻石</font>"+"里，请注意查收."));
        bindingView.tvBindsuccessTips.setText(Html.fromHtml("恭喜您，您的手机号"+Utils.submitPhone(UserManager.getInstance().getPhone(),3,7)+"已经绑定成功！"));
        bindingView.btnGetDraw.setText("领取成功");
        bindingView.btnGetDraw.setBackgroundResource(R.drawable.bt_bg_input_code_gray_radius);
        bindingView.btnGetDraw.setOnClickListener(null);
        VideoApplication.getInstance().setMineRefresh(true);//首页需要刷新
        ApplicationManager.getInstance().observerUpdata(Constant.OBSERVER_LIVE_ROOM_TASK_GET);//通知直播间控制器任务已经获取
        if(isShowAwardTips){
            VideoApplication.getInstance().setGetPhoneTask(true);
            VideoApplication.getInstance().setTaskCoin(taskInfo.getCoin());
            onBackPressed();
        }
    }

    /**
     * 绑定完成
     * @param isGet 是否可以领取
     */
    private void bindFinlish(boolean isGet) {
        bindSuccess = true;
        if(null==bindingView) return;
        bindingView.llBindView.setVisibility(View.GONE);
        bindingView.llBindsuccessView.setVisibility(View.VISIBLE);
        bindingView.btnGetDraw.setText("立即领取");
        bindingView.btnGetDraw.setBackgroundResource(isGet?R.drawable.bt_bg_input_code_red_radius:R.drawable.bt_bg_input_code_gray_radius);
        if(null==bindingView.btnGetDraw.getTag()){
            TaskInfo taskInfo=new TaskInfo();
            taskInfo.setApp_id(9);
            taskInfo.setCoin(1000);
            bindingView.btnGetDraw.setTag(taskInfo);
        }
        bindingView.btnGetDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=bindingView.btnGetDraw.getTag()){
                    TaskInfo taskInfo = (TaskInfo) bindingView.btnGetDraw.getTag();
                    if(null!=taskInfo){
                        getDraw(taskInfo);
                    }
                }
            }
        });
        if(!isGet)  {
            bindingView.btnGetDraw.setTag(null);
            bindingView.btnGetDraw.setOnClickListener(null);
        }
        bindingView.tvBindsuccessTips.setText(Html.fromHtml("恭喜您，您的手机号"+Utils.submitPhone(UserManager.getInstance().getPhone(),3,7)+"已绑定成功！"));
    }

    /**
     * 未绑定
     */
    private void unBind() {
        if(null==bindingView) return;
        bindingView.llBindsuccessView.setVisibility(View.GONE);
        bindingView.llBindView.setVisibility(View.VISIBLE);
        bindingView.btnGetDraw.setText("");
        bindingView.btnGetDraw.setBackgroundResource(0);
        bindingView.btnGetDraw.setOnClickListener(null);
    }

    /**
     * 获取奖励任务
     * @param taskInfo
     */
    private void getDraw(TaskInfo taskInfo) {
        UserManager.getInstance().drawTaskAward(taskInfo, new UserServerContract.OnNetCallBackListener() {
            @Override
            public void onSuccess(Object object) {
                if(null!=object&& object instanceof TaskInfo){
                    TaskInfo taskInfo= (TaskInfo) object;
                    taskSuccess(taskInfo,true);
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                ToastUtils.showCenterToast(errorMsg);
                if(null!=bindingView){
                    bindFinlish(true);
                }
            }
        });
    }

    /**
     * 准备获取验证码
     */
    private void getCode() {
        String account = bindingView.inputAccount.getText().toString().trim();
        if (TextUtils.isEmpty(account)) {
            ToastUtils.showCenterToast("手机号码不能为空");
            bindingView.inputAccount.startAnimation(mInputAnimation);
            return;
        }
        if (!Utils.isPhoneNumber(account)) {
            ToastUtils.showCenterToast("手机号码格式不正确");
            return;
        }
        getVerificationCode("86", account);
    }



    /**
     * 派发验证码
     * @param country
     * @param phone
     */
    public void getVerificationCode(String country, String phone) {
        showProgressDialog("正在请求发送验证码...",true);
        //派发验证码
        UserManager.getInstance().getVerificationCode(country, phone, new UserServerContract.OnNetCallBackListener() {
            @Override
            public void onSuccess(Object object) {
                closeProgressDialog();
                ToastUtils.showCenterToast("验证码已发送至您的手机");
                if(null!=object && object instanceof VerificationInfo){
                    VerificationInfo data= (VerificationInfo) object;
                    if(null!=bindingView){
                        int time=60;
                        if(!TextUtils.isEmpty(data.getDelay_time())){
                            time=Integer.parseInt(data.getDelay_time());
                        }
                        bindingView.btnGetCode.startCountdown(time);
                    }
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                closeProgressDialog();
                ToastUtils.showCenterToast(errorMsg);
            }
        });
    }

    @Override
    public void onDestroy() {
        if(null!=mInputAnimation) mInputAnimation.cancel(); mInputAnimation=null;
        if(null!=bindingView) bindingView.btnGetCode.onDestroy();
        if(null!= mScreenLayoutChangedHelp) mScreenLayoutChangedHelp.onDestroy();
        if(null!=bindingView) bindingView.btnGetCode.stopCountdown();
        super.onDestroy();
        mScreenLayoutChangedHelp =null;
    }

    @Override
    public void onBackPressed() {
        BindMobileManager.getInstance().getBindSubject().onNext(bindSuccess);
        BindMobileManager.getInstance().getBindSubject().onCompleted();
        finish();
    }
}