package com.changhong.app.dtv;

import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;
import android.util.Log;

import com.changhong.app.constant.Class_Constant;
import com.changhong.dvb.DVB;
import com.changhong.dvb.NetworkAttribute;
import com.changhong.dvb.TableListener;
import com.changhong.dvb.ProtoMessage.DVB_PSI;
import com.changhong.dvb.ProtoMessage.DVB_Section;
import com.changhong.dvb.ProtoMessage.DVB_Table;
import com.changhong.dvb.ProtoMessage.DVB_TableFilter;
import com.google.protobuf.ByteString;

public class NitMonitor {
	DVB_TableFilter.Builder  NitFilterBuilder;
	private int MagicNumber = 30001003;
	private static String TAG = "NitMonitor";
	Main context;
	private boolean ListenerSetted=false;
	public static int curNitVer=-1;
	public static final int GH_NetworkId=16512; //�軪��Ŀ��Ҫ�ж�NetworkId
	
	public NitMonitor(Context context){
		this.context = (Main)context;
		NitFilterBuilder = DVB_TableFilter.newBuilder();
		NitFilterBuilder.setType(DVB_PSI.PSI_NIT);
		if (ListenerSetted == false) {
			Log.i(TAG, "set  nitTableListener when creating");
			DVB.getManager().getTableInstance().setListener(nitTableListener);
			ListenerSetted = true;			
		}
	 }
	public void startMonitor(){
		if (ListenerSetted == false && nitTableListener!=null) {
			DVB.getManager().getTableInstance().setListener(nitTableListener);
			ListenerSetted = true;			
			Log.i(TAG, "set  nitTableListener when starting");			
		}		
		 DVB.getManager().getTableInstance().start_get(MagicNumber,
				 NitFilterBuilder.build());	
	}	
	public void stopMonitor(){
		DVB.getManager().getTableInstance().stop_get(MagicNumber,
				 NitFilterBuilder.build());		
		if(nitTableListener!=null&&ListenerSetted){
			DVB.getManager().getTableInstance().removeListener(nitTableListener);
			Log.i(TAG, "clear nitTableListener when stop");	
		}
		ListenerSetted = false;
	}
	private TableListener nitTableListener = new TableListener() {

		@Override
		public void tableCallback(DVB_Table table, Object obj) {

			Log.i(TAG, "****** listener callback is running ******");

			DVB_PSI tableType = table.getType();

			int Pid = table.getPid();

			Log.i(TAG, " tableCallback ==> tableType:" + tableType
					+ "    PID:" + Pid);

			/*
			 * if (isInDvbScan()) { Log.e(TAG,
			 * "We are in dvb scan mode, skip this callback"); return; }
			 */
			if (tableType == DVB_PSI.PSI_NIT && Pid == 0x0010) // NIT table
			{
				Log.e(TAG, "Nit reach my space, parse it!");
				checkNitVersionIsChanged(table);
				return;
			}

		}

	};	

	public void checkNitVersionIsChanged(DVB_Table table) {

		NetworkAttribute networkAttr = DVB.getManager().getChannelDBInstance()
				.getSavedNetworkAttribute();
		if (null == networkAttr) {
			Log.i(TAG, "nitTableListener-callback:networkAttr is null, return");
			return;
		}
		/*
		 * if (networkAttr.mNitCrc32 == null ||
		 * networkAttr.mNitCrc32.equals("null")) { Log.i(TAG,
		 * "nitTableListener-networkAttr.mNitCrc32 is null, return"); return; }
		 */

		int iNitVerInTable = table.getVersion();
		if (iNitVerInTable == networkAttr.mNitVersion
				|| iNitVerInTable == curNitVer) {
			Log.i(TAG, "Nit version NOT changed,DB=" + networkAttr.mNitVersion + ",LAST="
					+ curNitVer + ",cur=" + iNitVerInTable);
			curNitVer = iNitVerInTable;
			return;
		}
		Log.i(TAG, "Nit version changed,DB=" + networkAttr.mNitVersion + ",LAST="
				+ curNitVer + ",cur=" + iNitVerInTable);
		
		curNitVer = iNitVerInTable;

		int network_id = getNetworkIdFromNit(table);
		if (network_id != GH_NetworkId) {
			Log.i(TAG,
					"nitTableListener-callback: NOT GH_NetworkId 16512,Return");
			return;
		}
		context.onVkey(Class_Constant.VKEY_EMPTY_DBASE);

	}
	
	public static void notifyNewFreq(){
		curNitVer = -1;
	}
	private int getNetworkIdFromNit(DVB_Table table) {
		DVB_Section tableSection;
		ByteString tableSecData;
		int networkid = -1;
		try {
			tableSection = table.getSection(0);
			tableSecData = tableSection.getData();
			if (tableSecData.size() > 5) {
				int data1, data2;
				data1 = tableSecData.byteAt(3);
				data2 = tableSecData.byteAt(4);
				networkid = (data1 << 8) & 0xff00 | data2 & 0xff;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return networkid;
	}	
}
