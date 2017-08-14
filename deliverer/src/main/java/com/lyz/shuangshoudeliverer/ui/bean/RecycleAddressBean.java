package com.lyz.shuangshoudeliverer.ui.bean;

import java.io.Serializable;

/**
 * Created by Admin on 2017/7/17.
 */

public class RecycleAddressBean implements Serializable {
    /**
     *[{"area":"上海","is_default":false,"phone":"13052107931","contact":"测试","address":"杨浦区国顺东路800号西楼","id":1}]

     * area : 上海
     * is_default : false
     * phone : 13052107931
     * contact : 测试
     * address : 杨浦区国顺东路800号西楼
     * id : 1
     */

    private String area;
    private boolean is_default;
    private String phone;
    private String contact;
    private String address;
    private int id;

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public boolean isIs_default() {
        return is_default;
    }

    public void setIs_default(boolean is_default) {
        this.is_default = is_default;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


}
