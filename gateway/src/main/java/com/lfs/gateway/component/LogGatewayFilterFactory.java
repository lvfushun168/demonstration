package com.lfs.gateway.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import reactor.core.publisher.Mono;

@Slf4j
public class LogGatewayFilterFactory extends AbstractGatewayFilterFactory<LogGatewayFilterFactory.Config> {

    private static final String REQUEST_START_TIME = "request_start_time";


    public LogGatewayFilterFactory() {
        // 这里需要将自定义的config传过去，否则会报告ClassCastException
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            exchange.getAttributes().put(REQUEST_START_TIME, System.currentTimeMillis());
            return chain.filter(exchange).then(
                    Mono.fromRunnable(() -> {
                        Long startTime = exchange.getAttribute(REQUEST_START_TIME);
                        if (startTime != null) {
                            log.info("request url：{},cost time：{}ms", exchange.getRequest().getURI(), System.currentTimeMillis() - startTime);
                        }
                    })
            );
        };
    }

    public static class Config {
    }
}
