package com.flagship.mall.util;

import com.flagship.mall.common.Constant;
import org.apache.tomcat.util.codec.binary.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Author Flagship
 * @Date 2021/3/24 23:05
 * @Description MD5工具
 */
public class Md5Utils {
    public static String getMd5Str(String strValue) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        return Base64.encodeBase64String(md5.digest((strValue + Constant.SALT).getBytes()));
    }

    public static void main(String[] args) {
        String md5Str = null;
        try {
            md5Str = getMd5Str("1234");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        System.out.println(md5Str);
    }
}
