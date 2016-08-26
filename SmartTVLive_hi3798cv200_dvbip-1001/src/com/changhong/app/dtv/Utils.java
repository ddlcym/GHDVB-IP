package com.changhong.app.dtv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {
	private Utils(){
		
	}
	
	/**
	 * get current time 
	 * @return current time with format "yyyyMMddHHmmss"
	 */
	public static String getCurTime()
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINESE);     
		Date curDate = new Date(System.currentTimeMillis());     	
		return  formatter.format(curDate);
	}
	
	/**
	 * creat folder if not exists 
	 * @return is exist or created successfully
	 */
	public static boolean creatFolderIfNotExists(String strFolder)
	{
        File file = new File(strFolder);        
        if (!file.exists())
        {
            if (file.mkdirs())
            {                
                return true;
            } 
            else 
            {
                return false;
            }
        }
        
        return true;
	}
	
	
	public static String getExternalStorageDirectory()
	{
	    String dir = new String();
	    
	    try
	    {
	        Runtime runtime = Runtime.getRuntime();
	        Process proc = runtime.exec("mount");
	        InputStream is = proc.getInputStream();
	        InputStreamReader isr = new InputStreamReader(is);
	        String line;
	        BufferedReader br = new BufferedReader(isr);
	        
	        while ((line = br.readLine()) != null) 
	        {
	            if (line.contains("secure"))
	            {
	            	continue;
	            }
	            
	            if (line.contains("asec")) 
	            {
	            	continue;
	            }
	            
	            if (line.contains("fat"))
	            {
	                String columns[] = line.split(" ");
	                
	                if (columns != null && columns.length > 1)
	                {
	                    dir = dir.concat(columns[1] + "/");
	                }
	            }
	            else if (line.contains("fuse"))
	            {
	                String columns[] = line.split(" ");
	                
	                if (columns != null && columns.length > 1)
	                {
	                    dir = dir.concat(columns[1] + "/");
	                }
	            }
	        }
	    }
	    catch (FileNotFoundException e) 
	    {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    } catch (IOException e) 
	    {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	    
	    return dir;
	}
}
