package com.changhong.app.timeshift.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.changhong.dvb.Channel;

public class CacheData {

	public static boolean ab=true;
	public static List<Channel> allChannelInfo=new ArrayList<Channel>();
	public static Map<Integer, Channel> allChannelMap=new HashMap<Integer, Channel>();
	
	public static Map<String, List<ProgramInfo>> allProgramMap=new HashMap<String, List<ProgramInfo>>();
//	public static List<ProgramInfo> curProgramsList=new ArrayList<ProgramInfo>();
	
	public static String replayCurDay="";

	public static String curChannelNum="21";
	public static List<String> dayMonths=new LinkedList<String>();
	
	public static List<Channel> allChannelExtraInfo=new ArrayList<Channel>();

	public static Channel curChannel;
	public static ProgramInfo curProgram;
	public static List<ProgramInfo> curPrograms=new ArrayList<ProgramInfo>();//当前频道的3个节目
	
	public static List<Channel> getAllChannelExtraInfo() {
		return allChannelExtraInfo;
	}

	public static void setAllChannelExtraInfo(List<Channel> allChannelExtraInfo) {
		CacheData.allChannelExtraInfo = allChannelExtraInfo;
	}

	public static String getReplayCurDay() {
		return replayCurDay;
	}

	public static void setReplayCurDay(String replayCurDay) {
		CacheData.replayCurDay = replayCurDay;
	}

	public static Map<Integer, Channel> getAllChannelMap() {
		return allChannelMap;
	}

	public static void setAllChannelMap(Map<Integer, Channel> allChannelMap) {
		CacheData.allChannelMap = allChannelMap;
	}

	public static Map<String, List<ProgramInfo>> getAllProgramMap() {
		return allProgramMap;
	}

	public static void setAllProgramMap(Map<String, List<ProgramInfo>> allProgramMap) {
		CacheData.allProgramMap = allProgramMap;
	}

	public static String getCurChannelNum() {
		return curChannelNum;
	}
	public static void setCurChannelNum(String curChannelNum) {
		CacheData.curChannelNum = curChannelNum;
	}
	public static List<String> getDayMonths() {
		return dayMonths;
	}

	public static void setDayMonths(List<String> dayMonths) {
		CacheData.dayMonths = dayMonths;
	}

	public static List<Channel> getAllChannelInfo() {
		return allChannelInfo;
	}

	public static void setAllChannelInfo(List<Channel> allChannelInfo) {
		CacheData.allChannelInfo = allChannelInfo;
	}

	public static Channel getCurChannel() {
		return curChannel;
	}

	public static void setCurChannel(Channel curChannel) {
		CacheData.curChannel = curChannel;
	}

	public static ProgramInfo getCurProgram() {
		return curProgram;
	}

	public static void setCurProgram(ProgramInfo curProgram) {
		CacheData.curProgram = curProgram;
	}

	public static List<ProgramInfo> getCurPrograms() {
		return curPrograms;
	}

	public static void setCurPrograms(List<ProgramInfo> curPrograms) {
		CacheData.curPrograms = curPrograms;
		if(curPrograms!=null&&curPrograms.size()>1){
		setCurProgram(curPrograms.get(1));
		}
	}
		
}
