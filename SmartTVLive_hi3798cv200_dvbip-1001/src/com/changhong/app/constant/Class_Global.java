package com.changhong.app.constant;

public class Class_Global {


	public final static int	  MENU_ID_ROOT						= 0;
	public final static int	  MENU_ID_BANNER					= 1;
	public final static int	  MENU_ID_EPG_LIST					= 2;
	public final static int	  MENU_ID_MAIN_MENU					= 3;
	public final static int	  MENU_ID_SCAN_SET					= 4;
	public final static int	  MENU_ID_SCAN						= 5;
	public final static int	  MENU_ID_DIALOG					= 6;
	public final static int	  MENU_ID_SETTING					= 7;	
	public final static int	  MENU_ID_DTV_CA_MAIN				= 8;
	public static boolean b_FirstLoadIn = true;
	
	public static boolean b_FirstBoot = true;
	
	
	private static int gi_AimMenuID;		// 目标菜单ID，在返回时使用
	private static int gi_PushRootKey;	// 压入根菜单键值
	private static int gi_CurrMenuID;	// 当前菜单ID

	public static int bNeedScan=0;
	static
	{
          gi_PushRootKey	 = -1;
	}
	
	// 设置当前菜单ID
	public static void SetCurrMenuID ( int ri_MenuID )
	{
		gi_CurrMenuID = ri_MenuID;
	}

	// 得到当前菜单ID
	public static int GetCurrMenuID ()
	{
		return gi_CurrMenuID;
	}
	
	// 设置目标菜单ID
	public static void SetAimMenuID ( int ri_MenuID )
	{
		gi_AimMenuID = ri_MenuID;
	}

	// 得到目标菜单ID
	public static int GetAimMenuID ()
	{
		return gi_AimMenuID;
	}
	
	// 设置主菜单压键
	public static void SetRootMenuVKey ( int ri_VKey )
	{
		gi_PushRootKey = ri_VKey;
	}
	
	// 得到主菜单压键
	public static int GetRootMenuVKey ()
	{
		return gi_PushRootKey;
	}
}
