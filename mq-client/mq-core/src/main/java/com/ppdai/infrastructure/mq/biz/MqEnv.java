package com.ppdai.infrastructure.mq.biz;

import com.ppdai.infrastructure.mq.biz.common.util.Util;

public enum MqEnv {
	LOCAL, DEV, FWS, FAT, UAT, PRE, LPT, PRO, TOOLS;

	public static MqEnv fromString(String envName) {
		if (Util.isEmpty(envName)) {
			return null;
		}
		switch (envName.trim().toUpperCase()) {
		case "LPT":
			return MqEnv.LPT;
		case "PRE":
			return MqEnv.PRE;
		case "FAT":
		case "FWS":
			return MqEnv.FAT;
		case "UAT":
			return MqEnv.UAT;
		case "PRO":
		case "PROD": // just in case
			return MqEnv.PRO;
		case "DEV":
			return MqEnv.DEV;
		case "LOCAL":
			return MqEnv.LOCAL;
		default:
			return null;
		}
	}
}
