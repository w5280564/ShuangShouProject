package com.lyz.shuangshoudeliverer.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lyz.shuangshoudeliverer.R;
import com.lyz.shuangshoudeliverer.constant.StaticData;
import com.lyz.shuangshoudeliverer.interfacePackage.OnItemClickListener;
import com.lyz.shuangshoudeliverer.ui.bean.OrderListBean;
import com.lyz.shuangshoudeliverer.utils.ToastUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 评论系统
 */
public class EvaluationSystermAdapter extends BaseAdapter {
    private ArrayList<OrderListBean> dataList;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public EvaluationSystermAdapter(Context context, ArrayList<OrderListBean> dataList) {
        super();
        this.dataList = dataList;
        this.context = context;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return (dataList == null) ? 0 : dataList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public void setMoreData(ArrayList<OrderListBean> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        float index = context.getSharedPreferences("index", Context.MODE_PRIVATE).getFloat("index", 0);
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_evalation_systerm, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);

//            viewHolder.etEvaluationSystemItem.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    viewHolder.etEvaluationSystemItem.setFocusableInTouchMode(true);
//                }
//            });

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        if (StaticData.ORDER_STATE_RECYCLED.equals(String.valueOf(dataList.get(position).getState()))) {
            //已回收的状态
            viewHolder.relaEvaluationSystem.setVisibility(View.VISIBLE);
            viewHolder.relaContentSystem.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(dataList.get(position).getContent())) {
                viewHolder.etEvaluationSystemItem.setText(dataList.get(position).getContent());
            }

        } else if (StaticData.ORDER_STATE_FINISHED.equals(String.valueOf(dataList.get(position).getState()))) {
            //已完成的状态
            viewHolder.relaEvaluationSystem.setVisibility(View.GONE);
            viewHolder.relaContentSystem.setVisibility(View.VISIBLE);
            viewHolder.tvContentSystemItem.setText(dataList.get(position).getContent());
            viewHolder.tvTimeSystem.setText(StaticData.GTMToLocal(dataList.get(position).getJudgeDate()));
        }

        viewHolder.tvNumSystemItem.setText("订单号：" + dataList.get(position).getCode());

        if (onItemClickListener != null) {
            viewHolder.tvDetailSystemItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onClick(v, position);
                }
            });
            viewHolder.tvSubmitEvaluationSystemItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setTag(viewHolder.etEvaluationSystemItem.getText().toString());
                    if (!TextUtils.isEmpty(viewHolder.etEvaluationSystemItem.getText().toString())) {
                        onItemClickListener.onClick(v, position);
                    } else {
                        ToastUtils.getInstance().showToast("请输入评论内容");
                    }

                }
            });
        }


        return convertView;
    }


    static class ViewHolder {
        @BindView(R.id.tvNumSystemItem)
        TextView tvNumSystemItem;
        @BindView(R.id.tvDetailSystemItem)
        TextView tvDetailSystemItem;
        @BindView(R.id.etEvaluationSystemItem)
        EditText etEvaluationSystemItem;
        @BindView(R.id.relaEvaluationSystem)
        RelativeLayout relaEvaluationSystem;
        @BindView(R.id.tvContentSystemItem)
        TextView tvContentSystemItem;
        @BindView(R.id.tvTimeSystem)
        TextView tvTimeSystem;
        @BindView(R.id.relaContentSystem)
        RelativeLayout relaContentSystem;

        @BindView(R.id.tvSubmitEvaluationSystemItem)
        TextView tvSubmitEvaluationSystemItem;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}