package org.wildcloud.wildcloud_backend.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wildcloud.wildcloud_backend.dto.ImageResponseDto;
import org.wildcloud.wildcloud_backend.service.ImageService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@RestController
@RequestMapping("/api/images/")
@AllArgsConstructor
@Slf4j
public class ImageController {
    private final ImageService imageService;

    @GetMapping("/{cameraId}")
    public Flux<ImageResponseDto> getAll(@PathVariable Long cameraId,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "20")
                                         int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("created_at").descending());
        return imageService.retrieve(cameraId, pageable)
                .doOnSubscribe(s -> log.info("Retrieving images from cameraId {} from page {}", cameraId, page))
                .switchIfEmpty(Mono.fromRunnable(() ->
                        log.warn("No images found for cameraId {}", cameraId)
                ));
    }

    // todo: find a better way of serving local images,
    // it wouldn't work as it is if the app is run in production (i.e., with cloud storage)
    @GetMapping("/{cameraId}/{filename:.+}")
    public Mono<ResponseEntity<Resource>> getImage(
            @PathVariable String cameraId,
            @PathVariable String filename) {

        final Path uploadDir = Paths.get("uploads/images");

        Path file = uploadDir.resolve(cameraId).resolve(filename).normalize();
        if (!Files.exists(file)) {
            return Mono.just(ResponseEntity.notFound().build());
        }

        Resource resource = new FileSystemResource(file);
        MediaType mediaType = MediaType.IMAGE_PNG;

        return Mono.just(ResponseEntity.ok()
                .contentType(mediaType)
                .body(resource));
    }

    @DeleteMapping("/{imageId}")
    public Mono<ResponseEntity<Map<String, String>>> delete(
            @PathVariable Long imageId) {

        return imageService.delete(imageId)
                .doOnSubscribe(s -> log.info("Deleting imageId {}", imageId))
                .then(Mono.just(ResponseEntity.ok(Map.of("message", "Deletion successful"))))
                .onErrorResume(e -> {
                    log.error("Deletion failed for imageId {}", imageId, e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Map.of("message", "Deletion failed")));
                });
    }
}
