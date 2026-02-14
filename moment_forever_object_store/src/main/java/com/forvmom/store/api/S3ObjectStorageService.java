package com.forvmom.store.api;

import com.forvmom.store.config.ObjectStoreProperties;
import com.forvmom.store.dto.ObjectMetadata;
import com.forvmom.store.exception.ObjectStorageException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.*;

@Service
@ConditionalOnProperty(name = "object.store.provider", havingValue = "s3")
public class S3ObjectStorageService implements ObjectStorageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final ObjectStoreProperties properties;

    public S3ObjectStorageService(S3Client s3Client,
                                  S3Presigner s3Presigner,
                                  ObjectStoreProperties properties) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.properties = properties;
    }

    @Override
    public String store(String fileName, InputStream content, String contentType,
                        Map<String, Object> metadata) {
        try {
            String key = UUID.randomUUID().toString() + "_" + fileName;

            // Convert metadata to S3 format
            Map<String, String> s3Metadata = new HashMap<>();
            if (metadata != null) {
                metadata.forEach((k, v) -> s3Metadata.put(k, v.toString()));
            }

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(properties.getS3Bucket())
                    .key(key)
                    .contentType(contentType)
                    .metadata(s3Metadata)
                    .build();

            s3Client.putObject(request,
                    software.amazon.awssdk.core.sync.RequestBody.fromInputStream(content, content.available()));

            return key;

        } catch (Exception e) {
            throw new ObjectStorageException("Failed to store file in S3: " + fileName, e);
        }
    }

    @Override
    public Resource retrieve(String id) {
        try {
            // Generate presigned URL (valid for 10 minutes)
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(properties.getS3Bucket())
                    .key(id)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
            URL url = presignedRequest.url();

            return new UrlResource(url);

        } catch (Exception e) {
            throw new ObjectStorageException("Failed to retrieve file from S3: " + id, e);
        }
    }

    @Override
    public Optional<ObjectMetadata> getMetadata(String id) {
        try {
            HeadObjectRequest request = HeadObjectRequest.builder()
                    .bucket(properties.getS3Bucket())
                    .key(id)
                    .build();

            HeadObjectResponse response = s3Client.headObject(request);

            return Optional.of(mapToMetadata(id, response));

        } catch (NoSuchKeyException e) {
            return Optional.empty();
        } catch (Exception e) {
            throw new ObjectStorageException("Failed to get metadata from S3: " + id, e);
        }
    }

    @Override
    public List<ObjectMetadata> listAll() {
        try {
            ListObjectsV2Request request = ListObjectsV2Request.builder()
                    .bucket(properties.getS3Bucket())
                    .build();

            ListObjectsV2Iterable responses = s3Client.listObjectsV2Paginator(request);

            List<ObjectMetadata> metadataList = new ArrayList<>();

            for (ListObjectsV2Response response : responses) {
                for (S3Object s3Object : response.contents()) {
                    // For each object, get full metadata
                    getMetadata(s3Object.key()).ifPresent(metadataList::add);
                }
            }

            return metadataList;

        } catch (Exception e) {
            throw new ObjectStorageException("Failed to list objects from S3", e);
        }
    }

    @Override
    public void delete(String id) {
        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(properties.getS3Bucket())
                    .key(id)
                    .build();

            s3Client.deleteObject(request);

        } catch (Exception e) {
            throw new ObjectStorageException("Failed to delete file from S3: " + id, e);
        }
    }

    @Override
    public void deleteAll(List<String> ids) {
        try {
            List<ObjectIdentifier> objects = ids.stream()
                    .map(id -> ObjectIdentifier.builder().key(id).build())
                    .toList();

            DeleteObjectsRequest request = DeleteObjectsRequest.builder()
                    .bucket(properties.getS3Bucket())
                    .delete(Delete.builder().objects(objects).build())
                    .build();

            s3Client.deleteObjects(request);

        } catch (Exception e) {
            throw new ObjectStorageException("Failed to delete multiple files from S3", e);
        }
    }

    @Override
    public boolean exists(String id) {
        try {
            HeadObjectRequest request = HeadObjectRequest.builder()
                    .bucket(properties.getS3Bucket())
                    .key(id)
                    .build();

            s3Client.headObject(request);
            return true;

        } catch (NoSuchKeyException e) {
            return false;
        }
    }

    private ObjectMetadata mapToMetadata(String key, HeadObjectResponse response) {
        ObjectMetadata metadata = new ObjectMetadata();

        metadata.setId(key);
        metadata.setFileName(key.substring(key.indexOf('_') + 1)); // Remove UUID prefix
        metadata.setContentType(response.contentType());
        metadata.setSize(response.contentLength());
        metadata.setUploadDate(Date.from(response.lastModified()));
        metadata.setLastModified(Date.from(response.lastModified()));
        metadata.seteTag(null);

        // Convert S3 metadata to Map
        if (response.metadata() != null) {
            metadata.setUserMetadata(new HashMap<>(response.metadata()));
        }

        return metadata;
    }
}