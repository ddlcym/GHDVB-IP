package com.changhong.app.constant;

import com.changhong.app.dtv.Common;

/**
 * @author OscarChang
 * @广告参数
 */
public class Advertise_Constant {

	public static final int PFBANNER_CONS = 7001;
	public static final int OKLIST_CONS = 7002;

	// public static final String ADNET_ADDRESS =
	// "ads001.hbxig.citicguoanbn.com";// 广告位系统地址
	public static final String ADNET_ADDRESS = "http://10.0.110.7:8080";// 广告位系统地址
	public static final String TEMP_IP_ADDRESS = "10.0.110.164";// 盒子端IP信息

	public static final String LIVEPLAY_ID_PFBANNER = "yinhedtvpf";// 直播ID，PF条
	public static final String LIVEPLAY_ID_VOLUME = "yinhedtvvolume";// 直播ID，音量
	public static final String LIVEPLAY_ID_GUIDE = "yinhedtvzhinan";// 直播ID，指南
	public static final String LIVEPLAY_ID_OKLIST = "yinhedtvoklist";// 直播ID，OKLIST

	/* param to get advertisement others from pass */
	public static final String CARD_USER_ID = "NONE0000000000000000";
	public static final String APPLICATION_ID = "com.changhong.app.dtv";
	public static final String DEVICE_ID = "00000000000000000";
	public static final String DEVICE_TYPE = "0305";

	/* advertisement type */
	public static final String AD_TYPE_IMAGE = "005";// 图片广告
	public static final String AD_TYPE_VIDEO = "002";// 视频广告
	public static final String AD_TYPE_SELF = "001";// 视频广告

	/* 广告策略 JSON请求的参数 */
	// public static class ADJSONReqParams {
	// public static final String CATALOG_ID = "catalog_id";// 直播注释
	// 在VOD应用或者其他以树状方式呈现信息的应用下，当前所在页面位置标识
	// public static final String CHANNEL_ID = "1010";// 在广播应用下的频道标识
	// public static final String ADPOSITION_ID = "";// 广告位Id 不在这里进行传递 注释
	// public static final String IP = "10.0.110.164";// IP或者Node Group ID（必填）
	// public static final String USER_ID = "user_id";//智能卡卡号
	// public static final String APPLICATION_ID =
	// "com.changhong.app.dtv";
	//
	// public static final String DECISION_ID = "decision_id";// 决策Id 直播注释
	// public static final String DEVICE_ID = "device_id";// 设备标识（必填）
	// public static final String DEVICE_TYPE = "device_type";// 设备类型，手机、机顶盒（必填）
	// public static final String PROVIDER_ID = "provider_id";//直播注释
	// public static final String PROVIDER_ASSET_ID = "provider_asset_id";//直播注释
	// public static final String APPLICATION_PATH = "application_path";//直播注释
	// }
}
