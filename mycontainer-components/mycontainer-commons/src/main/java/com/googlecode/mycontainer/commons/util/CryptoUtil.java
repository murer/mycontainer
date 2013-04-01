package com.googlecode.mycontainer.commons.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

public class CryptoUtil {

	public static String getAuthBasic(String user, String pass) {
		byte[] bytes = new StringBuilder().append(user).append(':')
				.append(pass).toString().getBytes();
		String s = "Basic " + encodeBase64(bytes);
		return s;
	}

	public static String encodeBase64(byte[] bytes) {
		return new Base64(-1).encodeToString(bytes);
	}

	public static String[] decodeBasicAuth(String authorization) {
		String[] split = authorization.split(" ");
		byte[] decoded = decodeBase64(split[1]);
		String str = new String(decoded);
		String[] ret = str.split(":");
		return ret;
	}

	public static byte[] decodeBase64(String str) {
		return Base64.decodeBase64(str);
	}

	public static byte[] des3Encrypt(byte[] key, byte[] data) {
		try {
			SecretKey k = new SecretKeySpec(key, "DESede");
			Cipher c = Cipher.getInstance("DESede");
			c.init(Cipher.ENCRYPT_MODE, k);
			byte[] ret = c.doFinal(data);
			return ret;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (NoSuchPaddingException e) {
			throw new RuntimeException(e);
		} catch (InvalidKeyException e) {
			throw new RuntimeException(e);
		} catch (IllegalBlockSizeException e) {
			throw new RuntimeException(e);
		} catch (BadPaddingException e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] des3Decrypt(byte[] key, byte[] data) {
		try {
			SecretKey k = new SecretKeySpec(key, "DESede");
			Cipher c = Cipher.getInstance("DESede");
			c.init(Cipher.DECRYPT_MODE, k);
			byte[] ret = c.doFinal(data);
			return ret;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (NoSuchPaddingException e) {
			throw new RuntimeException(e);
		} catch (InvalidKeyException e) {
			throw new RuntimeException(e);
		} catch (IllegalBlockSizeException e) {
			throw new RuntimeException(e);
		} catch (BadPaddingException e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) throws Exception {
		System.out.println(Arrays.toString(generate3DesKey()));
	}

	public static String encodeBase64URLSafe(byte[] bytes) {
		return new Base64(-1, null, true).encodeToString(bytes);
	}

	public static byte[] generate3DesKey() {
		try {
			KeyGenerator keyGen = KeyGenerator.getInstance("DESede");
			SecretKey key = keyGen.generateKey();
			byte[] bytes = key.getEncoded();
			return bytes;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public static String hash(String str) {
		return DigestUtils.sha512Hex(str);
	}

}
