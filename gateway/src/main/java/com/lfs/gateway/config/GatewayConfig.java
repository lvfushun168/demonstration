package com.lfs.gateway.config;

import com.lfs.authentication.shiro.api.AuthApi;
import com.lfs.gateway.component.AuthGlobalFilter;
import com.lfs.gateway.component.LogGatewayFilterFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;

import java.util.stream.Collectors;

@Configuration
public class GatewayConfig {


    @Bean
    public LogGatewayFilterFactory logGatewayFilterFactory() {
        return new LogGatewayFilterFactory();
    }

    @Bean
    public AuthGlobalFilter authGlobalFilter(AuthApi authApi) {
        return new AuthGlobalFilter(authApi);
    }

    @Bean
    @ConditionalOnMissingBean
    public HttpMessageConverters messageConverters(ObjectProvider<HttpMessageConverter<?>> converters) {
        return new HttpMessageConverters(converters.orderedStream().collect(Collectors.toList()));
    }
}
