package com.forvmom.store.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "object.store")
public class ObjectStoreProperties {

    private String provider = "gridfs";
    private String gridfsBucket = "fs";
    private String s3Region;
    private String s3Bucket;
    private String s3AccessKey;
    private String s3SecretKey;

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getGridfsBucket() { return gridfsBucket; }
    public void setGridfsBucket(String gridfsBucket) { this.gridfsBucket = gridfsBucket; }

    public String getS3Region() { return s3Region; }
    public void setS3Region(String s3Region) { this.s3Region = s3Region; }

    public String getS3Bucket() { return s3Bucket; }
    public void setS3Bucket(String s3Bucket) { this.s3Bucket = s3Bucket; }

    public String getS3AccessKey() { return s3AccessKey; }
    public void setS3AccessKey(String s3AccessKey) { this.s3AccessKey = s3AccessKey; }

    public String getS3SecretKey() { return s3SecretKey; }
    public void setS3SecretKey(String s3SecretKey) { this.s3SecretKey = s3SecretKey; }
}