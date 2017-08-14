package com.lyz.shuangshoudeliverer.ui.bean;

import java.util.List;

/**
 * Created by Admin on 2017/7/17.
 */

public class PointBean {


    /**
     * boxlist : [{"box":2,"num":4,"box__name":"测试纸箱1"},{"box":1,"num":6,"box__name":"11"}]
     * order : 2
     * id : 2
     * pubDate : 2017-07-17T17:35:18.450326
     * point : 100
     */

    private int order;
    private int id;
    private String pubDate;
    private int point;
    private List<BoxlistBean> boxlist;

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public List<BoxlistBean> getBoxlist() {
        return boxlist;
    }

    public void setBoxlist(List<BoxlistBean> boxlist) {
        this.boxlist = boxlist;
    }

    public static class BoxlistBean {
        /**
         * box : 2
         * num : 4
         * box__name : 测试纸箱1
         */

        private int box;
        private int num;
        private String box__name;

        public int getBox() {
            return box;
        }

        public void setBox(int box) {
            this.box = box;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public String getBox__name() {
            return box__name;
        }

        public void setBox__name(String box__name) {
            this.box__name = box__name;
        }
    }
}
