package com.lyz.shuangshouproject.ui.bean;

import java.util.List;

/**
 * Created by Admin on 2017/7/11.
 */

public class OrderListBean {

    /**
     * content : 测试测试测试
     * code : SS123456678
     * pubDate : 2017-07-14T17:32:31.269464
     * boxlist : [{"box":1,"num":3,"box__name":"11","box__point":0},{"box":2,"num":1,"box__name":"测试纸箱1","box__point":0}]
     * recycleDate : 2017-07-14T17:08:04
     * state : 0
     * judgeDate : 2017-07-10T12:00:00
     * id : 1
     * <p>
     * 订单列表：
     * [{订单ID：id
     * 订单号：code（由SS+8位递增数字组成，如SS00000001）
     * 订单状态：state（0：待接单，1：已接单，2：已回收，3：已完成，4：已撤回）
     * 发布时间：pubDate
     * 回收时间：recycleDate
     * 纸箱列表：boxlist[{纸箱ID：box，纸箱规格名称：box__name，数量：num，每个纸箱积分：box__point}]
     * 评价：content
     * 评价时间：judgedate
     * }]
     */

    private String content;
    private String code;
    private String pubDate;
    private String recycleDate;
    private int state;
    private String judgeDate;
    private int id;
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

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getRecycleDate() {
        return recycleDate;
    }

    public void setRecycleDate(String recycleDate) {
        this.recycleDate = recycleDate;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getJudgeDate() {
        return judgeDate;
    }

    public void setJudgeDate(String judgeDate) {
        this.judgeDate = judgeDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<BoxlistBean> getBoxlist() {
        return boxlist;
    }

    public void setBoxlist(List<BoxlistBean> boxlist) {
        this.boxlist = boxlist;
    }


}
