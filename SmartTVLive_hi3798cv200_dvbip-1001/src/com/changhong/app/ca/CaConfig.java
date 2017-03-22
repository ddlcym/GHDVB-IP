package com.changhong.app.ca;



public class CaConfig {
		
	public final static String CA_MSG_NOVEL_EVENT = "com.chots.app.ca.nove.event";// 同方消息
	public final static String CA_MSG_IPP_DIALOG = "com.changhong.app.ca.ipp.dialog";//订购消息
	public final static String CA_MSG_MAIL_EVENT = "chots.action.mailEvent";//邮件
	public final static String CA_MSG_CARD_UPGRATE = "com.chots.action.card.upgrate";//智能卡升级
	public final static String CA_MSG_PARENT_FEED = "com.chots.action.parent.feed";//子母卡喂养
	public final static String CA_MSG_CHANGE_STATUS = "com.chots.app.ca.change.status";//状态变化
	public final static String CA_MSG_SHOW_NEW_MAIL = "com.chots.app.ca.show.newmail";//显示新邮件
	public final static String CA_MSG_HIDE_MSGBOX = "com.chots.app.ca.hide.msgbox";//隐藏信息盒子
	public final static String CA_MSG_CTR_SWITCH = "com.chots.app.ca.ctr.switch";//使能CA开关

	
	public final static int CA_MSG_EMAIL_MAX_NUM = 50;// 邮件最大值
	public final static int CA_NOVEL_PIN_NUM = 6;// 同方PIN码的值
	public final static int CA_SUMA_PIN_NUM = 8;// 数码PIN码的值

	/***************************************************** CA消息 命令定义 ****************************************/
	
	public final static int CA_COMMAND_HIDE_MSGBOX= 255;// 关闭信息盒子
	public final static int CA_COMMAND_HIDE_FINGER= 256;// 关闭指纹信息
	public final static int CA_COMMAND_HIDE_OSDBOX= 257;// 关闭OSD盒子

	
	
	public final static int CA_COMMAND_SHOW_NOTICE = 1;//显示Notice信息
	
	// 指纹
	public final static int CA_COMMAND_FORCE_SHOW = 2;// 强制显示

	public final static int CA_COMMAND_FINGER_SHOW = 3;// 指纹显示

	public final static int CA_COMMAND_SUPEROSD_SHOW = 4;//超级OSD显示

	public final static int CA_COMMAND_SHOW_CURTAIN = 6;//高级预览
	public final static int CA_COMMAND_HIDE_CURTAIN = 7;//隐藏高级预览

	// 强制换台
	public final static int CA_COMMAND_LOCK_SERVICE = 8;// 强制换台
	public final static int CA_COMMAND_UNLOCK_SERVICE = 9;// 解除强制换台

	// 机顶盒通知消息
	public final static int CA_COMMAND_ACTION_REQUEST = 10;// 机顶盒通知消息

	// 超级邮件
	public final static int CA_COMMAND_SUPEMAIL_NEW = 11;// 新邮件
	public final static int CA_COMMAND_SUPEMAIL_HIDEICON = 12;// 隐藏邮件图标
	public final static int CA_COMMAND_SUPEMAIL_SPACEEXHAUST = 13;// 邮件已满

	// 黑名单
	public final static int CA_COMMAND_BLACK_LIST = 14;// 黑名单功能

	// 连续观看限制
	public final static int CA_COMMAND_CONTINUSE_WATCH = 15;// 连续观看
	public final static int CA_COMMAND_CONTINUSE_WATCH_LIMIT = 16;// 限制连续观看
	public final static int CA_COMMAND_CONTINUSE_WATCH_LIMIT_LOOP = 17;//循环执行连续观看

	// 显示购买信息
	public final static int CA_COMMAND_SHOW_IPPV_DLG = 18;// 显示购买信息
	
	//隐藏进度条
	public final static int CA_COMMAND_HIDE_PROGRESS = 19;// 隐藏进度条

	//重启机顶盒
     public final static int CA_COMMAND_REBOOT = 20;// 重启机顶盒
     
   //初始化机顶盒
     public final static int CA_COMMAND_INITBOX = 21;// 初始化机顶盒

	/********************************************* CA 信息定义 *********************************************/

	public final static int CA_MSG_ACTION_REQUEST_SYSMSG = 1;// 系统信息
	public final static int CA_MSG_ACTION_REQUEST_FACTORYRESET = 2;// 恢复出厂设置
	
	
	

	/*********************************************  IPPV 购买 显示    *********************************************/

		public final static int CA_NOVEL_IPPV_FREEVIEWED_VALUE = 0;//IPP免费预览阶段
		public final static int CA_NOVEL_IPPV_PAYVIEWED_VALUE = 1;//iPPV收费阶段	
		public final static int CA_NOVEL_IPPT_PAYVIEWED_VALUE = 2;//iPPT收费阶段
		
		public final static int IPPV_BUY_OK = 0;//IPP购买成功
		public final static int IPPV_BUY_CARD_INVALID = 1;//卡无效
		public final static int IPPV_BUY_POINTER_INVALID = 2;//余额不足
		public final static int IPPV_BUY_CARD_NO_ROOM = 3;//智能卡没有空间
		public final static int IPPV_BUY_PRO_STATUS_INVALID = 4;//钱包状态不正确
		public final static int IPPV_BUY_DATA_NOT_FIND = 5;//没能找到对应的运营商和钱包


	/********************************************* SUPER OSD 显示 *********************************************/

	public final static int CA_MSG_SUPEROSD_SHOWTYPE_FORCE = 1;// 强制显示
	public final static int CA_MSG_SUPEROSD_SHOWTYPE_UNFORCE = 0;//非强迫显示
	public final static int CA_MSG_OSD_POSITION_CENTER = 0;//OSD正中央显示
	public final static int CA_MSG_OSD_POSITION_TOP = 1;//OSD屏幕顶部从右向左滚动
	public final static int CA_MSG_OSD_POSITION_BOTTOM = 2;//OSD屏幕底部从右向左滚动

	/********************************************** 同方命令定义 **************************************************/

	public final static int NOVEL_CODE_CB_SHOW_BUY_MSG = 1;
	public final static int NOVEL_CODE_CB_SHOW_FINGER = 2;
	public final static int NOVEL_CODE_CB_SHOW_OSD = 3;
	public final static int NOVEL_CODE_CB_HIDE_OSD = 4;
	public final static int NOVEL_CODE_CB_MAIL = 5;
	public final static int NOVEL_CODE_CB_SHOW_IPPV_DLG = 6;
	public final static int NOVEL_CODE_CB_HIDE_IPPV_DLG = 7;
	public final static int NOVEL_CODE_CB_PARENT_FEED = 8;
	public final static int NOVEL_CODE_CB_LOCK_SERVICE = 9;
	public final static int NOVEL_CODE_CB_UNLOCK_SERVICE = 10;
	public final static int NOVEL_CODE_CB_DETITLE = 11;
	public final static int NOVEL_CODE_CB_PROGRESS_DISPLAY = 12;
	public final static int NOVEL_CODE_CB_ENTITLE = 13;
	public static final int NOVEL_CODE_CB_HIDE_FINGER = 14;
	public static final int NOVEL_CODE_CB_ACTIONREQ = 15;
	public static final int NOVEL_CODE_CB_SHOW_CURTAIN = 16;
	public static final int NOVEL_CODE_CB_SHOW_SUPFP = 17;
	public static final int NOVEL_CODE_CB_HIDE_SUPFP = 18;
	public static final int NOVEL_CODE_CB_SHOW_SUPOSD = 19;
	public static final int NOVEL_CODE_CB_HIDE_SUPOSD = 20;
	public static final int NOVEL_CODE_CB_CONTINUEWATCHLIMIT = 21;
	
	
	/*******************************************ACTIONREQUEST type*************************************************/
	public static final int CA_NOVEL_ACTIONREQUEST_RESTARTSTB=0;/* 重启机顶盒 */
	public static final int CA_NOVEL_ACTIONREQUEST_FREEZESTB=1;	/* 冻结机顶盒 */
	public static final int CA_NOVEL_ACTIONREQUEST_SEARCHCHANNEL=2;    /* 重新搜索节目 */
	public static final int CA_NOVEL_ACTIONREQUEST_STBUPGRADE=3;   /* 机顶盒程序升级 */
	public static final int CA_NOVEL_ACTIONREQUEST_UNFREEZESTB=4;    /* 解除冻结机顶盒 */
	public static final int CA_NOVEL_ACTIONREQUEST_INITIALIZESTB=5;    /* 初始化机顶盒 */
	public static final int CA_NOVEL_ACTIONREQUEST_SHOWSYSTEMINFO=6;    /* 显示系统信息 */
	public static final int CA_NOVEL_ACTIONREQUEST_DISABLEEPGADINFO=7;   /* 禁用EPG广告信息功能 */
	public static final int CA_NOVEL_ACTIONREQUEST_ENABLEEPGADINFO=8;    /* 恢复EPG广告信息功能 */
	public static final int CA_NOVEL_ACTIONREQUEST_CARDINSERTFINISH=9;    /* 插卡处理完成 */

}
