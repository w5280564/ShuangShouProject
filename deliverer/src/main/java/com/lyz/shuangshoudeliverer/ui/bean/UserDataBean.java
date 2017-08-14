package com.lyz.shuangshoudeliverer.ui.bean;

/**
 * Created by Admin on 2017/7/14.
 */

public class UserDataBean {
    /**
     * note : 测试版本号被盗
     * phone : 15455484487644
     * realname : 测试
     * img : user/1498222524394_06092d51.jpg
     * sex : 1
     */

    private String note;
    private String phone;
    private String realname;
    private String img;
    private int sex;
    private int point;

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }



}
