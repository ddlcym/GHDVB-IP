package com.changhong.app.pip;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import android.util.Log;

import com.changhong.app.dtv.Common;
import com.changhong.app.dtv.SysApplication;
import com.changhong.dvb.Channel;
import com.changhong.dvb.ChannelDB;


/**
 * edit in 2015.08.06
 * @author yangtong
 *
 */
public class LivePip {
	
	private static final String TAG = "LivePip";
	private static LivePip pipInstance = null;
	private int[] mChannelIds = null;
	
	private SysApplication objApplication = null;
	private  ChannelDB 	liveDvbDatabase 	= null;
	
	private boolean bPipPlaying = false;
	private int mPlayIndex = -1;
	
	public static LivePip getInstance()
	{
		if(pipInstance == null)
		{
			pipInstance = new LivePip();
		}
		return pipInstance;
	}
	
	public LivePip()
	{
		objApplication = SysApplication.getInstance();
		liveDvbDatabase = objApplication.dvbDatabase;
	}
//	
//	public int pipCreate()
//	{
//		if(objApplication == null)
//		{
//			Log.e(TAG, "no SysApplication object acquired!");
//			return -1;
//		}
//		return objApplication.CreateSlave();
//	}
//	
//	public int pipDestroy()
//	{
//		if(objApplication == null)
//		{
//			Log.e(TAG, "no SysApplication object acquired!");
//			return -1;
//		}
//		return objApplication.DestroySlave();
//	}
//	
//	public void pipStart()
//	{
//		if(bPipPlaying == true)
//		{
//			Log.i(TAG, "Pip is started, don't start again!");
//			return;
//		}
//		if(objApplication == null)
//		{
//			Log.e(TAG, "no SysApplication object acquired!");
//			return;
//		}
//		if(liveDvbDatabase.getChannelCount() <= 0)
//		{
//			Log.e(TAG, "no channel in database");
//			return;
//		}
//		
//		pipCreate();
//		
//		int toPlay = objApplication.getSlaveLastChannelId();
//		DvbChannel toPlayChannel = null;
//		if(toPlay >= 0)
//		{
//			toPlayChannel = liveDvbDatabase.getChannel(toPlay);
//		}
//		if(toPlayChannel == null)
//		{
//			toPlayChannel = objApplication.getCurPlayingChannel();
//		}
//		objApplication.ShowSlaveVideo();
//		objApplication.playSlaveChannel(toPlayChannel);
//			
//		mPlayIndex = toPlayChannel.mi_ChanId;
//		bPipPlaying = true;
//	}
//	
//	public void pipStop()
//	{
//		if(bPipPlaying == false)
//		{
//			return;
//		}
//		objApplication.stopSlavePlay();
//		objApplication.BlankSlaveVideo();
//		objApplication.HideSlaveVideo();
//		pipDestroy();
//		mPlayIndex = -1;
//		bPipPlaying = false;
//	}
//	
//	public void pipPlayNextChannel()
//	{
//		Channel mo_CurChannel =liveDvbDatabase.getNextChannel(mPlayIndex, 1, true);
//		if(mo_CurChannel != null)
//		{
//			objApplication.playSlaveChannel(mo_CurChannel);
//			mPlayIndex = mo_CurChannel.mi_ChanId;
//		}
//		else
//		{
//			Common.LOGE("HANDLE_MSG_TYPE_NEW_CHANNEL_NEXT get null progrom ");
//		}
//
//	}
//	
//	public void pipPlayPreChannel()
//	{
//		DvbChannel mo_CurChannel =liveDvbDatabase.getNextChannel(mPlayIndex, -1, true);
//		if(mo_CurChannel != null)
//		{
//			objApplication.playSlaveChannel(mo_CurChannel);
//			mPlayIndex = mo_CurChannel.mi_ChanId;
//		}
//		else
//		{
//			Common.LOGE("HANDLE_MSG_TYPE_NEW_CHANNEL_NEXT get null progrom ");
//		}
//	}
//	
//	public void pipSwapWithMain()
//	{
//		if(bPipPlaying == false)
//		{
//			return;
//		}
//		Channel mainPlaying = objApplication.getCurPlayingChannel();
//		Channel slavePlaying = liveDvbDatabase.getChannel(mPlayIndex);
//		
//		objApplication.playChannel(slavePlaying, false);
//		objApplication.playSlaveChannel(mainPlaying);
//		mPlayIndex = mainPlaying.mi_ChanId;
//	}
	
	/*--------------------------- private interface ---------------------*/
	
}
