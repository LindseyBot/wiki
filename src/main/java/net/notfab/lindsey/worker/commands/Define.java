package net.notfab.lindsey.worker.commands;

import lombok.extern.slf4j.Slf4j;
import net.lindseybot.entities.discord.Label;
import net.lindseybot.entities.interaction.request.CommandRequest;
import net.lindseybot.entities.interaction.response.MessageResponse;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class Define {

    @RabbitListener(bindings = {@QueueBinding(
            value = @Queue(value = "commands.define", autoDelete = "true"),
            exchange = @Exchange("commands"),
            key = {"define"}
    )})
    public MessageResponse onCommand(@Payload CommandRequest event) {
        String word = event.getOptions().getString("word");
        String definition = this.define(word);
        if (definition == null) {
            return new MessageResponse(Label.of("commands.define.unknown", word));
        } else {
            return new MessageResponse(Label.raw("> " + definition));
        }
    }

    private String define(String word) {
        StringBuilder builder = new StringBuilder("https://www.urbandictionary.com/define.php?term=");
        for (String s : word.split(" ")) {
            builder.append(URLEncoder.encode(s, StandardCharsets.UTF_8)).append("+");
        }
        builder.setLength(builder.length() - 1);
        try {
            Document d = Jsoup.connect(builder.toString()).followRedirects(true).get();
            Elements definitions = d.getElementsByClass("def-panel");
            if (definitions.size() > 0) {
                String definition = definitions.get(0).getElementsByClass("meaning").get(0).text();
                if (definition.length() > 300) {
                    definition = definition.substring(0, 300) + "...";
                }
                if (!definition.endsWith(".")) {
                    definition += ".";
                }
                return definition;
            }
        } catch (HttpStatusException ex) {
            if (ex.getStatusCode() == 404) {
                return null;
            }
            log.error("Error while defining term - HTTP " + ex.getStatusCode(), ex);
        } catch (Exception ex) {
            log.error("Error while defining term", ex);
        }
        return null;
    }

}
