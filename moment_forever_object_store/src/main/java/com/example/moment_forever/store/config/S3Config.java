package com.example.moment_forever.store.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
@ConditionalOnProperty(name = "object.store.provider", havingValue = "s3")
public class S3Config {

    @Bean
    public S3Client s3Client(ObjectStoreProperties properties) {
        var builder = S3Client.builder()
                .region(Region.of(properties.getS3Region()));

        // If access key and secret key are provided
        if (properties.getS3AccessKey() != null && properties.getS3SecretKey() != null) {
            builder.credentialsProvider(
                    StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(
                                    properties.getS3AccessKey(),
                                    properties.getS3SecretKey()
                            )
                    )
            );
        }

        // If custom endpoint (for LocalStack or MinIO)
//        if (properties.getS3Endpoint() != null) {
//            builder.endpointOverride(URI.create(properties.getS3Endpoint()));
//        }

        return builder.build();
    }

    @Bean
    public S3Presigner s3Presigner(ObjectStoreProperties properties) {
        var builder = S3Presigner.builder()
                .region(Region.of(properties.getS3Region()));

        if (properties.getS3AccessKey() != null && properties.getS3SecretKey() != null) {
            builder.credentialsProvider(
                    StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(
                                    properties.getS3AccessKey(),
                                    properties.getS3SecretKey()
                            )
                    )
            );
        }

//        if (properties.getS3Endpoint() != null) {
//            builder.endpointOverride(URI.create(properties.getS3Endpoint()));
//        }

        return builder.build();
    }
}