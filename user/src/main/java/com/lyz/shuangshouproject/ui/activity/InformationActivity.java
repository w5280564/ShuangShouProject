package com.lyz.shuangshouproject.ui.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.lyz.shuangshouproject.ExitApplication;
import com.lyz.shuangshouproject.R;
import com.lyz.shuangshouproject.constant.StaticData;
import com.lyz.shuangshouproject.httpapi.RetrofitHelper;
import com.lyz.shuangshouproject.ui.adapter.InforAdapter;
import com.lyz.shuangshouproject.ui.base.BaseActivity;
import com.lyz.shuangshouproject.ui.bean.InforBean;
import com.lyz.shuangshouproject.utils.ToastUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.schedulers.Schedulers;

public class InformationActivity extends BaseActivity {

    @BindView(R.id.imgLeftTab)
    ImageView imgLeftTab;
    @BindView(R.id.tvTitleTab)
    TextView tvTitleTab;
    @BindView(R.id.listviewRecycleAddress)
    ListView listviewRecycleAddress;
    @BindView(R.id.swipeRefreshHome)
    SwipeRefreshLayout swipeRefreshHome;

    private ArrayList<InforBean> mDataList;
    private InforAdapter mAdapter;
    private int mPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StaticData.setTranslucentStatus(this);
        setContentView(R.layout.activity_evaluation_system);
        ExitApplication.getInstance().addActivity(this);
        ButterKnife.bind(this);

        initView();
        getMessageList(mPage);
    }

    private void initView(){
        tvTitleTab.setText("消息通知");

        mDataList = new ArrayList<>();

        mAdapter = new InforAdapter(this,mDataList);
        listviewRecycleAddress.setAdapter(mAdapter);

        swipeRefreshHome.setOnRefreshListener(new MyswipeLayout());
        loadMore();
    }

    private class MyswipeLayout implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            mDataList.clear();
            mPage = 1;
//            getOrderList( mPage);
            swipeRefreshHome.setRefreshing(false);
            ToastUtils.getInstance().showToast("刷新完成");
        }
    }

    private void loadMore() {
        listviewRecycleAddress.setOnScrollListener(new ListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    // 判断是否滚动到底部
                    if (view.getLastVisiblePosition() == view.getCount() - 1 && mDataList.size() >= 20) {
                        mPage++;
                        //加载更多功能的代码
//                        getOrderList( mPage);
                        ToastUtils.getInstance().showToast("加载更多");
                        swipeRefreshHome.setRefreshing(false);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0) {

                }
            }
        });
    }

    /**
     * 获取全部订单列表
     */
    private void getMessageList(int page) {
        RetrofitHelper.getNetworkService()
                .getMessageList(page)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<ArrayList<InforBean>>() {
                    @Override
                    public void onNext(ArrayList<InforBean> value) {

                        mDataList.addAll(value);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof HttpException) { // 网络错误
                            HttpException httpEx = (HttpException) e;
                            String responseMsg = httpEx.getMessage();
                            Log.e("----TAG网络错误", "" + responseMsg);
                            // ...
                        } else { // 其他错误
                            Log.e("----TAG其他错误", "" + e.getMessage());
                            // ...
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }


    @OnClick(R.id.imgLeftTab)
    public void onClick() {
        finish();
    }
}
