package net.notfab.lindsey.worker.services;

import net.lindseybot.controller.registry.CommandRegistry;
import net.lindseybot.entities.discord.Label;
import net.lindseybot.entities.interaction.commands.CommandMeta;
import net.lindseybot.entities.interaction.commands.OptType;
import net.lindseybot.entities.interaction.commands.builder.CommandBuilder;
import net.lindseybot.enums.Modules;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SlashManager {

    public SlashManager(CommandRegistry registry, @Value("${app.beta}") boolean beta) {
        this.register(registry, beta, this.pokedex());
        this.register(registry, beta, this.define());
        this.register(registry, beta, this.hearthstone());
    }

    private void register(CommandRegistry registry, boolean beta, CommandMeta meta) {
        if (meta.getGuilds().isEmpty() && beta) {
            return;
        }
        registry.register(meta);
    }

    private CommandMeta pokedex() {
        return new CommandBuilder("pokedex", Label.of("commands.pokedex.description"), 1.0)
                .module(Modules.FUN)
                .addOption(OptType.STRING, "search", Label.of("commands.pokedex.search"), true)
                .build();
    }

    private CommandMeta define() {
        return new CommandBuilder("define", Label.of("commands.define.description"), 1.0)
                .module(Modules.FUN)
                .addOption(OptType.STRING, "word", Label.of("commands.define.word"), true)
                .build();
    }

    private CommandMeta hearthstone() {
        return new CommandBuilder("hearthstone", Label.of("commands.hearthstone.description"), 1.0)
                .module(Modules.FUN)
                .addOption(OptType.STRING, "card", Label.of("commands.hearthstone.card"), true)
                .addOption(OptType.BOOLEAN, "gold", Label.of("commands.hearthstone.gold"), false)
                .build();
    }

}
