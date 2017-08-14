package com.lyz.shuangshouproject.utils.customView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;


public class MyScrollView1 extends ScrollView {

    private int flag = 0;    //并发控制标志位
    private OnZdyScrollViewListener onZdyScrollViewListener;

    public MyScrollView1(Context context) {
        super(context);
    }

    public MyScrollView1(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public MyScrollView1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    //listview加载完毕，将并发控制符置为0


    public void loadingComponent() {
        flag = 0;

    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        View view = this.getChildAt(0);
        //如果scrollview滑动到底部并且并发控制符为0，回调接口向服务器端请求数据
        if (this.getHeight() + this.getScrollY() == view.getHeight() && flag == 0) {
            flag = 1;//一进来就将并发控制符置为1，虽然onScrollChanged执行多次，但是由于并发控制符的值为1，不满足条件就不会执行到这
            onZdyScrollViewListener.ZdyScrollViewListener();

        }

    }


    public void setOnZdyScrollViewListener(OnZdyScrollViewListener onZdyScrollViewListener) {
        this.onZdyScrollViewListener = onZdyScrollViewListener;

    }


    public interface OnZdyScrollViewListener {

        public void ZdyScrollViewListener();

    }
}
