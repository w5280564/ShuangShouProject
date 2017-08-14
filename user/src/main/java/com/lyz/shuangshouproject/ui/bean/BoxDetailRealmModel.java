package com.lyz.shuangshouproject.ui.bean;

import io.realm.RealmObject;

/**
 * Created by Admin on 2017/7/17.
 */

public class BoxDetailRealmModel extends RealmObject {

    /**
     * id：id
     * num:num
     */

    private int id;
    private int num;
    private String name;
    private int userId;  //区分纸箱是否属于同一个商家，不是同一个商家，不能再同一个订单中

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
