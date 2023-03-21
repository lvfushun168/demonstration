package com.lfs.authentication.shiro.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EnableTransactionManagement
@EnableReactiveMongoRepositories
public class MongodbConfig  {
    @Value("${spring.data.mongodb.host}")
    private String mongoHost;

    @Value("${spring.data.mongodb.port}")
    private int mongoPort;

    @Value("${spring.data.mongodb.database}")
    private String mongoDB;

    @Bean
    public MongoClient mongo() {
        return MongoClients.create(String.format("mongodb://%s:%d/%s", mongoHost, mongoPort, mongoDB));
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongo(), mongoDB);
    }


}