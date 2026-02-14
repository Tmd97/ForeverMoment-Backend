package com.forvmom.store.dto;

import java.util.Date;
import java.util.Map;

/**
 * Data transfer object containing metadata information about a stored object.
 * Does NOT contain the actual file content.
 * Used for list operations and metadata-only views.
 */
public class ObjectMetadata {

    /**
     * Unique identifier of the object in storage system.
     * For GridFS: the ObjectId as string
     * For S3: the object key
     */
    private String id;

    /**
     * Original filename as provided during store operation.
     * Used for display purposes and content disposition.
     */
    private String fileName;

    /**
     * MIME type of the file.
     * Used for proper content-type headers when serving files.
     */
    private String contentType;

    /**
     * Size of the file in bytes.
     */
    private long size;

    /**
     * Timestamp when the object was first stored.
     */
    private Date uploadDate;

    /**
     * Timestamp when the object was last modified.
     * Initially same as uploadDate.
     */
    private Date lastModified;

    /**
     * Entity tag / checksum for versioning and caching.
     * For GridFS: MD5 hash
     * For S3: ETag
     */
    private String eTag;

    /**
     * Custom user-defined metadata stored with the object.
     * Examples: userId, description, category, tags, etc.
     */
    private Map<String, Object> userMetadata;

    // Constructors, getters and setters...


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public String geteTag() {
        return eTag;
    }

    public void seteTag(String eTag) {
        this.eTag = eTag;
    }

    public Map<String, Object> getUserMetadata() {
        return userMetadata;
    }

    public void setUserMetadata(Map<String, Object> userMetadata) {
        this.userMetadata = userMetadata;
    }
}