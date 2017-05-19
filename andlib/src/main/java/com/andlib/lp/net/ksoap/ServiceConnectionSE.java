// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ServiceConnectionSE.java

package com.andlib.lp.net.ksoap;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

// Referenced classes of package org.ksoap2.transport:
//			ServiceConnection

public class ServiceConnectionSE
	implements ServiceConnection
{

	public void setConnectTimeOut(int timeout) {  
	    connection.setConnectTimeout(timeout);  
	}  
	private HttpURLConnection connection;

	public ServiceConnectionSE(String url)
		throws IOException
	{
		connection = (HttpURLConnection)(new URL(url)).openConnection();
		connection.setUseCaches(false);
		connection.setDoOutput(true);
		connection.setDoInput(true);
	}

	public void connect()
		throws IOException
	{
		connection.connect();
	}

	public void disconnect()
	{
		connection.disconnect();
	}

	public void setRequestProperty(String string, String soapAction)
	{
		connection.setRequestProperty(string, soapAction);
	}

	public void setRequestMethod(String requestMethod)
		throws IOException
	{
		connection.setRequestMethod(requestMethod);
	}

	public OutputStream openOutputStream()
		throws IOException
	{
		return connection.getOutputStream();
	}

	public InputStream openInputStream()
		throws IOException
	{
		return connection.getInputStream();
	}

	public InputStream getErrorStream()
	{
		return connection.getErrorStream();
	}

}
