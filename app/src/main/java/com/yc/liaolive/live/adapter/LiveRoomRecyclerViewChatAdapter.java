package com.yc.liaolive.live.adapter;

import android.graphics.Color;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.yc.liaolive.base.adapter.BaseMultiItemQuickAdapter;
import com.yc.liaolive.base.adapter.BaseViewHolder;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.interfaces.PerfectClickListener;
import com.yc.liaolive.live.bean.CustomMsgInfo;
import com.yc.liaolive.live.util.LiveChatUserGradleSpan;
import com.yc.liaolive.util.ScreenUtils;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/5/11
 * 直播间聊天、消息列表适配器
 */

public class LiveRoomRecyclerViewChatAdapter extends BaseMultiItemQuickAdapter<CustomMsgInfo,BaseViewHolder> {

    public static final int ITEM_MESSAGE_DEFAULT =110;//默认
    public static final int ITEM_MESSAGE_CONTENT =0;//普通消息
    public static final int ITEM_MESSAGE_GIFT=1;//礼物消息
    public static final int ITEM_MESSAGE_SYSTEM =2;//系统通知
    //应用场景
    public static final int SCEEN_MODE_ROOM=0;
    public static final int SCEEN_MODE_PRIVATE_ROOM=1;
    private final int mGiftIconWidth;
    private int mSceenMode=SCEEN_MODE_ROOM;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     * @param data A new list is created out of this one to avoid mutable list
     */
    public LiveRoomRecyclerViewChatAdapter(List<CustomMsgInfo> data) {
        super(data);
        addItemType(ITEM_MESSAGE_DEFAULT,R.layout.item_live_chat_system_msg_list);
        addItemType(ITEM_MESSAGE_CONTENT,R.layout.item_live_chat_message_list);
        addItemType(ITEM_MESSAGE_GIFT,R.layout.item_live_chat_gift_list);
        addItemType(ITEM_MESSAGE_SYSTEM,R.layout.item_live_chat_system_msg_list);
        mGiftIconWidth = ScreenUtils.dpToPxInt(13f);
    }

    @Override
    protected void convert(BaseViewHolder helper, CustomMsgInfo item) {
        switch (item.getItemType()) {
            case ITEM_MESSAGE_DEFAULT:
                createDefaultItem(helper,item);
                break;
            case ITEM_MESSAGE_CONTENT:
                createMessageItem(helper,item);
                break;
            case ITEM_MESSAGE_GIFT:
                createGiftMessageItem(helper,item);
                break;
            case ITEM_MESSAGE_SYSTEM:
                createSystemMessageItem(helper,item);
                break;
        }
    }

    /**
     * 初始化一个默认的Item
     * @param holder
     * @param customMsgInfo
    content="#notify#通知#notify# "+" <font color='#FFEDA6'>"+data.getSendUserName()+"：</font>"+data.getMsgContent();
    content="#gradle#"+data.getSendUserGradle()+"#gradle#"+" <font color='#FFEDA6'>"+data.getSendUserName()+"</font>："+data.getMsgContent();
    SpannableString spannableString = LiveChatUserGradleSpan.getSpannableDrawableText(Html.fromHtml(content), data.getSendUserName(), null, viewHolder.tvContent, null);
     */
    private void createDefaultItem(BaseViewHolder holder,CustomMsgInfo customMsgInfo) {
        if(null!=customMsgInfo){
            ((TextView) holder.getView(R.id.tv_item_content)).setText(Html.fromHtml("<font color='#E0DBDB'>直播间网警</font> <font color='#FFF566'>进入直播间</font>"));
        }
    }

    /**
     * 初始化普通消息Item
     * @param holder
     * @param customMsgInfo
     */
    private void createMessageItem(BaseViewHolder holder, CustomMsgInfo customMsgInfo) {
        if(null!=customMsgInfo){
            //LiveUtils.setUserGradle(((ImageView) holder.getView(R.id.item_user_gradle)),customMsgInfo.getSendUserGradle());//设置用户等级
            ImageView vipGradle = (ImageView) holder.getView(R.id.item_vip_gradle);
            vipGradle.setVisibility(customMsgInfo.getSendUserVIP()>0?View.VISIBLE:View.GONE);
            vipGradle.setImageResource(customMsgInfo.getSendUserVIP()>0?R.drawable.ic_vip_icon:0);//会员等级
            //根据会员等级设置背景样式
            View itemLayout = holder.getView(R.id.ll_item_layout);
            itemLayout.setBackgroundResource(customMsgInfo.getSendUserVIP()>0?R.drawable.full_live_red_cacht_content_bg:R.drawable.shape_room_cacht_content_bg);
            String nickName="未知用户";
            try {
                nickName= URLDecoder.decode(null==customMsgInfo.getSendUserName()?"未知用户":customMsgInfo.getSendUserName().replaceAll("%", "%25"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }catch (RuntimeException e){
                e.printStackTrace();
            }finally {
                String content="<font color='#E0DBDB'>"+nickName+"</font>  <font color='#FFF566'>"+customMsgInfo.getMsgContent()+"</font>";//默认样式的消息
                //观众进场
                if(Constant.MSG_CUSTOM_ADD_USER.equals(customMsgInfo.getChildCmd())){
                    if(Constant.USER_TYPE_SERVER==customMsgInfo.getSendUserType()){
                        content="<font color='#FF5654'>"+nickName+"</font>  <font color='#FFF566'>"+customMsgInfo.getMsgContent()+"</font>";
                    }else{
                        content="<font color='#E0DBDB'>"+nickName+"</font>  <font color='#FFF566'>"+customMsgInfo.getMsgContent()+"</font>";
                    }
                    //普通聊天消息
                }else if(Constant.MSG_CUSTOM_TEXT.equals(customMsgInfo.getChildCmd())){
                    //客服身份
                    if(Constant.USER_TYPE_SERVER==customMsgInfo.getSendUserType()){
                        content="<font color='#FF5654'>"+nickName+"  </font>"+"<font color='#FFF566'>"+customMsgInfo.getMsgContent()+"</font>";
                    }else{
                        content="<font color='#E0DBDB'>"+nickName+"</font>  <font color='#FFF566'>"+customMsgInfo.getMsgContent()+"</font>";
                    }
                    //关注主播
                }else if(Constant.MSG_CUSTOM_FOLLOW_ANCHOR.equals(customMsgInfo.getChildCmd())){
                    content="<font color='#E0DBDB'>"+nickName+"</font>  <font color='#6DEAFB'>"+customMsgInfo.getMsgContent()+"</font>";
                    //警告、错误消息
                }else if(Constant.MSG_CUSTOM_ERROR.equals(customMsgInfo.getChildCmd())){
                    content="<font color='#E0DBDB'>"+nickName+"</font>  <font color='#FF7575'>"+customMsgInfo.getMsgContent()+"</font>";
                }
                //自定义表情处理
                TextView tvContent = (TextView) holder.getView(R.id.tv_item_content);
                SpannableString spannableString = LiveChatUserGradleSpan.stringFormatEmoji(Html.fromHtml(content), tvContent);
                tvContent.setText(spannableString);
                itemLayout.setTag(customMsgInfo);
                itemLayout.setOnClickListener(new PerfectClickListener(300) {
                    @Override
                    protected void onNoDoubleClick(View v) {
                        if(null!=mOnItemClickListener) mOnItemClickListener.onItemClick(0,v, (CustomMsgInfo) v.getTag());
                    }
                });
            }
        }
    }

    /**
     * 初始化一个礼物条目消息,主播端显示
     * @param holder
     * @param customMsgInfo
     * @return
     */
    private void createGiftMessageItem(BaseViewHolder holder, CustomMsgInfo customMsgInfo) {
        if(null!=customMsgInfo){
            LinearLayout itemLayout = holder.getView(R.id.ll_item_layout);
            itemLayout.removeAllViews();
            if(Constant.MSG_CUSTOM_GIFT.equals(customMsgInfo.getChildCmd())&&null!=customMsgInfo.getGift()){
                String nickName="未知用户";
                String accapNickName="主播";
                try {
                    nickName=URLDecoder.decode(null==customMsgInfo.getSendUserName()?"未知用户":customMsgInfo.getSendUserName().replaceAll("%", "%25"), "utf-8");
                    accapNickName=URLDecoder.decode(null==customMsgInfo.getAccapUserName()?"主播":customMsgInfo.getAccapUserName().replaceAll("%", "%25"), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }finally {
                    TextView textView=new TextView(holder.itemView.getContext());
                    textView.setTextColor(Color.parseColor("#E0DBDB"));
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
                    //昵称、描述
                    if(1==mSceenMode){
                        Spanned spanned = Html.fromHtml("<font color='#E0DBDB'>"+nickName + "</font>  <font color='#91FFC0'>送给"+accapNickName+ " "+customMsgInfo.getGift().getTitle() + "</font>  ");
                        textView.setText(spanned);
                    }else{
                        Spanned spanned = Html.fromHtml("<font color='#E0DBDB'>"+nickName + "</font>  <font color='#91FFC0'>送给主播" + customMsgInfo.getGift().getTitle() + "</font>  ");
                        textView.setText(spanned);
                    }

                    //礼物ICON
                    ImageView imageView=new ImageView(holder.itemView.getContext());
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(mGiftIconWidth,mGiftIconWidth);
                    imageView.setLayoutParams(layoutParams);
                    Glide.with(holder.itemView.getContext())
                            .load(customMsgInfo.getGift().getSrc())
                            .error(R.drawable.ic_video_live)
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.RESULT)//缓存源资源和转换后的资源
                            .skipMemoryCache(true)//跳过内存缓存
                            .into(imageView);
                    //数量
                    TextView textViewCount=new TextView(holder.itemView.getContext());
                    textViewCount.setTextColor(Color.parseColor("#91FFC0"));
                    textViewCount.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
                    textViewCount.setText("  x"+customMsgInfo.getGift().getCount());

                    itemLayout.setBackgroundResource(R.drawable.shape_room_cacht_content_bg);
                    itemLayout.addView(textView,0);
                    itemLayout.addView(imageView,1);
                    itemLayout.addView(textViewCount,2);
                    itemLayout.setTag(customMsgInfo);
                    itemLayout.setOnClickListener(new PerfectClickListener(300) {
                        @Override
                        protected void onNoDoubleClick(View v) {
                            if(null!=mOnItemClickListener) mOnItemClickListener.onItemClick(0,v, (CustomMsgInfo) v.getTag());
                        }
                    });
                }
            }
        }
    }

    /**
     * 初始化系统消息Item
     * @param holder
     * @param customMsgInfo
     */
    private void createSystemMessageItem(BaseViewHolder holder, CustomMsgInfo customMsgInfo) {
        if(null!=customMsgInfo){
            String textColor=TextUtils.isEmpty(customMsgInfo.getMsgContentColor())?"#FF7575":customMsgInfo.getMsgContentColor();
            TextView tvContent = (TextView) holder.getView(R.id.tv_item_content);
            tvContent.setTextColor(Color.parseColor(textColor));
            tvContent.setText(customMsgInfo.getMsgContent());
        }
    }

    public void setSceenMode(int sceenMode) {
        this.mSceenMode=sceenMode;
    }

    public interface OnItemClickListener{
        void onItemClick(int position, View view, CustomMsgInfo customMsgInfo);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
