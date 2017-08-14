package com.lyz.shuangshoudeliverer.ui.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.lyz.shuangshoudeliverer.ExitApplication;
import com.lyz.shuangshoudeliverer.R;
import com.lyz.shuangshoudeliverer.constant.StaticData;
import com.lyz.shuangshoudeliverer.httpapi.RetrofitHelper;
import com.lyz.shuangshoudeliverer.ui.adapter.CountDetailAdapter;
import com.lyz.shuangshoudeliverer.ui.base.BaseActivity;
import com.lyz.shuangshoudeliverer.ui.bean.PointBean;
import com.lyz.shuangshoudeliverer.ui.bean.UserDataBean;
import com.lyz.shuangshoudeliverer.utils.ToastUtils;
import com.lyz.shuangshoudeliverer.utils.customView.MyListView;
import com.lyz.shuangshoudeliverer.utils.customView.MyScrollView1;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.schedulers.Schedulers;

public class CountDetailActivity extends BaseActivity {

    @BindView(R.id.imgLeftTab)
    ImageView imgLeftTab;
    @BindView(R.id.tvTitleTab)
    TextView tvTitleTab;
    @BindView(R.id.tvRightTab)
    TextView tvRightTab;
    @BindView(R.id.listviewRecycleAddress)
    MyListView listviewRecycleAddress;
    @BindView(R.id.tvTotalPoints)
    TextView tvTotalPoints;
    @BindView(R.id.scrollViewCount)
    MyScrollView1 scrollViewCount;
    @BindView(R.id.swipeRefreshCount)
    SwipeRefreshLayout swipeRefreshCount;

    private ArrayList<PointBean> mDataList;
    private CountDetailAdapter mAdapter;

    private int mPage = 1;

    private PopupWindow mPopupWindow;
    private View mPopView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StaticData.setTranslucentStatus(this);
        setContentView(R.layout.activity_points_detail);
        ExitApplication.getInstance().addActivity(this);
        ButterKnife.bind(this);

        initView();

        getPointList(mPage);
        getTotalPoint();
    }


    private void initView() {
        tvTitleTab.setText("积分详情");
        tvRightTab.setVisibility(View.VISIBLE);
        tvRightTab.setText("提现");
        showTixianPop();

        mDataList = new ArrayList<>();


        mAdapter = new CountDetailAdapter(this, mDataList);
        listviewRecycleAddress.setAdapter(mAdapter);
        swipeRefreshCount.setOnRefreshListener(new myswipeLayout());

        scrollViewCount.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                swipeRefreshCount.setEnabled(scrollViewCount.getScrollY() == 0);
            }
        });

        scrollViewCount.setOnZdyScrollViewListener(new MyScrollView1.OnZdyScrollViewListener() {
            @Override
            public void ZdyScrollViewListener() {
                //上拉加载更多数据
                mPage++;
                ToastUtils.getInstance().showToast("加载更多");
                getPointList(mPage);
            }
        });
    }

    @OnClick({R.id.imgLeftTab, R.id.tvRightTab})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgLeftTab:
                finish();
                break;
            case R.id.tvRightTab:
                mPopupWindow.showAtLocation(tvTitleTab, Gravity.CENTER,0,0);

                break;
        }
    }

    public class myswipeLayout implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {

            swipeRefreshCount.setRefreshing(false);
            mDataList.clear();
            mPage = 1;
            getPointList(mPage);
            getTotalPoint();
            Toast.makeText(CountDetailActivity.this, "刷新完成", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 获取积分详情列表
     */
    private void getTotalPoint() {
        RetrofitHelper.getNetworkService()
                .getUserData()
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<UserDataBean>() {
                    @Override
                    public void onNext(UserDataBean value) {
                        tvTotalPoints.setText(String.valueOf(value.getPoint()));
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    /**
     * 获取积分详情列表
     */
    private void getPointList(int page) {
        RetrofitHelper.getNetworkService()
                .getPointList(page)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<ArrayList<PointBean>>() {
                    @Override
                    public void onNext(ArrayList<PointBean> value) {

                        scrollViewCount.loadingComponent(); //重置并发控制符
                        mDataList.addAll(value);
                        mAdapter.setMoreData(mDataList);

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }


    private void showTixianPop() {
        mPopView = getLayoutInflater().inflate(R.layout.pop_tixian, null);

        final TextView tvPopTixianCancle = (TextView) mPopView.findViewById(R.id.tvPopTixianCancle);

        LinearLayout linearPopSort = (LinearLayout) mPopView.findViewById(R.id.linearPopTixian);
        linearPopSort.setAlpha(1);

        mPopupWindow = new PopupWindow(mPopView, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setOutsideTouchable(true);

        ColorDrawable dw = new ColorDrawable(0xb0000000);
        mPopupWindow.setBackgroundDrawable(dw);

        mPopView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int y = (int) motionEvent.getY();
                int height = mPopView.findViewById(R.id.linearPopTixian).getTop();
                int bottom_h = mPopView.findViewById(R.id.linearPopTixian).getBottom();

                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height || y > bottom_h) {
                        mPopupWindow.dismiss();
                    }
                }
                return true;
            }
        });

        tvPopTixianCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPopupWindow.dismiss();
            }
        });


    }

}
