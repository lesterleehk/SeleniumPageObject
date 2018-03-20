package com.java.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/*
 * This class implements an alternative scheme for encrypting/decrypting passwords as used by Talent Suite in its native
 * Database Authentication Adapter. This particular implementation uses AES and a hardcoded SECRET_KEY_SEED. If making
 * substantial changes to this component, keep in mind that the following two methods must be available as "public":
 * 
 * - encrypt(plainText) - comparePasswords(plainText, EncryptedText)
 * 
 * The "decrypt" method is NOT used internally by NTS since we never retrieve or examine original passwords, but it is
 * provided here for completeness.
 * 
 * Note that however things are changed here, the "password" column in the "users" table must support the encrypted
 * result string/size. In NTS 8.3 and earlier it is only 10 characters and so may need modification. NTS 9.0 is 50
 * characters, and in NTS 9.1 and later it has been changed to 255.
 */
public final class AES_Cipher {

	private static final String DEFAULT_ENCODING = "UTF-8";

	private static SecretKey secretKey = null;

	private static byte[] SECRET_KEY_SEED = {
	                                         (byte) 0x6e, (byte) 0x25, (byte) 0x72, (byte) 0x15, (byte) 0x41, (byte) 0x37, (byte) 0x51, (byte) 0x49,
	                                         (byte) 0x2b, (byte) 0xa4, (byte) 0x1f, (byte) 0x44, (byte) 0xc7 };

	// Suppress default constructor for noninstantiability
	private AES_Cipher() {

		throw new AssertionError();
	}

	/*
	 * Decrypt is an AES-based decryption method for retrieving the plaintext version of a previously encrypted
	 * password.
	 */
	public static String decrypt(final String cipherText) {

		try {
			return decryptUsingAes(cipherText, defaultKey(), DEFAULT_ENCODING);
		}
		catch (Exception one) {
			throw new RuntimeException(one);
		}
	}

	private static String decryptUsingAes(final String cipherText, final SecretKey key, final String encoding) throws Exception {

		final SecretKeySpec spec = new SecretKeySpec(key.getEncoded(), "AES");
		final Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, spec);
		return new String(cipher.doFinal(hex2byte(cipherText)), encoding);
	}

	private static SecretKey generateAesKey(final byte[] seed) throws NoSuchAlgorithmException {

		final KeyGenerator generator = KeyGenerator.getInstance("AES");
		final SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		random.setSeed(seed);
		generator.init(128, random);
		return generator.generateKey();
	}

	private static SecretKey defaultKey() throws NoSuchAlgorithmException {

		if (secretKey == null)
			secretKey = generateAesKey(SECRET_KEY_SEED);
		return secretKey;
	}

	// ------------------------------------------------------------------------
	// UTILITY METHODS
	//
	// These utility methods are included so that they do not need to reference
	// internal obfuscated libraries
	// ------------------------------------------------------------------------
	/**
	 * Converts a byte array to hex string
	 * 
	 * @param block
	 *            the input byte array to be converted
	 * 
	 * @return a hex string converted from the input byte array
	 */
	private static final String toHexString(final byte[] block) {

		final StringBuilder buf = new StringBuilder();

		for (int i = 0; i < block.length; i++)
			byte2hex(block[i], buf);
		return buf.toString();
	}

	/**
	 * Converts a byte to hex digit and writes to the supplied buffer
	 * 
	 * @param b
	 *            the input byte
	 * @param buf
	 *            the output string buffer
	 */
	private static void byte2hex(byte b, StringBuilder buf) {

		char[] hexChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		int high = ((b & 0xf0) >> 4);
		int low = (b & 0x0f);

		buf.append(hexChars[high]);
		buf.append(hexChars[low]);
	}

	/**
	 * Converts a hex String to a byte array.
	 * 
	 * @param input
	 *            the hex String.
	 * @return the converted byte array.
	 */
	private static final byte[] hex2byte(final String input) {

		final char[] hexChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		final byte[] result = new byte[input.length() / 2];
		int x = 0;
		int high = 0, low = 0;

		/*
		 * don't work, throw exception for (int i=0; i < input.length(); i++) { String s = "0x" + input.substring(i,
		 * i+2); result[x++] = Byte.parseByte(s,16); }
		 */
		for (int i = 0; i < input.length(); i++) {
			for (int j = 0; j < 16; j++) {
				if (input.charAt(i) == hexChars[j]) {
					// high order bits
					high = j * 16;
					i++;
					for (int k = 0; k < 16; k++) {
						if (input.charAt(i) == hexChars[k]) {
							// low order bits
							low = k;
							break;
						}
					}
					break;
				}
			}
			result[x++] = (byte) (high + low);
		}
		return result;
	}

}
