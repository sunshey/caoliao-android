package com.yc.liaolive.view.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.yc.liaolive.R;

/**
 * 下拉刷新和上拉加载更多
 */

public class PullToRefreshView extends LinearLayout {

	private static final String TAG = "PullToRefreshView";
	// refresh states
	private static final int PULL_TO_REFRESH = 2;
	private static final int RELEASE_TO_REFRESH = 3;
	private static final int REFRESHING = 4;
	// pull state
	private static final int PULL_UP_STATE = 0;
	private static final int PULL_DOWN_STATE = 1;
	/**
	 * last y
	 */
	private int mLastMotionY;
	/**
	 * lock
	 */
	private boolean mLock;
	/**
	 * header view
	 */
	private View mHeaderView;
	/**
	 * footer view
	 */
	private View mFooterView;
	/**
	 * list or grid
	 */
	private AdapterView<?> mAdapterView;
	/**
	 * scrollview
	 */
	private ScrollView mScrollView;
	/**
	 * header view height
	 */
	private int mHeaderViewHeight;
	/**
	 * footer view height
	 */
	private int mFooterViewHeight;

	/**
	 * header tip text
	 */
	private TextView mHeaderTextView;
	/**
	 * footer tip text
	 */
	private TextView mFooterTextView;

	/**
	 * header progress bar
	 */
	private ProgressBar mHeaderProgressBar;
	/**
	 * footer progress bar
	 */
	private ProgressBar mFooterProgressBar;
	/**
	 * layout inflater
	 */
	private LayoutInflater mInflater;
	/**
	 * header view current state
	 */
	private int mHeaderState;
	/**
	 * footer view current state
	 */
	private int mFooterState;
	/**
	 * 拉起或下拉;PULL_UP_STATE或PULL_DOWN_STATE
	 */
	private int mPullState;
	/**
	 * 加载中动画
	 */
	private RotateAnimation mFlipAnimation;
	/**
	 * 加载中动画
	 */
	private RotateAnimation mReverseFlipAnimation;
	/**
	 * 脚部刷新侦听器
	 */
	private OnFooterRefreshListener mOnFooterRefreshListener;
	/**
	 *头部刷新监听器
	 */
	private OnHeaderRefreshListener mOnHeaderRefreshListener;




	@SuppressWarnings("unused")
	public PullToRefreshView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PullToRefreshView(Context context) {
		super(context);
		init();
	}


	private void init() {
		//加载动画我们需要的所有代码而不是通过XML
		mFlipAnimation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mFlipAnimation.setInterpolator(new LinearInterpolator());
		mFlipAnimation.setDuration(250);
		mFlipAnimation.setFillAfter(true);
		mReverseFlipAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
		mReverseFlipAnimation.setDuration(250);
		mReverseFlipAnimation.setFillAfter(true);

		mInflater = LayoutInflater.from(getContext());
		//添加头部
		addHeaderView();
	}

	/**
	 * 初始化头部
	 */
	private void addHeaderView() {

		mHeaderView = mInflater.inflate(R.layout.refresh_header, this, false);
		mHeaderTextView = (TextView) mHeaderView.findViewById(R.id.tv_header_tip);//状态
		mHeaderProgressBar = (ProgressBar) mHeaderView.findViewById(R.id.pull_to_refresh_progress);//进度条
		//头部布局
		measureView(mHeaderView);
		mHeaderViewHeight = mHeaderView.getMeasuredHeight();
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				mHeaderViewHeight);

		params.topMargin = -(mHeaderViewHeight);

		addView(mHeaderView, params);

	}

	/**
	 * 初始化脚部
	 */
	private void addFooterView() {
		//添加脚部
		mFooterView = mInflater.inflate(R.layout.refresh_footer, this, false);
		mFooterTextView = (TextView) mFooterView.findViewById(R.id.pull_to_load_text);
		mFooterProgressBar = (ProgressBar) mFooterView.findViewById(R.id.pull_to_load_progress);

		//添加脚部布局
		measureView(mFooterView);
		mFooterViewHeight = mFooterView.getMeasuredHeight();
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				mFooterViewHeight);

		addView(mFooterView, params);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		addFooterView();
		initContentAdapterView();
	}

	/**
	 * init AdapterView like ListView,GridView and so on;or init ScrollView
	 *
	 * @description hylin 2012-7-30‰∏ãÂçà8:48:12
	 */
	private void initContentAdapterView() {
		int count = getChildCount();
		if (count < 3) {
			throw new IllegalArgumentException(
					"this layout must contain 3 child views,and AdapterView or ScrollView must in the second position!");
		}
		View view = null;
		for (int i = 0; i < count - 1; ++i) {
			view = getChildAt(i);
			if (view instanceof AdapterView<?>) {
				mAdapterView = (AdapterView<?>) view;
			}
			if (view instanceof ScrollView) {
				// finish later
				mScrollView = (ScrollView) view;
			}
		}
		if (mAdapterView == null && mScrollView == null) {

			throw new IllegalArgumentException(
					"must contain a AdapterView or ScrollView in this layout!");
		}
	}

	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent e) {
		int y = (int) e.getRawY();
		switch (e.getAction()) {
		case MotionEvent.ACTION_DOWN:

			mLastMotionY = y;
			break;
		case MotionEvent.ACTION_MOVE:

			int deltaY = y - mLastMotionY;
			if (isRefreshViewScroll(deltaY)) {
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			break;
		}
		return false;
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mLock) {
			return true;
		}
		int y = (int) event.getRawY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:

			break;
		case MotionEvent.ACTION_MOVE:
			int deltaY = y - mLastMotionY;
			if (mPullState == PULL_DOWN_STATE) {

				Log.i(TAG, " pull down!parent view move!");
				headerPrepareToRefresh(deltaY);

			} else if (mPullState == PULL_UP_STATE) {
				// PullToRefreshViewÊâßË°å‰∏äÊãâ
				Log.i(TAG, "pull up!parent view move!");
				footerPrepareToRefresh(deltaY);
			}
			mLastMotionY = y;
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			int topMargin = getHeaderTopMargin();
			if (mPullState == PULL_DOWN_STATE) {
				if (topMargin >= 0) {

					headerRefreshing();
				} else {

					setHeaderTopMargin(-mHeaderViewHeight);
				}
			} else if (mPullState == PULL_UP_STATE) {
				if (Math.abs(topMargin) >= mHeaderViewHeight
						+ mFooterViewHeight) {

					footerRefreshing();
				} else {

					setHeaderTopMargin(-mHeaderViewHeight);
				}
			}
			break;
		}
		return super.onTouchEvent(event);
	}


	private boolean isRefreshViewScroll(int deltaY) {
		if (mHeaderState == REFRESHING || mFooterState == REFRESHING) {
			return false;
		}

		if (mAdapterView != null) {

			if (deltaY > 0) {

				View child = mAdapterView.getChildAt(0);
				if (child == null) {

					return false;
				}
				if (mAdapterView.getFirstVisiblePosition() == 0
						&& child.getTop() == 0) {
					mPullState = PULL_DOWN_STATE;
					return true;
				}
				int top = child.getTop();
				int padding = mAdapterView.getPaddingTop();
				if (mAdapterView.getFirstVisiblePosition() == 0
						&& Math.abs(top - padding) <= 8) {
					mPullState = PULL_DOWN_STATE;
					return true;
				}

			} else if (deltaY < 0) {
				View lastChild = mAdapterView.getChildAt(mAdapterView
						.getChildCount() - 1);
				if (lastChild == null) {

					return false;
				}

				if (lastChild.getBottom() <= getHeight()
						&& mAdapterView.getLastVisiblePosition() == mAdapterView
								.getCount() - 1) {
					mPullState = PULL_UP_STATE;
					return true;
				}
			}
		}

		if (mScrollView != null) {

			View child = mScrollView.getChildAt(0);
			if (deltaY > 0 && mScrollView.getScrollY() == 0) {
				mPullState = PULL_DOWN_STATE;
				return true;
			} else if (deltaY < 0
					&& child.getMeasuredHeight() <= getHeight()
							+ mScrollView.getScrollY()) {
				mPullState = PULL_UP_STATE;
				return true;
			}
		}
		return false;
	}


	private void headerPrepareToRefresh(int deltaY) {
		int newTopMargin = changingHeaderViewTopMargin(deltaY);

		if (newTopMargin >= 0 && mHeaderState != RELEASE_TO_REFRESH) {
			mHeaderTextView.setText(R.string.pull_to_refresh_release_label);
			mHeaderState = RELEASE_TO_REFRESH;
		} else if (newTopMargin < 0 && newTopMargin > -mHeaderViewHeight) {
			mHeaderTextView.setText(R.string.pull_to_refresh_pull_label);
			mHeaderState = PULL_TO_REFRESH;
		}
	}

	private void footerPrepareToRefresh(int deltaY) {
		int newTopMargin = changingHeaderViewTopMargin(deltaY);

		if (Math.abs(newTopMargin) >= (mHeaderViewHeight + mFooterViewHeight)
				&& mFooterState != RELEASE_TO_REFRESH) {
			mFooterTextView.setText("松手刷新");

			mFooterState = RELEASE_TO_REFRESH;
		} else if (Math.abs(newTopMargin) < (mHeaderViewHeight + mFooterViewHeight)) {
			mFooterTextView.setText("上拉加载更多");
			mFooterState = PULL_TO_REFRESH;
		}
	}

	private int changingHeaderViewTopMargin(int deltaY) {
		LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
		float newTopMargin = params.topMargin + deltaY * 0.3f;

		if (deltaY > 0 && mPullState == PULL_UP_STATE
				&& Math.abs(params.topMargin) <= mHeaderViewHeight) {
			return params.topMargin;
		}

		if (deltaY < 0 && mPullState == PULL_DOWN_STATE
				&& Math.abs(params.topMargin) >= mHeaderViewHeight) {
			return params.topMargin;
		}
		params.topMargin = (int) newTopMargin;
		mHeaderView.setLayoutParams(params);
		invalidate();
		return params.topMargin;
	}


	private void headerRefreshing() {
		mHeaderState = REFRESHING;
		setHeaderTopMargin(0);
		mHeaderProgressBar.setVisibility(View.VISIBLE);
		mHeaderTextView.setText(R.string.pull_to_refresh_refreshing_label);
		if (mOnHeaderRefreshListener != null) {
			mOnHeaderRefreshListener.onHeaderRefresh(this);
		}
	}


	private void footerRefreshing() {
		mFooterState = REFRESHING;
		int top = mHeaderViewHeight + mFooterViewHeight;
		setHeaderTopMargin(-top);
		mFooterProgressBar.setVisibility(View.VISIBLE);
		mFooterTextView.setText("正在加载中");

		if (mOnFooterRefreshListener != null) {
			mOnFooterRefreshListener.onFooterRefresh(this);
		}
	}

	private void setHeaderTopMargin(int topMargin) {
		LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
		params.topMargin = topMargin;
		mHeaderView.setLayoutParams(params);
		invalidate();
	}


	public void onHeaderRefreshComplete() {

		setHeaderTopMargin(-mHeaderViewHeight);
		mHeaderTextView.setText(R.string.pull_to_refresh_pull_label);
		mHeaderProgressBar.setVisibility(View.GONE);
//		Time localTime = new Time("Asia/Hong_Kong");
//		localTime.setToNow();
//		String date = localTime.format("%m-%d %H:%M");

		mHeaderState = PULL_TO_REFRESH;
	}


	public void onHeaderRefreshComplete(CharSequence lastUpdated) {
		setLastUpdated(lastUpdated);
		onHeaderRefreshComplete();
	}


	public void onFooterRefreshComplete() {

		setHeaderTopMargin(-mHeaderViewHeight);
		mFooterTextView.setText("上拉加载更多");
		mFooterProgressBar.setVisibility(View.GONE);
		mFooterState = PULL_TO_REFRESH;
	}


	public void setLastUpdated(CharSequence lastUpdated) {
		if (lastUpdated != null) {
//			mHeaderUpdateTextView.setVisibility(View.VISIBLE);
//			mHeaderUpdateTextView.setText(lastUpdated);
		} else {
//			mHeaderUpdateTextView.setVisibility(View.GONE);
		}
	}

	/**

	 *
	 * @description

	 */
	private int getHeaderTopMargin() {
		LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
		return params.topMargin;
	}

	/**
	 * lock
	 *
	 * @description
	 */
	@SuppressWarnings("unused")
	private void lock() {
		mLock = true;
	}

	/**
	 * unlock
	 *
	 * @description
	 */
	@SuppressWarnings("unused")
	private void unlock() {
		mLock = false;
	}

	/**
	 * set headerRefreshListener
	 *
	 * @description
	 * @param headerRefreshListener
	 */
	public void setOnHeaderRefreshListener(
			OnHeaderRefreshListener headerRefreshListener) {
		mOnHeaderRefreshListener = headerRefreshListener;
	}

	public void setOnFooterRefreshListener(
			OnFooterRefreshListener footerRefreshListener) {
		mOnFooterRefreshListener = footerRefreshListener;
	}

	/**
	 * Interface definition for a callback to be invoked when list/grid footer
	 * view should be refreshed.
	 */
	public interface OnFooterRefreshListener {
		public void onFooterRefresh(PullToRefreshView view);
	}

	/**
	 * Interface definition for a callback to be invoked when list/grid header
	 * view should be refreshed.
	 */
	public interface OnHeaderRefreshListener {
		public void onHeaderRefresh(PullToRefreshView view);
	}
}
