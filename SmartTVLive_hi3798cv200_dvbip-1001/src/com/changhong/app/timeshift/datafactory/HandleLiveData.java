package com.changhong.app.timeshift.datafactory;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;

import com.changhong.app.dtv.SysApplication;
import com.changhong.app.timeshift.common.CacheData;
import com.changhong.app.timeshift.common.L;
import com.changhong.app.utils.SortData;
import com.changhong.dvb.Channel;
import com.changhong.dvb.ChannelDB;
import com.changhong.dvb.DVB;

public class HandleLiveData {
	private JsonResolve JsonResolve = null;
	private Context con;

	private static HandleLiveData handliveData;

	public static HandleLiveData getInstance() {
		if (null == handliveData) {
			handliveData = new HandleLiveData();
		}
		return handliveData;
	}

	public HandleLiveData() {
		if (null == JsonResolve) {
			JsonResolve = JsonResolve.getInstance();
		}

		if (null == con) {
			con = SysApplication.getInstance();
		}
	}

	public List<Channel> dealChannelJson(JSONObject json) {
		List<Channel> channels = JsonResolve.jsonToChannels(json);
		for (Channel channel : channels) {
			CacheData.allChannelMap.put(channel.logicNo, channel);
			CacheData.allChannelInfo.add(channel);
		}

		// sort
//		Collections.sort(channels, new Comparator<Channel>() {
//
//			public int compare(Channel o1, Channel o2) {
//				int result = Integer.parseInt(o1.getChannelNumber()) - Integer.parseInt(o2.getChannelNumber());
//				if (result == 0) {
//					result = o1.getChannelName().compareTo(o2.getChannelName());
//				}
//				return result;
//			}
//		});

		return channels;
	}
	
	public void dealChannelExtra(JSONObject json){
		int chanId=0;
//		L.i("handlelivedata-dealChannelExtra:"+json.toString());
		List<Channel> timeshiftChannel=JsonResolve.jsonToChannelExtra(json);
		CacheData.setAllChannelExtraInfo(timeshiftChannel);
		if(null==timeshiftChannel||timeshiftChannel.size()==0) 
		{
			return;
		}
		
		for(int i=0; i<timeshiftChannel.size();i++){
			Channel channel=timeshiftChannel.get(i);
			ChannelDB db=DVB.getManager().getChannelDBInstance();
			Channel channel2 = db.getChannelByLogicNo(channel.logicNo);
			if(channel2!=null){
				chanId=channel2.chanId;
				db.updateChannel(chanId, "resource_code", channel.resource_code);
				db.updateChannel(chanId, "logo",channel.logo );
				db.updateChannel(chanId, "is_ttv", channel.is_ttv);
				db.updateChannel(chanId, "is_btv",channel. is_btv);
			}
		}
	}
	

	public String dealCategoryVer(JSONObject json){
		return JsonResolve.jsonToCategoryVer(json);		
	}
	public List<SortData> dealCategoryName(JSONObject json){
		//L.e("dealCategoryName:"+json.toString());
		return JsonResolve.jsonToCategoryName(json);		
	}
	
	public void dealCategoryData(JSONObject json){
		//L.i("dealCategoryData:"+json.toString());
		List<Channel> sortIdChannel=JsonResolve.jsonToCategoryChannel(json);
		if(null==sortIdChannel||sortIdChannel.size()==0) 
		{			
			L.e("dealCategoryData: return:"+sortIdChannel.size()); 
			return;
		}		
		ChannelDB db=DVB.getManager().getChannelDBInstance();	
		Channel chanInDB,chan;
		for(int i=0; i<sortIdChannel.size();i++){
			chan=sortIdChannel.get(i);
			chanInDB=db.getChannelByTsIdAndServiceId(chan.tsId, chan.serviceId);
			if(chanInDB!=null){
				db.updateChannel(chanInDB.chanId, "favorite", ""+chan.sortId);
				L.e("dealCategoryData: set >> id="+chanInDB.chanId+",sortid="+chan.sortId+","+chanInDB.name);
			}else{
				L.e("dealCategoryData: not found >> tsid="+chan.tsId+",serid="+chan.serviceId);
			}
		}
			
	}	
}
