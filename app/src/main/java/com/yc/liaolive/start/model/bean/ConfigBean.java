package com.yc.liaolive.start.model.bean;

import com.yc.liaolive.bean.NoticeMessage;
import com.yc.liaolive.bean.ServerBean;
import java.io.Serializable;
import java.util.List;

/**
 * 启动配置
 * Created by yangxueqin on 2019/1/21.
 */

public class ConfigBean implements Serializable {
    /**
     * official_notice : {"room":{"content":"系统公告：直播严禁低俗、色情、引诱、暴力、暴露、赌博、反动等不良内容，一旦涉及将被封禁账号，网警和房管24小时在线巡查！","color":"#FF7575"},"video_chat":{"content":"系统公告：您已开始视频聊天，请遵守视频聊天行为规范，任何不良的违规行为将会被记录，并提供给有关部门依法处置的依据！","color":"#FF7575"},"message":{"content":"请勿私加微信、QQ。谨防受骗。禁止涉黄辱骂等违规行为，倡导绿色交友","color":"#000000"}}
     * gift_config : [[[0,500],[1,9,19,99,520,999]],[[501,2000],[1,9,19,99]],[[2001,1000000],[1]]]
     * gift_edit_lasttime : 0
     * server : {"server_identify":"1000000000","server_avatar":"http://a.tnxxjs.com/uploads/appupload/ci1sbfxcrksfuj8hm2_1547603805!40x40.png","server_nickname":"红娘客服","server_desc":"投诉、申请售后及订单异常联系我们"}
     * home_page : [{"type":0,"text":"首页","target_id":"1","img_url":"","width":"0","height":"0","show_index":"0","son_page":[{"type":"0","text":"小视频","target_id":"1","img_url":"","width":"0","height":"0","show_index":"0","son_page":[{"type":"0","text":"推荐","target_id":"1","img_url":"","width":"0","height":"0","show_index":"0","son_page":[]},{"type":"0","text":"最新","target_id":"2","img_url":"","width":"0","height":"0","show_index":"0","son_page":[]},{"type":"0","text":"付费","target_id":"2","img_url":"","width":"0","height":"0 ","show_index":"0","son_page":[]},{"type":"0","text":"热门","target_id":"3","img_url":"","width":"0","height":"0","show_index":"0","son_page":[]}]},{"type":"0","text":"1v1快聊","target_id":"4","img_url":"","width":"0","height":"0","show_index":1,"son_page":[]},{"type":"0","text":"图片","target_id":"5","img_url":"","width":"0","height":"0","show_index":1,"son_page":[{"type":"0","text":"推荐","target_id":"6","img_url":"","width":"0","height":"0 ","show_index":"0","son_page":[]},{"type":"0","text":"最新","target_id":"7","img_url":"","width":"0","height":"0 ","show_index":"0","son_page":[]},{"type":"0","text":"付费","target_id":"8","img_url":"","width":"0","height":"0 ","show_index":"0","son_page":[]},{"type":"0","text":"热门","target_id":"9","img_url":"","width":"0","height":"0 ","show_index":"0","son_page":[]}]}]},{"type":"0","text":"关注","target_id":"10","img_url":"","width":"0","height":"0 ","show_index":"0","son_page":[]},{"type":"1","text":"1v1","target_id":"4","img_url":"http://zbtest.6071.com/uploads/images/20180727/318de31787d2db12b94334db23a39dec.png","width":"100","height":"100 ","show_index":"0","son_page":[]},{"type":"0","text":"消息","target_id":"11","img_url":"","width":"0","height":"0","show_index":"0","son_page":[]},{"type":"0","text":"我的","target_id":"12","img_url":"","width":"0","height":"0 ","show_index":"0","son_page":[]}]
     */
    private NoticeMessage official_notice;
    private String gift_edit_lasttime;
    private ServerBean server;
    private List<List<List<Integer>>> gift_config;
    private List<PageBean> home_page;
    private String search_but;
    private String plus_but;
    private IndexShowConfig home_pgae_show_index;
    //图片、视频预览、私信控制器
    private List<String> video_controller;
    private List<String> image_controller;
    private List<String> chat_controller;
    private List<String> asmr_controller;

    public String getPlus_but() {
        return plus_but;
    }

    public void setPlus_but(String plus_but) {
        this.plus_but = plus_but;
    }

    public List<String> getVideo_controller() {
        return video_controller;
    }

    public void setVideo_controller(List<String> video_controller) {
        this.video_controller = video_controller;
    }

    public List<String> getImage_controller() {
        return image_controller;
    }

    public void setImage_controller(List<String> image_controller) {
        this.image_controller = image_controller;
    }

    public List<String> getChat_controller() {
        return chat_controller;
    }

    public void setChat_controller(List<String> chat_controller) {
        this.chat_controller = chat_controller;
    }

    public List<String> getAsmr_controller() {
        return asmr_controller;
    }

    public void setAsmr_controller(List<String> asmr_controller) {
        this.asmr_controller = asmr_controller;
    }

    public IndexShowConfig getHome_pgae_show_index() {
        return home_pgae_show_index;
    }

    public void setHome_pgae_show_index(IndexShowConfig home_pgae_show_index) {
        this.home_pgae_show_index = home_pgae_show_index;
    }

    public NoticeMessage getOfficial_notice() {
        return official_notice;
    }

    public void setOfficial_notice(NoticeMessage official_notice) {
        this.official_notice = official_notice;
    }

    public String getGift_edit_lasttime() {
        return gift_edit_lasttime;
    }

    public void setGift_edit_lasttime(String gift_edit_lasttime) {
        this.gift_edit_lasttime = gift_edit_lasttime;
    }

    public ServerBean getServer() {
        return server;
    }

    public void setServer(ServerBean server) {
        this.server = server;
    }

    public List<List<List<Integer>>> getGift_config() {
        return gift_config;
    }

    public void setGift_config(List<List<List<Integer>>> gift_config) {
        this.gift_config = gift_config;
    }

    public List<PageBean> getHome_page() {
        return home_page;
    }

    public void setHome_page(List<PageBean> home_page) {
        this.home_page = home_page;
    }

    public String getSearch_but() {
        return search_but;
    }

    public void setSearch_but(String search_but) {
        this.search_but = search_but;
    }

    public static class PageBean  implements Serializable{
        /**
         * type : 0
         * text : 首页
         * target_id : 1
         * url : http://www.baidu.com
         * sub_title : http://www.baidu.com
         * img_url :
         * width : 0
         * height : 0
         * show_index : 0
         * son_page : [{"type":"0","text":"小视频","target_id":"1","img_url":"","width":"0","height":"0","show_index":"0","son_page":[{"type":"0","text":"推荐","target_id":"1","img_url":"","width":"0","height":"0","show_index":"0","son_page":[]},{"type":"0","text":"最新","target_id":"2","img_url":"","width":"0","height":"0","show_index":"0","son_page":[]},{"type":"0","text":"付费","target_id":"2","img_url":"","width":"0","height":"0 ","show_index":"0","son_page":[]},{"type":"0","text":"热门","target_id":"3","img_url":"","width":"0","height":"0","show_index":"0","son_page":[]}]},{"type":"0","text":"1v1快聊","target_id":"4","img_url":"","width":"0","height":"0","show_index":1,"son_page":[]},{"type":"0","text":"图片","target_id":"5","img_url":"","width":"0","height":"0","show_index":1,"son_page":[{"type":"0","text":"推荐","target_id":"6","img_url":"","width":"0","height":"0 ","show_index":"0","son_page":[]},{"type":"0","text":"最新","target_id":"7","img_url":"","width":"0","height":"0 ","show_index":"0","son_page":[]},{"type":"0","text":"付费","target_id":"8","img_url":"","width":"0","height":"0 ","show_index":"0","son_page":[]},{"type":"0","text":"热门","target_id":"9","img_url":"","width":"0","height":"0 ","show_index":"0","son_page":[]}]}]
         */

        private int type;
        private String text;
        private String target_id;
        private String img_url;
        private String width;
        private String height;
        private String show_index;
        private String icon;
        private String open_url;
        private String sub_title;
        private String icon_check;
        private List<SonPageBeanX> son_page;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getTarget_id() {
            return target_id;
        }

        public void setTarget_id(String target_id) {
            this.target_id = target_id;
        }

        public String getImg_url() {
            return img_url;
        }

        public void setImg_url(String img_url) {
            this.img_url = img_url;
        }

        public String getWidth() {
            return width;
        }

        public void setWidth(String width) {
            this.width = width;
        }

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }

        public String getShow_index() {
            return show_index;
        }

        public void setShow_index(String show_index) {
            this.show_index = show_index;
        }

        public List<SonPageBeanX> getSon_page() {
            return son_page;
        }

        public void setSon_page(List<SonPageBeanX> son_page) {
            this.son_page = son_page;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getIcon_check() {
            return icon_check;
        }

        public void setIcon_check(String icon_check) {
            this.icon_check = icon_check;
        }

        public String getOpen_url() {
            return open_url;
        }

        public void setOpen_url(String open_url) {
            this.open_url = open_url;
        }

        public String getSub_title() {
            return sub_title;
        }

        public void setSub_title(String sub_title) {
            this.sub_title = sub_title;
        }

        public static class SonPageBeanX  implements Serializable{
            /**
             * type : 0
             * text : 小视频
             * target_id : 1
             * img_url :
             * width : 0
             * height : 0
             * show_index : 0
             * params : 0
             * son_page : [{"type":"0","text":"推荐","target_id":"1","img_url":"","width":"0","height":"0","show_index":"0","son_page":[]},{"type":"0","text":"最新","target_id":"2","img_url":"","width":"0","height":"0","show_index":"0","son_page":[]},{"type":"0","text":"付费","target_id":"2","img_url":"","width":"0","height":"0 ","show_index":"0","son_page":[]},{"type":"0","text":"热门","target_id":"3","img_url":"","width":"0","height":"0","show_index":"0","son_page":[]}]
             */

            private String type;
            private String text;
            private String target_id;
            private String img_url;
            private String width;
            private String height;
            private String show_index;
            private String filter_type;
            private String params;
            private List<SonPageBean> son_page;

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }

            public String getTarget_id() {
                return target_id;
            }

            public void setTarget_id(String target_id) {
                this.target_id = target_id;
            }

            public String getImg_url() {
                return img_url;
            }

            public void setImg_url(String img_url) {
                this.img_url = img_url;
            }

            public String getWidth() {
                return width;
            }

            public void setWidth(String width) {
                this.width = width;
            }

            public String getHeight() {
                return height;
            }

            public void setHeight(String height) {
                this.height = height;
            }

            public String getShow_index() {
                return show_index;
            }

            public void setShow_index(String show_index) {
                this.show_index = show_index;
            }

            public List<SonPageBean> getSon_page() {
                return son_page;
            }

            public void setSon_page(List<SonPageBean> son_page) {
                this.son_page = son_page;
            }

            public String getFilter_type() {
                return filter_type;
            }

            public void setFilter_type(String filter_type) {
                this.filter_type = filter_type;
            }

            public String getParams() {
                return params;
            }

            public void setParams(String params) {
                this.params = params;
            }

            public static class SonPageBean  implements Serializable{
                /**
                 * type : 0
                 * text : 推荐
                 * target_id : 1
                 * img_url :
                 * width : 0
                 * height : 0
                 * show_index : 0
                 * son_page : []
                 */

                private String type;
                private String text;
                private String target_id;
                private String img_url;
                private String width;
                private String height;
                private String show_index;
                private String filter_type;
                private List<?> son_page;

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }

                public String getText() {
                    return text;
                }

                public void setText(String text) {
                    this.text = text;
                }

                public String getTarget_id() {
                    return target_id;
                }

                public void setTarget_id(String target_id) {
                    this.target_id = target_id;
                }

                public String getImg_url() {
                    return img_url;
                }

                public void setImg_url(String img_url) {
                    this.img_url = img_url;
                }

                public String getWidth() {
                    return width;
                }

                public void setWidth(String width) {
                    this.width = width;
                }

                public String getHeight() {
                    return height;
                }

                public void setHeight(String height) {
                    this.height = height;
                }

                public String getShow_index() {
                    return show_index;
                }

                public void setShow_index(String show_index) {
                    this.show_index = show_index;
                }

                public List<?> getSon_page() {
                    return son_page;
                }

                public void setSon_page(List<?> son_page) {
                    this.son_page = son_page;
                }

                public String getFilter_type() {
                    return filter_type;
                }

                public void setFilter_type(String filter_type) {
                    this.filter_type = filter_type;
                }
            }
        }
    }

    @Override
    public String toString() {
        return "ConfigBean{" +
                "official_notice=" + official_notice +
                ", gift_edit_lasttime='" + gift_edit_lasttime + '\'' +
                ", server=" + server +
                ", gift_config=" + gift_config +
                ", home_page=" + home_page +
                ", search_but='" + search_but + '\'' +
                ", plus_but='" + plus_but + '\'' +
                ", home_pgae_show_index=" + home_pgae_show_index +
                ", video_controller=" + video_controller +
                ", image_controller=" + image_controller +
                ", chat_controller=" + chat_controller +
                ", asmr_controller=" + asmr_controller +
                '}';
    }
}