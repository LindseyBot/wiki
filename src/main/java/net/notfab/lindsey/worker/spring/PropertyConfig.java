package net.notfab.lindsey.worker.spring;

import net.lindseybot.properties.ControllerProperties;
import net.notfab.lindsey.worker.spring.properties.ApiProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class PropertyConfig {

    @Bean
    @ConfigurationProperties(prefix = "app.controller")
    public ControllerProperties controller() {
        return new ControllerProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "app.api")
    public ApiProperties api() {
        return new ApiProperties();
    }

}
