package com.sunlife.web.cms.core.utils;

import java.security.SecureRandom;

/**
 * @author Sunlife
 */
public class UniqueCodeUtil {

	private static final int CODE_LENGTH = 6;

	public static String generateUniqueCode() {
		
		SecureRandom random = new SecureRandom();
		StringBuilder otp = new StringBuilder(CODE_LENGTH);
		for (int i = 0; i < CODE_LENGTH; i++) {
			otp.append(random.nextInt(10));
		}
		return otp.toString();
	}
}
