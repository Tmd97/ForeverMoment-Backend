package com.forvmom.core.controller.admin;

import com.forvmom.common.response.ApiResponse;
import com.forvmom.common.response.ResponseUtil;
import com.forvmom.core.services.ImageService;
import com.forvmom.store.dto.ImageMetadataResponse;
import com.forvmom.store.dto.ImageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/images")
@Tag(name = "Admin Image API", description = "Endpoints for uploading and managing images (Admin only)")
public class ImageController {

    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload Image", description = "Upload a new image with metadata")
    public ResponseEntity<ApiResponse<ImageResponse>> uploadImage(
            @RequestParam("file") @Parameter(description = "Image file to upload", content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)) MultipartFile file,
            @RequestParam Map<String, Object> metadata) {

        ImageResponse response = imageService.uploadImage(file, metadata);
        return ResponseEntity.ok(ResponseUtil.buildCreatedResponse(response, "Image uploaded successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Download Image", description = "Download an image by its unique ID")
    public ResponseEntity<Resource> downloadImage(@PathVariable String id) throws IOException {
        Resource resource = imageService.downloadImage(id);

        // Get content type from metadata service
        String contentType = imageService.getContentType(id)
                .orElse("application/octet-stream");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .contentLength(resource.contentLength())
                .body(resource);
    }

    @GetMapping("/{id}/metadata")
    @Operation(summary = "Get Image Metadata", description = "Fetch metadata for a stored image")
    public ResponseEntity<ApiResponse<ImageMetadataResponse>> getImageMetadata(@PathVariable String id) {
        ImageMetadataResponse response = imageService.getImageMetadata(id);
        return ResponseEntity.ok(ResponseUtil.buildOkResponse(response, "Image metadata fetched successfully"));
    }

    @GetMapping
    @Operation(summary = "Get All Images", description = "Fetch metadata for all stored images")
    public ResponseEntity<ApiResponse<List<ImageMetadataResponse>>> getAllImages() {
        List<ImageMetadataResponse> response = imageService.getAllImages();
        return ResponseEntity.ok(ResponseUtil.buildOkResponse(response, "Images fetched successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Image", description = "Permanently delete an image by ID")
    public ResponseEntity<ApiResponse<Void>> deleteImage(@PathVariable String id) {
        imageService.deleteImage(id);
        return ResponseEntity.ok(ResponseUtil.buildCreatedResponse(null, "Image deleted successfully"));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteImages(@RequestBody List<String> ids) {
        imageService.deleteImages(ids);
        return ResponseEntity.ok(ResponseUtil.buildCreatedResponse(null, "Images deleted successfully"));
    }
}