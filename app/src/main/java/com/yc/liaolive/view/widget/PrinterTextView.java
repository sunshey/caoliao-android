package com.yc.liaolive.view.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;

/**
 * Created by wanglin  on 2018/7/20 18:32.
 */
public class PrinterTextView extends AppCompatTextView {

    private ValueAnimator mValueAnimator;
    private CharSequence mText;
    private BufferType mType;

    public PrinterTextView(Context context) {
        super(context);
    }

    public PrinterTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PrinterTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PrinterTextView setTextTitle(CharSequence text, BufferType type) {
        stopAnimator();
        if (TextUtils.isEmpty(text)) {
            mText = "";
            super.setText(mText, type);
            return this;
        }
        mText = text;
        mType = type;
        mValueAnimator = ValueAnimator.ofInt(0, mText.length())
                .setDuration(700);
        mValueAnimator.setInterpolator(new LinearInterpolator());
        super.setText("", type);
        return this;
    }

    public CharSequence getText() {
        return mText;
    }

    public final void updateText(CharSequence paramCharSequence) {
        if (mType != null) ;
        super.setText(paramCharSequence, mType);
    }


    public void stopAnimator() {
        if (mValueAnimator != null) {
            mValueAnimator.removeAllUpdateListeners();
            mValueAnimator.cancel();
            mValueAnimator = null;
        }
    }

    public void startAnimator() {
        if (TextUtils.isEmpty(mText)) {
            stopAnimator();
            return;
        }

        mValueAnimator.addUpdateListener(new MyUpdateListener(this));
        mValueAnimator.start();
    }

    private class MyUpdateListener implements ValueAnimator.AnimatorUpdateListener {

        PrinterTextView mTextView;

        public MyUpdateListener(PrinterTextView cusTextView) {
            mTextView = cusTextView;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            int i = (int) animation.getAnimatedValue();
            if (i > 0 && i <= mTextView.getText().length()) {
                SpannableString sss = new SpannableString(mTextView.getText().toString());
                sss.setSpan(new ForegroundColorSpan(0), i, mTextView.getText().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                mTextView.updateText(sss);
            }
        }
    }
}
