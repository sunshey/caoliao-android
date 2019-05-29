package com.yc.liaolive.live.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import com.yc.liaolive.R;
import com.yc.liaolive.gift.adapter.GiftAnchorAdapter;
import com.yc.liaolive.live.bean.PusherInfo;
import com.yc.liaolive.util.ScreenUtils;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/6/12
 * 礼物面板接收人选择器PopupWindow
 */

public class GiftEditTextPopupWindow extends android.support.v7.widget.AppCompatEditText {

    private static final String TAG = "EditTextWithPopupWindow";
    private final int DRAWABLE_RIGHT = 2;
    private final int UP_ARROW = 0;
    private final int DOWN_ARROW = 1;

    private GiftAnchorAdapter mAnchorAdapter;//接收人Adapter
    private ListPopupWindow listPopupWindow;
    private Drawable rightDrawable;


    public GiftEditTextPopupWindow(Context context) {
        super(context);
        init(context);
    }

    public GiftEditTextPopupWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GiftEditTextPopupWindow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    @SuppressLint("NewApi")
    private void init(Context context) {
        setInputType(InputType.TYPE_NULL);  // 禁止弹出输入法窗口
        // 获取EditText的DrawableRight，假如没有设置我们使用默认的图片
        rightDrawable = getCompoundDrawables()[DRAWABLE_RIGHT];
        if (rightDrawable == null) {
            rightDrawable = getResources().getDrawable(R.drawable.ic_gift_down);
        }
        rightDrawable.setBounds(0, 0, rightDrawable.getIntrinsicWidth(), rightDrawable.getIntrinsicHeight());
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], rightDrawable, getCompoundDrawables()[3]);
        listPopupWindow = new ListPopupWindow(context);
        mAnchorAdapter=new GiftAnchorAdapter(getContext(),null);
        listPopupWindow.setAdapter(mAnchorAdapter);
        listPopupWindow.setAnchorView(this);
        listPopupWindow.setWidth(ScreenUtils.dpToPxInt(170f));
        listPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        listPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        listPopupWindow.setModal(true);
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<PusherInfo> data = mAnchorAdapter.getData();
                if(null!=data&&data.size()>position){
                    PusherInfo pusherInfo = data.get(position);
                    if(null!=pusherInfo){
                        GiftEditTextPopupWindow.this.setText(pusherInfo.getUserName());
                    }
                    listPopupWindow.dismiss();
                    if(null!=onItemChangedListener) onItemChangedListener.onItemChanged(pusherInfo);
                }
            }
        });

        listPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setArrow(DOWN_ARROW);
            }
        });

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // 显示下拉列表
                    setArrow(UP_ARROW);
                    if (!listPopupWindow.isShowing()) {
                        listPopupWindow.show();
                    }
                }
                return true;
            }
        });
    }

    /**
     * 设置接收人信息
     * @param accepPusherList
     */
    public void setAccepList(List<PusherInfo> accepPusherList) {
        if(null!=accepPusherList&&accepPusherList.size()>0){
            if(null!=mAnchorAdapter){
                mAnchorAdapter.setNewData(accepPusherList);
            }
            PusherInfo pusherInfo = accepPusherList.get(0);
            if(null!=onItemChangedListener) onItemChangedListener.onItemChanged(pusherInfo);
            this.setText(pusherInfo.getUserName());
        }
    }

    private void setArrow(int flag) {
        Drawable drawable;
        if (UP_ARROW == flag) {
            drawable = getResources().getDrawable(R.drawable.ic_gift_up);
        } else {
            drawable = getResources().getDrawable(R.drawable.ic_gift_down);
        }
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        setCompoundDrawables(getCompoundDrawables()[0],
                getCompoundDrawables()[1], drawable, getCompoundDrawables()[3]);
    }

    public interface OnItemChangedListener {
        void onItemChanged(PusherInfo data);
    }
    private OnItemChangedListener onItemChangedListener;

    public void setOnItemChangedListener(OnItemChangedListener onItemChangedListener) {
        this.onItemChangedListener = onItemChangedListener;
    }
}
