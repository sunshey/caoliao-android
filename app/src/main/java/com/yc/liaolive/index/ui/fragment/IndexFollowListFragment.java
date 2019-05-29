package com.yc.liaolive.index.ui.fragment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import com.opensource.svgaplayer.SVGADynamicEntity;
import com.umeng.analytics.MobclickAgent;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.base.BaseFragment;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.bean.BannerInfo;
import com.yc.liaolive.bean.CreateRoomInfo;
import com.yc.liaolive.common.CaoliaoController;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.databinding.FragmentIndexOneListBinding;
import com.yc.liaolive.index.adapter.LiveListOneAdapter;
import com.yc.liaolive.index.model.bean.OneListBean;
import com.yc.liaolive.live.bean.RoomList;
import com.yc.liaolive.live.ui.activity.LiveRoomPusherActivity;
import com.yc.liaolive.ui.contract.IndexFollowContract;
import com.yc.liaolive.ui.dialog.CommenNoticeDialog;
import com.yc.liaolive.ui.presenter.IndexFollowPresenter;
import com.yc.liaolive.user.IView.UserServerContract;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.user.ui.PersonCenterActivity;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.view.layout.DataChangeView;
import com.yc.liaolive.view.widget.AutoBannerLayout;
import com.yc.liaolive.view.widget.IndexGridLayoutManager;

/**
 * TinyHung@Outlook.com
 * 2018/8/18
 * 首页-关注 展示关注的用户和推荐的用户列表
 */

public class IndexFollowListFragment extends BaseFragment<FragmentIndexOneListBinding, IndexFollowPresenter> implements IndexFollowContract.View {

    private static final String TAG = "IndexFollowListFragment";
    private LiveListOneAdapter mAdapter;
    private boolean isRefresh=true;
    private DataChangeView mEmptyView;
    private int mIndex;//当前片段的Index
    private String dataType ;
    private IndexFragment mParentFragment;

    /**
     * 默认-1 1.获取1对1 2.获取一对多 -1.获取全部
     * @param dataType
     * @return
     */
    public static IndexFollowListFragment getInstance (String dataType) {
        IndexFollowListFragment fragment = new IndexFollowListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", dataType);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if(null!=arguments){
            mIndex = arguments.getInt("index");
            dataType = arguments.getString("type");
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_index_one_list;
    }

    @Override
    protected void initViews() {
        IndexGridLayoutManager gridLayoutManager = new IndexGridLayoutManager(getActivity(),2, IndexGridLayoutManager.VERTICAL,false);
        bindingView.recylerView.setLayoutManager(gridLayoutManager);
        initAdapter();
        //初始化占位布局
        mEmptyView = bindingView.loadingView;
        mEmptyView.setOnRefreshListener(new DataChangeView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(null!=mPresenter&&!mPresenter.isLoading()){
                    mEmptyView.showLoadingView();
                    refreshListData();
                }
            }
        });
//        mAdapter.setEmptyView(mEmptyView);

        //销毁监听
        bindingView.recylerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                AutoBannerLayout bannerLayout = (AutoBannerLayout) view.findViewById(R.id.item_banner);
                if(null!=bannerLayout){
                    bannerLayout.onReset();
                }
            }
        });

        //设置刷新监听
        bindingView.swiperLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(null!=mPresenter&&!mPresenter.isLoading()){
                    refreshListData();
                }
            }
        });

//        View.OnClickListener onClickListener=new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                switch (v.getId()) {
//                    //直播
//                    case R.id.btn_live:
//                        startPublish();
//                        break;
//                    //视频通话
//                    case R.id.btn_one_live:
//                        break;
//                    //测试代码
//                    case R.id.btn_text:
//                        testFunction();
//                        break;
//                }
//            }
//        };
//        bindingView.btnLive.setOnClickListener(onClickListener);
//        bindingView.btnOneLive.setOnClickListener(onClickListener);
//        bindingView.btnText.setOnClickListener(onClickListener);

        //是否允许下拉刷新
//        bindingView.appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
//            @Override
//            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//                if(verticalOffset>=0){
//                    bindingView.swiperLayout.setEnabled(true);
//                }else{
//                    bindingView.swiperLayout.setEnabled(false);
//                }
//            }
//        });

        //滚动监听
        bindingView.recylerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //滚动过程中触发
                if(null!=mParentFragment&&recyclerView.getScrollState()==1){
                    //上滑
                    if(dy<0){
                        mParentFragment.showMainTabLayout(true);
                        //下滑
                    }else if(dy>0){
                        mParentFragment.showMainTabLayout(false);
                    }
                }
            }
        });
    }

    private void initAdapter() {
        mAdapter= new LiveListOneAdapter(null,"-1");
        mAdapter.showEmptyView(true);
        //点击事件处理
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(null!=view.getTag()){
                    RoomList roomList = (RoomList) view.getTag();
                    startRoomExrat(roomList);
                }
            }
        });

        //多条目类 广告
        mAdapter.setOnMultiItemClickListener(new LiveListOneAdapter.OnMultiItemClickListener() {
            @Override
            public void onBannerClick(BannerInfo bannerInfo) {
                if(null == bannerInfo) return;
                if (!TextUtils.isEmpty(bannerInfo.getJump_url())) {
                    CaoliaoController.start(bannerInfo.getJump_url(),true,null);
                }
            }
        });
        bindingView.recylerView.setAdapter(mAdapter);
    }

    /**
     * 开启直播间
     */
    private void startPublish() {
        showProgressDialog("请稍后...");
        UserManager.getInstance().createRoom(new UserServerContract.OnNetCallBackListener() {
            @Override
            public void onSuccess(Object object) {
                if(null==getActivity()) return;
                if(!getActivity().isFinishing()) {
                    closeProgressDialog();
                    if (null != object && object instanceof CreateRoomInfo) {
                        CreateRoomInfo createRoomInfo = (CreateRoomInfo) object;
                        LiveRoomPusherActivity.statrPublish(getActivity(), createRoomInfo);
                    }
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                if(null==getActivity()) return;
                if(!getActivity().isFinishing()){
                    closeProgressDialog();
                    //如果用户被封禁直播权限了
                    if(code==Constant.REQUST_RESULT_CODE_ROOM_CLODE){
                        CommenNoticeDialog.getInstance(getActivity()).setTipsData("创建直播间失败", errorMsg, "关闭").show();
                        return;
                    }
                    //用户需要芝麻认证
                    if(NetContants.API_RESULT_NO_BIND_ZHIMA==code){
                        ToastUtils.showCenterToast("需要芝麻认证");
                        return;
                    }
                }
            }
        });
    }

    private int currentOffset=0;
    private int scollOffset(int verticalOffset) {
        int abs = Math.abs(verticalOffset);
        if(abs>currentOffset){
            currentOffset=abs;
            return 1;
        }
        if(abs<currentOffset){
            currentOffset=abs;
            return -1;
        }
        currentOffset=abs;
        return 0;
    }

    @Override
    protected void onRefresh() {
        super.onRefresh();
        showLoadingView();
        refreshListData();
    }

    /**
     * 测试功能
     */
    private void testFunction() {
//        CaoliaoController.startActivity(ZhimaAuthentiActivity.class.getName());
    }

    @Override
    public void onResume() {
        super.onResume();
        if(VideoApplication.getInstance().isIndexRefresh()&&null!=mPresenter&&!mPresenter.isLoading()){
            refreshListData();
            VideoApplication.getInstance().setIndexRefresh(false);
        }
    }


    @Override
    protected void onVisible() {
        super.onVisible();
        if(isRefresh&&null!=bindingView&&null!=mAdapter&&null!=mPresenter&&!mPresenter.isLoading()){
            if(null!=mEmptyView) mEmptyView.showLoadingView();
            refreshListData();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mParentFragment = (IndexFragment) getParentFragment();
        mPresenter=new IndexFollowPresenter();
        mPresenter.attachView(this);
    }

    /**
     * 刷新数据
     */
    private void refreshListData() {
        mPresenter.getFollows("",0, dataType);
    }

    //来自主页的刷新事件
    @Override
    public void fromMainUpdata() {
        super.fromMainUpdata();
        if(null==bindingView)return;
        if(null!=mPresenter&&!mPresenter.isLoading()){
            bindingView.swiperLayout.setRefreshing(true);
            bindingView.recylerView.scrollToPosition(0);
            mPresenter.getFollows("",0, dataType);
        }
    }

    /**
     * 进入直播间
     * @param roomList 主播基本信息
     */
    private void startRoomExrat(RoomList roomList) {
        if(null==roomList) return;
        MobclickAgent.onEvent(getActivity(), "click_player");
//        if(TextUtils.equals(Constant.INDEX_ITEM_TYPE_ROOM,roomList.getItemCategory())){
//            LiveRoomPullActivity.start(getContext(),roomList.getUserid(),roomList.getNickname(),roomList.getAvatar(),roomList.getFrontcover(), TextUtils.isEmpty(roomList.getPush_stream_flv())?roomList.getPush_stream():roomList.getPush_stream_flv(),roomList.getRoomid());
//            return;
//        }
        //前往用户中心
        PersonCenterActivity.start(getActivity(),roomList.getUserid());
    }

    //========================================联网回调===============================================

    @Override
    public void showErrorView() {

    }

    @Override
    public void complete() {

    }

    /**
     * 关注的数据
     * @param data
     * @param type 0：关注 1：推荐
     */
    @Override
    public void showLiveRooms(OneListBean data, int type) {
        isRefresh=false;
//        data.setImage_small_show("1");
        if(null!=bindingView) bindingView.swiperLayout.setRefreshing(false);
        if(null!=mEmptyView) {
            mEmptyView.stopLoading();
            mEmptyView.setVisibility(View.GONE);
        }
        if(null!=mAdapter){
            //关注的数据
            mAdapter.loadMoreComplete();
            initAdapter();
            mAdapter.setImage_small_show(data.getImage_small_show());
            mAdapter.setNewData(data.getList());
            if(mAdapter.getData().size()>1){
                mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
                    @Override
                    public void onLoadMoreRequested() {
                        mAdapter.loadMoreEnd();
                    }
                },bindingView.recylerView);
            }
        }
    }

    /**
     * 关注、推荐为空
     * @param type 0：关注 1：推荐
     */
    @Override
    public void showLiveRoomEmpty(int type) {
        isRefresh=false;
        if(null!=bindingView) bindingView.swiperLayout.setRefreshing(false);
        if(null != mEmptyView){
            mEmptyView.stopLoading();
            mEmptyView.showEmptyState("暂无关注内容",
                    "快去关自己注喜欢的小姐姐吧~", R.drawable.icon_empty_follow);
        }
    }

    @Override
    public void showLiveRoomError(int code, String errorMsg,int type) {
        if(null!=bindingView) bindingView.swiperLayout.setRefreshing(false);
        if(null!=mAdapter){
            if(mAdapter.getData().size()<=0) {
                if(null!=mEmptyView) mEmptyView.showErrorView(errorMsg);
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    //========================================测试代码===============================================

    private Bitmap drawTextAtBitmap(Drawable soucressDrawable, String text){
        if(null==soucressDrawable) return null;
        BitmapDrawable drawable= (BitmapDrawable) soucressDrawable;
        Bitmap bitmap = drawable.getBitmap();
        int x = bitmap.getWidth();
        int y = bitmap.getHeight();
        Bitmap newbit = Bitmap.createBitmap(x, y, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newbit);
        Paint paint = new Paint(); // 在原始位置0，0插入原图
        canvas.drawBitmap(bitmap, 0, 0, paint);
        paint.setColor(Color.parseColor("#dedbde"));
        paint.setTextSize(20);
        // 在原图指定位置写上字
        canvas.drawText(text, 53 , 30, paint);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        // 存储 canvas.restore();
        return newbit;
    }

    /**
     * 进行简单的文本替换
     * @return
     */
    private SVGADynamicEntity requestDynamicItem() {
        SVGADynamicEntity dynamicEntity = new SVGADynamicEntity();
        TextPaint textPaint = new TextPaint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(28);
        dynamicEntity.setDynamicText("Pony 送了一打风油精给主播", textPaint, "banner");
        return dynamicEntity;
    }

    /**
     * 富文本是会自动换行的，不要设置过长的文本
     * @return
     */
    private SVGADynamicEntity requestDynamicItemWithSpannableText() {
        SVGADynamicEntity dynamicEntity = new SVGADynamicEntity();
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("Pony 送了一打风油精给主播");
        spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.YELLOW), 0, 4, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        TextPaint textPaint = new TextPaint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(28);
        dynamicEntity.setDynamicText(new StaticLayout(
                spannableStringBuilder,
                0,
                spannableStringBuilder.length(),
                textPaint,
                0,
                Layout.Alignment.ALIGN_CENTER,
                1.0f,
                0.0f,
                false
        ), "lucky_box_big_10");
        return dynamicEntity;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mParentFragment = null;
//        if(null!=bindingView) bindingView.indexFollowTopBar.onDestroy();
    }
}
