package com.example.moment_forever.core.services;

import com.example.moment_forever.store.api.ObjectStorageService;
import com.example.moment_forever.store.dto.ImageMetadataResponse;
import com.example.moment_forever.store.dto.ImageResponse;
import com.example.moment_forever.store.dto.ObjectMetadata;
import com.example.moment_forever.store.exception.ImageNotFoundException;
import com.example.moment_forever.store.exception.ImageStorageException;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ImageService {

    private final ObjectStorageService storageService;

    public ImageService(ObjectStorageService storageService) {
        this.storageService = storageService;
    }

    /**
     * Upload an image to storage
     */
    public ImageResponse uploadImage(MultipartFile file, Map<String, Object> metadata) {
        try {
            // Prepare metadata
            Map<String, Object> finalMetadata = new HashMap<>();
            if (metadata != null) {
                finalMetadata.putAll(metadata);
            }

            // Store the file
            String id = storageService.store(
                    file.getOriginalFilename(),
                    file.getInputStream(),
                    file.getContentType(),
                    finalMetadata
            );

            // Return response
            ImageResponse response = new ImageResponse();
            response.setId(id);
            response.setFileName(file.getOriginalFilename());
            response.setContentType(file.getContentType());
            response.setSize(file.getSize());
            response.setUploadDate(new Date());

            return response;

        } catch (IOException e) {
            throw new ImageStorageException("Failed to upload image", e);
        }
    }

    /**
     * Download an image by ID
     */
    public Resource downloadImage(String id) {
        try {
            return storageService.retrieve(id);
        } catch (Exception e) {
            throw new ImageNotFoundException("Image not found with id: " + id);
        }
    }

    /**
     * Get image metadata by ID
     */
    public ImageMetadataResponse getImageMetadata(String id) {
        Optional<ObjectMetadata> metadata = storageService.getMetadata(id);

        if (metadata.isEmpty()) {
            throw new ImageNotFoundException("Image not found with id: " + id);
        }

        return mapToImageMetadataResponse(metadata.get());
    }

    /**
     * Get all images metadata
     */
    public List<ImageMetadataResponse> getAllImages() {
        return storageService.listAll()
                .stream()
                .map(this::mapToImageMetadataResponse)
                .collect(Collectors.toList());
    }

    /**
     * Delete an image by ID
     */
    public void deleteImage(String id) {
        if (!storageService.exists(id)) {
            throw new ImageNotFoundException("Image not found with id: " + id);
        }
        storageService.delete(id);
    }

    public Optional<String> getContentType(String id) {
        return storageService.getMetadata(id)
                .map(ObjectMetadata::getContentType);
    }

    /**
     * Delete multiple images
     */
    public void deleteImages(List<String> ids) {
        storageService.deleteAll(ids);
    }

    private ImageMetadataResponse mapToImageMetadataResponse(ObjectMetadata metadata) {
        ImageMetadataResponse response = new ImageMetadataResponse();
        response.setId(metadata.getId());
        response.setFileName(metadata.getFileName());
        response.setContentType(metadata.getContentType());
        response.setSize(metadata.getSize());
        response.setUploadDate(metadata.getUploadDate());
        response.setUserMetadata(metadata.getUserMetadata());
        return response;
    }
}