package org.wildcloud.wildcloud_backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.wildcloud.wildcloud_backend.model.UploadResult;
import org.wildcloud.wildcloud_backend.request.DirectUploadRequest;
import org.wildcloud.wildcloud_backend.service.ImageUploadService;

import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@Slf4j
public class UploadController {
    private final ImageUploadService uploadService;

    @PostMapping("/direct")
    public ResponseEntity<?> directUpload(@RequestPart("file") MultipartFile file) {
        try {
            DirectUploadRequest request = DirectUploadRequest.builder()
                    .file(file)
                    .build();

            UploadResult result = uploadService.processUpload("direct", request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Unexpected error during upload", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unexpected server error"));
        }
    }
}
