package com.lyz.shuangshouproject.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.lyz.shuangshouproject.ExitApplication;
import com.lyz.shuangshouproject.R;
import com.lyz.shuangshouproject.constant.StaticData;
import com.lyz.shuangshouproject.httpapi.RetrofitHelper;
import com.lyz.shuangshouproject.interfacePackage.OnItemClickListener;
import com.lyz.shuangshouproject.ui.adapter.RecycleAddressAdapter;
import com.lyz.shuangshouproject.ui.base.BaseActivity;
import com.lyz.shuangshouproject.ui.bean.RecycleAddressBean;
import com.lyz.shuangshouproject.ui.bean.UserAccountBean;
import com.lyz.shuangshouproject.utils.ToastUtils;
import com.lyz.shuangshouproject.utils.customView.MyListView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * 回收地址
 */
public class RecycleAddressActivity extends BaseActivity implements OnItemClickListener {

    @BindView(R.id.imgLeftTab)
    ImageView imgLeftTab;
    @BindView(R.id.tvTitleTab)
    TextView tvTitleTab;
    @BindView(R.id.listviewRecycleAddress)
    MyListView listviewRecycleAddress;

    @BindView(R.id.tvBottomAddAddress)
    TextView tvBottomAddAddress;

    private ArrayList<RecycleAddressBean> mDataList;
    private RecycleAddressAdapter mAdapter;

    private final int INTENT_FLAG_ADDNEW_ADDRESS = 1;
    private final int INTENT_FLAG_CHANGE_ADDRESS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StaticData.setTranslucentStatus(this);
        setContentView(R.layout.activity_recycle_address);
        ExitApplication.getInstance().addActivity(this);
        ButterKnife.bind(this);

        initView();
        getAllAddress();

    }

    private void initView() {
        tvTitleTab.setText("回收地址");

        mDataList = new ArrayList<>();

        mAdapter = new RecycleAddressAdapter(this, mDataList);
        listviewRecycleAddress.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);

        tvBottomAddAddress.setVisibility(View.VISIBLE);
        //长按，删除回收地址
        listviewRecycleAddress.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteAddress(mDataList.get(position).getId());
                return false;
            }
        });

        listviewRecycleAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("RecycleAddressBean",mDataList.get(position));
                intent.putExtras(bundle);
                setResult(Activity.RESULT_OK,intent);
                finish();
            }
        });
    }

    @Override
    public void onClick(View view, int position) {

        Intent intent = new Intent(this, EditAddressActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("flag", INTENT_FLAG_CHANGE_ADDRESS);
        bundle.putSerializable("RecycleAddressBean", mDataList.get(position));
        intent.putExtras(bundle);
        startActivityForResult(intent, INTENT_FLAG_CHANGE_ADDRESS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            mDataList.clear();
            getAllAddress();
        }
    }

    /**
     * 获取回收地址
     */
    private void getAllAddress() {
        RetrofitHelper.getNetworkService()
                .getAllAddress()
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<ArrayList<RecycleAddressBean>>() {
                    @Override
                    public void onNext(ArrayList<RecycleAddressBean> value) {

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

    @OnClick({R.id.imgLeftTab, R.id.tvBottomAddAddress})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgLeftTab:
                finish();
                break;
            case R.id.tvBottomAddAddress:
                Intent intent = new Intent(this, EditAddressActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("flag", INTENT_FLAG_ADDNEW_ADDRESS);
                intent.putExtras(bundle);
                startActivityForResult(intent, INTENT_FLAG_ADDNEW_ADDRESS);
                break;
        }
    }

    /**
     * 删除回收地址
     */
    private void deleteAddress(int id) {
        RetrofitHelper.getNetworkService()
                .deleteAddress(id)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<UserAccountBean>() {
                    @Override
                    public void onNext(UserAccountBean value) {

                        if (value.isSuccess()){
                            ToastUtils.getInstance().showToast("删除成功");
                            mDataList.clear();
                            getAllAddress();
                        }else {
                            ToastUtils.getInstance().showToast("删除失败");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.getInstance().showToast("删除失败");
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

}
