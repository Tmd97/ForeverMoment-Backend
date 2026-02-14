package com.forvmom.store.api;

import com.forvmom.store.dto.ObjectMetadata;
import com.forvmom.store.exception.ObjectStorageException;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;

@Service
public class GridFsObjectStorageService implements ObjectStorageService {

    private final GridFsTemplate gridFsTemplate;

    public GridFsObjectStorageService(GridFsTemplate gridFsTemplate) {
        this.gridFsTemplate = gridFsTemplate;
    }

    @Override
    public String store(String fileName, InputStream content, String contentType, Map<String, Object> metadata) {
        try {
            // Convert Map to DBObject for GridFS
            org.bson.Document metaDataDoc = new org.bson.Document();
            if (metadata != null) {
                metaDataDoc.putAll(metadata);
            }

            // Store file in GridFS
            ObjectId objectId = gridFsTemplate.store(
                    content,
                    fileName,
                    contentType,
                    metaDataDoc
            );

            return objectId.toString();
        } catch (Exception e) {
            throw new ObjectStorageException("Failed to store file: " + fileName, e);
        }
    }

    @Override
    public Resource retrieve(String id) {
        try {
            GridFSFile file = gridFsTemplate.findOne(
                    Query.query(Criteria.where("_id").is(id))
            );

            if (file == null) {
                throw new ObjectStorageException("File not found with id: " + id);
            }

            GridFsResource resource = gridFsTemplate.getResource(file);
            return new InputStreamResource(resource.getInputStream()) {
                @Override
                public String getFilename() {
                    return file.getFilename();
                }

                @Override
                public long contentLength() {
                    return file.getLength();
                }


            };
        } catch (Exception e) {
            throw new ObjectStorageException("Failed to retrieve file: " + id, e);
        }
    }

    @Override
    public Optional<ObjectMetadata> getMetadata(String id) {
        try {
            GridFSFile file = gridFsTemplate.findOne(
                    Query.query(Criteria.where("_id").is(id))
            );

            if (file == null) {
                return Optional.empty();
            }

            return Optional.of(mapToMetadata(file));
        } catch (Exception e) {
            throw new ObjectStorageException("Failed to get metadata for: " + id, e);
        }
    }

    @Override
    public List<ObjectMetadata> listAll() {
        try {
            var files = gridFsTemplate.find(new Query());
            List<ObjectMetadata> metadataList = new ArrayList<>();

            for (GridFSFile file : files) {
                metadataList.add(mapToMetadata(file));
            }

            return metadataList;
        } catch (Exception e) {
            throw new ObjectStorageException("Failed to list all files", e);
        }
    }

    @Override
    public void delete(String id) {
        try {
            gridFsTemplate.delete(
                    Query.query(Criteria.where("_id").is(id))
            );
        } catch (Exception e) {
            throw new ObjectStorageException("Failed to delete file: " + id, e);
        }
    }

    @Override
    public void deleteAll(List<String> ids) {
        try {
            for (String id : ids) {
                gridFsTemplate.delete(
                        Query.query(Criteria.where("_id").is(id))
                );
            }
        } catch (Exception e) {
            throw new ObjectStorageException("Failed to delete multiple files", e);
        }
    }

    @Override
    public boolean exists(String id) {
        try {
            GridFSFile file = gridFsTemplate.findOne(
                    Query.query(Criteria.where("_id").is(id))
            );
            return file != null;
        } catch (Exception e) {
            return false;
        }
    }

    private ObjectMetadata mapToMetadata(GridFSFile file) {
        ObjectMetadata metadata = new ObjectMetadata();

        metadata.setId(file.getObjectId().toString());
        metadata.setFileName(file.getFilename());
        metadata.setContentType(file.getMetadata() != null ?
                file.getMetadata().getString("_contentType") : null);
        metadata.setSize(file.getLength());
        metadata.setUploadDate(file.getUploadDate());
        metadata.setLastModified(file.getUploadDate()); // GridFS doesn't have last modified
        metadata.seteTag(null);

        // Convert Document to Map
        if (file.getMetadata() != null) {
            Map<String, Object> userMetadata = new HashMap<>();
            for (String key : file.getMetadata().keySet()) {
                // Skip internal GridFS fields
                if (!key.startsWith("_")) {
                    userMetadata.put(key, file.getMetadata().get(key));
                }
            }
            metadata.setUserMetadata(userMetadata);
        }

        return metadata;
    }
}