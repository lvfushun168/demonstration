package com.lfs.openai.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;


@Configuration
public class MongodbConfig {
//
//    @Value("${spring.data.mongodb.host}")
//    private String mongoHost;
//
//    @Value("${spring.data.mongodb.port}")
//    private int mongoPort;
//
//    @Value("${spring.data.mongodb.database}")
//    private String mongoDB;

    @Bean
    public MongoClient mongo() {
        return MongoClients.create(String.format("mongodb://%s:%d/%s", "120.24.95.4", 27017, "lfs"));
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongo(), "lfs");
    }

//
//    @Bean
//    public MongoClientFactoryBean mongoClient() {
//        MongoClientFactoryBean factoryBean = new MongoClientFactoryBean();
//        factoryBean.setHost("120.24.95.4");
//        factoryBean.setPort(27017);
//        return factoryBean;
//    }
//
//    @Bean
//    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
//        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, "lfs");
//        return mongoTemplate;
//    }

}
