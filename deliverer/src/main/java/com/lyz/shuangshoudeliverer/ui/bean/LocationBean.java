package com.lyz.shuangshoudeliverer.ui.bean;

/**
 * Created by Admin on 2017/7/24.
 *
 */

public class LocationBean {


    /**
     * lat : null
     * lng : null
     * id : 13
     * realname : 快递员
     */

    private double lat;
    private double lng;
    private int id;
    private String realname;




    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
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

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }
}
