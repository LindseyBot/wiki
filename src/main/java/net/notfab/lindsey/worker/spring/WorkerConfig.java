package net.notfab.lindsey.worker.spring;

import net.lindseybot.controller.registry.CommandRegistry;
import net.lindseybot.properties.ControllerProperties;
import net.notfab.lindsey.shared.utils.Snowflake;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class WorkerConfig {

    @Bean
    public CommandRegistry commands(ControllerProperties properties) {
        return new CommandRegistry(properties);
    }

    @Bean
    public Snowflake snowflake() {
        return new Snowflake();
    }

}
