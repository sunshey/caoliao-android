package com.yc.liaolive.live.constants;

import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.live.bean.VideoChatInfo;

/**
 * Created by yangxueqin on 2018/12/6.
 * 1v1列表图片点击进入主播小视频 数据
 */

public interface IVideoPlayerView extends BaseContract.BaseView{

    void setVideoData(VideoChatInfo info);
}
