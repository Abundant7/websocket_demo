package com.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;


import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

/**
 * WQ
 * 2022/2/25 15:50
 * DES/CBC/PKCS5Padding加密解密用
 */
@Slf4j
public class DesCBCUtil {

    //算法名称
    public static final String ALGORITHM_DES = "DES/CBC/PKCS5Padding";
    //密码
    public static final String KEY = "D3eU9n7t";
    //偏移量
    public static final String IV = "epsoftgz";


    /**
     * WQ
     * 2022/2/25 15:49
     * DES/CBC/PKCS5Padding 加密
     */
    public static String encode(String data) {
        try {
            return encode(KEY, data);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("数据加密出错");
            return null;
        }
    }

    /**
     * WQ
     * 2022/2/25 15:49
     * DES/CBC/PKCS5Padding 解密
     */
    public static String decode(String data) {
        byte[] datas;
        String value;
        try {
            datas = decode(KEY, Base64.decodeBase64(data.getBytes()));
            value = new String(datas);
        } catch (Exception e) {
            value = "";
        }
        return value;
    }

    /**
     * DES算法，加密
     *
     * @param data 待加密字符串
     * @param key  加密私钥，长度不能够小于8位
     * @return 加密后的字节数组，一般结合Base64编码使用
     */
    public static String encode(String key, String data) throws Exception {
        return encode(key, data.getBytes());
    }

    /**
     * 获取编码后的值
     *
     * @param key
     * @param data
     * @return
     * @throws Exception
     */
    public static String decode(String key, String data) {
        byte[] datas;
        String value;
        try {
            datas = decode(key, Base64.decodeBase64(data.getBytes()));
            value = new String(datas);
        } catch (Exception e) {
            value = "";
        }
        return value;
    }

    /**
     * DES算法，加密
     *
     * @param data 待加密字符串
     * @param key  加密私钥，长度不能够小于8位
     * @return 加密后的字节数组，一般结合Base64编码使用
     */
    private static String encode(String key, byte[] data) throws Exception {
        try {
            DESKeySpec dks = new DESKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            //key的长度不能够小于8位字节
            Key secretKey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
            IvParameterSpec iv = new IvParameterSpec(IV.getBytes());
            AlgorithmParameterSpec paramSpec = iv;
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, paramSpec);
            byte[] bytes = cipher.doFinal(data);
            return new String(Base64.encodeBase64(bytes));
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    /**
     * DES算法，解密
     *
     * @param data 待解密字符串
     * @param key  解密私钥，长度不能够小于8位
     * @return 解密后的字节数组
     * @throws Exception 异常
     */
    private static byte[] decode(String key, byte[] data) throws Exception {
        try {
            SecureRandom sr = new SecureRandom();
            DESKeySpec dks = new DESKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            //key的长度不能够小于8位字节
            Key secretKey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
            IvParameterSpec iv = new IvParameterSpec(IV.getBytes());
            AlgorithmParameterSpec paramSpec = iv;
            cipher.init(Cipher.DECRYPT_MODE, secretKey, paramSpec);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public static void main(String[] args) throws Exception {
        String clearText = "111";
        System.out.println("密钥：" + KEY);
        System.out.println("偏移量：" + IV);
        String encryptText = encode(clearText);
        System.out.println("加密后：" + encryptText);
        String decodeText = decode(encryptText);
        System.out.println("解密后：" + decodeText);
    }

}