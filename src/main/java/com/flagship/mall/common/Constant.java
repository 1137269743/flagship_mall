package com.flagship.mall.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Author Flagship
 * @Date 2021/3/24 23:12
 * @Description 常量值
 */
@Component
public class Constant {
    public static final String SALT = "451@DSDsda*&(&89[m";
    public static final String FLAGSHIP_MALL_USER = "flagship_mall_user";
    public static String FILE_UPLOAD_DIR;

    @Value("${file.upload.dir}")
    public void setFileUploadDir(String fileUploadDir) {
        FILE_UPLOAD_DIR = fileUploadDir;
    }
}
