package com.lyz.shuangshoudeliverer.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.lyz.shuangshoudeliverer.ExitApplication;
import com.lyz.shuangshoudeliverer.R;
import com.lyz.shuangshoudeliverer.constant.StaticData;
import com.lyz.shuangshoudeliverer.httpapi.RetrofitHelper;
import com.lyz.shuangshoudeliverer.interfacePackage.OnItemClickListener;
import com.lyz.shuangshoudeliverer.ui.activity.EvaluationActivity;
import com.lyz.shuangshoudeliverer.ui.activity.OrderDetailActivity;
import com.lyz.shuangshoudeliverer.ui.adapter.OrderListHomeAdapter;
import com.lyz.shuangshoudeliverer.ui.base.BaseFragment;
import com.lyz.shuangshoudeliverer.ui.bean.OrderListBean;
import com.lyz.shuangshoudeliverer.ui.bean.UserAccountBean;
import com.lyz.shuangshoudeliverer.utils.ToastUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.schedulers.Schedulers;


/**
 * 待接单
 */
public class FragMySSFinished extends BaseFragment implements OnItemClickListener {


    @BindView(R.id.listviewOrderListHome)
    ListView listviewOrderListHome;
    @BindView(R.id.swipeRefreshHome)
    SwipeRefreshLayout swipeRefreshHome;

    private View view;

    private ArrayList<OrderListBean> mDataList;
    private OrderListHomeAdapter mAdapter;
    private String mState = "3";
    private int mPage = 1;

    private final int INTENT_FALG_TO_ORDER_DETAIL = 1;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
            return view;
        }
        view = inflater.inflate(R.layout.frag_home_order_list, null);
        ExitApplication.getInstance().addActivity(this.getActivity());

        ButterKnife.bind(this, view);

        ini();
        getOrderList(mState, mPage);
        return view;
    }


    public void ini() {
        mDataList = new ArrayList<>();

        swipeRefreshHome.setOnRefreshListener(new MyswipeLayout());
//        swipeRefreshLayout.setColorSchemeResources(R.color.colorTab);

        mAdapter = new OrderListHomeAdapter(FragMySSFinished.this.getActivity(), mDataList);
        listviewOrderListHome.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);

        listviewOrderListHome.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FragMySSFinished.this.getActivity(), OrderDetailActivity.class);
                intent.putExtra("orderId", mDataList.get(position).getId());
                intent.putExtra("orderState", mDataList.get(position).getState());
                startActivityForResult(intent, INTENT_FALG_TO_ORDER_DETAIL);
            }
        });

        loadMore();
    }

    @Override
    public void onClick(View view, int position) {
        if (StaticData.FLAG_USER) { //普通用户
            //待接单状态中的撤回按钮
            if (StaticData.ORDER_STATE_ORDERS.equals(String.valueOf(mDataList.get(position).getState()))) {
                deleteOrder(String.valueOf(mDataList.get(position).getId()));
            } else if (StaticData.ORDER_STATE_RECYCLED.equals(String.valueOf(mDataList.get(position).getState()))) {
                //已回收的状态需要用户评价
                Intent intent = new Intent(FragMySSFinished.this.getActivity(), EvaluationActivity.class);
                intent.putExtra("orderId", mDataList.get(position).getId());
                startActivityForResult(intent, INTENT_FALG_TO_ORDER_DETAIL);
            }


        } else {

            //快递员未接单 接单
            if (StaticData.ORDER_STATE_ORDERS.equals(String.valueOf(mDataList.get(position).getState()))) {
                //快递员接单
                takeOrder(String.valueOf(mDataList.get(position).getId()));
            } else if (StaticData.ORDER_STATE_ORDERED.equals(String.valueOf(mDataList.get(position).getState()))) {
                //快递员已接单  回收
                takeBackOrder(String.valueOf(mDataList.get(position).getId()));
            }

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INTENT_FALG_TO_ORDER_DETAIL && resultCode == Activity.RESULT_OK) {
            mDataList.clear();
            getOrderList(mState, 1);
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
        listviewOrderListHome.setOnScrollListener(new ListView.OnScrollListener() {
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

    /**
     * 快递员接单订单
     */
    private void takeOrder(String orderId) {
        RetrofitHelper.getNetworkService()
                .takeOrder(orderId)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<UserAccountBean>() {
                    @Override
                    public void onNext(UserAccountBean value) {

                        if (value.isSuccess()) {
                            mDataList.clear();
                            getOrderList(mState, 1);
                        } else {
                            ToastUtils.getInstance().showToast("接单失败");
                        }
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

    /**
     * 快递员回收订单
     */
    private void takeBackOrder(String orderId) {
        RetrofitHelper.getNetworkService()
                .takeBackOrder(orderId)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<UserAccountBean>() {
                    @Override
                    public void onNext(UserAccountBean value) {

                        if (value.isSuccess()) {
                            mDataList.clear();
                            getOrderList(mState, 1);
                        } else {
                            ToastUtils.getInstance().showToast("回收订单失败");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.getInstance().showToast("回收订单失败");
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

    /**
     * 撤回订单
     */
    private void deleteOrder(String orderId) {
        RetrofitHelper.getNetworkService()
                .deleteOrder(orderId)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<UserAccountBean>() {
                    @Override
                    public void onNext(UserAccountBean value) {

                        if (value.isSuccess()) {
                            mDataList.clear();
                            getOrderList(mState, 1);
                        } else {
                            ToastUtils.getInstance().showToast("撤回订单失败");
                        }
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


}
