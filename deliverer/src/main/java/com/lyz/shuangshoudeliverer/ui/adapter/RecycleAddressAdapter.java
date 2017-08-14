package com.lyz.shuangshoudeliverer.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lyz.shuangshoudeliverer.R;
import com.lyz.shuangshoudeliverer.interfacePackage.OnItemClickListener;
import com.lyz.shuangshoudeliverer.ui.bean.RecycleAddressBean;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 回收地址
 */
public class RecycleAddressAdapter extends BaseAdapter {
    private ArrayList<RecycleAddressBean> dataList;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public RecycleAddressAdapter(Context context, ArrayList<RecycleAddressBean> dataList) {
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

    public void setMoreData(ArrayList<RecycleAddressBean> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener( OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        float index = context.getSharedPreferences("index", Context.MODE_PRIVATE).getFloat("index", 0);
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_recycle_address, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        viewHolder.tvContactsRecycle.setText("联系人："+dataList.get(position).getContact());
        viewHolder.tvPhoneRecycle.setText("联系电话："+dataList.get(position).getPhone());
        viewHolder.tvAddressRecycle.setText("回收地址："+dataList.get(position).getAddress());

        if (dataList.get(position).isIs_default()){
            viewHolder.tvAddressFlagRecycle.setText("当前为默认地址");
            viewHolder.tvAddressFlagRecycle.setTextColor(Color.parseColor("#6CD9EE"));
        }else {
            viewHolder.tvAddressFlagRecycle.setText("设为默认地址");
            viewHolder.tvAddressFlagRecycle.setTextColor(Color.parseColor("#6CD9EE"));
        }

        viewHolder.tvEditAddressRecycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null){
                    onItemClickListener.onClick(v,position);
                }

            }
        });

        viewHolder.tvAddressFlagRecycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null){
                    onItemClickListener.onClick(v,position);
                }
            }
        });

        return convertView;
    }


    static class ViewHolder {
        @BindView(R.id.tvContactsRecycle)
        TextView tvContactsRecycle;
        @BindView(R.id.tvPhoneRecycle)
        TextView tvPhoneRecycle;
        @BindView(R.id.tvAddressRecycle)
        TextView tvAddressRecycle;
        @BindView(R.id.tvAddressFlagRecycle)
        TextView tvAddressFlagRecycle;
        @BindView(R.id.tvEditAddressRecycle)
        TextView tvEditAddressRecycle;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}