package com.lyz.shuangshoudeliverer.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lyz.shuangshoudeliverer.R;
import com.lyz.shuangshoudeliverer.constant.StaticData;
import com.lyz.shuangshoudeliverer.ui.bean.InforBean;
import com.lyz.shuangshoudeliverer.utils.customView.ExpandableTextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 消息通知
 */
public class InforAdapter extends BaseAdapter {
    private ArrayList<InforBean> dataList;
    private Context context;

    public InforAdapter(Context context, ArrayList<InforBean> dataList) {
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

    public void setMoreData(ArrayList<InforBean> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        float index = context.getSharedPreferences("index", Context.MODE_PRIVATE).getFloat("index", 0);
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_infor, parent, false);
            viewHolder = new ViewHolder(convertView);
            viewHolder.expandableInfor.setListener(new ExpandableTextView.OnExpandStateChangeListener() {
                @Override
                public void onExpandStateChanged(boolean isExpanded) {

                }
            });
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.expandableInfor.setText(dataList.get(position).getContent(), true);
        viewHolder.tvTitleInforItem.setText(dataList.get(position).getTitle());
        viewHolder.tvTimeInforItem.setText(StaticData.GTMToLocal(dataList.get(position).getPubDate()));

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.tvTitleInforItem)
        TextView tvTitleInforItem;
        @BindView(R.id.tvContentInforItem)
        TextView tvContentInforItem;
        @BindView(R.id.tvTimeInforItem)
        TextView tvTimeInforItem;
        @BindView(R.id.tvPackUpInforItem)
        TextView tvPackUpInforItem;
        @BindView(R.id.expandableInfor)
        ExpandableTextView expandableInfor;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}