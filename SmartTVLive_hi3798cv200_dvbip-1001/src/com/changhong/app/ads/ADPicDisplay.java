package com.changhong.app.ads;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.changhong.app.dtv.R;
import com.changhong.dvb.Channel;

public class ADPicDisplay {
	
	private static ImageView[] image_ad = new ImageView[3];
	private static ADPicJsonParser adPicJsonParser;
	private static ADPicDisplay aDPicDisplay;
	
	private ADPicDisplay(){
		if(adPicJsonParser==null){
			adPicJsonParser = ADPicJsonParser.getADPicJsonParserInstance();		
			adPicJsonParser.startParse();
		}
	}
	public static ADPicDisplay getInstance(){
		if(aDPicDisplay==null)
			aDPicDisplay = new ADPicDisplay();
		return aDPicDisplay;
	}
	public void addPicDisplayItem(int ad_pos, ImageView iv){
		if(ad_pos>=0&&ad_pos<=2){
			image_ad[ad_pos] = iv;
		}		
	}	
	public void addPicDisplayItem(ImageView iv_banner,ImageView iv_volbar,ImageView iv_chanlist){
		image_ad[0] = iv_banner;
		image_ad[1]  = iv_volbar;
		image_ad[2]  = iv_chanlist;
	}
	/*
	private static void disADPic(ImageView imView,Bitmap bitmap){
		imView.setImageBitmap(bitmap); 
	}
	*/
	public void disADPic(int ad_pos, Channel channel, String curTime) {
		if (adPicJsonParser.isADPicParsingNow()) {
			Log.i("ADV", "ADPicParsingNow is true,so use default pic");
		} else {
			String channel_id = channel.tsId + "-" + channel.serviceId;
			ImageView imView = checkIV(ad_pos);
			if (imView != null) {
				Bitmap bitmap = adPicJsonParser.getBitMap(ad_pos, channel_id,
						curTime);

				if (bitmap != null)
					imView.setImageBitmap(bitmap);
				else {
					imView.setBackgroundResource(R.drawable.default_img);
				}
			}
		}
	}
	
	private ImageView checkIV(int ad_pos) {
		if(ad_pos>=0&&ad_pos<=2){
			return image_ad[ad_pos];
		}		
		return null;
	}
	public void updateADPicSource() {
		if(adPicJsonParser!=null){
			Log.i("ADV", "updateADPicSource begin");
			adPicJsonParser.startParse();
			Log.i("ADV", "updateADPicSource end");
		}else{
			Log.e("ADV", "updateADPicSource error >> adPicJsonParser is null");
		}
	}
}
