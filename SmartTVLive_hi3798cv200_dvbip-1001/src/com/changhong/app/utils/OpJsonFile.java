package com.changhong.app.utils;

import java.io.BufferedReader;  
import java.io.File;  
import java.io.FileReader;  
import java.io.FileWriter;  
import java.io.IOException;  
import java.io.PrintWriter;  
import org.json.JSONException;  
import org.json.JSONObject;  
  
public class OpJsonFile {  
  
    /** 
     * @param args 
     * @throws JSONException 
     * @throws IOException 
     */  
   
    public static void writeJSONObj(String filePath, JSONObject obj){
    	try {
            FileWriter fw = new FileWriter(filePath);  
            PrintWriter out = new PrintWriter(fw);  
            out.write(obj.toString());  
            out.println();  
            fw.close();  
            out.close();  			
		} catch (Exception e) {
			e.printStackTrace();
		}

    }  
  
    public static JSONObject readJSONObj(String path){

        File file = new File(path);  
        BufferedReader reader = null;  
        String laststr = "";  
        JSONObject object=null;
        try {  
            reader = new BufferedReader(new FileReader(file));  
            String tempString = null;  
            while ((tempString = reader.readLine()) != null) {  
                laststr = laststr + tempString;  
            }  
            reader.close();  
            object = new JSONObject(laststr);
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            if (reader != null) {  
                try {  
                    reader.close();  
                } catch (IOException e1) {  
                	e1.printStackTrace();
                }  
            }  
        }                
        return object;  
    }  
}  
