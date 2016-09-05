package com.changhong.app.timeshift.datafactory;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.changhong.app.dtv.R;
import com.changhong.app.timeshift.common.ProgramInfo;
import com.changhong.app.timeshift.common.Utils;

/**
 * @author cym
 * @date 创建时间：2016年8月14日 下午3:04:40
 * @version 1.0
 * @parameter
 */
public class TimeShiftProgramAdapter extends BaseAdapter {

	private LayoutInflater mInflater = null;
	private List<ProgramInfo> myList = new ArrayList<ProgramInfo>();

	public TimeShiftProgramAdapter(Context context) {
		// 根据context上下文加载布局，这里的是Activity本身，即this
		this.mInflater = LayoutInflater.from(context);
	}

	public void setData(List<ProgramInfo> list) {
		myList = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return myList != null ? myList.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.timeshift_program_item,
					null);
			holder = new ViewHolder();
			holder.programTime = (TextView) convertView
					.findViewById(R.id.timeshift_program_time);
			holder.programName = (TextView) convertView
					.findViewById(R.id.timeshift_program_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (myList != null && myList.size() != 0) {
			ProgramInfo program = myList.get(position);
			holder.programTime.setText(Utils.hourAndMinute(program.getBeginTime())+"-"+Utils.hourAndMinute(program.getEndTime()));
			holder.programName.setText(program.getEventName());
			
		}
		return convertView;
	}

	private class ViewHolder {
		TextView programTime;
		// TextView channelIndex;
		TextView programName;
		// ImageView favView;
	}
}
