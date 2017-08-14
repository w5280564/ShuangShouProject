package com.lyz.shuangshoudeliverer.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.lyz.shuangshoudeliverer.ExitApplication;
import com.lyz.shuangshoudeliverer.R;
import com.lyz.shuangshoudeliverer.constant.StaticData;
import com.lyz.shuangshoudeliverer.httpapi.RetrofitHelper;
import com.lyz.shuangshoudeliverer.interfacePackage.OnItemClickListener;
import com.lyz.shuangshoudeliverer.ui.adapter.EvaluationSystermAdapter;
import com.lyz.shuangshoudeliverer.ui.base.BaseActivity;
import com.lyz.shuangshoudeliverer.ui.bean.OrderListBean;
import com.lyz.shuangshoudeliverer.ui.bean.UserAccountBean;
import com.lyz.shuangshoudeliverer.utils.ToastUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.schedulers.Schedulers;

public class EvaluationSystermActivity extends BaseActivity implements OnItemClickListener {

    @BindView(R.id.imgLeftTab)
    ImageView imgLeftTab;
    @BindView(R.id.tvTitleTab)
    TextView tvTitleTab;
    @BindView(R.id.listviewRecycleAddress)
    ListView listviewRecycleAddress;

    @BindView(R.id.swipeRefreshHome)
    SwipeRefreshLayout swipeRefreshHome;

    private String mState = "2,3";
    private int mPage = 1;

    private final int INTENT_FALG_TO_ORDER_DETAIL = 1;

    private ArrayList<OrderListBean> mDataList;
    private EvaluationSystermAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StaticData.setTranslucentStatus(this);
        setContentView(R.layout.activity_evaluation_system);
        ExitApplication.getInstance().addActivity(this);
        ButterKnife.bind(this);

        initView();
        getOrderList(mState, mPage);
    }

    private void initView() {
        tvTitleTab.setText("评论系统");
        swipeRefreshHome.setOnRefreshListener(new MyswipeLayout());

        mDataList = new ArrayList<>();

        mAdapter = new EvaluationSystermAdapter(this, mDataList);
        listviewRecycleAddress.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        loadMore();

    }

    @OnClick(R.id.imgLeftTab)
    public void onClick() {
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INTENT_FALG_TO_ORDER_DETAIL && resultCode == Activity.RESULT_OK) {
            mDataList.clear();
            getOrderList(mState, 1);
        }
    }

    @Override
    public void onClick(View view, int position) {
        switch (view.getId()) {
            case R.id.tvDetailSystemItem:
                Intent intent = new Intent(EvaluationSystermActivity.this, OrderDetailActivity.class);
                intent.putExtra("orderId", mDataList.get(position).getId());
                intent.putExtra("orderState", mDataList.get(position).getState());
                startActivityForResult(intent, INTENT_FALG_TO_ORDER_DETAIL);
                break;
            case R.id.tvSubmitEvaluationSystemItem:
                commentOrder(mDataList.get(position).getId(), view.getTag().toString());
                break;
        }
    }


    private class MyswipeLayout implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            mDataList.clear();
            mPage = 1;
            getOrderList(mState, mPage);
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
                        getOrderList(mState, mPage);
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
    private void getOrderList(String state, int page) {
        RetrofitHelper.getNetworkService()
                .getOrderList(state, page)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<ArrayList<OrderListBean>>() {
                    @Override
                    public void onNext(ArrayList<OrderListBean> value) {
                        mDataList.addAll(value);
                        mAdapter.setMoreData(mDataList);

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

    private void commentOrder(int orderId, String content) {
        RetrofitHelper.getNetworkService()
                .commentOrder(orderId, content)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<UserAccountBean>() {
                    @Override
                    public void onNext(UserAccountBean value) {
                        if (value.isSuccess()) {
                            ToastUtils.getInstance().showToast("评价成功");

                        } else {
                            ToastUtils.getInstance().showToast("评价失败");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.getInstance().showToast("评价失败");
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
}
