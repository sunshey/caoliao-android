package com.yc.liaolive.interfaces;

import com.yc.liaolive.bean.PrivateMedia;

/**
 * TinyHung@Outlook.com
 * 2018/10/10
 * 照片预览 辅助状态更新
 */

public interface ImagePreviewHelp {
    /**
     *
     * @param newMediaInfo 变化的对象
     * @param groupPosition 子Pager所对应的父容器在Acticity中的位置
     */
    void newMediaInfo(PrivateMedia newMediaInfo, int groupPosition);

    /**
     * 双击事件,传递给垂直ViewPager
     * @param groupPosition
     */
    void onDoubleClick(int groupPosition);
}
