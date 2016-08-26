package com.changhong.app.utils;

import com.changhong.dvb.Channel;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class DtvProvider extends ContentProvider {

	private SQLiteDatabase db;
	private String dbPath = "data/changhong/dvb/chdvb.db";
	private static final int DVB = 1;
	private static final int DVB_ID = 2;
	private static UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		MATCHER.addURI("com.changhong.dtvprovider", "channels", DVB);
		MATCHER.addURI("com.changhong.dtvprovider", "channels/#", DVB_ID);
	}

	public DtvProvider(){
		db = SQLiteDatabase.openOrCreateDatabase(dbPath, null);
	}
	
	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		db = SQLiteDatabase.openOrCreateDatabase(dbPath, null);
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		Cursor cursor = null;
		switch (MATCHER.match(uri)) {
		case DVB:

			cursor = db.query("channels", null, null, null, null, null,
					"service_id");
			break;

		case DVB_ID:
			String service_id = uri.getPathSegments().get(1);
			cursor = db.query("channels", null, "service_id=?",
					new String[] { service_id }, null, null, null);
			break;
		default:
			break;
		}
		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		switch (MATCHER.match(uri)) {
		case DVB:
			return "vnd.android.cursor.dir/vnd.com.changhong.provider.dtvprovider.channels";

		case DVB_ID:
			return "vnd.android.cursor.item/vnd.com.changhong.provider.dtvprovider.channels";

		default:
			throw new IllegalArgumentException("Unknown URL");
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		int number = 0;
		switch (MATCHER.match(uri)) {

		case DVB_ID:
			String service_id = uri.getPathSegments().get(1);
			number = db.delete("channels", "service_id=?",
					new String[] { service_id });
			break;

		default:
			break;
		}
		return number;
	}

	public void sortTwoChannelsByService(Uri uri, int oldServiceId,
			int newServiceId) {
		switch (MATCHER.match(uri)) {
		case DVB:
			Cursor oldCursor = db.query("channels", null, "service_id=?",
					new String[] { String.valueOf(oldServiceId) }, null, null,
					null);
			Cursor newCursor = db.query("channels", null, "service_id=?",
					new String[] { String.valueOf(newServiceId) }, null, null,
					null);
			ContentValues values = new ContentValues();
			values.put("id", newCursor.getInt(0));
			values.put("name", newCursor.getString(1));
			values.put("service_id", newCursor.getInt(2));
			values.put("logic_no", newCursor.getInt(3));
			values.put("sort_id", newCursor.getInt(4));
			values.put("video_pid", newCursor.getInt(5));
			values.put("video_stream_type", newCursor.getInt(6));
			values.put("audio_pid", newCursor.getInt(7));
			values.put("audio_stream_type", newCursor.getInt(8));
			values.put("pmt_pid", newCursor.getInt(9));
			values.put("pcr_pid", newCursor.getInt(10));
			values.put("ts_id", newCursor.getInt(11));
			values.put("ori_network_id", newCursor.getInt(12));
			values.put("signal_type", newCursor.getInt(13));
			values.put("frequency_khz", newCursor.getInt(14));
			values.put("symbol_rate_kbps", newCursor.getInt(15));
			values.put("modulation", newCursor.getInt(16));
			values.put("spectrum", newCursor.getInt(17));
			values.put("bandwidth_mhz", newCursor.getInt(18));
			values.put("teletext", newCursor.getString(19));
			values.put("subtitle", newCursor.getString(20));
			values.put("favorite", newCursor.getInt(21));
			values.put("skip", newCursor.getInt(22));
			values.put("lock", newCursor.getInt(23));
			values.put("carrier_ex_cable", newCursor.getString(24));
			values.put("carrier_ex_ter", newCursor.getString(25));
			values.put("carrier_ex_sat", newCursor.getString(26));
			values.put("extend", newCursor.getString(27));
			ContentValues values1 = new ContentValues();
			values1.put("id", oldCursor.getInt(0));
			values1.put("name", oldCursor.getString(1));
			values1.put("service_id", oldCursor.getInt(2));
			values1.put("logic_no", oldCursor.getInt(3));
			values1.put("sort_id", oldCursor.getInt(4));
			values1.put("video_pid", oldCursor.getInt(5));
			values1.put("video_stream_type", oldCursor.getInt(6));
			values1.put("audio_pid", oldCursor.getInt(7));
			values1.put("audio_stream_type", oldCursor.getInt(8));
			values1.put("pmt_pid", oldCursor.getInt(9));
			values1.put("pcr_pid", oldCursor.getInt(10));
			values1.put("ts_id", oldCursor.getInt(11));
			values1.put("ori_network_id", oldCursor.getInt(12));
			values1.put("signal_type", oldCursor.getInt(13));
			values1.put("frequency_khz", oldCursor.getInt(14));
			values1.put("symbol_rate_kbps", oldCursor.getInt(15));
			values1.put("modulation", oldCursor.getInt(16));
			values1.put("spectrum", oldCursor.getInt(17));
			values1.put("bandwidth_mhz", oldCursor.getInt(18));
			values1.put("teletext", oldCursor.getString(19));
			values1.put("subtitle", oldCursor.getString(20));
			values1.put("favorite", oldCursor.getInt(21));
			values1.put("skip", oldCursor.getInt(22));
			values1.put("lock", oldCursor.getInt(23));
			values1.put("carrier_ex_cable", oldCursor.getString(24));
			values1.put("carrier_ex_ter", oldCursor.getString(25));
			values1.put("carrier_ex_sat", oldCursor.getString(26));
			values1.put("extend", oldCursor.getString(27));
			db.update("channels", values, "service_id=?",
					new String[] { String.valueOf(oldServiceId) });
			db.update("channels", values1, "service_id=?",
					new String[] { String.valueOf(newServiceId) });
			break;

		default:
			break;
		}
	}

	public void sortAllChannelsByServiceId(Uri uri) {
		switch (MATCHER.match(uri)) {
		case DVB:
			// 对数据库进行排序
			// String serviceId = uri.getPathSegments().get(1);
			Cursor cursor = db.query("channels", null, null, null, null, null,
					"service_id asc");
			if (cursor != null) {		
				Log.i("yangtong","11111111111");
			//	db.execSQL("delete from channels");
				int i=0;
				while (cursor.moveToNext()) {
					i++;
					ContentValues values = new ContentValues();
				//	values.put("id", cursor.getInt(0));
					values.put("name", cursor.getString(1));
					values.put("service_id", cursor.getInt(2));
					values.put("logic_no", cursor.getInt(3));
					values.put("sort_id", cursor.getInt(4));
					values.put("video_pid", cursor.getInt(5));
					values.put("video_stream_type", cursor.getInt(6));
					values.put("audio_pid", cursor.getInt(7));
					values.put("audio_stream_type", cursor.getInt(8));
					values.put("pmt_pid", cursor.getInt(9));
					values.put("pcr_pid", cursor.getInt(10));
					values.put("ts_id", cursor.getInt(11));
					values.put("ori_network_id", cursor.getInt(12));
					values.put("signal_type", cursor.getInt(13));
					values.put("frequency_khz", cursor.getInt(14));
					values.put("symbol_rate_kbps", cursor.getInt(15));
					values.put("modulation", cursor.getInt(16));
					values.put("spectrum", cursor.getInt(17));
					values.put("bandwidth_mhz", cursor.getInt(18));
					values.put("teletext", cursor.getString(19));
					values.put("subtitle", cursor.getString(20));
					values.put("favorite", cursor.getInt(21));
					values.put("skip", cursor.getInt(22));
					values.put("lock", cursor.getInt(23));
					values.put("carrier_ex_cable", cursor.getString(24));
					values.put("carrier_ex_ter", cursor.getString(25));
					values.put("carrier_ex_sat", cursor.getString(26));
					values.put("extend", cursor.getString(27));
				//	db.insert("channels", null, values);
					db.update("channels", values, "id=?", new String[]{String.valueOf(i)});
				}
			}
			break;

		default:
			break;
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
