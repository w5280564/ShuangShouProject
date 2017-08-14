package com.lyz.shuangshouproject.ui.bean;

/**
 * Created by Admin on 2017/7/14.
 *
 */

public class UserAccountBean {
    /**
     * err_msg : empty phone or password
     * success : false
     * err_code : 1003
     */

    private String err_msg;
    private boolean success;
    private int err_code;
    private int type;


    public String getErr_msg() {
        return err_msg;
    }

    public void setErr_msg(String err_msg) {
        this.err_msg = err_msg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getErr_code() {
        return err_code;
    }

    public void setErr_code(int err_code) {
        this.err_code = err_code;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
