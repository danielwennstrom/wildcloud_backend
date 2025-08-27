package org.wildcloud.wildcloud_backend.registrar;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.wildcloud.wildcloud_backend.service.ImageUploadService;
import org.wildcloud.wildcloud_backend.validator.ImageValidator;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ValidatorRegistrar {
    private final ImageUploadService uploadService;
    private final ApplicationContext applicationContext;

    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        Map<String, ImageValidator> validators = applicationContext.getBeansOfType(ImageValidator.class);

        validators.forEach((beanName, validator) -> {
            String sourceType = extractValidator(beanName);
            uploadService.registerValidator(sourceType, validator);
        });
    }

    private String extractValidator(String beanName) {
        return beanName
                .replace("Validator", "");
    }
}
