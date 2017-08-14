package com.lyz.shuangshoudeliverer.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lyz.shuangshoudeliverer.R;
import com.lyz.shuangshoudeliverer.ui.bean.BoxDetailBean;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 纸箱信息
 */
public class NewAddOrderAdapter extends BaseAdapter {
    private ArrayList<BoxDetailBean> dataList;
    private Context context;

    public NewAddOrderAdapter(Context context, ArrayList<BoxDetailBean> dataList) {
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

    public void setMoreData(ArrayList<BoxDetailBean> dataList) {
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

        viewHolder.tvFormatCartonItem.setText("规则："+dataList.get(position).getName());
        viewHolder.tvNumCartonItem.setText("数量："+dataList.get(position).getNum());

        if (dataList.get(position).isFlagIsAdd()){
            viewHolder.tvAnimCartonItem.setVisibility(View.VISIBLE);
            AlphaAnimation alphaAnimation = (AlphaAnimation) AnimationUtils.loadAnimation(context, R.anim.alpha_add_order);
            viewHolder.tvAnimCartonItem.startAnimation(alphaAnimation);
        }else {
            viewHolder.tvAnimCartonItem.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }


    static class ViewHolder {
        @BindView(R.id.tvFormatCartonItem)
        TextView tvFormatCartonItem;
        @BindView(R.id.tvNumCartonItem)
        TextView tvNumCartonItem;
        @BindView(R.id.tvAnimCartonItem)
        TextView tvAnimCartonItem;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}