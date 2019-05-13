package com.hospital.tools;

import com.hospital.constant.Constants;

import java.util.Random;

public class CommonHelper {

    public static String GetVerifyCode(int length){
        Random random=new Random();
        String codeStr="";
        for (int i=0;i<length;i++){
            int codeNum=random.nextInt(10);
            codeStr+=codeNum;
        }
        return codeStr;
    }

    public static String formatImageUrl(String imagePath) {
        return Constants.IMAGE_PUBLIC_PATH + imagePath;
    }
}
