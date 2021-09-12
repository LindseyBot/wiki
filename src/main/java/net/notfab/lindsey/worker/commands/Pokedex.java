package net.notfab.lindsey.worker.commands;

import lombok.extern.slf4j.Slf4j;
import net.lindseybot.entities.discord.Label;
import net.lindseybot.entities.discord.builders.EmbedBuilder;
import net.lindseybot.entities.discord.builders.MessageBuilder;
import net.lindseybot.entities.interaction.request.CommandRequest;
import net.lindseybot.entities.interaction.response.MessageResponse;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class Pokedex {

    @RabbitListener(bindings = {@QueueBinding(
            value = @Queue(value = "commands.pokedex", autoDelete = "true"),
            exchange = @Exchange("commands"),
            key = {"pokedex"}
    )})
    public MessageResponse onCommand(@Payload CommandRequest event) {
        String search = event.getOptions().getString("search");
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://pokeapi.co/api/v2/pokemon/" + search)
                .get()
                .build();
        String str;
        try {
            Response resp = client.newCall(request).execute();
            ResponseBody body = resp.body();
            if (body == null || !resp.isSuccessful()) {
                return new MessageResponse(Label.of("commands.pokedex.unknown"));
            }
            str = body.string();
            if (str.equals("Not Found")) {
                return new MessageResponse(Label.of("commands.pokedex.unknown"));
            }
        } catch (IOException ex) {
            return new MessageResponse(Label.of("internal.error"));
        }
        JSONObject obj = new JSONObject(str);
        JSONArray types = obj.getJSONArray("types");
        String name = obj.getString("name");
        EmbedBuilder embed = new EmbedBuilder()
                .title(Label.raw(name.substring(0, 1).toUpperCase() + name.substring(1)))
                .field(Label.of("commands.wiki.pokedex.id"), Label.raw(Integer.toString(obj.getInt("id"))), true)
                .field(Label.of("commands.wiki.pokedex.height"), Label.raw(Double.toString((double) obj.getInt("height") / 10)), true)
                .field(Label.of("commands.wiki.pokedex.weight"), Label.raw(Double.toString((double) obj.getInt("weight") / 10)), true)
                .thumbnail(obj.getJSONObject("sprites").getString("front_default"))
                .footer(event.getMember().getUser());
        if (types.length() == 1) {
            embed.field(Label.of("commands.wiki.pokedex.type"), Label.raw(types.getJSONObject(0).getJSONObject("type").getString("name")), true);
        } else {
            embed.field(Label.of("commands.wiki.pokedex.type"), Label.raw(types.getJSONObject(0).getJSONObject("type").getString("name") + " & " +
                    types.getJSONObject(1).getJSONObject("type").getString("name")), true);
        }
        return new MessageResponse(new MessageBuilder().embed(embed.build()).build());
    }

}
