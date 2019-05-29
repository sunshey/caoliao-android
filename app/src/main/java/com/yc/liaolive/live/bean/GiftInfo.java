package com.yc.liaolive.live.bean;

import java.io.Serializable;

/**
 * TinyHung@Outlook.com
 * 2018/5/14
 */

public class GiftInfo  implements Serializable{

    private long id;//礼物ID
    private String title;//标题
    private String desp;//介绍
    private int price;//价格
    private String src;//封面
    private String tag;//标签
    private boolean selector;//是否选中
    private int gift_type;//分类类别,4：全站，需要全局广播
    private String big_svga;//奢侈礼物  SVGA
    private String svga;//礼物 ICON  SVGA
    private int drawTimes;//中奖倍数
    private int drawIntegral;//中奖积分
    private int count;//用户点击选中的礼物个数
    private int addtime;
    private String source_room_id="";//消息来源房间id
    private int prize_level;//中奖等级 1：普通 2：大奖 3：超级大奖
    private int gift_category;//0:奢侈礼物 1：幸运礼物
    private boolean isTanmu;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesp() {
        return desp;
    }

    public void setDesp(String desp) {
        this.desp = desp;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }


    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isSelector() {
        return selector;
    }

    public void setSelector(boolean selector) {
        this.selector = selector;
    }

    public int getGiftType() {
        return gift_type;
    }

    public void setGiftType(int giftType) {
        this.gift_type = giftType;
    }

    public String getBigSvga() {
        return big_svga;
    }

    public void setBigSvga(String bigSvga) {
        this.big_svga = bigSvga;
    }

    public String getSvga() {
        return svga;
    }

    public void setSvga(String svga) {
        this.svga = svga;
    }


    public int getDrawTimes() {
        return drawTimes;
    }

    public void setDrawTimes(int drawTimes) {
        this.drawTimes = drawTimes;
    }

    public int getDrawIntegral() {
        return drawIntegral;
    }

    public void setDrawIntegral(int drawIntegral) {
        this.drawIntegral = drawIntegral;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getAddtime() {
        return addtime;
    }

    public void setAddtime(int addtime) {
        this.addtime = addtime;
    }

    public int getGift_type() {
        return gift_type;
    }

    public void setGift_type(int gift_type) {
        this.gift_type = gift_type;
    }

    public String getBig_svga() {
        return big_svga;
    }

    public void setBig_svga(String big_svga) {
        this.big_svga = big_svga;
    }

    public String getSource_room_id() {
        return source_room_id;
    }

    public void setSource_room_id(String source_room_id) {
        this.source_room_id = source_room_id;
    }

    public int getPrize_level() {
        return prize_level;
    }

    public void setPrize_level(int prize_level) {
        this.prize_level = prize_level;
    }

    public int getGift_category() {
        return gift_category;
    }

    public void setGift_category(int gift_category) {
        this.gift_category = gift_category;
    }

    public boolean isTanmu() {
        return isTanmu;
    }

    public void setTanmu(boolean tanmu) {
        isTanmu = tanmu;
    }

    @Override
    public String toString() {
        return "GiftInfo{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", desp='" + desp + '\'' +
                ", price=" + price +
                ", src='" + src + '\'' +
                ", tag='" + tag + '\'' +
                ", selector=" + selector +
                ", giftType=" + gift_type +
                ", bigSvga='" + big_svga + '\'' +
                ", svga='" + svga + '\'' +
                ", drawTimes=" + drawTimes +
                ", drawIntegral=" + drawIntegral +
                ", count=" + count +
                ", addtime=" + addtime +
                ", source_room_id=" + source_room_id +
                ", prize_level=" + prize_level +
                ", gift_category=" + gift_category +
                ", tanmu=" + isTanmu +
                '}';
    }
}
