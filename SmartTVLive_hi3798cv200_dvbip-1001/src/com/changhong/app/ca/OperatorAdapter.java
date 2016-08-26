package com.changhong.app.ca;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import com.changhong.app.dtv.R;


/**
 * Created by Administrator on 15-5-15.
 */
public class OperatorAdapter extends BaseAdapter {

    private LayoutInflater minflater;

    private List<HashMap<String,String>> OperatorList;

    public OperatorAdapter(Context context, List<HashMap<String,String>> OperatorList) {
        this.minflater = LayoutInflater.from(context);
        this.OperatorList = OperatorList;
    }
    /**
     * 更新数据，更新list后在调用notifyDataSetChanged()
     * @param ipList
     */
    public void updateList(List<HashMap<String,String>> OperatorList) {
		this.OperatorList=OperatorList;
		notifyDataSetChanged();
	}

    @Override
    public int getCount() {
        return OperatorList==null?0:OperatorList.size();
    }

    @Override
    public Object getItem(int position) {
        return OperatorList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /**
         * VIEW HOLDER的配置
         */
        final ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = minflater.inflate(R.layout.ca_operator_item, null);
            vh.NameInfo = (TextView)convertView.findViewById(R.id.name);
            vh.StartTimeInfo = (TextView)convertView.findViewById(R.id.starttime);
            vh.EntitleInfo = (TextView)convertView.findViewById(R.id.entitletime);
            vh.ExpireInfo = (TextView)convertView.findViewById(R.id.endtime);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        if(OperatorList.get(position).get("Name")!=null&&!((String)(OperatorList.get(position).get("Name"))).equals("")){
        	vh.NameInfo.setText(""+OperatorList.get(position).get("Name"));
        }else{
        	vh.NameInfo.setText("空名称");
        }
        if(OperatorList.get(position).get("StartTime")!=null&&!((String)(OperatorList.get(position).get("StartTime"))).equals("")){
        	vh.StartTimeInfo.setText(""+OperatorList.get(position).get("StartTime"));
        }else{
        	vh.StartTimeInfo.setText("");
        }
        if(OperatorList.get(position).get("ExpireTime")!=null&&!((String)(OperatorList.get(position).get("ExpireTime"))).equals("")){
        	vh.ExpireInfo.setText(""+OperatorList.get(position).get("ExpireTime"));
        }else{
        	vh.ExpireInfo.setText("");
        }
        if(OperatorList.get(position).get("EntitleTime")!=null&&!((String)(OperatorList.get(position).get("EntitleTime"))).equals("")){
        	vh.EntitleInfo.setText(""+OperatorList.get(position).get("EntitleTime"));
        }else{
        	vh.EntitleInfo.setText("");
        }
        
        return convertView;
    }

    public final class ViewHolder {
        public TextView NameInfo;
        public TextView StartTimeInfo;
        public TextView EntitleInfo;
        public TextView ExpireInfo;
    }
}
