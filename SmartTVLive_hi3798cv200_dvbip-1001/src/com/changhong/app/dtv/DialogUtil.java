package com.changhong.app.dtv;


import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
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

	public static Dialog showPromptDialog(Context context, String prompt,String prompt1,
			String positiveBtnName, String negtiveBtnName,
			final DialogBtnOnClickListener listener) {

		final Dialog dialog = new Dialog(context, R.style.Dialog_zhou_use);

		View view = LayoutInflater.from(context).inflate(
				R.layout.view_dialog, null);

		Button bt_submit = (Button) view.findViewById(R.id.reset_submit);
		Button bt_cancel = (Button) view.findViewById(R.id.reset_cancel);
		
		TextView zhibo_prompt_z = (TextView) view
				.findViewById(R.id.zhibo_prompt_z);
		
		TextView zhibo_prompt_z1 = (TextView) view
				.findViewById(R.id.zhibo_prompt_z1);
		
	
		

		zhibo_prompt_z.setText(prompt);
		
		if(prompt1!=null){
			zhibo_prompt_z1.setVisibility(View.VISIBLE);
			zhibo_prompt_z1.setText(prompt1);

		}

		dialog.setContentView(view);

		LayoutParams param = dialog.getWindow().getAttributes();
		param.gravity = Gravity.CENTER;

		param.width = 600;
		param.height = 450;

		bt_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogMessage dialogMessage = new DialogMessage(dialog);
				if (listener != null) {
					listener.onSubmit(dialogMessage);
				}
			}
		});
		bt_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogMessage dialogMessage = new DialogMessage(dialog);
				if (listener != null) {
					listener.onCancel(dialogMessage);
				}
			}
		});

		dialog.getWindow().setAttributes(param);

		try {

			dialog.show();
		} catch (Exception e) {
			// TODO: handle exception
		}
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
