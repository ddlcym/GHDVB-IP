package com.changhong.app.book;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class BookInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String bookDay;
	public String bookTimeStart;
	public String bookEnventName;
	public String bookChannelName;	
	public int 	  bookChannelIndex;
	
	public BookInfo myclone() {
		BookInfo outer = null;
		try { // 将该对象序列化成流,因为写在流里的是对象的一个拷贝，而原对象仍然存在于JVM里面。所以利用这个特性可以实现对象的深拷贝
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(this);
			// 将流序列化成对象
			ByteArrayInputStream bais = new ByteArrayInputStream(
					baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			outer = (BookInfo) ois.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return outer;
	}	
	
}
