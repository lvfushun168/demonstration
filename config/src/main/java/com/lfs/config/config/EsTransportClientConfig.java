package com.lfs.config.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.ElasticsearchConfigurationSupport;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

import javax.annotation.PostConstruct;
import java.time.Duration;

/**
 * ES配置
 * （transportClient在7中被取消，并在8中被删除）
 */
@Configuration
public class EsTransportClientConfig extends ElasticsearchConfigurationSupport {
//
//    @Bean
//    public Client elasticsearchClient() throws UnknownHostException {
//        Settings settings = Settings.builder().put("cluster.name", "elasticsearch_lvfushun").build();
//        TransportClient client = new PreBuiltTransportClient(settings);
//        // 注意端口9300
//        client.addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"), 9200));
//        return client;
//    }
//
//    @Bean(name = {"elasticsearchOperations", "elasticsearchTemplate"})
//    public ElasticsearchTemplate elasticsearchTemplate() throws UnknownHostException {
//        return new ElasticsearchTemplate(elasticsearchClient(), entityMapper());
//    }

    //--------新版的使用以下配置
    //TODO 禁用netty后启动速度过慢

    @Bean
    RestHighLevelClient elasticsearchClient() {
        ClientConfiguration configuration = ClientConfiguration.builder()
                .connectedTo("127.0.0.1:9200")
                .withConnectTimeout(Duration.ofSeconds(100))
                .withSocketTimeout(Duration.ofSeconds(100))
                //.useSsl()
                //.withDefaultHeaders(defaultHeaders)
                //.withBasicAuth(username, password)
                // ... other options

                .build();
        RestHighLevelClient client = RestClients.create(configuration).rest();
        return client;
    }

    @Bean
    public ElasticsearchRestTemplate elasticsearchRestTemplate() {
        return new ElasticsearchRestTemplate(elasticsearchClient());
    }






    @PostConstruct
    void init(){
        // 解决redis与es共用netty连接导致的连接冲突报错
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }


}
