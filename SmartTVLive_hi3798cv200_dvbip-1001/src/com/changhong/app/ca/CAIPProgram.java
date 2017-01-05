package com.changhong.app.ca;

import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.changhong.app.dtv.R;
import com.changhong.dvb.CA_IppvProgramInfo;
import com.changhong.dvb.ProtoMessage.DVB_CA_TYPE;

public class CAIPProgram extends CABaseFragment {

	Button sureBtn;
	EditText pwd;
	View rootView=null;

	CA_IppvProgramInfo mIppvProgramInfo=new CA_IppvProgramInfo();

	private CAIPProgram() {

	}

	public CAIPProgram(String ippvProgramInfo) {
		String[] infors=ippvProgramInfo.split("&&");
		if(null != infors && infors.length >= 5){
			mIppvProgramInfo.mCaIppvProgramInfoNovel.mECMPId=converStringToInt(infors[0]);
			mIppvProgramInfo.mCaIppvProgramInfoNovel.mBuyTape=converStringToInt(infors[1]);
			mIppvProgramInfo.mCaIppvProgramInfoNovel.mPrice=converStringToInt(infors[2]);
			mIppvProgramInfo.mCaIppvProgramInfoNovel.mPriceTaping=converStringToInt(infors[3]);
			mIppvProgramInfo.mCaIppvProgramInfoNovel.mIsBuy=infors[4].equals("true");
		}
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.ca_book_ipp, container, false);
		initViewAndEvent(rootView);
		return rootView;
	}

	private void initViewAndEvent(View v) {
	
		pwd=(EditText)v.findViewById(R.id.pin_input_check); //确认密码
		sureBtn=(Button)v.findViewById(R.id.button_pin_sure);  //提交按钮
		sureBtn.setOnClickListener(this);
		pwd.requestFocus();
		nextFragment=null;
	}
	
	@Override
	public void onClick(View arg0) {
			
		String pinPwd = pwd.getText().toString();  //获取输入密码
		
		if(null != pinPwd && pinPwd.length() == CaConfig.CA_NOVEL_PIN_NUM){
			int result=thisCa.stopIPPVBuyDlg(pinPwd, mIppvProgramInfo);
			if (result == 0) {
				Toast.makeText(getActivity(), R.string.IPPT_BUY_SUCCESS, Toast.LENGTH_SHORT).show();
			}else {
				Toast.makeText(getActivity(), R.string.IPPT_BUY_FAILED, Toast.LENGTH_SHORT).show();
			}
			
			
		}else {
			Toast.makeText(getActivity(), R.string.ca_pin_invalid, Toast.LENGTH_SHORT).show();
		}
        getActivity().finish();		
	
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent arg1) {

		if (super.onKeyDown(keyCode, arg1)) {
			return true;
		}
		View focusView = rootView.findFocus();
		if (focusView instanceof EditText) {
			// 左键处理回退
			if (KeyEvent.KEYCODE_UNKNOWN == keyCode) {
				int index = ((EditText) focusView).getSelectionStart();
				if (index > 0) {
					Editable editable = ((EditText) focusView).getText();
					editable.delete(index - 1, index);
					return true;
				}
			} 
		}
		
		 if (KeyEvent.KEYCODE_BACK== keyCode) {
			getActivity().finish();
			return true;
		}
		return false;
	}
	


    private int converStringToInt(String str){
    	int intValue=0;
    	try {
    		intValue=Integer.parseInt(str);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return intValue;
    }
	

}
