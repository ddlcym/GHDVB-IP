package com.changhong.app.ads;

import com.changhong.dvb.Channel;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class ADPicDisplay {
	
	private static ImageView[] image_ad = new ImageView[3];
	private static ADPicJsonParser adPicJsonParser;
	
	public ADPicDisplay(){
		if(adPicJsonParser==null)
			adPicJsonParser = ADPicJsonParser.getADPicJsonParserInstance();		
	}
	
	public void addPicDisplayItem(int ad_pos, ImageView iv){
		if(ad_pos>=0&&ad_pos<=2){
			image_ad[ad_pos] = iv;
		}		
		if(adPicJsonParser==null)
			adPicJsonParser = ADPicJsonParser.getADPicJsonParserInstance();
	}	
	public void addPicDisplayItem(ImageView iv_banner,ImageView iv_volbar,ImageView iv_chanlist){
		image_ad[0] = iv_banner;
		image_ad[1]  = iv_volbar;
		image_ad[2]  = iv_chanlist;
		if(adPicJsonParser==null)
			adPicJsonParser = ADPicJsonParser.getADPicJsonParserInstance();
	}
	/*
	private static void disADPic(ImageView imView,Bitmap bitmap){
		imView.setImageBitmap(bitmap); 
	}
	*/
	public void disADPic(int ad_pos,Channel channel, String curTime){
		String channel_id = channel.tsId+"-"+channel.serviceId;
		ImageView imView = checkIV(ad_pos);
		Bitmap bitmap = adPicJsonParser.getBitMap(ad_pos,channel_id,curTime);
		if(imView!=null && bitmap!=null)
			imView.setImageBitmap(bitmap); 
	}
	
	private ImageView checkIV(int ad_pos) {
		if(ad_pos>=4&&ad_pos<=6){
			ad_pos -=4;
		}	
		if(ad_pos>=0&&ad_pos<=2){
			return image_ad[ad_pos];
		}		
		return null;
	}
}
