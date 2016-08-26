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
public class OperatorInfoAdapter extends BaseAdapter {

    private LayoutInflater minflater;

    private List<HashMap<String,String>> OperatorList;

    public OperatorInfoAdapter(Context context, List<HashMap<String,String>> OperatorList) {
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
            convertView = minflater.inflate(R.layout.ca_operatorinfo_item, null);
            vh.ID = (TextView)convertView.findViewById(R.id.operatorid);
            vh.Name = (TextView)convertView.findViewById(R.id.operatorname);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        if(OperatorList.get(position).get("operatorid")!=null&&!((String)(OperatorList.get(position).get("operatorid"))).equals("")){
        	vh.ID.setText(""+OperatorList.get(position).get("operatorid"));
        }else{
        	vh.ID.setText("");
        }
        if(OperatorList.get(position).get("operator")!=null&&!((String)(OperatorList.get(position).get("operator"))).equals("")){
        	vh.Name.setText(""+OperatorList.get(position).get("operator"));
        }else{
        	vh.Name.setText("");
        }
        
        return convertView;
    }

    public final class ViewHolder {
        public TextView ID;
        public TextView Name;
    }
}
