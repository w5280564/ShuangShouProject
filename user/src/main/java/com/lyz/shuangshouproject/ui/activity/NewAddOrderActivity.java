package com.lyz.shuangshouproject.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.google.gson.Gson;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.lyz.shuangshouproject.ExitApplication;
import com.lyz.shuangshouproject.R;
import com.lyz.shuangshouproject.constant.StaticData;
import com.lyz.shuangshouproject.httpapi.RetrofitHelper;
import com.lyz.shuangshouproject.ui.adapter.NewAddOrderAdapter;
import com.lyz.shuangshouproject.ui.base.BaseActivity;
import com.lyz.shuangshouproject.ui.bean.BoxDetailBean;
import com.lyz.shuangshouproject.ui.bean.BoxDetailRealmModel;
import com.lyz.shuangshouproject.ui.bean.BoxUploadBean;
import com.lyz.shuangshouproject.ui.bean.RecycleAddressBean;
import com.lyz.shuangshouproject.ui.bean.UserAccountBean;
import com.lyz.shuangshouproject.utils.MyRealmUtils;
import com.lyz.shuangshouproject.utils.ShareSaveUtils;
import com.lyz.shuangshouproject.utils.ToastUtils;
import com.lyz.shuangshouproject.utils.customView.MyListView;
import com.lyz.shuangshouproject.utils.scan.core.CaptureActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmResults;

/**
 * 新增订单
 * <p>
 * 根据扫描出的id，获取此二维码对应的纸箱信息
 */
public class NewAddOrderActivity extends BaseActivity implements OnGetGeoCoderResultListener {


    @BindView(R.id.imgLeftTab)
    ImageView imgLeftTab;
    @BindView(R.id.tvTitleTab)
    TextView tvTitleTab;
    @BindView(R.id.listviewNewOrder)
    MyListView listviewNewOrder;
    @BindView(R.id.tvAddCartonNewOrder)
    TextView tvAddCartonNewOrder;
    @BindView(R.id.tvPersonNewOrder)
    TextView tvPersonNewOrder;
    @BindView(R.id.tvChangeAddressNewOrder)
    TextView tvChangeAddressNewOrder;
    @BindView(R.id.tvPhoneNewOrder)
    TextView tvPhoneNewOrder;
    @BindView(R.id.tvAddressNewOrder)
    TextView tvAddressNewOrder;
    @BindView(R.id.tvPublicNewOrder)
    TextView tvPublicNewOrder;
    @BindView(R.id.tvAddAddressNewOrder)
    TextView tvAddAddressNewOrder;
    @BindView(R.id.linearAddressDetail)
    LinearLayout linearAddressDetail;
    private ArrayList<BoxDetailBean> mDataList;
    private NewAddOrderAdapter mAdapter;


    private String mId;
    private String mContact, mPhone, mAddress, mBoxeslist, mLng, mLat;
    private String mCity;
    private boolean flagChooseAddress = false;
    private boolean flagSearchLatAndLng;

    private GeoCoder mSearch = null; // 搜索模块，

    private Realm mRealm;
    private RealmAsyncTask transaction;
    private RealmResults<BoxDetailRealmModel> mRealmResults;

    private PopupWindow mPopupWindow;
    private View mPopView;

    private PopupWindow mPopupWindowRemind;
    private View mPopViewRemind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StaticData.setTranslucentStatus(this);
        setContentView(R.layout.activity_new_order);

        ButterKnife.bind(this);
        ExitApplication.getInstance().addActivity(this);
        mId = getIntent().getStringExtra("cartonId");
        initView();
        getCartonDetailFormId(mId);
    }


    private void initView() {
        tvTitleTab.setText("订单信息");

        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);

        mRealm = MyRealmUtils.getRealmCartonDetail(this);
        mRealmResults = mRealm.where(BoxDetailRealmModel.class).findAll();

        mDataList = new ArrayList<>();

        if (mRealmResults.size() != 0) {
            for (BoxDetailRealmModel model : mRealmResults) {
                BoxDetailBean bean = new BoxDetailBean();
                bean.setNum(model.getNum());
                bean.setName(model.getName());
                bean.setId(model.getId());

                mDataList.add(bean);
            }
        }


        mAdapter = new NewAddOrderAdapter(this, mDataList);
        listviewNewOrder.setAdapter(mAdapter);

        showSearchPop();
        showRemindPop();
        try {
            ArrayList<String> addressList = ShareSaveUtils.getlocList(this, "Address");
            if (addressList != null && addressList.size() != 0) {
                tvAddAddressNewOrder.setVisibility(View.GONE);
                linearAddressDetail.setVisibility(View.VISIBLE);
                flagChooseAddress = true;

                mContact = addressList.get(0);
                mPhone = addressList.get(1);
                mAddress = addressList.get(2);
                tvPersonNewOrder.setText("联系人：" + mContact);
                tvPhoneNewOrder.setText("联系电话：" + mPhone);
                tvAddressNewOrder.setText("回收地址：" + mAddress);

                mCity = addressList.get(3);

//                if (addressList.size() == 4 && !TextUtils.isEmpty(addressList.get(3))) {
//                    String area = addressList.get(3);
//                    String city_array[] = area.split("#");
//                    if (city_array.length == 1 || city_array.length == 2) {
//                        mCity = city_array[city_array.length - 1];
//                    } else {
//                        if (city_array.length != 0)
//                            mCity = city_array[city_array.length - 1];
//                    }
//                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @OnClick({R.id.imgLeftTab, R.id.tvAddCartonNewOrder, R.id.tvChangeAddressNewOrder, R.id.tvPublicNewOrder, R.id.tvAddAddressNewOrder})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgLeftTab:
                mPopupWindow.showAtLocation(tvTitleTab, Gravity.CENTER, 0, 0);

                break;
            case R.id.tvAddCartonNewOrder:
                Intent intent = new Intent(this, CaptureActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.tvChangeAddressNewOrder:
                Intent intent2 = new Intent(this, RecycleAddressActivity.class);
                startActivityForResult(intent2, 1);
                break;

            case R.id.tvPublicNewOrder:

                if (!flagChooseAddress) {
                    ToastUtils.getInstance().showToast("请选择回收地址");
                    return;
                }

                if (TextUtils.isEmpty(mContact)) {
                    ToastUtils.getInstance().showToast("回收地址中联系人为空，请更换回收地址");
                    return;
                }

                if (TextUtils.isEmpty(mPhone)) {
                    ToastUtils.getInstance().showToast("回收地址中联系电话为空，请更换回收地址");
                    return;
                }

                if (TextUtils.isEmpty(mAddress)) {
                    ToastUtils.getInstance().showToast("回收地址中联系地址为空，请更换回收地址");
                    return;
                }

                if (mDataList.size() == 0) {
                    ToastUtils.getInstance().showToast("回收纸箱为空，请扫描添加纸箱");
                    return;
                }

                ArrayList<BoxUploadBean> arrayList = new ArrayList<>();
                for (BoxDetailBean bean : mDataList) {
                    BoxUploadBean object = new BoxUploadBean();
                    object.setId(bean.getId());
                    object.setNum(bean.getNum());
                    arrayList.add(object);
                }

                Gson gson = new Gson();
                mBoxeslist = gson.toJson(arrayList);

                flagSearchLatAndLng = true;
                // Geo搜索 根据城市和具体的地址，获取对应的经纬度
                mSearch.geocode(new GeoCodeOption().city(mCity).address(mAddress));

                break;
            case R.id.tvAddAddressNewOrder:
                Intent intent1 = new Intent(this, RecycleAddressActivity.class);
                startActivityForResult(intent1, 1);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            linearAddressDetail.setVisibility(View.VISIBLE);
            tvAddAddressNewOrder.setVisibility(View.GONE);
            flagChooseAddress = true;
            Bundle bundle = data.getExtras();
            if (bundle == null) {
                return;
            }
            RecycleAddressBean bean = (RecycleAddressBean) bundle.getSerializable("RecycleAddressBean");
            if (bean != null) {
                ArrayList<String> addressList = new ArrayList<>();
                addressList.add(bean.getContact());
                addressList.add(bean.getPhone());
                addressList.add(bean.getAddress());
                addressList.add(bean.getArea());

                mContact = bean.getContact();
                mPhone = bean.getPhone();
                mAddress = bean.getAddress();
                tvPersonNewOrder.setText("联系人：" + mContact);
                tvPhoneNewOrder.setText("联系电话：" + mPhone);
                tvAddressNewOrder.setText("回收地址：" + mAddress);
                mCity = bean.getArea();
                ShareSaveUtils.locList(this, "Address", addressList);


//                if (!TextUtils.isEmpty(bean.getArea())) {
//                    String area = bean.getArea();
//                    String city_array[] = area.split("#");
//                    if (city_array.length == 1 || city_array.length == 2) {
//                        mCity = city_array[city_array.length - 1];
//                    } else {
//                        if (city_array.length != 0)
//                            mCity = city_array[city_array.length - 1];
//                    }
//                }

            }
        }
    }

    /**
     * 根据纸箱id 获取纸箱信息
     */
    private void getCartonDetailFormId(String id) {
        RetrofitHelper.getNetworkService()
                .getCartonDetailFormId(id)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<BoxDetailBean>() {
                    @Override
                    public void onNext(final BoxDetailBean value) {

                        if (mRealmResults.size() != 0) {
                            BoxDetailRealmModel firstModel = mRealmResults.get(0);

                            if (firstModel.getUserId() == value.getUser()) {

                                boolean flagAlreadyHas = false;
                                int index = 0;
                                for (int i = 0; i < mDataList.size(); i++) {
                                    BoxDetailBean bean = mDataList.get(i);
                                    if (bean.getId() == value.getId()) {
                                        flagAlreadyHas = true;  //列表中已经有此种类型的纸箱了，修改数据库中的num，没有的话添加此纸箱
                                        index = i;
                                        break;
                                    }
                                }

                                if (flagAlreadyHas) { //修改数据

                                    BoxDetailBean bean = mDataList.get(index);
                                    bean.setNum(bean.getNum() + 1);
                                    bean.setFlagIsAdd(true);
                                    mDataList.set(index, bean);
                                    mAdapter.notifyDataSetChanged();

                                    BoxDetailRealmModel model = mRealmResults.get(index);

                                    mRealm.beginTransaction();
                                    model.setNum(bean.getNum());
                                    mRealm.commitTransaction();

                                } else { //添加新的数据

                                    value.setNum(1);
                                    mDataList.add(value);
                                    mAdapter.setMoreData(mDataList);

                                    transaction = mRealm.executeTransactionAsync(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            BoxDetailRealmModel model = realm.createObject(BoxDetailRealmModel.class);
                                            model.setId(value.getId());
                                            model.setNum(1); //num加一
                                            model.setName(value.getName());
                                            model.setUserId(value.getUser());
                                        }
                                    });

                                }
                            } else { //两个纸箱对应的商家id不同，不能再同一个订单中

                                mPopupWindowRemind.showAtLocation(tvTitleTab, Gravity.CENTER, 0, 0);

                            }
                        } else { //第一次扫描添加纸箱

                            value.setNum(1);
                            mDataList.add(value);
                            mAdapter.setMoreData(mDataList);


                            transaction = mRealm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    BoxDetailRealmModel model = realm.createObject(BoxDetailRealmModel.class);
                                    model.setId(value.getId());
                                    model.setNum(1); //num加一
                                    model.setName(value.getName());
                                    model.setUserId(value.getUser());
                                }
                            });

                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof HttpException) { // 网络错误
                            HttpException httpEx = (HttpException) e;
                            String responseMsg = httpEx.getMessage();
                            int errorCode = httpEx.code();
                            if (errorCode == 404) {  //没有找到此纸箱

                                ToastUtils.getInstance().showToast("抱歉，此纸箱不存在，请重新扫描");
                            }
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
     * 发布新订单
     */
    private void MakeOrder() {
        RetrofitHelper.getNetworkService()
                .makeOrder(mBoxeslist, mContact, mPhone, mAddress, mLng, mLat)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<UserAccountBean>() {
                    @Override
                    public void onNext(UserAccountBean value) {
//                        {"name":"测试纸箱1","point":0,"area":"上海","user__realname":"147用户","quality":"木质","id":2,"size":"12*89*90"}

                        if (value.isSuccess()) {

                            transaction = mRealm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.deleteAll();
                                }
                            });

                            ShareSaveUtils.clearShareData("Address", NewAddOrderActivity.this);

                            ToastUtils.getInstance().showToast("发布成功");
                            finish();
                        } else {
                            ToastUtils.getInstance().showToast("发布失败");
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

                        ToastUtils.getInstance().showToast("发布失败" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            ToastUtils.getInstance().showToast("抱歉，搜索失败，未定位到回收地址的经纬度");
            return;
        }

        String strInfo = String.format("纬度：%f 经度：%f",
                result.getLocation().latitude, result.getLocation().longitude);

        mLat = String.valueOf(result.getLocation().latitude);
        mLng = String.valueOf(result.getLocation().longitude);
//        纬度：31.299729 经度：121.531716
        if (flagSearchLatAndLng) {
            MakeOrder();
        }
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

    }

    private void showSearchPop() {
        mPopView = getLayoutInflater().inflate(R.layout.pop_back_delete, null);

        final TextView tvPopDeleteCancle = (TextView) mPopView.findViewById(R.id.tvPopDeleteCancle);
        final TextView tvPopSure = (TextView) mPopView.findViewById(R.id.tvPopSure);

        LinearLayout linearPopSort = (LinearLayout) mPopView.findViewById(R.id.linearPopDelete);
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
                int height = mPopView.findViewById(R.id.linearPopDelete).getTop();
                int bottom_h = mPopView.findViewById(R.id.linearPopDelete).getBottom();

                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height || y > bottom_h) {
                        mPopupWindow.dismiss();
                    }
                }
                return true;
            }
        });

        tvPopDeleteCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });

        tvPopSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                transaction = mRealm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.deleteAll();
                    }
                });

                ShareSaveUtils.clearShareData("Address", NewAddOrderActivity.this);
                mPopupWindow.dismiss();
                finish();
            }
        });

    }

    private void showRemindPop() {
        mPopViewRemind = getLayoutInflater().inflate(R.layout.pop_tixian, null);

        TextView tvPopTiXianTitle = (TextView) mPopViewRemind.findViewById(R.id.tvPopTiXianTitle);
        tvPopTiXianTitle.setText("纸箱不属于同一个商家\n请重新扫描添加");
        final TextView tvPopTixianCancle = (TextView) mPopViewRemind.findViewById(R.id.tvPopTixianCancle);

        LinearLayout linearPopSort = (LinearLayout) mPopViewRemind.findViewById(R.id.linearPopTixian);
        linearPopSort.setAlpha(1);

        mPopupWindowRemind = new PopupWindow(mPopViewRemind, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT, true);
        mPopupWindowRemind.setOutsideTouchable(true);

        ColorDrawable dw = new ColorDrawable(0xb0000000);
        mPopupWindowRemind.setBackgroundDrawable(dw);

        mPopViewRemind.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int y = (int) motionEvent.getY();
                int height = mPopViewRemind.findViewById(R.id.linearPopTixian).getTop();
                int bottom_h = mPopViewRemind.findViewById(R.id.linearPopTixian).getBottom();

                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height || y > bottom_h) {
                        mPopupWindowRemind.dismiss();
                    }
                }
                return true;
            }
        });

        tvPopTixianCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPopupWindowRemind.dismiss();
            }
        });


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();


//        避免crash
        if (transaction != null && !transaction.isCancelled()) {
            transaction.cancel();
        }

        mRealm.close();
    }
}
