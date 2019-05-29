package com.yc.liaolive.interfaces;

/**
 * TinyHung@outlook.com
 * 2017/6/27 18:43
 * 点击 Despz中话题,@a，网址链接的监听事件
 */
public interface TopicClickListener {
    void onTopicClick(String topic);
    void onUrlClick(String url);
    void onAuthoeClick(String authorID);
}
