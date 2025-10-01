package org.wildcloud.wildcloud_backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.wildcloud.wildcloud_backend.adapter.MultipartFileAdapter;
import org.wildcloud.wildcloud_backend.domain.FileAdapter;
import org.wildcloud.wildcloud_backend.dto.UploadRequestDto;
import org.wildcloud.wildcloud_backend.enums.SourceType;
import org.wildcloud.wildcloud_backend.model.ImageUploadContext;
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
    
    @PostMapping("/direct")
    public Mono<ResponseEntity<?>> directUpload(@RequestPart("metadata") UploadRequestDto request, @RequestPart("files") Flux<FilePart> files) {
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
                .map(adapters -> ImageUploadContext.builder()
                        .files(adapters)
                        .userId(request.getUserId())
                        .cameraId(request.getCameraId())
                        .build())
                .flatMap(context -> uploadService.processUpload(SourceType.DIRECT, context))
                .map(summary -> {
                    return ResponseEntity.ok(Map.of(
                            "message", "Upload finished",
                            "successes", summary.getSuccesses(),
                            "failures", summary.getFailures()
                    ));
                });
    }
}
    
