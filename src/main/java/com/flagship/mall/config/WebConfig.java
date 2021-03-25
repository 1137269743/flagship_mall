package com.flagship.mall.config;

import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.FormContentFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * @Author Flagship
 * @Date 2021/3/25 17:52
 * @Description
 */
public class WebConfig extends WebMvcConfigurationSupport {
        @Bean
        public FormContentFilter formContentFilter() {
            return new FormContentFilter();
        }
}
