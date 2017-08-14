package com.lyz.shuangshoudeliverer.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lyz.shuangshoudeliverer.R;
import com.lyz.shuangshoudeliverer.constant.StaticData;
import com.lyz.shuangshoudeliverer.ui.bean.PointBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 积分详情
 */
public class CountDetailAdapter extends BaseAdapter {
    private ArrayList<PointBean> dataList;
    private Context context;

    public CountDetailAdapter(Context context, ArrayList<PointBean> dataList) {
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

    public void setMoreData(ArrayList<PointBean> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        float index = context.getSharedPreferences("index", Context.MODE_PRIVATE).getFloat("index", 0);
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_count_detail, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvCountDetail.setText("+"+dataList.get(position).getPoint());
        viewHolder.tvTimeCountDetail.setText(StaticData.GTMToLocal(dataList.get(position).getPubDate()));

        List<PointBean.BoxlistBean> boxlist = dataList.get(position).getBoxlist();
        StringBuilder builder = new StringBuilder();
        if (boxlist!=null && boxlist.size()!=0){
            for (PointBean.BoxlistBean boxlistBean:boxlist){
                builder.append(boxlistBean.getBox__name()+"纸箱*"+boxlistBean.getNum()+"  ");
            }
        }

        viewHolder.tvContentCountDetail.setText(builder.toString());

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.tvCountDetail)
        TextView tvCountDetail;
        @BindView(R.id.tvTimeCountDetail)
        TextView tvTimeCountDetail;
        @BindView(R.id.tvContentCountDetail)
        TextView tvContentCountDetail;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}