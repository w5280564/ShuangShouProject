package com.lyz.shuangshoudeliverer.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.bigkoo.pickerview.OptionsPickerView;
import com.google.gson.Gson;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.lyz.shuangshoudeliverer.ExitApplication;
import com.lyz.shuangshoudeliverer.R;
import com.lyz.shuangshoudeliverer.constant.StaticData;
import com.lyz.shuangshoudeliverer.httpapi.RetrofitHelper;
import com.lyz.shuangshoudeliverer.ui.base.BaseActivity;
import com.lyz.shuangshoudeliverer.ui.bean.ProvinceBean;
import com.lyz.shuangshoudeliverer.ui.bean.RecycleAddressBean;
import com.lyz.shuangshoudeliverer.ui.bean.UserAccountBean;
import com.lyz.shuangshoudeliverer.utils.GetJsonDataUtil;
import com.lyz.shuangshoudeliverer.utils.ToastUtils;

import org.json.JSONArray;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.schedulers.Schedulers;


/**
 * 编辑地址
 */
public class EditAddressActivity extends BaseActivity implements OnGetGeoCoderResultListener {

    @BindView(R.id.imgLeftTab)
    ImageView imgLeftTab;
    @BindView(R.id.tvTitleTab)
    TextView tvTitleTab;
    @BindView(R.id.etNameEditAddress)
    EditText etNameEditAddress;
    @BindView(R.id.etPhoneEditAddress)
    EditText etPhoneEditAddress;
    @BindView(R.id.tvDistrictAddress)
    TextView tvDistrictAddress;
    @BindView(R.id.tvProvinceAddress)
    TextView tvProvinceAddress;
    @BindView(R.id.tvCityAddress)
    TextView tvCityAddress;
    @BindView(R.id.tvZoneAddress)
    TextView tvZoneAddress;
    @BindView(R.id.relDistrictAddress)
    LinearLayout relDistrictAddress;
    @BindView(R.id.etAddressEditAddress)
    EditText etAddressEditAddress;
    @BindView(R.id.imgDefaultAddress)
    ImageView imgDefaultAddress;
    @BindView(R.id.relaDefaultAddress)
    RelativeLayout relaDefaultAddress;
    @BindView(R.id.btnSaveEditAddress)
    Button btnSaveEditAddress;
    @BindView(R.id.activity_person_data)
    LinearLayout activityPersonData;

    private float index;

    private ArrayList<ProvinceBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();

    private int flagChangeOrAdd;

    private GeoCoder mSearch = null; // 搜索模块，
    private String mLng, mLat;
    private int mIsDefault = 0;//是否为默认：isdefault   0 否 1 是
    private int mId;
    private boolean mFlagSave = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StaticData.setTranslucentStatus(this);
        setContentView(R.layout.activity_edit_address);
        ExitApplication.getInstance().addActivity(this);
        ButterKnife.bind(this);

        initView();

    }

    private void initView() {
        SharedPreferences s = EditAddressActivity.this.getSharedPreferences("index", Context.MODE_PRIVATE);
        index = s.getFloat("index", 0);
        tvTitleTab.setText("编辑地址");

        initJsonData();

        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);


        Bundle bundle = getIntent().getExtras();
        flagChangeOrAdd = bundle.getInt("flag");

        if (flagChangeOrAdd == 1) { //新增

        } else if (flagChangeOrAdd == 2) { //修改

            RecycleAddressBean recycleBean = (RecycleAddressBean) bundle.getSerializable("RecycleAddressBean");

            if (recycleBean != null) {
                etNameEditAddress.setText(recycleBean.getContact());
                etPhoneEditAddress.setText(recycleBean.getPhone());
                etAddressEditAddress.setText(recycleBean.getAddress());

                mId = recycleBean.getId();

                if (recycleBean.isIs_default()){
                    mIsDefault = 1;
                    imgDefaultAddress.setImageResource(R.drawable.select_set_default_address_img);
                }else {
                    mIsDefault = 0;
                    imgDefaultAddress.setImageResource(R.drawable.select_set_no_deafult_address_img);
                }


                String area = recycleBean.getArea();
                try {
                    if (!TextUtils.isEmpty(area)) { //省市区中间用#隔开
                        String areaArray[] = area.split("#");
                        if (areaArray.length == 2) {
                            tvProvinceAddress.setText(areaArray[0].trim());
                            tvCityAddress.setText(areaArray[0].trim());
                            tvZoneAddress.setText(areaArray[1].trim());
                        } else if (areaArray.length == 3) {
                            tvProvinceAddress.setText(areaArray[0].trim());
                            tvCityAddress.setText(areaArray[1].trim());
                            tvZoneAddress.setText(areaArray[2].trim());
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        searchGeocode(tvCityAddress.getText().toString().trim(), etAddressEditAddress.getText().toString().trim());
    }


    @OnClick({R.id.relDistrictAddress, R.id.btnSaveEditAddress, R.id.imgLeftTab,R.id.imgDefaultAddress})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgLeftTab:
                finish();
                break;
            case R.id.imgDefaultAddress:
                if (mIsDefault==0){
                    mIsDefault = 1;
                    imgDefaultAddress.setImageResource(R.drawable.select_set_default_address_img);
                }else {
                    mIsDefault = 0;
                    imgDefaultAddress.setImageResource(R.drawable.select_set_no_deafult_address_img);
                }


                break;
            case R.id.relDistrictAddress:
                try {
                    ShowPickerView();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btnSaveEditAddress:

                mFlagSave = true;
                searchGeocode(tvCityAddress.getText().toString().trim(), etAddressEditAddress.getText().toString().trim());

                break;
        }
    }

    private void ShowPickerView() {// 弹出选择器

        OptionsPickerView pvOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = options1Items.get(options1).getPickerViewText() +
                        options2Items.get(options1).get(options2) +
                        options3Items.get(options1).get(options2).get(options3);

                tvProvinceAddress.setText(options1Items.get(options1).getPickerViewText());
                tvCityAddress.setText(options2Items.get(options1).get(options2));
                tvZoneAddress.setText(options3Items.get(options1).get(options2).get(options3));
            }
        })

                .setTitleText("城市选择")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(20)
                .setOutSideCancelable(false)// default is true
                .build();

        /*pvOptions.setPicker(options1Items);//一级选择器
        pvOptions.setPicker(options1Items, options2Items);//二级选择器*/
        pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
        pvOptions.show();
    }


    /**
     * 发起搜索
     * 根据城市 搜索对应的经纬度
     */
    public void searchGeocode(String city, String address) {
        // Geo搜索
        mSearch.geocode(new GeoCodeOption().city(city).address(address));
    }

    /**
     * 发起搜索
     * 根据经纬度获取对应的城市地址信息
     */
    public void reverseGeoCode(float lat, float lon) {
        LatLng ptCenter = new LatLng(lat, lon);
        // 反Geo搜索
        mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                .location(ptCenter));
    }


    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {

            return;
        }

        String strInfo = String.format("纬度：%f 经度：%f",
                result.getLocation().latitude, result.getLocation().longitude);

        mLat = String.valueOf(result.getLocation().latitude);
        mLng = String.valueOf(result.getLocation().longitude);
//        纬度：31.299729 经度：121.531716
        if (mFlagSave) {
            if (flagChangeOrAdd == 1) {
                makeAddress();
            } else if (flagChangeOrAdd == 2) {
                editAddress();
            }
        }

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(EditAddressActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }

        Toast.makeText(EditAddressActivity.this, result.getAddress(),
                Toast.LENGTH_LONG).show();
    }

    /**
     * 新增回收地址
     * 联系人：contact
     * 联系电话：phone
     * 回收地区：area
     * 回收地址：address
     * 是否为默认：isdefault
     * 经度：lng
     * 纬度：lat
     */
    private void makeAddress() {
        RetrofitHelper.getNetworkService()
                .makeAddress(etNameEditAddress.getText().toString().trim(),
                        etPhoneEditAddress.getText().toString(),
                        tvProvinceAddress.getText().toString() + tvCityAddress.getText().toString() + tvZoneAddress.getText().toString(),
                        etAddressEditAddress.getText().toString().trim(), mIsDefault, mLng, mLat)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<UserAccountBean>() {
                    @Override
                    public void onNext(UserAccountBean value) {

                        if (value.isSuccess()) {
                            ToastUtils.getInstance().showToast("保存成功");
                            setResult(Activity.RESULT_OK);
                            finish();
                        } else {
                            mFlagSave = false;
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
     * 修改回收地址
     * 联系人：contact
     * 联系电话：phone
     * 回收地区：area
     * 回收地址：address
     * 是否为默认：isdefault
     * 经度：lng
     * 纬度：lat
     */
    private void editAddress() {
        RetrofitHelper.getNetworkService()
                .editAddress(mId, etNameEditAddress.getText().toString().trim(),
                        etPhoneEditAddress.getText().toString(),
                        tvProvinceAddress.getText().toString() + tvCityAddress.getText().toString() + tvZoneAddress.getText().toString(),
                        etAddressEditAddress.getText().toString().trim(), mIsDefault, mLng, mLat)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<UserAccountBean>() {
                    @Override
                    public void onNext(UserAccountBean value) {
                        if (value.isSuccess()) {
                            ToastUtils.getInstance().showToast("保存成功");
                            setResult(Activity.RESULT_OK);
                            finish();
                        } else {
                            mFlagSave = false;
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


    private void initJsonData() {//解析数据

        /**
         * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
         * 关键逻辑在于循环体
         *
         * */
        String JsonData = new GetJsonDataUtil().getJson(this, "province.json");//获取assets目录下的json文件数据

        ArrayList<ProvinceBean> jsonBean = parseData(JsonData);//用Gson 转成实体

        /**
         * 添加省份数据
         *
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        options1Items = jsonBean;

        for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
            ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {//遍历该省份的所有城市
                String CityName = jsonBean.get(i).getCityList().get(c).getName();
                CityList.add(CityName);//添加城市

                ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表

                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                if (jsonBean.get(i).getCityList().get(c).getArea() == null
                        || jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
                    City_AreaList.add("");
                } else {

                    for (int d = 0; d < jsonBean.get(i).getCityList().get(c).getArea().size(); d++) {//该城市对应地区所有数据
                        String AreaName = jsonBean.get(i).getCityList().get(c).getArea().get(d);

                        City_AreaList.add(AreaName);//添加该城市所有地区数据
                    }
                }
                Province_AreaList.add(City_AreaList);//添加该省所有地区数据
            }

            /**
             * 添加城市数据
             */
            options2Items.add(CityList);

            /**
             * 添加地区数据
             */
            options3Items.add(Province_AreaList);
        }


    }


    public ArrayList<ProvinceBean> parseData(String result) {//Gson 解析
        ArrayList<ProvinceBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                ProvinceBean entity = gson.fromJson(data.optJSONObject(i).toString(), ProvinceBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSearch.destroy();
    }
}
