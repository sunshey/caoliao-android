package com.yc.liaolive.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseActivity;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.ActivityContentFragmentBinding;
import com.yc.liaolive.recharge.ui.fragment.GoodsDiamondFragment;
import com.yc.liaolive.recharge.ui.fragment.RechargeAwardFragment;
import com.yc.liaolive.ui.adapter.CallNotesListAdapter;
import com.yc.liaolive.ui.fragment.LiveUserAttestFragment;
import com.yc.liaolive.ui.fragment.NoticeDetailsFragment;
import com.yc.liaolive.ui.fragment.PublicNoticeFragment;
import com.yc.liaolive.ui.fragment.TaskCenterFragment;
import com.yc.liaolive.ui.fragment.UserTagFragment;
import com.yc.liaolive.user.ui.DiamondDetailsActivity;
import com.yc.liaolive.user.ui.fragment.CallAssetListFragment;
import com.yc.liaolive.user.ui.fragment.CallNotesListFragment;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.view.widget.CommentTitleView;

/**
 * TinyHung@outlook.com
 * 2017/6/1 11:59
 * Fragment载体
 */

public class ContentFragmentActivity extends BaseActivity<ActivityContentFragmentBinding>{

    /**
     * 启动入口
     * @param context
     * @param fragmentTarge
     * @param title
     * @param userID
     */
    public static void start(android.content.Context context,int fragmentTarge,String title,String userID){
        Intent intent=new Intent(context, ContentFragmentActivity.class);
        intent.putExtra(Constant.KEY_FRAGMENT_TYPE,fragmentTarge);
        intent.putExtra(Constant.KEY_TITLE,title);
        intent.putExtra(Constant.KEY_AUTHOR_ID,userID);
        context.startActivity(intent);
    }

    /**
     * 启动入口
     * @param context
     * @param fragmentTarge
     * @param title
     * @param extra1
     * @param extra2
     * @param subTitle 副标题
     */
    public static void start(android.content.Context context,int fragmentTarge,String title,String extra1,String extra2,String subTitle){
        Intent intent=new Intent(context, ContentFragmentActivity.class);
        intent.putExtra(Constant.KEY_FRAGMENT_TYPE,fragmentTarge);
        intent.putExtra(Constant.KEY_TITLE,title);
        intent.putExtra(Constant.KEY_ID,extra1);
        intent.putExtra(Constant.KEY_URL,extra2);
        intent.putExtra(Constant.KEY_SUBTITLE,subTitle);
        context.startActivity(intent);
    }

    /**
     * 启动入口
     * @param context
     * @param fragmentTarge
     * @param title
     */
    public static void start(android.content.Context context,int fragmentTarge,String title,String extra1,String extra2){
        Intent intent=new Intent(context, ContentFragmentActivity.class);
        intent.putExtra(Constant.KEY_FRAGMENT_TYPE,fragmentTarge);
        intent.putExtra(Constant.KEY_TITLE,title);
        intent.putExtra(Constant.KEY_ID,extra1);
        intent.putExtra(Constant.KEY_URL,extra2);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_fragment);
        switchFragment();
    }

    /**
     * 根据传递的参数打开并配置Fragment
     */
    private void switchFragment() {
        Intent intent = getIntent();
        if(null==intent) {
            ToastUtils.showCenterToast("跳转错误!");
            finish();
            return;
        }

        int fragmentType = intent.getIntExtra(Constant.KEY_FRAGMENT_TYPE, 0);
        String stringExtra = intent.getStringExtra(Constant.KEY_SUBTITLE);
        bindingView.titleView.setMoreTitle(stringExtra);
        bindingView.titleView.showMoreTitle(TextUtils.isEmpty(stringExtra)?false:true);
        switch (fragmentType) {
            //系统公告
            case Constant.FRAGMENT_TYPE_PUBLIC_NOTICE:
                addReplaceFragment(new PublicNoticeFragment(),intent.getStringExtra(Constant.KEY_TITLE));
                break;
            //任务中心
            case Constant.FRAGMENT_TYPE_TASK_CENTER:
                addReplaceFragment(new TaskCenterFragment(),intent.getStringExtra(Constant.KEY_TITLE));
                break;
            //充值
            case Constant.FRAGMENT_TYPE_TASK_RECHARGE:
                addReplaceFragment(new GoodsDiamondFragment(),intent.getStringExtra(Constant.KEY_TITLE));
                break;
            //实名认证
            case Constant.FRAGMENT_TYPE_TASK_AUTHEN:
                addReplaceFragment(new LiveUserAttestFragment(),intent.getStringExtra(Constant.KEY_TITLE));
                break;
            //通知详情
            case Constant.FRAGMENT_TYPE_NOTICE_DETAILS:
                addReplaceFragment(NoticeDetailsFragment.getInstance(intent.getStringExtra(Constant.KEY_ID)),intent.getStringExtra(Constant.KEY_TITLE));
                break;
            //充值奖励
            case Constant.FRAGMENT_RECHARGE_AWARD:
                addReplaceFragment(new RechargeAwardFragment(),intent.getStringExtra(Constant.KEY_TITLE));
                 break;
            //我的通话
             case Constant.FRAGMENT_TYPE_MY_CALL:
                 addReplaceFragment(CallNotesListFragment.newInstance(intent.getStringExtra(Constant.KEY_URL), CallNotesListAdapter.ITEM_TYPE_LET,0,-1),intent.getStringExtra(Constant.KEY_TITLE));
                 break;
             //我的钻石
            case Constant.FRAGMENT_TYPE_MY_MONERY:
                addReplaceFragment(CallAssetListFragment.newInstance(intent.getStringExtra(Constant.KEY_ID), CallNotesListAdapter.ITEM_TYPE_DIAMOND),intent.getStringExtra(Constant.KEY_TITLE));
                break;
            //我的积分
            case Constant.FRAGMENT_TYPE_MY_INTEGRAL:
                addReplaceFragment(CallAssetListFragment.newInstance(intent.getStringExtra(Constant.KEY_ID), CallNotesListAdapter.ITEM_TYPE_INTEGRAL),intent.getStringExtra(Constant.KEY_TITLE));
                break;
            //我的预约
            case Constant.FRAGMENT_TYPE_MY_MAKE:
                addReplaceFragment(CallNotesListFragment.newInstance(intent.getStringExtra(Constant.KEY_URL), CallNotesListAdapter.ITEM_TYPE_MAKE,0,-1),intent.getStringExtra(Constant.KEY_TITLE));
                break;
            //用户主播标签设置
            case Constant.FRAGMENT_TYPE_USER_TAG:
                addReplaceFragment(new UserTagFragment(),intent.getStringExtra(Constant.KEY_TITLE));
                break;
            default:
        }
    }

    /**
     * 设置标题的透明度
     * @param alpha
     */
    public void setTitleAlpha(float alpha) {
        if(null!=bindingView) bindingView.titleView.setTitleAlpha(alpha);
    }

    @Override
    public void initViews() {
        bindingView.titleView.setOnTitleClickListener(new CommentTitleView.OnTitleClickListener() {
            @Override
            public void onBack(View v) {
                onBackPressed();
            }

            @Override
            public void onMoreTitleClick(View v) {
                //积分、钻石 详情
                DiamondDetailsActivity.start(ContentFragmentActivity.this,getIntent().getStringExtra(Constant.KEY_ID));
            }
        });
    }

    @Override
    public void initData() {
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }

    /**
     * 切换当前显示的Fragment
     * @param fragment
     */
    public void addReplaceFragment(Fragment fragment,String title) {
        if(null!=title&&null!=bindingView) bindingView.titleView.setTitle(title);
        try {
            android.support.v4.app.FragmentManager supportFragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, fragment, "FRAGMENT");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commitAllowingStateLoss();
        }catch (Exception e){

        }
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount()==1){
            finish();
        }else{
            super.onBackPressed();
        }
    }
}
