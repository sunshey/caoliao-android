package com.yc.liaolive.msg.model;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Looper;
import android.os.SystemClock;
import android.text.Html;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.TIMCustomElem;
import com.tencent.TIMElem;
import com.tencent.TIMElemType;
import com.tencent.TIMMessage;
import com.umeng.analytics.MobclickAgent;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.R;
import com.yc.liaolive.bean.CustomMsgCall;
import com.yc.liaolive.bean.PrivateMedia;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.live.bean.CommonJson;
import com.yc.liaolive.live.bean.CustomMsgInfo;
import com.yc.liaolive.live.bean.RoomExtra;
import com.yc.liaolive.live.constants.LiveConstant;
import com.yc.liaolive.live.ui.activity.LiveRoomPullActivity;
import com.yc.liaolive.live.util.LiveChatUserGradleSpan;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.media.ui.activity.VerticalImagePreviewActivity;
import com.yc.liaolive.media.ui.activity.VerticalVideoPlayerAvtivity;
import com.yc.liaolive.msg.adapter.ChatAdapter;
import com.yc.liaolive.msg.manager.FriendManager;
import com.yc.liaolive.msg.manager.VoiceModelManager;
import com.yc.liaolive.msg.model.bean.ChatAwardMessage;
import com.yc.liaolive.msg.model.bean.ChatCommentMessage;
import com.yc.liaolive.msg.model.bean.ChatGiftMessage;
import com.yc.liaolive.msg.model.bean.ResetVoiceMessage;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.DateUtil;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.util.VideoDataUtils;
import com.yc.liaolive.videocall.bean.CallCmdExtra;
import com.yc.liaolive.videocall.bean.CallExtraInfo;
import com.yc.liaolive.videocall.manager.MakeCallManager;
import com.yc.liaolive.view.widget.CircleRadarLayout;
import com.yc.liaolive.view.widget.RoundImageView;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 自定义消息
 */
public class CustomMessage extends Message {

    private String TAG = "CustomMessage";
    private Type customType = Type.INVALID;
    //通用适配消息，输入状态
    private ChatCommentMessage mCommentMessage;
    //礼物消息
    private ChatGiftMessage mChatGiftMessage;
    //私密多媒体
    private PrivateMedia mPrivateMedia;
    //视频通话唤醒
    private CustomMsgCall mCustomMsgCall;
    //语音消息
    private ResetVoiceMessage mResetVoiceMessage;
    //中奖消息
    private ChatAwardMessage mChatAwardMessage;

    /**
     * @param message  私信会话走的 private_media CMD 协议
     */
    public CustomMessage(TIMMessage message) {
        if(message==null) return;
        this.message = message;
        if(message.getElementCount()>0){
            TIMElem element = message.getElement(0);
            if(null!=element&&element.getType()== TIMElemType.Custom&&element instanceof TIMCustomElem){
                TIMCustomElem customElem = (TIMCustomElem) element;
                try {
                    String result = new String(customElem.getData());
                    //CommonJson<Object> commonUserJson = JSON.parseObject(result, new TypeReference<CommonJson<Object>>() {}.getType());
                    CommonJson<Object> commonUserJson = new Gson().fromJson(result, new TypeToken<CommonJson<Object>>() {}.getType());
                    if(null!=commonUserJson&&!TextUtils.isEmpty(commonUserJson.cmd)){
                        //服务端--视频聊小视频
                        if(TextUtils.equals(Constant.MESSAGE_PRIVATE_CUSTOM_WAKEUP,commonUserJson.cmd)){
                            customType = Type.CALL_WAKEUP;
                            CommonJson<CustomMsgCall> callParseObject = new Gson().fromJson(result, new TypeToken<CommonJson<CustomMsgCall>>() {}.getType());
                            if(null!=callParseObject&&null!=callParseObject.data){
                                mCustomMsgCall=callParseObject.data;
                            }
                            //服务端--语音消息
                        }else if(TextUtils.equals(Constant.PRIVATE_CHAT_VOICE,commonUserJson.cmd)){
                            customType = Type.CHAT_VOICE;
                            CommonJson<ResetVoiceMessage> callParseObject = new Gson().fromJson(result, new TypeToken<CommonJson<ResetVoiceMessage>>() {}.getType());
                            if(null!=callParseObject&&null!=callParseObject.data){
                                mResetVoiceMessage = callParseObject.data;
                                try {
                                    mResetVoiceMessage.setId(Long.parseLong(mResetVoiceMessage.getTime()));
                                }catch (RuntimeException e){
                                    mResetVoiceMessage.setId(System.currentTimeMillis());
                                }
                            }
                            //服务端--中奖等系统消息
                        }else if(Constant.MSG_CUSTOM_ROOM_SYSTEM.equals(commonUserJson.cmd)){
                            CommonJson<CustomMsgInfo> commonJson = new Gson().fromJson(result, new TypeToken<CommonJson<CustomMsgInfo>>() {}.getType());
                            customType = Type.AWARD;
                            if(null!=commonJson&&null!=commonJson.data&&null!=commonJson.data.getGift()){
                                mChatAwardMessage = new ChatAwardMessage();
                                mChatAwardMessage.setDrawTimes(commonJson.data.getGift().getDrawTimes());
                                mChatAwardMessage.setGiftName(commonJson.data.getGift().getTitle());
                                mChatAwardMessage.setMsgContent(commonJson.data.getMsgContent());
                                mChatAwardMessage.setSendUserVIP(commonJson.data.getSendUserVIP());
                                mChatAwardMessage.setSendUserName(commonJson.data.getSendUserName());
                            }
                            //服务端--直播邀请
                        }else if(Constant.MESSAGE_PRIVATE_CUSTOM_LIVE.equals(commonUserJson.cmd)){
                            CommonJson<CustomMsgInfo> commonJson = new Gson().fromJson(result, new TypeToken<CommonJson<CustomMsgInfo>>() {}.getType());
                            customType = Type.LIVE;
                            if(null!=commonJson&&null!=commonJson.data){
                                mCommentMessage = new ChatCommentMessage();
                                mCommentMessage.setCover(commonJson.data.getFontCover());
                            }
                            //服务端--私有多媒体文件
                        }else if(Constant.MESSAGE_PRIVATE_CUSTOM_MEDIA.equals(commonUserJson.cmd)){
                            CommonJson<CustomMsgInfo> commonJson = new Gson().fromJson(result, new TypeToken<CommonJson<CustomMsgInfo>>() {}.getType());
                            customType = Type.MEDIA;
                            if(null!=commonJson&&null!=commonJson.data){
                                mPrivateMedia=new PrivateMedia();
                                mPrivateMedia.setAnnex_type(commonJson.data.getAnnex_type());
                                mPrivateMedia.setContent(commonJson.data.getContent());
                                mPrivateMedia.setFile_type(commonJson.data.getFile_type());
                                mPrivateMedia.setId(commonJson.data.getId());
                                mPrivateMedia.setImg_path(commonJson.data.getImg_path());
                                mPrivateMedia.setFile_path(commonJson.data.getFile_path());
                                mPrivateMedia.setIs_private(commonJson.data.getIs_private());
                                mPrivateMedia.setPrice(commonJson.data.getPrice());
                                mPrivateMedia.setVideo_durtion(commonJson.data.getVideo_durtion());
                                mPrivateMedia.setChat_price(commonJson.data.getChat_price());//commonJson.data.getChat_price()
                            }
                            //服务端--视频通话结算消息
                        }else if(Constant.PRIVATE_CALL_NOTICE.equals(commonUserJson.cmd)){
                            CommonJson<CustomMsgInfo> commonJson = new Gson().fromJson(result, new TypeToken<CommonJson<CustomMsgInfo>>() {}.getType());
                            customType = Type.CALL_CUSTOM;
                            if(null!=commonJson&&null!=commonJson.data){
                                mCommentMessage = new ChatCommentMessage();
                                mCommentMessage.setContent(commonJson.data.getContent());
                            }
                            //本地--视频通话信令
                        }else if(LiveConstant.VIDEO_CALL_CMD.equals(commonUserJson.cmd)){
                            CommonJson<CallCmdExtra> commonJson = new Gson().fromJson(result, new TypeToken<CommonJson<CallCmdExtra>>() {}.getType());
                            customType = Type.CALL;
                            if(null!=commonJson&&null!=commonJson.data){
                                mCommentMessage = new ChatCommentMessage();
                                mCommentMessage.setContent(commonJson.data.getContent());
                            }
                            //本地--私信、视频通话 礼物消息
                        }else if(LiveConstant.MSG_CUSTOM_ROOM_PRIVATE_GIFT.equals(commonUserJson.cmd)){
                            customType = Type.CHAT_GIFT;
                            CommonJson<CustomMsgInfo> commonJson = new Gson().fromJson(result, new TypeToken<CommonJson<CustomMsgInfo>>() {}.getType());
                            if(null!=commonJson&&null!=commonJson.data&&null!=commonJson.data.getGift()){
                                mChatGiftMessage=new ChatGiftMessage();
                                mChatGiftMessage.setCount(commonJson.data.getGift().getCount());
                                mChatGiftMessage.setGiftId(commonJson.data.getGift().getId());
                                mChatGiftMessage.setIcon(commonJson.data.getGift().getSrc());
                                mChatGiftMessage.setName(commonJson.data.getGift().getTitle());
                                mChatGiftMessage.setTotalPrice(commonJson.data.getGift().getPrice());
                                mChatGiftMessage.setUrl(commonJson.data.getGift().getBigSvga());
                                mChatGiftMessage.setContent(commonJson.data.getMsgContent());
                            }
                            //输入中状态
                        }else if(Constant.PRIVATE_CHAT_INPUT_ING.equals(commonUserJson.cmd)){
                            customType = Type.INPUTING;
                            ChatCommentMessage commentMessage = new Gson().fromJson(result, ChatCommentMessage.class);
                            this.mCommentMessage=commentMessage;
                        }else{
                            customType = Type.INVALID;
                        }
                    }
                } catch (Exception e) {

                }
            }
        }
    }

    /**
     * 构造自定义消息
     * @param type
     * @param info
     */
    public CustomMessage(Type type, String info) {
        message = new TIMMessage();
        String data = "";
        switch (type) {
            case INPUTING:
                try {
                    JSONObject dataJson = new JSONObject();
                    dataJson.put("cmd", Constant.PRIVATE_CHAT_INPUT_ING);
                    dataJson.put("content","对方正在输入中...");
                    dataJson.put("actionParam","input");
                    data = dataJson.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
                default:
                    data=info;
        }
        TIMCustomElem elem = new TIMCustomElem();
        elem.setData(data.getBytes());
        message.addElement(elem);
    }

    /**
     * 返回会话类型
     * @return
     */
    public Type getType() {
        return customType;
    }

    /**
     * 服务端推送多媒体文件
     * @return
     */
    public PrivateMedia getPrivateMedia() {
        return mPrivateMedia;
    }

    public ChatCommentMessage getCommentMessage() {
        return mCommentMessage;
    }

    /**
     * 服务端推送多媒体文件
     * @return
     */
    public CustomMsgCall getCustomMsgCall() {
        return mCustomMsgCall;
    }

    /**
     * 服务端推送多媒体文件
     * @return
     */
    public ChatGiftMessage getChatGiftMessage() {
        return mChatGiftMessage;
    }

    /**
     * 显示消息
     * @param viewHolder 界面样式
     * @param context    显示消息的上下文
     */
    @Override
    public void showMessage(ChatAdapter.ViewHolder viewHolder, Context context) {
        clearView(viewHolder);
        switch (customType) {
            case CHAT_GIFT:
                showGiftMessage(viewHolder,context);
                break;
            case AWARD:
                showAwardMessage(viewHolder, context);
                break;
            case CALL:
                showVideoCallMessage(viewHolder, context);
                break;
            case LIVE:
                showLiveMessage(viewHolder, context);
                break;
            case MEDIA:
                showMediaMessage(viewHolder, context);
                break;
            case CALL_CUSTOM:
                showCallCustomMessage(viewHolder,context);
                break;
            case CALL_WAKEUP:
                showCallOutNotice(viewHolder,context);
                break;
            case CHAT_VOICE:
                showVoiceMessahe(viewHolder,context);
                break;
        }
        showStatus(viewHolder);
    }

    /**
     * 私信礼物消息
     * @param viewHolder
     * @param context
     */
    private void showGiftMessage(ChatAdapter.ViewHolder viewHolder, Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;
        if (isSelf()) {
            view = inflater.inflate(R.layout.message_chat_gift_right, null);
        } else {
            view = inflater.inflate(R.layout.message_chat_gift_left, null);
        }
        ImageView ivGift = view.findViewById(R.id.iv_gift_icon);
        TextView tvName = view.findViewById(R.id.tv_chatcontent);
        TextView tvGiftCount = view.findViewById(R.id.tv_gift_count);
        TextView tvGiftMoney = view.findViewById(R.id.tv_gift_momey);
        viewHolder.rightAvatar.setVisibility(View.VISIBLE);
        viewHolder.leftAvatar.setVisibility(View.VISIBLE);
        Glide.with(context).load(UserManager.getInstance().getAvatar()).error(R.drawable.ic_default_user_head).into(viewHolder.rightAvatar);
        FriendProfile profile = FriendshipInfo.getInstance().getProfile(message.getConversation().getPeer());
        profile = getProfile(profile);
        if (profile != null) {
            Glide.with(context).load(profile.getAvatarUrl()).error(R.drawable.ic_default_user_head).into(viewHolder.leftAvatar);
        } else {
            viewHolder.leftAvatar.setImageResource(R.drawable.ic_default_user_head);
        }
        setParams(viewHolder,false);
        if(null!=mChatGiftMessage){
            Glide.with(context).load(this.mChatGiftMessage.getIcon()).error(R.drawable.ic_default_gift_icon).dontAnimate().into(ivGift);
            tvName.setText(mChatGiftMessage.getName());
            tvGiftCount.setText(String.valueOf(mChatGiftMessage.getCount()));
            long price=mChatGiftMessage.getCount()*mChatGiftMessage.getTotalPrice();
            tvGiftMoney.setText(String.valueOf(price));
        }
        if (isSelf()) {
            getBubbleView(viewHolder).setPadding(getBubbleView(viewHolder).getPaddingLeft(), 0, 0, getBubbleView(viewHolder).getPaddingBottom());
        } else {
            getBubbleView(viewHolder).setPadding(0, 0, getBubbleView(viewHolder).getPaddingRight(), getBubbleView(viewHolder).getPaddingBottom());
        }
        getBubbleView(viewHolder).setBackground(null);
        getBubbleView(viewHolder).addView(view);
    }

    /**
     * 中奖消息
     * @param viewHolder
     * @param context
     */
    private void showAwardMessage(ChatAdapter.ViewHolder viewHolder, Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.chat_award_view, null);
        TextView tvAwardMsg = view.findViewById(R.id.tv_content);
        final RelativeLayout rl = view.findViewById(R.id.rl_gift);
        viewHolder.rightAvatar.setVisibility(View.INVISIBLE);
        viewHolder.leftAvatar.setVisibility(View.INVISIBLE);
        setParams(viewHolder,false);
        if(null!=mChatAwardMessage){
            tvAwardMsg.setText("恭喜["+mChatAwardMessage.getSendUserName()+"] 送出的"+mChatAwardMessage.getGiftName()+"中了"+mChatAwardMessage.getDrawTimes()+"倍大奖");
        }
//        Animation animation = AnimationUtils.loadAnimation(context, R.anim.shake);
//        rl.startAnimation(animation);
        getBubbleView(viewHolder).addView(view);
        getBubbleView(viewHolder).setBackground(null);
    }

    /**
     * 视频通话内部信令,此处业务场景已过滤了视频通话的信令消息
     * @param viewHolder
     * @param context
     */
    private void showVideoCallMessage(ChatAdapter.ViewHolder viewHolder, Context context) {
        TextView tv = new TextView(AppEngine.getApplication());
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        tv.setPadding(3, 0, 3, 0);
        tv.setTextColor(
                AppEngine.getApplication().getResources().getColor(isSelf() ? R.color.black : R.color.black));

        viewHolder.rightAvatar.setVisibility(View.VISIBLE);
        viewHolder.leftAvatar.setVisibility(View.VISIBLE);

        Glide.with(context).load(UserManager.getInstance().getAvatar()).error(R.drawable.ic_default_user_head).into(viewHolder.rightAvatar);
        FriendProfile profile = FriendshipInfo.getInstance().getProfile(message.getConversation().getPeer());
        profile = getProfile(profile);
        if (profile != null) {
            Glide.with(context).load(profile.getAvatarUrl()).error(R.drawable.ic_default_user_head).into(viewHolder.leftAvatar);
        } else {
            viewHolder.leftAvatar.setImageResource(R.drawable.ic_default_user_head);
        }

        setParams(viewHolder,false);
        if (isSelf()) {
            getBubbleView(viewHolder).setBackgroundResource(R.drawable.ic_msg_item_right);
        } else {
            getBubbleView(viewHolder).setBackgroundResource(R.drawable.ic_msg_item_left);
        }
        if(null!=mCommentMessage){
            tv.setText(mCommentMessage.getContent());
        }
        getBubbleView(viewHolder).addView(tv);

    }

    /**
     * 视频通话邀请
     * @param viewHolder
     * @param context
     */
    private void showLiveMessage(final ChatAdapter.ViewHolder viewHolder, final Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.chat_live_view, null);
        CircleRadarLayout circleRadarLayout = view.findViewById(R.id.radar_layout);
        circleRadarLayout.onStart();
        viewHolder.rightAvatar.setVisibility(View.VISIBLE);
        viewHolder.leftAvatar.setVisibility(View.VISIBLE);
        Glide.with(context).load(UserManager.getInstance().getAvatar()).error(R.drawable.ic_default_user_head).into(viewHolder.rightAvatar);
        FriendProfile profile = FriendshipInfo.getInstance().getProfile(message.getConversation().getPeer());
        profile = getProfile(profile);
        if (profile != null) {
            Glide.with(context).load(profile.getAvatarUrl()).error(R.drawable.ic_default_user_head).into(viewHolder.leftAvatar);
        } else {
            viewHolder.leftAvatar.setImageResource(R.drawable.ic_default_user_head);
        }
        setParams(viewHolder,true);
        final RelativeLayout container = getBubbleView(viewHolder);
        container.setPadding(0, 0, Utils.dip2px(13), 0);
        container.setBackground(null);
        container.addView(view);
        final FriendProfile finalProfile = profile;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LiveRoomPullActivity activity = LiveRoomPullActivity.getInstance();
                if(null!=activity){
                    activity.finish();
                }
                if(null!=finalProfile){
                    MobclickAgent.onEvent(context, "msg_click_player");
                    RoomExtra roomExtra=new RoomExtra();
                    roomExtra.setUserid(finalProfile.getIdentify());
                    roomExtra.setNickname(finalProfile.getName());
                    roomExtra.setAvatar(finalProfile.getAvatarUrl());
                    if(null!=mCommentMessage)roomExtra.setFrontcover(mCommentMessage.getCover());
                    LiveRoomPullActivity.start(context, roomExtra);
                }
            }
        });
    }

    /**
     * 私有多媒体文件
     * @param viewHolder
     * @param context
     * 用户发送消息出去，扣费金额大于0，表名是付费发送的消息，显示扣分金额给自己看，对方不可见
     * 用户发送出去的私密照片、视频，附带价格信息，只有对方（点播方）可以看到价格信息，自己不可见
     */
    private void showMediaMessage(ChatAdapter.ViewHolder viewHolder, final Context context) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.chat_media_view, null);
        viewHolder.rightAvatar.setVisibility(View.VISIBLE);
        viewHolder.leftAvatar.setVisibility(View.VISIBLE);
        Glide.with(context).load(UserManager.getInstance().getAvatar()).error(R.drawable.ic_default_user_head).into(viewHolder.rightAvatar);
        FriendProfile profile = FriendshipInfo.getInstance().getProfile(message.getConversation().getPeer());
        profile = getProfile(profile);
        if (profile != null) {
            Glide.with(context).load(profile.getAvatarUrl()).error(R.drawable.ic_default_user_head).into(viewHolder.leftAvatar);
        } else {
            viewHolder.leftAvatar.setImageResource(R.drawable.ic_default_user_head);
        }
        setParams(viewHolder,false);
        final RelativeLayout container = getBubbleView(viewHolder);
        container.setPadding(0, 0,0, 0);
        container.setBackground(null);
        container.addView(view);
        final FriendProfile finalProfile = profile;
        if(null!=mPrivateMedia){
            //照片、图片
            try {
                if(1==mPrivateMedia.getAnnex_type()){
                    //图片不需要付费，默认是全高清的
                    if(Constant.MEDIA_TYPE_IMAGE==mPrivateMedia.getFile_type()&&TextUtils.isEmpty(mPrivateMedia.getFile_path())){
                        mPrivateMedia.setFile_path(mPrivateMedia.getImg_path());
                    }
                    view.findViewById(R.id.media_root_view).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.content_root_view).setVisibility(View.GONE);
                    RelativeLayout mediaImageView = view.findViewById(R.id.media_image_view);
                    //类型设置
                    TextView itemDurtion = (TextView) view.findViewById(R.id.tv_item_durtion);
                    itemDurtion.setText(Constant.MEDIA_TYPE_IMAGE==mPrivateMedia.getFile_type()?"":DateUtil.timeFormatSecond(mPrivateMedia.getVideo_durtion()));
                    //封面设置
                    ImageView itemVideoPlayer = (ImageView) view.findViewById(R.id.ic_item_video_player);
                    itemVideoPlayer.setImageResource(Constant.MEDIA_TYPE_VIDEO==mPrivateMedia.getFile_type()?R.drawable.ic_private_media_video:0);
                    //如果是自己发送的消息，隐藏价格信息
                    TextView tvPrice = (TextView) view.findViewById(R.id.tv_price);
                    // TODO: 2018/9/20 暂时隐藏
//                    if(mPrivateMedia.getPrice()>0) tvPrice.setText(""+mPrivateMedia.getPrice());
                    if(isSelf()){
                        tvPrice.setVisibility(View.GONE);
                        mediaImageView.setBackgroundResource(R.drawable.ic_chat_media_right);
                    }else{
                        tvPrice.setVisibility(View.VISIBLE);
                        mediaImageView.setBackgroundResource(R.drawable.ic_chat_media_left);
                    }
                    RoundImageView simpleDraweeView = view.findViewById(R.id.item_iv_icon);
                    Glide.with(context).load(mPrivateMedia.getImg_path())
                            .asBitmap()
                            .placeholder(R.drawable.ic_default_item_cover)
                            .error(R.drawable.ic_default_item_cover)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                            .into(new BitmapImageViewTarget(simpleDraweeView) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    super.setResource(resource);
                                }
                            });

                    view.setTag(mPrivateMedia);
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MobclickAgent.onEvent(context, "msg_click_media");
                            startPreviewMedia((PrivateMedia) v.getTag(),context,finalProfile.getIdentify(),v);
                        }
                    });
                    //纯文本类型
                }else{
                    RelativeLayout msgContent  = view.findViewById(R.id.rightContentMessage);
                    TextView contentPrice = (TextView) view.findViewById(R.id.tv_content_price);
                    if (isSelf()) {
                        msgContent.setBackgroundResource(R.drawable.ic_msg_item_right);
                        contentPrice.setVisibility(mPrivateMedia.getChat_price()>0?View.VISIBLE:View.GONE);
                    } else {
                        contentPrice.setVisibility(View.GONE);
                        msgContent.setBackgroundResource(R.drawable.ic_msg_item_left);
                    }
                    view.findViewById(R.id.media_root_view).setVisibility(View.GONE);
                    view.findViewById(R.id.content_root_view).setVisibility(View.VISIBLE);
                    if(mPrivateMedia.getChat_price()>0) contentPrice.setText("-"+mPrivateMedia.getChat_price());
                    TextView textView=new TextView(context);
                    RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    textView.setLayoutParams(layoutParams);
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                    textView.setPadding(3, 0, 3, 0);
                    textView.setTextColor(AppEngine.getApplication().getResources().getColor(R.color.black));
                    textView.setText(LiveChatUserGradleSpan.stringFormatEmoji(Html.fromHtml(TextUtils.isEmpty(mPrivateMedia.getContent())?"[未知消息]":mPrivateMedia.getContent()), textView));
                    msgContent.addView(textView);
                }
            }catch (RuntimeException e){

            }
        }
    }

    /**
     * 视频通话结算消息
     * @param viewHolder
     * @param context
     */
    private void showCallCustomMessage(ChatAdapter.ViewHolder viewHolder, Context context) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.chat_call_custom_view, null);
        viewHolder.rightAvatar.setVisibility(View.VISIBLE);
        viewHolder.leftAvatar.setVisibility(View.VISIBLE);
        Glide.with(context).load(UserManager.getInstance().getAvatar()).error(R.drawable.ic_default_user_head).into(viewHolder.rightAvatar);
        FriendProfile profile = FriendshipInfo.getInstance().getProfile(message.getConversation().getPeer());
        profile = getProfile(profile);
        if (profile != null) {
            Glide.with(context).load(profile.getAvatarUrl()).error(R.drawable.ic_default_user_head).into(viewHolder.leftAvatar);
        } else {
            viewHolder.leftAvatar.setImageResource(R.drawable.ic_default_user_head);
        }
        setParams(viewHolder,false);
        final RelativeLayout container = getBubbleView(viewHolder);
        container.setPadding(0, 0,0, 0);
        container.setBackground(null);
        container.addView(view);
        try {
            LinearLayout msgContent  = view.findViewById(R.id.ll_msg_content);
            if (isSelf()) {
                msgContent.setBackgroundResource(R.drawable.ic_msg_item_right);
            } else {
                msgContent.setBackgroundResource(R.drawable.ic_msg_item_left);
            }
            if(null!=mCommentMessage) ((TextView) msgContent.findViewById(R.id.tv_content)).setText(mCommentMessage.getContent());
        }catch (RuntimeException e){

        }
    }

    /**
     * 视频通话小视频推送
     * @param viewHolder
     * @param context
     */
    private void showCallOutNotice(ChatAdapter.ViewHolder viewHolder, final Context context) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.chat_call_notic_view, null);
        viewHolder.rightAvatar.setVisibility(View.VISIBLE);
        viewHolder.leftAvatar.setVisibility(View.VISIBLE);
        //发送人头像
        FriendProfile profile = FriendshipInfo.getInstance().getProfile(message.getConversation().getPeer());
        profile = getProfile(profile);
        Object headUrl;
        if (profile != null) {
            headUrl=profile.getAvatarUrl();
        } else {
            if(null!=mCustomMsgCall){
                headUrl=mCustomMsgCall.getAnchorAvatar();
            }else{
                headUrl=R.drawable.ic_default_user_head;
            }
        }
        Glide.with(context).load(headUrl).error(R.drawable.ic_default_user_head).into(viewHolder.leftAvatar);
        setParams(viewHolder,false);
        final RelativeLayout container = getBubbleView(viewHolder);
        container.setPadding(0, 0,0, 0);
        container.setBackground(null);
        container.addView(view);
        if(null!=mCustomMsgCall){
            ((TextView) view.findViewById(R.id.item_wake_title)).setText(mCustomMsgCall.getDesc());
            View wakeRoot = view.findViewById(R.id.item_wake_root);
            wakeRoot.setTag(mCustomMsgCall);
            wakeRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(null!=v.getTag()&&null!=context&&context instanceof Activity){
                        LiveRoomPullActivity activity = LiveRoomPullActivity.getInstance();
                        if(null!=activity){
                            activity.finish();
                        }

                        CustomMsgCall customMsgCall = (CustomMsgCall) v.getTag();
                        CallExtraInfo callExtraInfo=new CallExtraInfo();
                        callExtraInfo.setToUserID(customMsgCall.getAnchorId());
                        callExtraInfo.setToNickName(customMsgCall.getAnchorNickName());
                        callExtraInfo.setToAvatar(customMsgCall.getAnchorAvatar());
                        MakeCallManager.getInstance().attachActivity(((Activity) context)).mackCall(callExtraInfo, 1);
                    }
                }
            });
        }
    }

    /**
     * 系统推送的语音消息,只会出现在左侧
     * @param viewHolder
     * @param context
     */
    private void showVoiceMessahe(ChatAdapter.ViewHolder viewHolder, Context context) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.chat_voice_reset_api, null);
        viewHolder.rightAvatar.setVisibility(View.VISIBLE);
        viewHolder.leftAvatar.setVisibility(View.VISIBLE);
        //发送人头像
        FriendProfile profile = FriendshipInfo.getInstance().getProfile(message.getConversation().getPeer());
        profile = getProfile(profile);
        Object headUrl;
        if (profile != null) {
            headUrl=profile.getAvatarUrl();
        } else {
            if(null!=mCustomMsgCall){
                headUrl=mCustomMsgCall.getAnchorAvatar();
            }else{
                headUrl=R.drawable.ic_default_user_head;
            }
        }
        Glide.with(context).load(headUrl).error(R.drawable.ic_default_user_head).into(viewHolder.leftAvatar);
        setParams(viewHolder,false);
        final RelativeLayout container = getBubbleView(viewHolder);
        container.setPadding(0, 0,0, 0);
        container.setBackground(null);
        container.addView(view);
        //确定消息的发出方向样式
        LinearLayout msgContent  = view.findViewById(R.id.ll_msg_content);
        //动画
        ImageView voideIcon  = view.findViewById(R.id.iv_voice_icon);
        //播放状态
        final View msgRead  = view.findViewById(R.id.view_msg_read);
        //语音时长
        TextView msgDurtion = (TextView) view.findViewById(R.id.view_msg_durtion);
        voideIcon.setImageResource(message.isSelf()? R.drawable.right_voice: R.drawable.left_voice);
        final AnimationDrawable animationDrawable = (AnimationDrawable) voideIcon.getDrawable();
        msgContent.setBackgroundResource(isSelf()?R.drawable.ic_msg_item_right:R.drawable.ic_msg_item_left);
        if(null!=mResetVoiceMessage){
            setLayoutParams(msgContent,mResetVoiceMessage.getDurtion());
            //如果当前音频正在播放，回显播放状态
            if(VoiceModelManager.getInstance().isPlaying(mResetVoiceMessage.getId())){
                VoiceModelManager.getInstance().updataAnimation(animationDrawable);
            }
            //未、已播放状态
            msgRead.setBackgroundResource(VoiceModelManager.getInstance().isRead(mResetVoiceMessage.getId())?0:R.drawable.arl_app_stype_gray_dot);
            msgDurtion.setText(VoiceModelManager.getInstance().formatDurtion(mResetVoiceMessage.getDurtion()));
            msgContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //立即标记已播放状态
                    msgRead.setBackgroundResource(0);
                    VoiceModelManager.getInstance().startPlay(mResetVoiceMessage,animationDrawable);
                }
            });
        }
    }

    /**
     * 获取消息摘要
     */
    @Override
    public String getSummary()       {
        return "";
    }

    /**
     * 保存消息或消息文件
     */
    @Override
    public void save() {

    }


    private FriendProfile getProfile(FriendProfile profile) {
        if (profile == null) {
            profile = FriendManager.getInstance().getFriendShipsById(message.getConversation().getPeer());
        }
        return profile;
    }

    /**
     * 根据语音时长确定ITEM的宽度
     * @param msgContent
     * @param durtion
     */
    private void setLayoutParams(LinearLayout msgContent, long durtion) {
        if(null==msgContent) return;
        int width = Utils.dip2px((float) (80 + Math.ceil(Math.min(durtion , 60) / 5 * 10)));
        msgContent.getLayoutParams().width=width;
    }

    private void setParams(ChatAdapter.ViewHolder viewHolder,boolean isLive) {
        if (isSelf()) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewHolder.rightPanel.getLayoutParams();
            layoutParams.rightMargin = (Utils.dip2px(10));
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            viewHolder.rightPanel.setLayoutParams(layoutParams);
        } else {
            //左侧消息父容器
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewHolder.leftPanel.getLayoutParams();
            layoutParams.leftMargin = (Utils.dip2px(10));
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            viewHolder.leftPanel.setLayoutParams(layoutParams);
            //消息体
            LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams) viewHolder.leftMessage.getLayoutParams();
            layoutParams1.leftMargin = (Utils.dip2px(6));
            viewHolder.leftMessage.setLayoutParams(layoutParams1);
            //左侧用户头像
            RelativeLayout.MarginLayoutParams leftLayoutParams = (RelativeLayout.MarginLayoutParams) viewHolder.item_left_user_icon_view.getLayoutParams();
            leftLayoutParams.topMargin = isLive ? Utils.dip2px(35) : 0;
            viewHolder.item_left_user_icon_view.setLayoutParams(leftLayoutParams);
        }
    }

    /**
     * 预览多媒体文件
     * @param privateMedia
     * @param context
     * @param identify
     * @param view
     */
    private void startPreviewMedia(final PrivateMedia privateMedia, final Context context, final String identify, final View view) {
        if(null!=context&&context instanceof Activity){
            //关闭可能已经打开的多媒体预览界面
            ApplicationManager.getInstance().observerUpdata(Constant.OBSERVER_FINLISH_MEDIA_PLAYER);
            privateMedia.setUserid(identify);
            List<PrivateMedia> list=new ArrayList<>();
            list.add(privateMedia);
            VideoDataUtils.getInstance().setVideoData(list,0);
            VideoDataUtils.getInstance().setSource("0");
            VideoDataUtils.getInstance().setHostUrl(NetContants.getInstance().URL_FILE_LIST());
            VideoDataUtils.getInstance().setIndex(-2);
            new android.os.Handler(Looper.getMainLooper()).postAtTime(new Runnable() {
                @Override
                public void run() {
                    //预览照片
                    if(Constant.MEDIA_TYPE_IMAGE==privateMedia.getFile_type()){
                        VerticalImagePreviewActivity.start((Activity) context,identify,view);
                        return;
                    }else if(Constant.MEDIA_TYPE_VIDEO==privateMedia.getFile_type()){
                        VerticalVideoPlayerAvtivity.start((Activity) context,identify, NetContants.getInstance().URL_FILE_LIST(),-1,0,0,privateMedia.getId(),view);
                        return;
                    }
                }
            }, SystemClock.uptimeMillis()+100);
        }
    }

    public enum Type {
        INPUTING,
        INVALID,
        CALL,
        CHAT_GIFT,
        AV_GROUP,
        AWARD,
        LIVE,
        MEDIA,
        CALL_CUSTOM,
        CALL_WAKEUP,
        CHAT_VOICE
    }
}
