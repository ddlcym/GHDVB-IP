package com.changhong.app.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.R.integer;
import android.util.Log;

import com.changhong.dvb.Channel;

public class SortData {
	public int rank;
	public String pramKey;
	public String pramValue;
	private static List<SortData> sortNameList;
		
	public static void saveSortNameList(List<SortData> rSortNameList){
		sortNameList = rSortNameList;
		int sortNum;
		if(( sortNum=sortNameList.size())!=0){
			Log.i("mmmm","saveSortNameList>> total type= "+sortNum);			
			for (int i = 0; i < sortNum; i++) {
				Log.i("mmmm","<<"+sortNameList.get(i).rank+","+sortNameList.get(i).pramValue+","+sortNameList.get(i).pramKey+">>");
			}
		}		
	}
	private static int covernt(String key){
		int sortNum=0,result=0;
		if((sortNum=sortNameList.size())!=0){
			for (int i = 0; i < sortNum; i++) {
				if(sortNameList.get(i).pramKey.equals(key)){					
					//result=sortNameList.get(i).rank;
					switch(sortNameList.get(i).rank){
					case 4:	result = 1;break;
					case 3:	result = 2;break;
					case 2:	result = 3;break;
					case 0:	result = 4;break;
					default: result = 0;break;
					}
					break;
				}
			}
		}
		return result;
	}
	public static void setChannelType(Channel chan, String type){
		int val,res=0;
		String[]  result=type.split(",");
		for(String element:result){
			Log.i("mmmm","<<"+element.trim()+">>");
			 if((val=covernt(element.trim()))!=0){
				 res |= 1<<val;
			 }			
		}		
		chan.sortId = res;
	}
	public static void setChannelType_new(Channel chan, String type){
		Log.i("mmmm","setChannelType_new>>"+chan.chanId+",type:"+type);
		
		int rev=0;
		Pattern p = Pattern.compile("\\d+");  
		Matcher m = p.matcher(type);  
		m.find();
		try {
			rev = Integer.parseInt(m.group());	
		} catch (Exception e) {
			e.printStackTrace();
		}		
		chan.sortId = rev;
	}	
}