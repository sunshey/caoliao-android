package com.yc.liaolive.view.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.lang.ref.WeakReference;

/**
 * TinyHung@Outlook.com
 * 2017/10/25.
 * 让其Item自动无限滚动
 */

public class AutoPollRecyclerView extends RecyclerView {

    private static final long TIME_AUTO_POLL = 16;
    private static final String TAG = AutoPollRecyclerView.class.getSimpleName();
    private boolean running; //标示是否正在自动轮询
    private boolean canRun;//标示是否可以自动轮询,可在不需要的是否置false
    private  WeakReference<AutoPollTask> mAutoPollTaskWeakReference;
    private boolean isSlidingToLeft = false;



    public AutoPollRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initTask();
    }

    private void initTask() {
        AutoPollTask autoPollTask = new AutoPollTask(this);
        mAutoPollTaskWeakReference = new WeakReference<AutoPollTask>(autoPollTask);
        this.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                isSlidingToLeft = dy > 0;
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int lastItemPosition = manager.findLastCompletelyVisibleItemPosition();
                int itemCount = manager.getItemCount();
                // 判断是否滑动到了最后一个item，并且是向左滑动
                if (lastItemPosition == (itemCount - 1) && isSlidingToLeft) {
                    if(null!=mOnScrollLastListener){
                        mOnScrollLastListener.onLast();
                    }
                }

            }
        });
    }


    public interface OnScrollLastListener{
        void onLast();
    }

    public void setOnScrollLastListener(OnScrollLastListener onScrollLastListener) {
        mOnScrollLastListener = onScrollLastListener;
    }

    private OnScrollLastListener mOnScrollLastListener;


    private class AutoPollTask implements Runnable {
        private final WeakReference<AutoPollRecyclerView> mReference;
        //使用弱引用持有外部类引用->防止内存泄漏
        public AutoPollTask(AutoPollRecyclerView reference) {
            this.mReference = new WeakReference<AutoPollRecyclerView>(reference);
        }
        @Override
        public void run() {
            if(null!=mReference&&null!=mReference.get()){
                AutoPollRecyclerView recyclerView = mReference.get();
                if (recyclerView != null && recyclerView.running &&recyclerView.canRun) {
                    if(recyclerView.getChildCount()>0){
                        recyclerView.scrollBy(2, 2);
                        if(null!=mAutoPollTaskWeakReference&&null!=mAutoPollTaskWeakReference.get()){
                            recyclerView.postDelayed(mAutoPollTaskWeakReference.get(),recyclerView.TIME_AUTO_POLL);
                        }
                    }
                }
            }
        }
    }
    //开启:如果正在运行,先停止->再开启
    public void start() {
        if (running) stop();
        canRun = true;
        running = true;
        if(null==mAutoPollTaskWeakReference||null==mAutoPollTaskWeakReference.get()){
            initTask();
        }
        postDelayed(mAutoPollTaskWeakReference.get(),TIME_AUTO_POLL);
    }
    public void stop(){
        running = false;
        if(null!=mAutoPollTaskWeakReference&&null!=mAutoPollTaskWeakReference.get()){
            removeCallbacks(mAutoPollTaskWeakReference.get());
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (running)
                    stop();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                if (canRun)
                    start();
                break;
        }
        return super.onTouchEvent(e);
    }
}
