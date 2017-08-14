package com.lyz.shuangshouproject.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lyz.shuangshouproject.ExitApplication;
import com.lyz.shuangshouproject.R;
import com.lyz.shuangshouproject.constant.StaticData;
import com.lyz.shuangshouproject.ui.base.BaseFragmentActivity;
import com.lyz.shuangshouproject.ui.fragment.FragMySSFinished;
import com.lyz.shuangshouproject.ui.fragment.FragMySSOrdered;
import com.lyz.shuangshouproject.ui.fragment.FragMySSOrders;
import com.lyz.shuangshouproject.ui.fragment.FragMySSRecycled;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 我的双收
 */
public class MyShuangShouActivity extends BaseFragmentActivity {


    @BindView(R.id.imgLeftTab)
    ImageView imgLeftTab;
    @BindView(R.id.tvTitleTab)
    TextView tvTitleTab;
    @BindView(R.id.tvOrderMySS)
    TextView tvOrderMySS;
    @BindView(R.id.linearOrdersMySS)
    LinearLayout linearOrdersMySS;
    @BindView(R.id.tvOrderedMySS)
    TextView tvOrderedMySS;
    @BindView(R.id.linearOrderedMySS)
    LinearLayout linearOrderedMySS;
    @BindView(R.id.tvRecycledMySS)
    TextView tvRecycledMySS;
    @BindView(R.id.linearRecycledMySS)
    LinearLayout linearRecycledMySS;
    @BindView(R.id.tvFinishedMySS)
    TextView tvFinishedMySS;
    @BindView(R.id.linearFinishedMySS)
    LinearLayout linearFinishedMySS;
//    @BindView(R.id.tvEvaluationMySS)
//    TextView tvEvaluationMySS;
//    @BindView(R.id.linearEvaluationMySS)
//    LinearLayout linearEvaluationMySS;
    @BindView(R.id.framelayoutMySS)
    FrameLayout framelayoutMySS;
    private FragmentManager fm;
    private FragmentTransaction ft;

    private FragMySSOrders fragMySSOrders;
    private FragMySSOrdered fragMySSOrdered;
    private FragMySSRecycled fragMySSRecycled;
    private FragMySSFinished fragMySSFinished;

    private Fragment mContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StaticData.setTranslucentStatus(this);
        setContentView(R.layout.activity_myshuangshou);
        ExitApplication.getInstance().addActivity(this);
        ButterKnife.bind(this);


        init();
    }


    private void init() {

        tvTitleTab.setText("我的双收");

        fragMySSOrders = new FragMySSOrders();
        fragMySSOrdered = new FragMySSOrdered();
        fragMySSRecycled = new FragMySSRecycled();
        fragMySSFinished = new FragMySSFinished();


        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        ft.add(R.id.framelayoutMySS, fragMySSOrders);
        ft.commit();
        mContent = fragMySSOrders;

    }


    @OnClick({R.id.imgLeftTab, R.id.linearOrdersMySS, R.id.linearOrderedMySS, R.id.linearRecycledMySS, R.id.linearFinishedMySS})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgLeftTab:
                finish();
                break;
            case R.id.linearOrdersMySS:
                switchContent(fragMySSOrders);
                uploadUI(tvOrderMySS, tvOrderedMySS, tvRecycledMySS, tvFinishedMySS);
                break;
            case R.id.linearOrderedMySS:
                switchContent(fragMySSOrdered);
                uploadUI(tvOrderedMySS, tvOrderMySS, tvRecycledMySS, tvFinishedMySS);
                break;
            case R.id.linearRecycledMySS:
                switchContent(fragMySSRecycled);
                uploadUI(tvRecycledMySS, tvOrderMySS, tvOrderedMySS, tvFinishedMySS);
                break;
            case R.id.linearFinishedMySS:
                switchContent(fragMySSFinished);
                uploadUI(tvFinishedMySS, tvOrderMySS, tvOrderedMySS, tvRecycledMySS);
                break;
        }
    }

    /**
     * 修改显示的内容 不会重新加载
     **/
    public void switchContent(Fragment to) {
        if (mContent != to) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (!to.isAdded()) { // 先判断是否被add过
                transaction.hide(mContent).add(R.id.framelayoutMySS, to); // 隐藏当前的fragment，add下一个到Activity中
                //使用commit() onSaveInstanceState方法保存状态会报异常
                transaction.commitAllowingStateLoss();
            } else {
                transaction.hide(mContent).show(to); // 隐藏当前的fragment，显示下一个
                transaction.commitAllowingStateLoss();
            }
            mContent = to;
        }
    }

    private void uploadUI(TextView tv1, TextView tv2, TextView tv3, TextView tv4) {

        tv1.setTextColor(Color.parseColor("#000000"));
        tv2.setTextColor(Color.parseColor("#c9c9c9"));
        tv3.setTextColor(Color.parseColor("#c9c9c9"));
        tv4.setTextColor(Color.parseColor("#c9c9c9"));

    }
}
