package com.changhong.app.constant;

// 区域化定义类(使用省会区号电话)
public class Class_OP {
	// 宁夏省网
	public final static int OP_NINGXIA_WANG		   	= 0x09510000;
	// 新疆区网
	public final static int OP_XINJIANG_WANG		= 0x09910000;
	// 四川省网
	public final static int OP_SICHUAN_WANG		   	= 0x08100000;
	// 成都兴网
	public final static int OP_CHENGDU_XINGWANG		= 0x00280000;
	// 广东省网
	public final static int OP_GUANGDONG_WANG		= 0x07500000;
	// 陕西省网
	public final static int OP_SHANXI_WANG			= 0x09100000;
	// 湖北省网
	public final static int OP_HUBEI_WANG			= 0x07100000;
	// 安徽省网
	public final static int OP_ANHUI_WANG			= 0x05510000;
	// 重庆市网
	public final static int OP_CHONGQING_WANG		= 0x00230000;
	// 内蒙区网
	public final static int OP_NEIMENG_WANG			= 0x04710000;
	// 吉林省网
	public final static int OP_JILIN_WANG			= 0x04510000;
	// 长虹内网
	public final static int OP_CH_WANG				= 0x08160241;
	// 绵阳市网
	public final static int OP_MIANYANG_WANG		= 0x08160000;

	public static int mi_OPConfig 					= OP_MIANYANG_WANG;

	// 吉林省网参数
	// 排序用的Bouquent_ID
	public final static short mi_Jilin_Sort_BqtID	= 0x7011;
	public final static short mi_Jilin_Sort_Desc	= 0x90;
	
	// 重庆市网参数
	public final static short mi_Cq_Default_BqtID	= 0x6001;
	public final static short mi_Cq_Logic_Desc		= 0xE2;
	public final static short mi_Cq_OP_ID			= 2300;


	// 四川省网
	public final static short mi_SC_Default_BqtID	= 0x7148;
	public final static short mi_SC_BAT_TID                  =0x4a;
	public final static short mi_SC_BAT_PID			= 0x11;
	
	// 成都兴网
	public final static short mi_CDX_Default_BqtID	= 0x7011;
	public final static short mi_CDX_Logic_Desc		= 0x82;
	public final static short mi_CDX_OP_ID			= 2802;
	public final static short mi_CDX_BAT_TID                  =0x4a;
	public final static short mi_CDX_BAT_PID			= 0x11;
	
	public final static short mi_CDX_PFT_TID                  =218; 
	public final static short mi_CDX_PFT_PID			= 7585;


	// 绵阳市网
	public final static short mi_MY_PFT_TID					= 218;
	public final static short mi_MY_PFT_PID					= 7585;
	public final static int   mi_MY_Service_Default_SID		= 45;
	public final static int   mi_MY_Service_Default_TSID	= 1;
	public final static int   mi_MY_Service_Default_NetID	= 2070;
}
