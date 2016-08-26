package com.changhong.app.dtv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.R.integer;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.changhong.dvb.CA;
import com.changhong.dvb.CA_Mail_Head;
import com.changhong.dvb.DVB;
import com.changhong.dvb.DVBManager;
import com.changhong.dvb.ProtoMessage.DVB_CA_TYPE;

public class Email_z extends Activity {
	SimpleAdapter adapter = null;
	EpgListview yj_conten_z;
	TextView yj_prompt_z;
	Button yj_remove_z;
	Context context = null;

	int EmailNumber = 0;

	int CurrentItemPosition = -1;
	private DVBManager dvbManager;
	private List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
	private CA thisCa;
	
	private homeReceiver mHomeReceiver;
	
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

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.yj_z);
		
		mHomeReceiver = new homeReceiver();
		registerReceiver(mHomeReceiver, new IntentFilter("HOME_PRESSED"));

		dvbManager = DVB.getManager();
		thisCa = dvbManager.getCaInstance();

		context = Email_z.this;

		InitView();

	}

	private void InitView() {

		yj_conten_z = (EpgListview) findViewById(R.id.yj_conten_z);
		yj_prompt_z = (TextView) findViewById(R.id.yj_prompt_z);
		yj_remove_z = (Button) findViewById(R.id.yj_remove_z);

		yj_remove_z.setOnClickListener(OnClickedListener);

		yj_prompt_z.setText("按“确认”查看");

	}

	private OnClickListener OnClickedListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			

			HashMap<String, Object> map = list.get(CurrentItemPosition);
			
		int mailId = Integer.parseInt((String) map.get("number"));
		Log.i("Email_z", " removeMail ----->mailId--->" + mailId);
		thisCa.removeMail(mailId);
		
			list.remove(CurrentItemPosition);

			if (list.size() <= 0) {

				yj_remove_z.setVisibility(view.INVISIBLE);

			}

			adapter.notifyDataSetChanged();
		}

	};

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	
	
	/*private List<HashMap<String, Object>> getEmailData() {

		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		
		for(int k=0;k<90;k++){

		HashMap<String, Object> hashMap = new HashMap<String, Object>();

		hashMap.put("number", +k+"     " +"11111111111111111111111111111");

		hashMap.put("title", "" + "123112");
		hashMap.put("flag", R.drawable.emain_z12);// 图像资源的ID
		hashMap.put("time", "123312");
		
		list.add(hashMap);
		
		}

		return list;

	}*/
	

	private List<HashMap<String, Object>> getEmailData() {
		
		
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

		CA_Mail_Head[] mailHeads = thisCa.getAllMailHeads();

		if (mailHeads != null && mailHeads.length > 0) {
			
			DVB_CA_TYPE curCaType = thisCa.getCurType();

			Log.i("Email_z", " getEmailData()----->curCaType--->" + curCaType);

			for (int i = 0; i < mailHeads.length; i++) {

				if (DVB_CA_TYPE.CA_NOVEL == curCaType) {
					if (mailHeads[i] == null) {

						Log.i("Email_z",
								" getEmailData()----->mailHeads[i]   is null--->");
						break;
					}
					HashMap<String, Object> hashMap = new HashMap<String, Object>();

					// Log.i("Email_z",
					// " getEmailData()----->mailHeads[i].mCaMailHeadNovel.miId--->"+mailHeads[i].mCaMailHeadNovel.miId);
					// Log.i("Email_z",
					// " getEmailData()----->mailHeads[i].mCaMailHeadNovel.miId--->"+mailHeads[i].mCaMailHeadNovel.mCreateTime.miDay);

					hashMap.put("number", ""
							+ mailHeads[i].mCaMailHeadNovel.miId);
					String time = mailHeads[i].mCaMailHeadNovel.mCreateTime.miYear
							+ "-"
							+ mailHeads[i].mCaMailHeadNovel.mCreateTime.miMonth
							+ "-"
							+ mailHeads[i].mCaMailHeadNovel.mCreateTime.miDay
							+ " "
							+ mailHeads[i].mCaMailHeadNovel.mCreateTime.miHour
							+ ":"
							+ mailHeads[i].mCaMailHeadNovel.mCreateTime.miMinute
							+ ":"
							+ mailHeads[i].mCaMailHeadNovel.mCreateTime.miSecond;
					hashMap.put("time", "" + time);
					int flag = mailHeads[i].mCaMailHeadNovel.mReaded;
	

					if (flag == 0) {

						hashMap.put("flag", R.drawable.emain_z12);// 图像资源的ID

					} else {
						hashMap.put("flag", R.drawable.emain_z13);// 图像资源的ID
					}
					hashMap.put("title", mailHeads[i].mCaMailHeadNovel.mTitile);
					list.add(hashMap);
				} else if (DVB_CA_TYPE.CA_SUMA == curCaType) {
					if (mailHeads[i] == null) {
						Log.i("Email_z",
								" getEmailData()----->mailHeads[i]   is null--->");
						break;
					}

					EmailNumber++;

					Log.i("Email_z",
							" getEmailData()----->mailHeads[i].mCaMailHeadNovel.miId--->"
									+ mailHeads[i].mCaMailHeadSuma.miId);
					
					Log.i("Email_z",
							" getEmailData()----->mailHeads[i].mCaMailHeadNovel.mContentLength--->"
									+ mailHeads[i].mCaMailHeadSuma.mContentLength);

					HashMap<String, Object> hashMap = new HashMap<String, Object>();


					String Number = "000";

					if (EmailNumber < 10) {

						Number = "00" + EmailNumber;
					} else if (EmailNumber >= 10 && EmailNumber < 100) {

						Number = "0" + EmailNumber;
					}

			//	    hashMap.put("EmailNumber", Number);
					
					hashMap.put("number", ""
							+mailHeads[i].mCaMailHeadSuma.miId);

					String time = mailHeads[i].mCaMailHeadSuma.mCreateTime.miYear
							+ "-"
							+ mailHeads[i].mCaMailHeadSuma.mCreateTime.miMonth
							+ "-"
							+ mailHeads[i].mCaMailHeadSuma.mCreateTime.miDay;

					hashMap.put("time", time);

					int flag = mailHeads[i].mCaMailHeadSuma.mReaded;
					
					
					Log.i("Email_z",
							"email is readed ?---> flag   --->"+flag);
					if (flag == 0) {

						hashMap.put("flag", R.drawable.emain_z12);// 图像资源的ID

					} else {
						hashMap.put("flag", R.drawable.emain_z1);// 图像资源的ID
					}
					hashMap.put("title", ""
							+ mailHeads[i].mCaMailHeadSuma.mTitile);
					list.add(hashMap);
				}
			}
		} else {

			Log.i("Email_z",
					" getEmailData()----->mailHeads  is null -----mailHeads.length");
		}

		return list;
	}

	private void init() {

		list.clear();
		list = getEmailData();
		
		
		Log.i("Email_z","list.size"+list.size());

		adapter = new SimpleAdapter(this, list, R.layout.yj_item_z,
				new String[] { "number", "flag", "title", "time", }, new int[] {
						R.id.item_number, R.id.item_flag, R.id.item_title,
						R.id.item_time });

		yj_conten_z.setAdapter(adapter);
		yj_conten_z.setOnItemSelectedListener(itemSelectedListener);
		yj_conten_z.setOnItemClickListener(itemClickListener);

	}

	OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			HashMap<String, Object> map = list.get(position);

			Intent mIntent = new Intent(Email_z.this, EmailContent_z.class);
			mIntent.putExtra("id", Integer.parseInt((String) map.get("number")));
			mIntent.putExtra("time", (String) map.get("time"));
			mIntent.putExtra("title", (String) map.get("title"));
			startActivity(mIntent);

		}
	};

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		init();
  	getWindow().getDecorView().setVisibility(View.VISIBLE);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		getWindow().getDecorView().setVisibility(View.INVISIBLE);
	}

	OnItemSelectedListener itemSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			if (view != null) {

				CurrentItemPosition = position;

				int i = view.getTop();

				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) yj_remove_z
						.getLayoutParams();
				params.setMargins(1320, i, 0, 0);// 通过自定义坐标来放置你的控件
				yj_remove_z.setLayoutParams(params);

				Log.i("email_z", "view.getTop(); === " + view.getTop());

				if (yj_remove_z.getVisibility() == View.INVISIBLE)
					yj_remove_z.setVisibility(View.VISIBLE);
			}

		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}
	};
	@Override
	public boolean onKeyDown(int arg0, KeyEvent arg1) {
		// charle 哎，改不来，总是要死机
		switch (arg0) {
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			{
				
				return true;
			}
		case KeyEvent.KEYCODE_VOLUME_UP:
			{
				
				return true;	
			}
		case KeyEvent.KEYCODE_VOLUME_MUTE:
			{
				return true;
			}
		
		}
		return super.onKeyDown(arg0, arg1);
	}
}