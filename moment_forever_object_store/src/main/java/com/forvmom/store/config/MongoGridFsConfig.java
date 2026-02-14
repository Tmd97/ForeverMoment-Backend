package com.forvmom.store.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

@Configuration
@ConditionalOnProperty(name = "object.store.provider", havingValue = "gridfs", matchIfMissing = true)
public class MongoGridFsConfig {

    @Bean
    public GridFsTemplate gridFsTemplate(MongoDatabaseFactory dbFactory,
                                         MongoConverter converter,
                                         ObjectStoreProperties properties) {
        // Get bucket name from properties (default: "fs")
        String bucket = properties.getGridfsBucket();

        // Create GridFsTemplate with bucket name
        return new GridFsTemplate(dbFactory, converter, bucket);
    }
}