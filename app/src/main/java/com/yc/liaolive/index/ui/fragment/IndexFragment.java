package com.yc.liaolive.index.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.androidkun.xtablayout.XTabLayout;
import com.music.player.lib.util.MusicUtils;
import com.umeng.analytics.MobclickAgent;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.base.BaseFragment;
import com.yc.liaolive.bean.IndexMenu;
import com.yc.liaolive.bean.IndexTabInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.FragmentIndexBinding;
import com.yc.liaolive.index.manager.IndexFragController;
import com.yc.liaolive.index.ui.MainActivity;
import com.yc.liaolive.index.view.IndexTopBarLayout;
import com.yc.liaolive.media.ui.activity.MediaLocationAudioEditActivity;
import com.yc.liaolive.media.ui.activity.MediaLocationVideoListActivity;
import com.yc.liaolive.search.ui.SearchActivity;
import com.yc.liaolive.ui.adapter.AppFragmentPagerAdapter;
import com.yc.liaolive.ui.presenter.IndexLivePresenter;
import com.yc.liaolive.user.IView.UserServerContract;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ScreenUtils;
import com.yc.liaolive.util.SharedPreferencesUtil;
import com.yc.liaolive.util.ToastUtils;
import java.lang.reflect.Field;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/5/24
 * 首页 关注、热门、附近 载体
 */

public class IndexFragment extends BaseFragment<FragmentIndexBinding, IndexLivePresenter> {

    private static final String TAG = "IndexHomeFragment";
    private  List<IndexTabInfo> mMineTabInfos=null;
    private List<Fragment> mFragments;
    private MainActivity mActivity;
    private int mCurrentIndex;//当前显示的位置
    private int mGroupIndex; //当前在group中的位置index
    private String mTargetID;
    private PopupWindow mPopupWindow;

    /**
     * @param index 当前在group中的位置index
     * @param targetID
     * @return
     */
    public static IndexFragment getInstance(int index,String targetID) {
        IndexFragment fragment = new IndexFragment();
        Bundle bundle=new Bundle();
        bundle.putInt("index", index);
        bundle.putString("targetID", targetID);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if(null != arguments){
            mGroupIndex = arguments.getInt("index");
            mTargetID = arguments.getString("targetID","1");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (MainActivity) context;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_index;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //ASMR独有菜单
        if(!TextUtils.isEmpty(mTargetID)&&mTargetID.equals("2")&& UserManager.getInstance().isPostAsmrAvailable()){
            //根据配置栏目显示上传入口
            if(null!=IndexFragController.getInstance().getIndexMenus()&&IndexFragController.getInstance().getIndexMenus().size()>0){
                bindingView.indexTopBar.showSearchBar(true);
                bindingView.indexTopBar.setSearchBarRes(R.drawable.ic_add_post_media);
                bindingView.indexTopBar.setOnTopbarChangedListener(new IndexTopBarLayout.OnTopbarChangedListener() {
                    @Override
                    public void onChanged(boolean isShow) {}

                    @Override
                    public void onSearch(View view) {
                        initPopwindow(bindingView.collapseToolbar);
                    }
                });
            }
        }
    }

    @Override
    protected void initViews() {
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP ){
            bindingView.view5.setVisibility(View.VISIBLE);
        }
        mFragments = IndexFragController.getInstance().getIndexFragments(mGroupIndex);
        List<String> titles = IndexFragController.getInstance().getIndexTitles();
        final List<String> targets = IndexFragController.getInstance().getIndexTargets();
        AppFragmentPagerAdapter myAppFragmentPagerAdapter = new AppFragmentPagerAdapter(getChildFragmentManager(), mFragments, titles);
        bindingView.viewPager.setAdapter(myAppFragmentPagerAdapter);
        bindingView.viewPager.setOffscreenPageLimit(mFragments.size());
        bindingView.indexTopBar.mXTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        bindingView.indexTopBar.mXTabLayout.setupWithViewPager(bindingView.viewPager);
        //滚动监听
        ViewPager.OnPageChangeListener onChangeListener=new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentIndex=position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        bindingView.viewPager.addOnPageChangeListener(onChangeListener);
        //首次登录使用启动配置设置index，第二次及以后直接展示1V1快聊
        boolean isFirstShow = SharedPreferencesUtil.getInstance().getBoolean("isFirstShow", true);
        int index;
        if (isFirstShow) {
            SharedPreferencesUtil.getInstance().putBoolean("isFirstShow", false);
            index = UserManager.getInstance().getHomeIndex();
            index = index > 2 || index < 0 ? 0 : index;
        } else {
            index = IndexFragController.getInstance().getDefaultIndex();
        }
        bindingView.viewPager.setCurrentItem(index);
        bindingView.indexTopBar.setOnTopbarChangedListener(new IndexTopBarLayout.OnTopbarChangedListener() {
            @Override
            public void onChanged(boolean isShow) {
                //顶部
                if(isShow){
                    bindingView.indexTopBar.setVisibility(View.VISIBLE);
                }else{
                    bindingView.indexTopBar.setVisibility(View.GONE);
                }
            }
            @Override
            public void onSearch(View view) {
                SearchActivity.start(getActivity(),null);
            }
        });
        //主页样式，白色背景、增加下划线
        bindingView.indexTopBar.setTollBarBackgroundResource(R.drawable.bg_black_shape_index_white);
        bindingView.indexTopBar.showLineView(false);
        bindingView.indexTopBar.showSearchBar(UserManager.getInstance().isSearchAvailable());//取自后台配置是否可用
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            bindingView.collapseToolbar.setMinimumHeight(ScreenUtils.dpToPxInt(25f));
        }

        for (int i = 0; i < bindingView.indexTopBar.mXTabLayout.getTabCount(); i++) {
            XTabLayout.Tab tab = bindingView.indexTopBar.mXTabLayout.getTabAt(i);
            if (tab == null) return;
            //这里使用到反射，拿到Tab对象后获取Class
            Class c = tab.getClass();
            try {
                //Filed “字段、属性”的意思,c.getDeclaredField 获取私有属性。
                //"mView"是Tab的私有属性名称(可查看TabLayout源码),类型是 TabView,TabLayout私有内部类。
                Field field = c.getDeclaredField("mView");
                //值为 true 则指示反射的对象在使用时应该取消 Java 语言访问检查。值为 false 则指示反射的对象应该实施 Java 语言访问检查。
                //如果不这样会报如下错误
                // java.lang.IllegalAccessException:
                //Class com.test.accessible.Main
                //can not access
                //a member of class com.test.accessible.AccessibleTest
                //with modifiers "private"
                field.setAccessible(true);
                final View view = (View) field.get(tab);
                if (view == null) return;
                view.setTag(i);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = (int) view.getTag();
                        if (position < targets.size()) {
                            MobclickAgent.onEvent(getActivity(), "index_tab_click_"+targets.get(position));
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 右上角菜单
     * @param v
     */
    private void initPopwindow(View v) {
        if(null!=IndexFragController.getInstance().getIndexMenus()){
            if(null==mPopupWindow){
                List<IndexMenu> indexMenus = IndexFragController.getInstance().getIndexMenus();
                View view = View.inflate(getActivity(), R.layout.index_menu_layout, null);
                LinearLayout rootView = (LinearLayout) view.findViewById(R.id.root_view);
                rootView.setOrientation(LinearLayout.VERTICAL);
                int margin = ScreenUtils.dpToPxInt(15f);
                for (int i = 0; i < indexMenus.size(); i++) {
                    TextView textView =new TextView(getActivity());
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,16);
                    textView.setTextColor(Color.parseColor("#FFFFFF"));
                    textView.setText(indexMenus.get(i).getTitle());
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mPopupWindow.dismiss();
                            if(null!=v.getTag()){
                                Integer integer= (Integer) v.getTag();
                                UserManager.getInstance().checkedUploadFilePermission(String.valueOf(integer), new UserServerContract.OnNetCallBackListener() {
                                    @Override
                                    public void onSuccess(Object object) {
                                        if(Constant.MEDIA_TYPE_ASMR_VIDEO==integer){
                                            Intent intent=new Intent(getActivity(),MediaLocationVideoListActivity.class);
                                            intent.putExtra(Constant.KEY_SELECTED_KEY,Constant.KEY_SELECTED_SMAR_VIDEO);
                                            startActivityForResult(intent,Constant.SELECT_VIDEO_REQUST);
                                        }else if(Constant.MEDIA_TYPE_ASMR_AUDIO==integer){
                                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                            intent.setType("audio/*");
                                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                                            startActivityForResult(intent,Constant.SELECT_AUDIO_REQUST);
                                        }
                                    }

                                    @Override
                                    public void onFailure(int code, String errorMsg) {
                                        ToastUtils.showCenterToast(errorMsg);
                                    }
                                });
                            }
                        }
                    });
                    LinearLayout.LayoutParams layoutParams =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(margin,margin,margin,margin);
                    textView.setLayoutParams(layoutParams);
                    rootView.addView(textView);
                    textView.setTag(indexMenus.get(i).getId());
                    if(i<indexMenus.size()-1){
                        View line=new View(getActivity());
                        line.setLayoutParams(new LinearLayout.LayoutParams(-1,ScreenUtils.dpToPxInt(0.5f)));
                        line.setBackgroundColor(Color.parseColor("#1AFFFFFF"));
                        rootView.addView(line);
                    }
                }
                mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                mPopupWindow.setFocusable(true);//获得焦点，才能让View里的点击事件生效
                int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                bindingView.collapseToolbar.measure(width,width);
            }
            mPopupWindow.showAtLocation(v, Gravity.RIGHT|Gravity.TOP, ScreenUtils.dpToPxInt(16f),bindingView.collapseToolbar.getMeasuredHeight());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Constant.SELECT_AUDIO_REQUST&&null!=data&&null!=data.getData()){
            Logger.d(TAG,"onActivityResult-->DUDIO,DATA:"+data.getData());
            if(null!=data.getData()){
                String realPathFromURI = MusicUtils.getInstance().getPathFromURI(getActivity(),data.getData());
                if(!TextUtils.isEmpty(realPathFromURI)){
                    Logger.d(TAG,"onActivityResult-->realPathFromURI:"+realPathFromURI);
                    MediaLocationAudioEditActivity.start(getActivity(), Uri.parse(realPathFromURI).getPath());
                }
            }
        }
    }

    /**
     * 充气自定义View
     * @param index
     * @return
     */
    private View getTabView(int index) {
        if(null!=mMineTabInfos&&mMineTabInfos.size()>0){
            IndexTabInfo mineTabInfo = mMineTabInfos.get(index);
            View inflate = View.inflate(getActivity(), R.layout.index_list_tab_item, null);
            TextView tvItemTitle = (TextView) inflate.findViewById(R.id.tv_item_title);
            tvItemTitle.setText(mineTabInfo.getTitleName());
            int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            inflate.measure(width,width);
            inflate.requestLayout();
            //最后一个，直接隐藏分割线
            if(index== (bindingView.indexTopBar.mXTabLayout.getTabCount()-1)){
                inflate.findViewById(R.id.view_tab_item_line).setVisibility(View.GONE);
            }
            return inflate;
        }
        return null;
    }

    public void changeToTarget (String targetId) {
        int index = IndexFragController.getInstance().getIndexTargets().indexOf(targetId);
        if (index > -1 && null!=bindingView) {
            bindingView.viewPager.setCurrentItem(index);
        }
    }

    /**
     * 显示、隐藏首页TAB
     * @param flag
     */
    public void showMainTabLayout(boolean flag) {
//        if(null!=bindingView) bindingView.indexTopBar.showMainTabLayout(flag);
        if(null!=mActivity) mActivity.showMainTabLayout(flag);
    }

    /**
     * 来自主页的刷新事件
     */
    @Override
    public void fromMainUpdata() {
        super.fromMainUpdata();
        if(null==bindingView) return;
        //先展开头部BAR
        bindingView.appBarLayout.setExpanded(true,true);
        if(null!=mFragments&&mFragments.size()>bindingView.viewPager.getCurrentItem()){
            Fragment fragment = mFragments.get(bindingView.viewPager.getCurrentItem());
            if(null!=fragment && fragment instanceof IndexOneListFragment){
                ((IndexOneListFragment) fragment).fromMainUpdata();
            } else if(fragment instanceof IndexFollowListFragment){
                ((IndexFollowListFragment) fragment).fromMainUpdata();
            } else if(null!=fragment && fragment instanceof IndexVideoGroupFragment){
                ((IndexVideoGroupFragment) fragment).fromMainUpdata();
            } else if(null!=fragment && fragment instanceof NearbyUserFragment){
                ((NearbyUserFragment) fragment).fromMainUpdata();
            }
        }
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        if(VideoApplication.getInstance().isIndexRefresh()){
            if(null!=mFragments&&mFragments.size()>1){
                Fragment fragment = mFragments.get(1);//只刷新热门
                if(null!=fragment&&fragment instanceof IndexOneListFragment){
                    ((IndexOneListFragment) fragment).fromMainUpdata();
                }
            }
            VideoApplication.getInstance().setIndexRefresh(false);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //通知当前子fragment 用于页面PV统计
        if(null != mFragments && mFragments.size() > 0){
            Fragment fragment = mFragments.get(mCurrentIndex);
            fragment.setUserVisibleHint(isVisibleToUser);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null!=bindingView) bindingView.indexTopBar.onDestroy();
        mActivity=null; mCurrentIndex=0;
    }
}