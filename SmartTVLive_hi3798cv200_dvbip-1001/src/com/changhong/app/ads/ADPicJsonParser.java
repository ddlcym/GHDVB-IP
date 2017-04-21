package com.changhong.app.ads;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.changhong.app.ads.ADPicData.AD_Content;
import com.changhong.app.ads.ADPicData.Time_List;
import com.changhong.app.book.BookInfo;
import com.changhong.app.dtv.P;
import com.changhong.app.utils.OpJsonFile;
import com.changhong.dvb.Channel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

public class ADPicJsonParser {
	private Thread parseThread;
	private static ADPicJsonParser adPicJsonParser;
	private ADPicData ad_bannel, ad_vol_bar, ad_chan_list, curAdPicData;
	public static final String TAG = "ADVPIC";
	private static final String advFile_path = "/private/adv";

	private ADPicJsonParser() {
		if (parseThread == null) {
			parseThread = new Thread() {
				@Override
				public void run() {
					P.d(TAG, "parse ad data thread Start  !");
					do_ParseADJsonFile();
					P.d(TAG, "parse ad data thread end  !");
				}
			};
			parseThread.start();
		}
	}

	protected void do_ParseADJsonFile() {
		File AdvDir = new File(advFile_path);
		if (AdvDir.exists()) {
			if (!AdvDir.isDirectory())
				return;
		} else
			return;

		int filenumber = 0;

		File[] list = AdvDir.listFiles();
		for (File file : list) {
			if (file.isDirectory()) {
				File[] list2 = file.listFiles();
				String strDirName = file.getName();
				String path1 = advFile_path + "/" + strDirName;
				if (list2.length > 0) {
					curAdPicData = checkAdOwner(strDirName);
					if (curAdPicData == null)
						continue;
					curAdPicData.filenameArray.clear();
					// curAdPicData.bmpall.clear();
				}
				for (File file2 : list2) {
					if (file2.isFile()) {
						String fileallname = path1 + "/" + file2.getName();
						Log.d(TAG, "FILE NAME:" + fileallname);
						if (file2.getName().equalsIgnoreCase("json")) {
							parseADJson(curAdPicData, fileallname);
						} else {
							curAdPicData.filenameArray.add(fileallname);
							// Bitmap bmp=
							// BitmapFactory.decodeFile(fileallname);
							// curAdPicData.bmpall.add(bmp);
						}
					} else {
						Log.d(TAG, "invalue directory!!!");
					}
				}
			} else {
				Log.d(TAG, "invalue file!!!");
			}
		}
	}

	private void parseADJson(ADPicData curAdPicData, String fileallname) {
		int groups_idx;
		int groups_ad_idx;
		int channel_ad_idx;

		if (curAdPicData == null || fileallname == null)
			return;

		File jsonFile = new File(fileallname);
		if (jsonFile.exists() && jsonFile.isFile()) {
			JSONObject jsonObject = OpJsonFile.readJSONObj(fileallname);

			if (jsonObject == null) {
				Log.e(TAG, "readJSONObj->" + fileallname + " fail!!!");
				return;
			}

			Log.i(TAG,
					curAdPicData.jsonType + "-json content>>>"
							+ jsonObject.toString());

			JSONObject def_ad = getJsonObject(jsonObject, "default");
			if (def_ad != null) {
				curAdPicData.obj_Ad_default = curAdPicData.newADContent(0, 0,
						0, 0, getJsonObjectString(def_ad, "type"),
						getJsonObjectString(def_ad, "url"), "1", null);
			} else {
				Log.d(TAG, "read default-> fail!!!");
			}
			JSONArray groups = getJsonObjectArray(jsonObject, "groups");
			if (groups != null) {
				for (groups_idx = 0; groups_idx < groups.length(); groups_idx++) {
					JSONObject extraJson;
					try {
						extraJson = (JSONObject) groups.get(groups_idx);
						if (extraJson != null) {
							JSONArray group_list = getJsonObjectArray(
									extraJson, "group_list");
							if (group_list != null) {
								List<String> list_user = new ArrayList<String>();
								for (int n = 0; n < groups.length(); n++) {
									Log.e(TAG, "read group_list-> "
											+ (String) group_list.get(n));
									list_user.add((String) group_list.get(n));
								}
								curAdPicData.addGroupsList(curAdPicData
										.newGroupsList(groups_idx, list_user));
							} else {
								Log.e(TAG, "read group_list-> fail!!!");
							}
							JSONArray groups_ad = getJsonObjectArray(extraJson,
									"groups_ad");
							if (groups_ad != null) {
								for (groups_ad_idx = 0; groups_ad_idx < groups
										.length(); groups_ad_idx++) {
									JSONObject perGroups_ad = (JSONObject) groups_ad
											.get(groups_ad_idx);
									if (perGroups_ad != null) {
										JSONArray chan_list = getJsonObjectArray(
												perGroups_ad, "channel_list");
										if (chan_list != null) {
											List<String> list_channel_list = new ArrayList<String>();
											for (int k = 0; k < chan_list
													.length(); k++) {
												Log.e(TAG,
														"read channel_list-> "
																+ (String) chan_list
																		.get(k));
												list_channel_list
														.add((String) chan_list
																.get(k));
											}
											curAdPicData
													.addChannelList(curAdPicData
															.newChannelList(
																	groups_idx,
																	groups_ad_idx,
																	list_channel_list));
										} else {
											Log.e(TAG,
													"read channel_list-> fail!!!");
										}
										JSONArray chan_ad = getJsonObjectArray(
												perGroups_ad, "channel_ad");
										if (chan_ad != null) {
											for (channel_ad_idx = 0; channel_ad_idx < chan_ad
													.length(); channel_ad_idx++) {
												List<Time_List> list_time_list = new ArrayList<Time_List>();
												JSONObject perChannel_ad = (JSONObject) chan_ad
														.get(channel_ad_idx);
												if (perChannel_ad != null) {
													JSONArray time_list = getJsonObjectArray(
															perChannel_ad,
															"time_list");
													if (time_list != null) {
														for (int l = 0; l < time_list
																.length(); l++) {
															JSONObject perTime_list = (JSONObject) time_list
																	.get(l);
															list_time_list
																	.add(curAdPicData
																			.newTimeList(
																					getJsonObjectString(
																							perTime_list,
																							"start_time"),
																					getJsonObjectString(
																							perTime_list,
																							"end_time")));
														}
													} else {
														Log.e(TAG,
																"read time_list-> fail!!!");
													}
													JSONArray ad_list = getJsonObjectArray(
															perChannel_ad,
															"ad_list");
													if (ad_list != null) {
														for (int t = 0; t < ad_list
																.length(); t++) {
															JSONObject perAd_list = (JSONObject) ad_list
																	.get(t);
															curAdPicData
																	.addADContent(curAdPicData
																			.newADContent(
																					groups_idx,
																					groups_ad_idx,
																					channel_ad_idx,
																					t,
																					getJsonObjectString(
																							perAd_list,
																							"type"),
																					getJsonObjectString(
																							perAd_list,
																							"url"),
																					getJsonObjectString(
																							perAd_list,
																							"default_ad"),
																					list_time_list));

														}
													} else {
														Log.e(TAG,
																"read ad_list-> fail!!!");
													}
												}
											}
										} else {
											Log.e(TAG,
													"read channel_ad-> fail!!!");
										}
									}									
								}
							} else {
								Log.e(TAG, "read groups_ad-> fail!!!");
							}
						}

					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			} else {
				Log.e(TAG, "read groups-> fail!!!");
			}
		}
	}

	private ADPicData checkAdOwner(String strDirName) {
		ADPicData localObj = null;
		if (strDirName != null) {
			if (strDirName.startsWith("4_")) {
				ad_bannel = new ADPicData(4);
				localObj = ad_bannel;
			} else if (strDirName.startsWith("5_")) {
				ad_vol_bar = new ADPicData(5);
				localObj = ad_vol_bar;
			} else if (strDirName.startsWith("6_")) {
				ad_chan_list = new ADPicData(6);
				localObj = ad_chan_list;
			}
			return localObj;
		}
		return null;
	}

	public static ADPicJsonParser getADPicJsonParserInstance() {
		if (adPicJsonParser == null) {
			adPicJsonParser = new ADPicJsonParser();
		}
		return adPicJsonParser;
	}

	public static Bitmap getBitMap(String file) {
		Bitmap bitmap = null;
		try {
			File myFile = new File(file);
			InputStream is = new FileInputStream(myFile);
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bitmap;
	}

	public Bitmap getBitMap(int pic_pos,String channel_id,String curTime) {
		String filename = getAdFile(pic_pos,channel_id,curTime);
		return getBitMap(filename);
	}

	private String getAdFile(int pic_pos,String channel_id,String curTime) {
		ADPicData localObj = null;
		switch(pic_pos){
		case 0:
			localObj = ad_bannel;
			break;
		case 1:
			localObj = ad_vol_bar;
			break;
		case 2:
			localObj = ad_chan_list;
			break;
		default:
			return null;
		}
	
		//localObj.list_ad_Content;
		return null;
	}

	// =================================base function =====================================

	private static String getJsonObjectString(JSONObject jsonObj, String key) {

		String rValue = "";
		try {
			rValue = jsonObj.getString(key);
		} catch (JSONException ex) {
			ex.printStackTrace();
			Log.e(TAG, "get String :" + key);
		}
		return rValue;
	}

	private static JSONArray getJsonObjectArray(JSONObject jsonObj, String key) {

		JSONArray rValue = null;
		try {
			rValue = jsonObj.getJSONArray(key);
		} catch (JSONException ex) {
			ex.printStackTrace();
			Log.e(TAG, "get array:" + key);
		}
		return rValue;
	}

	private static int getJsonObjInt(JSONObject json, String key) {
		int i = -1;
		try {
			i = json.getInt(key);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, "get Int:" + key);
		}
		return i;
	}

	private static JSONObject getJsonObject(JSONObject json, String key) {
		JSONObject obj = null;
		try {
			obj = json.getJSONObject(key);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, "get Obj:" + key);
		}
		return obj;
	}
}
