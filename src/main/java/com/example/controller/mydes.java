package com.example.controller;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

public class mydes {

    /**
     * 使用指定的key对字符串加密
     *
     * @param data 待加密字符串
     * @param key 密钥
     * @return 加密后的数据
     */
    public static String encrypt(String data, String key) {
        byte[] bt = new byte[0];
        String encodeData = "";
        try {
            bt = encrypt(data.getBytes(CHARSET_UTF8), key.getBytes(CHARSET_UTF8));
            //字节转化为16进制字符串
            encodeData = byteToHexString(bt);
        } catch (Exception e) {
            //logger.error("异常",e);
        }
        return encodeData;
    }
    private static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        SecureRandom sr = new SecureRandom();

        DESKeySpec dks = new DESKeySpec(key);

        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);

        Cipher cipher = Cipher.getInstance(PKCS_5_PADDING);
        String IV = "epsoftgz";
        IvParameterSpec iv = new IvParameterSpec(IV.getBytes());
        AlgorithmParameterSpec paramSpec = iv;
        cipher.init(Cipher.ENCRYPT_MODE, securekey, paramSpec);
        //cipher.init(Cipher.ENCRYPT_MODE, securekey, new IvParameterSpec(dks.getKey()));
        return cipher.doFinal(data);
    }
    /**
     *
     * 将byte数组转换成16进制字符串
     *
     * @param bytes byte数组
     * @return 16进制字符串
     */
    public static String byteToHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer(bytes.length);
        String sTemp;
        for (int i = 0; i < bytes.length; i++) {
            sTemp = Integer.toHexString(0xFF & bytes[i]);
            if (sTemp.length() < 2) {
                sb.append(0);
            }
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }


    /**
     *
     * @param data
     * @param key
     * @return
     */
    public static String decrypt(String data, String key) {
        String decryptString = "";
        try {
            byte[] bt = decrypt(hexStringToBytes(data), key.getBytes(CHARSET_UTF8));
            decryptString = new String(bt, CHARSET_UTF8);
        } catch (Exception e) {
            //logger.error("异常",e);
        }
        return decryptString;
    }
    /**
     * 解密
     * @param data byte数组
     * @param key 加密key
     * @return 解密后的byte数组
     * @throws Exception 异常
     */
    private static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        SecureRandom sr = new SecureRandom();
        DESKeySpec dks = new DESKeySpec(key);

        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);

        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance(PKCS_5_PADDING);
        // 用密钥初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, new IvParameterSpec(dks.getKey()));

        return cipher.doFinal(data);
    }
    /**
     * 将16进制字符串转换为byte[]
     *
     * @param str 待转字符串
     * @return 转后的byte数组
     */
    public static byte[] hexStringToBytes(String str) {
        if (str == null || str.trim().equals("")) {
            return new byte[0];
        }
        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }
        return bytes;
    }



    /**
     * 加密方式 DES
     */
    private final static String DES = "DES";

    /**
     * 填充模式 DES/CBC/PKCS5Padding
     */
    private final static String PKCS_5_PADDING = "DES/CBC/PKCS5Padding";

    /**
     * 字符编码 UTF-8
     */
    static final String CHARSET_UTF8 = "UTF-8";

    public static void main(String[] args) {
        String data = "111";
        String key = "D3eU9n7t";


        String desString = encrypt(data,key);
        System.out.println("DES:" + desString);

        String decodeString = decrypt(desString,key);
        System.out.println(decodeString);

    }


}
