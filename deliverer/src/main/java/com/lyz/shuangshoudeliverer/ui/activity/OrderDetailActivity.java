package com.lyz.shuangshoudeliverer.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.lyz.shuangshoudeliverer.ExitApplication;
import com.lyz.shuangshoudeliverer.R;
import com.lyz.shuangshoudeliverer.constant.StaticData;
import com.lyz.shuangshoudeliverer.httpapi.RetrofitHelper;
import com.lyz.shuangshoudeliverer.ui.adapter.CartonOrderDetailAdapter;
import com.lyz.shuangshoudeliverer.ui.base.BaseActivity;
import com.lyz.shuangshoudeliverer.ui.bean.BoxlistBean;
import com.lyz.shuangshoudeliverer.ui.bean.OrderDetailBean;
import com.lyz.shuangshoudeliverer.ui.bean.UserAccountBean;
import com.lyz.shuangshoudeliverer.utils.ToastUtils;
import com.lyz.shuangshoudeliverer.utils.customView.MyListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.schedulers.Schedulers;

import static com.lyz.shuangshoudeliverer.constant.StaticData.ORDER_STATE_ORDERED;


public class OrderDetailActivity extends BaseActivity {

    @BindView(R.id.imgLeftTab)
    ImageView imgLeftTab;
    @BindView(R.id.tvTitleTab)
    TextView tvTitleTab;
    @BindView(R.id.imgRightTab)
    ImageView imgRightTab;
    @BindView(R.id.tvScheduleOrderDetail)
    TextView tvScheduleOrderDetail;
    @BindView(R.id.tvTimeOrderDetail)
    TextView tvTimeOrderDetail;
    @BindView(R.id.tvIssuerDetail)
    TextView tvIssuerDetail;
    @BindView(R.id.tvPhoneOrderDetail)
    TextView tvPhoneOrderDetail;
    @BindView(R.id.tvAddressOrderDetail)
    TextView tvAddressOrderDetail;
    @BindView(R.id.listviewOrderDetail)
    MyListView listviewOrderDetail;
    @BindView(R.id.tvCountOrderDetail)
    TextView tvCountOrderDetail;
    @BindView(R.id.tvRecyclePersonOrderDetail)
    TextView tvRecyclePersonOrderDetail;
    @BindView(R.id.imgPersonOrderDetail)
    SimpleDraweeView imgPersonOrderDetail;
    @BindView(R.id.tvNameOrderDetail)
    TextView tvNameOrderDetail;
    @BindView(R.id.tvRecyclePhoneOrderDetail)
    TextView tvRecyclePhoneOrderDetail;
    @BindView(R.id.tvCallPhoneOrderDetail)
    TextView tvCallPhoneOrderDetail;
    @BindView(R.id.linearRecyclePersonOrderDetail)
    LinearLayout linearRecyclePersonOrderDetail;
    @BindView(R.id.tvEvaluationOrderDetail)
    TextView tvEvaluationOrderDetail;
    @BindView(R.id.tvEvaluationContentOrderDetail)
    TextView tvEvaluationContentOrderDetail;
    @BindView(R.id.tvCallToUserOrderDetail)
    TextView tvCallToUserOrderDetail;

    @BindView(R.id.tvBottomBtnOrderDetail)
    TextView tvBottomBtnOrderDetail;
    private ArrayList<BoxlistBean> mDataList;
    private CartonOrderDetailAdapter mAdapter;


    private PopupWindow mPopupWindow;
    private View mPopView;

    private int mOrderId, mOrderState;
    private String mUserPhone, mUserName, mDisptacherPhone, mPublicName;

    private final int INTENT_FLAG_TO_EVALUATION = 1;

    private float index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StaticData.setTranslucentStatus(this);
        setContentView(R.layout.activity_order_detail);

        ButterKnife.bind(this);
        ExitApplication.getInstance().addActivity(this);

        initView();
    }

    private void initView() {

        index = getSharedPreferences("index", Context.MODE_PRIVATE).getFloat("index", 0);
        mOrderId = getIntent().getIntExtra("orderId", 0);
        mOrderState = getIntent().getIntExtra("orderState", 0);

        if (StaticData.FLAG_USER) {
            tvCallToUserOrderDetail.setVisibility(View.GONE);

        } else {
            tvCallToUserOrderDetail.setVisibility(View.VISIBLE);
        }


        mDataList = new ArrayList<>();
        mAdapter = new CartonOrderDetailAdapter(this, mDataList);
        listviewOrderDetail.setAdapter(mAdapter);

        getOrderDetail(mOrderId);

        listviewOrderDetail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(OrderDetailActivity.this, CartonDetailActivity.class);
                intent.putExtra("cartonId", mDataList.get(position).getBox());
                startActivity(intent);
            }
        });
    }

    @OnClick({R.id.imgLeftTab, R.id.tvCallPhoneOrderDetail, R.id.tvBottomBtnOrderDetail, R.id.tvCallToUserOrderDetail})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgLeftTab:
                finish();
                break;
            case R.id.tvCallPhoneOrderDetail:

                if (!TextUtils.isEmpty(mDisptacherPhone)) {
                    showCallPop(mDisptacherPhone, mUserName);
                }

                break;
            case R.id.tvBottomBtnOrderDetail:
                if (StaticData.ORDER_STATE_ORDERS.equals(String.valueOf(mOrderState))) {  //待接单

                    if (StaticData.FLAG_USER) { //待接单状态下，用户可以撤销订单
                        deleteOrder(String.valueOf(mOrderId));
                    } else { //快递员接单
                        takeOrder(String.valueOf(mOrderId));
                    }

                } else if (StaticData.ORDER_STATE_RECYCLED.equals(String.valueOf(mOrderState))) {//已回收待评价
                    if (StaticData.FLAG_USER) { //已回收待评价
                        Intent intent = new Intent(this, EvaluationActivity.class);
                        intent.putExtra("orderId", mOrderId);
                        intent.putExtra("content", tvEvaluationContentOrderDetail.getText().toString());
                        startActivityForResult(intent, INTENT_FLAG_TO_EVALUATION);
                    }
                } else if (StaticData.ORDER_STATE_ORDERED.equals(String.valueOf(mOrderState))) { //已接单
                    if (!StaticData.FLAG_USER) { //已接单状态下，快递员回收
                        takeBackOrder(String.valueOf(mOrderId));
                    }
                }

                break;
            case R.id.tvCallToUserOrderDetail:
                if (!TextUtils.isEmpty(mUserPhone)) {
                    showCallPop(mUserPhone, mPublicName);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INTENT_FLAG_TO_EVALUATION && resultCode == Activity.RESULT_OK) {
            mDataList.clear();
            getOrderDetail(mOrderId);
        }
    }

    /**
     * 获取订单详情
     */
    private void getOrderDetail(int orderId) {
        RetrofitHelper.getNetworkService()
                .getOrderDetail(String.valueOf(orderId))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<OrderDetailBean>() {
                    @Override
                    public void onNext(OrderDetailBean value) {
                        tvTitleTab.setText("订单" + value.getCode());

                        tvTimeOrderDetail.setText("发布时间：" + StaticData.GTMToLocal(value.getPubDate()));
                        tvIssuerDetail.setText("发布人：" + value.getContact());
                        tvPhoneOrderDetail.setText("联系电话：" + value.getPhone());
                        tvAddressOrderDetail.setText("回收地址：" + value.getAddress());

                        mPublicName = value.getContact();
                        mUserPhone = value.getPhone();
                        mDataList.addAll(value.getBoxlist());
                        mAdapter.setMoreData(mDataList);
                        int score = 0;
                        List<BoxlistBean> boxlist = value.getBoxlist();

                        for (BoxlistBean boxlistBean : boxlist) {
                            if (boxlistBean.getBox__point() != 0 && boxlistBean.getNum() != 0) {
                                score = score + boxlistBean.getBox__point() * boxlistBean.getNum();
                            }
                        }
                        tvCountOrderDetail.setText(String.valueOf(score));
                        if (StaticData.ORDER_STATE_ORDERS.equals(String.valueOf(mOrderState))) {  //待接单
                            if (StaticData.FLAG_USER) {
                                tvScheduleOrderDetail.setText("状态：待接单");
                                tvBottomBtnOrderDetail.setText("撤回订单");
                            } else {
                                tvScheduleOrderDetail.setText("状态：未接单");
                                tvBottomBtnOrderDetail.setText("接单");
                            }

                            tvBottomBtnOrderDetail.setVisibility(View.VISIBLE);
                            tvRecyclePersonOrderDetail.setVisibility(View.GONE);
                            linearRecyclePersonOrderDetail.setVisibility(View.GONE);
                            tvEvaluationOrderDetail.setVisibility(View.GONE);
                            tvEvaluationContentOrderDetail.setVisibility(View.GONE);

                        } else if (ORDER_STATE_ORDERED.equals(String.valueOf(mOrderState))) {  //已接单

                            if (StaticData.FLAG_USER) {
                                tvScheduleOrderDetail.setText("状态：已接单");
                                tvBottomBtnOrderDetail.setVisibility(View.GONE);
                            } else {
                                tvScheduleOrderDetail.setText("状态：已接单");
                                tvBottomBtnOrderDetail.setText("到达并开始回收");
                                tvBottomBtnOrderDetail.setVisibility(View.VISIBLE);
                            }

                            tvRecyclePersonOrderDetail.setVisibility(View.VISIBLE);
                            linearRecyclePersonOrderDetail.setVisibility(View.VISIBLE);
                            tvEvaluationOrderDetail.setVisibility(View.GONE);
                            tvEvaluationContentOrderDetail.setVisibility(View.GONE);

                            imgPersonOrderDetail.setController(StaticData.loadFrescoImg(Uri.parse(RetrofitHelper.ImgUrl + value.getRecycleman__img()),
                                    index, 60, 60, imgPersonOrderDetail));
                            tvNameOrderDetail.setText("姓名：" + value.getRecycleman__realname());
                            tvRecyclePhoneOrderDetail.setText("联系电话：" + value.getRecycleman__phone());
                            mDisptacherPhone = value.getRecycleman__phone();
                            mUserName = value.getRecycleman__realname();

                        } else if (StaticData.ORDER_STATE_RECYCLED.equals(String.valueOf(mOrderState))) {//已回收

                            if (StaticData.FLAG_USER) {
                                tvBottomBtnOrderDetail.setText("评价");
                                tvBottomBtnOrderDetail.setVisibility(View.VISIBLE);
                            } else {
                                tvBottomBtnOrderDetail.setVisibility(View.GONE);
                            }

                            tvScheduleOrderDetail.setText("状态：已回收");

                            tvRecyclePersonOrderDetail.setVisibility(View.VISIBLE);
                            linearRecyclePersonOrderDetail.setVisibility(View.VISIBLE);

                            if (!TextUtils.isEmpty(value.getContent())) {
                                tvEvaluationOrderDetail.setVisibility(View.VISIBLE);
                                tvEvaluationContentOrderDetail.setVisibility(View.VISIBLE);
                                tvEvaluationContentOrderDetail.setText(value.getContent());

                            } else {
                                tvEvaluationOrderDetail.setVisibility(View.GONE);
                                tvEvaluationContentOrderDetail.setVisibility(View.GONE);

                            }


                            imgPersonOrderDetail.setController(StaticData.loadFrescoImg(Uri.parse(RetrofitHelper.ImgUrl + value.getRecycleman__img()),
                                    index, 60, 60, imgPersonOrderDetail));
                            tvNameOrderDetail.setText("姓名：" + value.getRecycleman__realname());
                            tvRecyclePhoneOrderDetail.setText("联系电话：" + value.getRecycleman__phone());
                            mDisptacherPhone = value.getRecycleman__phone();
                            mUserName = value.getRecycleman__realname();
                        } else if (StaticData.ORDER_STATE_FINISHED.equals(String.valueOf(mOrderState))) {//已完成
                            tvScheduleOrderDetail.setText("状态：已完成");
                            tvBottomBtnOrderDetail.setVisibility(View.GONE);
                            tvRecyclePersonOrderDetail.setVisibility(View.VISIBLE);
                            linearRecyclePersonOrderDetail.setVisibility(View.VISIBLE);
                            tvEvaluationOrderDetail.setVisibility(View.VISIBLE);
                            tvEvaluationContentOrderDetail.setVisibility(View.VISIBLE);

                            imgPersonOrderDetail.setController(StaticData.loadFrescoImg(Uri.parse(RetrofitHelper.ImgUrl + value.getRecycleman__img()),
                                    index, 60, 60, imgPersonOrderDetail));
                            tvNameOrderDetail.setText("姓名：" + value.getRecycleman__realname());
                            tvRecyclePhoneOrderDetail.setText("联系电话：" + value.getRecycleman__phone());
                            mDisptacherPhone = value.getRecycleman__phone();
                            mUserName = value.getRecycleman__realname();
                            tvEvaluationContentOrderDetail.setText(value.getContent());


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
                            setResult(Activity.RESULT_OK);
                            finish();
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
                            setResult(Activity.RESULT_OK);
                            finish();
                        } else {
                            ToastUtils.getInstance().showToast("接单失败");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.getInstance().showToast("接单失败");
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
     * 快递员接单订单 takeBackOrder
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
                            setResult(Activity.RESULT_OK);
                            finish();
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

    private void showCallPop(final String phone, String name) {
        mPopView = getLayoutInflater().inflate(R.layout.pop_call_phone, null);
        LinearLayout linearPopCall = (LinearLayout) mPopView.findViewById(R.id.linearPopCall);
        final TextView tvPhone = (TextView) mPopView.findViewById(R.id.tvPhoneNumPerson);
        TextView tvCallPerson = (TextView) mPopView.findViewById(R.id.tvCallPerson);
        TextView tvPoneCallPerson = (TextView) mPopView.findViewById(R.id.tvPoneCallPerson);
        TextView tvPopCallName = (TextView) mPopView.findViewById(R.id.tvPopCallName);

        linearPopCall.setAlpha(1);
        tvPhone.setText(phone);
        tvPopCallName.setText(name);
        mPopupWindow = new PopupWindow(mPopView, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setOutsideTouchable(true);
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        mPopupWindow.setBackgroundDrawable(dw);

        mPopView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int y = (int) motionEvent.getY();
                int height = mPopView.findViewById(R.id.linearPopCall).getTop();
                int bottom_h = mPopView.findViewById(R.id.linearPopCall).getBottom();

                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height || y > bottom_h) {
                        mPopupWindow.dismiss();
                    }
                }
                return true;
            }
        });

        mPopupWindow.showAtLocation(tvTitleTab, Gravity.CENTER, 0, 0);

        tvCallPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                startActivity(intent);

            }
        });
        tvPoneCallPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });
    }
}
