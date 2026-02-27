package com.forvmom.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.image")
public class ImageUrlConfig {

    private String publicBaseUrl = "/images";
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
            return cdnBaseUrl + "/" + storageFileName;
        }
        return publicBaseUrl + "/" + storageFileName;
    }

    public String buildAdminUrl(Long mediaId) {
        return adminBaseUrl + "/" + mediaId;
    }

    public String buildThumbnailUrl(String storageFileName) {
        if (useCdn && cdnBaseUrl != null) {
            return cdnBaseUrl + "/thumb/" + storageFileName;
        }
        return publicBaseUrl + "/thumb/" + storageFileName;
    }
}