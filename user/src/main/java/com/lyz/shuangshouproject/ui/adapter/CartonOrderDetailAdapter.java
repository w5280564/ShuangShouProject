package com.lyz.shuangshouproject.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lyz.shuangshouproject.R;
import com.lyz.shuangshouproject.ui.bean.BoxlistBean;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 纸箱信息
 */
public class CartonOrderDetailAdapter extends BaseAdapter {
    private ArrayList<BoxlistBean> dataList;
    private Context context;

    public CartonOrderDetailAdapter(Context context, ArrayList<BoxlistBean> dataList) {
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

    public void setMoreData(ArrayList<BoxlistBean> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        float index = context.getSharedPreferences("index", Context.MODE_PRIVATE).getFloat("index", 0);
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_order_carton, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvFormatCartonItem.setText("规则："+dataList.get(position).getBox__name());
        viewHolder.tvNumCartonItem.setText("数量："+dataList.get(position).getNum());

        return convertView;
    }


    static class ViewHolder {
        @BindView(R.id.tvFormatCartonItem)
        TextView tvFormatCartonItem;
        @BindView(R.id.tvNumCartonItem)
        TextView tvNumCartonItem;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}