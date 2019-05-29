package com.yc.liaolive.ui.dialog;

import android.app.Activity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseDialog;
import com.yc.liaolive.databinding.DialogPickerDataBinding;
import com.yc.liaolive.ui.pickerview.WheelView;
import com.yc.liaolive.util.DataFactory;
import com.yc.liaolive.util.Logger;

import java.util.List;

/**
 * TinyHung@outlook.com
 * 2018/10/17
 * 数据选择选择器
 */

public class DataPickerDialog extends BaseDialog<DialogPickerDataBinding> {

    private static final String TAG = "DataPickerDialog";
    private final String subTitle;
    private final String hintSelected;
    private List<String> mData;

    /**
     *
     * @param activity
     * @param subTitle 副标题
     * @param hintSelected 回显内容
     * @return
     */
    public static DataPickerDialog getInstance(Activity activity,String subTitle,String hintSelected) {
        return new DataPickerDialog(activity,subTitle,hintSelected);
    }

    public DataPickerDialog(Activity context, String subTitle,String hintSelected) {
        super(context, R.style.ButtomDialogAnimationStyle);
        this.subTitle=subTitle;
        this.hintSelected=hintSelected;
        setContentView(R.layout.dialog_picker_data);
        initLayoutParams(Gravity.BOTTOM);
    }


    private class DataAdapter extends WheelView.WheelAdapter {

        @Override
        protected int getItemCount() {
            return null==mData?0:mData.size();
        }

        @Override
        protected String getItem(int index) {
            return mData.get(index);
        }
    }

    @Override
    public void initViews() {
        if(!TextUtils.equals("start",subTitle)) ((TextView) findViewById(R.id.tv_sub_ttile)).setText(subTitle);
        findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=mOnSelectedListener){
                    int currentItem = bindingView.viewWheel.getCurrentItem();
                    if(null!=mData&&mData.size()>currentItem){
                        String content = mData.get(currentItem);
                        DataPickerDialog.this.dismiss();
                        mOnSelectedListener.onItemSelected(currentItem,content);
                    }
                }
            }
        });
        mData = DataFactory.createPickerData(subTitle);
        DataAdapter adapter = new DataAdapter();
        bindingView.viewWheel.setAdapter(adapter);
        int index=DataFactory.getCurrentHint(mData,hintSelected);
        //回显选中的项，如果未0，默认选中中奖的项
        bindingView.viewWheel.setCurrentItem(0==index?mData.size()/2:index);
    }


    @Override
    public void dismiss() {
        super.dismiss();
    }

    public interface OnSelectedListener{
        void onItemSelected(int id,String content);
    }
    private OnSelectedListener mOnSelectedListener;

    public DataPickerDialog setOnSelectedListener(OnSelectedListener onSelectedListener) {
        mOnSelectedListener = onSelectedListener;
        return DataPickerDialog.this;
    }
}
