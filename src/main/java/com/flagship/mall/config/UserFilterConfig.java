package com.flagship.mall.config;

import com.flagship.mall.filter.AdminFilter;
import com.flagship.mall.filter.UserFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

/**
 * @Author Flagship
 * @Date 2021/3/25 15:47
 * @Description Admin过滤器的配置
 */
@Configuration
public class UserFilterConfig {
    @Bean
    public UserFilter userFilter() {
        return new UserFilter();
    }

    @Bean(name = "userFilterConf")
    public FilterRegistrationBean adminFilterConfig() {
        FilterRegistrationBean<Filter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(userFilter());
        filterFilterRegistrationBean.addUrlPatterns("/cart/*");
        filterFilterRegistrationBean.addUrlPatterns("/carts");
        filterFilterRegistrationBean.addUrlPatterns("/order/*");
        filterFilterRegistrationBean.setName("userFilterConfig");
        return filterFilterRegistrationBean;
    }
}
