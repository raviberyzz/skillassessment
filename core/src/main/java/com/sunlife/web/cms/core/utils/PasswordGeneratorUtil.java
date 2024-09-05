package com.sunlife.web.cms.core.utils;

import java.security.SecureRandom;

public class PasswordGeneratorUtil {
	private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private static final int PASSWORD_LENGTH = 5;
	private static final SecureRandom random = new SecureRandom();

	public static String generatePassword() {
		StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
		for (int i = 0; i < PASSWORD_LENGTH; i++) {
			int index = random.nextInt(CHARACTERS.length());
			password.append(CHARACTERS.charAt(index));
		}
		return password.toString();
	}

}