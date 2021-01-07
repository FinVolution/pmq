package com.ppdai.infrastructure.mq.biz.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class IPUtil {
	private static Logger log = LoggerFactory.getLogger(IPUtil.class);
	private static String m_local;

	private static InetAddress findValidateIp(List<InetAddress> addresses) {
		InetAddress local = null;
		for (InetAddress address : addresses) {
			if (address instanceof Inet4Address) {
				if (address.isLoopbackAddress() || address.isSiteLocalAddress()) {
					if (local == null) {
						local = address;
					} else if (address.isSiteLocalAddress() && !address.isLoopbackAddress()) {
						// site local address has higher priority than other address
						local = address;
					} else if (local.isSiteLocalAddress() && address.isSiteLocalAddress()) {
						// site local address with a host name has higher
						// priority than one without host name
						if (local.getHostName().equals(local.getHostAddress()) && !address.getHostName()
								.equals(address.getHostAddress())) {
							local = address;
						}
					}
				} else {
					if (local == null) {
						local = address;
					}
				}
			}
		}
		return local;
	}

	public static String getLocalIP() {
		if (Util.isEmpty(m_local)) {
			try {
				List<NetworkInterface> nis = Collections.list(NetworkInterface.getNetworkInterfaces());
				List<InetAddress> addresses = new ArrayList<>();
				InetAddress local = null;
				try {
					for (NetworkInterface ni : nis) {
						if (ni.isUp()) {
							addresses.addAll(Collections.list(ni.getInetAddresses()));
						}
					}
					local = findValidateIp(addresses);
				} catch (Exception e) {
					// ignore
				}
				m_local = local.getHostAddress().toString();
				;
			} catch (Throwable e) {
				log.error("", e);
			}
		}
		return m_local;
	}

	private static Map<String, String> ipCache = new ConcurrentHashMap<>();

	public static String getLocalIP(String netWorkCard) {
		if (Util.isEmpty(netWorkCard)) {
			return getLocalIP();
		}
		if (!ipCache.containsKey(netWorkCard)) {
			try {
				Enumeration<NetworkInterface> e1 = (Enumeration<NetworkInterface>) NetworkInterface.getNetworkInterfaces();
				while (e1.hasMoreElements()) {
					NetworkInterface ni = e1.nextElement();
					if (netWorkCard.equals(ni.getName())) {
						Enumeration<InetAddress> e2 = ni.getInetAddresses();
						while (e2.hasMoreElements()) {
							InetAddress ia = e2.nextElement();
							if (ia instanceof Inet6Address) {
								continue;
							}
							ipCache.put(netWorkCard, ia.getHostAddress());
						}
						break;
					} else {
						continue;
					}
				}
			} catch (Throwable e) {
				log.error("", e);
			}
		}
		return ipCache.get(netWorkCard);
	}
}
