package com.example.bankingservice.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * 비밀번호 암호화 위한 유틸클래스 SHA-256 + Salt 알고리즘 사용
 */
public class EncryptUtil {

    private static final int SALT_STRING_LENGTH = 20;

    private static String getSalt() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[SALT_STRING_LENGTH];

        secureRandom.nextBytes(salt);

        StringBuffer stringBuffer = new StringBuffer();
        for (byte b : salt) {
            stringBuffer.append(String.format("%02x", b));
        }

        return stringBuffer.toString();
    }

    public static String getEncrypt(String pwd) {
        String result = "";
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update((pwd + getSalt()).getBytes());
            byte[] pwdSalt = messageDigest.digest();

            StringBuffer stringBuffer = new StringBuffer();
            for (byte b : pwdSalt) {
                stringBuffer.append(String.format("%02x", b));
            }

            result = stringBuffer.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return result;
    }

}
