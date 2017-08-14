package com.lyz.shuangshouproject.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lyz.shuangshouproject.R;
import com.lyz.shuangshouproject.constant.StaticaAdaptive;


/**
 * 弹出框的列表适配器
 */
public class SelectAdapter extends BaseAdapter {
    private String[] selectlist;
    private Context context;

    public SelectAdapter(Context context, String[] selectlist) {
        super();
        this.selectlist = selectlist;
        this.context = context;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return (selectlist == null) ? 0 : selectlist.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return selectlist[position];
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        // TODO Auto-generated method stub
        int p = position;
        if (p == 0)
            return 0;
        else
            return 1;

    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }



    public class ViewHolder {
        TextView selectbtn;
    }

    private ViewHolder viewHolder;
    private LayoutInflater inflater;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        float index = context.getSharedPreferences("index", Context.MODE_PRIVATE).getFloat("index",0);
        if (convertView == null) {
            inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_item_select, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.selectbtn = (TextView) convertView.findViewById(R.id.selectbtn);
            StaticaAdaptive.adaptiveView(viewHolder.selectbtn, 713, 120,index);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.selectbtn.setText(selectlist[position]);
        viewHolder.selectbtn.setAlpha(1);
        return convertView;
    }




}