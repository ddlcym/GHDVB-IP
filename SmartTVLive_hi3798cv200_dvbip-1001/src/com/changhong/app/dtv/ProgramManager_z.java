package com.changhong.app.dtv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.app.book.BookInfo;
import com.changhong.dvb.Channel;
public class ProgramManager_z extends Activity {

	EpgListview jmglselect_z, jmgl_ydview_z, jmgl_jmview_z;

	private static final int MESSAGE_DISAPPEAR_PROMPT = 203;

	boolean MoveProgramOrder = false;

	BookInfo YDtempbookInfo = null;
	int YD_cur_position = -1;

	String FromlogicNoargs;
	String TologicNoargs;
	Boolean moveControl = true;
	int FromChanId;
	int ToChanId;

	TextView jmgl_jmview_title_z, jmgl_ydview_title_z, jmgl_item_move_z,
			jmgl_item_favor_z, jmgl_item_remove_z;
	ImageView jmgl_move_tag;
	TextView jmgl_prompt;

	static SysApplication objApplication;

	LinearLayout jmgl_program_operation_selector_z;
	boolean Move = false;
	int itemValue = 0, TYPE = -1, moveVaule = 0;

	Context context = null;

	int fromposition = 0, toposition = 0, currentposition = 0,
			frompositionForControl = 0;

	List<Channel> HDTvList = new ArrayList<Channel>();
	List<Channel> CurChannels = new ArrayList<Channel>();
	List<Channel> CurHDChannels = new ArrayList<Channel>();
	List<Channel> CurBDChannels = new ArrayList<Channel>();

	RelativeLayout program_Prompt_diolag;

	TextView program_Prompt_content;

	List<Channel> TempCurChannelsForMove = new ArrayList<Channel>();
	Channel CurselectedChannel;
	Vector<BookInfo> YDTvList = new Vector<BookInfo>();

	int FromlogicNo = -1;
	int TologicNo = -1;

	jmviewAdapter jmgl_jmview_zadapter = null;
	ydviewAdapter jmgl_ydview_zadapter = null;
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
		setContentView(R.layout.program_manager_z);
		
		mHomeReceiver = new homeReceiver();
		registerReceiver(mHomeReceiver, new IntentFilter("HOME_PRESSED"));

		context = ProgramManager_z.this;

		InitView();

		InitDtvobject();

		getAllTVtype();

	}

	private void InitDtvobject() {

		objApplication = SysApplication.getInstance();
	}

	Handler uiHandler = new Handler() {

		public void handleMessage(Message msg) {

			switch (msg.what) {

			case 1:

				jmgl_jmview_zadapter.notifyDataSetChanged();

				break;

			case 2:

				break;

			case MESSAGE_DISAPPEAR_PROMPT:

				program_Prompt_diolag.setVisibility(View.INVISIBLE);

				break;

			}
		};
	};

	private OnItemClickListener YD_EventClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {

			if (arg1 != null) {
				YD_cur_position = position;
				YDtempbookInfo = YDTvList.get(position);

				Log.i("Jmgl_z", "handleMessage3 ------->YDTvList.size"
						+ YDTvList.size());

				objApplication.delBookChannel(YDtempbookInfo.bookDay,
						YDtempbookInfo.bookTimeStart);

				Log.i("Jmgl_z", "handleMessage1 ------->YDTvList.size"
						+ YDTvList.size());

				YDTvList.remove(YD_cur_position);

				Log.i("Jmgl_z", "handleMessage2 ------->YDTvList.size"
						+ YDTvList.size());

				for (int i = 0; i < YDTvList.size(); i++) {

					Log.i("Jmgl_z", "handleMessage ------->"
							+ YDTvList.get(i).bookChannelName);

				}

				jmgl_ydview_zadapter.notifyDataSetChanged();
				
				Toast.makeText(ProgramManager_z.this, "删除预定成功",
						Toast.LENGTH_SHORT).show();

			//uiHandler.sendMessage(msg)(2,500);
			}

		}

	};

	private OnItemSelectedListener YD_EventSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {

		}
	};

	private OnItemClickListener ChannelClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {

			if (arg1 != null) {

				if (!MoveProgramOrder) {

					frompositionForControl = position;

					CurselectedChannel = CurChannels.get(position);

					currentposition = position;
					fromposition = position;

					int top = arg1.getTop();

					// LinearLayout.LayoutParams tmplayout ,mlayout;
					/*
					 * LinearLayout.LayoutParams tmplayout =
					 * (LinearLayout.LayoutParams) arg1 .getLayoutParams();
					 */
					RelativeLayout.LayoutParams mlayout = new RelativeLayout.LayoutParams(
							100, 100);

					mlayout.leftMargin = 1275;
					mlayout.topMargin = top + 225;
					mlayout.width = 225;
					mlayout.height = 271;

					Log.i("Jmgl_z",
							"mlayout.leftMargin  mlayout.leftMargin     mlayout.width  mlayout.height ------->"
									+ mlayout.leftMargin
									+ "      "
									+ mlayout.topMargin
									+ "    "
									+ mlayout.width + "     " + mlayout.height);

					jmgl_program_operation_selector_z.setLayoutParams(mlayout);
					jmgl_program_operation_selector_z
							.setVisibility(View.VISIBLE);
					jmgl_item_move_z.requestFocus();

					RelativeLayout.LayoutParams mlayout2 = new RelativeLayout.LayoutParams(
							100, 100);
					mlayout2.leftMargin = 1230;
					mlayout2.topMargin = top + 145;
					mlayout2.width = 45;
					mlayout2.height = 60;
					jmgl_move_tag.setLayoutParams(mlayout2);

				} else {

					MoveProgramOrder = false;
					jmgl_move_tag.setVisibility(View.INVISIBLE);

					Log.i("Jmgl_z",
							"Update   dvbDatabase   channel  logicNo------>     FromlogicNo    TologicNo    FromChanId   TologicNo------>"
									+ FromlogicNo
									+ "    "
									+ TologicNo
									+ "    "
									+ FromChanId + "    " + ToChanId);
					if (frompositionForControl != position) {

						program_Prompt_diolag.setVisibility(View.VISIBLE);

						objApplication.dvbDatabase.emptyChannel();

						for (int j = 0; j < CurChannels.size(); j++) {

							objApplication.dvbDatabase.addChannel(CurChannels
									.get(j));

						}

						jmgl_prompt.setText("按  “确认”  选择功能");

						program_Prompt_content.setText("节目移动成功！");

						uiHandler.sendEmptyMessageDelayed(
								MESSAGE_DISAPPEAR_PROMPT, 2000);

					}
				}
			}
		}

	};

	
	private OnItemSelectedListener ChannelSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {

			if (view != null) {
				
				if (MoveProgramOrder) {

					//if (moveControl) {
						
						//moveControl = false;	
						Log.i("moveTest",
								"ChannelSelectedListener   moveControl----"
										+ moveControl);
						
						toposition = position;
						
						if (fromposition != toposition) {

							int top = view.getTop();
							RelativeLayout.LayoutParams mlayout3 = new RelativeLayout.LayoutParams(
									100, 100);
							mlayout3.leftMargin = 1230;
							mlayout3.topMargin = top + 145;
							mlayout3.width = 45;
							mlayout3.height = 60;

							jmgl_move_tag.setLayoutParams(mlayout3);

							Move = true;
							
							Channel tempchannel;

							/*TempCurChannelsForMove.clear();

							TempCurChannelsForMove.addAll(CurChannels);

							FromlogicNo = CurChannels.get(fromposition).logicNo;

							TologicNo = CurChannels.get(toposition).logicNo;

							FromlogicNoargs = "" + FromlogicNo;
							TologicNoargs = "" + TologicNo;

							FromChanId = CurChannels.get(fromposition).chanId;
							ToChanId = CurChannels.get(toposition).chanId;

							CurChannels.get(toposition).logicNo = FromlogicNo;
							CurChannels.get(fromposition).logicNo = TologicNo;*/
							

							tempchannel = CurChannels.get(toposition);
							CurChannels.set(toposition,
									CurChannels.get(fromposition));
							CurChannels.set(fromposition, tempchannel);

							switch (TYPE) {

							case 0:

								tempchannel = HDTvList.get(toposition);
								HDTvList.set(toposition,
										HDTvList.get(fromposition));
								HDTvList.set(fromposition, tempchannel);

								break;
							case 2:
								break;
							}
						
							fromposition=toposition;
				

						}
						
					    uiHandler.removeMessages(1);
						//uiHandler.sendEmptyMessage(1);
					   uiHandler.sendEmptyMessageAtTime(1,1000);

				//	}

				}

			}

		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

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

			break;

		case KeyEvent.KEYCODE_DPAD_RIGHT:

			break;

		case KeyEvent.KEYCODE_DPAD_LEFT:

			if (jmgl_jmview_z.hasFocus()) {

				jmglselect_z.requestFocus();
				jmgl_prompt.setText("按  “确认”  选择功能");

				if (MoveProgramOrder) {

					MoveProgramOrder = false;
					jmgl_move_tag.setVisibility(View.INVISIBLE);

					getAllTVtype();

					if (TYPE == 0) {
						CurChannels.clear();
						CurChannels.addAll(HDTvList);
						jmgl_jmview_zadapter.notifyDataSetChanged();
					}

				}

			} else if (jmgl_ydview_z.hasFocus()) {

				jmgl_prompt.setText("按  “确认”  选择功能");
				jmglselect_z.requestFocus();

			}

			break;

		case KeyEvent.KEYCODE_BACK:

			if (jmgl_program_operation_selector_z.hasFocus()) {
				jmgl_program_operation_selector_z.setVisibility(View.INVISIBLE);
				jmgl_jmview_z.requestFocus();

			} else if (jmgl_ydview_z.hasFocus()) {

				jmgl_prompt.setText("按  “确认”  选择功能");
				jmglselect_z.requestFocus();

			} else if (jmgl_jmview_z.hasFocus()) {

				jmglselect_z.requestFocus();
				jmgl_prompt.setText("按  “确认”  选择功能");

				if (MoveProgramOrder) {

					MoveProgramOrder = false;
					jmgl_move_tag.setVisibility(View.INVISIBLE);

					getAllTVtype();

					if (TYPE == 0) {
						CurChannels.clear();
						CurChannels.addAll(HDTvList);
						jmgl_jmview_zadapter.notifyDataSetChanged();
					} 

				}

			} else if (jmglselect_z.hasFocus()) {

				finish();

			}
			break;

		}
		return false;

	}

	protected class ydviewAdapter extends BaseAdapter {
		
		private Context context;
		private LayoutInflater inflater;

		@Override
		public int getCount() {
			return YDTvList.size();
		}

		public ydviewAdapter(Context context) {
			this.context = context;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup viewGroup) {

			convertView = inflater.inflate(R.layout.jmgl_yd_item_z, null);

			BookInfo channel = YDTvList.get(position);

			Log.i("Jmgl_z",
					"getView ------->YDTvList.get(position).bookChannelName-----  YDTvList.get(position).bookEnventName"
							+ YDTvList.get(position).bookChannelName
							+ "   "
							+ YDTvList.get(position).bookEnventName);

			TextView jmgl_yd_jmhz = (TextView) convertView
					.findViewById(R.id.jmgl_yd_jmhz);
			TextView jmgl_yd_rqz = (TextView) convertView
					.findViewById(R.id.jmgl_yd_rqz);
			TextView jmgl_yd_sjz = (TextView) convertView
					.findViewById(R.id.jmgl_yd_sjz);
			TextView jmgl_yd_pdz = (TextView) convertView
					.findViewById(R.id.jmgl_yd_pdz);
			com.changhong.app.dtv.TextMarquee jmgl_yd_jmmcz = (com.changhong.app.dtv.TextMarquee) convertView
					.findViewById(R.id.jmgl_yd_jmmcz);

			if (channel.bookChannelIndex < 10) {
				jmgl_yd_jmhz.setText("00" + channel.bookChannelIndex);
			} else if (channel.bookChannelIndex < 100) {
				jmgl_yd_jmhz.setText("0" + channel.bookChannelIndex);
			} else {
				jmgl_yd_jmhz.setText("" + channel.bookChannelIndex);
			}

			jmgl_yd_rqz.setText(channel.bookDay);
			jmgl_yd_sjz.setText(channel.bookTimeStart);
			jmgl_yd_pdz.setText(channel.bookChannelName);
			jmgl_yd_jmmcz.setText(channel.bookEnventName);

			return convertView;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

	}

	protected class jmviewAdapter extends BaseAdapter {
		private Context context;
		// public List<Channel> dvbChannels;
		private LayoutInflater inflater;
		private int mChildCount = 0;

		int count;

		@Override
		public int getCount() {
			// mChildCount =dvbChannels.size();
			mChildCount = CurChannels.size();
			return mChildCount;
		}

		public void setdata(List<Channel> data) {
			
		}


		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public jmviewAdapter(Context context) {
			this.context = context;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup viewGroup) {
			
			
            final ViewHolder vh;
            if (convertView == null) {
                vh = new ViewHolder();
                convertView = inflater.inflate(R.layout.jmgl_item_z, null);
                
                vh.channelIndex = (TextView) convertView.findViewById(R.id.jmgl_bjjmjmh);
                vh.channelName = (com.changhong.app.dtv.TextMarquee ) convertView.findViewById(R.id.jmgl_bjjmjmmc);
                vh.favor_tag=(ImageView)convertView.findViewById(R.id.favor_tag);

                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            
			Channel channel;
			
		/*	if (toposition != fromposition && fromposition == position && Move) {

				channel = TempCurChannelsForMove.get(toposition);

				channel.logicNo = FromlogicNo;

				Log.i("Jmgl_z",
						"333333-------->"
								+ position
								+ "    "
								+ channel.name
								+ "  "
								+ TempCurChannelsForMove.get(fromposition).logicNo
								+ "     "
								+ TempCurChannelsForMove.get(toposition).logicNo);

				moveVaule++;
				if (moveVaule == 2) {
					Move = false;
					moveVaule = 0;
					fromposition = toposition;
				}

			} else if (toposition != fromposition && toposition == position
					&& Move) {

				// moveTag.setVisibility(View.VISIBLE);
				channel = TempCurChannelsForMove.get(fromposition);
				channel.logicNo = TologicNo;

				moveVaule++;
				if (moveVaule == 2) {
					Move = false;
					moveVaule = 0;
					fromposition = toposition;
				}

			} else {

				channel = CurChannels.get(position);
			}*/

			channel = CurChannels.get(position);
			
			
			Log.i("Jmgl_z_changeChannelTest",
					"position-----CurChannels.get(position).name---->"
							+ position+"   "+CurChannels.get(position).name);
			
			if (channel.favorite == 1) {

			    vh.favor_tag.setVisibility(View.VISIBLE);

			} else {

			    vh.favor_tag.setVisibility(View.INVISIBLE);

			}

			if (channel.logicNo < 10) {
			    vh.channelIndex.setText("00" + channel.logicNo);
			} else if (channel.logicNo < 100) {
			    vh.channelIndex.setText("0" + channel.logicNo);
			} else {
			    vh.channelIndex.setText("" + channel.logicNo);
			}

			  vh.channelName.setText(channel.name);
			// moveTag.setVisibility(View.INVISIBLE);

			return convertView;
		}
		
	    public final class ViewHolder {
			
            public TextView channelIndex;
            public com.changhong.app.dtv.TextMarquee channelName;
            public ImageView favor_tag;
        }

	}

	private void getAllTVtype() {

		HDTvList.clear();
		YDTvList.clear();
		CurChannels.clear();

		if (objApplication.dvbBookDataBase != null)
			YDTvList.addAll(objApplication.dvbBookDataBase.GetBookInfo());

	

		Channel[] dvbChannels = objApplication.dvbDatabase.getChannelsAllSC();

		if (dvbChannels != null) {
			for (Channel dvbChannel : dvbChannels) {
					HDTvList.add(dvbChannel);
				
			}

		}

	}

	private void InitView() {

		program_Prompt_diolag = (RelativeLayout) findViewById(R.id.program_Prompt_diolag);

		program_Prompt_content = (TextView) findViewById(R.id.program_Prompt_content);

		jmglselect_z = (EpgListview) findViewById(R.id.jmglselect_z);
		jmgl_jmview_z = (EpgListview) findViewById(R.id.jmgl_jmview_z);
		jmgl_ydview_z = (EpgListview) findViewById(R.id.jmgl_ydview_z);

		jmgl_prompt = (TextView) findViewById(R.id.jmgl_prompt);
		jmgl_ydview_title_z = (TextView) findViewById(R.id.jmgl_ydview_title_z);
		jmgl_jmview_title_z = (TextView) findViewById(R.id.jmgl_jmview_title_z);

		jmgl_item_move_z = (TextView) findViewById(R.id.jmgl_item_move_z);
		jmgl_item_favor_z = (TextView) findViewById(R.id.jmgl_item_favor_z);
		jmgl_item_remove_z = (TextView) findViewById(R.id.jmgl_item_remove_z);

		jmgl_move_tag = (ImageView) findViewById(R.id.jmgl_move_tag);

		jmgl_program_operation_selector_z = (LinearLayout) findViewById(R.id.jmgl_program_operation_selector_z);

		jmgl_item_move_z.setOnClickListener(OnClickedListener);
		jmgl_item_favor_z.setOnClickListener(OnClickedListener);
		jmgl_item_remove_z.setOnClickListener(OnClickedListener);

		jmgl_prompt.setText("按  “确认”  选择功能");

		// 生成动态数组，加入数据
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();

		HashMap<String, Object> map = new HashMap<String, Object>();

		map.put("ItemText", "频道编辑");

		listItem.add(map);

	/*	HashMap<String, Object> map2 = new HashMap<String, Object>();

		map2.put("ItemText", "标清节目编辑");

		listItem.add(map2);*/

	/*	HashMap<String, Object> map3 = new HashMap<String, Object>();

		map3.put("ItemText", "广播编辑");

		listItem.add(map3);*/

		HashMap<String, Object> map4 = new HashMap<String, Object>();

		map4.put("ItemText", "预定管理");

		listItem.add(map4);

		SimpleAdapter listItemAdapter = new SimpleAdapter(this, listItem,
				R.layout.jmgl_jmtitle,

				new String[] { "ItemText" },

				new int[] { R.id.jmgl_title });

		jmgl_jmview_zadapter = new jmviewAdapter(context);
		jmgl_jmview_z.setAdapter(jmgl_jmview_zadapter);
		jmgl_jmview_z.setOnItemSelectedListener(ChannelSelectedListener);
		jmgl_jmview_z.setOnItemClickListener(ChannelClickListener);

		jmgl_ydview_zadapter = new ydviewAdapter(context);
		jmgl_ydview_z.setAdapter(jmgl_ydview_zadapter);
		jmgl_ydview_z.setOnItemSelectedListener(YD_EventSelectedListener);
		jmgl_ydview_z.setOnItemClickListener(YD_EventClickListener);
		jmgl_ydview_z.setOnFocusChangeListener(YDItemChangeListener);

		jmglselect_z.setAdapter(listItemAdapter);
		jmglselect_z.setOnItemSelectedListener(JMGLItemSelectChangeListener);

	}

	private OnClickListener OnClickedListener = new OnClickListener() {

		@Override
		public void onClick(View view) {

			switch (view.getId()) {

			case R.id.jmgl_item_favor_z:

				if (CurselectedChannel.favorite == 0) {

					CurChannels.get(currentposition).favorite = 1;

					if (TYPE == 0) {

						HDTvList.get(currentposition).favorite = 1;
					}  else {

					}

					objApplication.dvbDatabase.updateChannel(
							CurselectedChannel.chanId, "favorite", "1");

					Toast.makeText(ProgramManager_z.this, "该节目已设置为喜爱",
							Toast.LENGTH_SHORT).show();
				} else {

					if (TYPE == 0) {

						HDTvList.get(currentposition).favorite = 0;
					}  else {

					}

					CurChannels.get(currentposition).favorite = 0;

					objApplication.dvbDatabase.updateChannel(
							CurselectedChannel.chanId, "favorite", "0");

					Toast.makeText(ProgramManager_z.this, "该节目已从喜爱列表删除",
							Toast.LENGTH_SHORT).show();
				}

				jmgl_program_operation_selector_z.setVisibility(View.INVISIBLE);
				jmgl_jmview_zadapter.notifyDataSetChanged();
				jmgl_jmview_z.requestFocus();

				break;
			case R.id.jmgl_item_move_z:

				jmgl_prompt.setText("按  “上下键”   进行移动");
				MoveProgramOrder = true;
				jmgl_move_tag.setVisibility(View.VISIBLE);

				jmgl_program_operation_selector_z.setVisibility(View.INVISIBLE);
				jmgl_jmview_z.requestFocus();

				break;
			case R.id.jmgl_item_remove_z:

				objApplication.dvbDatabase
						.removeChannel(CurselectedChannel.chanId);

				CurChannels.remove(currentposition);

				if (TYPE == 0) {

					HDTvList.remove(currentposition);
				}  else {

				}
				uiHandler.sendEmptyMessage(1);

				Toast.makeText(ProgramManager_z.this, "该节目已删除",
						Toast.LENGTH_LONG).show();

				jmgl_program_operation_selector_z.setVisibility(View.INVISIBLE);
				jmgl_jmview_z.requestFocus();

				break;

			}

		}

	};

	private OnFocusChangeListener YDItemChangeListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View arg0, boolean arg1) {

			if (arg1)
				jmgl_prompt.setText("  “确认”  删除预定             “返回”  上一级");
		}
	};

	private OnItemSelectedListener JMGLItemSelectChangeListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg1, View arg0, int arg2,
				long arg3) {

			Log.i("jmgl", "--------------------------------------arg1＝ !"
					+ arg1);

			TextView textView = (TextView) arg0.findViewById(R.id.jmgl_title);
			String Item = (String) textView.getText();

			Log.i("jmgl",
					"--------------------------------------Current  ITEM ＝ !"
							+ Item);

			if (Item.equals("频道编辑")) {

				TYPE = 0;

				CurChannels.clear();
				CurChannels.addAll(HDTvList);
				jmgl_jmview_title_z.setVisibility(View.VISIBLE);
				jmgl_ydview_title_z.setVisibility(View.GONE);

				jmgl_ydview_z.setVisibility(View.GONE);
				jmgl_jmview_z.setVisibility(View.VISIBLE);
				jmgl_jmview_zadapter.notifyDataSetChanged();
				// jmgl_jmview_z.setAdapter(new jmviewAdapter(context));

			} else if (Item.equals("广播编辑")) {

				TYPE = 2;
				jmgl_jmview_title_z.setVisibility(View.INVISIBLE);
				jmgl_ydview_title_z.setVisibility(View.GONE);
				jmgl_ydview_z.setVisibility(View.GONE);
				jmgl_jmview_z.setVisibility(View.INVISIBLE);

			} else if (Item.equals("预定管理")) {

				TYPE = 3;

				jmgl_jmview_title_z.setVisibility(View.GONE);
				jmgl_ydview_title_z.setVisibility(View.VISIBLE);

				jmgl_jmview_z.setVisibility(View.GONE);
				jmgl_ydview_z.setVisibility(View.VISIBLE);
				jmgl_ydview_zadapter.notifyDataSetChanged();

			}

		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}

	};

}