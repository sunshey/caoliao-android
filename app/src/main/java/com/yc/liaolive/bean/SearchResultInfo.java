package com.yc.liaolive.bean;

import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017/6/8 15:54
 */
public class SearchResultInfo {

    /**
     * code : 1
     * data : {"user_count":"11","user_list":[{"city":"","gender":"男","goldcoin":"0","id":"1060352","image_bg":"","imeil":"","is_follow":0,"login_type":"0","logo":"http://app.nq6.com//Upload/Picture/2017-07-21/a59695f92870.jpg","nickname":"小憨猪","open_id":"","password":"","phone":"","province":"","rank":"1","reg_time":"0","signature":"","status":"0"}],"video_count":"43","video_list":[{"add_time":"1500900730","collect_times":"0","comment_count":"2","comment_list":[{"add_time":"1500969138","comment":"过来瞄瞄","id":"203","logo":"http://wx.qlogo.cn/mmopen/GPyw0pGicibl5ia1mDXay57rEVVZ5mibWRfnxzs2KjP98snOyMiaibaDRJuqV2bAgpicicEMn4ZR8wdkHDd0TbK40wghhe80LEJuPG73/0","nickname":"黄天宇","status":"0","to_nickname":"","to_user_id":"0","user_id":"1060154","video_id":"270"},{"add_time":"1500969134","comment":"什么鬼啊","id":"202","logo":"http://wx.qlogo.cn/mmopen/GPyw0pGicibl5ia1mDXay57rEVVZ5mibWRfnxzs2KjP98snOyMiaibaDRJuqV2bAgpicicEMn4ZR8wdkHDd0TbK40wghhe80LEJuPG73/0","nickname":"黄天宇","status":"0","to_nickname":"","to_user_id":"0","user_id":"1060154","video_id":"270"}],"comment_times":"2","cover":"http://app.nq6.com/Upload/Cover/1060300/1915cd54df36a204f9ef9e936aaba53e_0.jpg","cur_frame":"0","desp":"超萌的外国小妞跑酷","file_md5":"1915cd54df36a204f9ef9e936aaba53e","imeil":"","is_exec":"0","is_follow":0,"is_hot":"0","is_interest":0,"is_private":"0","logo":"http://app.nq6.com//Upload/Picture/2017-07-21/674a58da9230.jpg","nickname":"ˋ宅久天然呆，呆时自萌、","path":"http://app.nq6.com/Upload/Video/1915cd54df36a204f9ef9e936aaba53e.mp4","play_times":"2","share_times":"0","status":"1","type":"3","user_id":"1060300","video_id":"270"}]}
     * msg : 获取搜索数据成功
     */

    private int code;
    /**
     * user_count : 11
     * user_list : [{"city":"","gender":"男","goldcoin":"0","id":"1060352","image_bg":"","imeil":"","is_follow":0,"login_type":"0","logo":"http://app.nq6.com//Upload/Picture/2017-07-21/a59695f92870.jpg","nickname":"小憨猪","open_id":"","password":"","phone":"","province":"","rank":"1","reg_time":"0","signature":"","status":"0"}]
     * video_count : 43
     * video_list : [{"add_time":"1500900730","collect_times":"0","comment_count":"2","comment_list":[{"add_time":"1500969138","comment":"过来瞄瞄","id":"203","logo":"http://wx.qlogo.cn/mmopen/GPyw0pGicibl5ia1mDXay57rEVVZ5mibWRfnxzs2KjP98snOyMiaibaDRJuqV2bAgpicicEMn4ZR8wdkHDd0TbK40wghhe80LEJuPG73/0","nickname":"黄天宇","status":"0","to_nickname":"","to_user_id":"0","user_id":"1060154","video_id":"270"},{"add_time":"1500969134","comment":"什么鬼啊","id":"202","logo":"http://wx.qlogo.cn/mmopen/GPyw0pGicibl5ia1mDXay57rEVVZ5mibWRfnxzs2KjP98snOyMiaibaDRJuqV2bAgpicicEMn4ZR8wdkHDd0TbK40wghhe80LEJuPG73/0","nickname":"黄天宇","status":"0","to_nickname":"","to_user_id":"0","user_id":"1060154","video_id":"270"}],"comment_times":"2","cover":"http://app.nq6.com/Upload/Cover/1060300/1915cd54df36a204f9ef9e936aaba53e_0.jpg","cur_frame":"0","desp":"超萌的外国小妞跑酷","file_md5":"1915cd54df36a204f9ef9e936aaba53e","imeil":"","is_exec":"0","is_follow":0,"is_hot":"0","is_interest":0,"is_private":"0","logo":"http://app.nq6.com//Upload/Picture/2017-07-21/674a58da9230.jpg","nickname":"ˋ宅久天然呆，呆时自萌、","path":"http://app.nq6.com/Upload/Video/1915cd54df36a204f9ef9e936aaba53e.mp4","play_times":"2","share_times":"0","status":"1","type":"3","user_id":"1060300","video_id":"270"}]
     */

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

    public static class DataBean {
        private String user_count;
        private String video_count;
        /**
         * city :
         * gender : 男
         * goldcoin : 0
         * id : 1060352
         * image_bg :
         * imeil :
         * is_follow : 0
         * login_type : 0
         * logo : http://app.nq6.com//Upload/Picture/2017-07-21/a59695f92870.jpg
         * nickname : 小憨猪
         * open_id :
         * password :
         * phone :
         * province :
         * rank : 1
         * reg_time : 0
         * signature :
         * status : 0
         */

        private List<UserListBean> user_list;
        /**
         * add_time : 1500900730
         * collect_times : 0
         * comment_count : 2
         * comment_list : [{"add_time":"1500969138","comment":"过来瞄瞄","id":"203","logo":"http://wx.qlogo.cn/mmopen/GPyw0pGicibl5ia1mDXay57rEVVZ5mibWRfnxzs2KjP98snOyMiaibaDRJuqV2bAgpicicEMn4ZR8wdkHDd0TbK40wghhe80LEJuPG73/0","nickname":"黄天宇","status":"0","to_nickname":"","to_user_id":"0","user_id":"1060154","video_id":"270"},{"add_time":"1500969134","comment":"什么鬼啊","id":"202","logo":"http://wx.qlogo.cn/mmopen/GPyw0pGicibl5ia1mDXay57rEVVZ5mibWRfnxzs2KjP98snOyMiaibaDRJuqV2bAgpicicEMn4ZR8wdkHDd0TbK40wghhe80LEJuPG73/0","nickname":"黄天宇","status":"0","to_nickname":"","to_user_id":"0","user_id":"1060154","video_id":"270"}]
         * comment_times : 2
         * cover : http://app.nq6.com/Upload/Cover/1060300/1915cd54df36a204f9ef9e936aaba53e_0.jpg
         * cur_frame : 0
         * desp : 超萌的外国小妞跑酷
         * file_md5 : 1915cd54df36a204f9ef9e936aaba53e
         * imeil :
         * is_exec : 0
         * is_follow : 0
         * is_hot : 0
         * is_interest : 0
         * is_private : 0
         * logo : http://app.nq6.com//Upload/Picture/2017-07-21/674a58da9230.jpg
         * nickname : ˋ宅久天然呆，呆时自萌、
         * path : http://app.nq6.com/Upload/Video/1915cd54df36a204f9ef9e936aaba53e.mp4
         * play_times : 2
         * share_times : 0
         * status : 1
         * type : 3
         * user_id : 1060300
         * video_id : 270
         */

        private List<VideoListBean> video_list;

        public String getUser_count() {
            return user_count;
        }

        public void setUser_count(String user_count) {
            this.user_count = user_count;
        }

        public String getVideo_count() {
            return video_count;
        }

        public void setVideo_count(String video_count) {
            this.video_count = video_count;
        }

        public List<UserListBean> getUser_list() {
            return user_list;
        }

        public void setUser_list(List<UserListBean> user_list) {
            this.user_list = user_list;
        }

        public List<VideoListBean> getVideo_list() {
            return video_list;
        }

        public void setVideo_list(List<VideoListBean> video_list) {
            this.video_list = video_list;
        }

        public static class UserListBean {
            private String city;
            private String gender;
            private String goldcoin;
            private String id;
            private String image_bg;
            private String imeil;
            private int is_follow;
            private String login_type;
            private String logo;
            private String nickname;
            private String open_id;
            private String password;
            private String phone;
            private String province;
            private String rank;
            private String reg_time;
            private String signature;
            private String status;

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getGender() {
                return gender;
            }

            public void setGender(String gender) {
                this.gender = gender;
            }

            public String getGoldcoin() {
                return goldcoin;
            }

            public void setGoldcoin(String goldcoin) {
                this.goldcoin = goldcoin;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getImage_bg() {
                return image_bg;
            }

            public void setImage_bg(String image_bg) {
                this.image_bg = image_bg;
            }

            public String getImeil() {
                return imeil;
            }

            public void setImeil(String imeil) {
                this.imeil = imeil;
            }

            public int getIs_follow() {
                return is_follow;
            }

            public void setIs_follow(int is_follow) {
                this.is_follow = is_follow;
            }

            public String getLogin_type() {
                return login_type;
            }

            public void setLogin_type(String login_type) {
                this.login_type = login_type;
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

            public String getOpen_id() {
                return open_id;
            }

            public void setOpen_id(String open_id) {
                this.open_id = open_id;
            }

            public String getPassword() {
                return password;
            }

            public void setPassword(String password) {
                this.password = password;
            }

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public String getProvince() {
                return province;
            }

            public void setProvince(String province) {
                this.province = province;
            }

            public String getRank() {
                return rank;
            }

            public void setRank(String rank) {
                this.rank = rank;
            }

            public String getReg_time() {
                return reg_time;
            }

            public void setReg_time(String reg_time) {
                this.reg_time = reg_time;
            }

            public String getSignature() {
                return signature;
            }

            public void setSignature(String signature) {
                this.signature = signature;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }
        }

        public static class VideoListBean {
            private String add_time;
            private String collect_times;
            private String comment_count;
            private String comment_times;
            private String cover;
            private String cur_frame;
            private String desp;
            private String file_md5;
            private String imeil;
            private String is_exec;
            private int is_follow;
            private String is_hot;
            private int is_interest;
            private String is_private;
            private String logo;
            private String nickname;
            private String path;
            private String play_times;
            private String share_times;
            private String status;
            private String type;
            private String user_id;
            private String video_id;
            private String video_width;
            private String download_permiss;
            private String cate;

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

            private String video_height;

            /**
             * add_time : 1500969138
             * comment : 过来瞄瞄
             * id : 203
             * logo : http://wx.qlogo.cn/mmopen/GPyw0pGicibl5ia1mDXay57rEVVZ5mibWRfnxzs2KjP98snOyMiaibaDRJuqV2bAgpicicEMn4ZR8wdkHDd0TbK40wghhe80LEJuPG73/0
             * nickname : 黄天宇
             * status : 0
             * to_nickname :
             * to_user_id : 0
             * user_id : 1060154
             * video_id : 270
             */

            private List<CommentListBean> comment_list;

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

            public String getIs_private() {
                return is_private;
            }

            public void setIs_private(String is_private) {
                this.is_private = is_private;
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

            public List<CommentListBean> getComment_list() {
                return comment_list;
            }

            public void setComment_list(List<CommentListBean> comment_list) {
                this.comment_list = comment_list;
            }

            public static class CommentListBean {
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
