package com.changhong.app.ca;

import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.changhong.app.dtv.R;
import com.changhong.app.dtv.SysApplication;
import com.changhong.dvb.CA_IppvProgramInfo;
import com.changhong.dvb.tofu_DateTime;
import com.changhong.dvb.ProtoCaNovel.DVB_CA_NOVEL_Data;
import com.changhong.dvb.ProtoCaNovel.DVB_CA_NOVEL_IPPV_Data;
import com.changhong.dvb.ProtoCaNovel.DVB_CA_NOVEL_IPPV_Price;
import com.changhong.dvb.ProtoCaNovel.DVB_CA_NOVEL_IPPV_Time;
import com.changhong.dvb.ProtoCaNovel.DVB_CA_NOVEL_MSG_CODE;

public class CABuyIPPDialog extends CABase {

	TextView operatorID, tokenID, progarmID, canTaping, price,
			duringTime;
	LinearLayout ippvDTime, ippBookDialog, ippBookResult;
	TextView ipptNotice, titleView;
	Button  BtnBookResult;
	private Handler mUIHandler = null;
	private final int IPP_BUY_DIALOG_REMOVE = 0;
	private final int IPP_BUY_DIALOG_STOP = 1;
	private CA_IppvProgramInfo ippvPro;
	int mCurType=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ca_buy_ipp_dialog);
		initViewAndData();
	}

	protected void initViewAndData() {
		super.initViewAndData();
		// 初始化视图
		ippBookDialog = (LinearLayout) findViewById(R.id.ipp_buy_dialog);
		ippBookResult = (LinearLayout) findViewById(R.id.ipp_buy_result);
		titleView = (TextView) findViewById(R.id.ipp_buy_title);
		operatorID = (TextView) findViewById(R.id.operator_id); // 营运商ID
		tokenID = (TextView) findViewById(R.id.wallet_id); // 钱包ID
		progarmID = (TextView) findViewById(R.id.progarm_id); // 节目ID
		canTaping = (TextView) findViewById(R.id.buy_price_type); // 可否录像
		price = (TextView) findViewById(R.id.buy_price); // 订购价格
//		priceTaping = (TextView) findViewById(R.id.buy_price_taping); // 可录像订购价格
		duringTime = (TextView) findViewById(R.id.progarm_during_time); // 节目持续时间

		ippvDTime = (LinearLayout) findViewById(R.id.ippv_duringtime); // IPPV视频持续时间
		ipptNotice = (TextView) findViewById(R.id.ippt_notice); // IPPV扣费提示信息
		BtnBookResult = (Button) findViewById(R.id.btn_buy_result); // 结果确认按钮

		// 初始化值
		BtnBookResult.setOnClickListener(this);
		Intent intent = getIntent();
		int msgType = intent.getIntExtra("msgType", -1);
		DVB_CA_NOVEL_IPPV_Data data = (DVB_CA_NOVEL_IPPV_Data) intent
				.getExtra("ippData");
		mCurType=0;
		initIppBuyData(msgType, data);
		setDialogTitle(msgType);

	}

	private void initIppBuyData(int msgType, DVB_CA_NOVEL_IPPV_Data ippData) {
		if (null == ippData) {
			return;
		}
		ippvPro = new CA_IppvProgramInfo();
		ippvPro.mCaIppvProgramInfoNovel.mECMPId = ippData.getEcmpId(0);
		ippvPro.mCaIppvProgramInfoNovel.mOperatorId = ippData.getOperatorId();
		ippvPro.mCaIppvProgramInfoNovel.mProductId = ippData.getProductId();
		ippvPro.mCaIppvProgramInfoNovel.mSlotId = ippData.getTokenId();
		ippvPro.mCaIppvProgramInfoNovel.mCanTape=1;

		int priceNum = ippData.getPriceCount();
		for (int i = 0; i < priceNum; i++) {
			DVB_CA_NOVEL_IPPV_Price temp = ippData.getPrice(i);
			if (1 == temp.getPriceType(0)) {
				ippvPro.mCaIppvProgramInfoNovel.mPriceTaping = temp.getPriceVal(0);
				ippvPro.mCaIppvProgramInfoNovel.mCanTape=1;				
			} else {
				ippvPro.mCaIppvProgramInfoNovel.mPrice = temp.getPriceVal(0);
			}
		}
		tofu_DateTime dateTime = new tofu_DateTime();
		if (ippData.getIpptTimeCount() > 0) {
			DVB_CA_NOVEL_IPPV_Time time = ippData.getIpptTime(0);
			dateTime.miYear = time.getYear(0);
			dateTime.miMonth = time.getMonth(0);
			dateTime.miDay = time.getDay(0);
			ippvPro.mCaIppvProgramInfoNovel.mDeductInterval = time.getDeductInterval(0);
		} else {
			return;
		}

		if (CaConfig.CA_NOVEL_IPPT_PAYVIEWED_VALUE == msgType) {
			String tNotice = getResources().getString(R.string.IPPT_BUY_INFOR);
			tNotice = tNotice
					.replace(
							"*",
							ippvPro.mCaIppvProgramInfoNovel.mDeductInterval
									+ "")
					.replace("@", ippvPro.mCaIppvProgramInfoNovel.mPrice + "")// 扣费点数
					.replace("%", ippvPro.mCaIppvProgramInfoNovel.mSlotId + "");// 扣费钱包
			ipptNotice.setText(tNotice);
		} else {

		}
		ippvPro.mCaIppvProgramInfoNovel.mExpiredTime = dateTime;
		operatorID.setText(ippvPro.mCaIppvProgramInfoNovel.mOperatorId + "");
		tokenID.setText(ippvPro.mCaIppvProgramInfoNovel.mSlotId + "");
		progarmID.setText(ippvPro.mCaIppvProgramInfoNovel.mProductId + "");
		
		duringTime.setText(ippvPro.mCaIppvProgramInfoNovel.mExpiredTime.ToDateTimeByString());
	    int canTape=ippvPro.mCaIppvProgramInfoNovel.mCanTape ;
		if(0== canTape ||1== canTape){
			String[] selector=getResources().getStringArray(R.array.ca_cantaping);
			canTaping.setText(selector[canTape]);
		}
		switchPrice();
	}
	
	
	private void switchPrice(){		
		mCurType=++mCurType%2;
		int curPrice=(1 ==mCurType)?ippvPro.mCaIppvProgramInfoNovel.mPriceTaping:ippvPro.mCaIppvProgramInfoNovel.mPrice;			
		int curType=(1 ==mCurType)?R.string.IPPT_BUY_PRICE_TAPE:R.string.IPPT_BUY_PRICE;	
		price.setText("<  " + curPrice + "  >");	
		canTaping.setText(curType);
	}

	private void setDialogTitle(int msgType) {
		String title = "";
		if (CaConfig.CA_NOVEL_IPPV_FREEVIEWED_VALUE == msgType) {
			title = getResources().getString(R.string.CA_BUY_IPPV_TITLE_FREE);
		} else if (CaConfig.CA_NOVEL_IPPV_PAYVIEWED_VALUE == msgType) {
			title = getResources().getString(
					R.string.CA_BUY_IPPV_TITLE_PAYVIEWED);
		} else {
			ippvDTime.setVisibility(View.GONE);
			ipptNotice.setVisibility(View.VISIBLE);
			title = getResources().getString(
					R.string.CA_BUY_IPPT_TITLE_PAYVIEWED);
		}
		titleView.setText(title);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		
		case R.id.btn_buy_result:
			finish();
			break;
		
		default:
			break;
		}
	}

	/**
	 * 确认购买
	 */
	protected void onSubmit(String pinCode) {
		int result = thisCa.stopIPPVBuyDlg(pinCode, ippvPro);
		Message msg = uiHandler.obtainMessage();
		msg.what = IPP_BUY_DIALOG_STOP;
		msg.obj = getIPPvBuyResult(result);
		uiHandler.sendMessage(msg);
	}

	private String getIPPvBuyResult(int resultCode) {
		String resultContent = null;
		int stringId = R.string.IPPT_BUY_FAILED;
		switch (resultCode) {
		case CaConfig.IPPV_BUY_OK:// IPP购买成功
			stringId = R.string.IPPT_BUY_SUCCESS;
			break;
		case CaConfig.IPPV_BUY_CARD_INVALID:// 卡无效
			stringId = R.string.MESSAGE_BADCARD_TYPE;

			break;
		case CaConfig.IPPV_BUY_POINTER_INVALID:// 余额不足
			stringId = R.string.MESSAGE_NOMONEY_TYPE;
			break;
		case CaConfig.IPPV_BUY_CARD_NO_ROOM:// 智能卡没有空间
			stringId = R.string.MESSAGE_NOMONEY_TYPE;

			break;
		case CaConfig.IPPV_BUY_PRO_STATUS_INVALID:// 钱包状态不正确
			stringId = R.string.MESSAGE_NOMONEY_TYPE;

			break;
		case CaConfig.IPPV_BUY_DATA_NOT_FIND:// 没能找到对应的运营商和钱包
			stringId = R.string.MESSAGE_NOOPER_TYPE;
			break;
		default:
			break;
		}

		try {
			resultContent = getResources().getString(stringId);
		} catch (NotFoundException e) {
			resultContent = null;
		}
		return resultContent;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent arg1) {
        boolean isAction =false;
		switch (keyCode) {

			case KeyEvent.KEYCODE_DPAD_CENTER: 
				ippvPro.mCaIppvProgramInfoNovel.mIsBuy=true;
				isAction=true;
				break;
			
			case KeyEvent.KEYCODE_BACK: {
				ippvPro.mCaIppvProgramInfoNovel.mIsBuy=false;
				isAction=true;
				break;
			}
			case KeyEvent.KEYCODE_F1: 
			case KeyEvent.KEYCODE_DPAD_LEFT: {
				switchPrice();
				return true;
			}
			
		}
		if(isAction){
			ippvPro.mCaIppvProgramInfoNovel.mBuyTape=mCurType;
			
            if(ippvPro.mCaIppvProgramInfoNovel.mIsBuy){
            	String ippvMsg=ippvPro.mCaIppvProgramInfoNovel.mECMPId+"&&"+ippvPro.mCaIppvProgramInfoNovel.mBuyTape
    					+"&&"+ippvPro.mCaIppvProgramInfoNovel.mPrice+"&&"+ippvPro.mCaIppvProgramInfoNovel.mPriceTaping
    					+"&&"+ippvPro.mCaIppvProgramInfoNovel.mIsBuy;
				Intent intent=new Intent(CABuyIPPDialog.this, CaMainActivity.class);
	            intent.putExtra("title", "CAIPProgram");
	            intent.putExtra("ippvPro", ippvMsg);
				startActivity(intent);
            }else {
            	thisCa.stopIPPVBuyDlg("000000", ippvPro);
            }
			finish();
            return true;	
		}		
		return super.onKeyDown(keyCode, arg1);
	}
	
	
	

	

	/** handler 异步更新UI **/
	public Handler uiHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case IPP_BUY_DIALOG_REMOVE:
				CABuyIPPDialog.this.finish();
				break;
			case IPP_BUY_DIALOG_STOP:// 显示IPP购买结果
				ippBookDialog.setVisibility(View.GONE);
				ippBookResult.setVisibility(View.VISIBLE);
				BtnBookResult.requestFocus();
				break;
			default:
				break;
			}
		}
	};

}
