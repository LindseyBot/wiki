package net.notfab.lindsey.worker.commands;

import lombok.extern.slf4j.Slf4j;
import net.lindseybot.entities.discord.Label;
import net.lindseybot.entities.interaction.request.CommandRequest;
import net.lindseybot.entities.interaction.response.MessageResponse;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Random;

@Slf4j
@Component
public class Hearthstone {

    @RabbitListener(bindings = {@QueueBinding(
            value = @Queue(value = "commands.hearthstone", autoDelete = "true"),
            exchange = @Exchange("commands"),
            key = {"hearthstone"}
    )})
    public MessageResponse onCommand(@Payload CommandRequest event) {
        Random gem = new Random();
        if (gem.nextBoolean()) {
            return new MessageResponse(Label.of("commands.flip.heads", event.getMember().getAsMention()));
        } else {
            return new MessageResponse(Label.of("commands.flip.tails", event.getMember().getAsMention()));
        }
    }

}
