package com.yc.liaolive.gift.view;

import android.content.Context;
import android.os.SystemClock;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kaikai.securityhttp.utils.LogUtil;
import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.yc.liaolive.R;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.gift.adapter.GiftItemAdapter;
import com.yc.liaolive.gift.interfaceView.GiftInterfaceView;
import com.yc.liaolive.gift.manager.GiftHelpManager;
import com.yc.liaolive.gift.manager.GiftResourceManager;
import com.yc.liaolive.live.bean.GiftInfo;
import com.yc.liaolive.live.bean.GiveGiftResultInfo;
import com.yc.liaolive.live.ui.contract.LiveGiftContact;
import com.yc.liaolive.live.ui.presenter.LiveGiftPresenter;
import com.yc.liaolive.live.util.LiveUtils;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.model.GlideCircleTransform;
import com.yc.liaolive.ui.dialog.QuireDialog;
import com.yc.liaolive.util.AnimationUtil;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ScreenUtils;
import com.yc.liaolive.view.layout.DataChangeView;
import com.yc.liaolive.view.widget.MarqueeTextView;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;

/**
 * TinyHung@Outlook.com
 * 2018/5/14
 * 礼物面板 负责礼物选中的操作逻辑
 */

public class GiftBoardView extends FrameLayout implements LiveGiftContact.View, Observer {

    private static final String TAG = "GiftBoardView";
    private TreeMap<Integer,List<GiftInfo>> mListMap;
    private GiftPagerAdapter mGiftAdapter;
    private LinearLayout mDotView;
    private ViewPager.OnPageChangeListener mOnPageChangeListener;
    private ViewPager mViewPager;
    private int count=0;
    private View currentView;//当前正选中的礼物ItemView
    private LiveGiftPresenter mPresenter;
    private int clickSelectedIndex=-1;//连续点击同一个礼物翻倍档次index位置
    private QuireDialog mQuireDialog;
    private MarqueeTextView mViewFiftDesp;//文字介绍
    public static final int GIFT_TYPE_NOIMAL=0;//普通礼物
    public static final int GIFT_TYPE_XINGYUN=1;//幸运礼物
    private boolean isRefresh=true;
    private int mClassID;//分类ID
    private GiftInterfaceView mInterFaceView;
    private int mCurrentFragmentIndex;//当前父容器所在的Index
    private boolean isInitFinlish=false;
    private boolean mIsRecovery;//是否需要恢复至上次操作状态
    private DataChangeView mLoadingView;
    private int mSourceApiType;//礼物素材适用的场景

    public GiftBoardView(Context context) {
        super(context);
        init(context,null);
    }

    public GiftBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    /**
     * 初始化
     * @param context
     * @param attrs
     */
    private void init(Context context,AttributeSet attrs) {
        View.inflate(context, R.layout.view_gift_board,this);
        //分页适配器初始化
        mViewPager = (ViewPager) findViewById(R.id.gift_view_pager);
        //默认ViewPager高度
        int viewPagerHeight = ScreenUtils.getScreenWidth() / 2;
        //默认礼物面本高度
        int itemLayoutHeight=viewPagerHeight+ScreenUtils.dpToPxInt(25f);
        ViewGroup.LayoutParams viewPagerLayoutParams = mViewPager.getLayoutParams();
        viewPagerLayoutParams.height=viewPagerHeight;
        mViewPager.setLayoutParams(viewPagerLayoutParams);
        
        LinearLayout rootView =(LinearLayout) findViewById(R.id.ll_root_view);
        ViewGroup.LayoutParams layoutParamsRoot = rootView.getLayoutParams();
        layoutParamsRoot.height=itemLayoutHeight;
        rootView.setLayoutParams(layoutParamsRoot);
        rootView.requestLayout();
        //加载中
        mLoadingView = (DataChangeView) findViewById(R.id.load_view);
        mLoadingView.getLayoutParams().height=viewPagerHeight;
        mLoadingView.setLoadingColor(getResources().getColor(R.color.white));
        mLoadingView.showLoadingView();
        //点击重新加载
        mLoadingView.setOnRefreshListener(new DataChangeView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(null!=mPresenter) mPresenter.getGiftsForType(String.valueOf(mClassID),mSourceApiType);
            }
        });
        //分页角标
        mDotView = (LinearLayout) findViewById(R.id.ll_dot_view);
        //分页适配器
        mGiftAdapter = new GiftPagerAdapter();
        mViewPager.setAdapter(mGiftAdapter);
        //小圆点切换
        mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if(isInitFinlish) GiftHelpManager.getInstance().setItemPagerIndex(position);
                if(mDotView.getChildCount()==0){
                    return;
                }
                for (int i = 0; i < mGiftAdapter.getCount(); i++) {
                    if(mDotView!=null){
                        mDotView.getChildAt(i).setEnabled(i != position);
                        View childAt = mDotView.getChildAt(i);
                        if(null!=childAt){
                            ViewGroup.LayoutParams layoutParams = childAt.getLayoutParams();
                            if(childAt.isEnabled()){
                                layoutParams.width=ScreenUtils.dpToPxInt(5f);
                                layoutParams.height=ScreenUtils.dpToPxInt(5f);
                            }else{
                                layoutParams.width=ScreenUtils.dpToPxInt(8f);
                                layoutParams.height=ScreenUtils.dpToPxInt(5f);
                            }
                            childAt.setLayoutParams(layoutParams);
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        mViewPager.addOnPageChangeListener(mOnPageChangeListener);
        //设置ViewPager切换效
        mViewPager.setOffscreenPageLimit(10);

        //选中礼物之后的文字描述信息
        mViewFiftDesp = (MarqueeTextView) findViewById(R.id.view_gift_desp);
        //仅作加载列表所用
        mPresenter = new LiveGiftPresenter();
        mPresenter.attachView(this);
        ApplicationManager.getInstance().addObserver(this);
    }


    /**
     * 绑定分类ID
     * @param typeID
     * 必须在onVisible()至前调用
     */
    public void setGiftClassID(int typeID) {
        this.mClassID=typeID;
    }

    /**
     * 片段可见，更新UI
     * @param isRecovery 是否需要恢复至上一次选中的项
     * @param sourceApiType 场景类别
     */
    public void onVisible(boolean isRecovery,int sourceApiType) {
        this.mSourceApiType=sourceApiType;
        if(isRefresh&&null!=mViewPager&&null!=mPresenter&&!mPresenter.isLoading()){
            loadDataForType(mClassID,isRecovery,sourceApiType);
        }
    }

    /**
     * 根据Type加载礼物列表
     * @param typeID 分类ID
     * @param isRecovery 是否需要恢复至上一次选中的项 只有在本地缓存中存在可恢复的项才予以恢复
     * @param sourceApiType 礼物数据场景类别
     */
    private void loadDataForType(int typeID, boolean isRecovery,int sourceApiType) {
        this.mIsRecovery=isRecovery;
        List<GiftInfo> giftCache = ApplicationManager.getInstance().getGiftCache(String.valueOf(typeID),sourceApiType);
        if(null!=giftCache&&giftCache.size()>0){
            isRefresh=false;
            showContentView();
            setData(giftCache);
            isInitFinlish=true;
        }else{
            showLoadingView();
            if(null!=mPresenter) mPresenter.getGiftsForType(String.valueOf(typeID),sourceApiType);
        }
    }

    /**
     * 引用实例
     * @param interFaceView
     */
    public void setInterFaceView(GiftInterfaceView interFaceView) {
        this.mInterFaceView=interFaceView;
    }

    /**
     * 显示内容框架
     */
    public void showContentView() {
        if(null!=mLoadingView) mLoadingView.reset();
    }

    /**
     * 显示加载中
     */
    public void showLoadingView() {
        if(null!=mLoadingView) mLoadingView.showLoadingView();
    }

    /**
     * 显示加载失败
     */
    private void showLoadErrorView() {
        if(null!=mLoadingView) mLoadingView.showErrorView();
    }

    /**
     * 显示占位布局
     */
    private void showEmptyView(){
        if(null!=mLoadingView){
            mLoadingView.showEmptyView("此分类下暂时没有礼物",R.drawable.ic_list_empty_icon);
        }
    }
    /**
     * 设置新的数据
     * @param giftInfos
     */
    public void setData(List<GiftInfo> giftInfos){
        if(null!=mListMap) mListMap.clear();
        mListMap=LiveUtils.subGroupGift(giftInfos,8);//对数据进行分组包装
        showContentView();
        if(null!=mGiftAdapter) mGiftAdapter.notifyDataSetChanged();
        addDots();
    }

    /**
     * 绘制页眉小圆点
     */
    private void addDots() {
        if (mListMap == null) {
            return;
        }
        int num=mListMap.size();
        int pxFor10Dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        if(null!=mDotView) mDotView.removeAllViews();
        for (int i=0;i<num;i++) {
            View dot = new View(getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(pxFor10Dp, pxFor10Dp);
            layoutParams.setMargins(0, 0, pxFor10Dp, 0);
            dot.setLayoutParams(layoutParams);
            dot.setBackgroundResource(R.drawable.live_arl_dot_selector);
            mDotView.addView(dot);
        }
        if(null!=mOnPageChangeListener) mOnPageChangeListener.onPageSelected(0);
    }

    /**
     * 重新还原为未选中状态
     */
    public void initializtionView(){
        GiftInfo oldGiftInfo = (GiftInfo)getTag();
        if(null!=currentView&&null!=oldGiftInfo){
            setSelected(currentView,oldGiftInfo,false);
        }
    }

    public void setFragmentIndex(int fragmentIndex) {
        this.mCurrentFragmentIndex=fragmentIndex;
    }

    /**
     * 返回持有的数据
     * @return
     */
    public Map<Integer, List<GiftInfo>> getData() {
        return mListMap;
    }

    /**
     * 注册观察者
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        if(null!=arg && arg instanceof String){
            //收到界面渲染完成通知，立即还原到上一次选中的项
            if(TextUtils.equals(Constant.OBSERVER_GIFT_RECOVERY_ADAPTER_INIT, (String) arg)){
                if(mIsRecovery&&GiftHelpManager.getInstance().isExitRecoveryState()&&mCurrentFragmentIndex==GiftHelpManager.getInstance().getFragmentIndex()){
                    if(null!=mViewPager) mViewPager.setCurrentItem(GiftHelpManager.getInstance().getItemPagerIndex());
                    if(null!=GiftHelpManager.getInstance().getOldItemView()){
                        clickSelectedIndex=GiftHelpManager.getInstance().getOldCountIndex();
                        clickSelectedIndex--;//避免还原选中状态，数量又加上去了。。。，这里的数量永远不会小于0
                        if(clickSelectedIndex<-1) clickSelectedIndex=-1;
                        currentView=GiftHelpManager.getInstance().getOldItemView();
                        GiftHelpManager.getInstance().getOldGiftInfo().setSelector(true);
                        setSelected(currentView,GiftHelpManager.getInstance().getOldGiftInfo(),true);
                    }
                }
            }
        }
    }


    //===========================================ITEM===============================================
    /**
     * 礼物面板分页片段适配器
     */
    private class GiftPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mListMap == null ? 0 : mListMap.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            GiftBoardLayoutItem item = new GiftBoardLayoutItem(mListMap.get(position),position,mCurrentFragmentIndex);
            container.addView(item.getView());
            return item.getView();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    /**
     * 礼物分页
     */
    private class GiftBoardLayoutItem {

        private View mView;

        /**
         *
         * @param giftInfos 数据
         * @param position 子界面位置
         * @param currentFragmentIndex 父容器位置
         */
        public GiftBoardLayoutItem(List<GiftInfo> giftInfos, int position, int currentFragmentIndex) {
            mView = View.inflate(getContext(), R.layout.view_gift_board_item, null);
            RecyclerView recyclerView = (RecyclerView) mView.findViewById(R.id.recycler_gift_item);
            final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4, GridLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(gridLayoutManager);
            final GiftItemAdapter adapter= new GiftItemAdapter(giftInfos,getContext());
            adapter.setOnItemClickListener(new GiftItemAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int poistion, View view,GiftInfo giftInfo) {
                    if(null!=adapter&&adapter.getData().size()>poistion){
                        selectedChangedGift(poistion,view,giftInfo);
                    }
                }
            });
            recyclerView.setAdapter(adapter);
            //在用户无操作礼物面板的情况下默认是第一个被选中
            if(mIsRecovery&&!GiftHelpManager.getInstance().isExitRecoveryState()&&0==position&&0==currentFragmentIndex){
                new android.os.Handler().postAtTime(new Runnable() {
                    @Override
                    public void run() {
                        View viewByPosition = gridLayoutManager.findViewByPosition(0);
                        if(null!=viewByPosition&&null!=viewByPosition.getTag()){
                            GiftInfo giftInfo = (GiftInfo) viewByPosition.getTag();
                            clickSelectedIndex=0;
                            clickSelectedIndex--;//避免还原选中状态，数量又加上去了。。。，这里的数量永远不会小于0
                            currentView=viewByPosition;
                            giftInfo.setSelector(true);
                            setSelected(currentView,giftInfo,true);
                        }
                    }
                }, SystemClock.uptimeMillis()+500);
            }
        }

        /**
         * 返回View
         * @return
         */
        public View getView() {
            return mView;
        }

        /**
         * ItemAdapter 发生了点击事件
         * @param poistion 当前点击的Item位置
         * @param view 当前点击的ItemView
         * @param giftInfo 当前点击的礼物
         */
        private void selectedChangedGift(int poistion, View view, GiftInfo giftInfo) {
            if(null==view||null==giftInfo) return;
            GiftInfo oldGiftInfo = (GiftInfo) getTag();
            //重复点击
            if(null!=currentView&&null!=oldGiftInfo&&giftInfo.getId()==oldGiftInfo.getId()&&giftInfo.isSelector()){
                TextView tvCount = (TextView) currentView.findViewById(R.id.tv_count);
                clickSelectedIndex++;
                List<Integer> giftCountMeals = GiftResourceManager.getInstance().getGiftCountMeals(oldGiftInfo.getPrice());
                if(clickSelectedIndex>=giftCountMeals.size()) clickSelectedIndex=0;
                count= giftCountMeals.get(clickSelectedIndex);
                //同步更新至直播间的倒计时按钮数量
                if(null!=mInterFaceView) mInterFaceView.selectedGiftChanged(giftInfo,count);

                if (null != tvCount) tvCount.setText(Html.fromHtml("x<font><big>" + count + "<big></font>"));
                AnimationUtil.playTextCountAnimation(tvCount);//重复播放仅播放数字动画
                GiftHelpManager.getInstance().setOldCountIndex(clickSelectedIndex);
                return;
            }
            //点击了其他礼物，立即停止连击功能
            if(null!=mInterFaceView) mInterFaceView.stopCountdown();
            //还原旧的选中项为未选中
            if(null!=currentView&&null!=oldGiftInfo){
                setSelected(currentView,oldGiftInfo,false);
            }
            //处理新的点击项为选中状态
            setSelected(view,giftInfo,true);
        }
    }

    /**
     * ItemAdapter 更新此控件是否选中
     * @param view itemView
     * @param giftInfo 礼物与ItemView绑定的元素
     * @param selected 是否选中
     */
    public void setSelected(View view,GiftInfo giftInfo,boolean selected){
        if(null==view||null==giftInfo) return;
        //选中的数字显示
        TextView tvCount = (TextView) view.findViewById(R.id.tv_count);
        //ITEM容器
        View itemGroup = view.findViewById(R.id.re_item_view);
        //礼物Icon
        ImageView viewIcon = view.findViewById(R.id.ic_item_icon);
        //礼物SVGA
        SVGAImageView viewSvgaIcon = (SVGAImageView) view.findViewById(R.id.view_svga_icon);
        //选中后静止的,兼容当svga内容为空
        ImageView selecetdIcon = view.findViewById(R.id.item_selected_icon);
        //标签
        TextView itemTag = (TextView) view.findViewById(R.id.item_tag);
        //标题
        View itemTitle = view.findViewById(R.id.tv_item_title);
        if(null!=itemTitle) itemTitle.setVisibility(selected?VISIBLE:GONE);
        if(null!=tvCount) tvCount.setVisibility(selected?VISIBLE:INVISIBLE);
        //已选中
        if(selected){
            if(null!=itemTag) itemTag.setVisibility(View.INVISIBLE);
            if(null!=viewIcon) viewIcon.setVisibility(GONE);//未选中静态
            itemGroup.setBackgroundResource(R.drawable.bg_gift_item_selected);
            //播放背景选中的动画
            AnimationUtil.playAnimation(itemGroup);
            //选中的礼物档次数字
            clickSelectedIndex++;
            List<Integer> giftCountMeals = GiftResourceManager.getInstance().getGiftCountMeals(giftInfo.getPrice());
            if(clickSelectedIndex<0||clickSelectedIndex>(giftCountMeals.size()-1)) return;
            if(giftCountMeals.size()>clickSelectedIndex){
                count= giftCountMeals.get(clickSelectedIndex);
            }
            if(null!=tvCount) tvCount.setText(Html.fromHtml("x<font><big>"+count+"<big></font>"));
            //礼物详情说明
            if(null!=mViewFiftDesp) mViewFiftDesp.setText(giftInfo.getDesp());
            //SVGA类型的Item
            // TODO: 2018/7/16 礼物重新上传后切换为svga字段
            if(null!=giftInfo.getSvga()&&giftInfo.getSvga().length()>0&&giftInfo.getSvga().endsWith(".svga")){
                //放大Icon View
                AnimationUtil.scalViewAnimationAdd(viewSvgaIcon);
                if(null!=selecetdIcon) selecetdIcon.setVisibility(INVISIBLE);//选中静态的
                if(null!=viewSvgaIcon){
                    //播放WEBP格式文件
//                        DraweeController controller = Fresco.newDraweeControllerBuilder()
//                                .setUri(giftInfo.getGif_src())
//                                .setAutoPlayAnimations(true)//设置为true将循环播放Gif动画
//                                .setOldController(viewIcon.getController())
//                                .build();
//                        viewIcon.setController(controller);
                    viewSvgaIcon.setVisibility(VISIBLE);//选中动画
                    //播放SVGA格式图片
                    File giftSvgaFile = GiftResourceManager.getInstance().getGiftSvga(giftInfo.getSvga());
                    playGiftSvgaAnimation(viewIcon,viewSvgaIcon,giftSvgaFile,giftInfo);
                }
                //普通的礼物动画
            }else{
                if(null!=viewSvgaIcon) viewSvgaIcon.setVisibility(INVISIBLE);
                if(null!=selecetdIcon) selecetdIcon.setVisibility(VISIBLE);
                //普通的ICON设置
                Glide.with(getContext()).load(giftInfo.getSrc())
                        .placeholder(R.drawable.ic_default_gift_icon)
                        .error(R.drawable.ic_default_gift_icon)
                        .crossFade()//渐变
                        .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                        .centerCrop()//中心点缩放
                        .skipMemoryCache(true)//跳过内存缓存
                        .transform(new GlideCircleTransform(getContext()))
                        .into(selecetdIcon);
//                    AnimationUtil.scalViewAnimationAdd(simpleDraweeView);
            }
            giftInfo.setSelector(true);
            //通知所有界面还原为初始状态
            ApplicationManager.getInstance().observerUpdata(Constant.OBSERVER_GIFT_CLEAN_SELECTED_REST);
            //标记给定
            setTag(giftInfo);
            if(null!=mInterFaceView) mInterFaceView.selectedGiftChanged(giftInfo,count);
            currentView=view;
            GiftHelpManager.getInstance().setOldCountIndex(clickSelectedIndex);
            GiftHelpManager.getInstance().setOldGiftInfo(giftInfo);
            //未选中
        }else{
            //档次位置
            clickSelectedIndex=-1;
            //选中的礼物个数
            count=0;
            //礼物详情说明
            if(null!=mViewFiftDesp) mViewFiftDesp.setText("");
            if(null!=tvCount) tvCount.setText(Html.fromHtml("x<font><big>"+count+"<big></font>"));
            if(null!=itemGroup) itemGroup.setBackgroundResource(0);
            if(null!=giftInfo) giftInfo.setSelector(false);
            if(null!=viewSvgaIcon) viewSvgaIcon.setVisibility(INVISIBLE);//动画
            if(null!=viewIcon) viewIcon.setVisibility(VISIBLE);//未选中的静态的
            //停止动画播放，还原原来大小
            if(null!=giftInfo.getSvga()&&giftInfo.getSvga().length()>0&&giftInfo.getSvga().endsWith(".svga")){
                AnimationUtil.scalViewAnimationReturn(viewSvgaIcon);
            }else{
                if(null!=selecetdIcon) {
                    selecetdIcon.setImageResource(0);
                    AnimationUtil.scalViewAnimationReturn(selecetdIcon);
                    selecetdIcon.setVisibility(INVISIBLE);//选中静态的
                }
            }
            //停止Gif动画播放
//                if(null!=viewIcon){
//                    DraweeController controller =  viewIcon.getController();
//                    if(null!=controller){
//                        Logger.d(TAG,"豪华礼物动画失去焦点处理");
//                        Animatable animatable = controller.getAnimatable();
//                        if(null!=animatable&&animatable.isRunning()){
//                            Logger.d(TAG,"豪华礼物动画失去焦点处理,停止动画预览");
//                            animatable.stop();
//                        }
//                    }
//                }
            //停止SVGA动画播放
            if(null!=viewSvgaIcon){
                viewSvgaIcon.stopAnimation(true);
            }
            //标签处理
            if(null!=giftInfo.getTag()&&giftInfo.getTag().length()>0){
                itemTag.setVisibility(VISIBLE);
                if(Constant.STRING_TAG_NEW.equals(giftInfo.getTag())){
                    itemTag.setText("");
                    itemTag.setBackgroundResource(R.drawable.ic_gift_new);
                }else{
                    itemTag.setBackgroundResource(R.drawable.bg_gift_tag_shape);
                    itemTag.setText(giftInfo.getTag());
                }
            }else{
                itemTag.setVisibility(GONE);
            }
            //选中的项
            setTag(null);
            currentView=null;
            if(null!=mInterFaceView) mInterFaceView.selectedGiftChanged(null,0);
        }
    }


    /**
     * 播放ICON SVGA动画
     * @param iconImage 静态
     * @param giftSvgaFile 动态
     * @param giftInfo 元素
     */
    private void playGiftSvgaAnimation(final ImageView iconImage, final SVGAImageView svgaImageView, File giftSvgaFile, final GiftInfo giftInfo) {
        if(null==svgaImageView) return;
        if(null!=giftSvgaFile&&giftSvgaFile.exists()){
            try {
                InputStream  inputStream = new FileInputStream(giftSvgaFile);
                SVGAParser parser = new SVGAParser(getContext());
                parser.parse(inputStream, giftSvgaFile.getName(),new SVGAParser.ParseCompletion() {
                    @Override
                    public void onComplete(@NotNull SVGAVideoEntity videoItem) {
                        if(null==svgaImageView) return;
                        //防止快速点击出现的未选中的项也播放动画
                        if(null!=getTag()){
                            GiftInfo tag = (GiftInfo) getTag();
                            if(tag.getId()==giftInfo.getId()){
                                SVGADrawable drawable = new SVGADrawable(videoItem);
                                //加载成功之后再显示或隐藏Icon
//                            if(null!=iconImage) iconImage.setVisibility(GONE);
//                            svgaImageView.setVisibility(VISIBLE);
                                svgaImageView.setImageDrawable(drawable);
                                svgaImageView.startAnimation();
                            }else{
                                svgaImageView.stopAnimation(true);
                            }
                        }
                    }
                    @Override
                    public void onError() {
                    }
                }, true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else{
            LogUtil.msg("网络地址播放");
            SVGAParser parser = new SVGAParser(getContext());
            try {
                parser.parse(new URL(giftInfo.getSvga()), new SVGAParser.ParseCompletion() {
                    @Override
                    public void onComplete(@NotNull SVGAVideoEntity videoItem) {
                        //防止快速点击出现的未选中的项也播放动画
                        if(null!=getTag()){
                            GiftInfo tag = (GiftInfo) getTag();
                            if(tag.getId()==giftInfo.getId()){
                                SVGADrawable drawable = new SVGADrawable(videoItem);
                                svgaImageView.setImageDrawable(drawable);
                                svgaImageView.startAnimation();
                            }else{
                                svgaImageView.stopAnimation(true);
                            }
                        }
                    }
                    @Override
                    public void onError() {
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }catch (RuntimeException e){

            }
        }
    }

    //========================================联网数据回调===========================================

    @Override
    public void showErrorView() {}

    @Override
    public void complete() {}

    @Override
    public void showGifts(List<GiftInfo> data,String type) {
        isRefresh=false;
        isInitFinlish=true;
        showContentView();
        setData(data);
    }

    @Override
    public void showGiftEmpty(String type) {
        isRefresh=false;
        showEmptyView();
    }

    @Override
    public void showGiftError(int code, String errMsg) {
        showLoadErrorView();
    }

    @Override
    public void showGivePresentSuccess(GiftInfo giftInfo, int giftCount, GiveGiftResultInfo data, boolean isDoubleClick) {

    }

    @Override
    public void showGivePresentError(int code, String data) {}

    @Override
    public void onRecharge() {}

    /**
     * 释放资源
     */
    public void onDestroy(){
        if(null!=ApplicationManager.getInstance()) ApplicationManager.getInstance().removeObserver(this);
        if(null!=mQuireDialog&&mQuireDialog.isShowing()) mQuireDialog.dismiss(); mQuireDialog=null;
        if(null!=mPresenter) mPresenter.detachView();
        if(null!=mListMap) mListMap.clear();
        if(null!=mDotView) mDotView.removeAllViews();
        isInitFinlish=false;
        mListMap=null;mInterFaceView=null;mViewPager=null;mIsRecovery=false;
        mOnPageChangeListener=null;mGiftAdapter=null;
    }
}
