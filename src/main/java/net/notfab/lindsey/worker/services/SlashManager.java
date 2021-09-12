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
        this.register(registry, beta, this.describePokedex());
        this.register(registry, beta, this.describeDefine());
        //this.register(registry, beta, this.describeRoll());
    }

    private void register(CommandRegistry registry, boolean beta, CommandMeta meta) {
        if (meta.getGuilds().isEmpty() && beta) {
            return;
        }
        registry.register(meta);
    }

    private CommandMeta describePokedex() {
        return new CommandBuilder("pokedex", Label.of("commands.pokedex.description"), 1.0)
                .module(Modules.FUN)
                .addOption(OptType.STRING, "search", Label.of("commands.pokedex.search"), true)
                .build();
    }

    private CommandMeta describeDefine() {
        return new CommandBuilder("define", Label.of("commands.define.description"), 1.0)
                .module(Modules.FUN)
                .addOption(OptType.STRING, "word", Label.of("commands.define.word"), true)
                .build();
    }

    private CommandMeta describeHearthstone() {
        return new CommandBuilder("hearthstone", Label.of("commands.hearthstone.description"), 1.0)
                .module(Modules.FUN)
                .addOption(OptType.INT, "sides", Label.of("commands.hearthstone.sides"), false)
                .guilds(213044545825406976L)
                .build();
    }

}
