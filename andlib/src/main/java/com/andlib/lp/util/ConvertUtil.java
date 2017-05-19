package com.andlib.lp.util;

import java.math.BigDecimal;
 //double point 2
public class ConvertUtil {

	public static double setFormat(double d) {
		BigDecimal bg = new BigDecimal(d);
		double f = bg.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
		return f;
	}

	public static double setFormat(String d) {
		BigDecimal bg = new BigDecimal(d);
		double f = bg.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
		return f;
	}
	
	
	
	
	
	
	
	
	
	
	
}
