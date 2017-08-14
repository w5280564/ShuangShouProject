package com.lyz.shuangshouproject.interfacePackage;

import android.view.View;

/**
 * Created by Admin on 2017/7/13.
 *
 * listview中item的点击事件监听
 * 附带值的
 *
 */

public interface OnItemClickValueListener {

    void onClick(View view, int position,String value);

}
