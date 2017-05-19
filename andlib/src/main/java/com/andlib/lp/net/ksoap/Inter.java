package com.andlib.lp.net.ksoap;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.andlib.lp.util.L;

/*******调用该类进行(webservice)ksoap的同步请求 */
public class Inter {
	private static final String tag = "Inter";
	// sundan.kemai.cn
	// 调用地址
	// private static final String url ="http://192.168.253.116:16901/jdews/JdeService.asmx";
	// 命名空间
	private static final String nameSpace = "http://tempuri.org/";
	// MD5 key
	private static final String key = "kmtech&sundan&ws";

	private static final int JDEURL = 0;
	private static final int CMSURL = 1;
	private static final int HQURL = 2;
	private static final int OMSURL = 3;
	private static final int SMSURL = 4;
	private static final int UPDURL = 5;
	private static final int UPLODATURL = 6;

	/**** 获取版本 */
	public static String getAppVersion(String versionNo) {
		String result = null;
		// 调用的方法名
		String methodName = "GetAppUpgradeUrl";
		// SOAP Action
		String soapAction = "http://tempuri.org/GetAppUpgradeUrl";
		// 指定WebService的命名空间和调用的方法名
		SoapObject rpc = new SoapObject(nameSpace, methodName);
		rpc.addProperty("Version", versionNo);
		String md5Str = MD5.getMd5Value(versionNo + key);
		L.e("====================== Params ======================");
		L.e("HashCode = " + md5Str);
		rpc.addProperty("HashCode", md5Str);
		result = callWebService(rpc, soapAction, OMSURL);
		return result;
	}

	/**
	 * 查询异常信息
	 * 
	 * @param SheetNo
	 * @param VipPhone
	 * @param BranchNo
	 * @return
	 */
	public static String GetErrorSaleFlowList(String SheetNo, String VipPhone,
			String BranchNo) {
		String result = null;
		// 调用的方法名
		String methodName = "GetErrorSaleFlowList";
		// SOAP Action
		String soapAction = "http://tempuri.org/GetErrorSaleFlowList";
		// 指定WebService的命名空间和调用的方法名
		SoapObject rpc = new SoapObject(nameSpace, methodName);
		rpc.addProperty("SheetNo", SheetNo);
		rpc.addProperty("VipPhone", VipPhone);
		rpc.addProperty("BranchNo", BranchNo);
		String md5Str = MD5.getMd5Value(SheetNo + VipPhone + BranchNo + key);
		L.e("====================== Params ======================");
		L.e("HashCode = " + md5Str);
		rpc.addProperty("HashCode", md5Str);
		result = callWebService(rpc, soapAction, OMSURL);
		return result;
	}

	
	/**
	 * webservice 请求方法
	 * @param rpc
	 * @param soapAction
	 * @return  请求的结果
	 */
	private static String callWebService(SoapObject rpc, String soapAction,int type) {
		
		String reusult = null;
		String serviceUrl = "";
		int timeOut = 30000;
		
		switch (type) {
		
		case JDEURL:
			 serviceUrl ="http://192.168.1.101/KMSunDanWS/JDEService.asmx";// CESHI
			L.i("进入jde-url:" + serviceUrl);
			break;
		default:
//			serviceUrl = MyApp.getmInstance().getJde_url();
			L.i("进入default:" + serviceUrl);
			break;
		}
		
		com.andlib.lp.net.ksoap.HttpTransportSE transport = new com.andlib.lp.net.ksoap.HttpTransportSE(serviceUrl);
		transport.setTimeOut(timeOut);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
		envelope.bodyOut = rpc;
		envelope.dotNet = true;
		try {
			transport.call(soapAction, envelope);
			Object object;
			object = (Object) envelope.getResponse();
			reusult = object.toString();
			L.i(tag,"reuslt=: " + reusult);
			return reusult;
		} catch (Exception e) {
			e.printStackTrace();
			L.i(tag,"call err:" + e);
			reusult = null;
		}
		return reusult;
	}
	
}