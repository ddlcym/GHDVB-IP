package com.changhong.app.ads;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;

public class ADPicData {
	class Time_List {
		String start_time;
		String end_time;
		public Time_List(String start_time,String end_time){
			this.start_time=start_time;
			this.end_time=end_time;
		}		
	}

	class Channel_List {
		int groups_idx;
		int groups_ad_idx;
		List<String> list_channel_id;
		public Channel_List(int groups_idx,int groups_ad_idx,List<String> list_channel_id){
			this.groups_idx = groups_idx;
			this.groups_ad_idx=groups_ad_idx;
			this.list_channel_id = list_channel_id;
		}		
	}

	class Groups_List {
		int groups_idx;		
		List<String> list_user;		
		public Groups_List(int groups_idx,List<String> list_user){
			this.groups_idx = groups_idx;
			this.list_user = list_user;
		}		
	}
	class AD_Content{
		int groups_idx;
		int groups_ad_idx;
		int channel_ad_idx;
		int ad_list_idx;
		String type;
		String url;
		String tag;
		int cateId;
		List<Time_List> list_time_list;
		
		public AD_Content(int groups_idx,int groups_ad_idx,int channel_ad_idx,int ad_list_idx,String type,String url,String tag,
				List<Time_List> list_time_list){
			this.groups_idx = groups_idx;
			this.groups_ad_idx=groups_ad_idx;
			this.channel_ad_idx=channel_ad_idx;
			this.ad_list_idx=ad_list_idx;
			this.type=type;
			this.url=url;
			this.tag=tag;
			this.list_time_list = list_time_list;
		}	
		/*
		public int get_groups_idx(){
			return groups_idx;
		}			
		public void set_groups_idx(int tag){
			this.groups_idx = tag;
		}
		public int get_groups_ad_idx(){
			return groups_ad_idx;
		}			
		public void set_groups_ad_idx(int tag){
			this.groups_ad_idx = tag;
		}
		public int get_channel_ad_idx(){
			return channel_ad_idx;
		}			
		public void set_channel_ad_idx(int tag){
			this.channel_ad_idx = tag;
		}
		public String getType(){
			return type;
		}		
		public void setType(String type){
			this.type = type;
		}
		public String getUrl(){
			return url;
		}			
		public void setUrl(String url){
			this.url = url;
		}
		public String getTag(){
			return tag;
		}			
		public void setTag(String tag){
			this.tag = tag;
		}	
		public int getCateId(){
			return cateId;
		}			
		public void setCateId(int tag){
			this.cateId = tag;
		}
		*/		
	}

	int jsonType; //0或4->banner, 1或5 ->volume bar, 2或6 ->channel list
	AD_Content obj_Ad_default;
	List<AD_Content> list_ad_Content;	
	List<Channel_List> list_channel_List;
	List<Groups_List> list_groups_list;
	String str_fileDirectoryString;	
    List<String> filenameArray = new ArrayList<String>();
    List<Bitmap> bmpall = new ArrayList<Bitmap>();
    
	public ADPicData(int pic_pos){
		jsonType = pic_pos;
		list_ad_Content = new ArrayList<AD_Content>();
		list_channel_List = new ArrayList<Channel_List>();
		list_groups_list = new ArrayList<Groups_List>();
	}

	//store the file_path for pics and json files
	public void savePicPath(String path1) {
		str_fileDirectoryString = path1;
	}	
	public String getPicPath() {
		return str_fileDirectoryString;
	}
	
	public AD_Content newADContent(int groups_idx,int groups_ad_idx,int channel_ad_idx,int ad_list_idx,String type,String url,String tag,
			List<Time_List> list_time_list) {
		return new AD_Content(groups_idx,groups_ad_idx,channel_ad_idx,ad_list_idx,type,url,tag,list_time_list);
	}	
	public void addADContent(AD_Content ad_Content) {
		list_ad_Content.add(ad_Content);
	}	
	public AD_Content getADContent(int groups_idx,int groups_ad_idx,int channel_ad_idx,int ad_list_idx) {
		AD_Content ad_Content;
		for(int i=0;i<list_ad_Content.size();i++){
			ad_Content = list_ad_Content.get(i);
			if(ad_Content.groups_idx==groups_idx && ad_Content.groups_ad_idx==groups_ad_idx &&
					ad_Content.channel_ad_idx==channel_ad_idx && ad_Content.ad_list_idx==ad_list_idx)
				return ad_Content;
		}
		return null;
	}	
	public Time_List newTimeList(String start_time,String end_time) {
		return new Time_List(start_time,end_time);
	}
	
	public Channel_List newChannelList(int groups_idx,int groups_ad_idx,List<String> list_channel_id) {
		return new Channel_List(groups_idx,groups_ad_idx, list_channel_id);
	}	
	public void addChannelList(Channel_List ad_Content) {
		list_channel_List.add(ad_Content);
	}	
	//list_groups_list = new ArrayList<Groups_List>();
	public Groups_List newGroupsList(int groups_idx,List<String> list_user) {
		return new Groups_List(groups_idx,list_user);
	}	
	public void addGroupsList(Groups_List Groups_List) {
		list_groups_list.add(Groups_List);
	}		
}
