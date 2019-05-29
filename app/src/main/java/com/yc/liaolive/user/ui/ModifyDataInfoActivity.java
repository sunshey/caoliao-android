package com.yc.liaolive.user.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.base.BaseActivity;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.ActivityEditUserinfoBinding;
import com.yc.liaolive.user.IView.UserServerContract;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.InputTools;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.view.widget.CommentTitleView;


/**
 * TinyHung@Outlook.com
 * 2018/10/17
 * 修改用户资料 昵称、个性签名、个人介绍 等信息
 * 在用户触发提交后立即生效
 */
public class ModifyDataInfoActivity extends BaseActivity<ActivityEditUserinfoBinding> {

    private static final String TAG = "ModifyDataInfoActivity";
    private int mMaxInput;
    private String mHintContent;
    private Animation mAnimation;
    private String mParamsKey;

    /**
     * 修改用户资料入口
     * @param context
     * @param title 标题
     * @param hintContent 回显到输入框内容
     * @param maxInput 最大输入个数限制
     * @param inputTips 附带提示内容
     * @param paramsKey 参数字段
     */
    public static void start(Activity context, String title, String hintContent, int maxInput, String inputTips, String paramsKey) {
        Intent intent=new Intent(context,ModifyDataInfoActivity.class);
        intent.putExtra("title",title);
        intent.putExtra("hintContent",hintContent);
        intent.putExtra("maxInput",maxInput);
        intent.putExtra("inputTips",inputTips);
        intent.putExtra("paramsKey",paramsKey);
        context.startActivityForResult(intent,Constant.REGISTER_MODIFY_NICKNAME_REQUST);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mParamsKey = intent.getStringExtra("paramsKey");
        if(TextUtils.isEmpty(mParamsKey)){
            ToastUtils.showCenterToast("参数错误");
            finish();
            return;
        }
        setContentView(R.layout.activity_edit_userinfo);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        mAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);
    }

    @Override
    public void initViews() {
        Intent intent = getIntent();
        if(null==intent) return;
        //回显标题
        bindingView.titleView.setTitle(intent.getStringExtra("title"));
        //回显输入框
        mHintContent = intent.getStringExtra("hintContent");
        if(!TextUtils.isEmpty(mHintContent)&&!mHintContent.startsWith("(")&&!mHintContent.endsWith(")")){
            bindingView.inputContent.setText(mHintContent);
            bindingView.inputContent.setSelection(mHintContent.length());
            bindingView.btnSubmit.setBackgroundResource(R.drawable.bt_app_style_bg_selector);
        }
        //输入框提示内容
        bindingView.inputContent.setHint(getHintText(mParamsKey));
        //最大输入限制
        mMaxInput = intent.getIntExtra("maxInput", 15);
        int length=TextUtils.isEmpty(mHintContent)?0: mHintContent.length();
        bindingView.tvNum.setText(String.format(getString(R.string.nick_count),length , mMaxInput));
        //提示语
        bindingView.tvTips.setText(intent.getStringExtra("inputTips"));
        bindingView.tvTips.setVisibility(TextUtils.isEmpty( bindingView.tvTips.getText().toString())?View.GONE:View.VISIBLE);
        //标题栏
        bindingView.titleView.setOnTitleClickListener(new CommentTitleView.OnTitleClickListener() {
            @Override
            public void onBack(View v) {
                onBackPressed();
            }
        });
        //输入框监听
        bindingView.inputContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if(!TextUtils.isEmpty(charSequence)&&charSequence.length()>0){
                    bindingView.tvNum.setText(String.format(getString(R.string.nick_count), charSequence.length(), mMaxInput));
                    bindingView.btnSubmit.setBackgroundResource(R.drawable.bt_app_style_bg_selector);
                    if(charSequence.length()>mMaxInput){
                        bindingView.inputContent.setText(Utils.subString(charSequence.toString(),mMaxInput));
                        bindingView.inputContent.setSelection( bindingView.inputContent.getText().toString().length());
                    }
                }else{
                    bindingView.tvNum.setText(String.format(getString(R.string.nick_count), 0, mMaxInput));
                    bindingView.btnSubmit.setBackgroundResource(R.drawable.bg_comment_button_false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        //提交用户信息
        bindingView.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitChanged();
            }
        });
    }

    /**
     * 获取Hint
     * @param paramsKey
     * @return
     */
    private String getHintText(String paramsKey) {
        if(TextUtils.equals(Constant.MODITUTY_KEY_NICKNAME,paramsKey)){
            return "请输入昵称";
        }
        if(TextUtils.equals(Constant.MODITUTY_KEY_SIGNTURE,paramsKey)){
            return "请输入个性签名内容";
        }
        if(TextUtils.equals(Constant.MODITUTY_KEY_SPECIALITY,paramsKey)){
            return "例如：知名演员、模特、明星";
        }
        return "请输入内容";
    }

    /**
     * 提交最新用户信息
     */
    private void submitChanged() {
        String newContent = bindingView.inputContent.getText().toString();
        if(TextUtils.isEmpty(newContent)){
            bindingView.inputContent.startAnimation(mAnimation);
            ToastUtils.showCenterToast("内容不能为空");
            return;
        }
        if(TextUtils.equals(mHintContent,newContent)){
            ToastUtils.showCenterToast("");
            bindingView.inputContent.startAnimation(mAnimation);
            ToastUtils.showCenterToast("未做任何修改");
            return;
        }

        showProgressDialog("修改中,请稍后..",false);
        //提交最新的用户信息
        UserManager.getInstance().modityUserData(mParamsKey, bindingView.inputContent.getText().toString(), new UserServerContract.OnNetCallBackListener() {
            @Override
            public void onSuccess(Object object) {
                closeProgressDialog();
                VideoApplication.getInstance().setMineRefresh(true);
                if(null!=object && object instanceof String){
                    saveLocation(((String) object));
                    setResult(Constant.REGISTER_MODIFY_NICKNAME_RESULT);
                    finish();
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                closeProgressDialog();
                ToastUtils.showCenterToast(errorMsg);
            }
        });
    }

    /**
     * 保存最新信息至本地
     * @param content
     */
    private void saveLocation(String content) {

        if(TextUtils.equals(Constant.MODITUTY_KEY_NICKNAME,mParamsKey)){
            UserManager.getInstance().setNickName(content);
            return;
        }
        if(TextUtils.equals(Constant.MODITUTY_KEY_POSITION,mParamsKey)){
            UserManager.getInstance().setPosition(content);
            return;
        }
        if(TextUtils.equals(Constant.MODITUTY_KEY_SIGNTURE,mParamsKey)){
            UserManager.getInstance().setSignature(content);
            return;
        }
        if(TextUtils.equals(Constant.MODITUTY_KEY_SEX,mParamsKey)){
            try {
                UserManager.getInstance().setSex(Integer.valueOf(content));
            }catch (RuntimeException e){

            }
            return;
        }
        //个人介绍修改
        if(TextUtils.equals(Constant.MODITUTY_KEY_SPECIALITY,mParamsKey)){
            UserManager.getInstance().setSpeciality(content);
            return;
        }
    }

    @Override
    public void initData() {

    }

    @Override
    public void onDestroy() {
        if(null!=mAnimation) mAnimation.cancel();
        mAnimation=null;
        super.onDestroy();
        if(null!=bindingView) InputTools.closeKeybord(bindingView.inputContent);
    }
}
