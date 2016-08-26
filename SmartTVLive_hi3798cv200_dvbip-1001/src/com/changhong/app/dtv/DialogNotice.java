package com.changhong.app.dtv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.changhong.app.constant.Class_Constant;
import com.changhong.app.constant.Class_Global;

public class DialogNotice extends Activity{
	
	private int	   si_MyMenuID = Class_Constant.MENU_ID_DTV_DIALOG;	

	
	private TextView 	tv_title;
	private TextView 	tv_details;
	private Button 		btn_yes;
	private Button 		btn_no;
	private Button 		btn_single_yes;
	
	private String 		str_title;
	private String 		str_details;
	private int 		i_diabtnnum;
	private int 		i_DialogDefault;
	private int 		i_diatime;

	boolean	b_FirstIn = true;	
	boolean b_keydown = true;
	boolean b_FirseInLoop = true;
	
	Class_Global obj_Global = new Class_Global ();
	Handler handler_dia=new Handler();
	int		i_Count;
	private Message mDlgMsg 					= null;
	final 	int HANDLE_MSG_TYPE_ClOSE_DLG 	= 1;
	int				i_AimMenuID			= -1;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog);
        
        i_Count = 0;
        
        /**����*/
        tv_title=(TextView)findViewById(R.id.id_dtv_dialog_title);
        
        /**����*/
        tv_details=(TextView)findViewById(R.id.id_dtv_dialog_details);
        
        /**2����ť*/
        btn_yes=(Button)findViewById(R.id.id_dtv_dia_yes);
        btn_yes.setOnClickListener(btn_yes_clicklistener);
        btn_no=(Button)findViewById(R.id.id_dtv_dia_no);
        btn_no.setOnClickListener(btn_no_clicklistener);
        
        /**һ����ť*/
        btn_single_yes=(Button)findViewById(R.id.id_dtv_dia_single_yes);
        btn_single_yes.setOnClickListener(btn_singleyes_clicklistener);
        
        Intent dialog_intent=getIntent();
        
    	str_title=dialog_intent.getStringExtra(Class_Constant.DIALOG_TITLE);
    	str_details=dialog_intent.getStringExtra(Class_Constant.DIALOG_DETAILS);
    	i_diabtnnum=dialog_intent.getIntExtra(Class_Constant.DIALOG_BUTTON_NUM, -1);
    	i_DialogDefault=dialog_intent.getIntExtra(Class_Constant.DIALOG_DEFAULT_BUTTON, -1);
    	i_diatime=dialog_intent.getIntExtra(Class_Constant.DIALOG_TIME, -1);
    	
    	//If Delay time is -1, means wait forever.
    	/*if ( -1 == i_diatime )
    		i_diatime = 5;
    		*/
    	
    	i_diatime = i_diatime * 1000;
    	
    	/**�жϲ����Ƿ���ȷ*/
        if(str_title==null || str_details== null || i_diabtnnum == -1/* || i_diatime == -1*/)
        {
        	/**����dialog*/
        	Common.LOGE("str_title ="+str_title +"    str_details =" +str_details+
        				  "i_diabtnnum == " + i_diabtnnum);
        	finish();
        }
        if(i_diabtnnum == 2)
        {
        	Common.LOGI("����2��button��ѡ��");
        	tv_title.setText(str_title);
        	tv_details.setText(str_details);      	
        }
        else if(i_diabtnnum == 1)
        {
        	Common.LOGI("����1��button��ѡ��"); 
        	tv_title.setText(str_title);
        	tv_details.setText(str_details); 
        	
        	btn_yes.setVisibility(View.GONE);
        	btn_no.setVisibility(View.GONE);
        	btn_single_yes.setVisibility(View.VISIBLE);
        }
        else
        {
        	Common.LOGI("����û��button��ѡ��"); 
        	tv_title.setText(str_title);
        	tv_details.setText(str_details); 
        	
        	btn_yes.setVisibility(View.GONE);
        	btn_no.setVisibility(View.GONE);
        	btn_single_yes.setVisibility(View.GONE);    	
        }

        SendMsgToHandle ( HANDLE_MSG_TYPE_ClOSE_DLG, i_diatime );
	}
	
	protected void onStart() {
    	if(i_diabtnnum == 2)
    	{
    		if ( 0 == i_DialogDefault )
    		{
    			btn_yes.setFocusable(true);
    			btn_yes.setFocusableInTouchMode(true);
    			btn_yes.requestFocus();
    		}
    		else
    		{
	  			btn_no.setFocusable(true);
    			btn_no.setFocusableInTouchMode(true);
    			btn_no.requestFocus();		
    	}
    	}
    	else if(i_diabtnnum == 1)
    	{
    		btn_single_yes.setFocusable(true);
    		btn_single_yes.setFocusableInTouchMode(true);
    		btn_single_yes.requestFocus();
    	}

		super.onStart();
	}

	OnClickListener btn_yes_clicklistener=new OnClickListener() {
		
		public void onClick(View v) {
		Common.LOGI("���÷���Ϊ     RESULT_OK   ");
		setResult(RESULT_OK);
		RemoveMsg ( HANDLE_MSG_TYPE_ClOSE_DLG );
		finish();
		
		}
	};

	OnClickListener btn_no_clicklistener=new OnClickListener() {
		
		public void onClick(View v) {
			
			Common.LOGI("���÷���Ϊ     RESULT_CANCELED");		
			setResult(RESULT_CANCELED);
			RemoveMsg ( HANDLE_MSG_TYPE_ClOSE_DLG );
			finish();
		}
	};
	
	OnClickListener btn_singleyes_clicklistener = new OnClickListener() {
		
		public void onClick(View v) {
			Common.LOGI("ֻ��һ��button�����,ȷ������");
			/**ע������Ҫ���÷��ص�Ŀ��˵�,��������*/
			RemoveMsg ( HANDLE_MSG_TYPE_ClOSE_DLG );
			finish();
		}
	};

	public boolean onKeyDown(int keyCode, KeyEvent event) {

    	b_keydown = true;
    	Common.LOGI("��⵽����,���ԶԻ�������ʧ����");

    	RemoveMsg ( HANDLE_MSG_TYPE_ClOSE_DLG );
    	SendMsgToHandle ( HANDLE_MSG_TYPE_ClOSE_DLG, i_diatime );
    	
		if(btn_yes.isFocused() ==true &&keyCode == Class_Constant.KEYCODE_LEFT_ARROW_KEY)
		{
			btn_no.requestFocus();
			return true;
		}
		else if(btn_no.isFocused() ==true && keyCode == Class_Constant.KEYCODE_RIGHT_ARROW_KEY)
		{
			btn_yes.requestFocus();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	protected void onResume() {
		int i_AimMenuID = -1;
    	
    	if ( true == b_FirstIn )
    	{
    		b_FirstIn = false;
    		Class_Global.SetCurrMenuID(si_MyMenuID);
    	}
    	else
    	{
    		i_AimMenuID = Class_Global.GetAimMenuID();
    		Common.LOGI("��ǰĿ��˵�Ϊ  == " + i_AimMenuID);
    		
    		if ( -1 != i_AimMenuID && i_AimMenuID != si_MyMenuID )
    		{
    			Common.LOGI("��ǰĿ��˵�����dialog,�˳�");
    			finish ();
    		}
    		Class_Global.SetCurrMenuID(si_MyMenuID);
    	}
		super.onResume();
	}

	private Handler mChildHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLE_MSG_TYPE_ClOSE_DLG:
				if (btn_yes.isFocused() == true
						|| btn_single_yes.isFocused() == true) {
					Common.LOGI("���÷���Ϊ     RESULT_OK   ");
					setResult(RESULT_OK);
				} else {
					Common.LOGI("���÷���Ϊ     RESULT_CANCELED");
					setResult(RESULT_CANCELED);
				}
				finish();
				break;
			}
		}
	};
	
	
	

	protected void onDestroy() {
		
		RemoveMsg ( HANDLE_MSG_TYPE_ClOSE_DLG );

		super.onDestroy();
	}

	
	
	
	private void RemoveMsg(int ri_MsgType) {
		mChildHandler.removeMessages(ri_MsgType);
	}
	
	private void SendMsgToHandle ( int ri_MsgType, int ri_Delay )
    {
		if(ri_Delay <= 0)
		{
			return;
		}
		mDlgMsg 		= mChildHandler.obtainMessage ();
		mDlgMsg.what 	= ri_MsgType;
        mChildHandler.sendMessageDelayed ( mDlgMsg, ri_Delay );
    }
	
	
}
