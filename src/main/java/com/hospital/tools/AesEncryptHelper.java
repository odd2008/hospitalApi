package com.hospital.tools;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.hospital.constant.Constants;
import org.apache.commons.codec.binary.Base64;

import java.util.HashMap;
import java.util.Map;

/**
 * 前后端数据传输加密工具类
 * @author pibigstar
 *
 */
public class AesEncryptHelper {

    //参数分别代表 算法名称/加密模式/数据填充方式
    private static final String ALGORITHMSTR = "AES/ECB/PKCS5Padding";

    /**
     * 加密
     * @param content 加密的字符串
     * @param encryptKey key值
     * @return
     * @throws Exception
     */
    public static String encrypt(String content, String encryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);
        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptKey.getBytes(), "AES"));
        byte[] b = cipher.doFinal(content.getBytes("utf-8"));
        // 采用base64算法进行转码,避免出现中文乱码
        return Base64.encodeBase64String(b);

    }

    /**
     * 解密
     * @param encryptStr 解密的字符串
     * @param decryptKey 解密的key值
     * @return
     * @throws Exception
     */
    public static String decrypt(String encryptStr, String decryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);
        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decryptKey.getBytes(), "AES"));
        // 采用base64算法进行转码,避免出现中文乱码
        byte[] encryptBytes = Base64.decodeBase64(encryptStr);
        byte[] decryptBytes = cipher.doFinal(encryptBytes);
        return new String(decryptBytes);
    }

    public static String encrypt(String content) throws Exception {
        return encrypt(content, Constants.JWT_TOKEN_KEY);
    }
    public static String decrypt(String encryptStr) throws Exception {
        return decrypt(encryptStr, Constants.JWT_TOKEN_KEY);
    }

    public static Map<String, String> getUserFromToken(String authorizationToken) throws Exception {
        String encrypt = JwtTokenHelper.getUsername(authorizationToken);
        String decrypt = decrypt(encrypt,  Constants.JWT_TOKEN_KEY);
        return (Map<String, String>) JSONObject.parse(decrypt);
    }

    public static void main(String[] args) throws Exception {
        Map map=new HashMap<String,String>();
        map.put("telephone","18262997781");
        map.put("password"," ConstantsJWT_TOKEN_KEY");
        String content = JSONObject.toJSONString(map);
        System.out.println("加密前：" + content);

        String encrypt = encrypt(content,  Constants.JWT_TOKEN_KEY);
        System.out.println("加密后：" + encrypt);

        String decrypt = decrypt(encrypt,  Constants.JWT_TOKEN_KEY);
        Map<String, Object> result = (Map<String, Object>) JSONObject.parse(decrypt);
        System.out.println("解密后：" + decrypt);
        System.out.println(result);

    }
}