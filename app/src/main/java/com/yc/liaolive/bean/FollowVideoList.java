package com.yc.liaolive.bean;

import com.yc.liaolive.base.adapter.entity.MultiItemEntity;
import java.io.Serializable;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017/5/31 14:14
 * 关注列表
 */
public class FollowVideoList implements Serializable {

    private int code;
    private DataBean data;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class DataBean implements Serializable{
        /**
         * add_time : 1499934284
         * collect_times : 0
         * comment_count : 6
         * comment_list : [{"add_time":"1499941741","comment":"还是没有高度呢","id":"119","logo":"http://app.nq6.com//Upload/Picture/2017-07-06/595de1bf2efd8.jpg","nickname":"聪明的小明","status":"0","to_nickname":"","to_user_id":"0","user_id":"1060153","video_id":"300"},{"add_time":"1499941726","comment":"现在呢？","id":"118","logo":"http://app.nq6.com//Upload/Picture/2017-07-06/595de1bf2efd8.jpg","nickname":"聪明的小明","status":"0","to_nickname":"","to_user_id":"0","user_id":"1060153","video_id":"300"}]
         * comment_times : 6
         * cover : http://app.nq6.com/Upload/Cover/1060150/13bdf8f7cd89aca3480a2608c73c7444_25.jpg
         * cur_frame : 25
         * desp : #哈士奇##萌宠#
         * fans_user_id : 1060153
         * file_md5 : 13bdf8f7cd89aca3480a2608c73c7444
         * imeil : 7acf482a762201be
         * is_exec : 1
         * is_follow : 1
         * is_hot : 0
         * is_interest : 0
         * logo : http://wx.qlogo.cn/mmopen/NqY4W322I5uZu0OjOGQXmWyTDeyvb3sNYoTN8kxnKUibbKbuFVFA87SlHlY6ib0xUPuwZ21gBkba0cbibWibwVMPDm6o7XF9Msgh/0
         * nickname : 相信我
         * path : http://app.nq6.com/Upload/Video/13bdf8f7cd89aca3480a2608c73c7444.mp4
         * play_times : 8
         * share_times : 0
         * status : 1
         * type : 2
         * user_id : 1060150
         * video_id : 300
         */

        private List<ListsBean> lists;

        public List<ListsBean> getLists() {
            return lists;
        }

        public void setLists(List<ListsBean> lists) {
            this.lists = lists;
        }

        public static class ListsBean implements Serializable,MultiItemEntity{
            private String add_time;
            private String collect_times;
            private String comment_count;
            private String comment_times;
            private String cover;
            private String cur_frame;
            private String desp;
            private String fans_user_id;
            private String file_md5;
            private String imeil;
            private String is_exec;
            private int is_follow;
            private String is_hot;
            private int is_interest;
            private String logo;
            private String nickname;
            private String path;
            private String play_times;
            private String share_times;
            private String status;
            private String type;
            private String user_id;
            private String video_id;
            private int itemType;
            private String is_private;
            private String video_width;
            private String video_height;
            private boolean isSelector;
            private String download_permiss ;//下载权限
            private String cate;//视频分类
            private int itemIndex;//Item在二维和一维数组中是唯一的

            public int getItemIndex() {
                return itemIndex;
            }

            public void setItemIndex(int itemIndex) {
                this.itemIndex = itemIndex;
            }

            public String getCate() {
                return cate;
            }

            public void setCate(String cate) {
                this.cate = cate;
            }

            public String getDownload_permiss() {
                return download_permiss;
            }

            public void setDownload_permiss(String download_permiss) {
                this.download_permiss = download_permiss;
            }

            public boolean isSelector() {
                return isSelector;
            }

            public void setSelector(boolean selector) {
                isSelector = selector;
            }

            /**
             * add_time : 1499941741
             * comment : 还是没有高度呢
             * id : 119
             * logo : http://app.nq6.com//Upload/Picture/2017-07-06/595de1bf2efd8.jpg
             * nickname : 聪明的小明
             * status : 0
             * to_nickname :
             * to_user_id : 0
             * user_id : 1060153
             * video_id : 300
             */

            private List<CommentListBean> comment_list;

            public int getItemType() {
                return itemType;
            }

            public void setItemType(int itemType) {
                this.itemType = itemType;
            }

            public String getAdd_time() {
                return add_time;
            }

            public void setAdd_time(String add_time) {
                this.add_time = add_time;
            }

            public String getCollect_times() {
                return collect_times;
            }

            public void setCollect_times(String collect_times) {
                this.collect_times = collect_times;
            }

            public String getComment_count() {
                return comment_count;
            }

            public void setComment_count(String comment_count) {
                this.comment_count = comment_count;
            }

            public String getComment_times() {
                return comment_times;
            }

            public void setComment_times(String comment_times) {
                this.comment_times = comment_times;
            }

            public String getCover() {
                return cover;
            }

            public void setCover(String cover) {
                this.cover = cover;
            }

            public String getCur_frame() {
                return cur_frame;
            }

            public void setCur_frame(String cur_frame) {
                this.cur_frame = cur_frame;
            }

            public String getDesp() {
                return desp;
            }

            public void setDesp(String desp) {
                this.desp = desp;
            }

            public String getFans_user_id() {
                return fans_user_id;
            }

            public void setFans_user_id(String fans_user_id) {
                this.fans_user_id = fans_user_id;
            }

            public String getFile_md5() {
                return file_md5;
            }

            public void setFile_md5(String file_md5) {
                this.file_md5 = file_md5;
            }

            public String getImeil() {
                return imeil;
            }

            public void setImeil(String imeil) {
                this.imeil = imeil;
            }

            public String getIs_exec() {
                return is_exec;
            }

            public void setIs_exec(String is_exec) {
                this.is_exec = is_exec;
            }

            public int getIs_follow() {
                return is_follow;
            }

            public void setIs_follow(int is_follow) {
                this.is_follow = is_follow;
            }

            public String getIs_hot() {
                return is_hot;
            }

            public void setIs_hot(String is_hot) {
                this.is_hot = is_hot;
            }

            public int getIs_interest() {
                return is_interest;
            }

            public void setIs_interest(int is_interest) {
                this.is_interest = is_interest;
            }

            public String getLogo() {
                return logo;
            }

            public void setLogo(String logo) {
                this.logo = logo;
            }

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public String getPath() {
                return path;
            }

            public void setPath(String path) {
                this.path = path;
            }

            public String getPlay_times() {
                return play_times;
            }

            public void setPlay_times(String play_times) {
                this.play_times = play_times;
            }

            public String getShare_times() {
                return share_times;
            }

            public void setShare_times(String share_times) {
                this.share_times = share_times;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getUser_id() {
                return user_id;
            }

            public void setUser_id(String user_id) {
                this.user_id = user_id;
            }

            public String getVideo_id() {
                return video_id;
            }

            public void setVideo_id(String video_id) {
                this.video_id = video_id;
            }

            public String getIs_private() {
                return is_private;
            }

            public void setIs_private(String is_private) {
                this.is_private = is_private;
            }

            public String getVideo_width() {
                return video_width;
            }

            public void setVideo_width(String video_width) {
                this.video_width = video_width;
            }

            public String getVideo_height() {
                return video_height;
            }

            public void setVideo_height(String video_height) {
                this.video_height = video_height;
            }


            public List<CommentListBean> getComment_list() {
                return comment_list;
            }

            public void setComment_list(List<CommentListBean> comment_list) {
                this.comment_list = comment_list;
            }

            public static class CommentListBean implements Serializable{
                private String add_time;
                private String comment;
                private String id;
                private String logo;
                private String nickname;
                private String status;
                private String to_nickname;
                private String to_user_id;
                private String user_id;
                private String video_id;

                public String getAdd_time() {
                    return add_time;
                }

                public void setAdd_time(String add_time) {
                    this.add_time = add_time;
                }

                public String getComment() {
                    return comment;
                }

                public void setComment(String comment) {
                    this.comment = comment;
                }

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getLogo() {
                    return logo;
                }

                public void setLogo(String logo) {
                    this.logo = logo;
                }

                public String getNickname() {
                    return nickname;
                }

                public void setNickname(String nickname) {
                    this.nickname = nickname;
                }

                public String getStatus() {
                    return status;
                }

                public void setStatus(String status) {
                    this.status = status;
                }

                public String getTo_nickname() {
                    return to_nickname;
                }

                public void setTo_nickname(String to_nickname) {
                    this.to_nickname = to_nickname;
                }

                public String getTo_user_id() {
                    return to_user_id;
                }

                public void setTo_user_id(String to_user_id) {
                    this.to_user_id = to_user_id;
                }

                public String getUser_id() {
                    return user_id;
                }

                public void setUser_id(String user_id) {
                    this.user_id = user_id;
                }

                public String getVideo_id() {
                    return video_id;
                }

                public void setVideo_id(String video_id) {
                    this.video_id = video_id;
                }
            }
        }
    }
}
