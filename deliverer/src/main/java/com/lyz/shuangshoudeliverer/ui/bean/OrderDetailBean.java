package com.lyz.shuangshoudeliverer.ui.bean;

import java.util.List;

/**
 * Created by Admin on 2017/7/17.
 */

public class OrderDetailBean {

    /**
     * content : 测试测试测试
     * boxlist : [{"box":1,"num":3,"box__name":"11","box__point":0},{"box":2,"num":1,"box__name":"测试纸箱1","box__point":0}]
     * code : SS123456678
     * recycleman__img :
     * pubDate : 2017-07-14T17:32:31.269464
     * recycleman : 4
     * lat : 31.299427
     * recycleDate : 2017-07-14T17:08:04
     * phone : 13052107931
     * state : 0
     * contact : android
     * recycleman__phone :
     * address : 国顺东路
     * judgeDate : 2017-07-10T12:00:00
     * lng : 121.531049
     * id : 1
     * recycleman__realname :
     */

    private String content;
    private String code;
    private String recycleman__img;
    private String pubDate;
    private int recycleman;
    private double lat;
    private String recycleDate;
    private String phone;
    private int state;
    private String contact;
    private String recycleman__phone;
    private String address;
    private String judgeDate;
    private double lng;
    private int id;
    private String recycleman__realname;
    private List<BoxlistBean> boxlist;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRecycleman__img() {
        return recycleman__img;
    }

    public void setRecycleman__img(String recycleman__img) {
        this.recycleman__img = recycleman__img;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public int getRecycleman() {
        return recycleman;
    }

    public void setRecycleman(int recycleman) {
        this.recycleman = recycleman;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getRecycleDate() {
        return recycleDate;
    }

    public void setRecycleDate(String recycleDate) {
        this.recycleDate = recycleDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getRecycleman__phone() {
        return recycleman__phone;
    }

    public void setRecycleman__phone(String recycleman__phone) {
        this.recycleman__phone = recycleman__phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getJudgeDate() {
        return judgeDate;
    }

    public void setJudgeDate(String judgeDate) {
        this.judgeDate = judgeDate;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRecycleman__realname() {
        return recycleman__realname;
    }

    public void setRecycleman__realname(String recycleman__realname) {
        this.recycleman__realname = recycleman__realname;
    }

    public List<BoxlistBean> getBoxlist() {
        return boxlist;
    }

    public void setBoxlist(List<BoxlistBean> boxlist) {
        this.boxlist = boxlist;
    }


}
