package org.wildcloud.wildcloud_backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.wildcloud.wildcloud_backend.adapter.FilePartAdapter;
import org.wildcloud.wildcloud_backend.domain.FileAdapter;
import org.wildcloud.wildcloud_backend.dto.AttachmentDto;
import org.wildcloud.wildcloud_backend.dto.CameraDTO;
import org.wildcloud.wildcloud_backend.dto.EmailUploadRequestDto;
import org.wildcloud.wildcloud_backend.dto.UploadRequestDto;
import org.wildcloud.wildcloud_backend.enums.SourceType;
import org.wildcloud.wildcloud_backend.model.ImageUploadContext;
import org.wildcloud.wildcloud_backend.service.CameraService;
import org.wildcloud.wildcloud_backend.service.ImageUploadService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class UploadController {
    private final ImageUploadService uploadService;
    private final CameraService cameraService;
    private final WebClient webClient;

    @PostMapping("/direct")
    public Mono<ResponseEntity<?>> directUpload(@RequestPart("metadata") UploadRequestDto request, @RequestPart("files") Flux<FilePart> files) {
        return files
                .doOnNext(filePart -> log.info("Received FilePart: {}", filePart.filename()))
                .flatMap(filePart -> DataBufferUtils.join(filePart.content())
                        .map(buffer -> {
                            byte[] bytes = new byte[buffer.readableByteCount()];
                            buffer.read(bytes);
                            DataBufferUtils.release(buffer);
                            return (FileAdapter) new FilePartAdapter(
                                    filePart.filename(),
                                    Objects.requireNonNull(filePart.headers().getContentType()).toString(),
                                    bytes,
                                    bytes.length
                            );
                        }))
                .collectList()
                .map(adapters -> ImageUploadContext.builder()
                        .files(adapters)
                        .cameraId(request.getCameraId())
                        .build())
                .flatMap(context -> uploadService.processUpload(SourceType.DIRECT, context))
                .map(summary -> ResponseEntity.ok(Map.of(
                        "message", "Upload finished",
                        "successes", summary.getSuccesses(),
                        "failures", summary.getFailures()
                )));
    }

    @PostMapping("/mailparser-webhook")
    public Mono<ResponseEntity<?>> emailUpload(@RequestBody EmailUploadRequestDto request) {
        log.info("Received email from: {}", request.getSender());

        return cameraService.findByEmail(request.getSender().getFirst().getAddress())
//                .switchIfEmpty(Mono.error(new CameraNotFoundException("No camera found for email: " + request.getSender())))
                .flatMap(camera -> processAttachments(request.getAttachments(), camera))
                .flatMap(context -> uploadService.processUpload(SourceType.EMAIL, context))
                .map(summary -> ResponseEntity.ok(Map.of(
                        "message", "Upload finished",
                        "successes", summary.getSuccesses(),
                        "failures", summary.getFailures()
                )));
    }

    private Mono<ImageUploadContext> processAttachments(List<AttachmentDto> attachments, CameraDTO camera) {
        return Flux.fromIterable(attachments)
                .flatMap(this::downloadAndProcessAttachment)
                .collectList()
                .map(adapters -> ImageUploadContext.builder()
                        .files(adapters)
                        .cameraId(camera.getCameraId())
                        .build());
    }

    private Mono<FileAdapter> downloadAndProcessAttachment(AttachmentDto attachment) {
        return webClient.get()
                .uri(attachment.getUrl())
                .exchangeToMono(response -> {
                    var headers = response.headers().asHttpHeaders();

                    String contentType = headers.getFirst("Content-Type");
                    String contentDisposition = headers.getFirst("Content-Disposition");
                    String filename;
                    if (contentDisposition != null && contentDisposition.contains("filename=")) {
                        filename = contentDisposition.substring(contentDisposition.indexOf("filename=") + 9)
                                .replace("\"", "");
                    } else {
                        filename = "unknown";
                    }

                    return response.bodyToMono(byte[].class)
                            .map(bytes -> new FilePartAdapter(filename, contentType, bytes, bytes.length));
                });
    }

}
    
