// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ServiceConnection.java

package com.andlib.lp.net.ksoap;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface ServiceConnection
{
	public abstract void setConnectTimeOut(int timeOut);
	public abstract void connect()
		throws IOException;

	public abstract void disconnect()
		throws IOException;

	public abstract void setRequestProperty(String s, String s1)
		throws IOException;

	public abstract void setRequestMethod(String s)
		throws IOException;

	public abstract OutputStream openOutputStream()
		throws IOException;

	public abstract InputStream openInputStream()
		throws IOException;

	public abstract InputStream getErrorStream();
}
