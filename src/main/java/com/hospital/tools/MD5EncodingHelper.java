package com.hospital.tools;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5EncodingHelper {
    private final static String[] hexDigits = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};

    public static String EncodeByMD5(String originString){
        if (originString!=null){
            try{
                //创建具有指定算法名称的信息摘要
                MessageDigest md5=MessageDigest.getInstance("MD5");
                //使用指定的字节数组对摘要进行更新
                byte[] results=md5.digest(originString.getBytes());
                //将得到的字节数组变成字符串返回
                String result=byteArrayToHexString(results);
                return result;
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static String byteArrayToHexString(byte[] b){
        StringBuffer resultSb=new StringBuffer();
        for (int i=0;i<b.length;i++){
            resultSb.append(byteToHexString(b[i]));
        }
        return resultSb.toString();
    }

    private static String byteToHexString(byte b){
        int n=b;
        if (n<0){
            n=256+n;
        }
        int d1=n/16;
        int d2=n%16;
        return hexDigits[d1]+hexDigits[d2];
    }
}
