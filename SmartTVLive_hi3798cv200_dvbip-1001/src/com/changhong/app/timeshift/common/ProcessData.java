package com.changhong.app.timeshift.common;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import android.util.Log;

import com.changhong.app.dtv.SysApplication;
import com.changhong.dvb.Channel;

/*return encrypt url adress*/
/*Author:OscarChang*/

public class ProcessData {

	private MD5Encrypt MD5;
//	private static final String serverAdress = "http://ott.yun.gehua.net.cn:8080/";
	private static final String serverAdress = "http://api.ott.yun.gehua.net.cn:8080/";
	private String MD5Key = "aidufei";
	private String conStr = "&authKey=";

	private String getCatalog="msis/getCatalog?";
	/* chnList:频道列表 param of must */
	private String chListPendingStr = "msis/getChannels?";
	private String chListVersion = "V001";
	private String chListChannelVersion = "0";
	private String chListResolution = "1280*768";
	private String chListTerminalType = "1";
	/* for test */
	// private String chListpgSize = "&pageSize=20";

	/*********************************************************************************/
	/* chPgmList:频道节目列表 param of must */
	private String chPgmListPendingStr = "msis/getChannelProgram?";
	private String chPgmListVersion = "V001";
	private String chPgmListResolution = "1280*768";
	private String chPgmListChannelResourceCode = "8061"; // not known
	private String chPgmListTerminalType = "1";

	/*********************************************************************************/
	/* pgmInfo:节目信息 : param of must */
	private String pgmInfoPendingStr = "msis/getCurrentProgramInfo?";
	private String pgmInfoVersion = "V001";
	private String pgmInfoChannelId = "8061";

	/*********************************************************************************/
	/* crntPgmList：频道的当前节目单 param of must */
	private String crntPgmListVersion = "V001";
	private String crntPgmListchannelResourceCode = "8061";
	private String crntPgmListPendingStr = "msis/getChannelCurrentPrograms?";
	/*********************************************************************************/
	/* playUrl:播放串 param of must */
	private String playUrlPendingStr = "msis/getPlayURL?";
	private String playUrlVersion = "V002";
	private String playUrlResolution = "1280*768";
	private String liveUrlPlayType = "2";
	private String replayUrlPlayType = "3";
	private String playUrlTerminalType = "7";
	/*********************************************************************************/
	/* livePgmInfo：直播节目详情 param of must */
	private String livePgmInfoPendingStr = "msis/getPorgramInfo?";
	private String livePgmInfoVersion = "V001";
	private String livePgmInfoResolution = "1024*720";
	private String livePgmInfoTerminalType = "1";
	/*********************************************************************************/
	/* channelInfo：频道信息 param of must */
	private String channelInfoPendingStr = "msis/getChannelInfo?";
	private String channelInfoVersion = "V001";
	private String channelInfoResolution = "800*600";
	private String channelInfoTerminalType = "1";
	
	/* getChannelsInfo 获取用户频道信息 */
	private String getChannelsInfo="msis/getChannelsInfo?";
	private String version1="V001";
//	private String userCode="01051608310000001";
	private String userCode="15914018212";
	/*   获取用户注册信息 */
	private String sendRegValidCode="msis/sendRegValidCode?";
	
	/*
	 * 获取频道分类信息
	 */
	public String getTypes(){
		String catalogURL=serverAdress+"msis/getPram?version=V001&PramName=ChannelType&terminalType=1";
		return strGETReturn(catalogURL);
	}

	/* generate channel list ： 获取频道列表 */
	public String getChannelList() {
//		String rawPlainStr = serverAdress + chListPendingStr + "version=" + chListVersion + "&channelVersion="
//				+ chListChannelVersion + "&resolution=" + chListResolution + "&terminalType=" + chListTerminalType;
		
		
//		String rawPlainStr = "http://hd.ott.yun.gehua.net.cn/getChannels?" + "version=" + chListVersion + "&channelVersion="
//				+ chListChannelVersion + "&resolution=" + chListResolution + "&terminalType=" + chListTerminalType;
		String rawPlainStr = serverAdress+"msis/getChannels?" + "version=" + chListVersion + "&channelVersion="
				+ chListChannelVersion + "&resolution=" + chListResolution + "&terminalType=" + chListTerminalType;
		

		return strGETReturn(rawPlainStr);
		
	}
	
	public String getCategoryString() {
		String categoryURL=serverAdress+"msis/getNBAT?"+"version=V001&PramName=ChannelType&terminalType=1";
		return strGETReturn(categoryURL);
	}
	
	/* generate channel's program list ：频道下的节目列表 */
	public String getChannelProgramList(Channel outterchanInfo) {
		String rawPlainStr = serverAdress + chPgmListPendingStr + "version=" + chPgmListVersion + "&resolution="
				+ chPgmListResolution + "&channelResourceCode=" + outterchanInfo.resource_code+ "&beginTime="
				+ getSevenDayAgo()[1] + "&endTime=" + getSevenDayAgo()[0] + "&terminalType=" + chPgmListTerminalType;

		return strGETReturn(rawPlainStr);
	}
	/* generate 4hours channel's program info 获取4小时内节目信息 */
	public String get4HoursProgramList(Channel outterchanInfo) {
		String rawPlainStr = serverAdress + chPgmListPendingStr + "version=" + chPgmListVersion + "&resolution="
				+ chPgmListResolution + "&channelResourceCode=" + outterchanInfo.resource_code + "&beginTime="
				+ get4HoursAgo()[1] + "&endTime=" + get4HoursAgo()[0] + "&terminalType=" + chPgmListTerminalType;

		return strGETReturn(rawPlainStr);
	}

	/* generate current channel's program info 获取节目信息 */
	public String getCurrentProgramInfo(Channel outterchanInfo) {
		String rawPlainStr = serverAdress + pgmInfoPendingStr + "&version=" + pgmInfoVersion + "&channelId="
				+ outterchanInfo.logicNo;

		return strPOSTReturn(rawPlainStr, "msis/getCurrentProgramInfo");
	}
	
	

	/* generate current channel's program list 获取频道的当前节目单 */
	public String getCurrentChannelProgramList(Channel outterchanInfo) {
		String rawPlainStr = serverAdress + crntPgmListPendingStr + "&version=" + crntPgmListVersion
				+ "&channelResourceCode=" + outterchanInfo.resource_code;

		return strGETReturn(rawPlainStr);
	}

	/* generate live play program info 获取直播节目详情 */
	public String getLivePlayProgramInfo(int outterProgramId) {
		// outterProgramId = 4892095;
		String rawPlainStr = serverAdress + livePgmInfoPendingStr + "&version=" + livePgmInfoVersion + "&resolution="
				+ livePgmInfoResolution + "&terminalType=" + livePgmInfoTerminalType + "&programId=" + outterProgramId;

		return strGETReturn(rawPlainStr);
	}

	/* generate live play url string 获取直播播放串 */
	public String getLivePlayUrlString(Channel outterchanInfo) {
//		String rawPlainStr = serverAdress + playUrlPendingStr + "version=" + playUrlVersion + "&resourceCode="
//				+ outterchanInfo.getResourceCode() + "&providerID=" + outterchanInfo.getProviderID() + "&assetID="
//				+ outterchanInfo.getAssetID() + "&resolution=" + playUrlResolution + "&playType=" + liveUrlPlayType
//				+ "&terminalType=" + playUrlTerminalType;
//
//		return strPOSTReturn(rawPlainStr, "msis/getPlayURL");
		return "0";
	}
	
	public String getLiveBackPlayUrl(Channel channel,int delay){
		String rawPlainStr="http://ott.yun.gehua.net.cn:8080/msis/getPlayURL?version=V001&userCode="+userCode+"&userName="+userCode+"&resourceCode="+channel.resource_code+"&resolution=1280*720&terminalType=4&playType=4&delay="+delay;
				
		return strPOSTReturn(rawPlainStr, "msis/getPlayURL");
	}

	/* generate replay url string 获取回看播放串 */
	public String getReplayPlayUrlString(Channel outterchanInfo, ProgramInfo outterProgramInfo, int outterDelay) {

		// int time = 100000;
//		String rawPlainStr = serverAdress + playUrlPendingStr + "version=" + playUrlVersion + "&resourceCode="
//				+ outterchanInfo.getResourceCode() + "&providerID=" + outterchanInfo.getProviderID() + "&assetID="
//				+ outterchanInfo.getAssetID() + "&resolution=" + playUrlResolution + "&playType=" + replayUrlPlayType
//				+ "&terminalType=" + playUrlTerminalType + "&shifttime="
//				+ outterProgramInfo.getBeginTime().getTime() / 1000 + "&shiftend="
//				+ outterProgramInfo.getEndTime().getTime() / 1000 + "&delay=" + outterDelay;
//
//		return strPOSTReturn(rawPlainStr, "msis/getPlayURL");
		return "0";
	}

	/* generate channel info url string 获取频道信息 */
	public String getChannelIfo(String channelId) {
		String rawPlainStr = serverAdress + channelInfoPendingStr + "version=" + channelInfoVersion + "&resourceCode="
				+ channelId + "&resolution=" + channelInfoResolution + "&terminalType=" + channelInfoTerminalType;

		return strGETReturn(rawPlainStr);
	}
	
	//发送用户注册证码
	public String sendRegValidCode(){
		String requestURL=null;
		requestURL=serverAdress+sendRegValidCode+"version="+version1+"&mobile="+userCode+"&codeType="+0;
		
		return strGETReturn(requestURL);
	}
	
	
	
	
	//获取用户频道信息，是否支持时移
	public String getChannelsInfo(){
		String requestURL=null;
		requestURL=serverAdress+getChannelsInfo+"version="+version1+"&userCode="+userCode+"&terminalType="+1;
		return strGETReturn(requestURL);
	}
	
	
	//获取系统时间
	public String getSystemTime(){
		String requestURL=null;
		requestURL="http://ip:port/msis/getSystemTime?";
		
		return strGETReturn(requestURL);
	}
	
	

	/* Http GET String return */
	public String strGETReturn(String arg) {

		return (arg + conStr + MD5Encrypt.MD5EncryptExecute(arg));
	}

	/* Http POST String return */
	public String strPOSTReturn(String arg0, String arg1) {

		return (arg0 + conStr + MD5Encrypt.MD5EncryptExecute(serverAdress + arg1));
	}

	public long dateToSecend(String str) {
		Date beginDate = null;
		long seconds = 0;
		SimpleDateFormat sdfNew = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			beginDate = sdfNew.parse(str);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		seconds = beginDate.getTime();

		return seconds;
	}

	/* get Date seven days ago */
	public static String[] getSevenDayAgo() {
		String twoDates[] = { "", "" };
		SimpleDateFormat sdfNew = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(curDate);
		gc.add(5, -6);
		Date backSevenDate = gc.getTime();
		curDate.setHours(23);
		curDate.setMinutes(59);
		curDate.setSeconds(59);
		backSevenDate.setHours(0);
		backSevenDate.setMinutes(0);
		backSevenDate.setSeconds(0);

		twoDates[0] = sdfNew.format(curDate);
		twoDates[1] = sdfNew.format(backSevenDate);
		twoDates[0] = twoDates[0].replace(" ", "+").replace(":", "%3A");
		twoDates[1] = twoDates[1].replace(" ", "+").replace(":", "%3A");
		return twoDates;
	}
	
	/* get Date seven days ago */
	public static String[] get4HoursAgo() {
		String twoDates[] = { "", "" };
		SimpleDateFormat sdfNew = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());
		Date backSevenDate = new Date(curDate.getTime()-4*60*60*1000);

		twoDates[0] = sdfNew.format(curDate);
		twoDates[1] = sdfNew.format(backSevenDate);
		twoDates[0] = twoDates[0].replace(" ", "+").replace(":", "%3A");
		twoDates[1] = twoDates[1].replace(" ", "+").replace(":", "%3A");
		return twoDates;
	}
	
	//获取系统SN号
	private static String getSN(){
		String sn=SystemProperiesInvoke.get(SysApplication.getInstance(), "sys.stb.serialnumber");
		Log.i("mmmm", "sn:"+sn);
		return sn;
	}
}
