package org.wildcloud.wildcloud_backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.wildcloud.wildcloud_backend.adapter.MultipartFileAdapter;
import org.wildcloud.wildcloud_backend.domain.FileAdapter;
import org.wildcloud.wildcloud_backend.model.UploadResult;
import org.wildcloud.wildcloud_backend.request.DirectUploadRequest;
import org.wildcloud.wildcloud_backend.service.ImageUploadService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@Slf4j
public class UploadController {
    private final ImageUploadService uploadService;

    // TODO: request DTO på frontend:s sida
    @PostMapping("/direct")
    public ResponseEntity<?> directUpload(@RequestParam("file") MultipartFile[] files) {
        try {
            List<FileAdapter> fileAdapters = Arrays.stream(files)
                    .map(MultipartFileAdapter::new)
                    .collect(Collectors.toList());
            
            DirectUploadRequest request = DirectUploadRequest.builder()
                    .files(fileAdapters)
                    .build();

            // TODO: skicka tillbaka en DTO? alt. ingenting alls
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
