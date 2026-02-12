package com.example.moment_forever.store.api;

import com.example.moment_forever.store.dto.ObjectMetadata;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service interface for object storage operations.
 * Abstracts away underlying storage implementations like GridFS, S3, etc.
 * Core module depends on this interface, not concrete implementations.
 */
public interface ObjectStorageService {

    /**
     * Stores a file/object in the underlying storage system.
     *
     * @param fileName    original name of the file (e.g., "profile-pic.jpg")
     * @param content     input stream containing the file data
     * @param contentType MIME type of the file (e.g., "image/jpeg", "application/pdf")
     * @param metadata    additional custom metadata as key-value pairs (e.g., userId, description, tags)
     * @return unique identifier (String) that can be used later to retrieve/delete the object
//     * @throws ObjectStorageException if storage operation fails
     */
    String store(String fileName, InputStream content, String contentType, Map<String, Object> metadata);

    /**
     * Retrieves a stored object by its unique identifier.
     * Returns Spring's Resource abstraction which can be:
     * - Streamed directly to HTTP response using ResponseEntity
     * - Converted to byte[], InputStream, or File
     *
     * @param id unique identifier of the object to retrieve
     * @return Resource containing the object data and metadata
     * @throws com.example.moment_forever.store.exception.ObjectStorageException if object not found or cannot be read
     */
    Resource retrieve(String id);

    /**
     * Fetches metadata of a stored object without downloading the actual content.
     * Useful for displaying file information in lists or previews.
     *
     * @param id unique identifier of the object
     * @return Optional containing ObjectMetadata if found, empty Optional if not exists
     */
    Optional<ObjectMetadata> getMetadata(String id);

    /**
     * Retrieves metadata for ALL objects in storage.
     * Does NOT include the actual file content - only metadata.
     * Use with caution for large datasets; consider pagination for production.
     *
     * @return List of ObjectMetadata for all stored objects
     */
    List<ObjectMetadata> listAll();

    /**
     * Permanently removes an object from storage.
     *
     * @param id unique identifier of the object to delete
//     * @throws ObjectStorageException if deletion fails
     */
    void delete(String id);

    /**
     * Permanently removes multiple objects in a single operation.
     * More efficient than calling delete() in a loop.
     *
     * @param ids list of unique identifiers to delete
//     * @throws ObjectStorageException if any deletion fails
     */
    void deleteAll(List<String> ids);

    /**
     * Checks whether an object exists in storage.
     * Useful before performing operations or for validation.
     *
     * @param id unique identifier to check
     * @return true if object exists, false otherwise
     */
    boolean exists(String id);
}