package org.wildcloud.wildcloud_backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.wildcloud.wildcloud_backend.model.UploadResult;
import org.wildcloud.wildcloud_backend.request.DirectUploadRequest;
import org.wildcloud.wildcloud_backend.service.ImageUploadService;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@Slf4j
public class UploadController {
    private final ImageUploadService uploadService;

    @PostMapping("/direct")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> directUpload(@RequestParam("file") MultipartFile file) {
        try {
            DirectUploadRequest request = DirectUploadRequest.builder()
                    .file(file)
                    .build();
            UploadResult result = uploadService.processUpload("direct", request);

            System.out.println(result);
            return ResponseEntity.ok("ok");
        } catch (Exception e) {
            log.error("Direct upload failed", e);
            return ResponseEntity.badRequest().body("badRequest");
        }
    }
}
