package org.wildcloud.wildcloud_backend.registrar;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.wildcloud.wildcloud_backend.model.ImageProcessor;
import org.wildcloud.wildcloud_backend.service.ImageUploadService;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ProcessorRegistrar {
    private final ImageUploadService uploadService;
    private final ApplicationContext applicationContext;

    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        Map<String, ImageProcessor> processorMap = applicationContext.getBeansOfType(ImageProcessor.class);

        processorMap.forEach((beanName, processor) -> {
            String sourceType = extractSourceType(beanName);
            uploadService.registerProcessor(sourceType, processor);
        });
    }

    private String extractSourceType(String beanName) {
        return beanName.toLowerCase()
                .replace("processor", "")
                .replace("upload", "");
    }
}
