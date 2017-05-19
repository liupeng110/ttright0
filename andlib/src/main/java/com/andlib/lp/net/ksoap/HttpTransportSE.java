// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HttpTransportSE.java

package com.andlib.lp.net.ksoap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.transport.Transport;
import org.xmlpull.v1.XmlPullParserException;

// Referenced classes of package org.ksoap2.transport:
//			Transport, ServiceConnectionSE, ServiceConnection

public class HttpTransportSE extends Transport
{
	
	private int timeout = 20000;//
	public HttpTransportSE(String url)
	{	super(url);
		
	}

	public void setTimeOut(int timeout){
		this.timeout = timeout;
	}
	public void call(String soapAction, SoapEnvelope envelope)
		throws IOException, XmlPullParserException
	{
		if (soapAction == null)
			soapAction = "\"\"";
		byte requestData[] = createRequestData(envelope);
		requestDump = debug ? new String(requestData) : null;
		responseDump = null;
		com.andlib.lp.net.ksoap.ServiceConnection  connection = getServiceConnection();
		connection.setConnectTimeOut(timeout);
		connection.setRequestProperty("User-Agent", "kSOAP/2.0");
		connection.setRequestProperty("SOAPAction", soapAction);
		connection.setRequestProperty("Content-Type", "text/xml");
		connection.setRequestProperty("Connection", "close");
		connection.setRequestProperty("Content-Length", "" + requestData.length);
		connection.setRequestMethod("POST");
		connection.connect();
		OutputStream os = connection.openOutputStream();
		os.write(requestData, 0, requestData.length);
		os.flush();
		os.close();
		requestData = null;
		InputStream is;
		try
		{
			connection.connect();
			is = connection.openInputStream();
		}
		catch (IOException e)
		{
			is = connection.getErrorStream();
			if (is == null)
			{
				connection.disconnect();
				throw e;
			}
		}
		if (debug)
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte buf[] = new byte[256];
			do
			{
				int rd = is.read(buf, 0, 256);
				if (rd == -1)
					break;
				bos.write(buf, 0, rd);
			} while (true);
			bos.flush();
			buf = bos.toByteArray();
			responseDump = new String(buf);
			is.close();
			is = new ByteArrayInputStream(buf);
		}
		parseResponse(envelope, is);
	}

	protected ServiceConnection getServiceConnection()
		throws IOException
	{
		return new com.andlib.lp.net.ksoap.ServiceConnectionSE(url);
	}
}
