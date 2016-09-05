package com.changhong.app.timeshift.common;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/* MD5 Encrypt */
/*Author:OscarChang*/
public class MD5Encrypt {



	public static String MD5EncryptExecute(String original) {
		return stringToMD5(original + HttpParam.MD5KEY);
	}

	/* md5 func */
	private static  String stringToMD5(String string) {
		byte[] hash;

		try {
			hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}

		StringBuilder hex = new StringBuilder(hash.length * 2);
		for (byte b : hash) {
			if ((b & 0xFF) < 0x10)
				hex.append("0");
			hex.append(Integer.toHexString(b & 0xFF));
		}

		return hex.toString();
	}
}
