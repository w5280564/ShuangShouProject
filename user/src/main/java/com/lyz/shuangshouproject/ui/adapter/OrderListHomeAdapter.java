package com.lyz.shuangshouproject.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lyz.shuangshouproject.R;
import com.lyz.shuangshouproject.constant.StaticData;
import com.lyz.shuangshouproject.interfacePackage.OnItemClickListener;
import com.lyz.shuangshouproject.ui.bean.BoxlistBean;
import com.lyz.shuangshouproject.ui.bean.OrderListBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 订单列表
 */
public class OrderListHomeAdapter extends BaseAdapter {
    private ArrayList<OrderListBean> dataList;
    private Context context;
    private OnItemClickListener onItemClickListener;


    public OrderListHomeAdapter(Context context, ArrayList<OrderListBean> dataList) {
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
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_order_list, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvNameOrderItem.setText(dataList.get(position).getCode());

        viewHolder.imgOrderItem.setController(StaticData.loadFrescoImgRes(R.drawable.icon_carton,
                index, 200, 200, viewHolder.imgOrderItem));

        int state = dataList.get(position).getState();


        if (StaticData.ORDER_STATE_ORDERS.equals(String.valueOf(state))) {  //待接单

            viewHolder.tvScheduleOrderItem.setText("待接单");
            viewHolder.tvOperationOrderItem.setVisibility(View.VISIBLE);

            if (StaticData.FLAG_USER) {
                viewHolder.tvOperationOrderItem.setText("撤回");
            } else { //快递员
                viewHolder.tvOperationOrderItem.setText("接单");
            }


        } else if (StaticData.ORDER_STATE_ORDERED.equals(String.valueOf(state))) {  //已接单
            viewHolder.tvScheduleOrderItem.setText("已接单");
            if (StaticData.FLAG_USER) {

                viewHolder.tvOperationOrderItem.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.tvOperationOrderItem.setText("回收");
                viewHolder.tvOperationOrderItem.setVisibility(View.VISIBLE);
            }

        } else if (StaticData.ORDER_STATE_RECYCLED.equals(String.valueOf(state))) {//已回收
            if (StaticData.FLAG_USER) {
                viewHolder.tvOperationOrderItem.setText("评价");
                viewHolder.tvOperationOrderItem.setVisibility(View.VISIBLE);
            } else {
                viewHolder.tvOperationOrderItem.setVisibility(View.INVISIBLE);
            }
            viewHolder.tvScheduleOrderItem.setText("已回收");

        } else if (StaticData.ORDER_STATE_FINISHED.equals(String.valueOf(state))) {//已完成
            viewHolder.tvScheduleOrderItem.setText("已完成");
            viewHolder.tvOperationOrderItem.setVisibility(View.INVISIBLE);
        }

        if (dataList.get(position).getBoxlist().size() != 0) {
            viewHolder.tvNameOrderItem.setVisibility(View.VISIBLE);
            viewHolder.tvNameOrderItem.setText(dataList.get(position).getBoxlist().get(0).getBox__name() + "*"
                    + dataList.get(position).getBoxlist().get(0).getNum());
            List<BoxlistBean> boxlist = dataList.get(position).getBoxlist();
            int score = 0;
            for (BoxlistBean boxlistBean : boxlist) {
                if (boxlistBean.getBox__point() != 0 && boxlistBean.getNum() != 0) {
                    score = score + boxlistBean.getBox__point() * boxlistBean.getNum();
                }

            }

            viewHolder.tvCountOrderItem.setText("积分 " + score);
        } else {
            viewHolder.tvNameOrderItem.setVisibility(View.INVISIBLE);
            viewHolder.tvCountOrderItem.setText("积分 0");
        }

        viewHolder.tvNumOrderItem.setText("订单号：" + dataList.get(position).getCode());
        viewHolder.tvTimeOrderItem.setText("发布时间：" + StaticData.GTMToLocal(dataList.get(position).getPubDate()));

        viewHolder.tvOperationOrderItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onClick(v, position);
            }
        });
        return convertView;
    }


    class ViewHolder {
        @BindView(R.id.tvNumOrderItem)
        TextView tvNumOrderItem;
        @BindView(R.id.tvNameOrderItem)
        TextView tvNameOrderItem;
        @BindView(R.id.tvCountOrderItem)
        TextView tvCountOrderItem;
        @BindView(R.id.tvTimeOrderItem)
        TextView tvTimeOrderItem;
        @BindView(R.id.imgOrderItem)
        SimpleDraweeView imgOrderItem;
        @BindView(R.id.tvScheduleOrderItem)
        TextView tvScheduleOrderItem;
        @BindView(R.id.tvOperationOrderItem)
        TextView tvOperationOrderItem;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}