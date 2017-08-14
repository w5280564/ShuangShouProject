package com.lyz.shuangshouproject.ui.bean;

/**
 * Created by Admin on 2017/7/17.
 */

public class BoxDetailBean {

    /**
     * id：id
     * 名称：name
     * 尺寸：size
     * 材质：quality
     * 积分：point
     * 产地：area
     * 所属商家名称：user__realname
     * name : 11
     * point : 0
     * area :
     * user__realname : null
     * quality :
     * id : 1
     * size :
     */

    private String name;
    private int point;



    private int user;
    private String area;
    private Object user__realname;
    private String quality;
    private int id;
    private String size;
    private boolean flagIsAdd;

    public boolean isFlagIsAdd() {
        return flagIsAdd;
    }

    public void setFlagIsAdd(boolean flagIsAdd) {
        this.flagIsAdd = flagIsAdd;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    private int num;

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Object getUser__realname() {
        return user__realname;
    }

    public void setUser__realname(Object user__realname) {
        this.user__realname = user__realname;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
