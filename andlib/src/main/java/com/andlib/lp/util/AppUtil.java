package com.andlib.lp.util;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.text.TextUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.security.auth.x500.X500Principal;


public class AppUtil {

	private static final String TAG = "APPUtil";

	private AppUtil() {

		throw new UnsupportedOperationException("cannot be instantiated");

	}


	public static String getAppName(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			int labelRes = packageInfo.applicationInfo.labelRes;
			return context.getResources().getString(labelRes);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}


	public static String getVersionName(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			return packageInfo.versionName;

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}


	public static int getVersionCode(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			return packageInfo.versionCode;

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return -1;
	}


	public static int getNumCores() {
		try {
			File dir = new File("/sys/devices/system/cpu/");
			File[] files = dir.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					if (Pattern.matches("cpu[0-9]", pathname.getName())) {
						return true;
					}
					return false;
				}
			});
			return files.length;
		} catch (Exception e) {
			return 1;
		}
	}


	public static boolean isNamedProcess(Context context, String processName) {
		if (context == null || TextUtils.isEmpty(processName)) {
			return false;
		}

		int pid = android.os.Process.myPid();
		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> processInfoList = manager
				.getRunningAppProcesses();
		if (processInfoList == null) {
			return true;
		}

		for (RunningAppProcessInfo processInfo : manager
				.getRunningAppProcesses()) {
			if (processInfo.pid == pid
					&& processName.equalsIgnoreCase(processInfo.processName)) {
				return true;
			}
		}
		return false;
	}


	public static boolean isApplicationInBackground(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> taskList = am.getRunningTasks(1);
		if (taskList != null && !taskList.isEmpty()) {
			ComponentName topActivity = taskList.get(0).topActivity;
			if (topActivity != null
					&& !topActivity.getPackageName().equals(
					context.getPackageName())) {
				return true;
			}
		}
		return false;
	}


	public static String getSign(Context context, String pkgName) {
		try {
			PackageInfo pis = context.getPackageManager().getPackageInfo(
					pkgName, PackageManager.GET_SIGNATURES);
			return hexdigest(pis.signatures[0].toByteArray());
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}


	private static String hexdigest(byte[] paramArrayOfByte) {
		final char[] hexDigits = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97,
				98, 99, 100, 101, 102 };
		try {
			MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
			localMessageDigest.update(paramArrayOfByte);
			byte[] arrayOfByte = localMessageDigest.digest();
			char[] arrayOfChar = new char[32];
			for (int i = 0, j = 0;; i++, j++) {
				if (i >= 16) {
					return new String(arrayOfChar);
				}
				int k = arrayOfByte[i];
				arrayOfChar[j] = hexDigits[(0xF & k >>> 4)];
				arrayOfChar[++j] = hexDigits[(k & 0xF)];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}


	public static int gc(Context context) {
		long i = getDeviceUsableMemory(context);
		int count = 0;
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> serviceList = am.getRunningServices(100);
		if (serviceList != null)
			for (RunningServiceInfo service : serviceList) {
				if (service.pid == android.os.Process.myPid())
					continue;
				try {
					android.os.Process.killProcess(service.pid);
					count++;
				} catch (Exception e) {
					e.getStackTrace();
				}
			}


		List<RunningAppProcessInfo> processList = am.getRunningAppProcesses();
		if (processList != null)
			for (RunningAppProcessInfo process : processList) {
				 if (process.importance > RunningAppProcessInfo.IMPORTANCE_VISIBLE) {

					String[] pkgList = process.pkgList;
					for (String pkgName : pkgList) {

						L.d(TAG, "======kill pkg" + pkgName);

						try {
							am.killBackgroundProcesses(pkgName);
							count++;
						} catch (Exception e) {
							e.getStackTrace();
						}
					}
				}
			}

		L.d(TAG, "清理了" + (getDeviceUsableMemory(context) - i) + "M内存");

		return count;
	}

	/**
	 * 获取设备的可用内存大小
	 *
	 * @param context
	 *            应用上下文对象context
	 * @return 当前内存大小
	 */
	public static int getDeviceUsableMemory(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo mi = new MemoryInfo();
		am.getMemoryInfo(mi);
		// 返回当前系统的可用内存
		return (int) (mi.availMem / (1024 * 1024));
	}

	/**
	 * 获取系统中所有的应用
	 *
	 * @param context
	 *            上下文
	 * @return 应用信息List
	 */
	public static List<PackageInfo> getAllApps(Context context) {

		List<PackageInfo> apps = new ArrayList<PackageInfo>();
		PackageManager pManager = context.getPackageManager();
		List<PackageInfo> paklist = pManager.getInstalledPackages(0);
		for (int i = 0; i < paklist.size(); i++) {
			PackageInfo pak = paklist.get(i);
			if ((pak.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
				// customs applications
				apps.add(pak);
			}
		}
		return apps;
	}

	/**
	 * 获取手机系统SDK版本
	 *
	 * @return 如API 17 则返回 17
	 */
	public static int getSDKVersion() {
		return android.os.Build.VERSION.SDK_INT;
	}

	/**
	 * 是否Dalvik模式
	 *
	 * @return 结果
	 */
	public static boolean isDalvik() {
		return "Dalvik".equals(getCurrentRuntimeValue());
	}

	/**
	 * 是否ART模式
	 *
	 * @return 结果
	 */
	public static boolean isART() {
		String currentRuntime = getCurrentRuntimeValue();
		return "ART".equals(currentRuntime)
				|| "ART debug build".equals(currentRuntime);
	}

	/**
	 * 获取手机当前的Runtime
	 *
	 * @return 正常情况下可能取值Dalvik, ART, ART debug build;
	 */
	public static String getCurrentRuntimeValue() {
		try {
			Class<?> systemProperties = Class
					.forName("android.os.SystemProperties");
			try {
				Method get = systemProperties.getMethod("get", String.class,
						String.class);
				if (get == null) {
					return "WTF?!";
				}
				try {
					final String value = (String) get.invoke(systemProperties,
							"persist.sys.dalvik.vm.lib",
							/* Assuming default is */"Dalvik");
					if ("libdvm.so".equals(value)) {
						return "Dalvik";
					} else if ("libart.so".equals(value)) {
						return "ART";
					} else if ("libartd.so".equals(value)) {
						return "ART debug build";
					}

					return value;
				} catch (IllegalAccessException e) {
					return "IllegalAccessException";
				} catch (IllegalArgumentException e) {
					return "IllegalArgumentException";
				} catch (InvocationTargetException e) {
					return "InvocationTargetException";
				}
			} catch (NoSuchMethodException e) {
				return "SystemProperties.get(String key, String def) method is not found";
			}
		} catch (ClassNotFoundException e) {
			return "SystemProperties class is not found";
		}
	}

	private final static X500Principal DEBUG_DN = new X500Principal(
			"CN=Android Debug,O=Android,C=US");

	/**
	 * 检测当前应用是否是Debug版本
	 *
	 * @param ctx
	 * @return
	 */
	public static boolean isDebuggable(Context ctx) {
		boolean debuggable = false;
		try {
			PackageInfo pinfo = ctx.getPackageManager().getPackageInfo(
					ctx.getPackageName(), PackageManager.GET_SIGNATURES);
			Signature signatures[] = pinfo.signatures;
			for (int i = 0; i < signatures.length; i++) {
				CertificateFactory cf = CertificateFactory.getInstance("X.509");
				ByteArrayInputStream stream = new ByteArrayInputStream(
						signatures[i].toByteArray());
				X509Certificate cert = (X509Certificate) cf
						.generateCertificate(stream);
				debuggable = cert.getSubjectX500Principal().equals(DEBUG_DN);
				if (debuggable)
					break;
			}

		} catch (NameNotFoundException e) {
		} catch (CertificateException e) {
		}
		return debuggable;
	}

}
