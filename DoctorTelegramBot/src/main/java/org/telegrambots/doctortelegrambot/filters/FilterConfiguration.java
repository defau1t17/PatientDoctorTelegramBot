package org.telegrambots.doctortelegrambot.filters;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfiguration {

    @Bean
    public FilterRegistrationBean<RequestFilter> initRequestFiler() {
        FilterRegistrationBean<RequestFilter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(new RequestFilter());
        filterFilterRegistrationBean.addUrlPatterns("/api/v1/doctor", "/api/v1/patient");
        return filterFilterRegistrationBean;
    }

}
