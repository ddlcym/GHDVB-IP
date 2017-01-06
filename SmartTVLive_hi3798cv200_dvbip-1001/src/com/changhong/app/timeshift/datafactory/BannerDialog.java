package com.changhong.app.timeshift.datafactory;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.changhong.app.dtv.R;
import com.changhong.app.dtv.SysApplication;
import com.changhong.app.timeshift.common.CacheData;
import com.changhong.app.timeshift.common.Class_Constant;
import com.changhong.app.timeshift.common.CommonMethod;
import com.changhong.app.timeshift.common.L;
import com.changhong.app.timeshift.common.MyApp;
import com.changhong.app.timeshift.common.PlayVideo;
import com.changhong.app.timeshift.common.ProcessData;
import com.changhong.app.timeshift.common.ProgramInfo;
import com.changhong.app.timeshift.common.Utils;
import com.changhong.app.timeshift.common.VolleyTool;
import com.changhong.app.timeshift.widget.MySeekbar;
import com.changhong.app.timeshift.widget.PlayButton;
import com.changhong.app.timeshift.widget.ShiftDialog;
import com.changhong.app.timeshift.widget.TwoWayAdapterView;
import com.changhong.app.timeshift.widget.TwoWayAdapterView.OnItemClickListener;
import com.changhong.app.timeshift.widget.TwoWayAdapterView.OnItemSelectedListener;
import com.changhong.app.timeshift.widget.TwoWayGridView;
import com.changhong.dvb.Channel;
import com.changhong.dvb.ChannelDB;
import com.changhong.dvb.DVB;
import com.changhong.dvb.PlayingInfo;

/**
 * @author OscarChang 时移信息条
 */
public class BannerDialog extends Dialog {

	private Context mContext;
	private Channel channelInfo; //当前频道
	private List<ProgramInfo> programListInfo;
	private List<ProgramInfo> programListInfo_back=new ArrayList<ProgramInfo>();//备份一次当前频道的节目信息
	private Handler parentHandler;
	private Player player;
	private String TAG = "mmmm";
	private boolean whetherMute;

	private MySeekbar programPlayBar;
	// private TextView channel_name = null;// 频道名称
	// private TextView channel_number = null;// 频道ID
	private TextView currentProgramName = null;
	private TextView nextProgramName = null;
	private TextView currentProgramTime = null;
	private TextView nextProgramTime = null;
	private TextView timeLength;
	private SurfaceView surView;
	private LinearLayout bannerView;//时移下方整个布局
	private ImageView left_arrows,right_arrows,timeshiftback;

	private ProcessData processData = null;
	private RequestQueue mReQueue;
	private Channel curChannel;
	private LinearLayout timeShiftInfo;
	private PlayButton palyButton;
	private ImageView muteIconImage,timeShiftIcon;
	private List<ProgramInfo> list;

	private TwoWayGridView timeshiftProList;
	private TimeShiftProgramAdapter programListAdapter;
	private RelativeLayout nextProgramContainer;
	private RelativeLayout curProgramContainer;
	private RelativeLayout programListContainer;//时移节目列表容器
	private boolean IsFocusList = false;
	private boolean firstInShift = true;
	int shiftcurindex;
	ProgramInfo curshiftpro;
	/*
	 * 显示隐藏容器
	 */
	private static final int PROGRAM_LIST=1100;
	private static final int NEXT_PROGRAM=1101;
	private static final int NOTHING=1102;
	AudioManager mAudioManager;
	int curvolumn;
	private ImageView revolumnback;
	private int[] vols = new int[]{R.drawable.rea,R.drawable.reb,R.drawable.rec,R.drawable.red,R.drawable.ree,R.drawable.ref,R.drawable.reg,
			R.drawable.reh,R.drawable.rei,R.drawable.rej,R.drawable.rek,R.drawable.rel,R.drawable.rem,R.drawable.ren,R.drawable.reo,R.drawable.rep};
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Class_Constant.BACK_TO_LIVE:
				{
					//拖动的时间大于当前系统时间
					curshiftpro = CacheData.getCurProgram();
					Log.i("test", "curshiftpro.getEventName()"+curshiftpro.getEventName()+curshiftpro.getBeginTime());
					ShiftDialog.Builder builder = new ShiftDialog.Builder(mContext);
					builder.setMessage("<" + curshiftpro.getEventName()+">"+"播放结束");
					builder.setTitle("温馨提示");
					builder.setPositiveButton("退出时移", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								dismiss();
								dialog.dismiss();
								parentHandler.sendEmptyMessage(Class_Constant.BACK_TO_LIVE);
						}
					});
		
					builder.setNegativeButton("重新播放",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
								PlayVideo.getInstance().playLiveBack(player,curChannel,curshiftpro);
						}
					});
		
					builder.create().show();
					
				}
				break;	

			case Class_Constant.SHIFT_LAST_PROGRAM:
				//时移回退到最开始
				{
					int len = list.size();
					ShiftDialog.Builder builder = new ShiftDialog.Builder(mContext);
					curshiftpro = CacheData.getCurProgram();
					Log.i("test", "curshiftpro.getEventName()"+curshiftpro.getEventName()+curshiftpro.getBeginTime());
					//shiftlist = CacheData.getTimeshiftPrograms();
					int i = curshiftpro.getBeginTime().compareTo(list.get(len-1).getBeginTime());
					Log.i("test","i"+ i);
					if(i==0){
						shiftcurindex = len-1;
					}else{
						shiftcurindex = list.indexOf(curshiftpro);
					}
					
					Log.i("test", "curindex"+shiftcurindex);
					for (ProgramInfo p : list) {
					   Log.i("test","EventName"+ p.getEventName());
				    }
					
					
					if (0 == shiftcurindex) {
						builder.setMessage("<" + list.get(shiftcurindex).getEventName()+">"+"即将开始");
						builder.setTitle("温馨提示");
						builder.setPositiveButton("退出时移", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dismiss();
									dialog.dismiss();
									parentHandler.sendEmptyMessage(Class_Constant.BACK_TO_LIVE);
							}
						});
	
						builder.setNegativeButton("确定播放",new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
									PlayVideo.getInstance().playLiveBack(player,curChannel,list.get(shiftcurindex));
									palyButton.setMyBG(PlayButton.Pause);
									programListInfo.remove(1);
									programListInfo.add(1,list.get(shiftcurindex));
								    programListInfo.remove(2);
								    programListInfo.add(2, list.get(shiftcurindex+1));
									initData();
									showViewVisibility(NEXT_PROGRAM);
									//nextProgramContainer.setVisibility(View.VISIBLE);
									//programListContainer.setVisibility(View.GONE);
									
							}
						});
					}else {
						builder.setMessage("<" + list.get(shiftcurindex-1).getEventName()+">"+"即将开始");
						builder.setTitle("温馨提示");
						builder.setPositiveButton("观看上一个节目", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
//									Log.i("mmmm", "PositiveButton"+list.get(shiftcurindex-1).getEventName());
									
									PlayVideo.getInstance().playLiveBack(player,curChannel,list.get(shiftcurindex-1));
									CacheData.setCurProgram(list.get(shiftcurindex-1));
									
									palyButton.setMyBG(PlayButton.Pause);
									programListInfo.remove(1);
									programListInfo.add(1,list.get(shiftcurindex-1));
									programListInfo.remove(2);
									programListInfo.add(2, list.get(shiftcurindex));
									initData();
									showViewVisibility(NEXT_PROGRAM);
									//nextProgramContainer.setVisibility(View.VISIBLE);
									//programListContainer.setVisibility(View.GONE);
							}
						});
	
						builder.setNegativeButton("重新播放",new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
									PlayVideo.getInstance().playLiveBack(player,curChannel,list.get(shiftcurindex));
									palyButton.setMyBG(PlayButton.Pause);
									programListInfo.remove(1);
									programListInfo.add(1,list.get(shiftcurindex));
									if(shiftcurindex != list.size()-1){
										programListInfo.remove(2);
										programListInfo.add(2, list.get(shiftcurindex+1));
									}
									
									initData();
									showViewVisibility(NEXT_PROGRAM);
									//nextProgramContainer.setVisibility(View.VISIBLE);
									//programListContainer.setVisibility(View.GONE);
									
							}
						});
					}
					builder.create().show();
				}
				break;
				
			 case Class_Constant.SHIFT_NEXT_PROGRAM:
				 {
					player.pause();//处理时移自动播放完一个节目，暂停节目，弹出对话框
					int len = list.size();
					ShiftDialog.Builder builder = new ShiftDialog.Builder(mContext);
					curshiftpro = CacheData.getCurProgram();
					Log.i("test", "curshiftpro.getEventName()"+curshiftpro.getEventName()+curshiftpro.getBeginTime());
					//shiftlist = CacheData.getTimeshiftPrograms();
					shiftcurindex = list.indexOf(curshiftpro);
					Log.i("test", "shiftcurindex"+shiftcurindex);
					for (ProgramInfo p : list) {
					   Log.i("test","EventName"+ p.getEventName());
				    }
					builder.setMessage("<" + list.get(shiftcurindex+1).getEventName()+">"+"即将开始");
					builder.setTitle("温馨提示");
					builder.setPositiveButton("观看下一个节目", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
								Log.i("test", "PositiveButton"+list.get(shiftcurindex+1).getEventName());
								PlayVideo.getInstance().playLiveBack(player,curChannel,list.get(shiftcurindex+1));
								palyButton.setMyBG(PlayButton.Pause);
								programListInfo.remove(1);
								programListInfo.add(1,list.get(shiftcurindex+1));
								//有一个问题，当前播放的节目是倒数第二个，然后点下一个节目播放，此时banner条上的信息更新有问题，下一个节目不知道是什么
								if(shiftcurindex != list.size()-2){
									programListInfo.remove(2);
									programListInfo.add(2, list.get(shiftcurindex+2));
								}else{
									programListInfo.remove(2);
									if(programListInfo_back!=null&&programListInfo_back.size()>2){
										programListInfo.add(2, programListInfo_back.get(2));
									}
								}
								
								initData();
								showViewVisibility(NEXT_PROGRAM);
								//nextProgramContainer.setVisibility(View.VISIBLE);
								//programListContainer.setVisibility(View.GONE);
						}
					});

					builder.setNegativeButton("重新播放",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
								PlayVideo.getInstance().playLiveBack(player,curChannel,list.get(shiftcurindex));
								palyButton.setMyBG(PlayButton.Pause);
								programListInfo.remove(1);
								programListInfo.add(1,list.get(shiftcurindex));
								programListInfo.add(2, list.get(shiftcurindex+1));
								initData();
								showViewVisibility(NEXT_PROGRAM);
								//nextProgramContainer.setVisibility(View.VISIBLE);
								//programListContainer.setVisibility(View.GONE);
								
						}
					});
				
				 builder.create().show(); 
				}
			    break;
			}
		}
		
	};
	public BannerDialog(Context context, Channel outterChannelInfo,
			List<ProgramInfo> outterListProgramInfo, Handler outterHandler,
			SurfaceView surView, AudioManager audioManager) {
		super(context, R.style.Translucent_NoTitle);
		setContentView(R.layout.bannernew);

		mContext = context;//.getApplicationContext();
		channelInfo = outterChannelInfo;
		programListInfo = outterListProgramInfo;
		parentHandler = outterHandler;
		mAudioManager = audioManager;
		whetherMute = false;
		this.surView = surView;

		//备份当前频道的节目信息
		if(outterListProgramInfo!=null&&outterListProgramInfo.size()>0){
			programListInfo_back=new ArrayList<ProgramInfo>(outterListProgramInfo);
		}
		initView();
		// 获取时移节目列表数据，并填充
		setTimeShiftProgramList();
		// initData();
		// setContentView(R.layout.setting_sys_help_dialog_details);
		// help_name=(TextView)findViewById(R.id.help_name);
		// help_content=(TextView)findViewById(R.id.help_content);
		// ibCancel=(ImageButton)findViewById(R.id.cancel_help);
		// Log.i("mmmm","content==" +name+content);
		//
		// ibCancel.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// MyApplication.vibrator.vibrate(100);
		// dismiss();
		// }
		// });
	}

	public void initView() {
		Window window = this.getWindow();
		WindowManager.LayoutParams wlp = window.getAttributes();
		wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
		wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
		window.setAttributes(wlp);
		window.setGravity(Gravity.BOTTOM);

		/* 频道名称、频道ID 节目名称 */

		// 时移节目列表
		timeshiftProList = (TwoWayGridView) findViewById(R.id.timeshift_program_list);
		curProgramContainer=(RelativeLayout)findViewById(R.id.timeshift_seekbar_container);
		nextProgramContainer=(RelativeLayout)findViewById(R.id.timeshift_nextpro_container);
		programListContainer=(RelativeLayout)findViewById(R.id.timeshift_program_list_container);
		left_arrows = (ImageView)findViewById(R.id.timeshift_left_arrows);
		right_arrows = (ImageView)findViewById(R.id.timeshift_right_arrows);

		// channel_name = (TextView) findViewById(R.id.banner_channel_name_id);
		// channel_number = (TextView) findViewById(R.id.banner_service_id);
		currentProgramName = (TextView) findViewById(R.id.current_program_info);
		nextProgramName = (TextView) findViewById(R.id.next_program_info);
	    currentProgramTime = (TextView) findViewById(R.id.shiyicurprotime);
		nextProgramTime = (TextView) findViewById(R.id.shiyinextprotime);
		programPlayBar = (MySeekbar) findViewById(R.id.bannernew_program_progress);
		timeshiftback = (ImageView)findViewById(R.id.timeshift_back);
		bannerView = (LinearLayout) findViewById(R.id.live_back_banner);
		timeLength = (TextView) findViewById(R.id.live_timelength);
		// bannerView.getBackground().setAlpha(255);
		palyButton = (PlayButton) findViewById(R.id.play_btn);
		//palyButton.setMyBG(PlayButton.Pause);
		palyButton.setMyBG(PlayButton.Play);
//		muteIconImage = (ImageView) findViewById(R.id.mute_icon);
		whetherMute = Boolean.valueOf(CommonMethod.getMuteState(SysApplication.getInstance()));
//		if (whetherMute) {
//			muteIconImage.setVisibility(View.VISIBLE);
//		} else {
//			muteIconImage.setVisibility(View.GONE);
//		}
		timeShiftInfo = (LinearLayout) findViewById(R.id.id_dtv_banner);
		timeShiftIcon = (ImageView) findViewById(R.id.time_shift_icon);
		revolumnback = (ImageView)findViewById(R.id.shift_volumn_background);
		programPlayBar.setFocusable(false);
		programPlayBar.setClickable(false);

		if (null == processData) {
			processData = new ProcessData();
		}
		mReQueue = VolleyTool.getInstance().getRequestQueue();
		
		//获取当前频道信息
		ChannelDB db=DVB.getManager().getChannelDBInstance();
		PlayingInfo thisPlayingInfo = DVB.getManager().getChannelDBInstance().getSavedPlayingInfo();
		//获取当前Channel详细信息
		curChannel = db.getChannel(thisPlayingInfo.mChannelId );
		
		// 设置时移节目列表的adapter
		programListAdapter = new TimeShiftProgramAdapter(mContext);
		timeshiftProList.setAdapter(programListAdapter);

		timeshiftProList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(TwoWayAdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (list != null&&programListInfo.size()!=0&&timeshiftProList.isShown()) {
					ProgramInfo program = list.get(position);
					
//					long curTime=System.currentTimeMillis();
//					int delayTime=(int) (curTime-program.getBeginTime().getTime())/1000;
					
//					CacheData.setCurProgram(program);
					programListInfo.remove(1);
					programListInfo.add(1, program);
					player.initSeekbar();
					PlayVideo.getInstance().playLiveBack(player, curChannel, program);
					
					
					palyButton.setMyBG(PlayButton.Play);
					if(programListInfo.size()!=0&&(list.size()-1)!=position){
						programListInfo.remove(2);
						programListInfo.add(2, list.get(position+1));
					}else{
						programListInfo.remove(2);
						if(programListInfo_back!=null&&programListInfo_back.size()>2){
							programListInfo.add(2, programListInfo_back.get(2));
						}
					}
					initData();
					nextProgramContainer.setVisibility(View.VISIBLE);
					programListContainer.setVisibility(View.GONE);
					IsFocusList = false;
				}
			}
		});
		curvolumn =  mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		Log.i("volumn", "enter shift curvolumn is"+curvolumn);
		if (curvolumn == 0) {
			revolumnback.setBackgroundResource(vols[curvolumn]);
			revolumnback.setVisibility(View.VISIBLE);
		}

	}

	public void initData() {
		// + programListInfo.get(1).getBeginTime()
		if (programListInfo.size() < 2) {
			return;
		}
		
		String currentProgramBginTime = Utils.hourAndMinute(programListInfo
				.get(1).getBeginTime());
		String currentProgramEndTime = Utils.hourAndMinute(programListInfo.get(
				1).getEndTime());
		String nextProgramBeginTime = Utils.hourAndMinute(programListInfo
				.get(2).getBeginTime());
		String nextProgramEndTime = Utils.hourAndMinute(programListInfo.get(2)
				.getEndTime());
		long length = programListInfo.get(1).getEndTime().getTime()
				- programListInfo.get(1).getBeginTime().getTime();
		// 设置频道号和频道名称
		// channel_name.setText(channelInfo.getChannelName());
		// channel_number.setText(channelInfo.getChannelNumber());
		/*currentProgramName.setText("当前节目       " + currentProgramBginTime + "-"
				+ currentProgramEndTime + "       "
				+ programListInfo.get(1).getEventName());*/
		/*nextProgramName.setText("下一节目       " + nextProgramBeginTime + "-"
				+ nextProgramEndTime + "       "
				+ programListInfo.get(2).getEventName());*/
		
		currentProgramTime.setText(currentProgramBginTime + "-"+ currentProgramEndTime);
		currentProgramName.setText(programListInfo.get(1).getEventName());
		
		nextProgramTime.setText(nextProgramBeginTime + "-"+ nextProgramEndTime);
		nextProgramName.setText(programListInfo.get(2).getEventName());
		timeLength.setText(currentProgramEndTime);
		programPlayBar.setMax((int) length);
		player.setDuration((int) length);
		
		/*player = new Player(parentHandler, surView,
				programPlayBar.getSeekBar(), programPlayBar.getCurText());		*/		
		if(null==player){
		player = new Player(mHandler, surView,
				programPlayBar.getSeekBar(), programPlayBar.getCurText());}
		player.setLiveFlag(true);
		player.initSeekbar();
		
		timeshiftProList.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(TwoWayAdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if(list!=null&&(list.size()-1)==position){
					right_arrows.setVisibility(View.INVISIBLE);
					left_arrows.setVisibility(View.VISIBLE);
				}else if(list!=null&&position==0){
					left_arrows.setVisibility(View.INVISIBLE);
					right_arrows.setVisibility(View.VISIBLE);
				}else{
					right_arrows.setVisibility(View.VISIBLE);
					left_arrows.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onNothingSelected(TwoWayAdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	@Override
	public void show() {
		super.show();
		// player.pause();
		// parentHandler.removeCallbacks(bannerRunnable);
		// parentHandler.postDelayed(bannerRunnable, 5000);
		initData();
		Player.setFirstPlayInShift(true);
		dvbBack();
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		super.hide();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == Class_Constant.KEYCODE_VOICE_UP) {
			mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 0);
			curvolumn = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			Log.i("volumn", "KEYCODE_VOICE_UP curvolumn is"+curvolumn);
			revolumnback.setBackgroundResource(vols[curvolumn]);
			revolumnback.setVisibility(View.VISIBLE);
			if (curvolumn != 0) {
				parentHandler.removeCallbacks(VolumnbackRunnable);
				parentHandler.postDelayed(VolumnbackRunnable, 5000);
			}else {
				parentHandler.removeCallbacks(VolumnbackRunnable);
			}
			return true;
		}
		if (keyCode == Class_Constant.KEYCODE_VOICE_DOWN) {
			mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0);
			curvolumn = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			Log.i("volumn", "KEYCODE_VOICE_DOWN curvolumn is"+curvolumn);
			revolumnback.setBackgroundResource(vols[curvolumn]);
			revolumnback.setVisibility(View.VISIBLE);
			if (curvolumn != 0) {
				parentHandler.removeCallbacks(VolumnbackRunnable);
				parentHandler.postDelayed(VolumnbackRunnable, 5000);
			}else {
				parentHandler.removeCallbacks(VolumnbackRunnable);
			}
			return true;
		}
		
		if (keyCode == Class_Constant.KEYCODE_MUTE){
			curvolumn = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			if (curvolumn == 0) {
				mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
				curvolumn = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
				Log.i("volumn", "huifu");
				revolumnback.setBackgroundResource(vols[curvolumn]);
				revolumnback.setVisibility(View.VISIBLE);
				parentHandler.removeCallbacks(VolumnbackRunnable);
				parentHandler.postDelayed(VolumnbackRunnable, 5000);
			}else {
				mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
				curvolumn = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
				Log.i("volumn", "set mute");
				revolumnback.setBackgroundResource(vols[curvolumn]);
				revolumnback.setVisibility(View.VISIBLE);
				if (VolumnbackRunnable != null) {
					parentHandler.removeCallbacks(VolumnbackRunnable);
				}
			}
			return true;
		}
		
		switch (keyCode) {
		/* 返回--取消 */
		case KeyEvent.KEYCODE_BACK:
			if (bannerView.isShown()) {
				if(programListContainer.isShown()){
					showViewVisibility(NEXT_PROGRAM);
					IsFocusList = false;
				}else{
					showViewVisibility(NOTHING);
				}
				return false;
			} else {
				player.setLiveFlag(false);
				CommonMethod.saveMutesState((whetherMute + ""),
						MyApp.getContext());
				Message msg = new Message();
				msg.what = Class_Constant.PLAY_BACKFROM_SHIFT;
				parentHandler.sendMessage(msg);
				
				ShiftDialog.Builder builder = new ShiftDialog.Builder(mContext);
				builder.setMessage("你确定退出时移电视？");
				builder.setTitle("温馨提示");
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dismiss();
							dialog.dismiss();
							parentHandler.sendEmptyMessage(Class_Constant.BACK_TO_LIVE);
							revolumnback.setVisibility(View.INVISIBLE);
					}
				});

				builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
					}
				});

				builder.create().show();
				
			}
			break;
		case Class_Constant.KEYCODE_DOWN_ARROW_KEY:
			Log.i("zyt", "dialog down key is pressed");
			showViewVisibility(NEXT_PROGRAM);
			IsFocusList = false;
			timeshiftProList.setFocusable(true);
			timeshiftProList.requestFocus();

			break;
		case Class_Constant.KEYCODE_UP_ARROW_KEY:
			Log.i("zyt", "dialog up key is pressed");
			if (bannerView.isShown()){
				timeshiftProList.setFocusable(true);
				timeshiftProList.requestFocus();
				showViewVisibility(PROGRAM_LIST);
				IsFocusList = true;
			}else {
				showViewVisibility(NEXT_PROGRAM);
				IsFocusList = false;
			}


			break;

		case Class_Constant.KEYCODE_RIGHT_ARROW_KEY:
			if (!IsFocusList) {
				bannerView.setVisibility(View.VISIBLE);
				timeshiftback.setVisibility(View.VISIBLE);
				palyButton.setMyBG(PlayButton.Forward);
				parentHandler.removeCallbacks(bannerRunnable);
				parentHandler.postDelayed(bannerRunnable, 5000);
				Player.handleProgress
						.sendEmptyMessage(Class_Constant.LIVE_FAST_FORWARD);
			}

			break;
		case Class_Constant.KEYCODE_LEFT_ARROW_KEY:
			if (!IsFocusList) {
				bannerView.setVisibility(View.VISIBLE);
				timeshiftback.setVisibility(View.VISIBLE);
				palyButton.setMyBG(PlayButton.Backward);
				parentHandler.removeCallbacks(bannerRunnable);
				parentHandler.postDelayed(bannerRunnable, 5000);
				Player.handleProgress
						.sendEmptyMessage(Class_Constant.LIVE_FAST_REVERSE);
			}
		

			break;
		case Class_Constant.KEYCODE_OK_KEY:
			
			if(programListContainer.isShown()){
				player.play();
				break;
			}
			if (player.isPlayerPlaying()) {
				player.pause();
				bannerView.setVisibility(View.VISIBLE);
				timeshiftback.setVisibility(View.VISIBLE);
				palyButton.setMyBG(PlayButton.Play);
				//处理banner还在的时候，按暂停，banner消失
				if (bannerRunnable != null) {
					parentHandler.removeCallbacks(bannerRunnable);
				}
			} else {
				player.play();
				//palyButton.setMyBG(PlayButton.Play);
				bannerView.setVisibility(View.VISIBLE);
				timeshiftback.setVisibility(View.VISIBLE);
				palyButton.setMyBG(PlayButton.Pause);
				//parentHandler.removeCallbacks(bannerRunnable);
				//parentHandler.postDelayed(bannerRunnable, 5000);

			}

			break;

		/*case Class_Constant.KEYCODE_MUTE:// mute
			// int current =
			// audioMgr.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
			whetherMute = !whetherMute;
			CommonMethod.saveMutesState((whetherMute + ""), MyApp.getContext());
			// Log.i("zyt", "keycode mute is " + whetherMute);
			if (muteIconImage.isShown()) {
				muteIconImage.setVisibility(View.GONE);
			} else {
				muteIconImage.setVisibility(View.VISIBLE);
			}
			break;
		case Class_Constant.KEYCODE_VOICE_UP:
		case Class_Constant.KEYCODE_VOICE_DOWN:
			if (muteIconImage.isShown()) {
				muteIconImage.setVisibility(View.GONE);
			}
			// audioMgr.setStreamMute(AudioManager.STREAM_MUSIC, true);
			whetherMute = false;
			CommonMethod.saveMutesState((whetherMute + ""), MyApp.getContext());
			break;*/
		case Class_Constant.KEYCODE_MENU_KEY:
			// Log.i("zyt", "onkeydown menukey is pressed " + keyCode);
			dismiss();
			CommonMethod.startSettingPage(MyApp.getContext());
			break;
		case KeyEvent.KEYCODE_F1://信息按键
			bannerView.setVisibility(View.VISIBLE);
			timeshiftback.setVisibility(View.VISIBLE);
			if (bannerRunnable != null) {
				parentHandler.removeCallbacks(bannerRunnable);
				parentHandler.postDelayed(bannerRunnable, 5000);
			}
			break;
		default:
			nextProgramContainer.setVisibility(View.VISIBLE);
			programListContainer.setVisibility(View.GONE);
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		switch (keyCode) {
		case Class_Constant.KEYCODE_RIGHT_ARROW_KEY:

			if (!IsFocusList) {
				Player.handleProgress
						.sendEmptyMessage(Class_Constant.RE_FAST_FORWARD_UP);
					palyButton.setMyBG(PlayButton.Pause);
			}
			
			break;
		case Class_Constant.KEYCODE_LEFT_ARROW_KEY:

			if(!IsFocusList){
				Player.handleProgress
						.sendEmptyMessage(Class_Constant.RE_FAST_REVERSE_UP);
					palyButton.setMyBG(PlayButton.Pause);
			}
			break;
		}
		if (player.isPlayerPlaying()) {
			Log.i("test", "player.isPlayerPlaying()"+player.isPlayerPlaying());
			parentHandler.removeCallbacks(bannerRunnable);
			parentHandler.postDelayed(bannerRunnable, 5000);
		}

		if (firstInShift) {
			Log.i("test", "firstInShift"+firstInShift);
			if (bannerRunnable != null) {
				parentHandler.removeCallbacks(bannerRunnable);
			}
			firstInShift = false;
		}
		return super.onKeyUp(keyCode, event);
	}

	private void dvbBack() {

		// String equestURL=processData.getReplayPlayUrlString(channel,
		// programListInfo.get(1), 0);
		CacheData.setCurProgram(programListInfo.get(1));
//		Log.i(TAG, "bannerdialog=dvbBack：");
		PlayVideo.getInstance().playTSDelayTime(player, curChannel, 0);
	}


	private Response.ErrorListener errorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError arg0) {
			// TODO Auto-generated method stub
			Log.i(TAG, "bannerdialog=error：" + arg0);
		}
	};

	Runnable bannerRunnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			//bannerView.setVisibility(View.GONE);
//			nextProgramContainer.setVisibility(View.VISIBLE);
//			programListContainer.setVisibility(View.GONE);
			showViewVisibility(NOTHING);
			IsFocusList = false;
		}
	};

	Runnable playBtnRunnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			palyButton.setVisibility(View.INVISIBLE);
		}
	};
	
	Runnable VolumnbackRunnable = new Runnable() {
		@Override
		public void run() {
			revolumnback.setVisibility(View.INVISIBLE);
		}
	};

	private void setTimeShiftProgramList() {
		String URL = processData.get4HoursProgramList(channelInfo);
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
				Request.Method.GET, URL, null,
				new Response.Listener<org.json.JSONObject>() {
					@Override
					public void onResponse(org.json.JSONObject arg0) {
						// TODO Auto-generated method stub
						// 相应成功
//						 Log.i(TAG, "player-getPointProList:" + arg0);
						list = JsonResolve.getInstance()
								.timeShiftPrograms(arg0);
						programListAdapter.setData(list);
						timeshiftProList.requestFocus();
						timeshiftProList.setSelection(list.size() - 1);
					}
				}, null);
		jsonObjectRequest.setTag("program");// 设置tag,cancelAll的时候使用
		mReQueue.add(jsonObjectRequest);
	}

	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		super.dismiss();
		if(player!=null){
			player.stopTimer();
			player.stop();//KEVIN-yang
			player=null;
		}
	}
	
	private void showViewVisibility(int ID){
		
		switch (ID){
		case NEXT_PROGRAM:
			nextProgramContainer.setVisibility(View.VISIBLE);
			programListContainer.setVisibility(View.GONE);
			bannerView.setVisibility(View.VISIBLE);
			timeshiftback.setVisibility(View.VISIBLE);
		break;
		
		case PROGRAM_LIST:
			
			nextProgramContainer.setVisibility(View.INVISIBLE);
			programListContainer.setVisibility(View.VISIBLE);
			bannerView.setVisibility(View.VISIBLE);
			timeshiftback.setVisibility(View.VISIBLE);
			break;
			
		case NOTHING:
			nextProgramContainer.setVisibility(View.VISIBLE);
			programListContainer.setVisibility(View.GONE);
			bannerView.setVisibility(View.GONE);
			timeshiftback.setVisibility(View.GONE);
			break;
		}
	}
}
