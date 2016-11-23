package com.changhong.app.timeshift.datafactory;

import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.changhong.app.dtv.SysApplication;
import com.changhong.app.timeshift.common.CacheData;
import com.changhong.app.timeshift.common.Class_Constant;
import com.changhong.app.timeshift.common.L;
import com.changhong.app.timeshift.common.ProcessData;
import com.changhong.app.timeshift.common.ProgramInfo;
import com.changhong.app.timeshift.common.Utils;
import com.changhong.app.timeshift.common.VolleyTool;
import com.changhong.dvb.Channel;
import com.changhong.dvb.ChannelDB;
import com.changhong.dvb.DVB;
import com.changhong.dvb.PlayingInfo;

public class Player implements MediaPlayer.OnBufferingUpdateListener,
		MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener,
		SurfaceHolder.Callback {
	private int videoWidth;
	private int videoHeight;
	public static MediaPlayer mediaPlayer;
	private SurfaceHolder surfaceHolder;
	public static SeekBar skbProgress;
	private SurfaceView surfaceView;
	private Timer mTimer;
	private static boolean playingFlag = false;
	private static Handler parentHandler;
	public static boolean handlerFlag = true;
	public static boolean keyFlag = false;

	private static TextView videoCurrentTime;

	private static int desPositon = 0;
	private static int duration = 0;
	private float moveStep = 0;

	/*
	 * 直播时移的参数
	 */
	private static boolean liveFlag = true;
	private static int curBeginTime = 0;
	private static ProcessData processData = null;
	private static RequestQueue mReQueue;
	private static Channel curChannel;
	private static ProgramInfo curProgram;
	private static int curProlength = 0;

	private static int delayTime = 0;// 秒
	private static boolean firstPlayInShift = true;// 直播中第一次进入时移

	/*
	 * 计算seekbar上的 textview位置
	 */
	private int curTextWidth;
	private int curTextHeight;
	private int seekwidth;

	// int maxTimes;

	public Player(Handler parentHandler, SurfaceView mySurfaceView,
			SeekBar skbProgress, TextView txvCurrent) {
		Player.skbProgress = skbProgress;
		this.surfaceView = mySurfaceView;
		Player.videoCurrentTime = txvCurrent;
		Player.parentHandler = parentHandler;

		if (videoCurrentTime != null) {
			curTextWidth = videoCurrentTime.getWidth();
			curTextHeight = videoCurrentTime.getHeight();
		}

		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		// 防止音频出不来
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		if (null == mTimer) {
			mTimer = new Timer();
		}
		try {
			mTimer.schedule(mTimerTask, 0, 1000);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		processData = new ProcessData();
		mReQueue = VolleyTool.getInstance().getRequestQueue();
		
		//获取当前频道信息
		ChannelDB db=DVB.getManager().getChannelDBInstance();
		PlayingInfo thisPlayingInfo = DVB.getManager().getChannelDBInstance().getSavedPlayingInfo();
		//获取当前Channel详细信息
		curChannel = db.getChannel(thisPlayingInfo.mChannelId );

		delayTime = 0;
		initMediaPlayer();
		this.skbProgress.setOnSeekBarChangeListener(new mySeekChangeLis());

	}

	private class mySeekChangeLis implements SeekBar.OnSeekBarChangeListener {

		@Override
		public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {

			if (null == videoCurrentTime) {
				return;
			}
			// int maxTimes = (int)
			// (CacheData.getCurProgram().getEndTime().getTime() -
			// CacheData.getCurProgram().getBeginTime().getTime());
			int maxTimes = getDuration();
			moveStep = (float) ((float) arg1 / (float) maxTimes);
			seekwidth = skbProgress.getWidth();//seekbar隐藏后宽度会变，所以需要每次都获取
			if (videoCurrentTime != null) {
				long beginTime = CacheData.getCurProgram().getBeginTime()
						.getTime();
				Log.i("mmmm", "player-maxTimes:" + maxTimes + "--seekwidth:" + seekwidth+ "--moveStep:" + moveStep + "--arg1:" + arg1);
				videoCurrentTime.setText(Utils.millToLiveBackString(beginTime+ arg1));
				Log.i("mmmm","playervideoCurrentTime:"+Utils.millToLiveBackString(beginTime+ arg1));
				Log.i("mmmm","player-layout:"+"--L:"+(seekwidth * moveStep)+"--R:"+((seekwidth * moveStep) + videoCurrentTime.getWidth())+"--B:"+videoCurrentTime.getHeight());
				videoCurrentTime.layout((int) (seekwidth * moveStep), 0,
						(int) (seekwidth * moveStep) + videoCurrentTime.getWidth(),
						videoCurrentTime.getHeight());
			}
			//进度条移到末尾
			if (liveFlag && arg1 == arg0.getMax()) {
				if (handlerFlag) {
					handlerFlag = false;
					Log.i("test", "parentHandler.sendEmptyMessage:SHIFT_NEXT_PROGRAM");
					 parentHandler
					 .sendEmptyMessage(Class_Constant.SHIFT_NEXT_PROGRAM);
				}
			}
			
		}

		@Override
		public void onStartTrackingTouch(SeekBar arg0) {

		}

		@Override
		public void onStopTrackingTouch(SeekBar arg0) {

		}

	}

	/*******************************************************
	 * 通过handler更新seekbar
	 ******************************************************/
	TimerTask mTimerTask = new TimerTask() {
		@Override
		public void run() {
			if (mediaPlayer == null)
				return;
			try {
				if (liveFlag) {
					if (mediaPlayer != null && mediaPlayer.isPlaying()
							&& Player.videoCurrentTime != null && !keyFlag) {
						handleProgress
								.sendEmptyMessage(Class_Constant.RE_UPDATE_PROGRESS);
					}
				} else {
					if (mediaPlayer != null && mediaPlayer.isPlaying()
							&& playingFlag && videoCurrentTime != null
							&& !keyFlag) {
						handleProgress
								.sendEmptyMessage(Class_Constant.RE_UPDATE_PROGRESS);
					}
				}
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}
	};

	public static Handler handleProgress = new Handler() {
		public void handleMessage(Message msg) {
			int skPos = 0;
			int position = 0;
			switch (msg.what) {
			case Class_Constant.REPLAY_SEEK_TO:
				skPos = msg.arg1;

				if (duration > 0) {
					Player.skbProgress.setProgress(skPos);
				}
				break;
			case Class_Constant.RE_FAST_FORWARD_DOWN:
				if (!playingFlag) {
					return;
				}
				mediaPlayer.pause();
				keyFlag = true;
				desPositon = Player.skbProgress.getProgress() + 30000;

				if (desPositon >= duration) {
					if (handlerFlag) {
						handlerFlag = false;
						// parentHandler
						// .sendEmptyMessage(Class_Constant.RE_NEXT_PROGRAM);

					}
					desPositon = duration;
				}
				Player.skbProgress.setProgress(desPositon);
				break;
			case Class_Constant.RE_FAST_REVERSE_DOWN:

				if (!playingFlag) {
					return;
				}
				if (mediaPlayer.isPlaying()) {
					mediaPlayer.pause();
				}
				keyFlag = true;
				desPositon = Player.skbProgress.getProgress() - 30000;

				/*if (desPositon < 0) {
					if (handlerFlag) {
						handlerFlag = false;
						parentHandler
								.sendEmptyMessage(Class_Constant.RE_LAST_PROGRAM);// 回退到最开始
						Log.i("xb", "Player*********");
					}
					desPositon = 0;
				}*/

				Player.skbProgress.setProgress(desPositon);
				//
				break;
			case Class_Constant.RE_FAST_FORWARD_UP:
			case Class_Constant.RE_FAST_REVERSE_UP:
				parentHandler.removeCallbacks(fastOperationRunnable);
				parentHandler.postDelayed(fastOperationRunnable, 1500);
				break;
			case Class_Constant.RE_PLAY:

				break;
			case Class_Constant.RE_PAUSE:

				break;

			case Class_Constant.RE_UPDATE_PROGRESS:
				if (null == videoCurrentTime) {
					return;
				}
				if (liveFlag) {
					int curmedPos = mediaPlayer.getCurrentPosition();
					desPositon = curmedPos + curBeginTime - delayTime * 1000;
					if (desPositon >= curProlength) {
						// 通知更新banner条
						// parentHandler.sendEmptyMessage(Class_Constant.LIVE_BACK_PROGRAM_OVER);
						// liveFlag=false;
						// parentHandler.sendEmptyMessage(Class_Constant.BACK_TO_LIVE);
					} else {
//						long beginTime = CacheData.getCurProgram()
//								.getBeginTime().getTime();
//						videoCurrentTime.setText(Utils
//								.millToLiveBackString(desPositon + beginTime));
					}
				} else {
					desPositon = mediaPlayer.getCurrentPosition();
					videoCurrentTime.setText(Utils.millToDateStr(desPositon));

				}
				if (duration > 0&&desPositon<(duration+2000)) {
					Player.skbProgress.setProgress(desPositon);
				}
				break;

			case Class_Constant.LIVE_FAST_FORWARD:
				if (mediaPlayer.isPlaying()) {
					mediaPlayer.pause();
				}
				keyFlag = true;
				Log.i("mmmm", "Player-handleProgress" + handleProgress);
				desPositon = Player.skbProgress.getProgress() + 30000;
				if (IsOutOfTimes(desPositon)) {
					if (handlerFlag) {
						parentHandler
								.sendEmptyMessage(Class_Constant.BACK_TO_LIVE);
					}
					keyFlag = false;
					desPositon = -1;
					break;

				}
				Player.skbProgress.setProgress(desPositon);
				break;
			case Class_Constant.LIVE_FAST_REVERSE:

				if (mediaPlayer.isPlaying()) {
					mediaPlayer.pause();
				}
				keyFlag = true;
				Log.i("mmmm", "**Player.skbProgress.getProgress():" + Player.skbProgress.getProgress());
				desPositon = Player.skbProgress.getProgress() - 30000;
				Log.i("mmmm", "**desPositon:" + desPositon);
				if (desPositon < 0) {
					Log.i("mmmm", "parentHandler.sendEmptyMessage:SHIFT_LAST_PROGRAM");
					// 提示已经到开始位置了
					if (handlerFlag) {
						handlerFlag = false;
						parentHandler
						.sendEmptyMessage(Class_Constant.SHIFT_LAST_PROGRAM);// 回退到最开始
					}
					desPositon = 0;
				}

				Player.skbProgress.setProgress(desPositon);

				break;
			}

		};
	};

	// *****************************************************

	public void play() {
		mediaPlayer.start();
	}

	public static void playUrl(String videoUrl) {
		handlerFlag = true;
		if (null == mediaPlayer || null == videoUrl)
			return;

		try {
			// mediaPlayer.stop();
			mediaPlayer.reset();
			mediaPlayer.setDataSource(videoUrl);
			mediaPlayer.prepare();// prepare֮���Զ�����

			if (liveFlag) {
				curBeginTime = getStartTime();
			} else {
				duration = mediaPlayer.getDuration();
				Player.skbProgress.setMax(duration);
			}
			// mediaPlayer.start();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void pause() {
		if (null != mediaPlayer) {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.pause();
			} else {
				mediaPlayer.start();
			}
		}
	}

	public boolean isPlayerPlaying() {
		boolean flag = false;
		if (null != mediaPlayer) {
			flag = mediaPlayer.isPlaying();
		}
		return flag;
	}

	public  void stop() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			// mediaPlayer.reset();
			mediaPlayer.release();  
			// mediaPlayer.setFreezeMode(1);
			// try {
			// CommonMethod.excuteCmd(CommonMethod.cmdBlack);
			// } catch (Exception e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			mediaPlayer = null;
			if (parentHandler != null) {
				parentHandler.removeCallbacks(fastOperationRunnable);
			}
		}
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
	}
	
	public void stopTimer(){
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		Log.e("mediaPlayer", "surface changed");
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		initMediaPlayer();
	}

	private void initMediaPlayer() {
		try {
			if(null==mediaPlayer){
				mediaPlayer = new MediaPlayer();
			}
			mediaPlayer.setDisplay(surfaceHolder);
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			// mediaPlayer.setFreezeMode(0);

			mediaPlayer.setOnBufferingUpdateListener(this);
			mediaPlayer.setOnPreparedListener(this);
			mediaPlayer.setOnCompletionListener(this);
		} catch (Exception e) {
			Log.e("mmmm", "error" + e);
		}
		Log.e("mmmm", "mediaPlayer-surface created");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		Log.e("mediaPlayer", "surface destroyed");
	}

	@Override
	/**
	 * ͨ��onPrepared����
	 */
	public void onPrepared(MediaPlayer arg0) {
		videoWidth = mediaPlayer.getVideoWidth();
		videoHeight = mediaPlayer.getVideoHeight();
		if (videoHeight != 0 && videoWidth != 0) {
			arg0.start();
		} else {
			Toast.makeText(SysApplication.getInstance(), "视频无效", Toast.LENGTH_SHORT)
					.show();
			Log.i("mm", "videoWidth or videoHeight =0");
		}
		if (liveFlag) {
			handleProgress.sendEmptyMessage(Class_Constant.RE_UPDATE_PROGRESS);
			if (firstPlayInShift) {
				mediaPlayer.pause();
			}
			firstPlayInShift = false;
		}
		curProgram = CacheData.getCurProgram();
		if (curProgram != null) {
			curProlength = (int) (curProgram.getEndTime().getTime() - curProgram
					.getBeginTime().getTime());
		}
		Log.e("mmmm", "onPrepared");
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		// TODO Auto-generated method stub
		parentHandler.sendEmptyMessage(Class_Constant.RE_NEXT_PROGRAM);//确认是否注释掉
	}

	// 播放视频准备好播放后调用此方法
	public void onBufferingUpdate(MediaPlayer arg0, int bufferingProgress) {
		Player.skbProgress.setSecondaryProgress(bufferingProgress);
		playingFlag = true;
//		int currentProgress = Player.skbProgress.getMax()
//				* mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration();
		if (bufferingProgress != 0) {
		}
		// Log.i("mmmm", mediaPlayer.isPlaying() + "|isplaying");
	}

	// public boolean onInfo(MediaPlayer arg0, int arg1, int arg2) {
	// switch (arg1) {
	// case MediaPlayer.MEDIA_INFO_BUFFERING_START:
	// //开始缓存，暂停播放
	// if (isPlaying()) {
	// stopPlayer();
	// needResume = true;
	// }
	// mLoadingView.setVisibility(View.VISIBLE);
	// break;
	// case MediaPlayer.MEDIA_INFO_BUFFERING_END:
	// //缓存完成，继续播放
	// if (needResume)
	// startPlayer();
	// mLoadingView.setVisibility(View.GONE);
	// break;
	// case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
	// //显示 下载速度
	// Logger.e("download rate:" + arg2);
	// break;
	// }
	// return true;
	// }
	// }
	// }

	/* 播放过程中时间进行更新显示 */
	// public void refreshVideoTime(TextView txvLen, TextView txvCur) {
	// txvLen.setText("");
	// txvCur.setText("");
	// }

	private static void playLiveBack(Channel channel, int delay) {
		mReQueue.cancelAll("bannerDialog");
		String requestURL = processData.getLiveBackPlayUrl(channel, delay);
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
				Request.Method.POST, requestURL, null,
				new Response.Listener<org.json.JSONObject>() {

					@Override
					public void onResponse(org.json.JSONObject arg0) {
						// TODO Auto-generated method stub
						 L.i("player=dvbBack:" + arg0);
						final String url = JsonResolve.getInstance()
								.getLivePlayURL(arg0);
						new Thread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								playUrl(url);
							}
						}).start();

					}
				}, errorListener);
		jsonObjectRequest.setTag("bannerDialog");// 设置tag,cancelAll的时候使用
		mReQueue.add(jsonObjectRequest);
	}

	private static Response.ErrorListener errorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError arg0) {
			// TODO Auto-generated method stub
			Log.i("mmmm", "MainActivity=error：" + arg0);
		}
	};

	private static int getPlayDelayTimes() {
		long times = 0;
		Date date = new Date();
		ProgramInfo program=CacheData.getCurProgram();
		times = (date.getTime() - Player.skbProgress.getProgress() - program.getBeginTime().getTime()) / 1000;

		return (int) times;
	}

	private static boolean IsOutOfTimes(int progress) {
		boolean flag = true;
		long times = 0;
		Date date = new Date();
		times = (date.getTime() - progress - CacheData.getCurProgram()
				.getBeginTime().getTime()) / 1000;
		if (times > 3) {
			flag = false;
		} else {
			flag = true;
		}

		return flag;
	}

	private static int getLiveMaxTime() {
		long times = 0;
		Date date = new Date();
		times = date.getTime()
				- CacheData.getCurProgram().getBeginTime().getTime();
		return (int) times;
	}

	public boolean isLiveFlag() {
		return liveFlag;
	}

	public void setLiveFlag(boolean liveFlag) {
		Player.liveFlag = liveFlag;
	}

	public static void setFirstPlayInShift(boolean firstPlayInShift) {
		Player.firstPlayInShift = firstPlayInShift;
	}

	public static int getDuration() {
		return duration;
	}

	public static void setDuration(int duration) {
		Player.duration = duration;
	}
	public static void setDelayTime(int delayTime) {
		Player.delayTime = delayTime;
	}

	private static int getStartTime() {
		long time = 0;
		int beginTime = 0;
		beginTime = (int) CacheData.getCurProgram().getBeginTime().getTime();
		Date date = new Date();
		time = date.getTime() - beginTime;
		return (int) time;
	}

	// fastforward and fastbackward post delay
	static Runnable fastOperationRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			// Player.skbProgress.setProgress(desPositon);
			if (null == mediaPlayer)
				return;
			if (!liveFlag) {
				Log.i("mmmm", "%%%%"+liveFlag);
				if (desPositon < 0) {
					mediaPlayer.pause();
					if (handlerFlag) {
						handlerFlag = false;
						parentHandler
								.sendEmptyMessage(Class_Constant.RE_LAST_PROGRAM);// 回退到最开始
						Log.i("xb", "Player*********");
					}
					desPositon = 0;
				}
				mediaPlayer.seekTo(desPositon);
				mediaPlayer.start();
			} else {
				
				Log.i("mmmm", "shiyi desPositon:"+desPositon);
				Log.i("mmmm", "shiyi skbProgress.getMax():"+skbProgress.getMax());
				if (desPositon > 0 && desPositon < skbProgress.getMax()) {
					delayTime = getPlayDelayTimes();
					playLiveBack(curChannel, delayTime);
				}
				if (desPositon == 0 || desPositon >= skbProgress.getMax()) {
					mediaPlayer.pause();
				}
			}

			keyFlag = false;
		}
	};
	
	public static boolean isPlayingFlag() {
		return playingFlag;
	}
	public static void setPlayingFlag(boolean playingFlag) {
		Player.playingFlag = playingFlag;
	}
	public void initSeekbar(){
		skbProgress.setProgress(0);
		desPositon=0;
	}

}
