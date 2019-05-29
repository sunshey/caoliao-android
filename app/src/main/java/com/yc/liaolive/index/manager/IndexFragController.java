package com.yc.liaolive.index.manager;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import com.yc.liaolive.bean.IndexMenu;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.index.ui.fragment.IndexAsmrAudioFragment;
import com.yc.liaolive.index.ui.fragment.IndexEmptyFragment;
import com.yc.liaolive.index.ui.fragment.IndexFollowListFragment;
import com.yc.liaolive.index.ui.fragment.IndexFragment;
import com.yc.liaolive.index.ui.fragment.IndexOneListFragment;
import com.yc.liaolive.index.ui.fragment.IndexVideoGroupFragment;
import com.yc.liaolive.index.ui.fragment.IndexVideoListFragment;
import com.yc.liaolive.index.ui.fragment.IndexWebViewFragment;
import com.yc.liaolive.index.ui.fragment.NearbyUserFragment;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.mine.ui.fragment.IndexMineFragment;
import com.yc.liaolive.msg.ui.fragment.IndexMessageFragment;
import com.yc.liaolive.start.manager.StartManager;
import com.yc.liaolive.start.model.bean.ConfigBean;
import com.yc.liaolive.start.model.bean.IndexShowConfig;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.SharedPreferencesUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * 首页各tab target_id 路由 作用于子页面
 * Created by yangxueqin on 2019/1/21.
 */

public class IndexFragController {

    private static final String TAG = "IndexFragController";
    /**
     交友（底部）  target_id 1
     交友--推荐         target_id 11
     交友--附近         target_id 12
     交友--视频         target_id 13
     交友--视频-推荐     target_id 131  filter_type
     交友--视频-最新     target_id 131  filter_type
     交友--视频-付费     target_id 131  filter_type
     交友--视频-热门     target_id 131  filter_type
     交友--视频-ASMR     target_id 132  filter_type
     交友--图片         target_id 14
     交友--图片-推荐     target_id 141  filter_type
     交友--图片-最新     target_id 141  filter_type
     交友--关注         target_id 15
     直播（底部）  target_id 2
     直播--推荐         target_id 22
     直播--关注         target_id 21
     消息（底部）  target_id 3
     我的（底部）  target_id 4
     WEBFragment   target_id 23
     Activity      target_id 24 其他原生的界面，底部组件不参与选中逻辑，直接跳走
     ASMR音频            target_id 25 ASMR 音频文件
     ASMR视频            target_id 22 ASMR 视频文件
     ASMR模块容器   target_id 27 ASMR视频
     ASMR模块容器   target_id 28 ASMR音频
     */

    private static IndexFragController mController;

    public static IndexFragController getInstance () {
        if (null == mController) {
            mController = new IndexFragController();
        }
        return mController;
    }
    //ASMR菜单
    private List<IndexMenu> mIndexMenus=null;

    /**
     * 生成底部fragment
     * @return
     */
    public List<Fragment> getBottomFragment() {
        List<Fragment> fragments = new ArrayList<>();
        if (StartManager.getInstance().getPageBeanList() != null
                && StartManager.getInstance().getPageBeanList().size() > 0) {
            List<ConfigBean.PageBean> pageBeanList = StartManager.getInstance().getPageBeanList();
            for (int i = 0 ;  i < pageBeanList.size(); i ++) {
                ConfigBean.PageBean pageBean = pageBeanList.get(i);
                switch (pageBean.getTarget_id()) {
                    //交友
                    case "1":
                        fragments.add(IndexFragment.getInstance(i,pageBean.getTarget_id()));
                        break;
                    //ASMR、一对多大厅
                    case "2":
                        fragments.add(IndexFragment.getInstance(i,pageBean.getTarget_id()));
                        break;
                    //消息
                    case "3":
                        fragments.add(new IndexMessageFragment());
                        break;
                    //我的
                    case "4":
                        fragments.add(new IndexMineFragment());
                        break;
                    //主页WEB
                    case "23":
                        fragments.add(IndexWebViewFragment.newInstance(pageBean.getOpen_url(),pageBean.getSub_title(),i));
                        break;
                    //其他原生界面，不参与切换及选中效果
                    case "24":
                        fragments.add(IndexEmptyFragment.newInstance(pageBean.getOpen_url(),i));
                        break;
                }
            }
        }
        return fragments;
    }

    private List<String> indexTitles = new ArrayList<>();
    private List<String> indexTargets = new ArrayList<>();

    private int defaultIndex = 0;

    public List<String> getIndexTitles() {
        return indexTitles;
    }

    public int getDefaultIndex() {
        return defaultIndex;
    }

    public List<String> getIndexTargets() {
        return indexTargets;
    }

    public List<Fragment> getIndexFragments (int groupIndex) {
        indexTitles.clear();
        indexTargets.clear();
        List<Fragment> fragments = new ArrayList<>();
        if (StartManager.getInstance().getPageBeanList() != null
                && StartManager.getInstance().getPageBeanList().size() > 0) {
            ConfigBean.PageBean pageBean = StartManager.getInstance().getPageBeanList().get(groupIndex);
            //打开子目录的当前索引
            try {
                defaultIndex = Integer.parseInt(pageBean.getShow_index());
            } catch (NumberFormatException e){
                defaultIndex = 0;
            }
            if (pageBean.getSon_page() != null && pageBean.getSon_page().size() > 0){
                List<ConfigBean.PageBean.SonPageBeanX> pageBeanXList = pageBean.getSon_page();
                for (int i = 0 ;  i < pageBeanXList.size(); i ++) {
                    ConfigBean.PageBean.SonPageBeanX pageBeanX = pageBeanXList.get(i);
                    indexTitles.add(pageBeanX.getText());
                    indexTargets.add(pageBeanX.getTarget_id());
                    switch (pageBeanX.getTarget_id()) {
                        case "11":
                            fragments.add(IndexOneListFragment.getInstance(groupIndex, "1"));
                            break;
                        //交友 二级或三级目录
                        case "12":
                            fragments.add(new NearbyUserFragment());
                            break;
                        //视频父容器
                        case "13":
                            fragments.add(IndexVideoGroupFragment.newInstance(groupIndex, i, 1,pageBeanX.getTarget_id()));
                            break;
                        case "131":
                            fragments.add(IndexVideoListFragment.newInstance(groupIndex, i,
                                    NetContants.getInstance().URL_FILE_INDEX_TOP(), Constant.MEDIA_TYPE_VIDEO, pageBeanX.getFilter_type()));
                            break;
                        case "132":
                            Logger.d(TAG,"ASMR视频");
                            fragments.add(IndexVideoListFragment.newInstance(groupIndex, i,
                                    NetContants.getInstance().URL_FILE_INDEX_TOP(), Constant.MEDIA_TYPE_ASMR_VIDEO, pageBeanX.getFilter_type()));
                            break;
                        //图片父容器
                        case "14":
                            fragments.add(IndexVideoGroupFragment.newInstance(groupIndex, i, 0,pageBeanX.getTarget_id()));
                            break;
                        case "141":
                            fragments.add(IndexVideoListFragment.newInstance(groupIndex, i,
                                    NetContants.getInstance().URL_FILE_INDEX_TOP(),Constant.MEDIA_TYPE_IMAGE, pageBeanX.getFilter_type()));
                            break;
                        case "15": //关注 1v1
                            fragments.add(IndexFollowListFragment.getInstance("1"));
                            break;
                        case "21": //关注
                            fragments.add(IndexFollowListFragment.getInstance("2"));
                            break;
                        //ASMR 视频，二级或三级目录
                        case "22":
                            addIndexMenus(Constant.MEDIA_TYPE_ASMR_VIDEO,"上传ASMRS视频");
                            fragments.add(IndexOneListFragment.getInstance(groupIndex, pageBeanX.getFilter_type()));
                            break;
                        //WebView
                        case "23":
                            fragments.add(IndexWebViewFragment.newInstance(pageBean.getOpen_url(),pageBean.getSub_title(),i));
                            break;
                        //ASMR 音频，二级或三级目录
                        case "25":
                            addIndexMenus(Constant.MEDIA_TYPE_ASMR_AUDIO,"上传ASMRS音频");
                            fragments.add(IndexAsmrAudioFragment.getInstance(groupIndex,i,NetContants.getInstance().URL_FILE_INDEX_TOP(),
                                    Constant.MEDIA_TYPE_ASMR_AUDIO, pageBean.getTarget_id()));
                            break;
                        //二级目录"附近"容器
                        case "26":
                            fragments.add(IndexVideoGroupFragment.newInstance(groupIndex, i, 1,pageBeanX.getTarget_id()));
                            break;
                        //二级目录"ASMR视频"容器
                        case "27":
                            addIndexMenus(Constant.MEDIA_TYPE_ASMR_VIDEO,"上传ASMRS视频");
                            fragments.add(IndexVideoGroupFragment.newInstance(groupIndex, i, 1,pageBeanX.getTarget_id()));
                            break;
                        //二级目录"ASMR音频"容器
                        case "28":
                            addIndexMenus(Constant.MEDIA_TYPE_ASMR_AUDIO,"上传ASMRS音频");
                            fragments.add(IndexVideoGroupFragment.newInstance(groupIndex, i, 1,pageBeanX.getTarget_id()));
                            break;
                    }
                }
            }

        }
        return fragments;
    }

    /**
     * 生成菜单
     * @param id
     * @param title
     */
    private void addIndexMenus(int id, String title) {
        if(null==mIndexMenus){
            mIndexMenus=new ArrayList<>();
        }
        boolean existMenus=false;
        for (int i = 0; i < mIndexMenus.size(); i++) {
            if(mIndexMenus.get(i).getId()==id){
                existMenus=true;
                break;
            }
        }
        if(!existMenus){
            mIndexMenus.add(new IndexMenu(id,title));
        }
    }


    private List<String> subTitles = new ArrayList<>();

    private int subDefaultIndex = 0;

    public List<String> getSubTitles() {
        return subTitles;
    }

    public int getSubDefaultIndex() {
        return subDefaultIndex;
    }

    public List<Fragment> getSubFragments (int mainIndex, int groupIndex) {
        subTitles.clear();
        List<Fragment> fragments = new ArrayList<>();
        if (StartManager.getInstance().getPageBeanList() != null
                && StartManager.getInstance().getPageBeanList().size() > 0) {
            ConfigBean.PageBean pageBean = StartManager.getInstance().getPageBeanList().get(mainIndex);
            if (pageBean.getSon_page() != null && pageBean.getSon_page().size() > 0){
                List<ConfigBean.PageBean.SonPageBeanX> pageBeanXList = pageBean.getSon_page();
                if (pageBeanXList.get(groupIndex).getSon_page() != null && pageBeanXList.get(groupIndex).getSon_page().size() > 0){
                    //打开子目录的当前索引
                    try {
                        subDefaultIndex = Integer.parseInt(pageBeanXList.get(groupIndex).getShow_index());
                    } catch (NumberFormatException e){
                        subDefaultIndex = 0;
                    }
                    List<ConfigBean.PageBean.SonPageBeanX.SonPageBean> pageBeanList = pageBean.getSon_page().get(groupIndex).getSon_page();
                    for (int i = 0 ;  i < pageBeanList.size(); i ++) {
                        ConfigBean.PageBean.SonPageBeanX.SonPageBean pageBeanX = pageBeanList.get(i);
                        subTitles.add(pageBeanX.getText());
                        switch (pageBeanX.getTarget_id()) {
                            case "11":
                                fragments.add(IndexOneListFragment.getInstance(groupIndex, "1"));
                                break;
                            case "12":
                                fragments.add(new NearbyUserFragment());
                                break;
                            case "13":
                                fragments.add(IndexVideoGroupFragment.newInstance(groupIndex, i, 1,pageBeanX.getTarget_id()));
                                break;
                            case "131":
                                fragments.add(IndexVideoListFragment.newInstance(groupIndex, i,
                                        NetContants.getInstance().URL_FILE_INDEX_TOP(), Constant.MEDIA_TYPE_VIDEO, pageBeanX.getFilter_type()));
                                break;
                            case "132":
                                fragments.add(IndexVideoListFragment.newInstance(groupIndex, i,
                                        NetContants.getInstance().URL_FILE_INDEX_TOP(), Constant.MEDIA_TYPE_ASMR_VIDEO, pageBeanX.getFilter_type()));
                                break;
                            case "14":
                                fragments.add(IndexVideoGroupFragment.newInstance(groupIndex, i, 0,pageBeanX.getTarget_id()));
                                break;
                            case "141":
                                fragments.add(IndexVideoListFragment.newInstance(groupIndex, i,
                                        NetContants.getInstance().URL_FILE_INDEX_TOP(),Constant.MEDIA_TYPE_IMAGE, pageBeanX.getFilter_type()));
                                break;
                            case "15": //关注 1v1
                                fragments.add(IndexFollowListFragment.getInstance("1"));
                                break;
                            case "21": //关注
                                fragments.add(IndexFollowListFragment.getInstance("2"));
                                break;
                            //ASMR 视频
                            case "22":
                                fragments.add(IndexOneListFragment.getInstance(groupIndex, pageBeanX.getFilter_type()));
                                break;
                            //ASMR 音频
                            case "25":
                                fragments.add(IndexAsmrAudioFragment.getInstance(groupIndex,i,NetContants.getInstance().URL_FILE_INDEX_TOP(),Constant.MEDIA_TYPE_ASMR_AUDIO, pageBeanX.getFilter_type()));
                                break;
                        }
                    }
                }
            }
        }
        return fragments;
    }

    /**
     * 获取首页配置的显示位置
     * @return
     */
    public String getMainIndex() {
        ConfigBean configBean = (ConfigBean) ApplicationManager.getInstance().getCacheExample().getAsObject(Constant.APP_CONFIG);
        if(null!=configBean&&null!=configBean.getHome_pgae_show_index()){
            IndexShowConfig pgaeShowIndex = configBean.getHome_pgae_show_index();
            String string = SharedPreferencesUtil.getInstance().getString(Constant.USER_CONFIG_FIRST,null);
            if(TextUtils.isEmpty(string)){
                SharedPreferencesUtil.getInstance().putString(Constant.USER_CONFIG_FIRST,"start");
                return pgaeShowIndex.getFirst();
            }
            return pgaeShowIndex.getSecond();
        }
        return "0";
    }

    public List<IndexMenu> getIndexMenus() {
        return mIndexMenus;
    }
}