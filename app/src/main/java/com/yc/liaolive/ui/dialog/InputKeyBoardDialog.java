package com.yc.liaolive.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseDialog;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.bean.ChatEmoji;
import com.yc.liaolive.databinding.DialogInputKeyboardLayoutBinding;
import com.yc.liaolive.ui.adapter.EmojiListAdapter;
import com.yc.liaolive.util.CommonUtils;
import com.yc.liaolive.util.EmotionUtils;
import com.yc.liaolive.util.InputTools;
import com.yc.liaolive.util.ScreenUtils;
import com.yc.liaolive.util.TextViewTopicSpan;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.util.Utils;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2017/10/19
 * 对输入框进行包装，避免了键盘弹起布局顶起的问题
 */

public class InputKeyBoardDialog extends BaseDialog<DialogInputKeyboardLayoutBinding>{

    private EmojiListAdapter mEmojiListAdapter;
    private int content_charMaxNum=99;//留言字数上限
    private String mHintTtext="写评论...";
    private String indexOutErrortex="评论内容超过字数限制";
    private boolean faceIsForbidden;//表情面板是否禁用
    private final InputMethodManager mInputMethodManager;
    private int mMode;

    public static InputKeyBoardDialog getInstance(@NonNull Activity context){
        return new InputKeyBoardDialog(context);
    }

    public InputKeyBoardDialog(@NonNull Activity context) {
        super(context, R.style.ButtomDialogAnimationStyle);
        setContentView(R.layout.dialog_input_keyboard_layout);
        mInputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        initLayoutParams(Gravity.BOTTOM);
        initEmotionData();
    }

    @Override
    public void initViews() {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) bindingView.llFacechoose.getLayoutParams();
        layoutParams.height = ScreenUtils.dpToPxInt(230);
        bindingView.llFacechoose.setLayoutParams(layoutParams);
        bindingView.inputEditText.setHint("写评论..");
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.iv_btn_face:
                        if(faceIsForbidden){
                            ToastUtils.showCenterToast("表情不可用!");
                            return;
                        }
                        showFaceBoard();
                        if(null!=mInputMethodManager)  mInputMethodManager.hideSoftInputFromWindow(bindingView.inputEditText.getWindowToken(), 0);
                        break;
                    case R.id.btn_submit:
                        if(bindingView.inputEditText.getText().toString().trim().isEmpty()){
                            return;
                        }
                        if(null!= mOnActionFunctionListener){
                            if(mOnActionFunctionListener.isAvailable()){
                                if(mMode==0){
                                    InputKeyBoardDialog.this.dismiss();
                                }
                                mOnActionFunctionListener.onSubmit(bindingView.inputEditText.getText().toString(),false);
                                bindingView.inputEditText.setText("");
                            }
                        }
                        break;

                    case R.id.input_edit_text:
                        if(bindingView.llFacechoose.getVisibility()!=View.GONE){
                            bindingView.llFacechoose.setVisibility(View.GONE);
                            bindingView.ivBtnFace.setImageResource(R.drawable.ic_message_face);
                        }
                        break;
                }
            }
        };
        bindingView.ivBtnFace.setOnClickListener(onClickListener);
        bindingView.btnSubmit.setOnClickListener(onClickListener);
        //获取EditText的点击事件，关闭表情面板
        bindingView.inputEditText.setOnClickListener(onClickListener);
        //监听输入框文字
        bindingView.inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!TextUtils.isEmpty(charSequence)&&charSequence.length()>0){
                    bindingView.btnSubmit.setBackgroundResource(R.drawable.full_room_input_send_bg);
                    if(charSequence.length()>content_charMaxNum){
                        ToastUtils.showCenterToast(indexOutErrortex);
                        bindingView.inputEditText.setText(Utils.subString(charSequence.toString(),content_charMaxNum));
                        bindingView.inputEditText.setSelection( bindingView.inputEditText.getText().toString().length());
                    }
                }else{
                    bindingView.btnSubmit.setBackgroundResource(R.drawable.full_room_input_unsend_bg);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        bindingView.llBottomInputBoart.setBackgroundColor(getContext().getResources().getColor(R.color.white));
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if(null!=mInputMethodManager)  mInputMethodManager.hideSoftInputFromWindow(bindingView.inputEditText.getWindowToken(), 0);
        if(null!=bindingView&&bindingView.llFacechoose.getVisibility()!=View.GONE){
            bindingView.llFacechoose.setVisibility(View.GONE);
            bindingView.ivBtnFace.setImageResource(R.drawable.ic_message_face);
        }
    }

    @Override
    public void show() {
        super.show();
        if(null!=mInputMethodManager) mInputMethodManager.showSoftInput(bindingView.inputEditText, InputMethodManager.SHOW_FORCED);
    }

    /**
     *
     * @param mode 0：点击确认按钮后关闭输入框 1：不关闭
     */
    public InputKeyBoardDialog setMode(int mode){
        this.mMode=mode;
        return this;
    }
    /**
     * 初始化表情
     */
    private void initEmotionData() {
        //表情集合
        List<ChatEmoji> chatEmojis = EmotionUtils.getInstance().emojis;
        mEmojiListAdapter = new EmojiListAdapter(R.layout.list_item_face,chatEmojis);
        if(null!=bindingView){
            bindingView.recylerView.setLayoutManager(new GridLayoutManager(mActivity,7,GridLayoutManager.VERTICAL,false));
            bindingView.recylerView.setHasFixedSize(true);
            mEmojiListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    if(null!=view.getTag()){
                        ChatEmoji emoji = (ChatEmoji) view.getTag();
                        if (!TextUtils.isEmpty(emoji.getCharacter())) {
                            SpannableString spannableString = EmotionUtils.getInstance().addFace(getContext(), emoji.getAbsolutePath(), emoji.getCharacter(),(int) bindingView.inputEditText.getTextSize());
                            bindingView.inputEditText.append(spannableString);
                        }
                    }
                }
            });
            bindingView.recylerView.setAdapter(mEmojiListAdapter);
        }
    }

    /**
     * 显示表情面板
     */
    private void showFaceBoard() {
        if(null==bindingView) return;
        //手动开启或关闭键盘
        if(bindingView.llFacechoose.getVisibility()!=View.GONE){
            bindingView.llFacechoose.setVisibility(View.GONE);
            bindingView.ivBtnFace.setImageResource(R.drawable.ic_message_face);
        }else{
            if (bindingView.llFacechoose.getVisibility() != View.VISIBLE) {
                bindingView.llFacechoose.setVisibility(View.VISIBLE);
                bindingView.ivBtnFace.setImageResource(R.drawable.ic_message_keybord);
            }
        }
    }

    /**
     * 设置确认按钮文字
     * @param submitText
     */
    public InputKeyBoardDialog setSubmitText(String submitText) {
        if(null!= bindingView){
            bindingView.btnSubmit.setText(submitText);
        }
        return this;
    }

    /**
     * 隐藏表情面板
     */
    public void hideFaceBtn() {
        faceIsForbidden=true;
    }

    /**
     * 回显输入框文字
     * @param inputText
     */
    public InputKeyBoardDialog setInputText(String inputText) {
        if(null!=bindingView&&!TextUtils.isEmpty(inputText)){
            SpannableString topicStyleContent = TextViewTopicSpan.getTopicStyleContent(inputText, CommonUtils.getColor(R.color.app_text_style),  bindingView.inputEditText,null,null);
            bindingView.inputEditText.setText(topicStyleContent);
            bindingView.inputEditText.setSelection(topicStyleContent.length());
        }
        return this;
    }

    /**
     * 根据参数，是否显示表平面板或者输入法
     * @param showKeyboard 是否显示输入法
     * @param showFaceBoard 是否显示表情面板
     */
    public InputKeyBoardDialog setParams(boolean showKeyboard, boolean showFaceBoard) {
        if(showKeyboard){
            InputTools.openKeybord( bindingView.inputEditText);
            bindingView.inputEditText.requestFocus();
        }
        if(showFaceBoard){
            showFaceBoard();
            bindingView.inputEditText.requestFocus();
        }
        return this;
    }


    /**
     * 设置输入字数限制
     * @param charMaxNum
     */
    public InputKeyBoardDialog setMaxTextCount(int charMaxNum) {
        this.content_charMaxNum=charMaxNum;
        return this;
    }


    /**
     * 设置HintText
     * @param hintTtext
     */
    public InputKeyBoardDialog setHintText(String hintTtext) {
        this.mHintTtext=hintTtext;
        if(null!= bindingView)  bindingView.inputEditText.setHint(mHintTtext);
        return this;
    }

    /**
     * 设置背景透明度 完全透明 0.0 - 1.0 完全不透明
     * @param windownAmount
     */
    public InputKeyBoardDialog setBackgroundWindown(float windownAmount) {
        getWindow().setDimAmount(windownAmount);
        return this;
    }


    /**
     * 设置文字超出提示语
     * @param errorText
     */
    public InputKeyBoardDialog setIndexOutErrorText(String errorText){
        this.indexOutErrortex=errorText;
        return this;
    }

    public  interface OnActionFunctionListener {
        void onSubmit(String content,boolean tanmuOpen);
        boolean isAvailable();
    }
    public InputKeyBoardDialog setOnActionFunctionListener(OnActionFunctionListener onActionFunctionListener) {
        mOnActionFunctionListener = onActionFunctionListener;
        return this;
    }
    private OnActionFunctionListener mOnActionFunctionListener;
}
