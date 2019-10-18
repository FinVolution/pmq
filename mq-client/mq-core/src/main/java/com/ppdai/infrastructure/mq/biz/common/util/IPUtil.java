package com.ppdai.infrastructure.mq.biz.common.util;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IPUtil {
	private static final String NETWORK_CARD = "eth0";
	private static final String NETWORK_CARD_BAND = "bond0";
	private static String netWorkCard = "";

	public static String getLocalHostName() {
		try {
			InetAddress addr = InetAddress.getLocalHost();
			return addr.getHostName();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	private static String getLinuxLocalIP() {
		String ip = "";
		try {
			Enumeration<NetworkInterface> e1 = (Enumeration<NetworkInterface>) NetworkInterface.getNetworkInterfaces();
			while (e1.hasMoreElements()) {
				NetworkInterface ni = e1.nextElement();
				if (netWorkCard.equals(ni.getName()) || NETWORK_CARD.equals(ni.getName())
						|| NETWORK_CARD_BAND.equals(ni.getName())) {
					Enumeration<InetAddress> e2 = ni.getInetAddresses();
					while (e2.hasMoreElements()) {
						InetAddress ia = e2.nextElement();
						if (ia instanceof Inet6Address) {
							continue;
						}
						ip = ia.getHostAddress();
					}
					break;
				} else {
					continue;
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return ip;
	}

	@SuppressWarnings("finally")
	private static String getWinLocalIP() {
		String ip = null;
		try {
			ip = InetAddress.getLocalHost().getHostAddress().toString();
		} finally {
			return ip;
		}
	}

	private static volatile String ip1 = ""; 
	static {
		if (System.getProperty("os.name").contains("Win")) {
			ip1 = getWinLocalIP();
		} else if (System.getProperty("os.name").contains("Mac OS")) {
			netWorkCard = "en0";
			ip1 = getLinuxLocalIP();
		} else {
			ip1 = getLinuxLocalIP();
		}
	}
	public static String getLocalIP() {		
		return ip1;
	}
	

	private static Map<String, String> ipCache = new ConcurrentHashMap<>();
	private static Object objLock1 = new Object();

	public static String getLocalIP(String netWorkName) {		
		if (Util.isEmpty(netWorkName)) {
			return getLocalIP();
		}
		if (!ipCache.containsKey(netWorkCard)||Util.isEmpty(ipCache.get(netWorkName))) {
			synchronized (objLock1) {
				if (!ipCache.containsKey(netWorkCard)||Util.isEmpty(ipCache.get(netWorkName))) {
					String ip = null;
					if (System.getProperty("os.name").contains("Win")) {
						ip = getWinLocalIP();
					} else if (System.getProperty("os.name").contains("Mac OS")) {
						if (Util.isEmpty(netWorkCard)) {
							netWorkCard = "en0";
						}
						ip = getLinuxLocalIP();
					} else {
						ip = getLinuxLocalIP();
					}
					ipCache.put(netWorkName, ip);
				}
			}
		}

		if (!ipCache.containsKey(netWorkCard)||Util.isEmpty(ipCache.get(netWorkName))) {
			throw new RuntimeException("ip获取异常,请指定网卡名称！");
		}
		return ipCache.get(netWorkName);
	}
}
