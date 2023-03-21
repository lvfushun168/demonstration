package com.lfs.gateway.component;

import com.lfs.authentication.shiro.api.AuthApi;
import com.lfs.authentication.shiro.dto.ServletDto;
import com.lfs.authentication.shiro.util.UserUtil;
import com.lfs.gateway.adapter.ServletAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Slf4j
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private AuthApi authApi;


    public AuthGlobalFilter(AuthApi authApi) {
        this.authApi = authApi;
    }

    /**
     * auth filter
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String reqPath = exchange.getRequest().getURI().getPath();
        if (reqPath.equals("/authentication/login")||reqPath.equals("/authentication/logout")){
             return chain.filter(exchange.mutate().build());
        }
        String accessToken = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (!authApi.isPermitted(ServletDto.builder()
                        .token(accessToken)
                .build())) {
            log.warn("没有授权的访问，{}", reqPath);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange.mutate().build());
    }

    /**
     * Make the order of this filter at the top
     */
    @Override
    public int getOrder() {
        return 1;
    }
}