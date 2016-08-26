package com.changhong.app.dtv;

import java.util.Vector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.changhong.app.book.BookInfo;

public class EpgTimeReceiver extends BroadcastReceiver{

	SysApplication objApplication;
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO 处理预约时间
		objApplication=SysApplication.getInstance();
		Vector<BookInfo> bookInfos = objApplication.queryBookChannels();
		for(BookInfo bookInfo :bookInfos)
		{
			String bookDay=bookInfo.bookDay;
			String bookTime=bookInfo.bookTimeStart;
			
		}
		
		
	}

}
