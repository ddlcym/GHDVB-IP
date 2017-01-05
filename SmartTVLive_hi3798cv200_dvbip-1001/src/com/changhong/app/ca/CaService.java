/**
 * Ca服务
 */
package com.changhong.app.ca;

import java.util.ArrayList;
import java.util.Random;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Instrumentation;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.changhong.app.dtv.Banner;
import com.changhong.app.dtv.CAMarquee;
import com.changhong.app.dtv.P;
import com.changhong.app.dtv.R;
import com.changhong.app.dtv.SysApplication;
import com.changhong.dvb.CA;
import com.changhong.dvb.CAListener;
import com.changhong.dvb.Channel;
import com.changhong.dvb.ChannelDB;
import com.changhong.dvb.DVB;
import com.changhong.dvb.DVBManager;
import com.changhong.dvb.LivePlayer;
import com.changhong.dvb.ProtoCaNovel.DVB_CA_NOVEL;
import com.changhong.dvb.ProtoCaNovel.DVB_CA_NOVEL_ACTIONREQ;
import com.changhong.dvb.ProtoCaNovel.DVB_CA_NOVEL_ACTIONTYPE;
import com.changhong.dvb.ProtoCaNovel.DVB_CA_NOVEL_CONTINUEWATCHLIMIT_DATA;
import com.changhong.dvb.ProtoCaNovel.DVB_CA_NOVEL_Data;
import com.changhong.dvb.ProtoCaNovel.DVB_CA_NOVEL_ES_INFO;
import com.changhong.dvb.ProtoCaNovel.DVB_CA_NOVEL_FEEDDATA;
import com.changhong.dvb.ProtoCaNovel.DVB_CA_NOVEL_FEEDING_CODE;
import com.changhong.dvb.ProtoCaNovel.DVB_CA_NOVEL_IPPV_Data;
import com.changhong.dvb.ProtoCaNovel.DVB_CA_NOVEL_MSG_CODE;
import com.changhong.dvb.ProtoCaNovel.DVB_CA_NOVEL_PROGRESS_INFO;
import com.changhong.dvb.ProtoCaNovel.DVB_CA_NOVEL_SERVICE_INFO;
import com.changhong.dvb.ProtoCaNovel.DVB_CA_NOVEL_SUPFP_DATA;
import com.changhong.dvb.ProtoCaNovel.DVB_CA_NOVEL_SUPOSD_DATA;
import com.changhong.dvb.ProtoMessage.DVB_CA;
import com.changhong.dvb.ProtoMessage.DVB_CAR_QAM;
import com.changhong.dvb.ProtoMessage.DVB_CA_TYPE;
import com.changhong.dvb.ProtoMessage.DVB_Carrier;
import com.changhong.dvb.ProtoMessage.DVB_CarrierCab;
import com.changhong.dvb.ProtoMessage.DVB_PLAYER_SYNC_CODE;
import com.changhong.dvb.ProtoMessage.DVB_SIGNAL_TYPE;
import com.changhong.dvb.Tuner;
import com.google.protobuf.ByteString;
import com.hisilicon.android.hidisplaymanager.HiDisplayManager;

public class CaService extends Service implements CAListener {

	private static final String TAG = "CaServer";
	private static final String CA_BLACK_LIST = "com.changhong.ca.blacklist";
	private static final String CA_CONTINUE_WATCH_STATUS = "com.changhong.ca.continuewatchlimit.status";
	private static final String CA_CONTINUE_WATCH_WORK_TIME = "com.changhong.ca.continuewatchlimit.worktime";
	private static final String CA_CONTINUE_WATCH_STOP_TIME = "com.changhong.ca.continuewatchlimit.stoptime";

	private static final String PACKAGE = "CaService";
	private SharedPreferences mCaSharePre = null;
	private DVBManager dvbManager;
	private CA thisCa;
	private BroadcastReceiver mReceiver = null;
	private ActivityManager mActivityManager = null;
	// CA 信息盒子
	private FrameLayout mCaMsgMain = null;
	private TextView mCaMsgTxt = null, mCaNoticeTxt = null,
			mCaMsgSupTxt = null;
	private ImageView mCaMsgImg = null;
	private RelativeLayout mCaNoticeMain = null;
	private FrameLayout mCAOsdMain = null;
	private CAMarquee mCAOsd_txt_top = null;
	private CAMarquee mCAOsd_txt_buttom = null;
	private TextView mCAOsd_txt_center = null;
	private WindowManager mWM = null;
	private WindowManager.LayoutParams mParams1, mParams2, mParams3;
	private int screenWidth, screenHeigh;
	private String mCurtainNotice, mContinuesWatch, mBlackList;
	private HiDisplayManager mDisplay = null;
	private SysApplication sysApplication = null;
	public LivePlayer dvbPlayer = null;
	public ChannelDB dvbDatabase = null;
	private Banner banner = null;
	private CaUtils mCaUtils = null;
	private String isCWL = "false";
	private Message saveMsg = null, saveCWLMsg = null;
	boolean isFirstEnter = true;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		initCaServer();
		registerCaReceiver();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	private void initCaServer() {
		dvbManager = DVB.getManager();
		mActivityManager = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
		thisCa = dvbManager.getCaInstance();
		mCaUtils = new CaUtils();
		dvbDatabase = dvbManager.getChannelDBInstance();
		dvbPlayer = dvbManager.getDefaultLivePlayer();
		if (null != dvbPlayer) {
			dvbPlayer.setSyncMode(DVB_PLAYER_SYNC_CODE.SYNC_REF_NONE);
		}
		sysApplication = SysApplication.getInstance();
		sysApplication.initDtvApp(this);
		banner = Banner.getInstance(this);
		mCaSharePre = getSharedPreferences(PACKAGE, 0);
		mDisplay = new HiDisplayManager();
		isFirstEnter = true;

		// set ca
		// set ca type
		ArrayList<DVB_CA_TYPE> caType = new ArrayList<DVB_CA_TYPE>();

		// Vanlen change this order
		caType.add(DVB_CA_TYPE.CA_SUMA);
		caType.add(DVB_CA_TYPE.CA_NOVEL);

		thisCa.setType(caType);
		// mo_Ca.start();

		thisCa.setListener(this);

		initCaMsgBox();
		// 参数定义
		Resources res = getResources();
		mCurtainNotice = res.getString(R.string.CA_CURTAIN_NOTICE);
		mContinuesWatch = res.getString(R.string.CA_CONTINUE_WATCH_LIMIT);
		mBlackList = res.getString(R.string.CA_CONTINUE_BLACK_LIST);

		// // /**
		// // * 接收路由返回信息
		// // */
		// // if (null == mMsgHandler) {
		// // mMsgHandler = new Handler() {
		// // @Override
		// // public void handleMessage(Message msg) {
		// //
		// // switch (msg.what) {
		// //
		// // case CaConfig.CA_COMMAND_SHOW_NOTICE:// 显示提示消息
		// // String notice = (String) msg.obj;
		// // if (null != notice && notice.length() > 0) {
		// // mCaNoticeTxt.setText(notice);
		// // showMsgBox(true);
		// // }
		// // break;
		// //
		// // case CaConfig.CA_COMMAND_FINGER_SHOW:// 显示指纹
		// // boolean isRepeat = false;
		// // mParams1.x = msg.arg1;
		// // mParams1.y = msg.arg2;
		// // if (mParams1.x == 0 && mParams1.y == 0) {
		// // setRandomCoodinate();
		// // }
		// //
		// // if (mParams1.x == screenWidth
		// // || mParams1.y == screenHeigh) {
		// // setRandomCoodinate(mParams1.x, mParams1.y);
		// // isRepeat = true;
		// // }
		// //
		// // String finger = (String) msg.obj;
		// // int txtWidth = mCaUtils.getDesiredWidth(mCaMsgTxt,
		// // finger);
		// //
		// // if (mParams1.x > (screenWidth - txtWidth - 46))
		// // mParams1.x = screenWidth - txtWidth - 46;
		// // if (mParams1.x < 26)
		// // mParams1.x = 26;
		// // if (mParams1.y > (screenHeigh - 60))
		// // mParams1.y = screenHeigh - 60;
		// // if (mParams1.y < 26)
		// // mParams1.y = 26;
		// // mCaMsgTxt.setText(finger);
		// // showMsgBox(false);
		// //
		// // if (isRepeat) {// 超级指纹重复显示处理
		// // Message newMsg = mMsgHandler.obtainMessage();
		// // newMsg.arg1 = msg.arg1;
		// // newMsg.arg2 = msg.arg2;
		// // newMsg.obj = finger;
		// // newMsg.what = CaConfig.CA_COMMAND_FINGER_SHOW;
		// // mMsgHandler.sendMessageDelayed(newMsg, 5000);
		// // }
		// // break;
		// // case CaConfig.CA_COMMAND_SUPEROSD_SHOW:// 超级OSD显示
		// // int position = msg.arg1;
		// // String osdTxt = (String) msg.obj;
		// // showOsd(position, osdTxt);
		// // break;
		// //
		// // case CaConfig.CA_COMMAND_FORCE_SHOW:// 强制显示（超级指纹和超级OSD）
		// //
		// // break;
		// // case CaConfig.CA_COMMAND_LOCK_SERVICE:// 强制换台
		// // int freq = msg.arg1;
		// // tunerTo(freq, 0);
		// // break;
		// // case CaConfig.CA_COMMAND_UNLOCK_SERVICE:// 解除强制换台
		// // // handleCaNovelEvent(10, null);
		// // break;
		// // case CaConfig.CA_COMMAND_SUPEMAIL_NEW:// 新邮件
		// // mCaMsgImg.setVisibility(View.VISIBLE);
		// // break;
		// // case CaConfig.CA_COMMAND_SUPEMAIL_HIDEICON:// 隐藏图标
		// // mCaMsgImg.setVisibility(View.GONE);
		// // break;
		// // case CaConfig.CA_COMMAND_SUPEMAIL_SPACEEXHAUST:// 邮箱已满
		// // if (View.GONE == mCaMsgImg.getVisibility()) {
		// // mCaMsgImg.setVisibility(View.VISIBLE);
		// // } else {
		// // mCaMsgImg.setVisibility(View.GONE);
		// // }
		// // mMsgHandler
		// // .sendEmptyMessageDelayed(
		// // CaConfig.CA_COMMAND_SUPEMAIL_SPACEEXHAUST,
		// // 2000);
		// // break;
		// // case CaConfig.CA_COMMAND_CONTINUSE_WATCH:// 连续观看
		// // // 连续观看限制模式
		// // byte byType = (Byte) msg.obj;
		// // // 清除连续观看限制信息
		// // mMsgHandler
		// // .removeMessages(CaConfig.CA_COMMAND_CONTINUSE_WATCH);
		// // mMsgHandler
		// // .removeMessages(CaConfig.CA_COMMAND_CONTINUSE_WATCH_LIMIT);
		// // if (1 == byType) {// 开启连续观看限制模式
		// // int workTime = msg.arg1;
		// // int stopTime = msg.arg2;
		// // Message msg0 = mMsgHandler.obtainMessage();
		// // msg0.what = CaConfig.CA_COMMAND_CONTINUSE_WATCH_LIMIT;
		// // msg0.arg1 = workTime;
		// // msg0.arg2 = stopTime;
		// // mMsgHandler.sendMessageDelayed(msg0,
		// // workTime * 1000);
		// // }
		// // // 隐藏信息盒子
		// // hideMsgBox();
		// // mCaUtils.setForcedShow(CaService.this, false);
		// //
		// // break;
		// // case CaConfig.CA_COMMAND_CONTINUSE_WATCH_LIMIT:// 连续观看限制
		// // int workTime = msg.arg1;
		// // int stopTime = msg.arg2;
		// //
		// // if (!mContinuesWatch.equals(mCaNoticeTxt.getText())) {
		// // mCaNoticeTxt.setText(mContinuesWatch);
		// //
		// // }
		// // showMsgBox(true);
		// // mCaUtils.setForcedShow(CaService.this, true);
		// // Message msg1 = mMsgHandler.obtainMessage();
		// // msg1.what = CaConfig.CA_COMMAND_CONTINUSE_WATCH;
		// // msg1.arg1 = workTime;
		// // msg1.arg2 = stopTime;
		// // msg1.obj = (byte) 1;
		// // mMsgHandler.sendMessageDelayed(msg1, stopTime * 1000);
		// // break;
		// // case CaConfig.CA_COMMAND_BLACK_LIST:// 黑名单功能
		// //
		// // if (!mBlackList.equals(mCaNoticeTxt.getText())) {
		// // mParams1.x = (screenWidth - mContinuesWatch
		// // .length()) / 2;
		// // mParams1.y = (screenHeigh - 60) / 2;
		// // mCaNoticeTxt.setText(mBlackList);
		// // mWM.updateViewLayout(mCaNoticeTxt, mParams1);
		// // }
		// // mCaNoticeTxt.setVisibility(View.VISIBLE);
		// // break;
		// // case CaConfig.CA_COMMAND_HIDE_MSGBOX:// 关闭信息盒子
		// //
		// // hideMsgBox();
		// // mCaUtils.setForcedShow(CaService.this, false);
		// // break;
		// // case CaConfig.CA_COMMAND_HIDE_OSDBOX:// 关闭OSD盒子
		// // hideOsdBox();
		// // mCaUtils.setForcedShow(CaService.this, false);
		// // break;
		// // case CaConfig.CA_COMMAND_HIDE_PROGRESS:// 隐藏进度条
		// // mCaUtils.closeActivity(CACardUpgrade.class);
		// // break;
		// // default:
		// // break;
		// // }
		// // }
		// // };
		// }
	}

	public boolean isCWL() {
		boolean reVlue = false;
		synchronized (isCWL) {
			if (isCWL.equals("true")) {
				reVlue = true;
			}
		}
		return reVlue;
	}

	public void setCWL(boolean cwl) {
		synchronized (isCWL) {
			this.isCWL = cwl ? "true" : "false";
		}
	}

	/**
	 * 初始化信息盒子
	 */
	private void initCaMsgBox() {

		if (null == mWM) {

			LayoutInflater li_inflater = LayoutInflater.from(this);

			mCaMsgMain = (FrameLayout) li_inflater.inflate(R.layout.ca_msg_box,
					null);

			mCaMsgTxt = (TextView) mCaMsgMain.findViewById(R.id.ca_msg_txt);
			mCaMsgImg = (ImageView) mCaMsgMain.findViewById(R.id.ca_msg_img);
			mCaMsgSupTxt = (TextView) mCaMsgMain
					.findViewById(R.id.ca_msg_supertxt);

			// 通知信息
			mCaNoticeMain = (RelativeLayout) li_inflater.inflate(R.layout.ca,
					null);
			;
			mCaNoticeTxt = (TextView) mCaNoticeMain
					.findViewById(R.id.id_root_ca_info);

			// OSD信息
			mCAOsdMain = (FrameLayout) li_inflater.inflate(R.layout.osd_roll,
					null);
			mCAOsd_txt_top = (CAMarquee) mCAOsdMain
					.findViewById(R.id.id_osd_roll_top);
			mCAOsd_txt_buttom = (CAMarquee) mCAOsdMain
					.findViewById(R.id.id_osd_roll_buttom);
			mCAOsd_txt_center = (TextView) mCAOsdMain
					.findViewById(R.id.id_osd_center);

			// 获取屏幕宽高
			mWM = (WindowManager) getApplicationContext().getSystemService(
					Context.WINDOW_SERVICE);
			screenWidth = mWM.getDefaultDisplay().getWidth();
			screenHeigh = mWM.getDefaultDisplay().getHeight();

			mParams1 = new WindowManager.LayoutParams();// 对对象的参数描述对象
			mParams1.height = WindowManager.LayoutParams.WRAP_CONTENT;// 宽度自适应
			mParams1.width = WindowManager.LayoutParams.WRAP_CONTENT;// 高度自适应
			mParams1.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;// 设置成不能获取焦点
			mParams1.format = PixelFormat.RGBA_8888;
			mParams1.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
			mParams1.gravity = Gravity.CENTER;
			mParams1.alpha = 1.0f;
			// 设置悬浮窗口长宽数据
			mWM.addView(mCaNoticeMain, mParams1);

			mParams3 = new WindowManager.LayoutParams();// 对对象的参数描述对象
			mParams3.height = WindowManager.LayoutParams.WRAP_CONTENT;// 宽度自适应
			mParams3.width = WindowManager.LayoutParams.WRAP_CONTENT;// 高度自适应
			mParams3.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;// 设置成不能获取焦点
			mParams3.format = PixelFormat.RGBA_8888;
			mParams3.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
			mParams3.gravity = Gravity.TOP | Gravity.LEFT;
			mParams3.alpha = 1.0f;
			// 设置悬浮窗口长宽数据
			mWM.addView(mCaMsgMain, mParams3);

			mParams2 = new WindowManager.LayoutParams();// 对对象的参数描述对象
			mParams2.height = screenHeigh;// 宽度自适应
			mParams2.width = screenWidth;// 高度自适应
			mParams2.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;// 设置成不能获取焦点
			mParams2.format = PixelFormat.RGBA_8888;
			mParams2.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
			mParams2.gravity = Gravity.TOP | Gravity.LEFT;
			mParams2.alpha = 1.0f;
			mParams2.x = 0;
			mParams2.y = 0;
			mWM.addView(mCAOsdMain, mParams2);

		}
	}

	// 预处理Ca未处理完成的任务
	private void preCaSaveTask() {
		if (null == mCaSharePre) {
			mCaSharePre = getSharedPreferences(PACKAGE, 0);
			mDisplay = new HiDisplayManager();
		} else {
			if (!isFirstEnter && null == saveMsg)
				return;
		}
		// 黑名单
		int blackList = mCaSharePre.getInt(CA_BLACK_LIST, 0);
		if (blackList > 0) {
			mMsgHandler.sendEmptyMessage(CaConfig.CA_COMMAND_BLACK_LIST);
		}

		// 连续观看限制
		int status = mCaSharePre.getInt(CA_CONTINUE_WATCH_STATUS, 0);
		int workTime = mCaSharePre.getInt(CA_CONTINUE_WATCH_WORK_TIME, 0);
		int stopTime = mCaSharePre.getInt(CA_CONTINUE_WATCH_STOP_TIME, 0);
		if (workTime > 0 && stopTime > 0) {
			if (null == saveCWLMsg) {
				Message msg = mMsgHandler.obtainMessage();
				msg.what = CaConfig.CA_COMMAND_CONTINUSE_WATCH_LIMIT;
				msg.arg1 = workTime;
				msg.arg2 = stopTime;
				msg.obj = 1;
				mMsgHandler.sendMessage(msg);
			} else {
				if (mMsgHandler
						.hasMessages(CaConfig.CA_COMMAND_CONTINUSE_WATCH_LIMIT_LOOP)) {
					mMsgHandler
							.removeMessages(CaConfig.CA_COMMAND_CONTINUSE_WATCH_LIMIT_LOOP);
				}
				status = (Integer) saveCWLMsg.obj;
				saveCWLMsg.obj = (0 == status) ? 1 : 0;
				mMsgHandler
						.sendEmptyMessage(CaConfig.CA_COMMAND_CONTINUSE_WATCH_LIMIT_LOOP);
			}
		}
		saveMsg = null;
		isFirstEnter = false;
	}

	/**
	 * 执行CA同方的信息指令
	 * 
	 * @param cmd
	 * @param msg
	 */
	private void handleCaNovelEvent(int cmdCode, DVB_CA_NOVEL data) {

		Message msg = null;
		DVB_CA_NOVEL_Data caData = null;
		boolean isForcedShow = false;
		saveMsg = null;

		switch (cmdCode) {
		case CaConfig.NOVEL_CODE_CB_SHOW_BUY_MSG:
			msg = mMsgHandler.obtainMessage();
			if (null != data && data.hasMsgcode()) {
				int msgCode = data.getMsgcode().getNumber();

				if (DVB_CA_NOVEL_MSG_CODE.NOVEL_MSG_CODE_CANCEL_VALUE == msgCode) {
					msg.what = CaConfig.CA_COMMAND_HIDE_MSGBOX;
				} else {
					msg.obj = getNovelNoticeContent(data.getMsgcode());
					if (null == msg.obj) {
						msg.what = CaConfig.CA_COMMAND_HIDE_MSGBOX;
					} else {
						msg.what = CaConfig.CA_COMMAND_SHOW_NOTICE;
					}
				}
				mMsgHandler.sendMessage(msg);
			}
			break;

		case CaConfig.NOVEL_CODE_CB_SHOW_FINGER:// 显示指纹
			mMsgHandler.removeMessages(CaConfig.CA_COMMAND_FINGER_SHOW);
			if (null != data)
				caData = data.getData();

			msg = mMsgHandler.obtainMessage();
			msg.arg1 = msg.arg2 = 0;
			if (null != caData && caData.getDataStringCount() > 0) {
				msg.obj = caData.getDataString(0);
				msg.what = CaConfig.CA_COMMAND_FINGER_SHOW;
				mMsgHandler.sendMessage(msg);
			}
			break;

		case CaConfig.NOVEL_CODE_CB_SHOW_IPPV_DLG:// 显示IPPV购买对话框

			if (null != data && data.hasIppvdata()) {
				ActivityManager am = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
				ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
				if (!cn.getClassName().contains("CABuyIPPDialog")) {
					DVB_CA_NOVEL_IPPV_Data ippData = data.getIppvdata();
					int msgType = ippData.getIppvSeg(0).getNumber();
					Intent intent = new Intent(this, CABuyIPPDialog.class);
					intent.putExtra("ippData", ippData);
					intent.putExtra("msgType", msgType);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				}
			} else {
				Intent intent = new Intent(this, CABuyIPPDialog.class);
				intent.putExtra("msgType", 2);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
			break;
		case CaConfig.NOVEL_CODE_CB_HIDE_IPPV_DLG:// 隐藏IPPV对话框
			mCaUtils.closeActivity(CABuyIPPDialog.class);
		case CaConfig.NOVEL_CODE_CB_UNLOCK_SERVICE:// 解锁服务
			mCaUtils.setForcedShow(this, false);
			break;
		case CaConfig.NOVEL_CODE_CB_PARENT_FEED:// 子母卡
			if (null != data && data.hasFeedData()) {
				int feedCodeNum = -1;
				byte feedDataB = 0;
				DVB_CA_NOVEL_FEEDDATA feedData = data.getFeedData();
				DVB_CA_NOVEL_FEEDING_CODE feedCode = data.getFeedingStatus();
				if (null != feedCode)
					feedCodeNum = feedCode.getNumber();
				Intent intent = new Intent();
				intent.putExtra("feedCodeNum", feedCodeNum);
				intent.setAction(CaConfig.CA_MSG_PARENT_FEED);
				sendBroadcast(intent);
			}
			break;
		case CaConfig.NOVEL_CODE_CB_LOCK_SERVICE:// 锁定服务
			boolean isLock = true;
			int freq = 0,
			audPid = 0,
			vidPid = 0;
			if (null != data && data.hasServiceInfo()) {
				DVB_CA_NOVEL_SERVICE_INFO service = data.getServiceInfo();
				if (service.hasFreq()) {
					freq = service.getFreq();
				}
				if (service.getAudDataCount() > 0) {
					DVB_CA_NOVEL_ES_INFO audData = service.getAudData(0);
					audPid = audData.getEsPid();
				}
				if (service.getVidDataCount() > 0) {
					DVB_CA_NOVEL_ES_INFO audData = service.getVidData(0);
					vidPid = audData.getEsPid();
				}
			}
			if (isLock) {// 锁定服务
				mCaUtils.setForcedShow(this, false);
			}
			playChannel(freq, vidPid, audPid);
			break;
		case CaConfig.NOVEL_CODE_CB_PROGRESS_DISPLAY:// 进度显示
			if (null != data && data.hasProgressStrip()) {
				int type = 0, percent = 0;
				DVB_CA_NOVEL_PROGRESS_INFO progressInfor = data
						.getProgressStrip();
				if (progressInfor.getMarkCount() > 0) {
					type = progressInfor.getMark(0);
				}
				if (progressInfor.getProgressPercentCount() > 0) {
					percent = progressInfor.getProgressPercent(0);
				}
				ComponentName cn = mActivityManager.getRunningTasks(1).get(0).topActivity;
				if (!cn.getClassName().contains("CACardUpgrade")) {
					Intent intent = new Intent(this, CACardUpgrade.class);
					intent.putExtra("type", type);
					intent.putExtra("percent", percent);
					intent.putExtra("msgType", "show");
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);

				} else {
					Intent intent = new Intent();
					intent.putExtra("type", type);
					intent.putExtra("percent", percent);
					intent.putExtra("msgType", "show");
					intent.setAction(CaConfig.CA_MSG_CARD_UPGRATE);
					sendBroadcast(intent);

					if (1 == type && 100 == percent) {
						mMsgHandler.sendEmptyMessageDelayed(
								CaConfig.CA_COMMAND_HIDE_PROGRESS, 16000);
					}
				}
				mMsgHandler.sendEmptyMessageDelayed(CaConfig.CA_COMMAND_REBOOT, 20000);	
			}

			break;
		case CaConfig.NOVEL_CODE_CB_ACTIONREQ:// 机顶盒通知消息

			if (null != data && data.hasActionReqInfo()) {
				DVB_CA_NOVEL_ACTIONREQ actionReq = data.getActionReqInfo();
				if (actionReq.getActionTpyeCount() > 0) {
					DVB_CA_NOVEL_ACTIONTYPE action = actionReq.getActionTpye(0);
					if (null != action) {
						doNovelActionRequest(action.getNumber());
					}
				}
			}
			break;
		case CaConfig.NOVEL_CODE_CB_SHOW_CURTAIN:// 窗帘显示
			if (null != data)
				caData = data.getData();

			msg = mMsgHandler.obtainMessage();
			if (null != caData && caData.getDataByteCount() > 0) {
				ByteString byteStr = caData.getDataByte(0);
				if (null != byteStr && !byteStr.isEmpty()) {
					msg.obj = byteStr.byteAt(0);
					msg.what = CaConfig.CA_COMMAND_SHOW_CURTAIN;
					mMsgHandler.sendMessage(msg);
				} else {
					msg.obj = (byte) 0;
					msg.what = CaConfig.CA_COMMAND_SHOW_CURTAIN;
					mMsgHandler.sendMessage(msg);
				}

			}

			break;
		case CaConfig.NOVEL_CODE_CB_SHOW_SUPFP:// 超级指纹

			mMsgHandler.removeMessages(CaConfig.CA_COMMAND_FINGER_SHOW);
			msg = mMsgHandler.obtainMessage();
			if (null != data && data.hasSupFPData()) {
				DVB_CA_NOVEL_SUPFP_DATA supFP = data.getSupFPData();
				if (supFP.getPositionH16XL16YCount() > 0) {
					H16AndL16 tempPosition = mCaUtils.getH16AndL16(supFP
							.getPositionH16XL16Y(0));
					msg.arg1 = screenWidth * tempPosition.H16 / 100;
					msg.arg2 = screenHeigh * tempPosition.L16 / 100;
					if (0 == msg.arg1 && 0 == msg.arg2) {
						msg.arg1 = msg.arg2 = 1;
					}

					if ((0 == tempPosition.H16 && 0 == tempPosition.L16)
							|| (100 == tempPosition.H16 && 100 == tempPosition.L16)
							|| (99 == tempPosition.H16 && 0 == tempPosition.L16)) {
						isForcedShow = true;
					}

				} else {
					msg.arg1 = msg.arg2 = 0;
				}
				msg.obj = supFP.getFPString(0);
				msg.what = CaConfig.CA_COMMAND_FINGER_SHOW;
				mMsgHandler.sendMessage(msg);

				// 强制显示
				if (supFP.getIsForcedShowCount() > 0) {
					if (supFP.getIsForcedShow(0)) {
						isForcedShow = true;
					}
				}

				if (isForcedShow) {
					mCaUtils.setForcedShow(this, true);
				}

			}
			break;

		case CaConfig.NOVEL_CODE_CB_SHOW_OSD:// 显示OSD

			msg = mMsgHandler.obtainMessage();
			msg.what = CaConfig.CA_COMMAND_SUPEROSD_SHOW;
			if (null != data)
				caData = data.getData();
			if (null != caData) {
				msg.arg1 = 1;
				if (caData.getDataIntCount() > 0) {
					msg.arg1 = caData.getDataInt(0);
				}
				if (caData.getDataStringCount() > 0) {
					msg.obj = caData.getDataString(0);
				}
				mMsgHandler.sendMessage(msg);
			}
			break;

		case CaConfig.NOVEL_CODE_CB_SHOW_SUPOSD:// 超级OSD

			msg = mMsgHandler.obtainMessage();
			if (null != data && data.hasSupOSDData()) {
				msg.arg1 = CaConfig.CA_MSG_OSD_POSITION_CENTER;
				DVB_CA_NOVEL_SUPOSD_DATA supOsd = data.getSupOSDData();
				if (supOsd.getDisplayModeCount() > 0) {
					msg.arg1 = supOsd.getDisplayMode(0);
				}
				if (supOsd.getOSDStringCount() > 0) {
					msg.obj = supOsd.getOSDString(0);
				}
				msg.what = CaConfig.CA_COMMAND_SUPEROSD_SHOW;
				mMsgHandler.sendMessage(msg);
				// 强制显示
				if (supOsd.getIsForcedShowCount() > 0) {
					if (supOsd.getIsForcedShow(0)) {
						mCaUtils.setForcedShow(this, true);
					}
				}
			}
			break;
		case CaConfig.NOVEL_CODE_CB_CONTINUEWATCHLIMIT:// 连续观看限制
			if (null != data && data.hasCWLData()) {
				DVB_CA_NOVEL_CONTINUEWATCHLIMIT_DATA cwldata = data
						.getCWLData();
				if (!cwldata.hasCtrlType() || cwldata.getCtrlType().isEmpty())
					return;

				byte type = ((ByteString) cwldata.getCtrlType()).byteAt(0);

				msg = mMsgHandler.obtainMessage();
				msg.arg1 = msg.arg2 = 0;

				if (0 == type) {// 禁止连续观看限制
					msg.what = CaConfig.CA_COMMAND_CONTINUSE_WATCH;
					setCWL(false);
					// mCaUtils.setForcedShow(this, false);
					// playChannel();
				} else if (1 == type) {// 启用连续观看限制
					if (cwldata.getTimeH16WorkL16StopCount() > 0) {
						H16AndL16 tempTime = mCaUtils.getH16AndL16(cwldata
								.getTimeH16WorkL16Stop(0));
						msg.arg1 = tempTime.H16 * 3600;
						msg.arg2 = tempTime.L16 * 60;
						msg.obj = 1;
						msg.what = CaConfig.CA_COMMAND_CONTINUSE_WATCH_LIMIT;
						msg.arg1 = msg.arg2 = 30;
						setCWL(true);
					}

				} else {// 取消连续观看限制
					msg.what = CaConfig.CA_COMMAND_CONTINUSE_WATCH;
					setCWL(false);
				}
				mMsgHandler.sendMessage(msg);
			}

			break;

		case CaConfig.NOVEL_CODE_CB_HIDE_SUPOSD:// 隐藏超级OSD
			mCaUtils.setForcedShow(this, false);
		case CaConfig.NOVEL_CODE_CB_HIDE_OSD:// 隐藏OSD
			mMsgHandler.sendEmptyMessage(CaConfig.CA_COMMAND_HIDE_OSDBOX);
			break;
		case CaConfig.NOVEL_CODE_CB_HIDE_SUPFP:// 隐藏超级指纹
		case CaConfig.NOVEL_CODE_CB_HIDE_FINGER:// 隐藏指纹
			mMsgHandler.removeMessages(CaConfig.CA_COMMAND_FINGER_SHOW);
			mMsgHandler.sendEmptyMessage(CaConfig.CA_COMMAND_HIDE_MSGBOX);
			mCaUtils.setForcedShow(this, false);
			break;

		}

	}

	/**
	 * 匹配本地的CA命令
	 * 
	 * @param cmd
	 * @return
	 */
	private int matchCaCmd(int cmd) {
		// TODO Auto-generated method stub
		return 0;
	}

	private void setRandomCoodinate() {
		Random ran = new Random(System.currentTimeMillis());
		mParams3.x = ran.nextInt(screenWidth);
		mParams3.y = ran.nextInt(screenHeigh);
	}

	private void setRandomCoodinate(int x, int y) {
		Random ran = new Random(System.currentTimeMillis());
		if (screenWidth == x || screenHeigh == y) {
			mParams3.x = ran.nextInt(screenWidth);
			mParams3.y = ran.nextInt(screenHeigh);
		}
	}

	/**
	 * 执行机顶盒通知包请求命令
	 * 
	 * @param code
	 * @return
	 */
	private void doNovelActionRequest(int code) {

		switch (code) {
		case CaConfig.CA_NOVEL_ACTIONREQUEST_RESTARTSTB: {/* 重启机顶盒 */
			Message msg = mMsgHandler.obtainMessage();
			msg.arg1 = code;
			msg.obj = getResources().getString(R.string.MESSAGE_STBLOCKED_TYPE);
			msg.what = CaConfig.CA_COMMAND_SHOW_NOTICE;
			mMsgHandler.sendMessage(msg);
//			stopPlay(true);
			
			mMsgHandler.sendEmptyMessageDelayed(CaConfig.CA_COMMAND_REBOOT, 20000);

			break;
		}
		case CaConfig.CA_NOVEL_ACTIONREQUEST_FREEZESTB: {/* 冻结机顶盒 */
			Message msg = mMsgHandler.obtainMessage();
			msg.arg1 = code;
			msg.obj = getResources().getString(R.string.MESSAGE_STBFREEZE_TYPE);
			msg.what = CaConfig.CA_COMMAND_SHOW_NOTICE;
			mMsgHandler.sendMessage(msg);
			stopPlay(true);
			break;
		}
		case CaConfig.CA_NOVEL_ACTIONREQUEST_SEARCHCHANNEL: {/* 重新搜索节目 */
			break;
		}
		case CaConfig.CA_NOVEL_ACTIONREQUEST_STBUPGRADE: {/* 机顶盒程序升级 */
			break;
		}
		case CaConfig.CA_NOVEL_ACTIONREQUEST_UNFREEZESTB: {/* 解除冻结机顶盒 */
			mMsgHandler.sendEmptyMessage(CaConfig.CA_COMMAND_HIDE_MSGBOX);
			playChannel();
			break;
		}
		case CaConfig.CA_NOVEL_ACTIONREQUEST_INITIALIZESTB: { /* 初始化机顶盒 */
			factoryReset();
			break;
		}
		case CaConfig.CA_NOVEL_ACTIONREQUEST_SHOWSYSTEMINFO: { /* 显示系统信息 */
			try {
				Intent intent = new Intent();
				ComponentName name = new ComponentName("com.SysSettings.main",
						"com.SysSettings.main.MainActivity");
				intent.setComponent(name);
				intent.putExtra("StartId", 12);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			} catch (Exception e) {
				Toast.makeText(this, "请先安装该app", Toast.LENGTH_SHORT).show();
			}
			break;
		}
		case CaConfig.CA_NOVEL_ACTIONREQUEST_DISABLEEPGADINFO: {/* 禁用EPG广告信息功能 */
			break;
		}
		case CaConfig.CA_NOVEL_ACTIONREQUEST_ENABLEEPGADINFO: {/* 恢复EPG广告信息功能 */
			break;
		}
		case CaConfig.CA_NOVEL_ACTIONREQUEST_CARDINSERTFINISH: {/* 插卡处理完成 */
			break;
		}

		default: {
		}
		}
	}

	public void showOsd(int position, String text) {
		String osdRollText = text;

		if (position == 0) {// center
			mCAOsd_txt_center.setText(osdRollText);
			if (!mCAOsdMain.isShown() || !mCAOsd_txt_center.isShown()) {
				mCAOsdMain.setVisibility(View.VISIBLE);
				mCAOsd_txt_center.setVisibility(View.VISIBLE);
				// FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)
				// mCAOsd_txt_center
				// .getLayoutParams();
				// params.leftMargin = (screenWidth - 520) / 2;
				// if (text.length() > 256-30) {
				// params.height = FrameLayout.LayoutParams.WRAP_CONTENT;
				// params.topMargin = (screenHeigh - text.length()-20) / 2;
				// } else {
				// params.height = 256;
				// params.topMargin = (screenHeigh - 256) / 2;
				// }
				// mCAOsd_txt_center.setLayoutParams(params);
			}
		} else if (position == 1) {// top
			mCAOsd_txt_top.setText(osdRollText);
			if (!mCAOsdMain.isShown() || !mCAOsd_txt_top.isShown()) {
				mCAOsdMain.setVisibility(View.VISIBLE);
				mCAOsd_txt_top.setVisibility(View.VISIBLE);
				mCAOsd_txt_top.init(mWM);
				mCAOsd_txt_top.startScroll();
			}
		} else {// buttom, fullscreen, halfscreen

			mCAOsd_txt_buttom.setText(osdRollText);
			if (!mCAOsdMain.isShown() || !mCAOsd_txt_buttom.isShown()) {
				mCAOsdMain.setVisibility(View.VISIBLE);
				mCAOsd_txt_buttom.setVisibility(View.VISIBLE);
				mCAOsd_txt_buttom.init(mWM);
				mCAOsd_txt_buttom.startScroll();
			}
		}

	}

	/**
	 * 隐藏信息盒子
	 */
	private void hideMsgBox() {
		// MSG
		mCaMsgTxt.setVisibility(View.GONE);
		mCaMsgImg.setVisibility(View.GONE);
		mCaNoticeMain.setVisibility(View.INVISIBLE);
		mCaMsgMain.setVisibility(View.INVISIBLE);

	}

	/**
	 * 隐藏OSD盒子
	 */
	private void hideOsdBox() {
		// OSD显示框
		mCAOsd_txt_top.setVisibility(View.GONE);
		mCAOsd_txt_buttom.setVisibility(View.GONE);
		mCAOsd_txt_center.setVisibility(View.GONE);
		mCAOsdMain.setVisibility(View.INVISIBLE);

	}

	private void showMsgBox(boolean isCenter) {

		if (isCenter) {
			mCaNoticeMain.setVisibility(View.VISIBLE);
			mWM.updateViewLayout(mCaNoticeMain, mParams1);

		} else {
			mCaMsgTxt.setVisibility(View.VISIBLE);
			mCaMsgImg.setVisibility(View.GONE);
			mCaMsgMain.setVisibility(View.VISIBLE);
			mWM.updateViewLayout(mCaMsgMain, mParams3);

		}

	}

	private void showImgBox() {
		mParams3.x = 200;
		mParams3.y = 300;
		mCaMsgTxt.setVisibility(View.GONE);
		mCaMsgImg.setVisibility(View.VISIBLE);
		mCaMsgMain.setVisibility(View.VISIBLE);
		mWM.updateViewLayout(mCaMsgMain, mParams3);
	}

	private void hideImgBox() {
		mCaMsgImg.setVisibility(View.GONE);
		mCaMsgMain.setVisibility(View.GONE);
	}

	private void showMsgSuperTxt() {
		mParams3.x = 50;
		mParams3.y = 200;
		mCaMsgTxt.setVisibility(View.GONE);
		mCaMsgImg.setVisibility(View.GONE);
		mCaMsgSupTxt.setVisibility(View.VISIBLE);
		mCaMsgMain.setVisibility(View.VISIBLE);
		mWM.updateViewLayout(mCaMsgMain, mParams3);
	}

	private void hideMsgSuperTxt() {
		mCaMsgSupTxt.setVisibility(View.GONE);
		mCaMsgMain.setVisibility(View.GONE);
	}

	private String getNovelNoticeContent(DVB_CA_NOVEL_MSG_CODE code) {
		String noticeContent = null;
		int stringId = 0;

		switch (code) {
		case NOVEL_MSG_CODE_BADCARD_TYPE: {
			stringId = R.string.MESSAGE_CALLBACK_TYPE;
			break;
		}
		case NOVEL_MSG_CODE_EXPICARD_TYPE: {
			stringId = R.string.MESSAGE_EXPICARD_TYPE;
			break;
		}
		case NOVEL_MSG_CODE_INSERTCARD_TYPE: {
			stringId = R.string.MESSAGE_INSERTCARD_TYPE;
			break;
		}
		case NOVEL_MSG_CODE_NOOPER_TYPE: {
			stringId = R.string.MESSAGE_NOOPER_TYPE;
			break;
		}
		case NOVEL_MSG_CODE_BLACKOUT_TYPE: {
			stringId = R.string.MESSAGE_BLACKOUT_TYPE;
			break;
		}
		case NOVEL_MSG_CODE_OUTWORKTIME_TYPE: {
			stringId = R.string.MESSAGE_OUTWORKTIME_TYPE;
			break;
		}
		case NOVEL_MSG_CODE_WATCHLEVEL_TYPE: {
			stringId = R.string.MESSAGE_WATCHLEVEL_TYPE;
			break;
		}
		case NOVEL_MSG_CODE_PAIRING_TYPE: {
			stringId = R.string.MESSAGE_PAIRING_TYPE;
			break;
		}
		case NOVEL_MSG_CODE_NOENTITLE_TYPE: {
			stringId = R.string.MESSAGE_NOENTITLE_TYPE;
			break;
		}
		case NOVEL_MSG_CODE_DECRYPTFAIL_TYPE: {
			stringId = R.string.MESSAGE_DECRYPTFAIL_TYPE;
			break;
		}
		case NOVEL_MSG_CODE_NOMONEY_TYPE: {
			stringId = R.string.MESSAGE_NOMONEY_TYPE;
			break;
		}
		case NOVEL_MSG_CODE_ERRREGION_TYPE: {
			stringId = R.string.MESSAGE_ERRREGION_TYPE;
			break;
		}
		case NOVEL_MSG_CODE_NEEDFEED_TYPE: {
			stringId = R.string.MESSAGE_NEEDFEED_TYPE;
			break;
		}
		case NOVEL_MSG_CODE_ERRCARD_TYPE: {
			stringId = R.string.MESSAGE_ERRCARD_TYPE;
			break;
		}
		case NOVEL_MSG_CODE_UPDATE_TYPE: {
			stringId = R.string.MESSAGE_UPDATE_TYPE;
			break;
		}
		case NOVEL_MSG_CODE_LOWCARDVER_TYPE: {
			stringId = R.string.MESSAGE_LOWCARDVER_TYPE;
			break;
		}
		case NOVEL_MSG_CODE_VIEWLOCK_TYPE: {
			stringId = R.string.MESSAGE_VIEWLOCK_TYPE;
			break;
		}
		case NOVEL_MSG_CODE_MAXRESTART_TYPE: {
			stringId = R.string.MESSAGE_MAXRESTART_TYPE;
			break;
		}
		case NOVEL_MSG_CODE_FREEZE_TYPE: {
			stringId = R.string.MESSAGE_FREEZE_TYPE;
			break;
		}
		case NOVEL_MSG_CODE_CALLBACK_TYPE: {
			stringId = R.string.MESSAGE_CALLBACK_TYPE;
			break;
		}
		case NOVEL_MSG_CODE_CURTAIN_TYPE: {
			// stringId = R.string.MESSAGE_CURTAIN_TYPE;
			break;
		}
		case NOVEL_MSG_CODE_CARDTESTSTART_TYPE: {
			stringId = R.string.MESSAGE_CARDTESTSTART_TYPE;
			break;
		}
		case NOVEL_MSG_CODE_CARDTESTFAILD_TYPE: {
			stringId = R.string.MESSAGE_CARDTESTFAILD_TYPE;
			break;
		}
		case NOVEL_MSG_CODE_CARDTESTSUCC_TYPE: {
			stringId = R.string.MESSAGE_CARDTESTSUCC_TYPE;
			break;
		}
		case NOVEL_MSG_CODE_NOCALIBOPER_TYPE: {
			stringId = R.string.MESSAGE_NOCALIBOPER_TYPE;
			break;
		}
		case NOVEL_MSG_CODE_STBLOCKED_TYPE: {
			stringId = R.string.MESSAGE_STBLOCKED_TYPE;
			break;
		}
		case NOVEL_MSG_CODE_STBFREEZE_TYPE: {
			stringId = R.string.MESSAGE_STBFREEZE_TYPE;
			break;
		}
		default: {
			return null;
		}
		}

		try {
			noticeContent = getResources().getString(stringId);
		} catch (NotFoundException e) {
			noticeContent = null;
		}

		return noticeContent;
	}

	@Override
	public void onDestroy() {
		// 保存当前CA信息
		saveCaTask();
		unregisterReceiver(mReceiver);
		saveCWLMsg = saveMsg = null;
		super.onDestroy();
	}

	/************************************** 保存当前CA状态 *****************************************************************/

	// 保存Ca未处理完成的任务

	private void saveCaTask() {
		if (null == saveMsg) {
			mCaSharePre.edit().putInt(CA_BLACK_LIST, 0).commit();
			mCaSharePre.edit().putInt(CA_CONTINUE_WATCH_STATUS, 0).commit();
			mCaSharePre.edit().putInt(CA_CONTINUE_WATCH_WORK_TIME, 0).commit();
			mCaSharePre.edit().putInt(CA_CONTINUE_WATCH_STOP_TIME, 0).commit();
			return;
		}
		int cmd = saveMsg.what;

		if (CaConfig.CA_COMMAND_BLACK_LIST == cmd) {// 黑名单

			mCaSharePre.edit().putInt(CA_BLACK_LIST, 1).commit();
			mCaSharePre.edit().putInt(CA_CONTINUE_WATCH_STATUS, 0).commit();
			mCaSharePre.edit().putInt(CA_CONTINUE_WATCH_WORK_TIME, 0).commit();
			mCaSharePre.edit().putInt(CA_CONTINUE_WATCH_STOP_TIME, 0).commit();

		} else if (CaConfig.CA_COMMAND_CONTINUSE_WATCH_LIMIT == cmd) {// 连续播放限制
			int workTime = 0;
			int stopTime = 0;
			int status = 0;
			if (null != saveCWLMsg) {
				workTime = saveCWLMsg.arg1;
				stopTime = saveCWLMsg.arg2;
				status = (Integer) saveCWLMsg.obj;
				status = (1 == status) ? 1 : 0;
			}
			mCaSharePre.edit().putInt(CA_CONTINUE_WATCH_STATUS, status)
					.commit();
			mCaSharePre.edit().putInt(CA_CONTINUE_WATCH_WORK_TIME, workTime)
					.commit();
			mCaSharePre.edit().putInt(CA_CONTINUE_WATCH_STOP_TIME, stopTime)
					.commit();
			mCaSharePre.edit().putInt(CA_BLACK_LIST, 0).commit();

		} else {
			mCaSharePre.edit().putInt(CA_BLACK_LIST, 0).commit();
			mCaSharePre.edit().putInt(CA_CONTINUE_WATCH_STATUS, 0).commit();
			mCaSharePre.edit().putInt(CA_CONTINUE_WATCH_WORK_TIME, 0).commit();
			mCaSharePre.edit().putInt(CA_CONTINUE_WATCH_STOP_TIME, 0).commit();
		}
		// 保存信息
		mDisplay.saveParam();
	}

	private void saveCaCWlStatus(int status) {
		if (null != saveCWLMsg) {
			if (0 == status || 1 == status) {
				mCaSharePre.edit().putInt(CA_CONTINUE_WATCH_STATUS, status)
						.commit();
				// 保存信息
				mDisplay.saveParam();
			}
		}
	}

	/***************************************** 注册 广播 接收CA消息 *****************************************************************/
	/**
	 * 注册CA设置命令接收广播
	 * */
	private void registerCaReceiver() {
		if (null == mReceiver) {
			IntentFilter mFilter = new IntentFilter();
			// mFilter.addAction(CaConfig.CA_MSG_NOVEL_EVENT);
			mFilter.addAction(CaConfig.CA_MSG_CHANGE_STATUS);

			mReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					String action = intent.getAction();
					if (CaConfig.CA_MSG_NOVEL_EVENT.equals(action)) {

						int eventType = intent.getIntExtra("eventType", -1);
						int cmdID = intent.getIntExtra("cmdCode", -1);
						Log.i("YDINFOR", "receive Ca CMD =" + cmdID);
						DVB_CA_NOVEL caData = (DVB_CA_NOVEL) intent
								.getExtra("caData");
						if (null != caData)
							Log.i("YDINFOR", "receive Ca msg is" + cmdID);
						if (2 == eventType) {
							handleCaNovelEvent(cmdID, caData);
						}
					} else if (CaConfig.CA_MSG_CHANGE_STATUS.equals(action)) {
						preCaSaveTask();
					}
				}
			};
			registerReceiver(mReceiver, mFilter);
		}
	}

	/************************************************* 模拟 按键 操作 ************************************************************/
	public static void simulateKeystroke(final int keyCode) {

		new Thread(new Runnable() {

			public void run() {
				// TODO Auto-generated method stub
				try {

					Instrumentation inst = new Instrumentation();
					inst.sendKeyDownUpSync(keyCode);

				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}).start();
	}

	/*************************************************** add for DVB 增加对DVB控制 **********************************************/

	/**
	 * 调频到指定的频点
	 * 
	 * @param frequency
	 * @param channelID
	 */

	private void tunerTo(int frequency, int channelID) {
		Tuner mTuner = DVB.getManager().getTunerInstance();
		DVB_Carrier.Builder mCarrier = DVB_Carrier.newBuilder();
		DVB_CarrierCab.Builder mCarrierCab = DVB_CarrierCab.newBuilder();
		mCarrierCab.setFreqencyKHz(frequency);
		mCarrierCab.setQam(DVB_CAR_QAM.QAM_64);
		mCarrierCab.setSymbolRateKbps(6875);
		mCarrier.setCab(mCarrierCab.build());
		mCarrier.setTunerId(0);
		mCarrier.setSignal(DVB_SIGNAL_TYPE.SIGNAL_C);
		Log.i("YDInfor", "start to tuner");
		int iTunerId = mTuner.connect(mCarrier.build());
		if (iTunerId >= 0) {
			Log.i(TAG, "Tuner to " + frequency + " success!");
		} else {

		}
	}

	/**
	 * 根据TSID 和 serviceID播放视频
	 * 
	 * @param serID
	 * @param tsID
	 * @return
	 */
	public int playChannel(int ri_FreqKHz, int ri_vidPid, int ri_audPid) {
		int result = -1;
		if (ri_FreqKHz > 0 && ri_vidPid != -1 && ri_audPid != -1) {
			Channel toPlayChannel = dvbDatabase.getChannelByavId(ri_FreqKHz,
					ri_vidPid, ri_audPid);
			if (toPlayChannel != null && null != sysApplication) {
				result = sysApplication
						.playChannel(toPlayChannel.chanId, false);
				P.d("GOT and play channel pointed by Intent params ts&serId");
				banner.show(toPlayChannel.chanId);
			}
		}
		return result;
	}

	/**
	 * 播放当前节目
	 * 
	 * @return
	 */
	public int playChannel() {
		int result = -1;
		if (null != sysApplication) {
			Channel toPlayChannel = sysApplication.getCurPlayingChannel();
			if (toPlayChannel != null) {
				result = sysApplication.playChannelCa(toPlayChannel.chanId,
						false);
				P.d("GOT and play channel pointed by Intent params ts&serId");
				banner.show(toPlayChannel.chanId);
			}
		}
		return result;
	}

	/**
	 * 停止播放节目
	 */
	public void stopPlay(boolean isBlank) {
		dvbPlayer.stop();
		if (isBlank)
			dvbPlayer.blank();
	}

	/**
	 * 恢复出厂设置
	 */
	private void factoryReset() {
		dvbDatabase = dvbManager.getChannelDBInstance();
		LivePlayer livePlayer = dvbManager.getDefaultLivePlayer();
		dvbDatabase.emptyChannel();
		dvbDatabase.emptyNetworkAttribute();
		dvbDatabase.emptyPlayingInfo();
		if (livePlayer != null) {
			livePlayer.stop();
			livePlayer.blank();
		}
	}

	@Override
	public void caCallback(DVB_CA data, Object reserved) {

		Log.i(TAG, "caCallback is running " + data.getType());

		switch (data.getType()) {
		case CA_NOVEL: {
			if (data.hasNovel()) {
				DVB_CA_NOVEL caData = data.getNovel();
				int cmdCode = caData.getCode().getNumber();
				handleCaNovelEvent(cmdCode, caData);
			}
			break;
		}
		default: {
			return;
		}
		}

	}

	/**
	 * CA显示处理
	 */
	Handler mMsgHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {

			case CaConfig.CA_COMMAND_SHOW_NOTICE:// 显示提示消息
				String notice = (String) msg.obj;
				if (null != notice && notice.length() > 0) {
					mCaNoticeTxt.setText(notice);
					showMsgBox(true);
				}
				break;

			case CaConfig.CA_COMMAND_FINGER_SHOW:// 显示指纹
				boolean isRepeat = false;
				mParams3.x = msg.arg1;
				mParams3.y = msg.arg2;
				if (mParams3.x == 0 && mParams3.y == 0) {
					setRandomCoodinate();
				}

				if (mParams3.x == screenWidth || mParams3.y == screenHeigh) {
					setRandomCoodinate(mParams3.x, mParams3.y);
					isRepeat = true;
				}

				String finger = (String) msg.obj;
				int txtWidth = mCaUtils.getDesiredWidth(mCaMsgTxt, finger);

				if (mParams3.x > (screenWidth - txtWidth - 46))
					mParams3.x = screenWidth - txtWidth - 46;
				if (mParams3.x < 26)
					mParams3.x = 26;
				if (mParams3.y > (screenHeigh - 60))
					mParams3.y = screenHeigh - 60;
				if (mParams3.y < 26)
					mParams3.y = 26;
				mCaMsgTxt.setText(finger);
				showMsgBox(false);

				if (isRepeat) {// 超级指纹重复显示处理
					Message newMsg = mMsgHandler.obtainMessage();
					newMsg.arg1 = msg.arg1;
					newMsg.arg2 = msg.arg2;
					newMsg.obj = finger;
					newMsg.what = CaConfig.CA_COMMAND_FINGER_SHOW;
					mMsgHandler.sendMessageDelayed(newMsg, 5000);
				}
				break;
			case CaConfig.CA_COMMAND_SUPEROSD_SHOW:// 超级OSD显示
				int position = msg.arg1;
				String osdTxt = (String) msg.obj;
				showOsd(position, osdTxt);
				break;

			case CaConfig.CA_COMMAND_SHOW_CURTAIN:// 高级预览
				byte type = (Byte) msg.obj;
				if (type > 0) {// 使能高级预览
					hideMsgSuperTxt();
					stopPlay(true);
				} else {
					if (!mCurtainNotice.equals(mCaMsgSupTxt.getText())) {
						mCaMsgSupTxt.setText(mCurtainNotice);
					}
					showMsgSuperTxt();
					playChannel();
				}

				break;
			case CaConfig.CA_COMMAND_FORCE_SHOW:// 强制显示（超级指纹和超级OSD）

				break;
			case CaConfig.CA_COMMAND_LOCK_SERVICE:// 强制换台
				int freq = msg.arg1;
				tunerTo(freq, 0);
				break;
			case CaConfig.CA_COMMAND_UNLOCK_SERVICE:// 解除强制换台
				// handleCaNovelEvent(10, null);
				break;
			case CaConfig.CA_COMMAND_SUPEMAIL_NEW:// 新邮件
				mCaMsgImg.setVisibility(View.VISIBLE);
				break;
			case CaConfig.CA_COMMAND_SUPEMAIL_HIDEICON:// 隐藏图标
				mCaMsgImg.setVisibility(View.GONE);
				break;
			case CaConfig.CA_COMMAND_SUPEMAIL_SPACEEXHAUST:// 邮箱已满
				if (View.GONE == mCaMsgImg.getVisibility()) {
					mCaMsgImg.setVisibility(View.VISIBLE);
				} else {
					mCaMsgImg.setVisibility(View.GONE);
				}
				mMsgHandler.sendEmptyMessageDelayed(
						CaConfig.CA_COMMAND_SUPEMAIL_SPACEEXHAUST, 2000);
				break;
			case CaConfig.CA_COMMAND_CONTINUSE_WATCH:// 连续观看
				// 隐藏信息盒子
				hideMsgBox();
				mCaUtils.setForcedShow(CaService.this, false);
				playChannel();
				break;
			case CaConfig.CA_COMMAND_CONTINUSE_WATCH_LIMIT:// 连续观看限制
				saveMsg = msg;
				// 清除连续观看限制信息
				if (mMsgHandler
						.hasMessages(CaConfig.CA_COMMAND_CONTINUSE_WATCH_LIMIT_LOOP)) {
					mMsgHandler
							.removeMessages(CaConfig.CA_COMMAND_CONTINUSE_WATCH_LIMIT_LOOP);
				}

				// if (!mContinuesWatch.equals(mCaNoticeTxt.getText())) {
				// mCaNoticeTxt.setText(mContinuesWatch);
				// }
				if (null == saveCWLMsg) {
					saveCWLMsg = mMsgHandler.obtainMessage();
					saveCWLMsg.obj = 0;
				}
				saveCWLMsg.arg1 = msg.arg1;
				saveCWLMsg.arg2 = msg.arg2;
				saveCaTask();
				sendEmptyMessage(CaConfig.CA_COMMAND_CONTINUSE_WATCH_LIMIT_LOOP);
				break;
			case CaConfig.CA_COMMAND_CONTINUSE_WATCH_LIMIT_LOOP:// 循环执行连续观看
				saveMsg = saveCWLMsg;
				if (null != saveCWLMsg) {
					int workTime = saveCWLMsg.arg1;
					int stopTime = saveCWLMsg.arg2;
					int status = (Integer) saveCWLMsg.obj;
					saveCWLMsg.obj = (0 == status) ? 1 : 0;
					if (0 == status) {

						if (!mContinuesWatch.equals(mCaNoticeTxt.getText())) {
							mCaNoticeTxt.setText(mContinuesWatch);
						}
						showMsgBox(true);
						mCaUtils.setForcedShow(CaService.this, true);
						stopPlay(true);
						mMsgHandler.sendEmptyMessageDelayed(
								CaConfig.CA_COMMAND_CONTINUSE_WATCH_LIMIT_LOOP,
								stopTime * 1000);

					} else {
						// 隐藏信息盒子
						hideMsgBox();
						mCaUtils.setForcedShow(CaService.this, false);
						playChannel();
						mMsgHandler.sendEmptyMessageDelayed(
								CaConfig.CA_COMMAND_CONTINUSE_WATCH_LIMIT_LOOP,
								workTime * 1000);
					}
				}
				break;

			case CaConfig.CA_COMMAND_BLACK_LIST:// 黑名单功能
				saveMsg = msg;
				if (!mBlackList.equals(mCaNoticeTxt.getText())) {
					mCaNoticeTxt.setText(mBlackList);
				}
				showMsgBox(true);
				break;
			case CaConfig.CA_COMMAND_HIDE_MSGBOX:// 关闭信息盒子

				hideMsgBox();
				mCaUtils.setForcedShow(CaService.this, false);
				break;
			case CaConfig.CA_COMMAND_HIDE_OSDBOX:// 关闭OSD盒子
				hideOsdBox();
				mCaUtils.setForcedShow(CaService.this, false);
				break;
			case CaConfig.CA_COMMAND_HIDE_PROGRESS:// 隐藏进度条
				mCaUtils.closeActivity(CACardUpgrade.class);
				break;
			case CaConfig.CA_COMMAND_REBOOT://重启机顶盒
				Log.i(TAG, "reboot  >>>>>>>");
				PowerManager pm = (PowerManager)getApplicationContext().getSystemService(Context.POWER_SERVICE);
				pm.reboot(null);
				
				break;	
			default:
				break;
			}
		}
	};

}
