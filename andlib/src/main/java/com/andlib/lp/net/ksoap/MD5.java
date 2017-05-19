package com.andlib.lp.net.ksoap;

import java.security.MessageDigest;
import java.util.Locale;

import android.util.Log;

public class MD5 {
	
	private static final String Tag = "com.shundian.inter.MD5";
	
	/**
	 * MD5加密密文返回
	 * */
	public static String getMd5Value(String sSecret) {
		String result = null;
		try {
			MessageDigest bmd5 = MessageDigest.getInstance("MD5");
			bmd5.update(sSecret.getBytes());
			StringBuffer buf = new StringBuffer();
			try {
				result = byte2hex(bmd5.digest(buf.toString().getBytes("UTF-8")));
				result= result.toLowerCase();
			} catch (Exception e) {
				Log.e(Tag, "MD5加密失败", e);
			}
			return result;
		} catch (Exception e) {
			Log.e(Tag, "MD5加密失败", e);
		}
		return null;
	}

	/**
	 * 二进制转字符串
	 */
	private static String byte2hex(byte[] b) {
		StringBuffer hs = new StringBuffer();
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs.append("0").append(stmp);
			else
				hs.append(stmp);
		}
		return hs.toString().toUpperCase(Locale.ENGLISH);
	}
	
}
