package com.yc.liaolive.msg.model;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.TIMCustomElem;
import com.tencent.TIMElem;
import com.tencent.TIMElemType;
import com.tencent.TIMMessage;
import com.tencent.TIMSoundElem;
import com.tencent.TIMValueCallBack;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.R;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.live.bean.CommonJson;
import com.yc.liaolive.live.bean.CustomMessageInfo;
import com.yc.liaolive.live.util.FileUtil;
import com.yc.liaolive.msg.adapter.ChatAdapter;
import com.yc.liaolive.msg.manager.FriendManager;
import com.yc.liaolive.msg.manager.VoiceModelManager;
import com.yc.liaolive.msg.model.bean.ChatParams;
import com.yc.liaolive.msg.model.bean.ResetVoiceMessage;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.MediaUtil;
import com.yc.liaolive.util.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * 语音消息数据
 */
public class VoiceMessage extends Message {

    private static final String TAG = "VoiceMessage";
    private ChatParams mChatParams;

    public VoiceMessage(TIMMessage message){
        this.message = message;
        if(message.getElementCount()>1){
            TIMElem nextElement = message.getElement(1);
            if(nextElement.getType()== TIMElemType.Custom&&nextElement instanceof TIMCustomElem){
                TIMCustomElem customElem = (TIMCustomElem) nextElement;
                if(null!=customElem.getData()){
                    String result = new String(customElem.getData());
                    CommonJson<ChatParams> commonUserJson = new Gson().fromJson(result, new TypeToken<CommonJson<ChatParams>>() {}.getType());
                    if(!TextUtils.isEmpty(commonUserJson.cmd)){
                        if(commonUserJson.cmd.equals(Constant.MESSAGE_VOICE_PARAMS)){
                            mChatParams = commonUserJson.data;
                        }
                    }
                }
            }
        }
    }


    /**
     * 语音消息构造方法
     *
     * @param duration 时长
     * @param data 语音数据
     */
    public VoiceMessage(long duration,byte[] data){
        message = new TIMMessage();
        TIMSoundElem elem = new TIMSoundElem();
        elem.setData(data);
        elem.setDuration(duration);  //填写语音时长
        message.addElement(elem);
    }

    /**
     * 语音消息构造方法
     *
     * @param duration 时长
     * @param filePath 语音数据地址
     */
    public VoiceMessage(long duration,String filePath){
        message = new TIMMessage();
        TIMSoundElem elem = new TIMSoundElem();
        elem.setPath(filePath);
        elem.setDuration(duration);  //填写语音时长
        message.addElement(elem);
    }

    /**
     * 语音消息构造方法
     *
     * @param duration 时长
     * @param filePath 语音数据地址
     * @param chatParams 语聊的价格等信息
     */
    public VoiceMessage(long duration, String filePath, ChatParams chatParams){
        message = new TIMMessage();
        TIMSoundElem elem = new TIMSoundElem();
        elem.setPath(filePath);
        elem.setDuration(duration);  //填写语音时长
        message.addElement(elem);
        //自定义参数
        TIMCustomElem customElem = new TIMCustomElem();
        //语音消息参数
        CommonJson<ChatParams> customMessage = new CommonJson<>();
        customMessage.data=chatParams;
        customMessage.cmd= Constant.MESSAGE_VOICE_PARAMS;
        String json = new Gson().toJson(customMessage,new TypeToken<CommonJson<CustomMessageInfo>>(){}.getType());
        try {
            customElem.setData(json.getBytes("UTF-8"));
            message.addElement(customElem);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    /**
     * 显示消息
     *
     * @param viewHolder 界面样式
     * @param context 显示消息的上下文
     */
    @Override
    public void showMessage(ChatAdapter.ViewHolder viewHolder, Context context) {
        clearView(viewHolder);
        Glide.with(context).load(UserManager.getInstance().getAvatar()).error(R.drawable.ic_default_user_head).into(viewHolder.rightAvatar);
        FriendProfile profile = FriendshipInfo.getInstance().getProfile(message.getSender());
        if (profile == null) {
            profile = FriendManager.getInstance().getFriendShipsById(message.getSender());
        }

        if (profile != null) {
            Glide.with(context).load(profile.getAvatarUrl()).error(R.drawable.ic_default_user_head).into(viewHolder.leftAvatar);
        } else {
            viewHolder.leftAvatar.setImageResource(R.drawable.ic_default_user_head);
        }

        View view;
        final View msgRead;
        LinearLayout msgContent;
        if (message.isSelf()){
            view =  LayoutInflater.from(context).inflate(R.layout.chat_voice_right_view, null);

            msgContent = view.findViewById(R.id.ll_msg_content);
            msgContent.setBackgroundResource(R.drawable.ic_msg_item_right);
            msgRead = null;
            ImageView voiceIcon = view.findViewById(R.id.iv_voice_icon);
            voiceIcon.setBackgroundResource(message.isSelf()? R.drawable.right_voice: R.drawable.left_voice);
            final AnimationDrawable frameAnimatio = (AnimationDrawable) voiceIcon.getBackground();
            msgContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VoiceMessage.this.playAudio(frameAnimatio);
                }
            });
            //语音价格
            if(null!=mChatParams){
                if(!TextUtils.isEmpty(mChatParams.getChat_price())&&!TextUtils.equals("0",mChatParams.getChat_price())){
                    ((TextView) view.findViewById(R.id.voice_price)).setText("-"+mChatParams.getChat_price());
                }
            }
        } else {
            view =  LayoutInflater.from(context).inflate(R.layout.chat_voice_reset_api, null);

            msgRead  = view.findViewById(R.id.view_msg_read);
            final ResetVoiceMessage resetVoiceMessage = new ResetVoiceMessage();
            TIMSoundElem elem = (TIMSoundElem) message.getElement(0);
            resetVoiceMessage.setDurtion(elem.getDuration());
            resetVoiceMessage.setPath("");
            resetVoiceMessage.setTime(String.valueOf(message.getMsgUniqueId()));
            resetVoiceMessage.setId(message.getMsgUniqueId());
            msgRead.setBackgroundResource(VoiceModelManager.getInstance().isRead((long) message.getMsgUniqueId())?0:R.drawable.arl_app_stype_gray_dot);

            msgContent = view.findViewById(R.id.ll_msg_content);
            msgContent.setBackgroundResource(R.drawable.ic_msg_item_left);

            ImageView voiceIcon = view.findViewById(R.id.iv_voice_icon);
            voiceIcon.setBackgroundResource(message.isSelf()? R.drawable.right_voice: R.drawable.left_voice);
            final AnimationDrawable frameAnimatio = (AnimationDrawable) voiceIcon.getBackground();

            msgContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //立即标记已播放状态
                    msgRead.setBackgroundResource(0);
                    VoiceModelManager.getInstance().startPlay(resetVoiceMessage, null); //只用于计数，不处理播放
                    VoiceMessage.this.playAudio(frameAnimatio);
                }
            });
        }

        TextView tv = view.findViewById(R.id.view_msg_durtion);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        tv.setTextColor(AppEngine.getApplication().getResources().getColor(isSelf() ? R.color.black : R.color.black));
        long duration = ((TIMSoundElem) message.getElement(0)).getDuration();
        tv.setText(String.valueOf(duration) + " \"");

        int width = Utils.dip2px((float) (80 + Math.ceil(Math.min(duration / 5, 60) * 10)));
        msgContent.getLayoutParams().width = width;

        final RelativeLayout container = getBubbleView(viewHolder);
        RelativeLayout.LayoutParams viewParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        container.setBackground(null);
        container.setPadding(0, 0, 0, 0);
        if (isSelf()) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewHolder.rightPanel.getLayoutParams();
            layoutParams.rightMargin = Utils.dip2px(10);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            viewHolder.rightPanel.setLayoutParams(layoutParams);
        } else {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewHolder.leftPanel.getLayoutParams();
            layoutParams.leftMargin = Utils.dip2px(10);

            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            viewHolder.leftPanel.setLayoutParams(layoutParams);

            LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams) viewHolder.leftMessage.getLayoutParams();
            layoutParams1.leftMargin = Utils.dip2px(6);
            viewHolder.leftMessage.setLayoutParams(layoutParams1);

            RelativeLayout.MarginLayoutParams leftLayoutParams = (RelativeLayout.MarginLayoutParams) viewHolder.item_left_user_icon_view.getLayoutParams();
            leftLayoutParams.topMargin = 0;
            viewHolder.item_left_user_icon_view.setLayoutParams(leftLayoutParams);
        }

        container.addView(view);
        showStatus(viewHolder);
    }

    /**
     * 获取消息摘要
     */
    @Override
    public String getSummary() {
        return AppEngine.getApplication().getString(R.string.summary_voice);
    }

    /**
     * 保存消息或消息文件
     */
    @Override
    public void save() {

    }

    private void playAudio(final AnimationDrawable frameAnimatio) {
        TIMSoundElem elem = (TIMSoundElem) message.getElement(0);

        elem.getSound(new TIMValueCallBack<byte[]>() {
            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onSuccess(byte[] bytes) {
                try{
                    File tempAudio = FileUtil.getTempFile(FileUtil.FileType.AUDIO);
                    FileOutputStream fos = new FileOutputStream(tempAudio);
                    fos.write(bytes);
                    fos.close();
                    FileInputStream fis = new FileInputStream(tempAudio);
                    MediaUtil.getInstance().play(fis);
                    frameAnimatio.start();
                    MediaUtil.getInstance().setEventListener(new MediaUtil.EventListener() {
                        @Override
                        public void onStop() {
                            frameAnimatio.stop();
                            frameAnimatio.selectDrawable(0);
                        }
                    });
                }catch (IOException e){

                }
            }
        });
    }
}
