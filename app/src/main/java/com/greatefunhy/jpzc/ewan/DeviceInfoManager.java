package com.greatefunhy.jpzc.ewan;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;

public class DeviceInfoManager {
	private static String TAG = "AAR_DeviceInfoManager";

	public static Activity mainActivity;

	//获取设备信息
	public static String getDeviceAndPackageInfo(Activity activity) {
		mainActivity = activity;
		String[] packageInfo = DeviceInfoManager.getPackageInfo();
		String info = "," + DeviceInfoManager.getDeviceUdid() + "," + packageInfo[0] + "," +
						packageInfo[1] + ",android," + DeviceInfoManager.getDeviceName() + "," +
						DeviceInfoManager.getDeviceSystemVersion() + "," +
						DeviceInfoManager.getMacAddress();
		return info;
	}

	private static String[] getPackageInfo() {
		String[] packageInfo = new String[2];
		try {
			PackageInfo info = mainActivity.getPackageManager().getPackageInfo(mainActivity.getPackageName(), 0);
			packageInfo[0] = info.packageName;
			packageInfo[1] = info.versionName;
		}
		catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return packageInfo;
	}

	public static String getDeviceName() {
		return Build.MODEL;
	}

	public static String getDeviceSystemVersion() {
		return Build.VERSION.RELEASE;
	}

	private static String getDeviceUdid() {
		String deviceInfoId = getIMEI() + getPuid() + getMacAddress();
		MessageDigest mDigest = null;
		try {
			mDigest = MessageDigest.getInstance("MD5");
		}
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		mDigest.update(deviceInfoId.getBytes(),0,deviceInfoId.length());
		byte p_md5Data[] = mDigest.digest();
		String udid = new String();
		for (int i = 0; i < p_md5Data.length; i++) {
			int b =  (0xFF & p_md5Data[i]);
			if (b <= 0xF)
				udid+="0";
			udid += Integer.toHexString(b);
		}
		return udid;
	}

	public static String getIMEI() {
		TelephonyManager telephonyMgr = (TelephonyManager) mainActivity.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyMgr.getDeviceId();
	}

    public static String getNetworkType() {
        ConnectivityManager connManager = (ConnectivityManager) mainActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null == connManager)
            return "NETWORN_NONE";
        NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
        if (activeNetInfo == null || !activeNetInfo.isAvailable())
            return "NETWORN_NONE";
        NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (null != wifiInfo) {
            NetworkInfo.State state = wifiInfo.getState();
            if (null != state)
                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                    return "NETWORN_WIFI";
                }
        }
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (null != networkInfo) {
            NetworkInfo.State state = networkInfo.getState();
            String strSubTypeName = networkInfo.getSubtypeName();
            if (null != state)
                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                    switch (activeNetInfo.getSubtype()) {
                        case TelephonyManager.NETWORK_TYPE_GPRS:
                        case TelephonyManager.NETWORK_TYPE_CDMA:
                        case TelephonyManager.NETWORK_TYPE_EDGE:
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            return "NETWORN_2G";
                        case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            return "NETWORN_3G";
                        case TelephonyManager.NETWORK_TYPE_LTE:
                            return "NETWORN_4G";
                        default:
                            if (strSubTypeName.equalsIgnoreCase("TD-SCDMA") || strSubTypeName.equalsIgnoreCase("WCDMA") || strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                                return "NETWORN_3G";
                            } else {
                                return "NETWORN_MOBILE";
                            }
                    }
                }
        }
        return "NETWORN_NONE";
    }

    private static String getPuid() {
		String puid = "35" + Build.BOARD.length() % 10 +
							  Build.BRAND.length() % 10 +
							  Build.CPU_ABI.length() % 10 +
							  Build.DEVICE.length() % 10 +
							  Build.DISPLAY.length() % 10 +
							  Build.HOST.length() % 10 +
							  Build.ID.length() % 10 +
							  Build.MANUFACTURER.length() % 10 +
							  Build.MODEL.length() % 10 +
							  Build.PRODUCT.length() % 10 +
							  Build.TAGS.length() % 10 +
							  Build.TYPE.length() % 10 +
							  Build.USER.length() % 10 ;
		return puid;
	}

	public static String getMacAddress() {
		WifiManager wm = (WifiManager)mainActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		return wm.getConnectionInfo().getMacAddress();
	}

	public static String getWifiIP (){
		//获取wifi服务
		WifiManager wifiManager = (WifiManager)mainActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		//判断wifi是否开启
		if (!wifiManager.isWifiEnabled()) {
			return null;
		}
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		String ip = intToIp(ipAddress);
		return ip;
	}
	private static String intToIp(int i) {
		return (i & 0xFF ) + "." +
				((i >> 8 ) & 0xFF) + "." +
				((i >> 16 ) & 0xFF) + "." +
				( i >> 24 & 0xFF) ;
	}

	public static String getLocalIpAddress()
	{
		try
		{
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
			{
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
				{
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress())
					{
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		}
		catch (SocketException ex)
		{
			ex.printStackTrace();
		}
		return null;
	}
}

