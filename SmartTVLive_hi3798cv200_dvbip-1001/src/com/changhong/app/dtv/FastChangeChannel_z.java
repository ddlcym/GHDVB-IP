package com.changhong.app.dtv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.changhong.app.constant.Advertise_Constant;
import com.changhong.dvb.Channel;
import com.changhong.dvb.DVB;
import com.changhong.dvb.ProtoMessage.DVB_EPG_PF;
import com.xormedia.adplayer.AdItem;
import com.xormedia.adplayer.AdPlayer;
import com.xormedia.adplayer.AdStrategy;
import com.xormedia.adplayer.IAdPlayerCallbackListener;
import com.xormedia.adplayer.IAdStrategyResponseListener;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class FastChangeChannel_z extends Activity {

	Button shangye_z, xiaye_z, fanhui_z;
	TextView ksht_xyjm, ksht_dqjm;
	Channel channel;

	private static Context context;

	String tag = "Huantai_fast_z";
	int currentSelect = -1;

	String PF_enventName_P = new String();
	String PF_enventName_F = new String();

	List<Channel> allTvList = new ArrayList<Channel>();
	List<Channel> CCTVList = new ArrayList<Channel>();
	List<Channel> starTvList = new ArrayList<Channel>();
	List<Channel> favTvList = new ArrayList<Channel>();
	List<Channel> localTvList = new ArrayList<Channel>();
	List<Channel> HDTvList = new ArrayList<Channel>();
	List<Channel> otherTvList = new ArrayList<Channel>();

	List<Channel> curChannelList = new ArrayList<Channel>();
	Boolean InitIndex = true;
	int pageItemNum = 15;
	int Index = 0;
	int Num = 0, currentpage = 1;
	int maxPageNum = 0;

	int currentFocusIndex = -1;

	int CurrentChannelId = -1;

	RelativeLayout ksht_conten;

	static SysApplication objApplication;
	TextView[] ksht;

	int[] IndexForChannelId;
	TextView yeshu, dqhxjm;

	EpgListview EpgEventInfo;

	boolean FirstIn = true;

	int chanId = 0;

	private static SysApplication sysApplication;
	private homeReceiver mHomeReceiver;

	private AdPlayer adPlayer;
	private ArrayList<AdItem> adList = new ArrayList<AdItem>();
	private AdStrategy ads;
	private Handler progressHandler;

	private class homeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {

			String strAction = arg1.getAction();

			if (strAction.equals("HOME_PRESSED")) {
				finish();
			}

		}

	}

	public void onDestroy() {
		unregisterReceiver(mHomeReceiver);
		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.huantai_fast_z);
		mHomeReceiver = new homeReceiver();
		registerReceiver(mHomeReceiver, new IntentFilter("HOME_PRESSED"));

		context = FastChangeChannel_z.this;
		objApplication = SysApplication.getInstance();

		shangye_z = (Button) findViewById(R.id.shangye_z);
		xiaye_z = (Button) findViewById(R.id.xiaye_z);
		fanhui_z = (Button) findViewById(R.id.fanhui_z);

		shangye_z.setOnClickListener(ClickedListener);
		xiaye_z.setOnClickListener(ClickedListener);
		fanhui_z.setOnClickListener(ClickedListener);

		yeshu = (TextView) findViewById(R.id.yeshu);
		ksht_xyjm = (TextView) findViewById(R.id.ksht_xyjm);
		ksht_dqjm = (TextView) findViewById(R.id.ksht_dqjm);

		ksht_conten = (RelativeLayout) findViewById(R.id.ksht_conten);
		EpgEventInfo = (EpgListview) findViewById(R.id.epgeventinfo);

		/* yihan advertisement */
		adPlayer = (AdPlayer) findViewById(R.id.adplayer_oklist);
		adPlayer.setDefaultAd(R.drawable.default_img, 1);
		// 生成动态数组，加入数据
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("ItemImage", R.drawable.g1);// 图像资源的ID
		map.put("ItemText", "所有");
		listItem.add(map);

		HashMap<String, Object> map2 = new HashMap<String, Object>();
		map2.put("ItemImage", R.drawable.g2);// 图像资源的ID
		map2.put("ItemText", "高清");
		listItem.add(map2);

		HashMap<String, Object> map3 = new HashMap<String, Object>();
		map3.put("ItemImage", R.drawable.g3);// 图像资源的ID
		map3.put("ItemText", "喜爱");
		listItem.add(map3);

		progressHandler = new Handler();
		/*
		 * HashMap<String, Object> map4 = new HashMap<String, Object>();
		 * map4.put("ItemImage", R.drawable.g4);// 图像资源的ID map4.put("ItemText",
		 * "本地"); listItem.add(map4);
		 */

		/*
		 * HashMap<String, Object> map5 = new HashMap<String, Object>();
		 * map5.put("ItemImage", R.drawable.g5);// 图像资源的ID map5.put("ItemText",
		 * "央视"); listItem.add(map5);
		 * 
		 * HashMap<String, Object> map6 = new HashMap<String, Object>();
		 * map6.put("ItemImage", R.drawable.g6);// 图像资源的ID map6.put("ItemText",
		 * "卫视"); listItem.add(map6);
		 */

		/*
		 * HashMap<String, Object> map7 = new HashMap<String, Object>();
		 * map7.put("ItemImage", R.drawable.g7);// 图像资源的ID map7.put("ItemText",
		 * "专业"); listItem.add(map7);
		 * 
		 * HashMap<String, Object> map8 = new HashMap<String, Object>();
		 * map8.put("ItemImage", R.drawable.g8);// 图像资源的ID map8.put("ItemText",
		 * "广播"); listItem.add(map8);
		 */

		SimpleAdapter listItemAdapter = new SimpleAdapter(this, listItem, R.layout.ksxtlist_item,

		new String[] { "ItemImage", "ItemText" },

		new int[] { R.id.ItemImage, R.id.ItemTitle });

		EpgEventInfo.setAdapter(listItemAdapter);

		// EpgEventInfo.setOnFocusChangeListener(epgEventChangeListener);

		EpgEventInfo.setOnItemSelectedListener(epgEventSelectedListener);

		if (sysApplication == null) {
			sysApplication = SysApplication.getInstance();
			sysApplication.initBookDatabase(context);
		}

		EpgEventInfo.requestFocus();

		getAllTVtype();

		curChannelList = allTvList;
		FirstIn = true;
		display_View();

	}

	private void display_View() {

		Num = curChannelList.size();
		Log.i("zyt", "OKLOIST下的EPG页面列数    " + Num);
		if (Num > 0) {
			maxPageNum = ((Num - 1) / pageItemNum + 1); // 计算包含的页数

		} else {
			maxPageNum = 1;
			return;
		}

		if (FirstIn) {

			int CurrrentProgramInedx = -1;

			Channel channel = objApplication.dvbDatabase.getChannel(objApplication.getLastProgram());

			CurrentChannelId = channel.chanId;

			Log.i(tag, " display_View----  CurrentChannelId------------> " + CurrentChannelId);

			for (int k = 0; k < Num; k++) {

				if (curChannelList.get(k).chanId == CurrentChannelId) {

					CurrrentProgramInedx = k;
					break;
				}

			}

			Log.i(tag, " display_View----  CurrrentProgramInedx------------> " + CurrrentProgramInedx);

			if (CurrrentProgramInedx > 0)
				currentpage = CurrrentProgramInedx / pageItemNum + 1;

			currentFocusIndex = CurrrentProgramInedx - (CurrrentProgramInedx / pageItemNum) * pageItemNum;

			Index = currentpage * pageItemNum - pageItemNum;

			Log.i(tag, " display_View----  currentFocusIndex------------  currentpage>---->Index " + currentFocusIndex
					+ "   " + currentpage + "   " + Index);

		}

		yeshu.setText(currentpage + "/" + maxPageNum);
		ksht_conten.removeAllViews();

		int startIndex = 0;

		startIndex = currentpage * pageItemNum - pageItemNum;

		LayoutInflater inflater = getLayoutInflater();

		RelativeLayout kshtcontent = (RelativeLayout) inflater.inflate(R.layout.kshtcontent, null);

		ksht = new TextView[15];

		IndexForChannelId = new int[15];

		ksht[0] = (TextView) kshtcontent.findViewById(R.id.ksht1z);
		ksht[1] = (TextView) kshtcontent.findViewById(R.id.ksht2z);
		ksht[2] = (TextView) kshtcontent.findViewById(R.id.ksht3z);
		ksht[3] = (TextView) kshtcontent.findViewById(R.id.ksht4z);
		ksht[4] = (TextView) kshtcontent.findViewById(R.id.ksht5z);
		ksht[5] = (TextView) kshtcontent.findViewById(R.id.ksht6z);
		ksht[6] = (TextView) kshtcontent.findViewById(R.id.ksht7z);
		ksht[7] = (TextView) kshtcontent.findViewById(R.id.ksht8z);
		ksht[8] = (TextView) kshtcontent.findViewById(R.id.ksht9z);
		ksht[9] = (TextView) kshtcontent.findViewById(R.id.ksht10z);
		ksht[10] = (TextView) kshtcontent.findViewById(R.id.ksht11z);
		ksht[11] = (TextView) kshtcontent.findViewById(R.id.ksht12z);
		ksht[12] = (TextView) kshtcontent.findViewById(R.id.ksht13z);
		ksht[13] = (TextView) kshtcontent.findViewById(R.id.ksht14z);
		ksht[14] = (TextView) kshtcontent.findViewById(R.id.ksht15z);

		for (int position = 0; position < ksht.length; position++) {

			Log.i("Huantai_fast_z", "Index-------->" + Index);
			if (Index >= Num) // 已经添加完毕
			{
				ksht[position].setVisibility(View.INVISIBLE);

			} else {

				ksht[position].setVisibility(View.VISIBLE);

				chanId = curChannelList.get(startIndex).chanId;

				ksht[position].setId(position);
				// ksht[position].getPositionID().setId(position);

				String channelNumberholder = null;

				if (curChannelList.get(startIndex).logicNo < 10) {
					channelNumberholder = "00" + curChannelList.get(startIndex).logicNo;
				} else if (curChannelList.get(startIndex).logicNo < 100) {
					channelNumberholder = "0" + curChannelList.get(startIndex).logicNo;
				} else {
					channelNumberholder = "" + curChannelList.get(startIndex).logicNo;
				}

				ksht[position].setText("      " + channelNumberholder + "      " + curChannelList.get(startIndex).name);
				// ksht[position].setOnClickListener(ClickedListener);

				// if(Index==Num-1||Index==Num-1)
				// ksht[position].setNextFocusDownId(R.id.shangye_z);

				ksht[position].setFocusable(true);
				ksht[position].setOnFocusChangeListener(kshtFocusChangeListener);

				ksht[position].setOnClickListener(kshtItemClickedListener);

				IndexForChannelId[position] = Index;
				Index++;
				startIndex++;
			}
		}

		ksht_conten.addView(kshtcontent);

		if (FirstIn) {

			Log.i("Huantai_fast_z", "FirstIn--------> currentFocusIndex--->" + FirstIn + "  " + currentFocusIndex);
			// ksht_conten.requestFocus();
			ksht[currentFocusIndex].requestFocus();

		}

	}

	public void getYiHanOKLISTAD(int channelId) {
		Channel curChannel = DVB.getManager().getChannelDBInstance().getChannel(channelId);
		ads = new AdStrategy(sysApplication, Advertise_Constant.LIVEPLAY_ID_OKLIST, Common.getParams(
				curChannel.serviceId + "", Advertise_Constant.LIVEPLAY_ID_OKLIST, Advertise_Constant.TEMP_IP_ADDRESS));
		// 请求广告决策
		ads.request();

		adPlayer.setIAdPlayerCallbackListener(new IAdPlayerCallbackListener() {

			@Override
			public void onPrepared(AdPlayer v) {
				// 广告内容准备就绪后播放
				Log.e("FSLog", "视频准备完成------------onPrepared");
				// adPlayer.play("");
			}

			@Override
			public void onError(AdPlayer v, int errorCode, String message) {
				// 广告控件处理中发生错误抛出后处理代码
				Log.e("FSLog", "播放异常------------onError");
			}

			@Override
			public void onCompleted(AdPlayer v) {
				// 广告控件中广告内容播放完毕后代码
				Log.e("FSLog", "广告播放结束-----------onCompleted");
				adPlayer.stop();
			}
		});

		ads.setOnResponseListener(new IAdStrategyResponseListener() {

			@Override
			public void onResponse(ArrayList<AdItem> items) {
				// 获得广告策略结果后处理代码
				Log.i("zyt", "AdStrategy-------------------------onResponse");
				Log.i("zyt", "AdStrategy-------------------------onResponse  之前广告列表的长度" + items.size());
				if (items != null && items.size() > 0) {
					AdItem temp = items.get(0);
					// Log.i("zyt", " " + items.size());
					if (temp.type == AdItem.TYPE_SELF_PLAY) {// 开机广告
					} else {// 非开机广告，获取插播广告的相对位置
						adList = items;
						if (temp.type == AdItem.TYPE_SELF_PLAY) {// 开机广告
						} else {// 非开机广告，获取插播广告的相对位置
							// 播放图片
							playAd(items.get(0).Id);
						}
					}
				} else {
					Log.i("zyt", "AdStrategy---------------onResponse:items is null or size is 0");
				}
			}

			@Override
			public void onError(int errorCode, String message) {
				// 获得广告策略结果出错
				Log.i("zyt", "AdStrategy---------------onError");
			}
		});

		// 将广告策略设置到广告控件中
		adPlayer.setAdStrategy(ads);
	}

	public void playAd(String id) {
		adPlayer.setVisibility(View.VISIBLE);
		adPlayer.play(id);
	}

	private OnClickListener kshtItemClickedListener = new OnClickListener() {
		@Override
		public void onClick(View view) {

			objApplication.playChannel(curChannelList.get(IndexForChannelId[view.getId()]).chanId, true);

			Intent intent = new Intent();
			intent.setAction("showbanner");
			context.sendBroadcast(intent);

			finish();
		}
	};
	private OnFocusChangeListener kshtFocusChangeListener = new OnFocusChangeListener() {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {

			if (hasFocus) {

				currentSelect = ((TextView) v).getId();

				// ksht[currentSelect].ksht_number.requestFocus();

				Log.i(tag, "currentSelect-------" + currentSelect);

				channel = sysApplication.dvbDatabase.getChannel(v.getId());

				if (OKLISTRunnable != null) {
					progressHandler.removeCallbacks(OKLISTRunnable);
				}
				progressHandler.post(OKLISTRunnable);
				PF_enventName_P = context.getResources().getString(R.string.noprogrampfinfo);
				PF_enventName_F = context.getResources().getString(R.string.noprogrampfinfo);
				DVB_EPG_PF pfInfo = DVB.getManager().getEpgInstance().getPfInfo(channel);

				if (pfInfo != null) {
					if (pfInfo.hasPresent()) {

						PF_enventName_P = pfInfo.getPresent().getName();
					}
					if (pfInfo.hasFollowing()) {

						PF_enventName_F = pfInfo.getFollowing().getName();
					}

				}

				ksht_xyjm.setText(PF_enventName_F);
				ksht_dqjm.setText(PF_enventName_P);

			}
		}
	};

	public Runnable OKLISTRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			if (channel != null) {
				getYiHanOKLISTAD(channel.chanId);
			} else {
				progressHandler.postDelayed(OKLISTRunnable, 500);
			}

		}
	};
	private OnFocusChangeListener epgEventChangeListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View arg0, boolean arg1) {

		}
	};

	private void getAllTVtype() {
		// fill all type tv

		// clear all tv type;
		allTvList.clear();
		// CCTVList.clear();
		// starTvList.clear();
		favTvList.clear();
		// localTvList.clear();
		HDTvList.clear();
		// otherTvList.clear();
		Channel[] dvbChannels = objApplication.dvbDatabase.getChannelsAllSC();

		if (dvbChannels != null) {

			for (Channel dvbChannel : dvbChannels) {
				allTvList.add(dvbChannel);

				if (dvbChannel.favorite == 1) {
					favTvList.add(dvbChannel);
				}

				/*
				 * String regExCCTV; regExCCTV =
				 * getResources().getString(R.string.zhongyang);
				 * java.util.regex.Pattern pattern = java.util.regex.Pattern
				 * .compile("CCTV|" + regExCCTV); java.util.regex.Matcher
				 * matcher = pattern .matcher(dvbChannel.name); boolean
				 * classBytype = matcher.find(); if (classBytype) {
				 * CCTVList.add(dvbChannel); } String regExStar; regExStar =
				 * getResources().getString(R.string.weishi);
				 * java.util.regex.Pattern patternStar = java.util.regex.Pattern
				 * .compile(".*" + regExStar + "$"); java.util.regex.Matcher
				 * matcherStar = patternStar .matcher(dvbChannel.name); boolean
				 * classBytypeStar = matcherStar.matches(); if (classBytypeStar)
				 * { starTvList.add(dvbChannel); }
				 */

				/*
				 * String regExLocal = "CDTV|SCTV|" +
				 * getResources().getString(R.string.rongcheng) + "|" +
				 * getResources().getString(R.string.jingniu) + "|" +
				 * getResources().getString(R.string.qingyang) + "|" +
				 * getResources().getString(R.string.wuhou) + "|" +
				 * getResources().getString(R.string.chenghua) + "|" +
				 * getResources().getString(R.string.jinjiang) + "|" +
				 * getResources().getString(R.string.chengdu) + "|" +
				 * getResources().getString(R.string.sichuan);
				 * java.util.regex.Pattern patternLocal =
				 * java.util.regex.Pattern .compile(regExLocal);
				 * java.util.regex.Matcher matcherLocal = patternLocal
				 * .matcher(dvbChannel.name); boolean classBytypeLocal =
				 * matcherLocal.find(); if (classBytypeLocal) {
				 * localTvList.add(dvbChannel); }
				 */

				String regExHD = getResources().getString(R.string.hd_dtv) + "|"
						+ getResources().getString(R.string.xinyuan_hdtv1) + "|"
						+ getResources().getString(R.string.xinyuan_hdtv2) + "|"
						+ getResources().getString(R.string.xinyuan_hdtv3) + "|"
						+ getResources().getString(R.string.xinyuan_hdtv4);
				java.util.regex.Pattern patternHD = java.util.regex.Pattern.compile("3D|" + regExHD + "|.*HD$");
				java.util.regex.Matcher matcherHD = patternHD.matcher(dvbChannel.name);
				boolean classBytypeHD = matcherHD.find();
				if (classBytypeHD) {
					HDTvList.add(dvbChannel);
				}

				/*
				 * String regExOther = "CDTV|SCTV|CCTV|" +
				 * getResources().getString(R.string.weishi) + "|" +
				 * getResources().getString(R.string.rongcheng) + "|" +
				 * getResources().getString(R.string.qingyang) + "|" +
				 * getResources().getString(R.string.wuhou) + "|" +
				 * getResources().getString(R.string.chenghua) + "|" +
				 * getResources().getString(R.string.jinjiang) + "|" +
				 * getResources().getString(R.string.chengdu) + "|" +
				 * getResources().getString(R.string.sichuan);
				 * java.util.regex.Pattern patternOther =
				 * java.util.regex.Pattern .compile(regExOther);
				 * java.util.regex.Matcher matcherOther = patternOther
				 * .matcher(dvbChannel.name); boolean classBytypeOther =
				 * matcherOther.find(); if (!classBytypeOther) {
				 * otherTvList.add(dvbChannel); }
				 */
			}
		}
	}

	public void showChannelList(int channelType) {

		switch (channelType) {
		case 1:
			// channelListview.setAdapter(new ChannelListAdapter(context,
			// allTvList));
			curChannelList = allTvList;
			// chanListTitleButton.setText(tvType[0]);
			break;
		case 5:
			// channelListview
			// .setAdapter(new ChannelListAdapter(context,
			// CCTVList));
			curChannelList = CCTVList;
			// chanListTitleButton.setText(tvType[1]);
			break;
		case 6:

			curChannelList = starTvList;
			// channelListview.setAdapter(new ChannelListAdapter(context,
			// starTvList));
			break;
		case 4:

			curChannelList = localTvList;
			// channelListview.setAdapter(new ChannelListAdapter(context,
			// localTvList));
			break;
		case 2:

			curChannelList = HDTvList;
			// channelListview
			// .setAdapter(new ChannelListAdapter(context,
			// HDTvList));
			break;
		case 3:

			curChannelList = favTvList;
			// channelListview.setAdapter(new ChannelListAdapter(context,
			// favTvList));
			break;
		case 7:

			// curChannelList = otherTvList;
			// channelListview.setAdapter(new ChannelListAdapter(context,
			// otherTvList));
			break;

		case 8:

			// curChannelList = otherTvList;
			// channelListview.setAdapter(new ChannelListAdapter(context,
			// otherTvList));
			break;

		default:
			break;
		}
	}

	private OnClickListener ClickedListener = new OnClickListener() {

		@Override
		public void onClick(View view) {

			switch (view.getId()) {

			case R.id.xiaye_z:

				if (currentpage < maxPageNum)
					currentpage++;
				else {
					currentpage = maxPageNum;
					return;
				}

				Log.i(tag, "ClickedListener----->xiaye  currentpage---- " + currentpage);

				display_View();
				break;
			case R.id.shangye_z:

				if (currentpage > 1) {

					if (currentpage == maxPageNum) {

						if (Num % pageItemNum == 0)
							Index = Index - 30;
						else
							Index = Index - Num % pageItemNum - 15;

					} else {

						Index = Index - 15 - 15;

					}

					Log.i(tag, "shangye_z-----  Index----" + Index);
					currentpage--;
				} else {

					currentpage = 1;
					return;
				}

				Log.i(tag, "ClickedListener----->shangye  currentpage---- " + currentpage);

				display_View();

				break;
			case R.id.fanhui_z:

				finish();

				break;

			}

		}

	};

	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {

		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_DOWN: {

			return true;
		}
		case KeyEvent.KEYCODE_VOLUME_UP: {

			return true;
		}
		case KeyEvent.KEYCODE_VOLUME_MUTE: {
			return true;
		}

		case KeyEvent.KEYCODE_DPAD_UP:
			if (EpgEventInfo.hasFocus()) {

				EpgEventInfo.requestFocus();

				return true;
			}
			break;

		case KeyEvent.KEYCODE_DPAD_DOWN:

			if (EpgEventInfo.hasFocus()) {

				EpgEventInfo.requestFocus();

				return true;
			}
			break;
		case KeyEvent.KEYCODE_BACK:

			if (EpgEventInfo.hasFocus()) {
				finish();

			} else if (shangye_z.hasFocus() || xiaye_z.hasFocus() || fanhui_z.hasFocus() || ksht_conten.hasFocus()) {

				InitIndex = false;
				EpgEventInfo.setFocusable(true);
				EpgEventInfo.requestFocus();
				ksht_xyjm.setText(null);
				ksht_dqjm.setText(null);
			}
			break;

		case KeyEvent.KEYCODE_DPAD_LEFT:

			if (ksht_conten.hasFocus()) {

				if (currentSelect == 0 || currentSelect == 3 || currentSelect == 6 || currentSelect == 9
						|| currentSelect == 12) {

					Log.i(tag, "onkey down   KEYCODE_DPAD_LEFT111------- display_View   		currentpage  "
							+ currentpage);

					if (currentpage > 1) {

						if (currentpage == maxPageNum) {

							if (Num % pageItemNum == 0)
								Index = Index - 30;
							else
								Index = Index - Num % pageItemNum - 15;

							Log.i(tag, "onkey down   KEYCODE_DPAD_LEFT------- display_View  Index" + Index);

						} else {

							Index = Index - 15 - 15;

						}
						currentpage--;

					} else {

						currentpage = 1;

						return true;

					}

					Log.i(tag, "onkey down   KEYCODE_DPAD_LEFT------- display_View");
					display_View();

					ksht_conten.requestFocus();
					return true;

				}

			} else if (shangye_z.hasFocus()) {

				shangye_z.requestFocus();
				return true;
			}
			break;

		case KeyEvent.KEYCODE_DPAD_RIGHT:

			if (ksht_conten.hasFocus()) {

				if (currentSelect == 2 || currentSelect == 5 || currentSelect == 8 || currentSelect == 11
						|| currentSelect == 14) {

					if (currentpage < maxPageNum)
						currentpage++;
					else {
						currentpage = maxPageNum;
						return true;
					}

					Log.i(tag, "onkey down   KEYCODE_DPAD_right------- display_View");
					display_View();
					ksht_conten.requestFocus();

					return true;
				}

			} else if (fanhui_z.hasFocus()) {

				fanhui_z.requestFocus();
				return true;
			} else if (EpgEventInfo.hasFocus() && Index <= 0) {

				EpgEventInfo.requestFocus();
				return true;
			} else if (EpgEventInfo.hasFocus() && Index > 0) {

				ksht_conten.requestFocus();
				EpgEventInfo.setFocusable(false);
				return true;

			}

			break;

		}

		return false;
	};

	private OnItemSelectedListener epgEventSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

			if (arg1 != null) {
				int type = -1;

				ImageView ItemImage = (ImageView) arg1.findViewById(R.id.ItemImage);

				TextView textView = (TextView) arg1.findViewById(R.id.ItemTitle);
				String string = (String) textView.getText();

				if (string.equals("所有")) {
					type = 1;
				} else if (string.equals("高清")) {
					type = 2;
				} else if (string.equals("喜爱")) {
					type = 3;
				} else if (string.equals("央视")) {
					type = 5;
				} else if (string.equals("卫视")) {
					type = 6;
				}

				/*
				 * else if (string.equals("专业")) { type = 7; } else if
				 * (string.equals("广播")) { type = 8; }
				 */

				Log.i("Huantai_fast_z", "zzzzzz--------------------------------------TYPE＝ ! " + type + "   " + string);

				if (FirstIn) {

					FirstIn = false;
				} else {

					Index = 0;

					showChannelList(type);
					currentpage = 1;
					display_View();
				}

			}

		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}

	};
}