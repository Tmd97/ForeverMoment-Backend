package com.example.moment_forever.core.controller.admin;

import com.example.moment_forever.common.response.ApiResponse;
import com.example.moment_forever.common.response.ResponseUtil;
import com.example.moment_forever.core.services.ImageService;
import com.example.moment_forever.store.dto.ImageMetadataResponse;
import com.example.moment_forever.store.dto.ImageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/images")
public class ImageController {

    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ImageResponse>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam Map<String, Object> metadata) {

        ImageResponse response = imageService.uploadImage(file, metadata);
        return ResponseEntity.ok(ResponseUtil.buildCreatedResponse(response, "Image uploaded successfully"));
    }

    @GetMapping("/{id}")
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
    public ResponseEntity<ApiResponse<ImageMetadataResponse>> getImageMetadata(@PathVariable String id) {
        ImageMetadataResponse response = imageService.getImageMetadata(id);
        return ResponseEntity.ok(ResponseUtil.buildOkResponse(response, "Image metadata fetched successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ImageMetadataResponse>>> getAllImages() {
        List<ImageMetadataResponse> response = imageService.getAllImages();
        return ResponseEntity.ok(ResponseUtil.buildOkResponse(response, "Images fetched successfully"));
    }

    @DeleteMapping("/{id}")
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