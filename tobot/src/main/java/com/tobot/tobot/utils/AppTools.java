package com.tobot.tobot.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.telephony.TelephonyManager;

public class AppTools {

	/**
	 *
	 * 判断网络是否可用
	 * 对是否连接WIFI进行判断
	 * @return true, 可用； false， 不可用
	 */
	public static boolean netWorkAvailable(Context mContext) {
		try {
			ConnectivityManager connectivityManager = (ConnectivityManager) mContext
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetInfo = connectivityManager
					.getActiveNetworkInfo();
			if (activeNetInfo != null
					&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				return true;
			}else {
				if (activeNetInfo != null
						&& activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
					return true;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}


	/**
	 *
	 * 获取App版本号
	 *
	 */
	public static int getVersionCode(Context context) {
		try {
			PackageInfo mPackageInfo = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return mPackageInfo.versionCode;
		} catch (NameNotFoundException e) {
		}
		return 1;
	}

	/**
	 *
	 * 获取android系统版本号
	 *
	 */
	public static String getAndroidVERSIONS () {
		return android.os.Build.VERSION.RELEASE;  // android系统版本号

	}

	/**
	 *
	 * 获取手机IMEI
	 *
	 */
	public static String getIMEI(Context context) {
		return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE))
				.getDeviceId();

	}

}
