package com.hospital.tools;

import java.util.Random;

public class PassSaltHelper {
    private final static String[] hexDigits = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};

    /*
    密码盐
     */
    public static String GetSalt(int length){
        StringBuffer salt=new StringBuffer();
        for (int i =0 ;i<length;i++){
            Random random=new Random();
            int index=random.nextInt(16);
            salt.append(hexDigits[index]);
        }
        return salt.toString();
    }
}
