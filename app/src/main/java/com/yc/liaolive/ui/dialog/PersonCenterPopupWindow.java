package com.yc.liaolive.ui.dialog;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.View;

import com.yc.liaolive.R;
import com.yc.liaolive.base.BasePopupWindow;
import com.yc.liaolive.databinding.PersonCenterMenuBinding;

/**
 * Created by wanglin  on 2018/7/6 15:08.
 */
public class PersonCenterPopupWindow extends BasePopupWindow<PersonCenterMenuBinding> {
    public PersonCenterPopupWindow(Activity context) {
        super(context);
//        bindingView.getRoot().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        //设置这个参数点击外边可消失
        setBackgroundDrawable(new ColorDrawable());
        //点击外边窗口消失
        setOutsideTouchable(true);
        //获得焦点，否则无法点击
        setFocusable(true);

    }

    @Override
    public int setAnimationStyle() {
        return R.style.RightTopAnimation;
    }

    @Override
    public int setLayoutID() {
        return R.layout.person_center_menu;
    }

    @Override
    public void initViews() {
//bindingView
    }

    @Override
    public void initData() {

    }


//    public void showAsDropDown(View v, int x, int y) {
//        //点击在按钮的中上方弹出popupWindow
//        int btnWidth = v.getMeasuredWidth();
//        int btnHeight = v.getMeasuredHeight();
//
//        int popWidth = getContentView().getMeasuredWidth();
//        int popHeight = getContentView().getMeasuredHeight();
//
//        int xoff =  (int)((float)(btnWidth - popWidth)/2);//PopupWindow的x偏移值
//        int yoff =   popHeight+btnHeight ; //因为相对于按钮的上方，所以该值为负值
//
//        super.showAsDropDown(v, x, y);
////        backgroundAlpha(0.6f);
//    }


}
