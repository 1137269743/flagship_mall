package com.flagship.mall.common;

import com.flagship.mall.exception.FlagshipMallException;
import com.flagship.mall.exception.FlagshipMallExceptionEnum;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;

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

    public interface ProductListOrderBy{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price desc", "price asc");
    }

    public interface ProductSaleStatus {
        int NOT_SALE = 0;
        int SALE = 1;
    }

    public interface CartSelected {
        int UN_CHECKED = 0;
        int CHECKED = 1;
    }

    public enum OrderStatusEnum {
        /**
         * 订单状态代码及说明
         */
        CANCELED(0, "用户已取消"),
        NOT_PAID(10, "未付款"),
        PAID(20, "已付款"),
        DELIVERED(30, "已发货"),
        FINISHED(40, "交易完成");

        private int code;
        private String value;

        public static OrderStatusEnum codeOf(int code) {
            for (OrderStatusEnum orderStatusEnum : values()) {
                if (orderStatusEnum.getCode() == code) {
                    return orderStatusEnum;
                }
            }
            throw new FlagshipMallException(FlagshipMallExceptionEnum.NO_ENUM);
        }

        OrderStatusEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
