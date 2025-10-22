package org.wildcloud.wildcloud_backend.registrar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.wildcloud.wildcloud_backend.validator.ImageValidator;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ValidatorRegistrar {
    private final ApplicationContext applicationContext;

    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        Map<String, ImageValidator> validators = applicationContext.getBeansOfType(ImageValidator.class);

        validators.forEach((beanName, validator) -> {
            String validatorName = extractValidator(beanName);
            log.info("Registered validator: {}", validatorName);
        });
    }

    private String extractValidator(String beanName) {
        return beanName
                .replace("Validator", "");
    }
}
