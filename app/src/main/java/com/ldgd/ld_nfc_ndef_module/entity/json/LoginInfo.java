package com.ldgd.ld_nfc_ndef_module.entity.json;

import java.util.List;

/**
 * Created by ldgd on 2021/3/29.
 */

public class LoginInfo {


    /**
     * errno : 0
     * errmsg :
     * data : {"token":{"username":"cy","token":"073b8780-9074-11eb-b6f2-651b6d2f4216","expired":1617097757688},"userProfile":{"_id":33,"username":"cy","phone":"","fullname":"chenyong","roles":"[\"控制\"]","openid":"oa89fv9pPkNdTKw5XKqcR6KOwcMg"},"grantedActions":["device/view","project/view","v_device_ebox/view","v_device_lamp/view","v_device_wiresafe/view","ticket/view","ticket/edit","ticket/new","device/edit","project/edit","v_device_ebox/edit","v_device_lamp/edit","v_device_wiresafe/edit","v_device_wiresafe/control","v_device_lamp/control","v_device_ebox/control","v_device_warning/close","v_device_warning/delete"]}
     */
    private int errno;
    private String errmsg;
    private DataBean data;

    public int getErrno() {
        return errno;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * token : {"username":"cy","token":"073b8780-9074-11eb-b6f2-651b6d2f4216","expired":1617097757688}
         * userProfile : {"_id":33,"username":"cy","phone":"","fullname":"chenyong","roles":"[\"控制\"]","openid":"oa89fv9pPkNdTKw5XKqcR6KOwcMg"}
         * grantedActions : ["device/view","project/view","v_device_ebox/view","v_device_lamp/view","v_device_wiresafe/view","ticket/view","ticket/edit","ticket/new","device/edit","project/edit","v_device_ebox/edit","v_device_lamp/edit","v_device_wiresafe/edit","v_device_wiresafe/control","v_device_lamp/control","v_device_ebox/control","v_device_warning/close","v_device_warning/delete"]
         */

        private TokenBean token;
        private UserProfileBean userProfile;
        private List<String> grantedActions;

        public TokenBean getToken() {
            return token;
        }

        public void setToken(TokenBean token) {
            this.token = token;
        }

        public UserProfileBean getUserProfile() {
            return userProfile;
        }

        public void setUserProfile(UserProfileBean userProfile) {
            this.userProfile = userProfile;
        }

        public List<String> getGrantedActions() {
            return grantedActions;
        }

        public void setGrantedActions(List<String> grantedActions) {
            this.grantedActions = grantedActions;
        }

        public static class TokenBean {
            /**
             * username : cy
             * token : 073b8780-9074-11eb-b6f2-651b6d2f4216
             * expired : 1617097757688
             */

            private String username;
            private String token;
            private long expired;

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public String getToken() {
                return token;
            }

            public void setToken(String token) {
                this.token = token;
            }

            public long getExpired() {
                return expired;
            }

            public void setExpired(long expired) {
                this.expired = expired;
            }
        }

        public static class UserProfileBean {
            /**
             * _id : 33
             * username : cy
             * phone :
             * fullname : chenyong
             * roles : ["控制"]
             * openid : oa89fv9pPkNdTKw5XKqcR6KOwcMg
             */

            private int _id;
            private String username;
            private String phone;
            private String fullname;
            private String roles;
            private String openid;

            public int get_id() {
                return _id;
            }

            public void set_id(int _id) {
                this._id = _id;
            }

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public String getFullname() {
                return fullname;
            }

            public void setFullname(String fullname) {
                this.fullname = fullname;
            }

            public String getRoles() {
                return roles;
            }

            public void setRoles(String roles) {
                this.roles = roles;
            }

            public String getOpenid() {
                return openid;
            }

            public void setOpenid(String openid) {
                this.openid = openid;
            }
        }
    }
}
