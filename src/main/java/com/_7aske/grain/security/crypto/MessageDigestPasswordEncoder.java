package com._7aske.grain.security.crypto;

import com._7aske.grain.component.Grain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Grain
public class MessageDigestPasswordEncoder implements PasswordEncoder {
	private final MessageDigest digest;

	public MessageDigestPasswordEncoder() throws NoSuchAlgorithmException {
		digest = MessageDigest.getInstance("SHA-256");
	}

	@Override
	public String encode(String password) {
		byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
		return new String(Base64.getEncoder().encode(encodedHash));
	}

	@Override
	public boolean matches(String password, String hashed) {
		return hashed.equals(encode(password));
	}
}
