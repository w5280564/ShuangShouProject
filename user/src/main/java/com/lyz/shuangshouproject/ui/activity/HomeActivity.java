package com.lyz.shuangshouproject.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lyz.shuangshouproject.ExitApplication;
import com.lyz.shuangshouproject.R;
import com.lyz.shuangshouproject.constant.StaticData;
import com.lyz.shuangshouproject.ui.account.PersonCenterActivity;
import com.lyz.shuangshouproject.ui.base.BaseFragmentActivity;
import com.lyz.shuangshouproject.ui.fragment.FragHomeMap;
import com.lyz.shuangshouproject.ui.fragment.FragHomeOrderList;
import com.lyz.shuangshouproject.ui.service.GpsService;
import com.lyz.shuangshouproject.utils.ToastUtils;
import com.lyz.shuangshouproject.utils.scan.core.CaptureActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends BaseFragmentActivity {
    @BindView(R.id.imgLeftTab)
    ImageView imgLeftTab;
    @BindView(R.id.tvTitleTab)
    TextView tvTitleTab;
    @BindView(R.id.imgRightTab)
    ImageView imgRightTab;
    @BindView(R.id.tvOrderListHome)
    TextView tvOrderListHome;
    @BindView(R.id.relativeOrderListHome)
    RelativeLayout relativeOrderListHome;
    @BindView(R.id.tvMapHome)
    TextView tvMapHome;
    @BindView(R.id.relativeMapHome)
    RelativeLayout relativeMapHome;
    @BindView(R.id.framelayoutHome)
    FrameLayout framelayoutHome;
    @BindView(R.id.tvOrderListEnglishHome)
    TextView tvOrderListEnglishHome;
    @BindView(R.id.tvMapEnglishHome)
    TextView tvMapEnglishHome;

    private FragmentManager fm;
    private FragmentTransaction ft;

    private FragHomeMap fragHomeMap;
    private FragHomeOrderList fragHomeOrderList;
    private Fragment mContent;

    private float index;
    public int flagFragment = 1;

    private PopupWindow mPopupWindow;
    private View mPopView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StaticData.setTranslucentStatus(this);
        setContentView(R.layout.activity_main);
        ExitApplication.getInstance().addActivity(this);
        ButterKnife.bind(this);

        init();
        inirFrag();

    }


    private void init() {

        DisplayMetrics DisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(DisplayMetrics);
        float width = DisplayMetrics.widthPixels;
        index = width / 750; //以750像素的宽度为标准
        float scale = getResources().getDisplayMetrics().scaledDensity;
        SharedPreferences s = getSharedPreferences("index", Context.MODE_PRIVATE); //存储到共享参数中
        SharedPreferences.Editor e = s.edit();
        e.putFloat("index", index);
        e.commit();

        imgLeftTab.setImageResource(R.drawable.icon_person);
        tvTitleTab.setText("双收快递");
        if (StaticData.FLAG_USER) {
            imgRightTab.setImageResource(R.drawable.icon_scan);
        } else {
            startGpsService(); //只有快递员需要向服务器发送位置
            showSearchPop();
            imgRightTab.setImageResource(R.drawable.dispatcher_select);
        }

        imgRightTab.setVisibility(View.VISIBLE);


    }

    private void inirFrag(){
        fragHomeMap = new FragHomeMap();
        fragHomeOrderList = new FragHomeOrderList();

        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        ft.add(R.id.framelayoutHome, fragHomeOrderList);
        ft.commit();
        mContent = fragHomeOrderList;
    }


    private void startGpsService(){
        Intent intent = new Intent(this, GpsService.class);
        // 启动该Service
        startService(intent);
    }

    @OnClick({R.id.imgLeftTab, R.id.imgRightTab, R.id.relativeOrderListHome, R.id.relativeMapHome})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgLeftTab:
                Intent intent = new Intent(this, PersonCenterActivity.class);
                startActivity(intent);
                break;
            case R.id.imgRightTab:
                if (StaticData.FLAG_USER) {
                    Intent intent1 = new Intent(this, CaptureActivity.class);
                    startActivity(intent1);
                } else {
                    if (flagFragment == 1) {

                        mPopupWindow.showAtLocation(tvTitleTab, Gravity.CENTER, 0, 0);
                    }
                }

                break;
            case R.id.relativeOrderListHome:
                flagFragment = 1;
                uploadUI(tvMapEnglishHome, tvMapHome, tvOrderListEnglishHome, tvOrderListHome);
                switchContent(fragHomeOrderList);
                break;
            case R.id.relativeMapHome:
                flagFragment = 2;
                uploadUI(tvOrderListEnglishHome, tvOrderListHome, tvMapEnglishHome, tvMapHome);

                switchContent(fragHomeMap);
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
                transaction.hide(mContent).add(R.id.framelayoutHome, to); // 隐藏当前的fragment，add下一个到Activity中
                //使用commit() onSaveInstanceState方法保存状态会报异常
                transaction.commitAllowingStateLoss();
            } else {
                transaction.hide(mContent).show(to); // 隐藏当前的fragment，显示下一个
                transaction.commitAllowingStateLoss();
            }
            mContent = to;
        }
    }

    private long secondTime;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - secondTime > 2000) {
                secondTime = System.currentTimeMillis();
                ToastUtils.getInstance().showToast(getResources().getString(R.string.app_exit_sure));
            } else {
                finish();
//                System.exit(0);
                ExitApplication.getInstance().exit();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void showSearchPop() {
        mPopView = getLayoutInflater().inflate(R.layout.pop_search, null);

        final TextView tvPopSearchOrders = (TextView) mPopView.findViewById(R.id.tvPopSearchOrders);
        final TextView tvPopSearchOrdered = (TextView) mPopView.findViewById(R.id.tvPopSearchOrdered);
        final TextView tvPopSearchRecycled = (TextView) mPopView.findViewById(R.id.tvPopSearchRecycled);
        final TextView tvPopSearchFinished = (TextView) mPopView.findViewById(R.id.tvPopSearchFinished);

        LinearLayout linearPopSort = (LinearLayout) mPopView.findViewById(R.id.linearPopSearch);
        linearPopSort.setAlpha(1);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) linearPopSort.getLayoutParams();
        params.topMargin = (int) (160 * index);
        linearPopSort.setLayoutParams(params);

        mPopupWindow = new PopupWindow(mPopView, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setOutsideTouchable(true);
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        mPopupWindow.setBackgroundDrawable(dw);

        mPopView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int y = (int) motionEvent.getY();
                int height = mPopView.findViewById(R.id.linearPopSearch).getTop();
                int bottom_h = mPopView.findViewById(R.id.linearPopSearch).getBottom();

                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height || y > bottom_h) {
                        mPopupWindow.dismiss();
                    }
                }
                return true;
            }
        });

        tvPopSearchOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flagFragment == 1) {
                    fragHomeOrderList.setmState("0");
                }
                uploadPopUI(tvPopSearchOrders, tvPopSearchOrdered, tvPopSearchRecycled, tvPopSearchFinished);

                mPopupWindow.dismiss();
            }
        });

        tvPopSearchOrdered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flagFragment == 1) {
                    fragHomeOrderList.setmState("1");
                }
                uploadPopUI(tvPopSearchOrdered, tvPopSearchOrders, tvPopSearchRecycled, tvPopSearchFinished);
                mPopupWindow.dismiss();
            }
        });
        tvPopSearchRecycled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flagFragment == 1) {
                    fragHomeOrderList.setmState("2");
                }
                uploadPopUI(tvPopSearchRecycled, tvPopSearchOrders, tvPopSearchOrdered, tvPopSearchFinished);
                mPopupWindow.dismiss();
            }
        });
        tvPopSearchFinished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flagFragment == 1) {
                    fragHomeOrderList.setmState("3");
                }
                uploadPopUI(tvPopSearchFinished, tvPopSearchOrders, tvPopSearchOrdered, tvPopSearchRecycled);
                mPopupWindow.dismiss();
            }
        });

    }



    private void uploadUI(TextView tv1, TextView tv2, TextView tv3, TextView tv4) {

        tv1.setTextColor(Color.parseColor("#c9c9c9"));
        tv2.setTextColor(Color.parseColor("#c9c9c9"));
        tv3.setTextColor(Color.parseColor("#000000"));
        tv4.setTextColor(Color.parseColor("#000000"));

    }

    private void uploadPopUI(TextView tv1, TextView tv2, TextView tv3, TextView tv4) {

        tv1.setTextColor(Color.parseColor("#3bd0ea"));
        tv2.setTextColor(Color.parseColor("#c6c6c6"));
        tv3.setTextColor(Color.parseColor("#c6c6c6"));
        tv4.setTextColor(Color.parseColor("#c6c6c6"));

    }
}
