package net.notfab.lindsey.worker.commands;

import lombok.extern.slf4j.Slf4j;
import net.lindseybot.entities.discord.Label;
import net.lindseybot.entities.interaction.request.CommandRequest;
import net.lindseybot.entities.interaction.response.MessageResponse;
import net.notfab.lindsey.worker.spring.properties.ApiProperties;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONException;
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
public class Hearthstone {

    private final OkHttpClient client = new OkHttpClient();
    private final ApiProperties properties;

    public Hearthstone(ApiProperties properties) {
        this.properties = properties;
    }

    @RabbitListener(bindings = {@QueueBinding(
            value = @Queue(value = "commands.hearthstone", autoDelete = "true"),
            exchange = @Exchange("commands"),
            key = {"hearthstone"}
    )})
    public MessageResponse onCommand(@Payload CommandRequest event) {
        String card = event.getOptions().getString("card");
        boolean gold = event.getOptions().getBoolean("gold");
        String str;
        try {
            Request request = new Request.Builder()
                    .url("https://omgvamp-hearthstone-v1.p.rapidapi.com/cards/search/" + card + "?collectible=1&locale=enUS")
                    .get()
                    .addHeader("x-rapidapi-host", "omgvamp-hearthstone-v1.p.rapidapi.com")
                    .addHeader("x-rapidapi-key", this.properties.getRapidApi())
                    .build();
            Response resp = client.newCall(request).execute();
            ResponseBody body = resp.body();
            if (!resp.isSuccessful() || body == null) {
                return new MessageResponse(Label.of("internal.error"));
            }
            str = body.string();
        } catch (IOException ex) {
            return new MessageResponse(Label.of("internal.error"));
        }
        String result = "";
        try {
            JSONArray arr = new JSONArray(str);
            if (gold) {
                result = arr.getJSONObject(0).getString("imgGold");
            } else {
                result = arr.getJSONObject(0).getString("img");
            }
        } catch (JSONException e) {
            JSONObject obj = new JSONObject(str);
            if (obj.getInt("error") == 404) {
                return new MessageResponse(Label.of("commands.hearthstone.unknown", card));
            }
        }
        return new MessageResponse(Label.raw(result));
    }

}
