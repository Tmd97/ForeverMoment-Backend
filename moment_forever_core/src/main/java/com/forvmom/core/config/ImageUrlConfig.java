package com.forvmom.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.image")
public class ImageUrlConfig {

    //TODO: think how it can be improved, multiple instances? port dynamic?
    //sometime docker service, some time localhost, maybe some env variable?
    private String baseUrl = "http://localhost:8081/api/platform";
    private String publicBaseUrl = "/public/images";
    private String adminBaseUrl = "/admin/images";
    private String cdnBaseUrl;
    private int cacheMaxAge = 31536000; // 1 year in seconds
    private boolean useCdn = false;

    // Getters and Setters
    public String getPublicBaseUrl() {
        return publicBaseUrl;
    }

    public void setPublicBaseUrl(String publicBaseUrl) {
        this.publicBaseUrl = publicBaseUrl;
    }

    public String getAdminBaseUrl() {
        return adminBaseUrl;
    }

    public void setAdminBaseUrl(String adminBaseUrl) {
        this.adminBaseUrl = adminBaseUrl;
    }

    public String getCdnBaseUrl() {
        return cdnBaseUrl;
    }

    public void setCdnBaseUrl(String cdnBaseUrl) {
        this.cdnBaseUrl = cdnBaseUrl;
    }

    public int getCacheMaxAge() {
        return cacheMaxAge;
    }

    public void setCacheMaxAge(int cacheMaxAge) {
        this.cacheMaxAge = cacheMaxAge;
    }

    public boolean isUseCdn() {
        return useCdn;
    }

    public void setUseCdn(boolean useCdn) {
        this.useCdn = useCdn;
    }

    // Helper methods to build URLs
    public String buildPublicUrl(String storageFileName) {
        if (useCdn && cdnBaseUrl != null) {
            return cdnBaseUrl + "/fetch/" + storageFileName;
        }
        return publicBaseUrl + "/fetch/" + storageFileName;
    }

    public String buildAdminUrl(Long mediaId) {
        return adminBaseUrl + "/" + mediaId;
    }

    public String buildThumbnailUrl(String storageFileName) {
        if (useCdn && cdnBaseUrl != null) {
            return cdnBaseUrl + "/fetch/" + storageFileName;
        }
        return publicBaseUrl + "/fetch/" + storageFileName;
    }
}