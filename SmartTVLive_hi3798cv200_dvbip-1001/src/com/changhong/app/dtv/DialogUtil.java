package com.changhong.app.dtv;


import android.R.integer;
import android.app.Dialog;
import android.content.Context;
import android.net.StaticIpConfiguration;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DialogUtil {
	public static interface DialogBtnOnClickListener {

		public void onSubmit(DialogMessage dialogMessage);

		public void onCancel(DialogMessage dialogMessage);

	}

	public static class DialogMessage {
		public Dialog dialog;// 弹出的对话框对象
		public String msg;// 文本信息

		public DialogMessage() {
		}

		public DialogMessage(Dialog dialog) {
			this.dialog = dialog;
		}
	}
	
	private static Handler mHandler;
	private static int iSecond = 20;	
	private static Button bt_submit;	
	private static String btn_ok_str;
	private static DialogBtnOnClickListener rev_listener;
	private static Dialog dialog;

	public static Runnable CountRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			iSecond--;
			if (iSecond > 0) {
				bt_submit.setText(String.format(btn_ok_str, iSecond));	
				bt_submit.invalidate();				
				mHandler.postDelayed(CountRunnable, 1000);
			}
			if (iSecond == 0) {
				DialogMessage dialogMessage = new DialogMessage(dialog);
				if (rev_listener != null) {
					rev_listener.onSubmit(dialogMessage);
				}
				
			}
		}

	};	
	public static Dialog showPromptDialog(Context context, String prompt,String prompt1,
			String positiveBtnName, String negtiveBtnName,
			final DialogBtnOnClickListener listener) {
				
		dialog = new Dialog(context, R.style.Dialog_zhou_use);

		View view = LayoutInflater.from(context).inflate(
				R.layout.view_dialog, null);

		bt_submit = (Button) view.findViewById(R.id.reset_submit);
		Button bt_cancel = (Button) view.findViewById(R.id.reset_cancel);
	
		TextView zhibo_prompt_z = (TextView) view
				.findViewById(R.id.zhibo_prompt_z);
		
		TextView zhibo_prompt_z1 = (TextView) view
				.findViewById(R.id.zhibo_prompt_z1);
		
		prompt.replace("\\n", "\n");
		int totalStrNumber=0;
		totalStrNumber = Utils.stringNumbers(totalStrNumber,prompt,"\n");
		if(totalStrNumber>=2){
			//zhibo_prompt_z.setTop(60);
			RelativeLayout.LayoutParams layoutParam = new RelativeLayout.LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); 
			layoutParam.setMargins(0, 60, 0, 0);			
			zhibo_prompt_z.setLayoutParams(layoutParam);
		}
		//zhibo_prompt_z.setText(prompt);	
		zhibo_prompt_z.setText(prompt);
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message message) {
				super.handleMessage(message);
				/*switch (message.what) {
				case 0x01:

					break;

				default:
					break;
				}*/
			}
		};		
		iSecond = 20;	
		rev_listener=listener;
		btn_ok_str = context.getResources().getString(R.string.btn_ok_timeout);
		bt_submit.setText(String.format(btn_ok_str, iSecond));
		
		if(prompt1!=null){
			zhibo_prompt_z1.setVisibility(View.VISIBLE);
			//zhibo_prompt_z1.setText(prompt1);
			zhibo_prompt_z1.setText(prompt1.replace("\\n", "\n"));
		}

		dialog.setContentView(view);

		LayoutParams param = dialog.getWindow().getAttributes();
		param.gravity = Gravity.CENTER;
		//param.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; // 不能抢占聚焦点  

	    /* if(mWindowManager.getDefaultDisplay().getWidth()==1920){
		     param.width=800; //600;  520 -> 800
		     param.height=400;//450;  256 -> 400
	     }else{
		     param.width=520; //600;  520 -> 800
		     param.height=256;//450;  256 -> 400	    	 
	     }*/
	     param.width=520; //600;  
	     param.height=256;//450;  
	     	     
		bt_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(mHandler!=null&&CountRunnable!=null)
					mHandler.removeCallbacks(CountRunnable);
				DialogMessage dialogMessage = new DialogMessage(dialog);
				if (listener != null) {
					listener.onSubmit(dialogMessage);
				}
			}
		});
		bt_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(mHandler!=null&&CountRunnable!=null)
					mHandler.removeCallbacks(CountRunnable);				
				DialogMessage dialogMessage = new DialogMessage(dialog);
				if (listener != null) {
					listener.onCancel(dialogMessage);
				}
			}
		});

		bt_submit.setFocusable(true); 
		bt_submit.setFocusableInTouchMode(true); 
		bt_submit.requestFocus();
		
		dialog.getWindow().setAttributes(param);

		try {

			dialog.show();
		} catch (Exception e) {
			// TODO: handle exception
		}
		mHandler.postDelayed(CountRunnable, 1000);		
		return dialog;   

	}
	
	
	public static Dialog showInformationDialog(Context context, String prompt,
			String positiveBtnName, String negtiveBtnName,
			final DialogBtnOnClickListener listener) {

		final Dialog dialog = new Dialog(context, R.style.Dialog_zhou_use);

		View view = LayoutInflater.from(context).inflate(
				R.layout.view_dialog, null);
		
		TextView zhibo_prompt_z = (TextView) view
				.findViewById(R.id.zhibo_prompt_z);

		zhibo_prompt_z.setText(prompt);

		dialog.setContentView(view);

		LayoutParams param = dialog.getWindow().getAttributes();
		param.gravity = Gravity.CENTER;

		param.width = 400;
		param.height = 300;

		dialog.getWindow().setAttributes(param);

		try {

			dialog.show();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return dialog;

	}

}
