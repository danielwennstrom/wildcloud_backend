package org.wildcloud.wildcloud_backend.registrar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.wildcloud.wildcloud_backend.enums.SourceType;
import org.wildcloud.wildcloud_backend.processor.ImageProcessor;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProcessorRegistrar {
    private final Map<SourceType, ImageProcessor> processors = new HashMap<>();
    private final ApplicationContext applicationContext;

    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        Map<String, ImageProcessor> beans = applicationContext.getBeansOfType(ImageProcessor.class);
        beans.forEach((name, processor) -> processors.put(processor.getSourceType(), processor));
        processors.forEach((sourceType, proc) -> log.info("Registered processor: {}", sourceType));
    }

    public ImageProcessor getProcessor(SourceType type) {
        return processors.get(type);
    }
}

