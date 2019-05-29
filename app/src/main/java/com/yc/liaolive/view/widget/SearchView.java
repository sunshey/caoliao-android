package com.yc.liaolive.view.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.yc.liaolive.R;
import com.yc.liaolive.search.adapter.AutoCompletSearchAdapter;
import com.yc.liaolive.util.ToastUtils;


/**
 * @time 2017/06/07 19:20
 * @des $自动匹配搜索框
 */

public class SearchView extends LinearLayout implements View.OnClickListener {

    private static final String TAG ="SearchView";
    private EditText et_Input;
    private ImageView iv_Delete;
    private ImageView iv_back;
    private Context mContext;
    private ListView lv_Tips;
    /**
     * 自动补全adapter
     */
    private AutoCompletSearchAdapter mAutoCompleteAdapter;


    /**
     * search view回调方法
     */
    public interface OnSearchViewListener {

        /**
         * 更新自动补全内容
         */
        void onRefreshAutoComplete(String text);

        /**
         * 开始搜索
         */
        void onSearch(String text);

        /**
         * 返回键
         */
        void onBack();
    }

    //监听器
    private OnSearchViewListener mOnSearchViewListener;

    /**
     * 设置搜索回调接口
     *
     * @param listener 监听者
     */
    public void setSearchViewListener(OnSearchViewListener listener) {
        mOnSearchViewListener = listener;
    }


    public SearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.search_bar_layout, this);
        initViews();
    }

    /**
     * 初始化
     */
    private void initViews() {

        et_Input = (EditText) findViewById(R.id.search_et_input);
        iv_Delete = (ImageView) findViewById(R.id.search_iv_delete);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        lv_Tips = (ListView) findViewById(R.id.search_lv_tips);
        ImageView iv_search = (ImageView) findViewById(R.id.iv_search);

        iv_Delete.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        iv_search.setOnClickListener(this);

        //添加输入框变化监听事件
        et_Input.addTextChangedListener(new onEditChangedListener());
        //对软键盘上面的确认键进行监听
        et_Input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (0 == KeyEvent.ACTION_DOWN) {
                    Log.d(TAG, "onEditorAction: 点击了软键盘上面的搜索");
                    if (!TextUtils.isEmpty(et_Input.getText().toString().trim())) {
                        //回调通知调用者进行搜索
                        notifyStartSearching();
                    }
                }
                return true;
            }
        });

        //输入框的触摸事件
        et_Input.setOnTouchListener(new OnTouchListener() {
            //按住和松开的标识
            int touch_flag = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                touch_flag++;
                if (touch_flag == 2) {
                    Log.d(TAG, "onTouch: 点击了输入框");
                }
                return false;
            }
        });
    }


    /**
     * 刷新适配器
     * @param adapter
     */
    public void setAutoCompleteAdapter(AutoCompletSearchAdapter adapter) {
        if(null!=adapter){
            this.mAutoCompleteAdapter = adapter;
            lv_Tips.setAdapter(mAutoCompleteAdapter);
            lv_Tips.setOnItemClickListener(onItemClickListener);
        }else{
            lv_Tips.setVisibility(GONE);
        }
    }


    /**
     * 自动匹配的条目点击事件
     */
    private AdapterView.OnItemClickListener onItemClickListener=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String text = mAutoCompleteAdapter.getItem(position).toString();
            et_Input.setText(text);
            et_Input.setSelection(text.length());
            notifyStartSearching();
        }
    };



    /**
     * 通知监听者 进行搜索操作
     */
    private void notifyStartSearching(){

        String key = et_Input.getText().toString().trim();
        if(!TextUtils.isEmpty(key)){
            lv_Tips.setVisibility(GONE);//隐藏自动补全
            //隐藏软键盘
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            if (null!=mOnSearchViewListener) {
                mOnSearchViewListener.onSearch(key);
            }
        }else{
            ToastUtils.showCenterToast("搜索关键字不能为空");
        }
    }




    /**
     * 输入框变化的监听事假
      */
    private class onEditChangedListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            if(!TextUtils.isEmpty(charSequence)&&charSequence.length()>0){
                iv_Delete.setVisibility(VISIBLE);
                lv_Tips.setVisibility(VISIBLE);
                lv_Tips.setAdapter(null);
                //更新autoComplete数据
                if (mOnSearchViewListener != null) {
                    mOnSearchViewListener.onRefreshAutoComplete(charSequence + "");
                }
            }else{
                iv_Delete.setVisibility(GONE);
                lv_Tips.setVisibility(GONE);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //搜索
            case R.id.iv_search:
                notifyStartSearching();
                break;
            //清空输入框
            case R.id.search_iv_delete:
                et_Input.setText("");
                iv_Delete.setVisibility(GONE);
                break;
            //返回键
            case R.id.iv_back:
               if(null!=mOnSearchViewListener){
                   mOnSearchViewListener.onBack();
               }
                break;
        }
    }
}
