package org.wildcloud.wildcloud_backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.wildcloud.wildcloud_backend.adapter.MultipartFileAdapter;
import org.wildcloud.wildcloud_backend.domain.FileAdapter;
import org.wildcloud.wildcloud_backend.request.DirectUploadRequest;
import org.wildcloud.wildcloud_backend.service.ImageUploadService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class UploadController {
    private final ImageUploadService uploadService;

    // TODO: request DTO på frontend:s sida
    @PostMapping("/direct")
    public Mono<ResponseEntity<?>> directUpload(@RequestPart("file") Flux<FilePart> files) {
        return files
                .doOnNext(filePart -> log.info("Received FilePart: {}", filePart.filename()))
                .flatMap(filePart -> DataBufferUtils.join(filePart.content())
                        .map(buffer -> {
                            byte[] bytes = new byte[buffer.readableByteCount()];
                            buffer.read(bytes);
                            DataBufferUtils.release(buffer);
                            return (FileAdapter) new MultipartFileAdapter(
                                    filePart.filename(),
                                    Objects.requireNonNull(filePart.headers().getContentType()).toString(),
                                    bytes,
                                    bytes.length
                            );
                        }))
                .collectList()
                .map(adapters -> {
                    DirectUploadRequest request = DirectUploadRequest.builder()
                            .files(adapters)
                            .userId("10")
                            .cameraId("100")
                            .build();
                    return request;
                })
                .flatMap(req -> {
                    return uploadService.processUpload("direct", req);
                })
                .map(results -> {
                    return ResponseEntity.ok(Map.of(
                            "message", "Upload successful",
                            "processedFiles", results.getMetadataList().size()
                    ));
                });
    }
}
    
