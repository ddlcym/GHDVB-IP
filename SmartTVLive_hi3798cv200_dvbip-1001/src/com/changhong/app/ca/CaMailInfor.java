package com.changhong.app.ca;

import java.io.Serializable;

public class CaMailInfor implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int id;// 邮件ID
	public String revDate;
	public String revTime;
	public boolean isRead;// 邮件是否阅读
	public String title;// 邮件标题
	public String content;// 邮件内容
}
