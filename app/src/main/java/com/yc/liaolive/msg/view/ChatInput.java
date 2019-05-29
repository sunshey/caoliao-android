package com.yc.liaolive.msg.view;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yc.liaolive.R;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.bean.ChatEmoji;
import com.yc.liaolive.msg.ui.activity.ChatActivity;
import com.yc.liaolive.ui.adapter.EmojiListAdapter;
import com.yc.liaolive.ui.contract.ChatView;
import com.yc.liaolive.util.EmotionUtils;
import com.yc.liaolive.util.Utils;

import java.util.List;

/**
 * 聊天界面输入控件
 */
public class ChatInput extends RelativeLayout implements View.OnClickListener {

    private ImageView btnFace, btnVoice;
    private TextView btnSend;
    private EditText editText;
    private InputMode inputMode = InputMode.NONE;
    private ChatView chatView;
    private TextView voicePanel;
    private boolean mIsShow = true;
    private String mIdentufy;
    private EmojiListAdapter mEmojiListAdapter;
    private RecyclerView mRecyclerView;//表情面板

    public ChatInput(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_chat_input, this);
        initView();
    }

    private void initView() {
        btnVoice = findViewById(R.id.btn_voice);
        btnVoice.setOnClickListener(this);
        btnVoice.setBackgroundResource(R.drawable.msg_voice_btn);
        btnSend = (TextView) findViewById(R.id.btn_send_msg);
        btnSend.setOnClickListener(this);
        btnSend.setEnabled(false);
        btnFace = (ImageView) findViewById(R.id.btn_face);
        btnFace.setBackgroundResource(R.drawable.ic_face_boart);
        btnFace.setOnClickListener(this);
        voicePanel = (TextView) findViewById(R.id.voice_panel);
        final int cancleMove = Utils.dip2px(100);
        final boolean[] canRecord = {false};
        voicePanel.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float downY = 0;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (null == mBtnTouchListener || !mBtnTouchListener.touchBtn()){
                            return false;
                        }
                        canRecord[0] = true;
                        downY = event.getY();
                        hanldVoiceOption(1);
                        updateVoiceView(1);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (canRecord[0]) {
                            float moveY = Math.abs(event.getY() - downY);
                            if (moveY > cancleMove) {
                                updateVoiceView(-1);
                            } else {
                                updateVoiceView(1);
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        if (canRecord[0]) {
                            float upY = Math.abs(event.getY() - downY);
                            if (upY > cancleMove) {
                                hanldVoiceOption(-1);
                            } else {
                                hanldVoiceOption(2);
                            }
                            updateVoiceView(0);
                        }
                        break;
                }
                return true;
            }
        });
        editText = (EditText) findViewById(R.id.input);
        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    btnSend.setEnabled(editText.getText().toString().trim().length() > 0);
                    updateView(InputMode.TEXT);
                }
            }
        });

        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnSend.setEnabled(s.toString().length() > 0);
                if (s.length() > 0) {
                    chatView.sending();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        initEmotionData();
    }

    /**
     * 初始化自定义表情
     */
    private void initEmotionData() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyler_view);
        //表情集合
        List<ChatEmoji> chatEmojis = EmotionUtils.getInstance().emojis;
        mEmojiListAdapter = new EmojiListAdapter(R.layout.list_item_face,chatEmojis);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),7,GridLayoutManager.VERTICAL,false));
        mRecyclerView.setHasFixedSize(true);
        mEmojiListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(null!=view.getTag()&&null!=editText){
                    ChatEmoji emoji = (ChatEmoji) view.getTag();
                    if (!TextUtils.isEmpty(emoji.getCharacter())) {
                        SpannableString spannableString = EmotionUtils.getInstance().addFace(getContext(), emoji.getAbsolutePath(), emoji.getCharacter(),(int) editText.getTextSize());
                        editText.append(spannableString);
                    }
                }
            }
        });
        mRecyclerView.setAdapter(mEmojiListAdapter);
    }

    private void updateView(InputMode mode) {
        if (mode == inputMode) return;
//        leavingCurrentState();
        editText.clearFocus();
        switch (inputMode = mode) {
            case MORE:
//                morePanel.setVisibility(VISIBLE);
                break;
            case TEXT:
                btnVoice.setBackgroundResource(R.drawable.msg_voice_btn);
                btnFace.setBackgroundResource(R.drawable.ic_face_boart);
                voicePanel.setVisibility(GONE);
                mRecyclerView.setVisibility(GONE);
                editText.setVisibility(VISIBLE);
                if (editText.requestFocus()) {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                }
                break;
            case VOICE:
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                }
                btnVoice.setBackgroundResource(R.drawable.ic_face_keyboard);
                btnFace.setBackgroundResource(R.drawable.ic_face_boart);
                voicePanel.setVisibility(VISIBLE);
                editText.setVisibility(GONE);
                mRecyclerView.setVisibility(GONE);
                btnSend.setEnabled(false);
                break;
            case EMOTICON:
                InputMethodManager imm2 = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm2.isActive()) {
                    imm2.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                }
                btnVoice.setBackgroundResource(R.drawable.msg_voice_btn);
                btnFace.setBackgroundResource(R.drawable.ic_face_keyboard);
                mRecyclerView.setVisibility(VISIBLE);
                voicePanel.setVisibility(GONE);
                editText.setVisibility(VISIBLE);
                break;
        }
    }

    public void hideInput() {
//        morePanel.setVisibility(GONE);
        if (null != editText && editText.requestFocus()) {
            View view = ((Activity) getContext()).getCurrentFocus();
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            editText.clearFocus();
        }
        try {
            if(null!=getContext() && ((Activity) getContext()) instanceof ChatActivity){
                Activity context = (Activity) getContext();
                ((ChatActivity)context).setSolveNavigationBar();
            }
        }catch (RuntimeException e){

        }
    }

    public void isMoreShow(boolean isShow) {
        this.mIsShow = isShow;
        if (!isShow) {
//            btnAdd.setVisibility(INVISIBLE);
//            btnSend.setVisibility(VISIBLE);
        }
    }

    /**
     * 更新语音发送按钮状态
     * @param status  1录音  -1松手取消  0正常
     */
    private void updateVoiceView(int status) {
        if (status == 1) {
            voicePanel.setText(getResources().getString(R.string.chat_release_send));
            voicePanel.setBackground(getResources().getDrawable(R.drawable.btn_voice_pressed));
            chatView.showStartVoiceView();
        } else if (status == -1) {
            voicePanel.setText(getResources().getString(R.string.chat_release_cancel));
            voicePanel.setBackground(getResources().getDrawable(R.drawable.btn_voice_normal));
            chatView.showCancelVoiceView();
        } else {
            voicePanel.setText(getResources().getString(R.string.chat_press_talk));
            voicePanel.setBackground(getResources().getDrawable(R.drawable.btn_voice_normal));
        }
    }

    /**
     *
     * @param option 1 开始录制 2开始发送 -1 取消发送
     */
    private void hanldVoiceOption (int option) {
        if (1 == option) {
            chatView.startSendVoice();
        } else if (2 == option) {
            chatView.endSendVoice();
        } else if (-1 == option) {
            chatView.cancelSendVoice();
        }
    }

    /**
     * 关联聊天界面逻辑
     */
    public void setChatView(ChatView chatView) {
        this.chatView = chatView;
    }

    public InputMode getInputMode() {
        return inputMode;
    }

//    private void setSendBtn() {
//        if (isSendVisible) {
////            btnAdd.setVisibility(INVISIBLE);
//            btnSend.setVisibility(VISIBLE);
//        } else {
////            btnAdd.setVisibility(VISIBLE);
//            btnSend.setVisibility(INVISIBLE);
//        }
//    }

//    private void prepareEmoticon() {
//        if (emoticonPanel == null) return;
//        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
//        Point point = new Point();
//        wm.getDefaultDisplay().getSize(point);
//        for (int i = 0; i < 20; ++i) {
//            LinearLayout linearLayout = new LinearLayout(getContext());
//            LinearLayout.MarginLayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f);
//
//            params.bottomMargin = Utils.dip2px(getContext(), 10);
//            linearLayout.setLayoutParams(params);
//
//            for (int j = 1; j < 8; ++j) {
//                try {
//                    AssetManager am = getContext().getAssets();
//
//                    final int index = 7 * i + j;
//
//                    InputStream is = am.open(String.format("emot/emoji_%d.png", index));
//                    Bitmap bitmap = BitmapFactory.decodeStream(is);
//                    Matrix matrix = new Matrix();
//                    int width = bitmap.getWidth();
//                    int height = bitmap.getHeight();
//                    matrix.postScale(1f, 1f);
//                    final Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
//                            width, height, matrix, true);
//                    ImageView image = new ImageView(getContext());
//                    image.setImageBitmap(resizedBitmap);
//
//                    image.setLayoutParams(new LinearLayout.LayoutParams(point.x / 7, LayoutParams.WRAP_CONTENT, 1f));
//                    linearLayout.addView(image);
//                    image.setOnClickListener(new OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            String content = String.valueOf(index);
//                            SpannableString str = new SpannableString(String.valueOf(index));
//                            ImageSpan span = new ImageSpan(getContext(), resizedBitmap, ImageSpan.ALIGN_BASELINE);
//                            str.setSpan(span, 0, content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                            editText.append(str);
//
//                        }
//                    });
//                    is.close();
//                } catch (IOException e) {
//
//                }
//
//            }
//            if (linearLayout.getChildCount() < 7) {
//                ImageView imageView = new ImageView(getContext());
//                imageView.setLayoutParams(new LinearLayout.LayoutParams(point.x / 7, LayoutParams.WRAP_CONTENT, 1f));
//                linearLayout.addView(imageView);
//            }
//            emoticonPanel.addView(linearLayout);
//        }
//        isEmoticonReady = true;
//    }

    /**
     * Called when a view has been clicked.
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_voice:
                updateView(inputMode == InputMode.VOICE ? InputMode.TEXT : InputMode.VOICE);
                break;
            case R.id.btn_face:
                updateView(inputMode == InputMode.EMOTICON ? InputMode.TEXT : InputMode.EMOTICON);
                break;
            case R.id.btn_send_msg:
                chatView.sendText();
                break;
        }
        //相册 chatView.sendImage();
    }


    /**
     * 获取输入框文字
     */
    public Editable getText() {
        return editText.getText();
    }

    /**
     * 设置输入框文字
     */
    public void setText(String text) {
        if(null!=editText){
            editText.setText(text);
            if(!TextUtils.isEmpty(text)){
                editText.setSelection(text.length());
            }
        }
    }


    /**
     * 设置输入模式
     */
    public void setInputMode(InputMode mode) {
        updateView(mode);
    }

    public void setIdentify(String identify) {
        this.mIdentufy=identify;
//        btnFace.setVisibility(!TextUtils.equals(identify, VideoApplication.getInstance().getServerIdentufy())?VISIBLE:GONE);
    }

    public enum InputMode {
        TEXT,
        VOICE,
        EMOTICON,
        MORE,
        VIDEO,
        NONE,
    }

    public interface OnVoiceBtnTouchListener {
        boolean touchBtn();
    }

    private OnVoiceBtnTouchListener mBtnTouchListener;

    public void setmBtnTouchListener(OnVoiceBtnTouchListener mBtnTouchListener) {
        this.mBtnTouchListener = mBtnTouchListener;
    }
}
